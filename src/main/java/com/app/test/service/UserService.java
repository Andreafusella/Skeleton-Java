package com.app.test.service;

import org.springframework.stereotype.Service;

import com.app.test.model.user.*;
import com.app.test.repository.UserRepository;


@Service
public class UserService extends AbstractService<User, UserDto, UserCreateDto, UserUpdateDto, UserPageDto, Integer> {

    public enum Fields {
        name(String.class),
        lastname(String.class),
        username(String.class),
        email(String.class),
        countryCallingCode(String.class),
        phone(String.class),
        nationality(String.class),
        gender(String.class);

        private final Class valueClass;

        Fields(Class valueClass) {
            this.valueClass = valueClass;
        }

        public Class getValueClass() {
            return valueClass;
        }
    }

    public UserService(UserRepository userRepository) {
        this.repository = userRepository;
    }

    @Override
    protected void validateCreateDto(UserCreateDto userCreateDto) {

    }

    @Override
    protected void validateUpdateDto(UserUpdateDto userUpdateDto, User existing) {

    }

    @Override
    protected void validateDelete(Integer id) {

    }
}
