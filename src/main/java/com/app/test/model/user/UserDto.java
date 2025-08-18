package com.app.test.model.user;

import com.app.test.model.base.BaseGetDto;

import lombok.Getter;
import lombok.Setter;

/**
 * @Author Mario Arcomano
 */
@Getter
@Setter
public class UserDto extends BaseGetDto {
    private String name;
    private String lastname;
    private String username;
    private String email;
    private String countryCallingCode;
    private String phone;
    private String nationality;
    private String gender;
}
