package org.application.spring.ddd.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity(name = Customer.TABLE_NAME)
@Table(name = Customer.TABLE_NAME)
public class Customer extends AppEntity {

    @Transient
    public static final String TABLE_NAME = "Customer";

    @Column(name = "firstName", length = 100)
    private String firstName;
    @Column(name = "lastName", length = 100)
    private String lastName;
    @Column(name = "phoneNumber", length = 15, unique = true)
    private String phoneNumber;

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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
