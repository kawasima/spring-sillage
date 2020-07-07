package net.unit8.sillage.example.persistence;

import net.unit8.sillage.example.domain.Employee;
import net.unit8.sillage.example.persistence.entity.PromoteEntity;
import net.unit8.sillage.example.port.out.PromotePort;
import org.springframework.stereotype.Component;

import javax.money.MonetaryAmount;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Component
public class PromotePersistenceAdapter implements PromotePort {
    private final PromoteRepository promoteRepository;
    private final EmployeeMapper employeeMapper;

    public PromotePersistenceAdapter(PromoteRepository promoteRepository,
                                     EmployeeMapper employeeMapper) {
        this.promoteRepository = promoteRepository;
        this.employeeMapper = employeeMapper;
    }

    @Override
    public void promote(Employee employee, MonetaryAmount promotedSalary) {
        PromoteEntity entity = new PromoteEntity();
        entity.setEmployee(employeeMapper.toEntity(employee));
        entity.setAmount(promotedSalary.getNumber().numberValue(BigDecimal.class));
        entity.setCurrencyUnit(promotedSalary.getCurrency().getCurrencyCode());
        entity.setPromotedAt(new Timestamp(System.nanoTime()));
        promoteRepository.save(entity);
    }
}
