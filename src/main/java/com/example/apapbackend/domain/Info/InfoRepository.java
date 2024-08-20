package com.example.apapbackend.domain.Info;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InfoRepository extends JpaRepository<Info, Long>, JpaSpecificationExecutor<Info> {

    /**
     * 특정 기간 내 이상 상황 탐지 정보 조회
     */
    @Query("SELECT i FROM Info i WHERE i.localDateTime BETWEEN :startDate AND :endDate")
    List<Info> findByDateRange(
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );

    /**
     * 특정 기간 내 일별 이상 상황 발생 횟수 조회
     */
    @Query("SELECT FUNCTION('DATE', i.localDateTime) AS date, COUNT(i) AS count " +
           "FROM Info i " +
           "WHERE i.localDateTime BETWEEN :startDate AND :endDate " +
           "GROUP BY FUNCTION('DATE', i.localDateTime)")
    Map<LocalDateTime, Long> findDailyCountsByDateRange(@Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate);


    /**
     * 특정 기간 내 일별, 이상 상황별 발생 횟수 조회 - 횟수 내림 차순 기준
     */
    @Query("SELECT FUNCTION('DATE', i.localDateTime) AS date, i.label, COUNT(i) AS count " +
           "FROM Info i " +
           "WHERE i.localDateTime BETWEEN :startDate AND :endDate " +
           "GROUP BY FUNCTION('DATE', i.localDateTime), i.label " +
           "ORDER BY FUNCTION('DATE', i.localDateTime), COUNT(i) DESC")
    List<Object[]> findDailyLabelCountsByDateRange(
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );

    /**
     * 특정 기간 내 발생한 이상 상황의 종류와 횟수 조회 - 횟수 내림 차순 기준
     */
    @Query("SELECT i.label, COUNT(i) AS count " +
           "FROM Info i " +
           "WHERE i.localDateTime BETWEEN :startDate AND :endDate " +
           "GROUP BY i.label " +
           "ORDER BY COUNT(i) DESC")
    List<Object[]> findLabelCountsByDateRange(
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
}
