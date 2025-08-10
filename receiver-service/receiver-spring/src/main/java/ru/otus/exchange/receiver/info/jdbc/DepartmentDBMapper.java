package ru.otus.exchange.receiver.info.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class DepartmentDBMapper implements RowMapper<DepartmentDB> {
    @Override
    public DepartmentDB mapRow(ResultSet rs, int rowNum) throws SQLException {
        DepartmentDB departmentDB = new DepartmentDB();
        departmentDB.setId(rs.getInt("id"));
        departmentDB.setCode(rs.getString("code"));
        departmentDB.setAcceptable(rs.getBoolean("is_acceptable"));
        return departmentDB;
    }
}
