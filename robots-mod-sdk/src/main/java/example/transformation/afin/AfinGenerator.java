package example.transformation.afin;

import java.awt.*;
import java.security.SecureRandom;

class AfinGenerator {
    private final static int UPPER_BOUND_FOR_COLOR = 255;
    private final static double UPPER_BOUND_FOR_RAND_COEFF = 1.5;
    private final static double LOWER_BOUND_FOR_RAND_COEFF = -1.5;
    private final static double UPPER_BOUND_FOR_FREE_COEFF = 2;
    private final static double LOWER_BOUND_FOR_FREE_COEFF = -2;
    private final SecureRandom secureRandom = new SecureRandom();

    AfinTransformation generateWithGoodCoeff(Color col) {
        double a;
        double b;
        double c;
        double d;
        double e;
        double f;
        do {

            do {
                a = secureRandom.nextDouble();
                d = secureRandom.nextDouble(a * a, 1);
                if (secureRandom.nextBoolean()) {
                    d = -d;
                }
            } while ((a * a + d * d) > 1);

            do {
                b = secureRandom.nextDouble();
                e = secureRandom.nextDouble(b * b, 1);
                if (secureRandom.nextBoolean()) {
                    e = -e;
                }
            } while ((b * b + e * e) > 1);

        } while ((a * a + b * b + d * d + e * e) > (1 + (a * e - d * b) * (a * e - d * b)));

        c = secureRandom.nextDouble(LOWER_BOUND_FOR_FREE_COEFF, UPPER_BOUND_FOR_FREE_COEFF);
        f = secureRandom.nextDouble(LOWER_BOUND_FOR_FREE_COEFF, UPPER_BOUND_FOR_FREE_COEFF);
        return new AfinTransformation(a, b, c, d, e, f, col);
    }

    AfinTransformation generateWithReallyRandomCoeff(Color col) {
        double a;
        double b;
        double c;
        double d;
        double e;
        double f;

        a = secureRandom.nextDouble(LOWER_BOUND_FOR_RAND_COEFF, UPPER_BOUND_FOR_RAND_COEFF);
        b = secureRandom.nextDouble(LOWER_BOUND_FOR_RAND_COEFF, UPPER_BOUND_FOR_RAND_COEFF);
        c = secureRandom.nextDouble(LOWER_BOUND_FOR_FREE_COEFF, UPPER_BOUND_FOR_FREE_COEFF);
        d = secureRandom.nextDouble(LOWER_BOUND_FOR_RAND_COEFF, UPPER_BOUND_FOR_RAND_COEFF);
        e = secureRandom.nextDouble(LOWER_BOUND_FOR_RAND_COEFF, UPPER_BOUND_FOR_RAND_COEFF);
        f = secureRandom.nextDouble(LOWER_BOUND_FOR_FREE_COEFF, UPPER_BOUND_FOR_FREE_COEFF);

        return new AfinTransformation(a, b, c, d, e, f, col);
    }

    AfinTransformation generateAfin() {
        Color randomColor;

        int red = secureRandom.nextInt(UPPER_BOUND_FOR_COLOR);
        int green = secureRandom.nextInt(UPPER_BOUND_FOR_COLOR);
        int blue = secureRandom.nextInt(UPPER_BOUND_FOR_COLOR);
        randomColor = new Color(red, green, blue);

        if (secureRandom.nextBoolean()) {
            return generateWithGoodCoeff(randomColor);
        } else {
            return generateWithReallyRandomCoeff(randomColor);
        }

    }
}
