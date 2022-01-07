package tech.xigam.wallpapershuffle.utils;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.WinDef.UINT_PTR;
import com.sun.jna.win32.*;

import java.util.HashMap;

public interface SPI extends StdCallLibrary {
    long SPI_SETDESKWALLPAPER = 20;
    long SPIF_UPDATEINIFILE = 0x01;
    long SPIF_SENDWININICHANGE = 0x02;
    
    SPI instance = Native.loadLibrary("user32", SPI.class, new HashMap<>() {
        {
            put(OPTION_TYPE_MAPPER, W32APITypeMapper.UNICODE);
            put(OPTION_FUNCTION_MAPPER, W32APIFunctionMapper.UNICODE);
        }
    });
    
    boolean SystemParametersInfo(
            UINT_PTR uiAction,
            UINT_PTR uiParam,
            String pvParam,
            UINT_PTR fWinIni
    );
}
