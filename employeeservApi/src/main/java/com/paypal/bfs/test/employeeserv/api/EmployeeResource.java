package com.paypal.bfs.test.employeeserv.api;

import com.paypal.bfs.test.employeeserv.api.model.Employee;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Interface for employee resource operations.
 */
public interface EmployeeResource {

    /**
     * Retrieves the {@link Employee} resource by id.
     *
     * @param id employee id.
     * @return {@link Employee} resource.
     */
    @RequestMapping("/v1/bfs/employees/{id}")
    ResponseEntity<Employee> employeeGetById(@PathVariable("id") String id);

    @RequestMapping("/v1/bfs/employees/create")
    void create(@RequestParam String firstName,
                @RequestParam String lastName,
                @RequestParam String dateOfBirth,
                @RequestParam String line1,
                @RequestParam(value = "line2", required = false, defaultValue = "") String line2,
                @RequestParam String city,
                @RequestParam String state,
                @RequestParam String country,
                @RequestParam String zipCode);
}
