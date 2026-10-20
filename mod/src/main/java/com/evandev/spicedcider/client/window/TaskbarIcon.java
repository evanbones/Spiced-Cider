package com.evandev.spicedcider.client.window;

import net.minecraft.Util;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFWNativeWin32;
import org.lwjgl.system.JNI;
import org.lwjgl.system.Library;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.SharedLibrary;
import org.lwjgl.system.windows.User32;

import java.nio.ByteBuffer;

import static org.lwjgl.system.MemoryUtil.*;
import static org.lwjgl.system.Pointer.POINTER_SIZE;

public final class TaskbarIcon {
    private static final String APP_ID = "SpicedCider.Game";
    private static final int ICON_SMALL = 0;
    private static final int ICON_BIG = 1;
    private static final int VT_LPWSTR = 31;
    private static final int PID_APP_ID = 5;

    public static void sync(long glfwWindow) {
        if (Util.getPlatform() != Util.OS.WINDOWS || glfwWindow == 0L) return;
        long hwnd = GLFWNativeWin32.glfwGetWin32Window(glfwWindow);
        if (hwnd == 0L) return;
        long big = User32.nSendMessage(hwnd, User32.WM_GETICON, ICON_BIG, 0L, User32.Functions.SendMessage);
        long small = User32.nSendMessage(hwnd, User32.WM_GETICON, ICON_SMALL, 0L, User32.Functions.SendMessage);
        if (big != 0L) User32.SetClassLongPtr(hwnd, User32.GCL_HICON, big);
        if (small != 0L) User32.SetClassLongPtr(hwnd, User32.GCL_HICONSM, small);
        setAppId(hwnd);
    }

    private static void setAppId(long hwnd) {
        try (SharedLibrary shell32 = Library.loadNative(TaskbarIcon.class, "org.lwjgl", "shell32");
             MemoryStack stack = MemoryStack.stackPush()) {
            long getStore = shell32.getFunctionAddress("SHGetPropertyStoreForWindow");
            if (getStore == NULL) return;
            ByteBuffer iid = guid(stack.calloc(4, 16), 0x886D8EEB, (short) 0x8CF2, (short) 0x4446, 0x8D, 0x02, 0xCD, 0xBA, 0x1D, 0xBD, 0xCF, 0x99);
            PointerBuffer out = stack.mallocPointer(1);
            if (JNI.callPPPI(hwnd, memAddress(iid), memAddress(out), getStore) != 0) return;
            long store = out.get(0);
            long vtable = memGetAddress(store);
            ByteBuffer key = guid(stack.calloc(4, 20), 0x9F4C2855, (short) 0x9F79, (short) 0x4B39, 0xA8, 0xD0, 0xE1, 0xD4, 0x2D, 0xE1, 0xD5, 0xF3);
            key.putInt(16, PID_APP_ID);
            ByteBuffer value = stack.calloc(8, 24);
            value.putShort(0, (short) VT_LPWSTR);
            memPutAddress(memAddress(value) + 8, memAddress(stack.UTF16(APP_ID)));
            JNI.callPPPI(store, memAddress(key), memAddress(value), memGetAddress(vtable + 6L * POINTER_SIZE));
            JNI.callPI(store, memGetAddress(vtable + 7L * POINTER_SIZE));
            JNI.callPI(store, memGetAddress(vtable + 2L * POINTER_SIZE));
        }
    }

    private static ByteBuffer guid(ByteBuffer buffer, int data1, short data2, short data3, int... data4) {
        buffer.putInt(0, data1).putShort(4, data2).putShort(6, data3);
        for (int i = 0; i < 8; i++) buffer.put(8 + i, (byte) data4[i]);
        return buffer;
    }
}
