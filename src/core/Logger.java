package core;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Logger {
    public final static String PATH = "log.txt";
    private BufferedWriter writer;
    private Map<String, BufferedWriter> writers = new HashMap<>();
    private boolean isOpen = false;
    
    private static Logger ourInstance = new Logger();
    
    @Contract(pure = true)
    public static Logger getInstance() {
        return ourInstance;
    }
    
    private Logger() {
        try {
            writer = new BufferedWriter(new FileWriter(PATH));
            isOpen = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void addLogger(String name, String path) {
        if (writers.containsKey(name)) {
            throw new IllegalStateException("Logger \"" + name + "\" already exists");
        }
        try {
            writers.put(name, new BufferedWriter(new FileWriter(path)));
        } catch (IOException e) {
            e.printStackTrace();
            Arrays.stream(e.getStackTrace()).forEach((p) -> logln(p.toString()));
        }
    }
    
    public void log(@NotNull String msg) {
        if (!isOpen) {
            throw new IllegalStateException("Logger is closed");
        }
        try {
            writer.write(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void log(String name, @NotNull String msg) {
        if (!writers.containsKey(name)) {
            throw new IllegalStateException("Logger name \"" + name + "\" not found");
        }
        BufferedWriter w = writers.get(name);
        log(msg);
        try {
            w.write(msg);
        } catch (IOException e) {
            e.printStackTrace();
            Arrays.stream(e.getStackTrace()).forEach((p) -> logln(p.toString()));
        }
    }
    
    public void logln(@NotNull String msg) {
        log(msg);
        try {
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void logln(String name, @NotNull String msg) {
        if (!writers.containsKey(name)) {
            throw new IllegalStateException("Logger name \"" + name + "\" not found");
        }
        BufferedWriter w = writers.get(name);
        log(name, msg);
        try {
            writer.newLine();
            w.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void close() {
        try {
            writer.close();
            writers.values().forEach((w) -> {
                try {
                    w.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            });
            isOpen = false;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
