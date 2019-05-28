package com.kiran.fileexplorer.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.management.ManagementFactory;

public class LoggerUtil {

    public static Logger contextLogger() {
        return LoggerFactory.getLogger(getCallingClassName());
    }

    private static synchronized String getCallingClassName() {
        long threadId = Thread.currentThread().getId();
        StackTraceElement[] stackTraceElements = ManagementFactory.getThreadMXBean().getThreadInfo(threadId, 3).getStackTrace();
        return stackTraceElements[2].getClassName();
    }
}
