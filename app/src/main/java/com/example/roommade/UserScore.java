package com.example.roommade;

public class UserScore {
    String userId;
    String name;
    double point;
    double distanceScore;
    double totalScore;
    int schedule;

    // 기본 생성자
    public UserScore() {
        // 기본값 설정 가능
    }

    // 생성자 오버로딩
    public UserScore(String userId, String name, double point, double distanceScore, double totalScore) {
        this.userId = userId;
        this.name = name;
        this.point = point;
        this.distanceScore = distanceScore;
        this.totalScore = totalScore;
    }

    public UserScore(String userId, String name, double point, double distanceScore, double totalScore, int schedule) {
        this.userId = userId;
        this.name = name;
        this.point = point;
        this.distanceScore = distanceScore;
        this.totalScore = totalScore;
        this.schedule = schedule;
    }

    // getter 및 setter
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPoint() {
        return point;
    }

    public void setPoint(double point) {
        this.point = point;
    }

    public double getDistanceScore() {
        return distanceScore;
    }

    public void setDistanceScore(double distanceScore) {
        this.distanceScore = distanceScore;
    }

    public double getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(double totalScore) {
        this.totalScore = totalScore;
    }

    public int getSchedule() {
        return schedule;
    }

    public void setSchedule(int schedule) {
        this.schedule = schedule;
    }
}
