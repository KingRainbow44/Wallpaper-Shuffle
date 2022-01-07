package tech.xigam.wallpapershuffle;

import tech.xigam.wallpapershuffle.utils.StartupConfigurator;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

/**
 * WallpaperShuffle: Windows Spotlight'ify your wallpaper collection!
 */
public final class WallpaperShuffle 
{
    public static WallpaperShuffle instance;
    
    public static void main(String[] args) {
        if(args.length < 1) {
            System.out.println("Usage: java -jar WallpaperShuffle.jar <path to wallpaper collection>");
            System.exit(1);
        }

        if(!args[0].matches("--self")) {
            File directory = new File(args[0]);
            if(!directory.canRead() || !directory.exists() || !directory.isDirectory()) {
                System.out.println("Invalid directory: " + args[0]); System.exit(1);
            } instance = new WallpaperShuffle(directory, args);
        } else instance = new WallpaperShuffle(null, args);
    }

    public File directory;
    
    /*
     * Optional settings to change in the command line.
     */
    
    public int delay = (60 * 60) * 24; // 24 hours
    public boolean allowRepeats = false;
    
    /*
     * Variables used by other aspects of the system.
     */
    
    public List<File> alreadyUsed = new ArrayList<>(); // This goes unused if repeats are allowed.
    
    public WallpaperShuffle(File wallpaperCollection, String[] additionalArgs) {
        if(wallpaperCollection != null) directory = wallpaperCollection; else {
            selfStart(); return;
        }
        
        parseArguments(additionalArgs); // Set additional settings from command line arguments.
        start(); // Start the wallpaper shuffling after settings have been set.
    }
    
    private void selfStart() {
        Configuration configuration = new Configuration(new File(StartupConfigurator.getConfigFile()));
        delay = configuration.delay; allowRepeats = configuration.repeats; directory = new File(String.valueOf(configuration.directory));
        start(); // Start the wallpaper shuffling after overriding settings.
    }
    
    private void start() {
        ShuffleTask task = new ShuffleTask(this);
        Timer timer = new Timer(); timer.schedule(task, 0, delay * 1000L);
        
        System.out.println("[WallpaperShuffle] Started at a delay of " + delay + " seconds.");
        System.out.println("[WallpaperShuffle] Repeats are " + (allowRepeats ? "allowed." : "not allowed."));
        System.out.println("[WallpaperShuffle] Shuffling wallpaper collection at " + directory.getAbsolutePath() + ".");
        System.out.println("[WallpaperShuffle] Press CTRL+C to stop.");
        System.out.println("[WallpaperShuffle] Restart the program with the `--help all` option to see a list of available options.");
    }
    
    private void parseArguments(String[] args) {
        List<String> arguments;
        
        /*
         * Split command line arguments.
         */
        if(args.length % 2 == 0) {
            arguments = new ArrayList<>(List.of(args));
            arguments.remove(args.length - 1);
        } else arguments = new ArrayList<>(List.of(args));
        arguments.remove(0);
        
        // Iterate over every other element in arguments.
        for(int i = 0; i < arguments.size(); i += 2) {
            String option = arguments.get(i);
            String value = arguments.get(i + 1);
            
            setArgument(option, value);
        }
    }
    
    private void setArgument(String option, String value) {
        switch (option) {
            default -> System.out.println("Invalid argument: " + option);
            case "-h", "--help" -> {
                switch (value) {
                    default -> {
                        System.out.println("Usage: java -jar WallpaperShuffle.jar <path to wallpaper collection> [options]");
                        System.out.println(
                                """
                                [-h, --help] Print this help message.
                                [-d, --delay] Set the delay between wallpaper changes in seconds.
                                [-r, --repeat] Allow wallpaper repeats.
                                [-s, --startup] Configure WallpaperShuffle to start automatically on startup.
                                """
                        );
                    }
                    case "delay" -> System.out.println("[-d, --delay] Set the delay between wallpaper changes in seconds.");
                    case "repeat" -> System.out.println("[-r, --repeat] Allow wallpaper repeats.");
                }
                System.exit(1);
            }
            case "-d", "--delay" -> {
                delay = Integer.parseInt(value);
            }
            case "-r", "--repeat" -> {
                allowRepeats = Boolean.parseBoolean(value);
            }
            case "-s", "--startup" -> {
                boolean enable = Boolean.parseBoolean(value);
                if(enable && !StartupConfigurator.startupEnabled())
                    StartupConfigurator.enableStartup(directory);
                else if (!enable && StartupConfigurator.startupEnabled())
                    StartupConfigurator.disableStartup();
            }
        }
    }
}
