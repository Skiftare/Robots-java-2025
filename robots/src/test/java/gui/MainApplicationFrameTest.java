package gui;

import gui.system.profiling.Profile;
import gui.system.profiling.Profile.FrameState;
import gui.system.localization.LocalizationManager;
import gui.ui.GameWindow;
import gui.ui.LogWindow;
import org.junit.jupiter.api.*;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class MainApplicationFrameTest {

    private MainApplicationFrame frame;

    @BeforeEach
    void setUp() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            frame = new MainApplicationFrame();
            // Без показа фрейма нормальные bounds не сохраняются при maximize
            frame.setVisible(true);
        });
    }

    @AfterEach
    void tearDown() throws Exception {
        SwingUtilities.invokeAndWait(frame::dispose);
    }

    @Test
    void testGetGameWindow() throws Exception {
        AtomicReference<GameWindow> ref = new AtomicReference<>();
        SwingUtilities.invokeAndWait(() -> ref.set(frame.getGameWindow()));

        GameWindow gw = ref.get();
        assertNotNull(gw, "getGameWindow() не должен вернуть null");
        assertTrue(gw instanceof GameWindow, "getGameWindow() должен вернуть именно GameWindow");
    }

    @Test
    void testUpdateTitleAndLocaleChanged() throws Exception {
        AtomicReference<String> titleBefore = new AtomicReference<>();
        AtomicReference<String> titleAfter  = new AtomicReference<>();

        SwingUtilities.invokeAndWait(() -> {
            // подменим заголовок, чтобы убедиться, что updateTitle действительно меняет его
            frame.setTitle("dummy");
            titleBefore.set(frame.getTitle());

            // вызываем localeChanged (внутри — updateTitle + UI-update)
            frame.localeChanged();
            titleAfter.set(frame.getTitle());
        });

        String expected = LocalizationManager.getInstance()
                .getString("application.title");
        assertEquals("dummy",  titleBefore.get(), "До обновления заголовок должен быть dummy");
        assertEquals(expected, titleAfter.get(),  "После localeChanged() заголовок берётся из LocalizationManager");
    }

    @Test
    void testCreateProfile() throws Exception {
        AtomicReference<Profile> ref = new AtomicReference<>();
        SwingUtilities.invokeAndWait(() -> ref.set(frame.createProfile("testProfile")));
        Profile profile = ref.get();

        assertEquals("testProfile", profile.getProfileName(), "Имя профиля должно совпадать");
        // Должны быть сведения о GameWindow и LogWindow
        assertNotNull(profile.getFrameState("GameWindow"), "Нужен FrameState для GameWindow");
        assertNotNull(profile.getFrameState("LogWindow"),  "Нужен FrameState для LogWindow");

        FrameState gwState = profile.getFrameState("GameWindow");
        assertNotNull(gwState.bounds,      "bounds не должен быть null");
        assertTrue(gwState.isVisible,      "GameWindow по умолчанию видима");
        assertFalse(gwState.isIcon,        "GameWindow по умолчанию не иконизирована");
        assertFalse(gwState.isMaximum,     "GameWindow по умолчанию не максимизирована");
    }

    @Test
    void testApplyProfileWithDifferentStates() throws Exception {
        AtomicReference<GameWindow> gwRef  = new AtomicReference<>();
        AtomicReference<LogWindow>  lwRef  = new AtomicReference<>();

        SwingUtilities.invokeAndWait(() -> {
            gwRef.set(frame.getGameWindow());
            // найдём LogWindow
            for (JInternalFrame f : frame.getDesktopPane().getAllFrames()) {
                if (f instanceof LogWindow) {
                    lwRef.set((LogWindow) f);
                }
            }
        });

        GameWindow gw = gwRef.get();
        LogWindow  lw = lwRef.get();
        assertNotNull(lw, "LogWindow должен присутствовать");

        // Запомним исходные bounds
        Rectangle gwBounds = gw.getBounds();
        Rectangle lwBounds = lw.getBounds();

        // Собираем кастомный профиль
        Profile profile = new Profile("p1",
                LocalizationManager.getInstance().getCurrentLanguage().getLocale().getLanguage());
        // 1) GameWindow — невидимое, без icon/max, zOrder=1
        profile.setFrameState("GameWindow",
                new FrameState(gwBounds, false, false, false, 1));
        // 2) LogWindow — видимое, иконизированное и максимизированное, zOrder=0
        profile.setFrameState("LogWindow",
                new FrameState(lwBounds, true, true, true, 0));

        // Применяем профиль
        SwingUtilities.invokeAndWait(() -> frame.applyProfile(profile));
        // ждём таймер из applyProfile (100 мс)
        Thread.sleep(200);

        AtomicReference<Boolean> gwVisible = new AtomicReference<>();
        AtomicReference<Boolean> lwIcon    = new AtomicReference<>();
        AtomicReference<Rectangle> gwBoundsAfter = new AtomicReference<Rectangle>();

        SwingUtilities.invokeAndWait(() -> {
            gwVisible.set(gw.isVisible());
            lwIcon.set(lw.isIcon());
            gwBoundsAfter.set(gw.getBounds());
        });

        assertFalse(gwVisible.get(), "GameWindow должна стать невидимой по профилю");
        assertEquals(gwBounds, gwBoundsAfter.get(),
                "bounds GameWindow должны восстановиться из FrameState");
        assertTrue(lwIcon.get(), "LogWindow должна быть иконизирована по профилю");
    }

    @Test
    void testRestoreNormalBoundsAfterMaximizeAndIcon() throws Exception {
        AtomicReference<GameWindow> gwRef = new AtomicReference<>();
        SwingUtilities.invokeAndWait(() -> gwRef.set(frame.getGameWindow()));
        GameWindow gw = gwRef.get();
        assertNotNull(gw, "GameWindow должен быть доступен");

        // 1) Задаём произвольные bounds в оконном режиме
        Rectangle original = new Rectangle(80,  60, 320, 240);
        SwingUtilities.invokeAndWait(() -> {
            try {
                gw.setMaximum(false);
                gw.setIcon(false);
            } catch (Exception ignored) {}
            gw.setBounds(original);
        });

        // 2) Максимизируем и иконизируем
        SwingUtilities.invokeAndWait(() -> {
            try {
                gw.setMaximum(true);
                gw.setIcon(true);
            } catch (Exception e) {
                fail("Не удалось максимизировать/иконизировать окно: " + e.getMessage());
            }
        });

        // 3) Сохраняем профиль
        AtomicReference<Profile> profile = new AtomicReference<>(new Profile("p-test",
                LocalizationManager.getInstance().getCurrentLanguage().getLocale().getLanguage()));
        // При сохранении учитывается getNormalBounds()
        SwingUtilities.invokeAndWait(() -> {
            Profile created = frame.createProfile("p-test");
            profile.set(created);
        });

        // Убедимся, что в профиле хранится нормальные bounds
        FrameState st = profile.get().getFrameState("GameWindow");
        assertNotNull(st, "В профиле должен быть FrameState для GameWindow");
        assertEquals(original, st.bounds, "В профиле должны лежать оригинальные bounds");

        // 4) Применяем профиль
        SwingUtilities.invokeAndWait(() -> frame.applyProfile(profile.get()));
        // ждём таймер из applyProfile
        Thread.sleep(200);

        // 5) Снимаем maximize и icon — возвращаемся в оконный режим
        AtomicReference<Rectangle> restoredRef = new AtomicReference<>();
        SwingUtilities.invokeAndWait(() -> {
            try {
                gw.setIcon(false);
                gw.setMaximum(false);
            } catch (Exception ignored) {}
            restoredRef.set(gw.getBounds());
        });

        // 6) Проверяем
        Rectangle restored = restoredRef.get();
        assertEquals(original, restored,
                String.format("Окно должно вернуться к исходным bounds %s, но получили %s",
                        original, restored));
    }
}
