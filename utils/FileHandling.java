package utils;

import java.io.File;
import java.io.InputStream;
import java.util.Scanner;

public class FileHandling {
    public static String loadResource(String fileName) throws Exception {
        String result = null;
        try (InputStream in = FileHandling.class.getResourceAsStream(fileName);
             Scanner scanner = new Scanner(new File(fileName))) {
            result = scanner.useDelimiter("\\A").next();
            scanner.close();
        }
        return result;
    }
}
