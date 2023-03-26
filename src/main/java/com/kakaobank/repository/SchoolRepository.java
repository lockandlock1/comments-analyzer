package com.kakaobank.repository;

import com.kakaobank.domain.school.School;
import com.kakaobank.domain.school.SchoolToken;
import com.kakaobank.repository.db.DQLService;
import com.kakaobank.util.AnalyzerUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SchoolRepository {

    private final DQLService db;


    public SchoolRepository(DQLService db) {
        this.db = db;
    }

    public String findSchool(SchoolToken schoolToken) {
        List<Pattern> matchedPatterns = new ArrayList<>();

        List<Pattern> unmatchedPatterns = new ArrayList<>();

        String levelPattern = AnalyzerUtils.getPatternFromLevel(schoolToken.getLevel());
        List<String> kindPatterns = AnalyzerUtils.getPatternFromKinds(schoolToken.getKinds());

        if (kindPatterns.size() > 0) {
            matchedPatterns.add(Pattern.compile(String.format("(%s)+.*%s$",
                    Pattern.quote(String.join("|", kindPatterns)),
                    Pattern.quote(levelPattern))));
        }

        // SQLite 연결 및 데이터 읽어오기
        List<School> schools = db.selectSchoolList(schoolToken.getSchoolName(), levelPattern);

        Map<String, Integer> schoolStatistics = calculateSchoolStatistics(schools, schoolToken, matchedPatterns, unmatchedPatterns);

        if (schoolStatistics.size() == 0) {
            return null;
        }

        List<String> list = new ArrayList<>(schoolStatistics.keySet());

        list.sort(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                Integer o1Value = schoolStatistics.get(o1);
                Integer o2Value = schoolStatistics.get(o2);
                Integer o1Len = o1.length();
                Integer o2Len = o2.length();
                if (o1Value.equals(o2Value)) {
                    return o1Len.compareTo(o2Len);
                } else {
                    return o2Value.compareTo(o1Value);
                }
            }
        });


        return list.get(0);
    }

    private Map<String, Integer> calculateSchoolStatistics(List<School> schools, SchoolToken schoolToken, List<Pattern> matchedPatterns, List<Pattern> unmatchedPatterns) {
        Map<String, Integer> schoolStatistics = new HashMap<>();
        for (School school : schools) {
            int score = calculateScore(schoolToken, school);

            int matched = patternFindFromSchoolName(school.getName(), matchedPatterns);
            int unmatched = patternFindFromSchoolName(school.getName(), unmatchedPatterns);

            if (matched != matchedPatterns.size() || unmatched > 0) continue;


            schoolStatistics.put(school.getName(), score);
        }

        return new HashMap<>(schoolStatistics);
    }

    private int calculateScore(SchoolToken schoolToken, School school) {
        int score = 0;
        if (schoolToken.isGirlKind() && school.getGender().equals("녀")) {
            score++;
        }

        if (!schoolToken.isGirlKind() && !school.getGender().equals("녀")) {
            score++;
        }


        if (schoolToken.isSpecialKind() && Arrays.asList("특수목적고등학교", "특성화고등학교").contains(school.getProperty())) {
            score++;
        }

        if (schoolToken.hasLocationIn(school.getName())) {
            score++;
        }

        if (schoolToken.hasLocationIn(school.getLocation())) {
            score++;
        }

        return score;
    }

    private int patternFindFromSchoolName(String schoolName, List<Pattern> patterns) {
        if (patterns.size() == 0) {
            return 0;
        }

        int count = 0;

        for (Pattern pattern : patterns) {
            if (isMatch(pattern.matcher(schoolName))) {
                count++;
            }
        }

        return count;
    }

    private boolean isMatch(Matcher matcher) {
        return matcher.find();
    }
}
