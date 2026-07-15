package com.maksymsuvorov.taskflow.service;

import org.springframework.data.domain.Sort;

import java.util.Set;

public final class SortValidator {

    private SortValidator() {
    }

    public static void validate(Sort sort, Set<String> allowedProperties) {
        for (Sort.Order order : sort) {
            if (!allowedProperties.contains(order.getProperty())) {
                throw new IllegalArgumentException(
                        "Cannot sort by '" + order.getProperty() + "'. Allowed: " + allowedProperties + "."
                );
            }
        }
    }

}
