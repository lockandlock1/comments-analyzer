package com.kakaobank.service;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        log.info("analyze start");
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
        } catch (IOException e) {
            log.error("path file error", e);
        } catch (CsvValidationException e) {
            log.error("csvfile read error", e);
        }

        return new HashMap<>(schoolStatusBoard);
    }

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
        List<String> schools = new ArrayList<>();
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

                String school = repository.findSchool(schoolToken);

                if (school == null) {
                    continue;
                }

                usedIndex = currentIndex;

                schools.add(school);
            }
        }

        return new ArrayList<>(schools);
    }

}
