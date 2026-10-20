package com.evandev.spicedcider.bootstrap;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.WString;
import com.sun.jna.ptr.IntByReference;
import net.neoforged.neoforgespi.earlywindow.GraphicsBootstrapper;

import java.io.InputStream;
import java.util.Locale;

public class EarlyTaskbarIcon implements GraphicsBootstrapper {
    private static final String APP_ID = "SpicedCider.Game";
    private static final String WINDOW_CLASS = "GLFW30";
    private static final int WM_SETICON = 0x0080;
    private static final int ICON_SMALL = 0;
    private static final int ICON_BIG = 1;
    private static final int GCL_HICON = -14;
    private static final int GCL_HICONSM = -34;
    private static final int ICON_RESOURCE_VERSION = 0x00030000;
    private static final long TIMEOUT_MS = 60_000L;

    @Override
    public String name() {
        return "spicedcider_early_icon";
    }

    @Override
    public void bootstrap(String[] arguments) {
        if (!System.getProperty("os.name", "").toLowerCase(Locale.ROOT).startsWith("windows")) return;
        try {
            Shell32.INSTANCE.SetCurrentProcessExplicitAppUserModelID(new WString(APP_ID));
        } catch (Throwable ignored) {
            return;
        }
        Thread thread = new Thread(EarlyTaskbarIcon::applyWhenWindowExists, "Spiced Cider early icon");
        thread.setDaemon(true);
        thread.start();
    }

    private static void applyWhenWindowExists() {
        try {
            Pointer big = createIcon("icon_64.png", 64);
            Pointer small = createIcon("icon_32.png", 32);
            if (big == null || small == null) return;
            long pid = ProcessHandle.current().pid();
            long deadline = System.currentTimeMillis() + TIMEOUT_MS;
            while (System.currentTimeMillis() < deadline) {
                Pointer hwnd = findWindow(pid);
                if (hwnd != null) {
                    User32.INSTANCE.SetClassLongPtrW(hwnd, GCL_HICON, big);
                    User32.INSTANCE.SetClassLongPtrW(hwnd, GCL_HICONSM, small);
                    User32.INSTANCE.PostMessageW(hwnd, WM_SETICON, ICON_BIG, Pointer.nativeValue(big));
                    User32.INSTANCE.PostMessageW(hwnd, WM_SETICON, ICON_SMALL, Pointer.nativeValue(small));
                    return;
                }
                Thread.sleep(10L);
            }
        } catch (Throwable ignored) {
        }
    }

    private static Pointer findWindow(long pid) {
        WString windowClass = new WString(WINDOW_CLASS);
        IntByReference owner = new IntByReference();
        Pointer hwnd = null;
        while ((hwnd = User32.INSTANCE.FindWindowExW(null, hwnd, windowClass, null)) != null) {
            User32.INSTANCE.GetWindowThreadProcessId(hwnd, owner);
            if (owner.getValue() == pid) return hwnd;
        }
        return null;
    }

    private static Pointer createIcon(String name, int size) throws Exception {
        try (InputStream in = EarlyTaskbarIcon.class.getResourceAsStream("/spicedcider_bootstrap/" + name)) {
            if (in == null) return null;
            byte[] data = in.readAllBytes();
            return User32.INSTANCE.CreateIconFromResourceEx(data, data.length, true, ICON_RESOURCE_VERSION, size, size, 0);
        }
    }

    private interface User32 extends Library {
        User32 INSTANCE = Native.load("user32", User32.class);

        Pointer FindWindowExW(Pointer parent, Pointer after, WString className, WString windowName);

        int GetWindowThreadProcessId(Pointer hwnd, IntByReference processId);

        boolean PostMessageW(Pointer hwnd, int message, long wParam, long lParam);

        Pointer SetClassLongPtrW(Pointer hwnd, int index, Pointer value);

        Pointer CreateIconFromResourceEx(byte[] data, int size, boolean icon, int version, int width, int height, int flags);
    }

    private interface Shell32 extends Library {
        Shell32 INSTANCE = Native.load("shell32", Shell32.class);

        int SetCurrentProcessExplicitAppUserModelID(WString appId);
    }
}
