package ru.otus.exchange.sender.core;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.extern.slf4j.Slf4j;
import ru.otus.exchange.common.KafkaMessage;
import ru.otus.exchange.sender.core.api.DBStorage;
import ru.otus.exchange.sender.core.api.DistributedLock;
import ru.otus.exchange.sender.core.api.MessageSender;
import ru.otus.exchange.sender.core.domain.Department;
import ru.otus.exchange.sender.core.domain.Message;
import ru.otus.exchange.sender.core.errors.SenderException;

@Slf4j
public class MessageUploaderImpl implements MessageUploader {

    private final DBStorage dbStorage;
    private final MessageSender messageSender;
    private final DistributedLock distributedLock;

    private final AtomicBoolean runProcess = new AtomicBoolean(false);

    private final Duration lockDuration;

    private final String depLockPrefix;

    public MessageUploaderImpl(
            DBStorage dbStorage,
            MessageSender messageSender,
            DistributedLock distributedLock,
            Duration lockDuration,
            String depLockPrefix) {
        this.dbStorage = dbStorage;
        this.messageSender = messageSender;
        this.distributedLock = distributedLock;
        this.lockDuration = lockDuration;
        this.depLockPrefix = depLockPrefix;
    }

    @Override
    public void upload(KafkaMessage kafkaMessage) {

        List<Department> toSendList = dbStorage.getDepartmentToSend(kafkaMessage.getDiscriminator());

        toSendList.stream()
                .map(dep -> {
                    Message message = new Message();
                    message.setDiscriminator(kafkaMessage.getDiscriminator());
                    message.setExchange(kafkaMessage.getExchange());
                    message.setKey(kafkaMessage.getKey());
                    message.setDepartmentId(dep.getId());
                    return message;
                })
                .forEach(dbStorage::addMessage); // add butch insert

        log.info("message {} processed", kafkaMessage);
    }

    @Override
    public void startSendProcess() {
        if (runProcess.compareAndSet(false, true)) {
            sendMessage();
        }
    }

    @Override
    public void stopSendProcess() {
        runProcess.set(false);
    }

    private boolean processDepartment(Department department) {
        String lockName = depLockPrefix + department.getCode();
        boolean messageFound = false;
        if (department.isActive() && distributedLock.acquireLock(lockName, lockDuration)) {
            try {
                log.info("processing department {}", department.getCode());
                CompletableFuture<?>[] futureList = dbStorage.findMessageToSend(department).stream()
                        .map(message -> CompletableFuture.runAsync(() -> {
                            try {
                                messageSender.send(
                                        department.getEndpoint(),
                                        message.getExchange().toString(),
                                        message.getKey());
                                dbStorage.setMessageSent(message);
                            } catch (SenderException se) {
                                log.error("send message {} error", message, se);
                            }
                        }))
                        .toArray(CompletableFuture[]::new);

                if (futureList.length > 0) {
                    messageFound = true;
                }

                CompletableFuture<Void> allOff = CompletableFuture.allOf(futureList);
                allOff.join();
            } finally {
                distributedLock.releaseLock(lockName);
            }
        }
        return messageFound;
    }

    public void sendMessage() {
        log.info("sendMessage start");

        while (runProcess.get()) {
            final AtomicBoolean messageFound = new AtomicBoolean(true);
            List<Department> departmentList = dbStorage.getDepartments();
            CompletableFuture<?>[] futureList = departmentList.stream()
                    .map(dep -> CompletableFuture.runAsync(() -> {
                        var result = processDepartment(dep);
                        messageFound.compareAndSet(true, result);
                    }))
                    .toArray(CompletableFuture[]::new);
            CompletableFuture<Void> allOff = CompletableFuture.allOf(futureList);
            allOff.join();

            if (!messageFound.get()) {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    log.warn("send process interrupted");
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }
        log.info("sendMessage complete");
    }
}
