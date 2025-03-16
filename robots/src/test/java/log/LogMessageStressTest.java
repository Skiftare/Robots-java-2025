package log;

import gui.ui.LogWindow;
import org.junit.Assert;
import org.junit.Test;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class LogMessageStressTest {

    private static final int MESSAGE_COUNT = 5000;
    private static final int PHASE_COUNT = 5;
    private static final int WINDOW_COUNT = 3;
    private static final double MAX_GROWTH_PERCENTAGE = 10.0;

    @Test
    public void testLogMessageMemoryLeak() throws Exception {
        // UI setup
        JFrame frame = new JFrame("Log Message Stress Test");
        JDesktopPane desktop = new JDesktopPane();
        frame.setContentPane(desktop);
        frame.setSize(800, 600);
        frame.setVisible(true);

        try {

            List<Long> memoryReadings = new ArrayList<>();
            Runtime runtime = Runtime.getRuntime();
            LogWindowSource logSource = Logger.getDefaultLogSource();

            List<LogWindow> windows = new ArrayList<>();
            for (int i = 0; i < WINDOW_COUNT; i++) {
                LogWindow window = new LogWindow(logSource);
                windows.add(window);
                desktop.add(window);
                window.setVisible(true);
                window.setLocation(i * 50, i * 50);
            }

            AtomicInteger notificationCounter = new AtomicInteger(0);
            LogChangeListener countingListener = notificationCounter::incrementAndGet;
            logSource.registerListener(countingListener);

            System.gc();
            Thread.sleep(1000);
            long baselineMemory = getUsedMemory(runtime);
            memoryReadings.add(baselineMemory);

            for (int phase = 0; phase < PHASE_COUNT; phase++) {
                for (int i = 0; i < MESSAGE_COUNT; i++) {
                    Logger.debug("Test message " + i + " in phase " + phase);

                    if (i % 100 == 99) {
                        Thread.sleep(5);
                    }
                }

                System.gc();
                Thread.sleep(500);
                memoryReadings.add(getUsedMemory(runtime));
            }

            for (LogWindow window : windows) {
                window.dispose();
                System.gc();
                Thread.sleep(500);
                memoryReadings.add(getUsedMemory(runtime));
            }

            logSource.unregisterListener(countingListener);
            System.gc();
            Thread.sleep(1000);
            long finalMemory = getUsedMemory(runtime);
            memoryReadings.add(finalMemory);

            boolean leakDetected = analyzeMemoryPattern(memoryReadings);

            double memoryRatio = (double) finalMemory / baselineMemory;
            boolean significantResidualMemory = memoryRatio > 1.5;

            int expectedNotifications = PHASE_COUNT * MESSAGE_COUNT;
            boolean notificationsMatch = notificationCounter.get() >= expectedNotifications;

            Assert.assertFalse("Memory leak pattern detected in log message handling", leakDetected);
            Assert.assertFalse("Significant memory not released after test", significantResidualMemory);
            Assert.assertTrue("Notification count mismatch", notificationsMatch);

            Assert.assertTrue("Log not properly limited by capacity",
                    logSource.size() <= 100);
        } finally {
            frame.dispose();
        }
    }

    private long getUsedMemory(Runtime runtime) {
        return runtime.totalMemory() - runtime.freeMemory();
    }

    /**
     * Analyzes memory readings to detect leak patterns
     *
     * @return true if a memory leak pattern is detected
     */
    private boolean analyzeMemoryPattern(List<Long> memoryReadings) {
        if (memoryReadings.size() < 4) {
            return false; // Not enough data points
        }

        // Skip first reading (often includes JVM warmup)
        int steadyIncreaseCount = 0;

        // Look for steady increases across multiple readings
        for (int i = 2; i < memoryReadings.size(); i++) {
            double growthPercentage =
                    100.0 * (memoryReadings.get(i) - memoryReadings.get(i - 1)) / memoryReadings.get(i - 1);

            if (growthPercentage > MAX_GROWTH_PERCENTAGE) {
                steadyIncreaseCount++;
            }
        }

        // If we have consistently increasing memory in most phases, likely a leak
        return steadyIncreaseCount >= (memoryReadings.size() / 2);
    }
}