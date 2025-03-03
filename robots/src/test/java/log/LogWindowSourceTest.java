package log;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

public class LogWindowSourceTest {

    private LogWindowSource logSource;
    private static final int QUEUE_LENGTH = 5;

    @Before
    public void setUp() {
        logSource = new LogWindowSource(QUEUE_LENGTH);
    }

    @Test
    public void testInitiallyEmpty() {
        assertEquals(0, logSource.size());
        assertTrue(logSource.all().iterator().hasNext() == false);
    }

    @Test
    public void testAppendAndRetrieveSingleMessage() {
        String testMessage = "Test message";
        logSource.append(LogLevel.Debug, testMessage);

        assertEquals(1, logSource.size());

        Iterator<LogEntry> iterator = logSource.all().iterator();
        assertTrue(iterator.hasNext());
        LogEntry entry = iterator.next();
        assertEquals(LogLevel.Debug, entry.getLevel());
        assertEquals(testMessage, entry.getMessage());
        assertFalse(iterator.hasNext());
    }

    @Test
    public void testQueueLimitEnforcement() {
        // Add more messages than queue can hold
        for (int i = 0; i < QUEUE_LENGTH + 3; i++) {
            logSource.append(LogLevel.Debug, "Message " + i);
        }

        // Should only have QUEUE_LENGTH messages
        assertEquals(QUEUE_LENGTH, logSource.size());

        // First message should be "Message 3" (oldest messages removed)
        String firstMessage = logSource.all().iterator().next().getMessage();
        assertEquals("Message 3", firstMessage);
    }

    @Test
    public void testRangeRetrieval() {
        // Add 5 messages
        for (int i = 0; i < 5; i++) {
            logSource.append(LogLevel.Debug, "Message " + i);
        }

        // Get range from index 1, count 2
        Iterable<LogEntry> range = logSource.range(1, 2);
        List<LogEntry> entries = new ArrayList<>();
        range.forEach(entries::add);

        assertEquals(2, entries.size());
        assertEquals("Message 1", entries.get(0).getMessage());
        assertEquals("Message 2", entries.get(1).getMessage());
    }

    @Test
    public void testInvalidRanges() {
        // Add a few messages
        logSource.append(LogLevel.Debug, "Message 1");
        logSource.append(LogLevel.Debug, "Message 2");

        // Test negative start
        assertTrue(!logSource.range(-1, 1).iterator().hasNext());

        // Test start beyond size
        assertTrue(!logSource.range(5, 1).iterator().hasNext());
    }

    @Test
    public void testClearMethod() {
        // Add a few messages
        logSource.append(LogLevel.Debug, "Message 1");
        logSource.append(LogLevel.Debug, "Message 2");

        assertEquals(2, logSource.size());

        logSource.clear();
        assertEquals(0, logSource.size());
        assertFalse(logSource.all().iterator().hasNext());
    }

    @Test
    public void testRegisterAndUnregisterListener() {
        final AtomicInteger notificationCount = new AtomicInteger(0);

        LogChangeListener listener = () -> notificationCount.incrementAndGet();

        // Register listener and append message
        logSource.registerListener(listener);
        logSource.append(LogLevel.Debug, "Test");
        assertEquals(1, notificationCount.get());

        // Append another message
        logSource.append(LogLevel.Debug, "Test 2");
        assertEquals(2, notificationCount.get());

        // Unregister listener and verify no more notifications
        logSource.unregisterListener(listener);
        logSource.append(LogLevel.Debug, "Test 3");
        assertEquals(2, notificationCount.get());
    }

    @Test
    public void testMultipleListeners() {
        final AtomicInteger listenerCount1 = new AtomicInteger(0);
        final AtomicInteger listenerCount2 = new AtomicInteger(0);

        LogChangeListener listener1 = () -> listenerCount1.incrementAndGet();
        LogChangeListener listener2 = () -> listenerCount2.incrementAndGet();

        logSource.registerListener(listener1);
        logSource.registerListener(listener2);

        logSource.append(LogLevel.Debug, "Test");

        assertEquals(1, listenerCount1.get());
        assertEquals(1, listenerCount2.get());
    }

    @Test
    public void testConcurrentAccess() throws InterruptedException {
        int numThreads = 10;
        int messagesPerThread = 100;
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        CountDownLatch latch = new CountDownLatch(numThreads);

        for (int i = 0; i < numThreads; i++) {
            final int threadId = i;
            executor.submit(() -> {
                try {
                    for (int j = 0; j < messagesPerThread; j++) {
                        logSource.append(LogLevel.Debug, "Thread " + threadId + " message " + j);
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(10, TimeUnit.SECONDS);
        executor.shutdown();

        // Should have exactly QUEUE_LENGTH messages due to limit
        assertEquals(QUEUE_LENGTH, logSource.size());
    }

    @Test
    public void testDefensiveCopying() {
        // Add messages
        logSource.append(LogLevel.Debug, "Message 1");
        logSource.append(LogLevel.Debug, "Message 2");

        // Get all entries
        Iterable<LogEntry> allEntries = logSource.all();

        // Try to modify the collection (should fail or not affect original)
        try {
            if (allEntries instanceof List) {
                ((List<LogEntry>) allEntries).clear();
            }
        } catch (UnsupportedOperationException e) {
            // This is also acceptable
        }

        // Original collection should be unchanged
        assertEquals(2, logSource.size());
    }
}