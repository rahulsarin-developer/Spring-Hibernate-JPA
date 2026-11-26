package com.project.spring_hibernate_jpa.service;

import com.project.spring_hibernate_jpa.dto.*;
import com.project.spring_hibernate_jpa.exception.ConflictException;
import com.project.spring_hibernate_jpa.exception.ResourceNotFoundException;
import com.project.spring_hibernate_jpa.model.Employee;
import com.project.spring_hibernate_jpa.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.Captor;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceImplTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    @Captor
    private ArgumentCaptor<Employee> employeeCaptor;

    private Employee employee;
    private EmployeeRequest employeeRequest;
    private EmployeeFilter employeeFilter;

    private static final Long EMPLOYEE_ID = 1L;

    @BeforeEach
    void setUp() {

        employee = new Employee();

        employee.setId(EMPLOYEE_ID);
        employee.setName("John");
        employee.setEmail("john@gmail.com");
        employee.setDepartment("IT");
        employee.setSalary(new BigDecimal("50000.00"));
        employee.setVersion(0L);
        employee.setCreatedAt(
                Instant.parse("2026-01-01T10:00:00Z")
        );

        employeeRequest = new EmployeeRequest(
                "John",
                "john@gmail.com",
                "IT",
                new BigDecimal("50000.00")
        );

        employeeFilter = new EmployeeFilter(
                null,
                "IT",
                null,
                null
        );
    }

    // ==========================================
    // ADD EMPLOYEE
    // ==========================================
    @Test
    @DisplayName("Add employee successfully")
    void addEmployee_Success() {

        when(employeeRepository.existsByEmail(
                employeeRequest.email()))
                .thenReturn(false);

        employeeService.addEmployee(employeeRequest);

        verify(employeeRepository)
                .existsByEmail(employeeRequest.email());

        verify(employeeRepository)
                .saveEmployee(employeeCaptor.capture());

        Employee savedEmployee = employeeCaptor.getValue();

        assertAll(
                () -> assertEquals(
                        employeeRequest.name(),
                        savedEmployee.getName()),

                () -> assertEquals(
                        employeeRequest.email(),
                        savedEmployee.getEmail()),

                () -> assertEquals(
                        employeeRequest.department(),
                        savedEmployee.getDepartment()),

                () -> assertEquals(
                        employeeRequest.salary(),
                        savedEmployee.getSalary())
        );
    }

    @Test
    @DisplayName("Add employee with duplicate email")
    void addEmployee_DuplicateEmail_ThrowsException() {

        when(employeeRepository.existsByEmail(
                employeeRequest.email()))
                .thenReturn(true);

        ConflictException exception = assertThrows(
                ConflictException.class,
                () -> employeeService.addEmployee(
                        employeeRequest)
        );

        assertEquals(
                "Employee already exists with email: "
                        + employeeRequest.email(),
                exception.getMessage()
        );

        verify(employeeRepository, never())
                .saveEmployee(any(Employee.class));
    }

    @Test
    @DisplayName("Add employee with zero salary")
    void addEmployee_ZeroSalary_Success() {

        EmployeeRequest request = new EmployeeRequest(
                "John",
                "john@gmail.com",
                "IT",
                BigDecimal.ZERO
        );

        when(employeeRepository.existsByEmail(request.email()))
                .thenReturn(false);

        employeeService.addEmployee(request);

        verify(employeeRepository)
                .saveEmployee(employeeCaptor.capture());

        assertEquals(
                BigDecimal.ZERO,
                employeeCaptor.getValue().getSalary()
        );
    }

    // ==========================================
    // GET EMPLOYEE BY ID
    // ==========================================
    @Test
    @DisplayName("Get employee by ID successfully")
    void getEmployeeById_Success() {

        when(employeeRepository.findById(EMPLOYEE_ID))
                .thenReturn(Optional.of(employee));

        EmployeeResponse response =
                employeeService.getEmployeeById(EMPLOYEE_ID);

        assertNotNull(response);

        assertAll(
                () -> assertEquals(
                        employee.getId(), response.id()),

                () -> assertEquals(
                        employee.getName(), response.name()),

                () -> assertEquals(
                        employee.getEmail(), response.email()),

                () -> assertEquals(
                        employee.getDepartment(),
                        response.department()),

                () -> assertEquals(
                        employee.getSalary(), response.salary()),

                () -> assertEquals(
                        employee.getVersion().longValue(),
                        response.version()),

                () -> assertEquals(
                        employee.getCreatedAt(),
                        response.createdAt())
        );

        verify(employeeRepository).findById(EMPLOYEE_ID);
    }

    @Test
    @DisplayName("Get employee with non-existing ID")
    void getEmployeeById_NotFound_ThrowsException() {

        when(employeeRepository.findById(EMPLOYEE_ID))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> employeeService.getEmployeeById(
                        EMPLOYEE_ID)
        );

        assertEquals(
                "Employee not found with id: " + EMPLOYEE_ID,
                exception.getMessage()
        );

        verify(employeeRepository).findById(EMPLOYEE_ID);
    }

    // ==========================================
    // UPDATE EMPLOYEE
    // ==========================================
    @Test
    @DisplayName("Update employee successfully")
    void updateEmployee_Success() {

        EmployeeRequest updatedRequest = new EmployeeRequest(
                "David",
                "david@gmail.com",
                "HR",
                new BigDecimal("75000.00")
        );

        when(employeeRepository.findById(EMPLOYEE_ID))
                .thenReturn(Optional.of(employee));

        when(employeeRepository.existsByEmail(
                updatedRequest.email(), EMPLOYEE_ID))
                .thenReturn(false);

        ResponseEntity<ApiResponse> response =
                employeeService.updateEmployee(
                        EMPLOYEE_ID, updatedRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        assertEquals(200, response.getBody().status());

        assertEquals(
                "Employee Updated Successfully",
                response.getBody().message()
        );

        verify(employeeRepository)
                .updateEmployee(employeeCaptor.capture());

        Employee updatedEmployee = employeeCaptor.getValue();

        assertAll(
                () -> assertEquals(
                        EMPLOYEE_ID, updatedEmployee.getId()),

                () -> assertEquals(
                        updatedRequest.name(),
                        updatedEmployee.getName()),

                () -> assertEquals(
                        updatedRequest.email(),
                        updatedEmployee.getEmail()),

                () -> assertEquals(
                        updatedRequest.department(),
                        updatedEmployee.getDepartment()),

                () -> assertEquals(
                        updatedRequest.salary(),
                        updatedEmployee.getSalary())
        );
    }

    @Test
    @DisplayName("Update employee with existing own email")
    void updateEmployee_SameEmail_Success() {

        when(employeeRepository.findById(EMPLOYEE_ID))
                .thenReturn(Optional.of(employee));

        when(employeeRepository.existsByEmail(
                employeeRequest.email(), EMPLOYEE_ID))
                .thenReturn(false);

        ResponseEntity<ApiResponse> response =
                employeeService.updateEmployee(
                        EMPLOYEE_ID, employeeRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        verify(employeeRepository)
                .existsByEmail(
                        employeeRequest.email(), EMPLOYEE_ID);

        verify(employeeRepository).updateEmployee(employee);
    }

    @Test
    @DisplayName("Update non-existing employee")
    void updateEmployee_NotFound_ThrowsException() {

        when(employeeRepository.findById(EMPLOYEE_ID))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> employeeService.updateEmployee(
                        EMPLOYEE_ID, employeeRequest)
        );

        assertEquals(
                "Employee not found with id: " + EMPLOYEE_ID,
                exception.getMessage()
        );

        verify(employeeRepository, never())
                .existsByEmail(anyString(), anyLong());

        verify(employeeRepository, never())
                .updateEmployee(any(Employee.class));
    }

    @Test
    @DisplayName("Update employee with duplicate email")
    void updateEmployee_DuplicateEmail_ThrowsException() {

        when(employeeRepository.findById(EMPLOYEE_ID))
                .thenReturn(Optional.of(employee));

        when(employeeRepository.existsByEmail(
                employeeRequest.email(), EMPLOYEE_ID))
                .thenReturn(true);

        ConflictException exception = assertThrows(
                ConflictException.class,
                () -> employeeService.updateEmployee(
                        EMPLOYEE_ID, employeeRequest)
        );

        assertEquals(
                "Employee already exists with email: "
                        + employeeRequest.email(),
                exception.getMessage()
        );

        verify(employeeRepository, never())
                .updateEmployee(any(Employee.class));
    }

    // ==========================================
    // DELETE EMPLOYEE
    // ==========================================
    @Test
    @DisplayName("Delete employee successfully")
    void deleteEmployee_Success() {

        when(employeeRepository.findById(EMPLOYEE_ID))
                .thenReturn(Optional.of(employee));

        ResponseEntity<ApiResponse> response =
                employeeService.deleteEmployee(EMPLOYEE_ID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        assertEquals(200, response.getBody().status());

        assertEquals(
                "Employee Deleted Successfully",
                response.getBody().message()
        );

        verify(employeeRepository).findById(EMPLOYEE_ID);
        verify(employeeRepository).delete(employee);
    }

    @Test
    @DisplayName("Delete non-existing employee")
    void deleteEmployee_NotFound_ThrowsException() {

        when(employeeRepository.findById(EMPLOYEE_ID))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> employeeService.deleteEmployee(
                        EMPLOYEE_ID)
        );

        assertEquals(
                "Employee not found with id: " + EMPLOYEE_ID,
                exception.getMessage()
        );

        verify(employeeRepository, never())
                .delete(any(Employee.class));
    }

    // ==========================================
    // UPDATE SALARY
    // ==========================================
    @Test
    @DisplayName("Update employee salary successfully")
    void updateSalary_Success() {

        SalaryRequest salaryRequest = new SalaryRequest(
                new BigDecimal("85000.00")
        );

        when(employeeRepository.findById(EMPLOYEE_ID))
                .thenReturn(Optional.of(employee));

        ResponseEntity<ApiResponse> response =
                employeeService.updateSalary(
                        EMPLOYEE_ID, salaryRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        assertEquals(200, response.getBody().status());

        assertEquals(
                "Employee Salary Updated Successfully",
                response.getBody().message()
        );

        verify(employeeRepository)
                .updateEmployee(employeeCaptor.capture());

        Employee updatedEmployee = employeeCaptor.getValue();

        assertAll(
                () -> assertEquals(
                        salaryRequest.salary(),
                        updatedEmployee.getSalary()),

                () -> assertEquals(
                        "John", updatedEmployee.getName()),

                () -> assertEquals(
                        "john@gmail.com",
                        updatedEmployee.getEmail()),

                () -> assertEquals(
                        "IT", updatedEmployee.getDepartment())
        );
    }

    @Test
    @DisplayName("Update employee salary to zero")
    void updateSalary_ZeroSalary_Success() {

        SalaryRequest salaryRequest =
                new SalaryRequest(BigDecimal.ZERO);

        when(employeeRepository.findById(EMPLOYEE_ID))
                .thenReturn(Optional.of(employee));

        ResponseEntity<ApiResponse> response =
                employeeService.updateSalary(
                        EMPLOYEE_ID, salaryRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        assertEquals(BigDecimal.ZERO, employee.getSalary());

        verify(employeeRepository).updateEmployee(employee);
    }

    @Test
    @DisplayName("Update salary of non-existing employee")
    void updateSalary_NotFound_ThrowsException() {

        SalaryRequest salaryRequest = new SalaryRequest(
                new BigDecimal("85000.00")
        );

        when(employeeRepository.findById(EMPLOYEE_ID))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> employeeService.updateSalary(
                        EMPLOYEE_ID, salaryRequest)
        );

        assertEquals(
                "Employee not found with id: " + EMPLOYEE_ID,
                exception.getMessage()
        );

        verify(employeeRepository, never())
                .updateEmployee(any(Employee.class));
    }

    // ==========================================
    // SEARCH RECORD - SUCCESS
    // ==========================================
    @Test
    @DisplayName("Search employees successfully")
    void searchRecord_Success() {

        when(employeeRepository.searchRecord(
                employeeFilter, 0, 10, "id", true))
                .thenReturn(List.of(employee));

        when(employeeRepository.count(employeeFilter))
                .thenReturn(1L);

        PageResponse<EmployeeResponse> response =
                employeeService.searchRecord(
                        employeeFilter, 0, 10, "id", "asc");

        assertNotNull(response);

        assertAll(
                () -> assertEquals(1, response.content().size()),
                () -> assertEquals(0, response.page()),
                () -> assertEquals(10, response.size()),
                () -> assertEquals(1L, response.totalElements()),
                () -> assertEquals(1, response.totalPages())
        );

        EmployeeResponse result = response.content().get(0);

        assertAll(
                () -> assertEquals(
                        employee.getId(), result.id()),

                () -> assertEquals(
                        employee.getName(), result.name()),

                () -> assertEquals(
                        employee.getEmail(), result.email()),

                () -> assertEquals(
                        employee.getDepartment(),
                        result.department()),

                () -> assertEquals(
                        employee.getSalary(), result.salary()),

                () -> assertEquals(
                        employee.getVersion().longValue(),
                        result.version()),

                () -> assertEquals(
                        employee.getCreatedAt(),
                        result.createdAt())
        );

        verify(employeeRepository)
                .searchRecord(
                        employeeFilter, 0, 10, "id", true);

        verify(employeeRepository).count(employeeFilter);
    }

    @Test
    @DisplayName("Search employees with empty result")
    void searchRecord_EmptyResult() {

        when(employeeRepository.searchRecord(
                employeeFilter, 0, 10, "id", true))
                .thenReturn(List.of());

        when(employeeRepository.count(employeeFilter))
                .thenReturn(0L);

        PageResponse<EmployeeResponse> response =
                employeeService.searchRecord(
                        employeeFilter, 0, 10, "id", "asc");

        assertNotNull(response);
        assertTrue(response.content().isEmpty());
        assertEquals(0L, response.totalElements());
        assertEquals(0, response.totalPages());
    }

    @Test
    @DisplayName("Search employees without filters")
    void searchRecord_NoFilters_Success() {

        EmployeeFilter filter = new EmployeeFilter(
                null, null, null, null
        );

        when(employeeRepository.searchRecord(
                filter, 0, 10, "id", true))
                .thenReturn(List.of(employee));

        when(employeeRepository.count(filter))
                .thenReturn(1L);

        PageResponse<EmployeeResponse> response =
                employeeService.searchRecord(
                        filter, 0, 10, "id", "asc");

        assertEquals(1, response.content().size());
        assertEquals(1L, response.totalElements());

        verify(employeeRepository)
                .searchRecord(filter, 0, 10, "id", true);
    }

    // ==========================================
    // PAGINATION
    // ==========================================
    @Test
    @DisplayName("Verify pagination and total pages")
    void searchRecord_Pagination_Success() {

        when(employeeRepository.searchRecord(
                employeeFilter, 2, 10, "id", true))
                .thenReturn(List.of(employee));

        when(employeeRepository.count(employeeFilter))
                .thenReturn(21L);

        PageResponse<EmployeeResponse> response =
                employeeService.searchRecord(
                        employeeFilter, 2, 10, "id", "asc");

        assertAll(
                () -> assertEquals(2, response.page()),
                () -> assertEquals(10, response.size()),
                () -> assertEquals(21L, response.totalElements()),
                () -> assertEquals(3, response.totalPages()),
                () -> assertEquals(1, response.content().size())
        );
    }

    @Test
    @DisplayName("Verify exact page division")
    void searchRecord_ExactTotalPages() {

        when(employeeRepository.searchRecord(
                employeeFilter, 0, 10, "id", true))
                .thenReturn(List.of(employee));

        when(employeeRepository.count(employeeFilter))
                .thenReturn(20L);

        PageResponse<EmployeeResponse> response =
                employeeService.searchRecord(
                        employeeFilter, 0, 10, "id", "asc");

        assertEquals(2, response.totalPages());
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 100})
    @DisplayName("Verify valid page size boundaries")
    void searchRecord_ValidPageSize(int size) {

        when(employeeRepository.searchRecord(
                employeeFilter, 0, size, "id", true))
                .thenReturn(List.of(employee));

        when(employeeRepository.count(employeeFilter))
                .thenReturn(1L);

        PageResponse<EmployeeResponse> response =
                employeeService.searchRecord(
                        employeeFilter, 0, size, "id", "asc");

        assertEquals(size, response.size());
        assertEquals(1, response.totalPages());
    }

    @ParameterizedTest
    @CsvSource({
            "-1, 10",
            "0, 0",
            "0, -1",
            "0, 101"
    })
    @DisplayName("Reject invalid page number or page size")
    void searchRecord_InvalidPagination_ThrowsException(
            int page, int size) {

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> employeeService.searchRecord(
                        employeeFilter,
                        page,
                        size,
                        "id",
                        "asc")
        );

        assertEquals(
                "page must be >= 0 and size must be 1..100",
                exception.getMessage()
        );

        verifyNoInteractions(employeeRepository);
    }

    // ==========================================
    // SORTING
    // ==========================================
    @ParameterizedTest
    @ValueSource(strings = {
            "id",
            "name",
            "email",
            "department",
            "salary",
            "createdAt"
    })
    @DisplayName("Verify supported sorting fields")
    void searchRecord_ValidSortFields(String sortBy) {

        when(employeeRepository.searchRecord(
                employeeFilter, 0, 10, sortBy, true))
                .thenReturn(List.of(employee));

        when(employeeRepository.count(employeeFilter))
                .thenReturn(1L);

        PageResponse<EmployeeResponse> response =
                employeeService.searchRecord(
                        employeeFilter, 0, 10, sortBy, "asc");

        assertNotNull(response);

        verify(employeeRepository)
                .searchRecord(
                        employeeFilter, 0, 10, sortBy, true);
    }

    @ParameterizedTest
    @CsvSource({
            "asc, true",
            "ASC, true",
            "desc, false",
            "DESC, false"
    })
    @DisplayName("Verify ascending and descending sorting")
    void searchRecord_ValidSortDirection(
            String direction, boolean ascending) {

        when(employeeRepository.searchRecord(
                employeeFilter, 0, 10, "salary", ascending))
                .thenReturn(List.of(employee));

        when(employeeRepository.count(employeeFilter))
                .thenReturn(1L);

        PageResponse<EmployeeResponse> response =
                employeeService.searchRecord(
                        employeeFilter,
                        0,
                        10,
                        "salary",
                        direction);

        assertNotNull(response);

        verify(employeeRepository)
                .searchRecord(
                        employeeFilter,
                        0,
                        10,
                        "salary",
                        ascending);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "invalidField",
            "address",
            "password",
            ""
    })
    @DisplayName("Reject unsupported sorting fields")
    void searchRecord_InvalidSortField_ThrowsException(
            String sortBy) {

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> employeeService.searchRecord(
                        employeeFilter, 0, 10, sortBy, "asc")
        );

        assertEquals(
                "Unsupported sort field",
                exception.getMessage()
        );

        verifyNoInteractions(employeeRepository);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "ascending",
            "descending",
            "invalid",
            ""
    })
    @DisplayName("Reject invalid sorting direction")
    void searchRecord_InvalidDirection_ThrowsException(
            String direction) {

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> employeeService.searchRecord(
                        employeeFilter, 0, 10, "id", direction)
        );

        assertEquals(
                "direction must be asc or desc",
                exception.getMessage()
        );

        verifyNoInteractions(employeeRepository);
    }

    // ==========================================
    // FILTERING
    // ==========================================
    @Test
    @DisplayName("Search employee using all filters")
    void searchRecord_AllFilters_Success() {

        EmployeeFilter filter = new EmployeeFilter(
                "John",
                "IT",
                new BigDecimal("30000"),
                new BigDecimal("80000")
        );

        when(employeeRepository.searchRecord(
                filter, 0, 10, "salary", true))
                .thenReturn(List.of(employee));

        when(employeeRepository.count(filter))
                .thenReturn(1L);

        PageResponse<EmployeeResponse> response =
                employeeService.searchRecord(
                        filter, 0, 10, "salary", "asc");

        assertEquals(1, response.content().size());
        assertEquals("John", response.content().get(0).name());
        assertEquals("IT", response.content().get(0).department());

        verify(employeeRepository)
                .searchRecord(filter, 0, 10, "salary", true);

        verify(employeeRepository).count(filter);
    }

    @Test
    @DisplayName("Search with minimum salary only")
    void searchRecord_MinSalaryOnly_Success() {

        EmployeeFilter filter = new EmployeeFilter(
                null,
                null,
                new BigDecimal("30000"),
                null
        );

        when(employeeRepository.searchRecord(
                filter, 0, 10, "salary", true))
                .thenReturn(List.of(employee));

        when(employeeRepository.count(filter))
                .thenReturn(1L);

        PageResponse<EmployeeResponse> response =
                employeeService.searchRecord(
                        filter, 0, 10, "salary", "asc");

        assertEquals(1, response.content().size());

        verify(employeeRepository)
                .searchRecord(filter, 0, 10, "salary", true);
    }

    @Test
    @DisplayName("Search with maximum salary only")
    void searchRecord_MaxSalaryOnly_Success() {

        EmployeeFilter filter = new EmployeeFilter(
                null,
                null,
                null,
                new BigDecimal("80000")
        );

        when(employeeRepository.searchRecord(
                filter, 0, 10, "salary", true))
                .thenReturn(List.of(employee));

        when(employeeRepository.count(filter))
                .thenReturn(1L);

        PageResponse<EmployeeResponse> response =
                employeeService.searchRecord(
                        filter, 0, 10, "salary", "asc");

        assertEquals(1, response.content().size());

        verify(employeeRepository)
                .searchRecord(filter, 0, 10, "salary", true);
    }

    @Test
    @DisplayName("Search with equal minimum and maximum salary")
    void searchRecord_EqualSalaryRange_Success() {

        EmployeeFilter filter = new EmployeeFilter(
                null,
                null,
                new BigDecimal("50000"),
                new BigDecimal("50000")
        );

        when(employeeRepository.searchRecord(
                filter, 0, 10, "salary", true))
                .thenReturn(List.of(employee));

        when(employeeRepository.count(filter))
                .thenReturn(1L);

        PageResponse<EmployeeResponse> response =
                employeeService.searchRecord(
                        filter, 0, 10, "salary", "asc");

        assertEquals(1, response.content().size());
    }

    @Test
    @DisplayName("Reject invalid salary range")
    void searchRecord_InvalidSalaryRange_ThrowsException() {

        EmployeeFilter filter = new EmployeeFilter(
                null,
                null,
                new BigDecimal("90000"),
                new BigDecimal("30000")
        );

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> employeeService.searchRecord(
                        filter, 0, 10, "salary", "asc")
        );

        assertEquals(
                "minSalary must be <= maxSalary",
                exception.getMessage()
        );

        verifyNoInteractions(employeeRepository);
    }

    // ==========================================
    // COUNT EMPLOYEES
    // ==========================================
    @Test
    @DisplayName("Count employees by department")
    void count_ByDepartment_Success() {

        when(employeeRepository.countByDepartment("IT"))
                .thenReturn(10L);

        Long count = employeeService.count("IT");

        assertEquals(Long.valueOf(10L), count);

        verify(employeeRepository).countByDepartment("IT");
    }

    @Test
    @DisplayName("Count employees without department")
    void count_AllEmployees_Success() {

        when(employeeRepository.countByDepartment(null))
                .thenReturn(100L);

        Long count = employeeService.count(null);

        assertEquals(Long.valueOf(100L), count);

        verify(employeeRepository).countByDepartment(null);
    }

    @Test
    @DisplayName("Count employees for non-existing department")
    void count_NoEmployeesFound() {

        when(employeeRepository.countByDepartment("UNKNOWN"))
                .thenReturn(0L);

        Long count = employeeService.count("UNKNOWN");

        assertEquals(Long.valueOf(0L), count);

        verify(employeeRepository)
                .countByDepartment("UNKNOWN");
    }
}