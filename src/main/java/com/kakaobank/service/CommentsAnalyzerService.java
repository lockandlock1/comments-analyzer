package com.kakaobank.service;

import com.kakaobank.domain.school.School;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.kakaobank.repository.db.DQLService;
import com.opencsv.exceptions.CsvValidationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openkoreantext.processor.KoreanTokenJava;
import org.openkoreantext.processor.OpenKoreanTextProcessorJava;
import org.openkoreantext.processor.tokenizer.KoreanTokenizer;
import scala.collection.Seq;
import com.kakaobank.repository.SchoolRepository;
import com.kakaobank.domain.school.SchoolToken;
import com.kakaobank.domain.school.SchoolTypes;
import com.kakaobank.util.AnalyzerUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.kakaobank.domain.school.SchoolTypes.*;


public class CommentsAnalyzerService {

    private final static Logger log = LogManager.getLogger(CommentsAnalyzerService.class);

    private static final String RESOURCES = "src/main/resources/";


    private static final int CSV_FILE_MESSAGE_COLUMN = 0;

    private SchoolRepository repository;

    public Map<String, Integer> analyze(String fileName) {
        repository = new SchoolRepository(new DQLService("schools.db"));
        Map<String, Integer> schoolStatusBoard = new HashMap<>();
        Path path = Paths.get(RESOURCES + fileName);

        log.info("comments analyze start");
        try {
            CSVReader reader = new CSVReaderBuilder(new InputStreamReader(Files.newInputStream(path), StandardCharsets.UTF_8))
                    .withSkipLines(1).build();


            String[] comments;
            while ((comments = reader.readNext()) != null) {
                List<String> schools = parse(comments[CSV_FILE_MESSAGE_COLUMN]);

                for (String school : schools) {
                    if (!schoolStatusBoard.containsKey(school)) {
                        schoolStatusBoard.put(school, 0);
                    }
                    int value = schoolStatusBoard.get(school) + 1;
                    schoolStatusBoard.put(school, value);
                }
            }
            log.info("comments analyze end");
        } catch (IOException e) {
            log.error("path file error", e);
        } catch (CsvValidationException e) {
            log.error("csvfile read error", e);
        }

        return new HashMap<>(schoolStatusBoard);
    }

//    private List<String> parse(String text) {
//        List<String> locations = AnalyzerUtils.getLocationsFromText(text);
//
//        // Normalize
//        CharSequence normalized = OpenKoreanTextProcessorJava.normalize(text);
//
//        // Tokenize
//        Seq<KoreanTokenizer.KoreanToken> tokens = OpenKoreanTextProcessorJava.tokenize(normalized);
//        List<KoreanTokenJava> filteredKoreaTokens = AnalyzerUtils.filterPos(OpenKoreanTextProcessorJava.tokensToJavaKoreanTokenList(tokens));
//
//        List<String> words = new ArrayList<>();
//
//        for (KoreanTokenJava koreaToken : filteredKoreaTokens) {
//            words.add(AnalyzerUtils.removeNonKorean(koreaToken.getText()));
//        }
//
//        int usedIndex = 0;
//        List<String> schools = new ArrayList<>();
//        for (int currentIndex = 0; currentIndex < words.size(); currentIndex++) {
//            SchoolTypes level = AnalyzerUtils.getSchoolLevel(words.get(currentIndex));
//            if (level == UNKNOWN_SCHOOL_LEVEL) {
//                continue;
//            }
//
//            List<String> targetData = words.subList(Math.max(usedIndex, currentIndex - 4), currentIndex + 1);
//
//            for (int i = 1; i < (targetData.size() + 1); i++) {
//                List<String> rawData = targetData.subList(targetData.size() + Math.max(-(targetData.size()), -i), targetData.size());
//
//                SchoolToken schoolToken = new SchoolToken(rawData, locations);
//                if (schoolToken.isSkip()) {
//                    continue;
//                }
//
//                String school = repository.findSchool(schoolToken);
//
//                if (school == null) {
//                    continue;
//                }
//
//                usedIndex = currentIndex;
//
//                schools.add(school);
//            }
//        }
//
//        return new ArrayList<>(schools);
//    }

    private List<String> parse(String text) {
        List<String> locations = AnalyzerUtils.getLocationsFromText(text);

        // Normalize
        CharSequence normalized = OpenKoreanTextProcessorJava.normalize(text);

        // Tokenize
        Seq<KoreanTokenizer.KoreanToken> tokens = OpenKoreanTextProcessorJava.tokenize(normalized);
        List<KoreanTokenJava> filteredKoreaTokens = AnalyzerUtils.filterPos(OpenKoreanTextProcessorJava.tokensToJavaKoreanTokenList(tokens));

        List<String> words = new ArrayList<>();

        for (KoreanTokenJava koreaToken : filteredKoreaTokens) {
            words.add(AnalyzerUtils.removeNonKorean(koreaToken.getText()));
        }

        int usedIndex = 0;
        List<String> schoolNameList = new ArrayList<>();
        for (int currentIndex = 0; currentIndex < words.size(); currentIndex++) {
            SchoolTypes level = AnalyzerUtils.getSchoolLevel(words.get(currentIndex));
            if (level == UNKNOWN_SCHOOL_LEVEL) {
                continue;
            }

            List<String> targetData = words.subList(Math.max(usedIndex, currentIndex - 4), currentIndex + 1);

            for (int i = 1; i < (targetData.size() + 1); i++) {
                List<String> rawData = targetData.subList(targetData.size() + Math.max(-(targetData.size()), -i), targetData.size());

                SchoolToken schoolToken = new SchoolToken(rawData, locations);
                if (schoolToken.isSkip()) {
                    continue;
                }


                String school = findSchool(schoolToken);

                if (school == null) {
                    continue;
                }

                usedIndex = currentIndex;

                schoolNameList.add(school);
            }
        }

        return new ArrayList<>(schoolNameList);
    }

    private String findSchool(SchoolToken schoolToken) {
        List<Pattern> matchedPatterns = new ArrayList<>();


        String levelPattern = AnalyzerUtils.getPatternFromLevel(schoolToken.getLevel());
        List<String> kindPatterns = AnalyzerUtils.getPatternFromKinds(schoolToken.getKinds());


        if (kindPatterns.size() > 0) {
            matchedPatterns.add(Pattern.compile(String.format("(%s)+.*%s$",
                    Pattern.quote(String.join("|", kindPatterns)),
                    Pattern.quote(levelPattern))));
        }

        List<School> schools = repository.findBySchoolNameAndLevel(schoolToken.getSchoolName(), levelPattern)
                .stream()
                .filter(school -> {
                    int matched = patternFindFromSchoolName(school.getName(), matchedPatterns);
                    calculateScore(schoolToken, school);
                    return matched == matchedPatterns.size();
                })
                .collect(Collectors.toList());

        if (schools.size() == 0) {
            return null;
        }

        schools.sort(new Comparator<School>() {
            @Override
            public int compare(School o1, School o2) {
                Integer o1Value = o1.getScore();
                Integer o2Value = o2.getScore();
                Integer o1Len = o1.getName().length();
                Integer o2Len = o2.getName().length();
                if (o1Value.equals(o2Value)) {
                    return o1Len.compareTo(o2Len);
                } else {
                    return o2Value.compareTo(o1Value);
                }
            }
        });


        return schools.get(0).getName();

    }

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

    private void calculateScore(SchoolToken schoolToken, School school) {
        if (schoolToken.isGirlKind() && school.getGender().equals("녀")) {
            school.plusScore();
        }

        if (!schoolToken.isGirlKind() && !school.getGender().equals("녀")) {
            school.plusScore();
        }


        if (schoolToken.isSpecialKind() && Arrays.asList("특수목적고등학교", "특성화고등학교").contains(school.getProperty())) {
            school.plusScore();
        }

        if (schoolToken.hasLocationIn(school.getName())) {
            school.plusScore();
            ;
        }

        if (schoolToken.hasLocationIn(school.getLocation())) {
            school.plusScore();
        }

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
