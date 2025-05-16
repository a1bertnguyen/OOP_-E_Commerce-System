package util;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {
    public static final String DATA_FILE = "src/Resources/data.txt";

    public static List<String> readLines(String filePathStr) {
        Path filePath = Paths.get(filePathStr);
        List<String> lines = new ArrayList<>();
        if (!Files.exists(filePath)) {
            System.err.println("Error: File does not exist at the path: " + filePathStr);
            // ensureDataFileExists() should be called before attempting to read if creation is intended.
            return lines;
        }

        try (BufferedReader reader = Files.newBufferedReader(filePath, StandardCharsets.UTF_8)) { // Explicitly use UTF-8
            System.err.println("DEBUG FileUtil: Successfully opened file for reading: " + filePathStr);
            String line;
            boolean firstLine = true;
            int lineCount = 0;

            while ((line = reader.readLine()) != null) {

                lineCount++;
                if (firstLine && !line.isEmpty() && line.charAt(0) == '\uFEFF') {
                    line = line.substring(1);
                }
                lines.add(line);
                firstLine = false;
            }
            System.err.println("DEBUG FileUtil: Finished reading file. Total lines read: " + lineCount + ". Lines added to list: " + lines.size());
        } catch (IOException e) {
            System.err.println("Failed to read file " + filePathStr + ". Ensure it is UTF-8 encoded. Error details: " + e.toString());
            // e.printStackTrace(); // Uncomment for full stack trace during development
        }
        if (lines.isEmpty() && Files.exists(filePath)) {
            try {
                if (Files.size(filePath) > 0) {
                    System.err.println("DEBUG FileUtil: File exists and is not empty, but readLines returned an empty list. Possible parsing or logic error within readLines.");
                } else {
                    System.err.println("DEBUG FileUtil: File exists but is empty.");
                }
            } catch (IOException ignored) {}
        }
        return lines;
    }

    public static void writeLines(String filePathStr, List<String> lines, boolean append) {
        Path filePath = Paths.get(filePathStr);
        createParentDirIfNotExists(filePath);

        StandardOpenOption[] options;
        if (append) {
            // For appending: create if not exists, append to existing.
            options = new StandardOpenOption[]{StandardOpenOption.CREATE, StandardOpenOption.APPEND, StandardOpenOption.WRITE};
        } else {
            // For overwriting: create if not exists, truncate if exists.
            options = new StandardOpenOption[]{StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE};
        }

        try (BufferedWriter writer = Files.newBufferedWriter(filePath, StandardCharsets.UTF_8, options)) { // Explicitly use UTF-8
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error writing to file " + filePathStr + ": " + e.toString());
            // e.printStackTrace(); // Uncomment for full stack trace
        }
    }

    private static void createParentDirIfNotExists(Path filePath) {
        Path parentDir = filePath.getParent();
        if (parentDir != null && !Files.exists(parentDir)) {
            try {
                Files.createDirectories(parentDir);
            } catch (IOException e) {
                System.err.println("Error creating parent directory '" + parentDir + "': " + e.toString());
                // e.printStackTrace(); // Uncomment for full stack trace
            }
        }
    }

    public static void ensureDataFileExists() {
        Path filePath = Paths.get(DATA_FILE);
        if (!Files.exists(filePath)) {
            createParentDirIfNotExists(filePath);
            try {
                Files.createFile(filePath);
                System.out.println("Created data file: " + DATA_FILE);
            } catch (IOException e) {
                System.err.println("Error creating data file '" + DATA_FILE + "': " + e.toString());
                // e.printStackTrace(); // Uncomment for full stack trace
            }
        }
    }
}