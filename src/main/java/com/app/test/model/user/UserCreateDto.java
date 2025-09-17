package com.app.test.model.user;

import com.app.test.model.base.BaseCreateDto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserCreateDto extends BaseCreateDto {

    private String name;
    private String lastname;
    private String username;
    private String email;
    private String countryCallingCode;
    private String phone;
    private String nationality;
    private String gender;

    public UserCreateDto() {
        super();
    }
}
