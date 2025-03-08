package gui.system.closing;


import org.junit.Before;
import org.junit.Test;
import org.junit.Assume;

import javax.swing.*;
import javax.swing.event.InternalFrameEvent;
import java.awt.GraphicsEnvironment;

import static org.junit.Assert.*;

public class CloseConfirmationTest {

    private JDesktopPane desktop;
    private JInternalFrame testFrame;
    private boolean confirmationCalled;

    class TestClosingStrategy implements ClosingStrategy {
        private final boolean shouldClose;

        public TestClosingStrategy(boolean shouldClose) {
            this.shouldClose = shouldClose;
        }

        @Override
        public boolean confirmClosing(JInternalFrame frame) {
            confirmationCalled = true;
            return shouldClose;
        }
    }

    @Before
    public void setUp() {
        Assume.assumeFalse(GraphicsEnvironment.isHeadless());

        desktop = new JDesktopPane();
        testFrame = new JInternalFrame("Test Frame", true, true, true, true);
        confirmationCalled = false;
    }

    @Test
    public void testClosingConfirmed() {
        CloseConfirmationDecorator.addCloseConfirmation(testFrame, new TestClosingStrategy(true));
        desktop.add(testFrame);
        testFrame.setVisible(true);

        InternalFrameEvent event = new InternalFrameEvent(testFrame, InternalFrameEvent.INTERNAL_FRAME_CLOSING);
        testFrame.getInternalFrameListeners()[0].internalFrameClosing(event);

        assertTrue("Должен вызываться метод подтверждения", confirmationCalled);
        assertFalse("Окно должно быть закрыто", testFrame.isVisible());
    }

    @Test
    public void testClosingCancelled() {
        CloseConfirmationDecorator.addCloseConfirmation(testFrame, new TestClosingStrategy(false));
        desktop.add(testFrame);
        testFrame.setVisible(true);

        InternalFrameEvent event = new InternalFrameEvent(testFrame, InternalFrameEvent.INTERNAL_FRAME_CLOSING);
        testFrame.getInternalFrameListeners()[0].internalFrameClosing(event);

        assertTrue("Должен вызываться метод подтверждения", confirmationCalled);
        assertTrue("Окно не должно быть закрыто", testFrame.isVisible());
    }
}
