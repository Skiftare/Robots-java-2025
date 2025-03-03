package log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

/**
 * слушатели удаляются из списка m_listeners в методе unregisterListener
 * ограничено количество сообщений в логе величиной m_iQueueLength.
 */
public class LogWindowSource
{
    private final int m_iQueueLength;
    private final LinkedList<LogEntry> m_messages; // using LinkedList to delete old messages
    private final ArrayList<LogChangeListener> m_listeners;
    private volatile LogChangeListener[] m_activeListeners;

    public LogWindowSource(int iQueueLength)
    {
        m_iQueueLength = iQueueLength;
        m_messages = new LinkedList<LogEntry>();
        m_listeners = new ArrayList<LogChangeListener>();
    }

    public void registerListener(LogChangeListener listener)
    {
        synchronized(m_listeners)
        {
            m_listeners.add(listener);
            m_activeListeners = null;
        }
    }

    public void unregisterListener(LogChangeListener listener)
    {
        synchronized(m_listeners)
        {
            m_listeners.remove(listener);
            m_activeListeners = null;
        }
    }

    public void append(LogLevel logLevel, String strMessage)
    {
        LogEntry entry = new LogEntry(logLevel, strMessage);
        synchronized(m_messages) {
            if (m_messages.size() >= m_iQueueLength) {
                m_messages.poll();
            }
            m_messages.add(entry);
        }

        // Defining listeners
        LogChangeListener[] activeListeners = m_activeListeners;
        if (activeListeners == null)
        {
            synchronized (m_listeners)
            {
                if (m_activeListeners == null)
                {
                    activeListeners = m_listeners.toArray(new LogChangeListener[0]);
                    m_activeListeners = activeListeners;
                }
            }
        }
        assert activeListeners != null;
        for (LogChangeListener listener : activeListeners)
        {
            listener.onLogChanged();
        }
    }

    public void clear() {
        synchronized(m_messages) {
            m_messages.clear();
        }

        LogChangeListener[] activeListeners = m_activeListeners;
        if (activeListeners != null) {
            for (LogChangeListener listener : activeListeners) {
                listener.onLogChanged();
            }
        }
    }

    public int size()
    {
        synchronized(m_messages) {
            return m_messages.size();
        }
    }

    public Iterable<LogEntry> range(int startFrom, int count)
    {
        synchronized(m_messages) {
            if (startFrom < 0 || startFrom >= m_messages.size())
            {
                return Collections.emptyList();
            }
            int indexTo = Math.min(startFrom + count, m_messages.size());
            return new ArrayList<>(m_messages.subList(startFrom, indexTo));
        }
    }

    public Iterable<LogEntry> all()
    {
        synchronized(m_messages) {
            return new ArrayList<>(m_messages);
        }
    }
}