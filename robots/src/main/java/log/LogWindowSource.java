package log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.WeakHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class LogWindowSource {
    private final int m_iQueueLength;
    private final ArrayList<LogEntry> m_messages;
    private final CopyOnWriteArrayList<LogChangeListener> m_listeners;

    public LogWindowSource(int iQueueLength) {
        m_iQueueLength = iQueueLength;
        m_messages = new ArrayList<>(iQueueLength);
        m_listeners = new CopyOnWriteArrayList<>();
    }

    public synchronized void registerListener(LogChangeListener listener) {
        if (listener != null) {
            m_listeners.addIfAbsent(listener);
        }
    }

    public synchronized void unregisterListener(LogChangeListener listener) {
        if (listener != null) {
            m_listeners.remove(listener);
        }
    }

    public void append(LogLevel logLevel, String strMessage) {
        synchronized (m_messages) {
            LogEntry entry = new LogEntry(logLevel, strMessage);

            // If queue is full, remove the oldest message
            if (m_messages.size() >= m_iQueueLength) {
                unregisterListener(m_listeners.get(0));
                m_messages.remove(0);
            }
            m_messages.add(entry);
        }

        // Notify listeners
        for (LogChangeListener listener : m_listeners) {
            if (listener != null) {
                try {
                    listener.onLogChanged();
                } catch (Exception e) {
                    //Вычёркиваем, всё равно уже слушатель мёртв.
                    m_listeners.remove(listener);
                }
            }
        }
    }

    public int size() {
        synchronized (m_messages) {
            return m_messages.size();
        }
    }

    public Iterable<LogEntry> range(int startFrom, int count) {
        synchronized (m_messages) {
            if (startFrom < 0 || startFrom >= m_messages.size()) {
                return Collections.emptyList();
            }
            int indexTo = Math.min(startFrom + count, m_messages.size());
            return new ArrayList<>(m_messages.subList(startFrom, indexTo));
        }
    }

    public Iterable<LogEntry> all() {
        synchronized (m_messages) {
            return new ArrayList<>(m_messages);
        }
    }
}