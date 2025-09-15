# Заметки по spring cloud gateway

## Имя фильтра 
Имя должно заканчиваться на суффикс GatewayFilterFactory

В списке фильтров указываем имя без суффикса:
``
classname: RequestIDGeneratorGatewayFilterFactory
filtername - RequestIDGenerator
``

## RequestRateLimiter
В основе лежит алгоритм token_bucket:
- есть скорость добавления токенов в секунду (replenishRate)
- есть емкость бака токенов, куда токены добавляются (burstCapacity)
- имеется стоимость запроса в токенах (requestedTokens)

Прим:
```
redis-rate-limiter.replenishRate: 1   # в одну секунду восстанавливается 1 токен
redis-rate-limiter.burstCapacity: 60  # моксимальное кол-во токенов 60
redis-rate-limiter.requestedTokens: 1 # стоимость запроса

итого - rpm 60/мин
```

Обязательно следим за KeyResolver. KeyResolver определяет политику вычисления ограничений. 
Например:
- ограничение по параметру запроса ?user=XXX  ```return exchange -> Mono.just(exchange.getRequest().getQueryParams().getFirst("user"));``` 
- ограничение просто на роут(ключ всегда один) ```return exchange -> Mono.just("xmlRouteResolver");```

Ссылки:
- https://spring.io/blog/2021/04/05/api-rate-limiting-with-spring-cloud-gateway
- https://en.wikipedia.org/wiki/Token_bucket

## Opentelemetry

В варианте встроенной библиотеки и интеграции с zipkin

```depenencies:
    implementation("io.micrometer:micrometer-registry-prometheus")
    implementation("io.micrometer:micrometer-tracing-bridge-otel")
    implementation("io.opentelemetry:opentelemetry-exporter-zipkin")
    implementation("io.opentelemetry.instrumentation:opentelemetry-instrumentation-annotations")
```

```application.yaml
spring:
  application:
    name: ${serviceId}
management:
  server:
    port: 7000
  endpoint:
    prometheus:
      #  fullacces by default; (see hasRole('ROLE_ADMIN'), authenticated)
      access: unrestricted
  endpoints:
    web:
      exposure:
        include: prometheus
  tracing:
    sampling:
      probability: 1.0
  zipkin:
    tracing:
      endpoint: http://zipkin:9411/api/v2/spans
    
```

```GateWayService
public static void main(String... args) {
    Hooks.enableAutomaticContextPropagation();  <--- для корректной работы с reactor и проброса traceId
    SpringApplication.run(GateWayService.class, args);
}
```

Для работы аннотации @Observed (ставится на бины или методы) дополнительно:

```depenencies:
implementation("org.springframework.boot:spring-boot-starter-aop")
```

```AppConfiguration

    @Bean
    public ObservedAspect observedAspect(ObservationRegistry observationRegistry) {
        return new ObservedAspect(observationRegistry);
    }

```

## CircuitBreaker

Фильтры на базе CircuitBreaker настраиваются, как обычно, но не забываем про http коды, 
на которые фильтр должен срабатывать:

```
...
filters:
  - name: CircuitBreaker
    args:
    name: backendA
    statusCodes:
      - 500  <--- http code 500
```


