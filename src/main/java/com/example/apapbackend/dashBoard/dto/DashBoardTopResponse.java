package com.example.apapbackend.dashBoard.dto;

public record DashBoardTopResponse(
    Situation mostFrequentSituation, // 가장 많이 발생한 이상 상황
    Camera mostFrequentCamera, // (이상 상황이) 가장 많이 발생한 카메라
    SituationCount averageSituationCount // 직전 주 대비 이상 상황 일 평균 발생 횟수
) {

    public record Situation(
        String name, // 이상 상황 라벨
        int changePercentage, // 증감율
        ChangeType changeType // 증감여부
    ) {}
    public record Camera(
        String name, // 카메라 이름
        int changePercentage, // 증감율
        ChangeType changeType // 증감여부
    ) {}
    public record SituationCount(
        int count, // 직전 주 대비 일 평균 이상 상황 발생 횟수
        int changePercentage, // 증감율
        ChangeType changeType // 증감여부
    ) {}

    public enum ChangeType {
        INCREASE,    // 증가
        DECREASE,    // 감소
        NO_CHANGE    // 그대로
    }
}
