package net.unit8.sillage.example.domain;

import com.fasterxml.jackson.annotation.JsonValue;

import java.io.Serializable;

public class EmployeeId implements Serializable {
    @JsonValue
    private final Long value;
    public EmployeeId(Long value) {
        this.value = value;
    }

    public Long getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
