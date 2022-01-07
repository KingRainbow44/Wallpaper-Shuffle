package tech.xigam.wallpapershuffle.utils;

import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.WinReg;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
    
    public static void enableStartup(File directory) {
        Advapi32Util.registryCreateKey(
                WinReg.HKEY_CURRENT_USER,
                "Software\\KingRainbow44\\Wallpaper Shuffle"
        ); // This creates the Wallpaper Shuffle key (registry folder).
        
        String userAppData = System.getenv("APPDATA");
        Advapi32Util.registrySetStringValue(
                WinReg.HKEY_CURRENT_USER,
                "Software\\KingRainbow44\\Wallpaper Shuffle",
                "Configuration", userAppData + "\\WallpaperShuffle\\config.ini"
        ); // This sets the config location.

        try { // Create the config file, download the latest JAR file to your AppData folder, and create the service.
            File configFile = new File(userAppData + "\\WallpaperShuffle\\config.ini");
            if (!configFile.exists()) {
                if(!configFile.getParentFile().mkdirs() || !configFile.createNewFile()) {
                    System.out.println("Failed to create config file.");
                } else {
                    FileWriter writer = new FileWriter(configFile);
                    writer.write("""
                            ; Wallpaper Shuffle Configuration File
                            directory=%s
                            delay=86400
                            repeats=false
                            """.formatted(directory.getAbsolutePath()));
                    writer.close();
                }
            }
            
            // Copy the file.
            File jarFile = new File(userAppData + "\\WallpaperShuffle\\WallpaperShuffle.jar");
            if(jarFile.exists()) {
                // noinspection ResultOfMethodCallIgnored
                jarFile.delete(); // Attempt to delete the JAR file.
            }

            Files.copy(
                    new File("Wallpaper Shuffle.jar").toPath(),
                    Path.of(userAppData + "\\WallpaperShuffle\\WallpaperShuffle.jar")
            );

            // Create the startup service.
            Runtime.getRuntime().exec("sc create \"Wallpaper Shuffle\" binPath= \"java -jar %s\\WallpaperShuffle\\WallpaperShuffle.jar --self true\" start=auto".formatted(userAppData));
            System.out.println("sc create \"Wallpaper Shuffle\" binPath= \"java -jar %s\\WallpaperShuffle\\WallpaperShuffle.jar --self true\" start=auto".formatted(userAppData));
            
            System.out.println("Startup mode has been enabled. The program will now restart as a service."); 
            System.out.println("The service start on restart, you might need to disable your anti-virus if it fails.");
            
            System.exit(0);
        } catch (IOException exception) {
            // TODO: Ask for elevation and restart the process.
            System.out.println("Unable to create service from the JAR file. Try running the program as an administrator and renaming your JAR file to 'Wallpaper Shuffle.jar'.");
            System.exit(1);
        }
    }
    
    public static void disableStartup() {
        Advapi32Util.registryDeleteKey(
                WinReg.HKEY_CURRENT_USER,
                "Software\\KingRainbow44\\Wallpaper Shuffle"
        ); // Remove the registry key.
        
        try {
            Runtime.getRuntime().exec("sc delete \"Wallpaper Shuffle\"");
            System.out.println("Startup mode has been disabled. The service has been removed.");
            System.exit(0);
        } catch (IOException exception) {
            // TODO: Ask for elevation and restart the process.
            System.out.println("Unable to remove the service. Try running the program as an administrator.");
            System.exit(1);
        }
    }
    
    public static String getConfigFile() {
        return Advapi32Util.registryGetStringValue(
                WinReg.HKEY_CURRENT_USER,
                "Software\\KingRainbow44\\Wallpaper Shuffle",
                "Configuration"
        );
    }
}
