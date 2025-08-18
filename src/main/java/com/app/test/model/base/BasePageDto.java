package com.app.test.model.base;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Mario Arcomano
 */
@Getter
@Setter
public class BasePageDto<DTO> {
    public List<DTO> content = new ArrayList<>();
    private int pageElements;
    private long totalCount;

    public int getPageElements() {
        return content.size();
    }
}
