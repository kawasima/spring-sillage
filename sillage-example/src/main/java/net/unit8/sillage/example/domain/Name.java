package net.unit8.sillage.example.domain;

import com.fasterxml.jackson.annotation.JsonValue;

import java.io.Serializable;

public class Name implements Serializable {
    @JsonValue
    private final String value;

    public Name(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
    @Override
    public String toString() {
        return value;
    }
}
