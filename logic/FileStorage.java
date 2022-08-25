package logic;

import java.io.*;

public class FileStorage {
    private static final boolean APPEND = true;

    public static void append(String fileName, String text) throws IOException {
        FileWriter fileWriter = new FileWriter(fileName, APPEND);
        fileWriter.append(text);
        fileWriter.close();
    }

    public static boolean exists(String fileName) {
        File file = new File(fileName);
        return file.exists();
    }
}
