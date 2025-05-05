// Файл: src/main/java/gui/system/sound/SoundManager.java
package gui.system.sound;

import javax.sound.sampled.*;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Загрузка и воспроизведение звуков перемещения, смерти и победы.
 * Взяты пулы клипов, чтобы сразу могло играть несколько звуков подряд.
 */
public class SoundManager {
    private static final int POOL_SIZE = 5;

    private static final List<Clip> moveClips  = new ArrayList<>(POOL_SIZE);
    private static final List<Clip> deathClips = new ArrayList<>(POOL_SIZE);
    private static final List<Clip> winClips   = new ArrayList<>(POOL_SIZE);

    private static final AtomicInteger moveIndex  = new AtomicInteger(0);
    private static final AtomicInteger deathIndex = new AtomicInteger(0);
    private static final AtomicInteger winIndex   = new AtomicInteger(0);

    static {
        for (int i = 0; i < POOL_SIZE; i++) {
            moveClips.add  (loadClip("sounds/move.wav"));
            deathClips.add (loadClip("sounds/death.wav"));
            winClips.add   (loadClip("sounds/congrats.wav"));
        }
    }

    private static Clip loadClip(String path) {
        try {
            // Ищем ресурс в корне classpath
            URL url = SoundManager.class.getClassLoader().getResource(path);
            if (url == null) {
                System.err.println("[SoundManager] resource not found: " + path);
                return null;
            }
            AudioInputStream ais = AudioSystem.getAudioInputStream(url);
            Clip clip = AudioSystem.getClip();
            clip.open(ais);
            return clip;
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.err.println("[SoundManager] failed to load: " + path);
            e.printStackTrace();
            return null;
        }
    }

    private static void playFromPool(List<Clip> pool, AtomicInteger idx) {
        if (pool.isEmpty()) return;
        Clip clip = pool.get(idx.getAndUpdate(i -> (i + 1) % pool.size()));
        if (clip == null) return;
        if (clip.isRunning()) clip.stop();
        clip.setFramePosition(0);
        clip.start();
    }

    /** Звук шага */
    public static void playMove()  { playFromPool(moveClips,  moveIndex);  }

    /** Звук гибели */
    public static void playDeath() { playFromPool(deathClips, deathIndex); }

    /** Звук победы */
    public static void playWin()   { playFromPool(winClips,   winIndex);   }
}
