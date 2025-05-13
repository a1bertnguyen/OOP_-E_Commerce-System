package util;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;

public class FileUtil {
    public static final String DATA_FILE = "src/Resources/data.txt";

    public static List<String> readLines(String filePathStr) {
        Path filePath = Paths.get(filePathStr);
        List<String> lines = new ArrayList<>();
        if (!Files.exists(filePath)) {
            System.err.println("Error: File does not exist at the path: " + filePathStr);
            return lines; // Return empty list if file does not exist
        }

        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            System.err.println("Fail to read file " + filePathStr + ": " + e.getMessage());
        }
        return lines;
    }

    public static void writeLines(String filePathStr, List<String> lines, boolean append) {
        Path filePath = Paths.get(filePathStr);
        createParentDirIfNotExists(filePath);

        try (BufferedWriter writer = Files.newBufferedWriter(filePath,
                append ? StandardOpenOption.APPEND : StandardOpenOption.CREATE,
                StandardOpenOption.WRITE)) {

            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error when recorded in the file " + filePathStr + ": " + e.getMessage());
        }
    }

    private static void createParentDirIfNotExists(Path filePath) {
        Path parentDir = filePath.getParent();
        if (parentDir != null && !Files.exists(parentDir)) {
            try {
                Files.createDirectories(parentDir);
            } catch (IOException e) {
                System.err.println("Error when creating a parent path for the file: " + e.getMessage());
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
                System.err.println("Error when creating data file: " + e.getMessage());
            }
        }
}

}
