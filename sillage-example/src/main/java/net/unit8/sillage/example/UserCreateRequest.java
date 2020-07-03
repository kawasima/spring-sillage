package net.unit8.sillage.example;

import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import java.io.Serializable;

public class UserCreateRequest implements Serializable {
    public UserCreateRequest(@Email String email, @Length(max = 255) String lastName, @Length(max = 255) String firstName) {
        this.email = email;
        this.lastName = lastName;
        this.firstName = firstName;
    }

    @Email
    private String email;
    @Length(max = 255)
    private String lastName;
    @Length(max = 255)
    private String firstName;

    public String getEmail() {
        return email;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFirstName() {
        return firstName;
    }
}
