package example.transformation.afin;

import java.security.SecureRandom;
import java.util.ArrayList;

public class AfinCompose {
    final SecureRandom random = new SecureRandom();
    private final ArrayList<AfinTransformation> afinMas;
    private final int n;

    public AfinCompose(int n) {
        this.n = n;
        AfinGenerator gen = new AfinGenerator();
        ArrayList<AfinTransformation> bufAfinArray = new ArrayList<>();
        for (int id = 0; id < n; id++) {
            bufAfinArray.add(gen.generateAfin());
        }
        this.afinMas = bufAfinArray;
    }

    public AfinTransformation getRandomAfin() {
        int randomNumber = random.nextInt(n);
        return afinMas.get(randomNumber);
    }

}
