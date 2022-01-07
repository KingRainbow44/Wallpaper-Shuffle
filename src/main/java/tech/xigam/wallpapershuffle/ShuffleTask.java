package tech.xigam.wallpapershuffle;

import com.sun.jna.platform.win32.WinDef;
import org.jetbrains.annotations.Nullable;
import tech.xigam.wallpapershuffle.utils.SPI;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.TimerTask;

public final class ShuffleTask extends TimerTask 
{
    private final WallpaperShuffle wallpaperShuffle;
    
    public ShuffleTask(WallpaperShuffle wallpaperShuffle) {
        this.wallpaperShuffle = wallpaperShuffle;
    }
    
    @Override
    public void run() {
        File file = null;
        if(wallpaperShuffle.allowRepeats) {
            file = getRandomFile(wallpaperShuffle.directory);
        } else {
            if(
                    wallpaperShuffle.alreadyUsed.size() == Objects.requireNonNull(wallpaperShuffle.directory.listFiles()).length
            ) wallpaperShuffle.alreadyUsed.clear();
            
            boolean isUnique = false;
            while(!isUnique) {
                file = getRandomFile(wallpaperShuffle.directory);
                isUnique = !wallpaperShuffle.alreadyUsed.contains(file);
            }
        } if(file == null) return;
        
        try {
            if(!isValidImage(file)) return;
            changeWallpaper(file);
        } catch (IOException ignored) { }
    }
    
    @Nullable
    private File getRandomFile(File directory) {
        File[] files = directory.listFiles(); if(files == null) return null;
        int randomIndex = (int) (Math.random() * files.length);
        return files[randomIndex];
    }
    
    private boolean isValidImage(File file) throws IOException {
        String name = file.getName();
        boolean isImageType = name.endsWith(".jpg") || name.endsWith(".png") || name.endsWith(".bmp") || name.endsWith(".jpeg");

        BufferedImage bufferedImage = ImageIO.read(file);
        int width = bufferedImage.getWidth(); int height = bufferedImage.getHeight();

        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        int screenWidth = gd.getDisplayMode().getWidth();
        int screenHeight = gd.getDisplayMode().getHeight();
        
        return width == screenWidth && screenHeight == height && isImageType;
    }
    
    private void changeWallpaper(File wallpaper) {
        String path = wallpaper.getAbsolutePath();

        SPI.instance.SystemParametersInfo(
                new WinDef.UINT_PTR(SPI.SPI_SETDESKWALLPAPER),
                new WinDef.UINT_PTR(0), path,
                new WinDef.UINT_PTR(SPI.SPIF_UPDATEINIFILE | SPI.SPIF_SENDWININICHANGE)
        );
    }
}
