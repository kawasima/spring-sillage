package net.unit8.sillage.example.persistence;

import net.unit8.sillage.example.domain.EmailAddress;
import net.unit8.sillage.example.domain.Employee;
import net.unit8.sillage.example.domain.EmployeeId;
import net.unit8.sillage.example.persistence.entity.EmployeeEntity;
import net.unit8.sillage.example.port.out.LoadEmployeePort;
import net.unit8.sillage.example.port.out.UpdateEmployeeStatePort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
public class EmployeePersistenceAdapter implements LoadEmployeePort, UpdateEmployeeStatePort {
    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;

    public EmployeePersistenceAdapter(EmployeeRepository employeeRepository,
                                      EmployeeMapper employeeMapper) {
        this.employeeRepository = employeeRepository;
        this.employeeMapper = employeeMapper;
    }


    private void addTxId(EmployeeEntity entity) {
        entity.setVersion(UUID.randomUUID().toString());
    }

    @Override
    public Employee loadEmployee(EmployeeId employeeId) {
        return employeeRepository.findById(employeeId.getValue())
                .map(employeeMapper::fromEntity)
                .orElse(null);
    }

    @Override
    public List<Employee> loadAllEmployees() {
        return StreamSupport.stream(employeeRepository.findAll().spliterator(), false)
                .map(employeeMapper::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public long countByEmail(EmailAddress emailAddress) {
        return employeeRepository.countByEmail(emailAddress.getSimpleAddress());
    }

    @Override
    public void updateEmployee(Employee employee) {
        EmployeeEntity entity = employeeMapper.toEntity(employee);
        addTxId(entity);
        employeeRepository.save(entity);
    }

    @Override
    public void deleteEmployee(Employee employee) {
        employeeRepository.delete(employeeMapper.toEntity(employee));
    }
}
