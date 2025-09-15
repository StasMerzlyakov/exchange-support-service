package ru.otus.exchange.receiver.info.jdbc;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;

@Slf4j
public class JdbcOperationsImpl implements JdbcOperations {

    private final JdbcTemplate jdbcTemplate;

    private static final MessageInfoDBMapper MESSAGE_INFO_MAPPER = new MessageInfoDBMapper();

    private static final DepartmentDBMapper DEPARTMENT_MAPPER = new DepartmentDBMapper();

    public JdbcOperationsImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @CacheEvict(value = "departments", allEntries = true)
    @Scheduled(fixedRateString = "${receiver.cache-refresh-timeout}")
    public void emptyDepartmentsCache() {
        log.info("emptying departments cache");
    }

    @Override
    @Cacheable("departments")
    public DepartmentDB findDepartmentByCode(String code) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT id , code, is_acceptable FROM department WHERE code = ? ", DEPARTMENT_MAPPER, code);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public void addMessageInfo(MessageInfoDB messageInfoDB) {
        jdbcTemplate.update(
                "INSERT INTO message_info" + "(message_id, process_guid, creator_id, type) "
                        + "VALUES (?, ?, ?, ?) "
                        + "ON CONFLICT (message_id) DO NOTHING;",
                messageInfoDB.messageID,
                messageInfoDB.processGUID,
                messageInfoDB.creatorID,
                messageInfoDB.type);
    }

    @Override
    public MessageInfoDB findMessageInfo(String messageID) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT id, message_id, process_guid, creator_id, type FROM message_info WHERE message_id = ? ",
                    MESSAGE_INFO_MAPPER,
                    messageID);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
}
