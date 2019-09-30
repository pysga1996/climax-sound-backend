package com.lambda.model.form;

import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.*;
import java.util.Date;

public class UserForm {
    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9]+([a-zA-Z0-9]([_\\- ])[a-zA-Z0-9])*[a-zA-Z0-9]+${8,}")
    private String username;

    @NotBlank
    @Pattern(regexp = "^(?=.*[\\d])(?=.*[a-z])(?=.*[A-Z]).{8,20}$")
    private String password;

    @NotBlank
    @Size(min = 2, max = 20)
    private String firstName;

    @Size(min = 2, max = 20)
    private String lastName;

    @NotNull
    private Boolean gender;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date birthDate;

    @Pattern(regexp = "^(\\(?\\+?[0-9]*\\)?)?[0-9_\\- ()]*${10,13}")
    private String phoneNumber;

    @Email
    private String email;

    public UserForm() {
    }

    public UserForm(String firstName, String lastName, String username, String password, String phoneNumber, Boolean gender, Date birthDate, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.gender = gender;
        this.birthDate = birthDate;
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Boolean getGender() {
        return gender;
    }

    public void setGender(Boolean gender) {
        this.gender = gender;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
