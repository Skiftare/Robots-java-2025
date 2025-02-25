package gui;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.awt.TextArea;
import java.util.List;

import log.LogChangeListener;
import log.LogEntry;
import log.LogWindowSource;

public class LogWindowTest {

    private LogWindow logWindow;
    private LogWindowSource mockLogSource;
    private TextArea mockLogContent;

    @BeforeEach
    public void setUp() {
        // Create a mock LogWindowSource to simulate the log source
        mockLogSource = Mockito.mock(LogWindowSource.class);

        // Create the LogWindow instance with the mocked source
        logWindow = new LogWindow(mockLogSource);

        // Mock the TextArea object to avoid the GUI's real components
        mockLogContent = Mockito.mock(TextArea.class);
        logWindow.m_logContent = mockLogContent;
    }

    @Test
    public void testLogWindowInitialization() {
        // Test that the LogWindow is initialized correctly
        assertNotNull(logWindow);
        assertEquals("Протокол работы", logWindow.getTitle());
        assertEquals(400, logWindow.getWidth());
        assertEquals(600, logWindow.getHeight());
        assertNotNull(logWindow.m_logSource);
        assertNotNull(logWindow.m_logContent);
    }

    @Test
    public void testUpdateLogContentWithNoEntries() {
        // Test that the content is updated correctly when there are no log entries
        Mockito.when(mockLogSource.all()).thenReturn(List.of(new LogEntry[0]));

        logWindow.updateLogContent();

        // Verify that the log content text area is set to empty
        Mockito.verify(mockLogContent).setText("");
    }

    @Test
    public void testUpdateLogContentWithEntries() {
        // Test that the content is updated correctly with log entries
        LogEntry entry1 = Mockito.mock(LogEntry.class);
        LogEntry entry2 = Mockito.mock(LogEntry.class);
        Mockito.when(entry1.getMessage()).thenReturn("Log entry 1");
        Mockito.when(entry2.getMessage()).thenReturn("Log entry 2");
        Mockito.when(mockLogSource.all()).thenReturn(List.of(new LogEntry[]{entry1, entry2}));

        logWindow.updateLogContent();

        // Verify that the content of the TextArea is updated with the expected text
        String expectedText = "Log entry 1\nLog entry 2\n";
        Mockito.verify(mockLogContent).setText(expectedText);
    }

    @Test
    public void testOnLogChanged() {
        // Test that the onLogChanged method calls updateLogContent
        LogChangeListener listener = logWindow;
        listener.onLogChanged();

        // Verify that updateLogContent is invoked when the log changes
        Mockito.verify(logWindow).updateLogContent();
    }

    @Test
    public void testLogSourceListenerRegistration() {
        // Verify that the LogWindow registers itself as a listener with the LogWindowSource
        Mockito.verify(mockLogSource).registerListener(logWindow);
    }
}
