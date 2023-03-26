package com.kakaobank.repository;

import com.kakaobank.domain.school.School;
import com.kakaobank.repository.db.DQLService;


import java.util.*;

public class SchoolRepository {

    private final DQLService db;


    public SchoolRepository(DQLService db) {
        this.db = db;
    }

    public List<School> findBySchoolNameAndLevel(String schoolName, String levelPattern) {
        return new ArrayList<>(db.selectSchoolList(schoolName, levelPattern));
    }

//    public String findSchool(SchoolToken schoolToken) {
//        List<Pattern> matchedPatterns = new ArrayList<>();
//
//
//        String levelPattern = AnalyzerUtils.getPatternFromLevel(schoolToken.getLevel());
//        List<String> kindPatterns = AnalyzerUtils.getPatternFromKinds(schoolToken.getKinds());
//
//        if (kindPatterns.size() > 0) {
//            matchedPatterns.add(Pattern.compile(String.format("(%s)+.*%s$",
//                    Pattern.quote(String.join("|", kindPatterns)),
//                    Pattern.quote(levelPattern))));
//        }
//
//
//        List<School> schools = calculateSchoolStatistics(db.selectSchoolList(schoolToken.getSchoolName(), levelPattern), schoolToken, matchedPatterns);
//
//
//
//        if (schools.size() == 0) {
//            return null;
//        }
//
//        schools.sort(new Comparator<School>() {
//            @Override
//            public int compare(School o1, School o2) {
//                Integer o1Value = o1.getScore();
//                Integer o2Value = o2.getScore();
//                Integer o1Len = o1.getName().length();
//                Integer o2Len = o2.getName().length();
//                if (o1Value.equals(o2Value)) {
//                    return o1Len.compareTo(o2Len);
//                } else {
//                    return o2Value.compareTo(o1Value);
//                }
//            }
//        });
//
//
//
//        return schools.get(0).getName();
//    }
//
//
//    private List<School> calculateSchoolStatistics(List<School> schools, SchoolToken schoolToken, List<Pattern> matchedPatterns) {
//        List<School> list = new ArrayList<>();
//        for (School school : schools) {
//            calculateScore(schoolToken, school);
//
//            int matched = patternFindFromSchoolName(school.getName(), matchedPatterns);
//
//            if (matched != matchedPatterns.size()) continue;
//
//
//            list.add(school);
//        }
//
//        return new ArrayList<>(list);
//    }
//
//    private void calculateScore(SchoolToken schoolToken, School school) {
//        if (schoolToken.isGirlKind() && school.getGender().equals("녀")) {
//            school.plusScore();
//        }
//
//        if (!schoolToken.isGirlKind() && !school.getGender().equals("녀")) {
//            school.plusScore();
//        }
//
//
//        if (schoolToken.isSpecialKind() && Arrays.asList("특수목적고등학교", "특성화고등학교").contains(school.getProperty())) {
//            school.plusScore();
//        }
//
//        if (schoolToken.hasLocationIn(school.getName())) {
//            school.plusScore();;
//        }
//
//        if (schoolToken.hasLocationIn(school.getLocation())) {
//            school.plusScore();
//        }
//
//    }
//
//    private int patternFindFromSchoolName(String schoolName, List<Pattern> patterns) {
//        if (patterns.size() == 0) {
//            return 0;
//        }
//
//        int count = 0;
//
//        for (Pattern pattern : patterns) {
//            if (isMatch(pattern.matcher(schoolName))) {
//                count++;
//            }
//        }
//
//        return count;
//    }
//
//    private boolean isMatch(Matcher matcher) {
//        return matcher.find();
//    }
}
