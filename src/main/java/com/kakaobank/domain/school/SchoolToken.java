package com.kakaobank.domain.school;

import com.kakaobank.util.AnalyzerUtils;

import java.util.List;
import java.util.regex.Pattern;

import static com.kakaobank.domain.school.SchoolTypes.*;

public class SchoolToken {

    private final List<String> locations;

    private Pattern locationPattern;

    private SchoolTypes level;


    private List<SchoolTypes> kinds;

    private String schoolName;

    public SchoolToken(List<String> rawData, List<String> locations) {
        this.locations = locations;
        if (locations != null && locations.size() > 0) {
            this.locationPattern = Pattern.compile(String.join("|", locations));
        }

        String joinedText = String.join("", rawData);
        this.level = AnalyzerUtils.getSchoolLevel(joinedText);
        this.kinds = AnalyzerUtils.getSchoolKinds(joinedText);
        this.schoolName = AnalyzerUtils.removeKeywords(joinedText);
    }

    public boolean hasLocationIn(String text) {
        if (this.locations == null || this.locations.size() == 0) {
            return false;
        }

        return this.locationPattern.matcher(text).find();

    }

    public boolean isSkip() {
        return this.schoolName.length() < 2;
    }

    public boolean isGirlKind() {
        return this.kinds.contains(GIRL_SCHOOL_KIND);
    }

    public boolean isSpecialKind() {
        return this.kinds.contains(SPECIAL_SCHOOL_KIND);
    }

    public SchoolTypes getLevel() {
        return level;
    }

    public List<SchoolTypes> getKinds() {
        return kinds;
    }

    public String getSchoolName() {
        return this.schoolName;
    }
}
