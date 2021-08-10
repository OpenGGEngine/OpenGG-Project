/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.console;

import com.opengg.core.GGInfo;
import com.opengg.core.thread.ThreadManager;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Javier
 */
public class GGConsole {
    private static final List<GGMessage> messages = new ArrayList<>();
    private static final List<ConsoleListener> listeners = new ArrayList<>();
    private static final List<LoggerOutputConsumer> consumers = new ArrayList<>();

    public static void initialize() {
        var currentOut = System.out;

        ThreadManager.setDefaultUncaughtExceptionHandler(new GGThreadExceptionHandler());
        ThreadManager.runDaemon(() -> {
            Scanner in = new Scanner(System.in);
            while (GGInfo.isEnded()) {
                acceptUserInput(in.nextLine());
            }
        }, "ConsoleListener");

        GGConsole.addOutputConsumer(new DefaultLoggerOutputConsumer(Level.DEBUG, currentOut::println));

        if (GGInfo.isRedirectStandardOut()) {
            var loggingStream = new PrintStream(currentOut) {
                @Override
                public void println(String x) {
                    GGConsole.log(x);
                }

                @Override
                public void println(Object x) {
                    GGConsole.log(x != null ? x.toString() : null);
                }
            };

            var errorStream = new PrintStream(currentOut) {
                @Override
                public void println(String x) {
                    GGConsole.error(x);
                }

                @Override
                public void println(Object x) {
                    GGConsole.error(x != null ? x.toString() : null);
                }
            };

            System.setOut(loggingStream);
            System.setErr(errorStream);
        }
    }

    public static void saveLogs(Path logsDirectory) {
        var time = Instant.now();

        try {
            Files.createDirectories(logsDirectory);
            Files.list(logsDirectory)
                    .filter(Files::isRegularFile)
                    .sorted(Comparator.comparingLong(f -> {
                        try {
                            var attrib = Files.readAttributes(f, BasicFileAttributes.class);
                            return -attrib.creationTime().toMillis();
                        } catch (IOException e) {
                            e.printStackTrace();
                            return 0;
                        }
                    })).skip(10)
                    .forEach(f -> {
                        try {
                            Files.delete(f);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });

            var logString = messages.stream().map(GGMessage::toString).collect(Collectors.joining("\n"));

            var logName = GGInfo.getApplicationName()
                    + "_Log_" +
                    Instant.now()
                            .truncatedTo(ChronoUnit.MILLIS)
                            .atZone(ZoneId.systemDefault()).toLocalDateTime()
                            .format(DateTimeFormatter.ISO_DATE_TIME)
                            // .replace( "-" , "" )
                            .replace("T", "_")
                            .replace(":", "-") +
                    ".log";
            Files.writeString(logsDirectory.resolve(logName), logString);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<GGMessage> getAllMessages() {
        return List.copyOf(messages);
    }

    public static GGMessage getMostRecent() {
        return messages.get(messages.size() - 1);
    }

    public static void log(String message) {
        write(message, Level.INFO);
    }

    public static void debug(String message) {
        write(message, Level.DEBUG);
    }

    public static void warning(String message) {
        write(message, Level.WARNING);
    }

    public static void warn(String message) {
        write(message, Level.WARNING);
    }

    public static void error(String message) {
        write(message, Level.ERROR);
    }

    public static void exception(Throwable message) {
        var trace = new StringWriter();
        var pw = new PrintWriter(trace);
        message.printStackTrace(pw);
        write(message.getMessage() + " at " + trace, Level.ERROR);
    }

    private static void write(String message, Level level) {
        GGMessage m = new GGMessage(message, getSender(), level);
        messages.add(m);
        consume(m);
    }

    private static void consume(GGMessage message) {
        for (var consumer : consumers) {
            consumer.onMessage(message);
        }
    }

    public static void addOutputConsumer(LoggerOutputConsumer consumer) {
        consumers.add(consumer);
    }

    private static String getSender() {
        StackTraceElement[] e = Thread.currentThread().getStackTrace();
        return (e[4].getClassName()).substring(e[4].getClassName().lastIndexOf('.') + 1);
    }

    public static void acceptUserInput(String string) {
        String[] strings = string.split(" ");

        UserCommand command = new UserCommand();
        command.time = Calendar.getInstance().getTime();
        command.command = strings[0];
        command.argCount = strings.length - 1;
        command.args = Arrays.copyOfRange(strings, 1, strings.length);
        for (ConsoleListener listener : listeners) {
            listener.onConsoleInput(command);
        }
    }

    public static void addListener(ConsoleListener listener) {
        listeners.add(listener);
    }
}
