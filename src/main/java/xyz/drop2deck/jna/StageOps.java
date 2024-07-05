package xyz.drop2deck.jna;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.PointerType;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.W32Errors;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;
import javafx.scene.paint.Color;

/**
 * A small collection of utility methods to customize a window.
 * Targets Windows 11+, won't show any effect on unsupported OSes.
 */
@SuppressWarnings("UnusedReturnValue")
public class StageOps {
    /**
     * A wrapper for HWND type.
     */
    public static class WindowHandle {
        private final WinDef.HWND value;

        public WindowHandle(String stageName) {
            value = getHwnd(stageName);
        }
    }

    private static WinDef.HWND getHwnd(String stageName) {
        return User32.INSTANCE.FindWindow(null, stageName);
    }

    private interface DwmSupport extends Library {
        DwmSupport INSTANCE = Native.load("dwmapi", DwmSupport.class);

        WinNT.HRESULT DwmSetWindowAttribute(
                WinDef.HWND hwnd,
                int dwAttribute,
                PointerType pvAttribute,
                int cbAttribute
        );
    }

    /**
     * A wrapper for DwmSetWindowAttribute.
     *
     * @param handle    WindowHandle for the window. Can be obtained by using findWindowHandle method. Can be null.
     * @param attribute dwAttribute
     * @param value     pvAttribute
     * @return True if it was successful, false if it wasn't.
     */
    public static boolean dwmSetBooleanValue(final WindowHandle handle, final DwmAttribute attribute, final boolean value) {
        if (handle == null) {
            return false;
        }
        return isOk(
                DwmSupport.INSTANCE.DwmSetWindowAttribute(
                        handle.value,
                        attribute.value,
                        new WinDef.BOOLByReference(new WinDef.BOOL(value)),
                        WinDef.BOOL.SIZE
                )
        );
    }

    /**
     * A wrapper for DwmSetWindowAttribute.
     *
     * @param handle    WindowHandle for the window. Can be obtained by using findWindowHandle method. Can be null.
     * @param attribute dwAttribute
     * @param value     pvAttribute
     * @return True if it was successful, false if it wasn't.
     */
    public static boolean dwmSetIntValue(final WindowHandle handle, final DwmAttribute attribute, final int value) {
        if (handle == null) {
            return false;
        }
        return isOk(
                DwmSupport.INSTANCE.DwmSetWindowAttribute(
                        handle.value,
                        attribute.value,
                        new WinDef.DWORDByReference(new WinDef.DWORD(value)),
                        WinDef.DWORD.SIZE
                )
        );
    }

    /**
     * Sets the border color of a window.
     *
     * @param handle WindowHandle for the window. Can be obtained by using findWindowHandle method. Can be null.
     * @param color  Border color
     * @return True if it was successful, false if it wasn't.
     */
    public static boolean setBorderColor(final WindowHandle handle, final Color color) {
        return dwmSetIntValue(handle, DwmAttribute.DWMWA_BORDER_COLOR, RGB(color));
    }

    /**
     * Sets the title bar background color of a window.
     *
     * @param handle WindowHandle for the window. Can be obtained by using findWindowHandle method. Can be null.
     * @param color  Caption color
     * @return True if it was successful, false if it wasn't.
     */
    public static boolean setCaptionColor(final WindowHandle handle, final Color color) {
        return dwmSetIntValue(handle, DwmAttribute.DWMWA_CAPTION_COLOR, RGB(color));
    }

    /**
     * Sets the title text color of a window.
     *
     * @param handle WindowHandle for the window. Can be obtained by using findWindowHandle method. Can be null.
     * @param color  Caption color
     * @return True if it was successful, false if it wasn't.
     */
    public static boolean setTextColor(final WindowHandle handle, final Color color) {
        return dwmSetIntValue(handle, DwmAttribute.DWMWA_TEXT_COLOR, RGB(color));
    }

    private static int floatingTo8Bit(final double n) {
        return (int) Math.min(255.0, Math.max(n * 255.0, 0.0));
    }

    private static boolean isOk(final WinNT.HRESULT result) {
        return WinNT.HRESULT.compare(result, W32Errors.S_OK) == 0;
    }

    private static int RGB(final Color color) {
        return (floatingTo8Bit(color.getBlue()) << 16)
                | (floatingTo8Bit(color.getGreen()) << 8)
                | floatingTo8Bit(color.getRed());
    }

    private StageOps() {
    }
}