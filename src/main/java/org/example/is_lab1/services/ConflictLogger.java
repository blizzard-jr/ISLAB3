package org.example.is_lab1.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


@Component
@Slf4j
public class ConflictLogger {
    private static final String LOG_FILE_PATH = System.getProperty("user.dir") + "/conflicts.txt";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final Object lock = new Object();

    public void logConflict(Long operationId, String existingName, String incomingName, String resolution) {
        synchronized (lock) {
            try {
                Path logPath = Paths.get(LOG_FILE_PATH);
                if (logPath.getParent() != null && !Files.exists(logPath.getParent())) {
                    Files.createDirectories(logPath.getParent());
                }

                try (FileWriter fw = new FileWriter(LOG_FILE_PATH, true);
                     BufferedWriter bw = new BufferedWriter(fw);
                     PrintWriter out = new PrintWriter(bw)) {

                    String timestamp = LocalDateTime.now().format(DATE_FORMATTER);
                    String logEntry = String.format(
                        "[%s] Operation %d: Conflict between '%s' and '%s'. Resolution: %s%n",
                        timestamp, operationId, existingName, incomingName, resolution
                    );

                    out.print(logEntry);
                    out.flush();

                    log.debug("Conflict logged: Operation {}, {} vs {}", operationId, existingName, incomingName);
                }
            } catch (IOException e) {
                log.error("Error writing to conflict log file: {}", e.getMessage(), e);
            }
        }
    }

    public void clearLog() {
        synchronized (lock) {
            try {
                Path logPath = Paths.get(LOG_FILE_PATH);
                Files.deleteIfExists(logPath);
                Files.createFile(logPath);
                Files.write(logPath, "No conflicts found.\n".getBytes());
                log.info("Conflict log file cleared and initialized");
            } catch (IOException e) {
                log.error("Error clearing conflict log file: {}", e.getMessage(), e);
            }
        }
    }
}






