package example.transformation.nonlinear.variations;


import example.Point;
import example.transformation.Transformation;

public class SinusoidalTransformation implements Transformation {
    @Override
    public Point apply(Point point) {
        double x = Math.sin(point.x());
        double y = Math.sin(point.y());
        return new Point(x, y);
    }

}
