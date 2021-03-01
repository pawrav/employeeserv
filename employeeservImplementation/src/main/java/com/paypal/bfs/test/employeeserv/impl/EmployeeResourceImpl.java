package com.paypal.bfs.test.employeeserv.impl;

import com.paypal.bfs.test.employeeserv.api.EmployeeResource;
import com.paypal.bfs.test.employeeserv.api.model.Address;
import com.paypal.bfs.test.employeeserv.api.model.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementation class for employee resource.
 */
@RestController
public class EmployeeResourceImpl implements EmployeeResource {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static String SELECT_QUERY = "SELECT a.*, b.* FROM employee AS a JOIN address AS b " +
            "ON a.id = b.employee_id WHERE id = ?";

    private static String SELECT_EMPLOYEE_QUERY = "SELECT count(id) FROM employee AS a " +
            "WHERE a.first_name = ? AND a.last_name = ? AND a.date_of_birth = ?";

    private static String INSERT_EMPLOYEE_QUERY = "INSERT INTO employee (first_name, last_name, date_of_birth) VALUES " +
            " (?, ?, ?)";

    private static String INSERT_ADDRESS_QUERY = "INSERT INTO address (employee_id, line1, line2, city, state, country, zip_code) VALUES " +
            "(?, ?, ?, ?, ?, ?, ?)";

    Logger logger = Logger.getLogger("EmployeeService");

    @Override
    public ResponseEntity<Employee> employeeGetById(String id) {

        Employee emp = null;

        try {
            emp = (Employee) jdbcTemplate.queryForObject(SELECT_QUERY, new Object[]{id},
                    (resultSet, i) -> {
                        Employee employee = new Employee();
                        Address address = new Address();

                        employee.setFirstName(resultSet.getString("first_name"));
                        employee.setLastName(resultSet.getString("last_name"));
                        employee.setDateOfBirth(resultSet.getDate("date_of_birth").toString());

                        address.setLine1(resultSet.getString("line1"));
                        address.setLine2(resultSet.getString("line2"));
                        address.setCity(resultSet.getString("city"));
                        address.setState(resultSet.getString("state"));
                        address.setCountry(resultSet.getString("country"));
                        address.setZipCode(resultSet.getString("zip_code"));

                        employee.setAddress(address);

                        return employee;
                    });
        } catch (EmptyResultDataAccessException e) {
            logger.log(Level.WARNING, e.getMessage(), e);
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(emp, HttpStatus.OK);
    }

    @Override
    @Transactional
    public void create(String firstName, String lastName, String dateOfBirth,
                       String line1, String line2, String city,
                       String state, String country, String zipCode) {

        int count = jdbcTemplate.queryForObject(SELECT_EMPLOYEE_QUERY, new Object[]{firstName, lastName, dateOfBirth}, Integer.class);

        if (count == 0) {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(
                    new PreparedStatementCreator() {
                        public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                            PreparedStatement ps = connection.prepareStatement(INSERT_EMPLOYEE_QUERY, new String[]{"id"});
                            ps.setString(1, firstName);
                            ps.setString(2, lastName);
                            ps.setString(3, dateOfBirth);
                            return ps;
                        }
                    },
                    keyHolder);

            jdbcTemplate.update(INSERT_ADDRESS_QUERY, keyHolder.getKey(), line1, line2, city, state, country, zipCode);
        }

    }
}
