package tech.xigam.wallpapershuffle.utils;

import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.WinReg;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class StartupConfigurator 
{
    public static boolean startupEnabled() {
        return Advapi32Util.registryKeyExists(
                WinReg.HKEY_CURRENT_USER,
                "Software\\KingRainbow44\\Wallpaper Shuffle"
        );
    }
    
    public static void enableStartup() {
        Advapi32Util.registryCreateKey(
                WinReg.HKEY_CURRENT_USER,
                "Software\\KingRainbow44\\Wallpaper Shuffle"
        ); // This creates the Wallpaper Shuffle key (registry folder).
        
        String userAppData = System.getenv("APPDATA");
        Advapi32Util.registrySetStringValue(
                WinReg.HKEY_CURRENT_USER,
                "Software\\KingRainbow44\\Wallpaper Shuffle",
                "Configuration", userAppData + "\\Wallpaper Shuffle\\config.ini"
        ); // This sets the config location.

        try {
            File configFile = new File(userAppData + "\\Wallpaper Shuffle\\config.ini");
            if (!configFile.exists()) {
                if(!configFile.getParentFile().mkdirs() || !configFile.createNewFile()) {
                    System.out.println("Failed to create config file.");
                } else {
                    FileWriter writer = new FileWriter(configFile);
                    writer.write("""
                            ; Wallpaper Shuffle Configuration File
                            directory=%s\\Wallpaper Shuffle\\wallpapers
                            delay=86400
                            repeats=false
                            """.formatted(userAppData));
                    writer.close();
                }
            }

            Runtime.getRuntime().exec("sc create \"Wallpaper Shuffle\" binPath= \"java -jar %s\\Wallpaper Shuffle\\WallpaperShuffle.jar --self\"".formatted(userAppData));
            Files.copy(Path.of(new URI("https://raw.githubusercontent.com/KingRainbow44/Wallpaper-Shuffle/main/WallpaperShuffle.jar")), Path.of(userAppData + "\\Wallpaper Shuffle\\WallpaperShuffle.jar"));
        } catch (IOException | URISyntaxException ignored) { }
    }
    
    public static void disableStartup() {
        
    }
}
