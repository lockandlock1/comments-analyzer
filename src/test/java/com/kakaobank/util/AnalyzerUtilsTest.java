package util;

import com.kakaobank.util.AnalyzerUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import com.kakaobank.domain.school.SchoolTypes;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class AnalyzerUtilsTest {

    @ParameterizedTest
    @CsvSource(value = {"당현초:ELEMENTARY_SCHOOL", "당현초등학교:ELEMENTARY_SCHOOL", "당현:UNKNOWN", "노원중:MIDDLE_SCHOOL", "노원중학교:MIDDLE_SCHOOL", "서라벌고:HIGH_SCHOOL", "서라벌고등학교:HIGH_SCHOOL"}, delimiter = ':')
    void 학교레벨_정규식_테스트(String text, String expected) {
        assertThat(AnalyzerUtils.getSchoolLevel(text).getLabel()).isEqualTo(expected);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "당현초", "당현초등학교",
            "상명중", "상명중학교",
            "상명고", "상명고등학교", })

    void 순수_초_중_고_테스트(String text) {
        List<SchoolTypes> result =  AnalyzerUtils.getSchoolKinds(text);
        assertThat(result).contains(SchoolTypes.NORMAL_SCHOOL_KIND);
        assertThat(result.size()).isEqualTo(1);
    }

    @ParameterizedTest
    @ValueSource(strings = {"상명여중", "상명여자중", "상명여자중학교",
            "상명여고", "상명여자고", "상명여자고등학교", })

    void 순수_여중_여고_테스트(String text) {
        List<SchoolTypes> result =  AnalyzerUtils.getSchoolKinds(text);
        assertThat(result).contains(SchoolTypes.GIRL_SCHOOL_KIND);
        assertThat(result.size()).isEqualTo(1);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "서울방송고", "서울방송중",  "서울방송여고", "서울방송여중", "서울방송여자고", "서울방송여자중",
            "서울방통고", "서울방통중",  "서울방통여고", "서울방통여중", "서울방통여자고", "서울방통여자중",
            "서울방송통신고", "서울방송통신중",  "서울방송통신여고", "서울방송통신여중", "서울방송통신여자고", "서울방송통신여자중",
            "서울방송통신고등학교", "서울방송통신중학교",  "서울방송통신여자고등학교", "서울방송통신여자중학교",

    })
    void 방송_중_고_테스트(String text) {
        List<SchoolTypes> result =  AnalyzerUtils.getSchoolKinds(text);
        assertThat(result).contains(SchoolTypes.OPEN_SCHOOL_KIND, SchoolTypes.SPECIAL_SCHOOL_KIND);

    }

    @ParameterizedTest
    @ValueSource(strings = {
            "서울과학고", "서울과고", "서울과학고등학교"
    })
    void 과학_학교_테스트(String text) {
        List<SchoolTypes> result =  AnalyzerUtils.getSchoolKinds(text);
        assertThat(result).contains(SchoolTypes.SPECIAL_SCIENCE_SCHOOL_KIND, SchoolTypes.SPECIAL_SCHOOL_KIND);

    }

    @ParameterizedTest
    @ValueSource(strings = {
            "서울외국어고", "서울외고", "서울외국어고등학교"
    })
    void 외국어_학교_테스트(String text) {
        List<SchoolTypes> result =  AnalyzerUtils.getSchoolKinds(text);
        assertThat(result).contains(SchoolTypes.SPECIAL_SCIENCE_SCHOOL_KIND, SchoolTypes.SPECIAL_SCHOOL_KIND);

    }

    @ParameterizedTest
    @ValueSource(strings = {
            "서울사대부중", "서울사대부고", "서울대학교사범대학부설고", "서울대학교사범대학부설고등학교",
            "경인교육대학교부설초등학교",
            "공주교육대학교부설초등학교", "공주교대부설초", "공주교대부초"
    })
    void 부설_학교_테스트(String text) {
        List<SchoolTypes> result =  AnalyzerUtils.getSchoolKinds(text);
        assertThat(result).contains(SchoolTypes.ATTACHED_SCHOOL_KIND);

    }

    @ParameterizedTest
    @CsvSource(value = {"asdf한d국d어dd:한국어", "%^사%0@과:사과"}, delimiter = ':')
    void removeNonKorean(String text, String expected) {
        assertThat(AnalyzerUtils.removeNonKorean(text)).isEqualTo(expected);
    }

    @ParameterizedTest
    @CsvSource(value = {"서라벌고:서라벌", "서라벌고등학교:서라벌", "민족사관고:민족사관", "서울과고:서울"}, delimiter = ':')
    void removeKeywords(String text, String expected) {
       assertThat(AnalyzerUtils.removeKeywords(text)).isEqualTo(expected);
    }

    @Test
    void getPatternFromLevel() {
        assertThat(AnalyzerUtils.getPatternFromLevel(SchoolTypes.HIGH_SCHOOL_LEVEL)).isEqualTo("고등학교");
        assertThat(AnalyzerUtils.getPatternFromLevel(SchoolTypes.ELEMENTARY_SCHOOL_LEVEL)).isEqualTo("초등학교");
        assertThat(AnalyzerUtils.getPatternFromLevel(SchoolTypes.MIDDLE_SCHOOL_LEVEL)).isEqualTo("중학교");
        assertThat(AnalyzerUtils.getPatternFromLevel(SchoolTypes.OTHER_SCHOOL_LEVEL)).isEqualTo("학교");
    }


    @ParameterizedTest
    @CsvSource(value = {"경기도:경기", "서울시 공무원:서울", "전북 고창에 살고있는 A씨:전라북도", "경북 문겸 점촌에 살고 있습니다.:경상북도"}, delimiter = ':')
    void getLocationsFromText(String text, String expected) {
        assertThat(AnalyzerUtils.getLocationsFromText(text).get(0)).isEqualTo(expected);
//        assertThat(AnalyzerUtils.getLocationsFromText(text).size()).isEqualTo(1);
    }
}