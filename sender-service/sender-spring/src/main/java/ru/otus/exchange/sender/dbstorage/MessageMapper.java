package ru.otus.exchange.sender.dbstorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import org.springframework.jdbc.core.RowMapper;
import ru.otus.exchange.common.Discriminator;
import ru.otus.exchange.sender.core.domain.Message;

public class MessageMapper implements RowMapper<Message> {
    @Override
    public Message mapRow(ResultSet rs, int rowNum) throws SQLException {
        Message message = new Message();
        message.setId(rs.getInt("id"));
        message.setExchange(UUID.fromString(rs.getString("exchange")));
        message.setKey(rs.getString("key"));
        message.setDiscriminator(Discriminator.valueOf(rs.getString("discriminator")));
        message.setDepartmentId(rs.getInt("department_id"));
        message.setSent(rs.getBoolean("sent"));
        return message;
    }
}
