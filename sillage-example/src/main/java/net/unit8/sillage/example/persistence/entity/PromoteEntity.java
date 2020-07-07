package net.unit8.sillage.example.persistence.entity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Table(name = "promote")
public class PromoteEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne()
    private EmployeeEntity employee;

    private BigDecimal amount;

    @Column(name = "currency_unit")
    private String currencyUnit;

    @Column(name = "promoted_at")
    private Timestamp promotedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EmployeeEntity getEmployee() {
        return employee;
    }

    public void setEmployee(EmployeeEntity employee) {
        this.employee = employee;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrencyUnit() {
        return currencyUnit;
    }

    public void setCurrencyUnit(String currencyUnit) {
        this.currencyUnit = currencyUnit;
    }

    public Timestamp getPromotedAt() {
        return promotedAt;
    }

    public void setPromotedAt(Timestamp promotedAt) {
        this.promotedAt = promotedAt;
    }
}
