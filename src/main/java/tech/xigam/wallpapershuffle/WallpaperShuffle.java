package tech.xigam.wallpapershuffle;

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

        File directory = new File(args[0]);
        if(!directory.canRead() || !directory.exists() || !directory.isDirectory()) {
            System.out.println("Invalid directory: " + args[0]); System.exit(1);
        }
        
        instance = new WallpaperShuffle(directory, args);
    }

    public final File directory;
    
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
        directory = wallpaperCollection;
        
        parseArguments(additionalArgs); // Set additional settings from command line arguments.
        start(); // Start the wallpaper shuffling after settings have been set.
    }
    
    private void start() {
        ShuffleTask task = new ShuffleTask(this);
        Timer timer = new Timer(); timer.schedule(task, 0, delay * 1000L);
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
        switch(option) {
            default:
                System.out.println("Invalid argument: " + option);
                return;
            case "-h":
            case "--help":
                System.out.println("Usage: java -jar WallpaperShuffle.jar <path to wallpaper collection> [options]");
                System.out.println(
                        """
                        [-h, --help] Print this help message.
                        [-d, --delay] Set the delay between wallpaper changes in seconds.
                        [-r, --repeat] Allow wallpaper repeats.
                        """
                );
                return;
            case "-d":
            case "--delay":
                delay = Integer.parseInt(value);
                return;
            case "-r":
            case "--repeat":
                allowRepeats = Boolean.parseBoolean(value);
                return;
        }
    }
}
