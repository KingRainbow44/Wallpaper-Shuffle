package tech.xigam.wallpapershuffle;

import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;

public final class Configuration {
    private final File config;
    
    @Nullable public File directory = null;
    public int delay = (60 * 60) * 24; // In seconds.
    public boolean repeats = false;
    
    public Configuration(File file) {
        config = file; read();
    }
    
    public void read() {
        try {
            String file = Files.readString(config.toPath(), Charset.defaultCharset());
            String[] lines = file.split("\n");
            
            for(String line : lines) {
                String[] split = line.split("=");
                String option = split[0].toLowerCase(); String value = split[1].toLowerCase();
                
                set(option, value);
            }
        } catch (IOException ignored) { }
    }
    
    public void set(String option, String value) {
        if(option.startsWith(";del")) return;
        
        switch(option) {
            default -> { }
            case "directory" -> directory = new File(value);
            case "delay" -> delay = Integer.parseInt(value);
            case "repeats" -> repeats = Boolean.parseBoolean(value);
        }
    }
}
