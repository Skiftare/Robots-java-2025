package example;

import java.awt.event.KeyEvent;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class FractalGenerator {
    private final BlockingQueue<KeyEvent> eventQueue = new LinkedBlockingQueue<>();
    private final List<Point> fractalPoints = new ArrayList<>();
    private volatile boolean isGenerating = false;

    public FractalGenerator() {
        // Запуск потока обработки событий
        new Thread(this::processEvents).start();
    }

    public void addEvent(KeyEvent event) {
        eventQueue.offer(event);
    }

    private void processEvents() {
        while (true) {
            try {
                KeyEvent event = eventQueue.take(); // Блокирующее ожидание события
                if (!isGenerating) {
                    isGenerating = true;
                    generateFractalPoints(500); // Генерация 500 точек (можно настроить)
                    isGenerating = false;
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private void generateFractalPoints(int count) {
        fractalPoints.clear();
        SecureRandom random = new SecureRandom();
        double x = 0, y = 0;

        for (int i = 0; i < count; i++) {
            double nextX = 0.5 * x - 0.5 * y; // Пример фрактального преобразования
            double nextY = 0.5 * x + 0.5 * y;
            fractalPoints.add(new Point(nextX, nextY));
            x = nextX;
            y = nextY;
        }
    }

    public List<Point> getFractalPoints() {
        return new ArrayList<>(fractalPoints);
    }
}