package logic;

import java.io.*;

public class FileStorage {
    final static boolean append = true;

    public static void append(String fileName, String text) throws IOException {
        FileWriter fileWriter = new FileWriter(fileName, append);
        fileWriter.append(text);
        fileWriter.close();
    }

    public static boolean exists(String fileName) {
        File file = new File(fileName);
        return file.exists();
    }
}
