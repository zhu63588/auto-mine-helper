package com.example.automine.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ModLogger {
    private static File logDir;
    private static File logFile;
    private static FileWriter logWriter;
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static void init(File configDir) {
        logDir = new File(configDir, "logs");
        if (!logDir.exists()) {
            logDir.mkdirs();
        }
        logFile = new File(logDir, "automine.log");
    }

    private static void writeLog(String level, String message) {
        String timestamp = dateFormat.format(new Date());
        String logLine = String.format("[%s] [%s] [AutoMine] %s", timestamp, level, message);
        System.out.println(logLine);

        try {
            if (logWriter == null) {
                logWriter = new FileWriter(logFile, true);
            }
            logWriter.write(logLine + "\n");
            logWriter.flush();
        } catch (IOException e) {
            System.err.println("Failed to write to log file: " + e.getMessage());
        }
    }

    public static void info(String message) {
        writeLog("INFO", message);
    }

    public static void warn(String message) {
        writeLog("WARN", message);
    }

    public static void error(String message) {
        writeLog("ERROR", message);
    }

    public static void debug(String message) {
        writeLog("DEBUG", message);
    }

    public static void close() {
        if (logWriter != null) {
            try {
                logWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
