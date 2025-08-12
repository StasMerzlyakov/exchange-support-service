# Cервис поддержки обмена данными (data exchange support service)

## Преамбула

По работе занимаемся организацией межведомственного обмена, когда данные одного ведомства нужно передать в другое.
Такой обмен сопровождается:
- контролем входных данных (в случае, если канал связи недоверенный - проверка на вирусы, вложения, инъекции и т.д.; для всех - проверка формата сообщения, проверка электронной подписи в разных форматах)
- изменением формата данных (как правило, XML одного формата в XML другого формата; иногда json)
- отправка данных
- контролем доставки (проверка того, что на каждое отправленное в ведомство сообщение было подтверждение)
- отслеживанием состояния бизнес-процессов (когда сообщения поступают в рамках одного бизнес-процесса и нужно проверять правильность последовательности)

Некоторые особенности рабочей системы:
- размер сообщений от 10КБ до 50MБ (среднее 500 кб)
- размер сообщений обусловлен наличием бинарных данных (base64)
- нагрузка в пиках до 100 тыс/час
- сообщения имеют уникальные идентификаторы; при генерации нового сообщения, как правило, формируется новый идентификатор в соответствии с новым форматом
- ведомства могут присылать сообщения повторно; система должна вести себя идемпотентно (отправителю нужно вернуть OK, новых сообщений не формируется)
- принцип почты - ведомство получает подтверждение о доставке от Системы, а Система уже отвечает за доставку до другого ведомства
- проверки данных бывают достаточно ресурсоемкими, поэтому основная часть проверок проходит асинхронно; фактом приема в обработку является формирование служебного сообщения-подтверждения, а не HTTP 200/201.   
- есть время удаления бизнес-данных
- есть время удаления мета-данных

## Описание проекта (возможны корректировки)

В проекте разрабатывается система обмена данными. Для простоты считаем что одна внешняя система присылает XML, 
другая принимает JSON.

![context](./docs/architecture/c4-context.drawio.png)

Реализовано пять java-сервисов: GateWay, Receiver, BlobStorage, Generator, Sender, а так же использованы ряд 
готовых контейнеров.

![containers](./docs/architecture/c4-containers.drawio.png)

**Прим**. Применяю подход, которые мы сейчас используем в проде:
Раздельная обработка обычных данных и бинарных - бинарные данные лежат в отдельном хранилище, в БД лежат только обычные 
данные и ссылки на блобы. В сообщения при хранении вставляются base64(ссылка_блоб). В качестве ссылок применяем 
хэш. Блобы подгружаются только когда они реально необходимы. С точки зрения прикладного софта(а также XML/JSON декодеров) 
ему нет разницы - реальный ли это блоб или ссылка.

[Описание работы с блобами](docs/adr/002-blob-references.md)

## compose
Папка, в которой собраны скрипты для сборки контейнеров, docker-compose для запуска(через run.sh) и 
jmeter скрипт (test.jmx; запускаю через send_test.sh)

## common
Содержит общие константы, типы данных и утилиты для разбора XML и сборки JSON
[common](common/README.md)


## gateway 
SpringCloudGateway reactive - сервис. Принимает данные, от внешней системы по определенном ендпоинту, делает проверу, 
добавляет processId и роутит их на Receiver. 

Фильтры:
- RPM 10/min (RequestRateLimiter gateway filter cluster на redis) 
- CircuitBreaker (resilience4j)
- Кастомный фильтр (добавляет заголовки)
- Кастомный фильтр (проверяет входной xml)

### Используемые навыки:
1. 14 - Разбор JMeter и организация нагрузочного тестирования (gateway-service/gateway-jmeter.jmx, blob-storage-service/blob-storage-spring/blob-stroage-grpc.jmx). 
2. 23 - Реактивное программирование: Профилирование приложения на Reactor  (ValidateInputXMLGatewayFilterFactory.apply - onSuccess, onError)
4. 31 - Проектирование и архитектура в разрезе микросервисов (API Gateway)
5. 37 - Шаблоны проектирования отказоустойчивого сервиса (Resilience4j CircuitBreaker) (application.yaml)

[Описание настроек для gateway](docs/adr/001-gateway-hints.md)


## models
Содержит DTO-модели (jaxb, json) + jmh - тесты
![jaxb-jmh-results](docs/img/02-jmh-jaxb-results.png)
### Используемые навыки:
1. 13 - Разбор библиотеки Java Microbenchmark Harness

## jfr-image
Содержит python-сервис для снятия jfr с работающего контейнера + сборка урезанной под сервис jre + cmd.

подробности [jfr](docs/adr/003-jfr-docker.md)

### Используемые навыки:
1. 11 - JDK tools
2. 18 - Профилирование java приложений. Thread dump, JFR


## blob-storage
java - GRPC + openapi (specification first) сервис, отвечающий за хранение бинарных данных (блобы и тела сообщений) по 
принципу key-value хранилище, где key - ключ вида $processGUID/blob_hash. 
(для корректности работы системы сервис должен быть идемпотентным по записи, но это на будущее)

Особенности рализации:
1. S3 - как основное (планирую для простоты minio)
2. SoftReference - внутренни кэш
[Реализация кэша](docs/adr/005-objects-in-memory.md)


### Используемые навыки:
1. 3 - Java Instrumentation & Java agent [006-reactor-blockhound](docs/adr/006-reactor-blockhound.md)
2. 4 -  Memory management. JVM memory structure  (GenericCache.java, MemorySyncStorage.java)
3. 15 - Java.util.concurrent. Atomics, ConcurrentHashMap, ConcurrentSkipListMap (FutureStorage.java)
4. 16 - Java.util.concurrent. Locks, ReadWriteLock, ReentrantLock (GenericCache.java)
5. 17 - Java.util.concurrent. CountDownLatch, Semaphore, Phaser (FutureStorage.java)
6. 21 - Java NIO (работаю с ByteBuffer; под капотом netty; docs/adr/005-objects-in-memory.md;)
7. 22 - Реактивное программирование: Reactor (весь модуль)
8. 34 - Protobuf, gRPC

## memory-dump
Содержит Dockerfile и compose для запуска сервиса blob-storage с возможностью подключения по jmx (VisualVM). Запускаю, 
даю нагрузку blob-storage-service/blob-storage-spring/blob-stroage-grpc.jmx ну и можно подключаться и анализировать.

![visualvm](docs/img/04-01-visualvm.png)
![jmeter](docs/img/04-02-jmeter.png)
![visualvm](docs/img/04-03-visualvm.png)

### Выводы:
1. Приложение не упало
2. Работа FullGC ![serialgc](docs/img/04-04-serialgc.png) - в принципе видно, что паузы достаточно маленькие но их моного
3. gceasy тоже ругается на FullGC и что очень много OutOfMemoryErrors - но текущая реализация на SoftReference!!

Инструменты для анализа есть - дальше можно анализировать работу приложения с WeakReference, ByteBuffer.allocateDirect 
и тюнингом GC,

![gceasy](docs/img/04-05-gceasy.png)
![gceasy](docs/img/04-06-gceasy.png)


### Используемые навыки:
1. 5 - Memory management. Разбор алгоритмов GC: SerialGC, ParallelGC, CMS GC
2. 7 - Memory dump
3. 19 - Профилирование java приложений. Работа с jvisualvm & asyncProfiler


## Receiver
java - синхронный REST-сервис. (reactor + swagger)

Синхронно:
- извлекает из сообщения идентификатор сообщения, код ведомства (классификатор), тип сообщения(классификатор) (по QName какого-нибудь элемента)
- проверяет возможность использования данного типа сообщения для ведомства
- проверяет по БД не дубликат ли это (далее для трассировки будет использоваться processGUID равный RequestID, или, если дубликат, будет использоваться существующий processGUID (сообщение может залететь повторно))
- извлекает блобы из сообщения, заменив их ссылками на блоб (хэш от данных MurmurHash) и раздельно сохраняет блобы и сообщение со ссылками в BlobStorage по пути ${processGUID}/имя_файла
- отправляет в Kafka(transaction producer) в топик to_generate для дальнейшей асинхронной обработки json вида:
```json
{
  "exchange": "550e8400-e29b-41d4-a716-446655440000", 
  "key": "2222222222222222222222222",
  "discriminator" : "exchangeMessage"  
}
```

### Используемые навыки:
1. 3 - Java Instrumentation & Java agent (opentelemetry-agent)
2. 30 - Сквозное логирование в микросервисах. (opentelemetry)
3. 33 - Rest: Swagger, OpenAPI
4. 35 - Kafka

![zipkin-tracing](docs/img/01-gateway-zipkin-tracing.png)
![zipkin-tracing](docs/img/05-01-receiver-tracing.png)


## Generator
java - сервис, отвечающий за генерацию json в зависимости от типа переданного сообщения. Генерирует новый идентификатор сообщения (считаем что формат messageID меняется вместе с форматом сообщения).

(jmh - можно помереть генерацию json freemarker, jackson или gson; с блобами и без)

- извлекает из kafak сообщение вида:
```json
{
"exchange": "550e8400-e29b-41d4-a716-446655440000",
"key": "2222222222222222222222222",
"discriminator" : "exchangeMessage"  
}
```
- по processGUID и messagID извлекает XML по messageID
- по messageType восстанавливает соответствующий jaxb-объект, маппит его в нужный DO, который преобразует в json; для json генерирует новый идентификатор
- json сохраняет в хранилище, в БД сохраняет соответствие messageID и jsonId. Если данные в БД уже были(пришёл повтор) то проверяет по хранилищу - надо ли генерировать новое сообщение 
- отправляет в kafka сообщение вида 
```json
{
  "exchange":"01989806-146c-7ebe-b0a7-bb2042538cb0",
  "key":"04a287a7-6c37-43e1-9e6d-9736fd4e32e4",
  "discriminator":"exchangeJson"}
```
![generator-kafka]((docs/img/06-01-generator-kafka.png)
![generator-s3]((docs/img/06-01-generator-s3.png)


## Sender
java - сервис, отвечающий за отправку данных.

- извлекает из kafka сообщение вида:
```json
{
  "exchange":"01989806-146c-7ebe-b0a7-bb2042538cb0",
  "key":"04a287a7-6c37-43e1-9e6d-9736fd4e32e4",
  "discriminator":"exchangeJson"}
```

- вносит в БД данные об отправке
- отправялет данные в нужные ендпоинты (вспомагательные сервисы extservice1 и extservice2)

Метрики:
- кол-во отправок по url
- скорость отправки данных (rpm)
- кол-во ошибок при отправках по url
- cpu
- ram


