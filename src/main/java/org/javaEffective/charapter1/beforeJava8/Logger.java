package org.javaEffective.charapter1.beforeJava8;

public interface Logger {
    void log(String message);

    static Logger getFileLogger() {
        return (Logger) new Exception("Not implemented yet");
    }

    default Logger getConsoleLogger() {
        return (Logger) new Exception("Not implemented yet");
    }
}
