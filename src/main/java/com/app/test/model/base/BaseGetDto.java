package com.app.test.model.base;

import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Mario Arcomano
 */
@MappedSuperclass
@Getter
@Setter
public class BaseGetDto extends BaseAuditing{
    protected Integer id;
}
