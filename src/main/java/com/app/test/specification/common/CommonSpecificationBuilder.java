package com.app.test.specification.common;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class CommonSpecificationBuilder<MODEL> {

    public List<CommonSpecification<MODEL>> specificationList = new ArrayList<>();

    public CommonSpecificationBuilder() {
    }

    public CommonSpecificationBuilder<MODEL> with(CommonSpecification<MODEL> specification) {
        specificationList.add(specification);
        return this;
    }

    public CommonSpecificationBuilder<MODEL> withFieldEqual(String fieldName, Object value) {
        SearchCriteria criteria = new SearchCriteria(fieldName, SearchCriteriaType.EQUAL_TO, value, false);
        specificationList.add(new CommonSpecification<>(criteria));

        return this;
    }
    public Specification<MODEL> build() {

        if (specificationList.isEmpty()) {
            return null;
        }

        Specification<MODEL> result = null;

        for (CommonSpecification<MODEL> spec : specificationList) {
            if (spec.getCriteria().getValue() == null) {
                continue;
            }
            result = spec.getCriteria().isOrPredicate() ? Specification.where(result).or(spec) : Specification.where(result).and(spec);
        }

        return result;
    }


    /**
     * @param specification
     * @return CommonSpecificationBuilder
     * @description add specification by passing CommonSpecification object
     */
    public CommonSpecificationBuilder where(CommonSpecification<MODEL> specification) {
        specificationList.add(specification);
        return this;
    }

    /**
     * @param criteria
     * @return CommonSpecificationBuilder
     * @description add specification by passing SearchCriteria object
     */
    public CommonSpecificationBuilder where(SearchCriteria criteria) {
        specificationList.add(new CommonSpecification<MODEL>(criteria));
        return this;
    }

    /**
     * @param key
     * @param searchCriteriaType
     * @param value
     * @param isOrPredicate
     * @return CommonSpecificationBuilder
     * @description add specification by passing key, SearchCriteriaType, value and isOrPredicate
     */
    public CommonSpecificationBuilder where(String key, SearchCriteriaType searchCriteriaType, Object value, boolean isOrPredicate) {
        specificationList.add(new CommonSpecification<MODEL>(new SearchCriteria(key, searchCriteriaType, value, isOrPredicate)));
        return this;
    }

    //-----------------------------------------------------------------------------------------------------

    // Below are the overloaded methods for the most common search criteria types

    public CommonSpecificationBuilder whereEqualTo(String key, Object value, boolean isOrPredicate) {
        return where(key, SearchCriteriaType.EQUAL_TO, value, isOrPredicate);
    }

    public CommonSpecificationBuilder like(String key, Object value, boolean isOrPredicate) {
        return where(key, SearchCriteriaType.LIKE, value, isOrPredicate);
    }

    public CommonSpecificationBuilder notEqualTo(String key, Object value, boolean isOrPredicate) {
        return where(key, SearchCriteriaType.NOT_EQUAL_TO, value, isOrPredicate);
    }

    public CommonSpecificationBuilder greaterThan(String key, Object value, boolean isOrPredicate) {
        return where(key, SearchCriteriaType.GREATER_THAN, value, isOrPredicate);
    }

    public CommonSpecificationBuilder greaterThanOrEqualTo(String key, Object value, boolean isOrPredicate) {
        return where(key, SearchCriteriaType.GREATER_THAN_OR_EQUAL_TO, value, isOrPredicate);
    }

    public CommonSpecificationBuilder lessThan(String key, Object value) {
        return where(key, SearchCriteriaType.LESS_THAN, value, false);
    }

    public CommonSpecificationBuilder lessThanOrEqualTo(String key, Object value) {
        return where(key, SearchCriteriaType.LESS_THAN_OR_EQUAL_TO, value, false);
    }

    public CommonSpecificationBuilder between(String key, Integer minValue, Integer maxValue, boolean isOrPredicate) {
        SearchCriteria searchCriteria = new SearchCriteria(key, SearchCriteriaType.BETWEEN, minValue, maxValue, isOrPredicate);
        return where(searchCriteria);
    }

    public CommonSpecificationBuilder in(String key, List<Object> values, boolean isOrPredicate) {
        return where(key, SearchCriteriaType.IN, values, isOrPredicate);
    }

    public CommonSpecificationBuilder notIn(String key, List<Object> values, boolean isOrPredicate) {
        return where(key, SearchCriteriaType.NOT_IN, values, isOrPredicate);
    }

    public CommonSpecificationBuilder beforeDate(String key, LocalDate value) {
        return where(key, SearchCriteriaType.DATE_TO, value, false);
    }

    public CommonSpecificationBuilder afterDate(String key, LocalDate value) {
        return where(key, SearchCriteriaType.DATE_FROM, value, false);
    }
}
