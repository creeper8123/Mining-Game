import java.util.Random;

public class GradientNoise {
    public static class Perlin1D{
        public double[] values;
        public boolean wrapAround;
        public Random random = new Random();
        public Long seed;
        public Perlin1D(int numberOfIndexes, Long seed , boolean wrap){
            if(seed == null){
                seed = new Random().nextLong();
            }
            this.seed = seed;
            random.setSeed(seed);
            wrapAround = wrap;
            values = new double[numberOfIndexes];
            if(wrap){
                for (int i = 0; i < values.length-1; i++) {
                    values[i] = random.nextDouble();
                }
                values[values.length-1] = values[0];
            }else{
                for (int i = 0; i < values.length; i++) {
                    values[i] = random.nextDouble();
                }
            }

        }

        public double linInterpolation(double index){
            double low = values[(int) Math.floor(index)];
            double high = values[(int) Math.ceil(index)];
            index = index % 1;
            return low * (1 - index) + high * index;
        }

        public double cosInterpolation(double index){
            double low = values[(int) Math.floor(index)];
            double high = values[(int) Math.ceil(index)];
            index = index % 1;
            index = (1 - Math.cos(index * Math.PI)) * 0.5;
            return low * (1 - index) + high * index;
        }
    }
}
