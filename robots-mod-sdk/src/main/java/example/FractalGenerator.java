package example;

import example.transformation.Transformation;
import example.transformation.afin.AfinCompose;
import example.transformation.afin.AfinTransformation;
import example.transformation.nonlinear.variations.HeartTransformation;
import example.transformation.nonlinear.variations.PolarTransformation;
import example.transformation.nonlinear.variations.SinusoidalTransformation;
import example.transformation.nonlinear.variations.SphericalTransformation;
import log.WindowLogger;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class FractalGenerator {
    private final BlockingQueue<KeyEvent> eventQueue = new LinkedBlockingQueue<>();
    private final ConcurrentHashMap<Long, ColoredPoint> fractalPoints = new ConcurrentHashMap<>();
    private final AtomicBoolean isGenerating = new AtomicBoolean(false);
    private final SecureRandom random = new SecureRandom();
    private final AfinCompose afinCompose;
    private Transformation currentTransformation; // Single active transformation
    private final List<ColoredPoint> newPoints = new ArrayList<>(); // Track new points
    private final Object newPointsLock = new Object(); // Lock for thread safety


    private static final Transformation[] TRANSFORMATIONS = new Transformation[]{
            new HeartTransformation(),
            new PolarTransformation(),
            new SinusoidalTransformation(),
            new SphericalTransformation()
    };

    private int symmetry = random.nextInt(0, 9);
    private int currentPointLimit = 2000; // Starting small
    private int maxPoints = 5000000;
    private int fractalType = random.nextInt(0, TRANSFORMATIONS.length); // Current fractal type

    private int growthFactor = 120;
    private int interactionCount = 0;


    public record ColoredPoint(double x, double y, Color color, int hitCount) {
    }


    public FractalGenerator() {
        // Initialize transformations
        afinCompose = new AfinCompose(8); // Fewer affine transformations for cleaner look

        // Start with the first transformation
        selectNextFractalType();

        // Start event processing thread
        new Thread(this::processEvents).start();
    }

    private void selectNextFractalType() {
        fractalType = (fractalType + 1) % TRANSFORMATIONS.length;
        currentTransformation = TRANSFORMATIONS[fractalType];
        WindowLogger.debug("[MOD] Selected fractal type: " + currentTransformation.getClass().getSimpleName());
    }

    public void addEvent(KeyEvent event) {
        eventQueue.offer(event);

        // Change symmetry with number keys
        if (event.getKeyCode() >= KeyEvent.VK_1 && event.getKeyCode() <= KeyEvent.VK_9) {
            symmetry = event.getKeyCode() - KeyEvent.VK_0;
            WindowLogger.debug("[MOD] Changed symmetry to " + symmetry);
            clearAndRegeneratePoints();
        }

        // Change fractal type with F key
        if (event.getKeyCode() == KeyEvent.VK_F) {
            selectNextFractalType();
            clearAndRegeneratePoints();
            currentPointLimit = 100; // Reset point count
            interactionCount = 0;
            growthFactor = 500;
        }

        // Clear with C key
        if (event.getKeyCode() == KeyEvent.VK_C) {
            clearAndRegeneratePoints();
            currentPointLimit = 100;
            interactionCount = 0;
            growthFactor = 500;
        }
    }


    private void clearAndRegeneratePoints() {
        fractalPoints.clear();
        WindowLogger.debug("[MOD] Cleared fractal points");
    }

    private void processEvents() {
        WindowLogger.debug("[MOD] FractalGenerator thread started");
        while (!Thread.currentThread().isInterrupted()) {
            try {
                KeyEvent event = eventQueue.take();
                WindowLogger.debug("[MOD] Received key event: " + KeyEvent.getKeyText(event.getKeyCode()));

                // Only grow fractal if not a special key
                if (event.getKeyCode() < KeyEvent.VK_1 || event.getKeyCode() > KeyEvent.VK_9) {
                    if (event.getKeyCode() != KeyEvent.VK_C && event.getKeyCode() != KeyEvent.VK_F) {
                        growFractal();
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private void growFractal() {
        if (!isGenerating.compareAndSet(false, true)) {
            return;
        }

        try {
            interactionCount++;

            // Calculate growth factor with more smooth scaling
            if (interactionCount <= 8) {
                growthFactor *= 5; // Faster initial growth
            } else {
                growthFactor = (int) (growthFactor * 1.2); // Slower later growth
            }

            growthFactor = Math.max(15, growthFactor);
            growthFactor = Math.min(growthFactor, 1212); // Cap growth factor

            currentPointLimit += growthFactor;
            currentPointLimit = Math.min(currentPointLimit, maxPoints);

            WindowLogger.debug("[MOD] Growing fractal to " + currentPointLimit + " points");

            generateMorePoints(growthFactor);

        } finally {
            isGenerating.set(false);
        }
    }

    private void generateMorePoints(int count) {
        // Start with a consistent seed point for more coherent fractals
        double x = 0.1;
        double y = 0.1;
        Point point = new Point(x, y);

        // Skip initial transient points
        for (int i = 0; i < 20; i++) {
            AfinTransformation afinTransform = afinCompose.getRandomAfin();
            point = afinTransform.apply(point);
            point = currentTransformation.apply(point); // Use current transformation
        }

        // Generate the new batch of points
        for (int i = 0; i < count && fractalPoints.size() < currentPointLimit; i++) {
            AfinTransformation afinTransform = afinCompose.getRandomAfin();
            Color color = afinTransform.getColor();

            point = afinTransform.apply(point);
            point = currentTransformation.apply(point); // Use current transformation

            // Add points only if they're within bounds
            if (Math.abs(point.x()) < 2.5 && Math.abs(point.y()) < 2.5) {
                applySymmetry(point, color);
            }
        }

        WindowLogger.debug("[MOD] Fractal now has " + fractalPoints.size() + " points");
    }

    private void applySymmetry(Point point, Color baseColor) {
        for (int s = 0; s < Math.max(1, symmetry); s++) {
            double theta = 2 * Math.PI * s / Math.max(1, symmetry);

            double xRot = point.x() * Math.cos(theta) - point.y() * Math.sin(theta);
            double yRot = point.x() * Math.sin(theta) + point.y() * Math.cos(theta);

            long hash = (long) (xRot * 1000000) + (long) (yRot * 1000);
            ColoredPoint existing = fractalPoints.get(hash);
            int hits = existing == null ? 1 : existing.hitCount() + 1;
            Color color = getColorForHitCount(hits, baseColor);

            ColoredPoint newPoint = new ColoredPoint(xRot, yRot, color, hits);
            fractalPoints.put(hash, newPoint);

            // Add to new points list
            synchronized (newPointsLock) {
                newPoints.add(newPoint);
            }
        }
    }

    private Color getColorForHitCount(int hitCount, Color baseColor) {
        float intensity = Math.min(1.0f, (float) Math.log1p(hitCount) / 4.0f);
        float[] hsb = new float[3];
        Color.RGBtoHSB(baseColor.getRed(), baseColor.getGreen(), baseColor.getBlue(), hsb);
        float newSat = Math.min(1.0f, hsb[1] + intensity * 0.3f);
        float newBri = Math.min(1.0f, 0.3f + intensity * 0.7f);
        return Color.getHSBColor(hsb[0], newSat, newBri);
    }

    public List<ColoredPoint> getFractalPoints() {
        return new ArrayList<>(fractalPoints.values());
    }

    public void shutdown() {
        Thread.currentThread().interrupt();
    }

    public List<ColoredPoint> getNewPoints() {
        synchronized (newPointsLock) {
            List<ColoredPoint> points = new ArrayList<>(newPoints);
            newPoints.clear();
            return points;
        }
    }

    // For total count display
    public int getTotalPointCount() {
        return fractalPoints.size();
    }
}