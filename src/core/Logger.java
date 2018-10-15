package core;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Logger {
    public final static String PATH = "log.txt";
    private BufferedWriter writer;
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
    
    public void logln(@NotNull String msg) {
        log(msg);
        try {
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void close() {
        try {
            writer.close();
            isOpen = false;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
