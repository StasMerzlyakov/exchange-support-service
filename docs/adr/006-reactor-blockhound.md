# Проблема
С реактором проблема - не всегда видно что используются бокирующие вызовы.

# Решение
Нашел проект https://github.com/reactor/BlockHound
Работает как java-agent. Требует для работы опций 
```-XX:+AllowRedefinitionToAddDeleteMethods  -XX:+EnableDynamicAgentLoading```
Для управления включением/отключениесм добавил в blob-storage-spring конфигурацимю

```
@Configuration
@ConditionalOnProperty(name= "blockHound", havingValue = "on")
public class BlockHoundConfiguration {

    @PostConstruct
    void init() {
        BlockHound.install();
    }
}
```
