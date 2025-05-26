package example.transformation.nonlinear.variations;


import example.Point;
import example.transformation.Transformation;

public class PolarTransformation implements Transformation {
    @Override
    public Point apply(Point point) {
        double x = point.x();
        double y = point.y();
        double newX = Math.atan2(y, x) / Math.PI;
        double newY = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2)) - 1.0;
        return new Point(newX, newY);
    }

}
