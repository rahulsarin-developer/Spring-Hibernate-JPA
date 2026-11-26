package com.project.spring_hibernate_jpa.controller;

import com.project.spring_hibernate_jpa.dto.*;
import com.project.spring_hibernate_jpa.service.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class EmployeeControllerTest {

    private static final String BASE_URL =
            "/api/v1/employee";

    @Mock
    private EmployeeService employeeService;

    @InjectMocks
    private EmployeeController employeeController;

    private MockMvc mockMvc;

    private static final String EMPLOYEE_JSON = """
            {
                "name": "John",
                "email": "john@example.com",
                "department": "IT",
                "salary": 75000
            }
            """;

    @BeforeEach
    void setUp() {

        mockMvc = MockMvcBuilders
                .standaloneSetup(employeeController)
                .build();
    }

    // 1. Add Employee
    @Test
    @DisplayName("Should add employee successfully")
    void shouldAddEmployeeSuccessfully() throws Exception {

        mockMvc.perform(post(BASE_URL + "/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(EMPLOYEE_JSON))
                .andExpect(status().isCreated());

        verify(employeeService, times(1))
                .addEmployee(any(EmployeeRequest.class));
    }

    // 2. Fetch Employee
    @Test
    @DisplayName("Should fetch employee by ID")
    void shouldFetchEmployeeById() throws Exception {

        EmployeeResponse response =
                mock(EmployeeResponse.class);

        when(employeeService.getEmployeeById(1L))
                .thenReturn(response);

        mockMvc.perform(get(BASE_URL + "/fetch/{id}", 1L))
                .andExpect(status().isOk());

        verify(employeeService, times(1))
                .getEmployeeById(1L);
    }

    // 3. Update Employee
    @Test
    @DisplayName("Should update employee successfully")
    void shouldUpdateEmployeeSuccessfully() throws Exception {

        ApiResponse response = new ApiResponse(
                HttpStatus.OK.value(),
                "Employee Updated Successfully"
        );

        when(employeeService.updateEmployee(
                eq(1L), any(EmployeeRequest.class)))
                .thenReturn(ResponseEntity.ok(response));

        mockMvc.perform(put(BASE_URL + "/update/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(EMPLOYEE_JSON))
                .andExpect(status().isOk());

        verify(employeeService, times(1))
                .updateEmployee(
                        eq(1L), any(EmployeeRequest.class));
    }

    // 4. Delete Employee
    @Test
    @DisplayName("Should delete employee successfully")
    void shouldDeleteEmployeeSuccessfully() throws Exception {

        ApiResponse response = new ApiResponse(
                HttpStatus.OK.value(),
                "Employee Deleted Successfully"
        );

        when(employeeService.deleteEmployee(1L))
                .thenReturn(ResponseEntity.ok(response));

        mockMvc.perform(delete(BASE_URL + "/delete/{id}", 1L))
                .andExpect(status().isOk());

        verify(employeeService, times(1))
                .deleteEmployee(1L);
    }

    // 5. Update Salary
    @Test
    @DisplayName("Should update employee salary successfully")
    void shouldUpdateSalarySuccessfully() throws Exception {

        String salaryJson = """
                {
                    "salary": 95000
                }
                """;

        ApiResponse response = new ApiResponse(
                HttpStatus.OK.value(),
                "Salary Updated Successfully"
        );

        when(employeeService.updateSalary(
                eq(1L), any(SalaryRequest.class)))
                .thenReturn(ResponseEntity.ok(response));

        mockMvc.perform(
                        patch(BASE_URL + "/update/{id}/salary", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(salaryJson))
                .andExpect(status().isOk());

        verify(employeeService, times(1))
                .updateSalary(
                        eq(1L), any(SalaryRequest.class));
    }

    // 6. Search with default parameters
    @Test
    @DisplayName("Should search employees with default parameters")
    void shouldSearchEmployeesWithDefaultParameters()
            throws Exception {

        mockMvc.perform(get(BASE_URL + "/search"))
                .andExpect(status().isOk());

        verify(employeeService, times(1))
                .searchRecord(
                        any(EmployeeFilter.class),
                        eq(0),
                        eq(10),
                        eq("id"),
                        eq("asc")
                );
    }

    // 7. Search with pagination
    @Test
    @DisplayName("Should search employees with pagination")
    void shouldSearchEmployeesWithPagination()
            throws Exception {

        mockMvc.perform(get(BASE_URL + "/search")
                        .param("page", "1")
                        .param("size", "5"))
                .andExpect(status().isOk());

        verify(employeeService, times(1))
                .searchRecord(
                        any(EmployeeFilter.class),
                        eq(1),
                        eq(5),
                        eq("id"),
                        eq("asc")
                );
    }

    // 8. Search with sorting
    @Test
    @DisplayName("Should search employees with sorting")
    void shouldSearchEmployeesWithSorting()
            throws Exception {

        mockMvc.perform(get(BASE_URL + "/search")
                        .param("sortBy", "salary")
                        .param("direction", "desc"))
                .andExpect(status().isOk());

        verify(employeeService, times(1))
                .searchRecord(
                        any(EmployeeFilter.class),
                        eq(0),
                        eq(10),
                        eq("salary"),
                        eq("desc")
                );
    }

    // 9. Search with filters
    @Test
    @DisplayName("Should search employees with filters")
    void shouldSearchEmployeesWithFilters()
            throws Exception {

        mockMvc.perform(get(BASE_URL + "/search")
                        .param("name", "John")
                        .param("department", "IT")
                        .param("minSalary", "50000")
                        .param("maxSalary", "100000"))
                .andExpect(status().isOk());

        verify(employeeService, times(1))
                .searchRecord(
                        any(EmployeeFilter.class),
                        eq(0),
                        eq(10),
                        eq("id"),
                        eq("asc")
                );
    }

    // 10. Count all employees
    @Test
    @DisplayName("Should count all employees")
    void shouldCountAllEmployees() throws Exception {

        when(employeeService.count(null))
                .thenReturn(100L);

        mockMvc.perform(get(BASE_URL + "/count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(100));

        verify(employeeService, times(1))
                .count(null);
    }

    // 11. Count employees by department
    @Test
    @DisplayName("Should count employees by department")
    void shouldCountEmployeesByDepartment()
            throws Exception {

        when(employeeService.count("IT"))
                .thenReturn(25L);

        mockMvc.perform(get(BASE_URL + "/count")
                        .param("department", "IT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(25));

        verify(employeeService, times(1))
                .count("IT");
    }

    // 12. Invalid Employee ID
    @Test
    @DisplayName("Should reject invalid employee ID")
    void shouldRejectInvalidEmployeeId()
            throws Exception {

        mockMvc.perform(get(BASE_URL + "/fetch/abc"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(employeeService);
    }

    // 13. Missing request body
    @Test
    @DisplayName("Should reject missing request body")
    void shouldRejectMissingRequestBody()
            throws Exception {

        mockMvc.perform(post(BASE_URL + "/add")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(employeeService);
    }

    // 14. Invalid pagination parameter
    @Test
    @DisplayName("Should reject invalid pagination parameter")
    void shouldRejectInvalidPaginationParameter()
            throws Exception {

        mockMvc.perform(get(BASE_URL + "/search")
                        .param("page", "abc"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(employeeService);
    }
}