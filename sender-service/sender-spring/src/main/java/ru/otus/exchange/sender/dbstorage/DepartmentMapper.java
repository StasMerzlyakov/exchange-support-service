package ru.otus.exchange.sender.dbstorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import ru.otus.exchange.sender.core.domain.Department;

public class DepartmentMapper implements RowMapper<Department> {
    @Override
    public Department mapRow(ResultSet rs, int rowNum) throws SQLException {
        Department department = new Department();
        department.setId(rs.getInt("id"));
        department.setCode(rs.getString("code"));
        department.setEndpoint(rs.getString("endpoint"));
        department.setActive(rs.getBoolean("is_active"));
        return department;
    }
}
