package net.unit8.sillage.example.domain;

import com.fasterxml.jackson.annotation.JsonValue;

import java.io.Serializable;

public class EmailAddress implements Serializable {
    private final String user;
    private final String domain;

    public EmailAddress(String value) {
        String[] tokens = value.split("@");
        if (tokens.length != 2) {
            throw new IllegalArgumentException("Invalid value: " + value);
        }
        user = tokens[0];
        domain = tokens[1];
    }

    @JsonValue
    public String getSimpleAddress() {
        return toString();
    }

    @Override
    public String toString() {
        return user + "@" + domain;
    }
}
