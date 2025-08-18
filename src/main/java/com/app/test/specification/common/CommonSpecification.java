package com.app.test.specification.common;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.Specification;

import com.app.test.exception.BaseException;
import com.app.test.exception.model.Error;
import com.app.test.exception.model.InternalErrorCode;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class CommonSpecification<MODEL> implements Specification<MODEL> {

    public SearchCriteria criteria;


    public CommonSpecification(SearchCriteria criteria) {
        this.criteria = criteria;
    }

    @Override
    public Predicate toPredicate(Root<MODEL> root, CriteriaQuery<?> query, CriteriaBuilder builder) {

        switch (criteria.getOperation()) {
            case EQUAL_TO:
                return builder.equal(root.get(criteria.getKey()), (criteria.getValue()));
            case NOT_EQUAL_TO:
                return builder.notEqual(root.get(criteria.getKey()), criteria.getValue().toString());
            case GREATER_THAN:
                return builder.greaterThan(
                        root.get(criteria.getKey()), criteria.getValue().toString());
            case GREATER_THAN_OR_EQUAL_TO:
                return builder.greaterThanOrEqualTo(
                        root.get(criteria.getKey()), criteria.getValue().toString());
            case LESS_THAN:
                return builder.lessThan(
                        root.get(criteria.getKey()), criteria.getValue().toString());
            case LESS_THAN_OR_EQUAL_TO:
                return builder.lessThanOrEqualTo(
                        root.get(criteria.getKey()), criteria.getValue().toString());
            case LIKE:
                return builder.like(
                        root.get(criteria.getKey()), "%" + criteria.getValue() + "%");
            case BETWEEN:
                return builder.between(
                        root.get(criteria.getKey()),
                        criteria.getValue().toString(),
                        criteria.getValue2().toString());
            case IN:
                if (criteria.getValue() instanceof List<?>) {
                    List<?> values = (List<?>) criteria.getValue();
                    CriteriaBuilder.In<Object> inClause = builder.in(root.get(criteria.getKey()));
                    // add all values to the IN clause
                    for (Object value : values) {
                        inClause.value(value);
                    }
                    return inClause;
                } else {
                    Error error = new Error(InternalErrorCode.INTERNAL_SEVER_ERROR, "Criteria value for IN operation must be a list");
                    BaseException baseException = new BaseException();
                    baseException.addError(error);
                    throw baseException;
                }
            case NOT_IN:
                if (criteria.getValue() instanceof List<?>) {
                    List<?> values = (List<?>) criteria.getValue();
                    CriteriaBuilder.In<Object> inClause = builder.in(root.get(criteria.getKey()));

                    // add all values to the IN clause
                    for (Object value : values) {
                        inClause.value(value);
                    }

                    // Negation of the 'IN' clause
                    return builder.not(inClause);
                } else {
                    Error error = new Error(InternalErrorCode.INTERNAL_SEVER_ERROR, "Criteria value for NOT_IN operation must be a list");
                    BaseException baseException = new BaseException();
                    baseException.addError(error);
                    throw baseException;
                }

            case DATE_TO:
                switch (criteria.getValue()) {
                    case Instant i -> {
                        return builder.lessThanOrEqualTo(root.get(criteria.getKey()), i);
                    }
                    case LocalDateTime ldt -> {
                        return builder.lessThanOrEqualTo(root.get(criteria.getKey()), ldt);
                    }
                    case LocalDate ld -> {
                        return builder.lessThanOrEqualTo(root.get(criteria.getKey()), ld);
                    }
                    default ->
                            throw new RuntimeException("Unmanaged type passed as SearchCriteria DATE_TO" + criteria.getValue().getClass().getName());
                }
            case DATE_FROM:
                switch (criteria.getValue()) {
                    case Instant i -> {
                        return builder.greaterThanOrEqualTo(root.get(criteria.getKey()), i);
                    }
                    case LocalDateTime ldt -> {
                        return builder.greaterThanOrEqualTo(root.get(criteria.getKey()), ldt);
                    }
                    case LocalDate ld -> {
                        return builder.greaterThanOrEqualTo(root.get(criteria.getKey()), ld);
                    }
                    default ->
                            throw new RuntimeException("Unmanaged type passed as SearchCriteria DATE_FROM" + criteria.getValue().getClass().getName());
                }
            default:
                return null;
        }
    }
}
