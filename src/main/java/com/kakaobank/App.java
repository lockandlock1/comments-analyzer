package com.kakaobank;

import com.kakaobank.repository.db.SQLiteManager;
import com.kakaobank.service.CommentsAnalyzerService;
import com.kakaobank.domain.exporter.Exporter;
import com.kakaobank.domain.exporter.TextFileExporter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;


public class App {

    private final static Logger log = LogManager.getLogger(App.class);

    public static void main(String[] args) {

//        Options options = new Options();
//
//        Option input = new Option("i", "input", true, "input file path");
//        input.setRequired(true);
//        options.addOption(input);
//
//        Option output = new Option("o", "output", true, "output file");
//        output.setRequired(true);
//        options.addOption(output);
//
//        CommandLineParser parser = new DefaultParser();
//        HelpFormatter formatter = new HelpFormatter();
//        CommandLine cmd = null;//not a good practice, it serves it purpose
//
//        try {
//            cmd = parser.parse(options, args);
//        } catch (ParseException e) {
//            System.out.println(e.getMessage());
//            formatter.printHelp("utility-name", options);
//
//            System.exit(1);
//        }
//
//        String inputFilePath = cmd.getOptionValue("input");
//        String outputFilePath = cmd.getOptionValue("output");
//
//        System.out.println(inputFilePath);
//        System.out.println(outputFilePath);

        CommentsAnalyzerService service = new CommentsAnalyzerService();
        Map<String, Integer> result = service.analyze("test.csv");

        log.info("analyze file name={}", "comments.csv");

        Exporter exporter = new TextFileExporter(result, "/Users/redkyu/Documents/git_src/study/school-name-searcher");

        log.info("text file export filePath={}", "/Users/redkyu/Documents/git_src/study/school-name-searcher");

        exporter.export();
    }

}
