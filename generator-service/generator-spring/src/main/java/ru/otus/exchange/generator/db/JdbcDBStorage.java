package ru.otus.exchange.generator.db;

import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.otus.exchange.generator.DBStorage;
import ru.otus.exchange.generator.errors.DBException;

@Slf4j
public class JdbcDBStorage implements DBStorage {

    private final JdbcTemplate jdbcTemplate;

    public JdbcDBStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public UUID getOrStoreUUID(String messageID, UUID jsonID) throws DBException {
        try {
            var result = jdbcTemplate.update(
                    "INSERT INTO generation_info" + "(message_id, json_id) "
                            + "VALUES (?, ?) "
                            + "ON CONFLICT (message_id) DO NOTHING;",
                    messageID,
                    jsonID.toString());
            if (result > 0) {
                log.info("record (message_id, json_id) ({}, {}) created", messageID, jsonID);
                return jsonID;
            }

            String jsonDBID = jdbcTemplate.queryForObject(
                    "SELECT json_id FROM generation_info where message_id = ?", String.class, messageID);

            log.info(" for message_id {} found exists json_id {})", messageID, jsonID);
            return UUID.fromString(jsonDBID);
        } catch (Exception e) {
            throw new DBException(e);
        }
    }
}
