package com.kakaobank.domain.school;


import lombok.Builder;
import lombok.Getter;

@Getter
public class School {
    private final String location;
    private final String name;
    private final String property;
    private final String gender;

    private int score;

    @Builder
    public School(String location, String name, String property, String gender) {
        this.location = location;
        this.name = name;
        this.property = property;
        this.gender = gender;
    }

    public void plusScore() {
        this.score++;
    }

    public int getScore() {
        return score;
    }
}
