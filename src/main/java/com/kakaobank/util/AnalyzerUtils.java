package com.kakaobank.util;

import org.openkoreantext.processor.KoreanPosJava;
import org.openkoreantext.processor.KoreanTokenJava;
import com.kakaobank.domain.school.SchoolTypes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import static org.openkoreantext.processor.KoreanPosJava.*;
import static com.kakaobank.domain.school.SchoolTypes.*;

public class AnalyzerUtils {
    private static final String INSTANCE_ERROR_MESSAGE = "StringUtils 클래스를 인스턴스화 할수 없습니다";

    private static final String ELEMENTARY_SCHOOL_LEVEL_PATTERN = "(초등학교|초)$";
    private static final String MIDDLE_SCHOOL_LEVEL_PATTERN = "(중학교|중)$";
    private static final String HIGH_SCHOOL_LEVEL_PATTERN = "(고등학교|고)$";
    private static final String OTHER_SCHOOL_LEVEL_PATTERN = "학교$";

    private static final String NORMAL_SCHOOL_PATTERN = "((초|중|고)|(초등|중|고등)학교)$";
    private static final String GIRL_SCHOOL_PATTERN = "((여|여자)[가-힇]*(중|고)|여자[가-힇]*(중|고등)학교)$";
    private static final String OPEN_SCHOOL_PATTERN = "((방송|방통|방송통신)(여|여자)?(중|고)|방송통신(여|여자)?(중|고등)학교)$";
    private static final String SPECIAL_SCHOOL_PATTERN = "((과|과학|외|외국어|예|예술|방송|방통|방송통신|상|상업|공|공업)(여|여자)?(중|고)|(과학|외국어|예술|방송통신|상업|공업)(여자)?(중|고등)학교)$";
    private static final String ATTACHED_SCHOOL_PATTERN = "'((교대부|사대부|부|부설|부속)(여|여자)?(초|중|고)|(교육대학|사범대학)(부|부속|부설)(여자)?(초등|중|고등)학교)$";
    private static final String BRANCH_SCHOOL_PATTERN = "분교(장)?$";
    private static final String OTHER_SCHOOL_PATTERN = "학교$";

    private static final String SPECIAL_SCIENCE_SCHOOL_PATTERN = "((과|과학)(여|여자)?(중|고)|과학(여|여자)?(중|고등)학교)$";
    private static final String SPECIAL_FOREIGN_SCHOOL_PATTERN = "((외|외국어)(여|여자)?(중|고)|외국어(여|여자)?(중|고등)학교)$";
    private static final String SPECIAL_ART_SCHOOL_PATTERN = "((예|예술)(여|여자)?(중|고)|예술(여|여자)?(중|고등)학교)$";
    private static final String SPECIAL_COMMERCIAL_SCHOOL_PATTERN = "((상|상업)(여|여자)?(중|고)|상업(여|여자)?(중|고등)학교)$";
    private static final String SPECIAL_TECHNICAL_SCHOOL_PATTERN = "((공|공업)(여|여자)?(중|고)|공업(여|여자)?(중|고등)학교)$";

    private static final String KOREAN_PATTERN = "[^가-힇\\s]*";

    private AnalyzerUtils() {
        throw new AssertionError(INSTANCE_ERROR_MESSAGE);
    }

    public static SchoolTypes getSchoolLevel(String text) {
        Matcher elementarySchoolMatcher = Pattern.compile(ELEMENTARY_SCHOOL_LEVEL_PATTERN).matcher(text);
        Matcher middleSchoolMatcher = Pattern.compile(MIDDLE_SCHOOL_LEVEL_PATTERN).matcher(text);
        Matcher highSchoolMatcher = Pattern.compile(HIGH_SCHOOL_LEVEL_PATTERN).matcher(text);
        Matcher otherSchoolMatcher = Pattern.compile(OTHER_SCHOOL_LEVEL_PATTERN).matcher(text);

        if (elementarySchoolMatcher.find()) {
            return ELEMENTARY_SCHOOL_LEVEL;
        }

        if (middleSchoolMatcher.find()) {
            return MIDDLE_SCHOOL_LEVEL;
        }

        if (highSchoolMatcher.find()) {
            return HIGH_SCHOOL_LEVEL;
        }

        if (otherSchoolMatcher.find()) {
            return OTHER_SCHOOL_LEVEL;
        }

        return UNKNOWN_SCHOOL_LEVEL;
    }


    public static List<SchoolTypes> getSchoolKinds(String text) {
        List<SchoolTypes> types = new ArrayList<>();

        Matcher normalSchoolMatcher = Pattern.compile(NORMAL_SCHOOL_PATTERN).matcher(text);
        Matcher girlSchoolMatcher = Pattern.compile(GIRL_SCHOOL_PATTERN).matcher(text);
        Matcher openSchoolMatcher = Pattern.compile(OPEN_SCHOOL_PATTERN).matcher(text);
        Matcher specialSchoolMatcher = Pattern.compile(SPECIAL_SCHOOL_PATTERN).matcher(text);
        Matcher attachedSchoolMatcher = Pattern.compile(ATTACHED_SCHOOL_PATTERN).matcher(text);
        Matcher branchSchoolMatcher = Pattern.compile(BRANCH_SCHOOL_PATTERN).matcher(text);
        Matcher otherSchoolMatcher = Pattern.compile(OTHER_SCHOOL_PATTERN).matcher(text);

        Matcher specialScienceSchoolMatcher = Pattern.compile(SPECIAL_SCIENCE_SCHOOL_PATTERN).matcher(text);
        Matcher specialForeignSchoolMatcher = Pattern.compile(SPECIAL_FOREIGN_SCHOOL_PATTERN).matcher(text);
        Matcher specialArtSchoolMatcher = Pattern.compile(SPECIAL_ART_SCHOOL_PATTERN).matcher(text);
        Matcher specialCommercialSchoolMatcher = Pattern.compile(SPECIAL_COMMERCIAL_SCHOOL_PATTERN).matcher(text);
        Matcher specialTechnicalSchoolMatcher = Pattern.compile(SPECIAL_TECHNICAL_SCHOOL_PATTERN).matcher(text);

        if (girlSchoolMatcher.find()) {
            types.add(GIRL_SCHOOL_KIND);
        }

        if (openSchoolMatcher.find()) {
            types.add(OPEN_SCHOOL_KIND);
        }

        if (specialSchoolMatcher.find()) {
            types.add(SPECIAL_SCHOOL_KIND);

            if (specialScienceSchoolMatcher.find()) {
                types.add(SPECIAL_SCIENCE_SCHOOL_KIND);
            }

            if (specialForeignSchoolMatcher.find()) {
                types.add(SPECIAL_FOREIGN_SCHOOL_KIND);
            }

            if (specialArtSchoolMatcher.find()) {
                types.add(SPECIAL_ART_SCHOOL_KIND);
            }

            if (specialCommercialSchoolMatcher.find()) {
                types.add(SPECIAL_COMMERCIAL_SCHOOL_KIND);
            }

            if (specialTechnicalSchoolMatcher.find()) {
                types.add(SPECIAL_TECHNICAL_SCHOOL_KIND);
            }
        }

        if (attachedSchoolMatcher.find()) {
            types.add(ATTACHED_SCHOOL_KIND);
        }

        if (branchSchoolMatcher.find()) {
            types.add(BRANCH_SCHOOL_KIND);
        }

        if (types.size() > 0) {
            return types;
        }

        if (normalSchoolMatcher.find()) {
            types.add(NORMAL_SCHOOL_KIND);
            return types;
        }

        if (otherSchoolMatcher.find()) {
            types.add(OTHER_SCHOOL_LEVEL);
            return types;
        }

        types.add(UNKNOWN_SCHOOL_LEVEL);

        return new ArrayList<>(types);
    }

    public static List<KoreanTokenJava> filterPos(List<KoreanTokenJava> koreanTokenJavaList) {
        List<KoreanPosJava> filteredList = Arrays.asList(Adjective, Conjunction, Punctuation, Foreign, KoreanParticle);

        List<KoreanTokenJava> data = new ArrayList<>();
        for (KoreanTokenJava token: koreanTokenJavaList) {
            if(!filteredList.contains(token.getPos())) {
                data.add(token);
            }
        }

        return new ArrayList<>(data);
    }

    public static String removeNonKorean(String text) {
        return Pattern.compile(KOREAN_PATTERN).matcher(text).replaceAll("");
    }

    public static String removeKeywords(String text) {
        text = Pattern.compile("여(중|고)$").matcher(text).replaceAll("여자\\1");
        text = Pattern.compile("과(중|고)$").matcher(text).replaceAll("과학\\1");
        text = Pattern.compile("외(중|고)$").matcher(text).replaceAll("외국어\\1");
        text = Pattern.compile("예(중|고)$").matcher(text).replaceAll("예술\\1");
        text = Pattern.compile("(방송|방통)(중|고)$").matcher(text).replaceAll("방송통신\\1");
        text = Pattern.compile("상(중|고)$").matcher(text).replaceAll("상업\\1");
        text = Pattern.compile("공(중|고)$").matcher(text).replaceAll("공업\\1");
        text = Pattern.compile("고$").matcher(text).replaceAll("고등학교");
        text = Pattern.compile("중$").matcher(text).replaceAll("중학교");
        text = Pattern.compile("초$").matcher(text).replaceAll("초등학교");

        String[] keywords = {"초등학교", "중학교", "고등학교", "학교", "분교장", "분교", "여자", "과학", "외국어", "예술", "방송통신", "상업", "공업", "병설", "부설"};
        text = Pattern.compile(String.join("|", keywords)).matcher(text).replaceAll("");
        text = removeNonKorean(text);

        return text;
    }

    public static String getPatternFromLevel(SchoolTypes level) {
        if (level == ELEMENTARY_SCHOOL_LEVEL) {
            return "초등학교";
        }

        if (level == MIDDLE_SCHOOL_LEVEL) {
            return "중학교";
        }

        if (level == HIGH_SCHOOL_LEVEL) {
            return "고등학교";
        }

        return "학교";
    }

    public static List<String> getPatternFromKinds(List<SchoolTypes> kinds) {
        List<String> patterns = new ArrayList<>();
        if (kinds.contains(GIRL_SCHOOL_KIND)) {
            patterns.add("여자");
        }

        if (kinds.contains(OPEN_SCHOOL_KIND)) {
            patterns.add("방송통신");
        }

        if (kinds.contains(SPECIAL_SCHOOL_KIND)) {
            if (kinds.contains(SPECIAL_SCIENCE_SCHOOL_KIND)) {
                patterns.add("과학");
            }

            if (kinds.contains(SPECIAL_FOREIGN_SCHOOL_KIND)) {
                patterns.add("외국어");
            }

            if (kinds.contains(SPECIAL_ART_SCHOOL_KIND)) {
                patterns.add("예술");
            }

            if (kinds.contains(SPECIAL_COMMERCIAL_SCHOOL_KIND)) {
                patterns.add("상업");
            }

            if (kinds.contains(SPECIAL_TECHNICAL_SCHOOL_KIND)) {
                patterns.add("공업");
            }
        }

        if (kinds.contains(ATTACHED_SCHOOL_KIND)) {
            patterns.add("사범대학");
            patterns.add("부속");
            patterns.add("부설");
        }

        if (kinds.contains(BRANCH_SCHOOL_KIND)) {
            patterns.add("분교");
        }

        return new ArrayList<>(patterns);
    }

    public static List<String> getLocationsFromText(String text) {
        List<String> locations = new ArrayList<>();

        // 도
        Matcher doMatcher = Pattern.compile("경기|강원|충청북도|충청남도|전라북도|전라남도|경상북도|경상남도|제주").matcher(text);
        locations = matchLocationAdd(doMatcher, locations);

        // 광역시
        doMatcher = Pattern.compile("서울|부산|대구|인천|광주|대전|울산|세종").matcher(text);
        locations = matchLocationAdd(doMatcher, locations);

        // 경기 남부
        doMatcher = Pattern.compile("성남|수원|안양|안산|용인|광명|평택|과천|오산|시흥|군포|의왕|하남|이천|안성|김포|화성|광주|여주|부천").matcher(text);
        locations = matchLocationAdd(doMatcher, locations);


        // 경기 북부
        doMatcher = Pattern.compile("고양|의정부|동두천|구리|남양주|파주|양주|포천").matcher(text);
        locations = matchLocationAdd(doMatcher, locations);

        // 강원
        doMatcher = Pattern.compile("춘천|원주|강릉|동해|태백|속초|삼척").matcher(text);
        locations = matchLocationAdd(doMatcher, locations);

        if (text.contains("충북")) {
            locations.add("충청북도");
        }

        if (text.contains("충남")) {
            locations.add("충청남도");
        }

        if (text.contains("전북")) {
            locations.add("전라북도");
        }

        if (text.contains("전남")) {
            locations.add("전라남도");
        }

        if (text.contains("경북")) {
            locations.add("경상북도");
        }

        if (text.contains("경남")) {
            locations.add("경상남도");
        }

        return new ArrayList<>(locations);
    }

    private static List<String> matchLocationAdd(Matcher matcher, List<String> list) {
        List<String> locations = new ArrayList<>(list);
        if (matcher.find()) {
            locations.add(matcher.group());
        }

        return new ArrayList<>(locations);
    }
}
