package gui.ui;

import gui.system.localization.LocaleChangeListener;
import gui.system.localization.LocalizationManager;
import log.LogChangeListener;
import log.LogEntry;
import log.LogWindowSource;

import javax.swing.*;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import java.awt.*;

public class LogWindow extends JInternalFrame implements LogChangeListener, LocaleChangeListener {
    private LogWindowSource m_logSource;
    private TextArea m_logContent;

    private final int width = 400;
    private final int height = 600;

    public LogWindow(LogWindowSource logSource) {
        super(LocalizationManager.getInstance().getString("log.window.title"), true, true, true, true);
        m_logSource = logSource;
        m_logSource.registerListener(this);
        m_logContent = new TextArea("");
        m_logContent.setSize(width, height);
        m_logContent.setEditable(false);
        LocalizationManager.getInstance().addListener(this);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(m_logContent, BorderLayout.CENTER);
        getContentPane().add(panel);
        pack();

        addInternalFrameListener(new InternalFrameAdapter() {
            @Override
            public void internalFrameClosed(InternalFrameEvent e) {
                unregisterFromLogSource();
            }
        });

        updateLogContent();
    }

    private void unregisterFromLogSource() {
        if (m_logSource != null) {
            m_logSource.unregisterListener(this);
            m_logSource = null;
        }
    }

    void updateLogContent() {
        if (m_logSource == null) return;

        StringBuilder content = new StringBuilder();
        for (LogEntry entry : m_logSource.all()) {
            content.append(entry.getMessage()).append("\n");
        }
        m_logContent.setText(content.toString());
        m_logContent.invalidate();
    }

    @Override
    public void onLogChanged() {
        EventQueue.invokeLater(this::updateLogContent);
    }

    @Override
    public void dispose() {
        LocalizationManager.getInstance().removeListener(this);
        unregisterFromLogSource();
        super.dispose();
    }

    @Override
    public void localeChanged() {
        this.setTitle(LocalizationManager.getInstance().getString("log.window.title"));
    }
}