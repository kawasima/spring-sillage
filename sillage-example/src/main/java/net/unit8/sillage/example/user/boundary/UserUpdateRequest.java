package net.unit8.sillage.example.user.boundary;

import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;

public class UserUpdateRequest {
    @Email
    private final String email;
    @Length(max = 255)
    private final String lastName;
    @Length(max = 255)
    private final String firstName;

    public UserUpdateRequest(String email, String lastName, String firstName) {
        this.email = email;
        this.lastName = lastName;
        this.firstName = firstName;
    }

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
