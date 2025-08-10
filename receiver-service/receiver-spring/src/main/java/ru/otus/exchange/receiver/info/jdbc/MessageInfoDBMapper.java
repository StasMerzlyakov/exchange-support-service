package ru.otus.exchange.receiver.info.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class MessageInfoDBMapper implements RowMapper<MessageInfoDB> {
    @Override
    public MessageInfoDB mapRow(ResultSet rs, int rowNum) throws SQLException {
        MessageInfoDB messageInfoDB = new MessageInfoDB();
        messageInfoDB.setId(rs.getLong("id"));
        messageInfoDB.setMessageID(rs.getString("message_id"));
        messageInfoDB.setProcessGUID(rs.getString("process_guid"));
        messageInfoDB.setCreatorID(rs.getInt("creator_id"));
        messageInfoDB.setType(rs.getString("type"));
        return messageInfoDB;
    }
}
