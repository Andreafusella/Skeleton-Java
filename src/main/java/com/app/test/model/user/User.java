package com.app.test.model.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;

import com.app.test.model.base.BaseEntity;

@Entity
@Table(name = "users")
@SQLRestriction("deleted=false")
@Getter
@Setter
public class User extends BaseEntity {

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "lastname", nullable = false)
    private String lastname;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "countryCallingCode", nullable = true)
    private String countryCallingCode;

    @Column(name = "phone", nullable = true, unique = true)
    private String phone;

    @Column(name = "nationality", nullable = true)
    private String nationality;

    @Column(name = "gender", nullable = true)
    private String gender;

}
