package com.kakaobank.domain.school;


import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class School {
    private final String location;
    private final String name;
    private final String property;
    private final String gender;

    public School(String location, String name, String property, String gender) {
        this.location = location;
        this.name = name;
        this.property = property;
        this.gender = gender;
    }
}
