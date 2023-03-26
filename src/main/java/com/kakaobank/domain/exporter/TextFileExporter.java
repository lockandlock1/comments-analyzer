package com.kakaobank.domain.exporter;

import com.kakaobank.service.CommentsAnalyzerService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public class TextFileExporter implements Exporter {

    private final static Logger log = LogManager.getLogger(CommentsAnalyzerService.class);

    private static final String FILE_NAME = "result.txt";

    private final Map<String, Integer> data;

    private final String outputFilePath;

    public TextFileExporter(Map<String, Integer> data, String outputPath) {
        this.data = data;
        this.outputFilePath = outputPath;
    }

    @Override
    public void export() {
        Path path = Paths.get(outputFilePath + File.separator + FILE_NAME);

        Path folderPath = Paths.get(outputFilePath);

        File file = new File(path.toUri());
        try {
            Files.createDirectories(folderPath);

            Files.deleteIfExists(path);

            Files.createFile(path);

            BufferedWriter writer = new BufferedWriter(new FileWriter(file));

            for (String key : data.keySet()) {
                writer.write(key + "\t" + data.get(key) + "\n");
            }

            writer.close();
        } catch (IOException e) {
            log.error("Text File Export Error", e);
            throw new RuntimeException(e);
        }
    }
}
