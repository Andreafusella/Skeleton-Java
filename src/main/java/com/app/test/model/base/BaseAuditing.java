package com.app.test.model.base;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

/**
 * @author Mario Arcomano
 */
@MappedSuperclass
@Getter
@Setter
public class BaseAuditing {
    @CreationTimestamp
    @Column(name = "created_date")
    protected Instant createdDate;

    @Column(name = "created_by")
    protected String createdBy;

    @UpdateTimestamp
    @Column(name = "modified_date")
    private Instant modifiedDate;

    @Column(name = "modified_by")
    protected String modifiedBy;

    @Column(name = "deleted")
    protected Boolean deleted = Boolean.FALSE;
}
