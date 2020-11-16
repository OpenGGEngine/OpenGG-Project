package com.opengg.core.render.internal.opengl;

import com.opengg.core.console.GGConsole;
import org.lwjgl.opengl.GLDebugMessageCallbackI;
import org.lwjgl.system.MemoryUtil;

import static org.lwjgl.opengl.GL43.*;
import static org.lwjgl.system.MemoryUtil.memByteBuffer;

public class DebugCallback implements GLDebugMessageCallbackI {
    @Override
    public void invoke(int source, int type, int id, int severity, int length, long message, long userParam) {
        String msg = MemoryUtil.memUTF8(memByteBuffer(message, length));
        var realSource = getFromGLSource(source);
        var realType = getFromCallbackType(type);
        switch (getFromGLSeverity(severity)){
            case HIGH -> GGConsole.error("From OpenGL (Source: " + realSource + ", type: " + realType + "): " + msg);
            case MEDIUM, LOW -> GGConsole.warn("From OpenGL (Source: " + realSource + ", type: " + realType + "): " + msg);
            case OTHER -> GGConsole.log("From OpenGL (Source: " + realSource + ", type: " + realType + "): " + msg);
        }
    }

    private MessageSeverity getFromGLSeverity(int severity){
        return switch (severity){
            case GL_DEBUG_SEVERITY_HIGH -> MessageSeverity.HIGH;
            case GL_DEBUG_SEVERITY_MEDIUM -> MessageSeverity.MEDIUM;
            case GL_DEBUG_SEVERITY_LOW -> MessageSeverity.LOW;
            case GL_DEBUG_SEVERITY_NOTIFICATION -> MessageSeverity.NOTIFICATION;
            default -> MessageSeverity.OTHER;
        };
    }

    private MessageSource getFromGLSource(int source){
        return switch (source){
            case GL_DEBUG_SOURCE_API -> MessageSource.API;
            case GL_DEBUG_SOURCE_WINDOW_SYSTEM -> MessageSource.WINDOW;
            case GL_DEBUG_SOURCE_SHADER_COMPILER -> MessageSource.SHADER;
            case GL_DEBUG_SOURCE_THIRD_PARTY -> MessageSource.THIRD_PARTY;
            case GL_DEBUG_SOURCE_APPLICATION -> MessageSource.APPLICATION;
            case GL_DEBUG_SOURCE_OTHER -> MessageSource.UNKNOWN;
            default -> MessageSource.UNKNOWN;
        };
    }

    private MessageType getFromCallbackType(int callback){
        return switch (callback){
            case GL_DEBUG_TYPE_ERROR -> MessageType.ERROR;
            case GL_DEBUG_TYPE_DEPRECATED_BEHAVIOR -> MessageType.DEPRECATED_USAGE;
            case GL_DEBUG_TYPE_UNDEFINED_BEHAVIOR -> MessageType.UNDEFINED_BEHAVIOR;
            case GL_DEBUG_TYPE_PORTABILITY -> MessageType.PORTABILITY;
            case GL_DEBUG_TYPE_PERFORMANCE -> MessageType.PERFORMANCE;
            case GL_DEBUG_TYPE_OTHER -> MessageType.OTHER;
            case GL_DEBUG_TYPE_MARKER -> MessageType.MARKER;
            default -> MessageType.OTHER;
        };
    }

    private enum MessageSource{
        API,
        WINDOW,
        SHADER,
        THIRD_PARTY,
        APPLICATION,
        UNKNOWN
    }

    private enum MessageSeverity{
        HIGH,
        MEDIUM,
        LOW,
        NOTIFICATION,
        OTHER
    }

    private enum MessageType {
        ERROR,
        DEPRECATED_USAGE,
        UNDEFINED_BEHAVIOR,
        PORTABILITY,
        PERFORMANCE,
        MARKER,
        OTHER
    }
}
