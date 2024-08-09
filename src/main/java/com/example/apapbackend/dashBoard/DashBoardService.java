package com.example.apapbackend.dashBoard;

import static com.example.apapbackend.dashBoard.dto.DashBoardTopResponse.*;

import com.example.apapbackend.Info.Info;
import com.example.apapbackend.Info.InfoRepository;
import com.example.apapbackend.dashBoard.dto.DashBoardBottomResponse;
import com.example.apapbackend.dashBoard.dto.DashBoardTopResponse;
import com.example.apapbackend.dashBoard.dto.DashBoardTopResponse.ChangeType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.temporal.WeekFields;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DashBoardService {

    private final InfoRepository infoRepository;

    public DashBoardTopResponse getTopInfo() {
        Situation mostFrequentLabel = getMostFrequentLabel(); // 지난 주 대비, 가장 많이 발생한 이상 상황
        Camera mostFrequentCamera = getMostFrequentCamera(); // 지난 달 대비, (이상 상황이) 가장 많이 발생한 카메라
        SituationCount situationCount = getSituationCount(); // 지난 주 대비, 이상 상황 발생 횟수 평균

        return new DashBoardTopResponse(mostFrequentLabel, mostFrequentCamera, situationCount);
    }

    @Transactional(readOnly = true)
    public Map<LocalDate, List<Map.Entry<String, Long>>> getTopLabelsForLast3Days() {
        // 최근 3일 계산
        LocalDate now = LocalDate.now();
        LocalDate threeDaysAgo = now.minusDays(2); // 오늘 포함하여 최근 3일

        // 날짜 범위 설정
        LocalDateTime startDate = getStartOfDay(threeDaysAgo);
        LocalDateTime endDate = getEndOfDay(now);

        // 데이터 조회
        List<Object[]> results = infoRepository.findDailyLabelCountsByDateRange(startDate, endDate);

        // 결과를 날짜별로 그룹화
        Map<LocalDate, List<Map.Entry<String, Long>>> dailyTopLabels = new HashMap<>();

        for (Object[] result : results) {
            LocalDateTime dateTime = (LocalDateTime) result[0];
            String label = (String) result[1];
            Long count = ((Number) result[2]).longValue();

            LocalDate date = dateTime.toLocalDate();
            dailyTopLabels.putIfAbsent(date, new ArrayList<>());
            dailyTopLabels.get(date).add(new AbstractMap.SimpleEntry<>(label, count));
        }

        // 각 날짜별로 가장 많이 발생한 이상 상황 4개 선택
        Map<LocalDate, List<Map.Entry<String, Long>>> topLabelsPerDay = dailyTopLabels.entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> entry.getValue().stream()
                    .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                    .limit(4)
                    .collect(Collectors.toList())
            ));

        return topLabelsPerDay;
    }

    @Transactional(readOnly = true)
    public Map<String, Long> getTopThreeLabelsForLast3Days() {
        // 최근 3일 계산
        LocalDate now = LocalDate.now();
        LocalDate threeDaysAgo = now.minusDays(2); // 오늘 포함하여 최근 3일

        // 날짜 범위 설정
        LocalDateTime startDate = getStartOfDay(threeDaysAgo);
        LocalDateTime endDate = getEndOfDay(now);

        // 데이터 조회
        List<Object[]> results = infoRepository.findLabelCountsForLast3Days(startDate, endDate);

        // 결과를 이상 상황별로 집계
        Map<String, Long> labelCounts = new HashMap<>();
        for (Object[] result : results) {
            String label = (String) result[0];
            Long count = ((Number) result[1]).longValue();
            labelCounts.put(label, count);
        }

        // 발생 횟수를 기준으로 상위 3개 선택
        return labelCounts.entrySet().stream()
            .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
            .limit(3)
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                Map.Entry::getValue,
                (e1, e2) -> e1, // 충돌 처리
                LinkedHashMap::new // 유지 순서
            ));
    }

    /**
     * 지난 주 대비, 가장 많이 발생한 이상 상황
     */
    private DashBoardTopResponse.Situation getMostFrequentLabel() {
        // 현재 날짜
        LocalDate now = LocalDate.now();

        // 현재 주차 및 지난주 주차 계산
        int currentYear = now.getYear();
        int currentWeek = now.get(WeekFields.ISO.weekOfYear());
        int lastWeek = currentWeek - 1;

        // 현재 주차 및 지난주 주차의 시작일과 종료일
        LocalDateTime currentWeekStart = getStartOfWeek(currentYear, currentWeek);
        LocalDateTime currentWeekEnd = getEndOfWeek(currentYear, currentWeek);
        LocalDateTime lastWeekStart = getStartOfWeek(currentYear, lastWeek);
        LocalDateTime lastWeekEnd = getEndOfWeek(currentYear, lastWeek);

        // 현재 주차 및 지난주 주차의 정보 조회
        List<Info> currentWeekInfos = infoRepository.findByDateRange(currentWeekStart, currentWeekEnd);
        List<Info> lastWeekInfos = infoRepository.findByDateRange(lastWeekStart, lastWeekEnd);

        // 각 주차에서의 이상 상황 발생 횟수 계산
        Map<String, Long> currentWeekCounts = currentWeekInfos.stream()
            .collect(Collectors.groupingBy(Info::getLabel, Collectors.counting()));
        Map<String, Long> lastWeekCounts = lastWeekInfos.stream()
            .collect(Collectors.groupingBy(Info::getLabel, Collectors.counting()));

        // 현재 주차에서 가장 많이 발생한 이상 상황 찾기
        String mostFrequentLabel = currentWeekCounts.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse("No data");

        // 현재 주차 및 지난주 주차에서 (위에서 찾은) 가장 많이 발생한 이상 상황의 횟수
        long currentCount = currentWeekCounts.getOrDefault(mostFrequentLabel, 0L);
        long lastCount = lastWeekCounts.getOrDefault(mostFrequentLabel, 0L);

        // 지난 주 대비 증감 퍼센트 계산
        double percentageChange = (lastCount == 0) ? 100.0 : ((double) (currentCount - lastCount) / lastCount) * 100;
        ChangeType changeType =
            (percentageChange > 0) ? ChangeType.INCREASE
                : (percentageChange < 0) ? ChangeType.DECREASE
                    : ChangeType.NO_CHANGE;

        // 결과 반환
        return new Situation(mostFrequentLabel, (int) percentageChange, changeType);
    }

    /**
     * 지난 달 대비, 이상 상황이 가장 많이 발생한 구역(카메라)
     */
    private DashBoardTopResponse.Camera getMostFrequentCamera() {
        // 현재 날짜 및 직전 달 계산
        LocalDate now = LocalDate.now();
        int currentYear = now.getYear();
        Month currentMonth = now.getMonth();
        Month lastMonth = currentMonth.minus(1);

        // 현재 달 및 직전 달의 시작일과 종료일
        LocalDateTime currentMonthStart = getStartOfMonth(currentYear, currentMonth);
        LocalDateTime currentMonthEnd = getEndOfMonth(currentYear, currentMonth);
        LocalDateTime lastMonthStart = getStartOfMonth(currentYear, lastMonth);
        LocalDateTime lastMonthEnd = getEndOfMonth(currentYear, lastMonth);

        // 현재 달 및 직전 달의 정보 조회
        List<Info> currentMonthInfos = infoRepository.findByDateRange(currentMonthStart, currentMonthEnd);
        List<Info> lastMonthInfos = infoRepository.findByDateRange(lastMonthStart, lastMonthEnd);

        // 현재 달과 직전 달에서의 카메라별 이상 상황 발생 횟수 계산
        Map<String, Long> currentMonthCameraCounts = currentMonthInfos.stream()
            .collect(Collectors.groupingBy(Info::getCameraName, Collectors.counting()));
        Map<String, Long> lastMonthCameraCounts = lastMonthInfos.stream()
            .collect(Collectors.groupingBy(Info::getCameraName, Collectors.counting()));

        // 현재 달에서 가장 많이 발생한 카메라 찾기
        String mostFrequentCamera = currentMonthCameraCounts.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse("No data");

        // 해당 카메라의 현재 달 및 직전 달의 이상 상황 발생 횟수
        long currentCount = currentMonthCameraCounts.getOrDefault(mostFrequentCamera, 0L);
        long lastCount = lastMonthCameraCounts.getOrDefault(mostFrequentCamera, 0L);

        // 증감 퍼센트 계산
        double percentageChange = (lastCount == 0) ? 100.0 : ((double) (currentCount - lastCount) / lastCount) * 100;
        ChangeType changeType = (percentageChange > 0) ? ChangeType.INCREASE : (percentageChange < 0) ? ChangeType.DECREASE : ChangeType.NO_CHANGE;

        // 결과 반환
        return new Camera(mostFrequentCamera, (int) percentageChange, changeType);
    }

    /**
     * 지난 주 대비, 이상 상황 발생 횟수 일 평균
     */
    private DashBoardTopResponse.SituationCount getSituationCount() {
        // 현재 날짜
        LocalDate now = LocalDate.now();

        // 현재 주차 및 지난주 주차 계산
        int currentYear = now.getYear();
        int currentWeek = now.get(WeekFields.ISO.weekOfYear());
        int lastWeek = currentWeek - 1;

        // 현재 주차 및 지난주 주차의 시작일과 종료일
        LocalDateTime currentWeekStart = getStartOfWeek(currentYear, currentWeek);
        LocalDateTime currentWeekEnd = getEndOfWeek(currentYear, currentWeek);
        LocalDateTime lastWeekStart = getStartOfWeek(currentYear, lastWeek);
        LocalDateTime lastWeekEnd = getEndOfWeek(currentYear, lastWeek);

        // 현재 주차 및 지난주 주차의 일별 이상 상황 발생 횟수 조회
        Map<LocalDateTime, Long> currentWeekDailyCounts = infoRepository.findDailyCountsByDateRange(currentWeekStart, currentWeekEnd);
        Map<LocalDateTime, Long> lastWeekDailyCounts = infoRepository.findDailyCountsByDateRange(lastWeekStart, lastWeekEnd);

        // 현재 주차와 지난주 주차의 일별 발생 횟수 평균 계산
        Map<LocalDate, Long> currentWeekCounts = getDailyCounts(currentWeekDailyCounts);
        Map<LocalDate, Long> lastWeekCounts = getDailyCounts(lastWeekDailyCounts);

        double currentWeekAverage = currentWeekCounts.values().stream().mapToLong(Long::longValue).average().orElse(0);
        double lastWeekAverage = lastWeekCounts.values().stream().mapToLong(Long::longValue).average().orElse(0);

        // 증감율 계산
        double percentageChange = (lastWeekAverage == 0) ? 100.0 : ((currentWeekAverage - lastWeekAverage) / lastWeekAverage) * 100;
        ChangeType changeType = (percentageChange > 0) ? ChangeType.INCREASE : (percentageChange < 0) ? ChangeType.DECREASE : ChangeType.NO_CHANGE;

        // 결과물 반환
        return new SituationCount((int) currentWeekAverage, (int) percentageChange, changeType);
    }

    /**
     * 유틸 메서드들
     */
    public Map<LocalDate, Long> getDailyCounts(Map<LocalDateTime, Long> data) {
        return data.entrySet().stream()
            .collect(Collectors.groupingBy(entry -> entry.getKey().toLocalDate(),
                Collectors.summingLong(Map.Entry::getValue)));
    }
    private LocalDateTime getStartOfDay(LocalDate date) {
        return LocalDateTime.of(date, LocalTime.MIN);
    }

    private LocalDateTime getEndOfDay(LocalDate date) {
        return LocalDateTime.of(date, LocalTime.MAX);
    }
    private LocalDateTime getStartOfWeek(int year, int week) {
        LocalDate startOfWeek = LocalDate.of(year, 1, 1)
            .with(WeekFields.ISO.weekOfYear(), week)
            .with(WeekFields.ISO.weekBasedYear(), year)
            .with(java.time.DayOfWeek.MONDAY);
        return startOfWeek.atStartOfDay();
    }
    private LocalDateTime getEndOfWeek(int year, int week) {
        LocalDateTime startOfWeek = getStartOfWeek(year, week);
        return startOfWeek.plusDays(6).with(LocalTime.MAX);
    }
    private LocalDateTime getStartOfMonth(int year, Month month) {
        return LocalDateTime.of(year, month, 1, 0, 0);
    }
    private LocalDateTime getEndOfMonth(int year, Month month) {
        LocalDate lastDayOfMonth = LocalDate.of(year, month, month.length(LocalDate.now().isLeapYear()));
        return LocalDateTime.of(lastDayOfMonth, LocalTime.MAX);
    }

    public DashBoardBottomResponse getBottomInfo() {
        Map<LocalDate, List<Entry<String, Long>>> topLabelsForLast3Days = getTopLabelsForLast3Days();
        Map<String, Long> topThreeLabelsForLast3Days = getTopThreeLabelsForLast3Days();

        return new DashBoardBottomResponse(topLabelsForLast3Days, topThreeLabelsForLast3Days);
    }
}
