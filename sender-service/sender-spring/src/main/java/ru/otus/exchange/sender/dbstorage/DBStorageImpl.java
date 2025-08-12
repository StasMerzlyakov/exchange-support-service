package ru.otus.exchange.sender.dbstorage;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import ru.otus.exchange.common.Discriminator;
import ru.otus.exchange.sender.core.api.DBStorage;
import ru.otus.exchange.sender.core.domain.Department;
import ru.otus.exchange.sender.core.domain.Message;
import ru.otus.exchange.sender.core.errors.DBStorageException;

@Slf4j
public class DBStorageImpl implements DBStorage {

    private final JdbcTemplate jdbcTemplate;

    private final int toSendLimit;

    private final DepartmentMapper departmentMapper = new DepartmentMapper();

    private final MessageMapper messageMapper = new MessageMapper();

    public DBStorageImpl(JdbcTemplate jdbcTemplate, int toSendLimit) {
        this.jdbcTemplate = jdbcTemplate;
        this.toSendLimit = toSendLimit;
    }

    @CacheEvict(value = "departments", allEntries = true)
    @Scheduled(fixedRateString = "${sender.cache-refresh-timeout}")
    public void emptyDepartmentsCache() {
        log.info("emptying departments cache");
    }

    @Override
    @Cacheable("departments")
    public List<Department> getDepartments() {
        try {
            return jdbcTemplate.query("SELECT id, code, endpoint, is_active FROM department", departmentMapper);
        } catch (Exception e) {
            throw new DBStorageException(e);
        }
    }

    @CacheEvict(value = "departmentsToSend", allEntries = true)
    @Scheduled(fixedRateString = "${sender.cache-refresh-timeout}")
    public void departmentsToSendCache() {
        log.info("emptying departmentsToSend cache");
    }

    @Override
    @Cacheable("departmentsToSend")
    public List<Department> getDepartmentToSend(Discriminator discriminator) {
        try {
            return jdbcTemplate.query("SELECT id, code, endpoint, is_active FROM department", departmentMapper);
        } catch (Exception e) {
            throw new DBStorageException(e);
        }
    }

    @Override
    public void addMessage(Message message) {
        try {
            jdbcTemplate.update(
                    "INSERT INTO message" + "(exchange, key, discriminator, department_id, sent)"
                            + "VALUES (?, ?, ?, ?, ?) "
                            + "ON CONFLICT (key, department_id) DO NOTHING;",
                    message.getExchange().toString(),
                    message.getKey(),
                    message.getDiscriminator().name(),
                    message.getDepartmentId(),
                    message.isSent());
        } catch (Exception e) {
            throw new DBStorageException(e);
        }
    }

    @Override
    public void setMessageSent(Message message) {
        try {
            jdbcTemplate.update("UPDATE message SET sent = true WHERE id = ?", message.getId());
        } catch (Exception e) {
            throw new DBStorageException(e);
        }
    }

    @Override
    public List<Message> findMessageToSend(Department department) {

        try {
            return jdbcTemplate.query(
                    "SELECT id, exchange, key, discriminator, department_id, sent "
                            + "FROM message WHERE sent = false AND department_id = ? LIMIT ?",
                    messageMapper,
                    department.getId(),
                    toSendLimit);
        } catch (Exception e) {
            throw new DBStorageException(e);
        }
    }
}
