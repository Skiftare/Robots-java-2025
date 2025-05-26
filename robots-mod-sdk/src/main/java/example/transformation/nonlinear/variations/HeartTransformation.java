package example.transformation.nonlinear.variations;


import example.Point;
import example.transformation.Transformation;

import static java.lang.Math.*;

public class HeartTransformation implements Transformation {

    @Override
    public Point apply(Point point) {
        double x = point.x();
        double y = point.y();
        double r = sqrt(x * x + y * y);
        double theta = atan2(y, x);
        double newX = r * sin(theta * r);
        double newY = -r * cos(theta * r);

        return new Point(newX, newY);
    }
}
