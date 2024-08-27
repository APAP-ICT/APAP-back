package com.example.apapbackend.domain.Info;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.springframework.data.jpa.domain.Specification;

public class InfoSpecifications {

    public static Specification<Info> hasStartDate(LocalDate startDate) {
        return (Root<Info> root, CriteriaQuery<?> query, CriteriaBuilder cb) ->
            startDate == null ? cb.conjunction() : cb.greaterThanOrEqualTo(root.get("startDate"), startDate.atStartOfDay());
    }

    public static Specification<Info> hasEndDate(LocalDate endDate) {
        return (Root<Info> root, CriteriaQuery<?> query, CriteriaBuilder cb) ->
            endDate == null ? cb.conjunction() : cb.lessThanOrEqualTo(root.get("endDate"), endDate.atTime(23, 59, 59, 999_999_999));
    }

    public static Specification<Info> hasCameraName(String cameraName) {
        return (Root<Info> root, CriteriaQuery<?> query, CriteriaBuilder cb) ->
            cameraName == null ? cb.conjunction() : cb.equal(root.get("cameraName"), cameraName);
    }

    public static Specification<Info> hasLabel(String label) {
        return (Root<Info> root, CriteriaQuery<?> query, CriteriaBuilder cb) ->
            label == null ? cb.conjunction() : cb.equal(root.get("label"), label);
    }
}
