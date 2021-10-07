package science.aist.machinelearning.algorithm.clustering.kmeans.distance;

import science.aist.machinelearning.algorithm.clustering.kmeans.VectorDistance;

/**
 * <p>Calculates the squared Euclidean distance between two vectors</p>
 *
 * @author Andreas Pointner
 * @since 1.0
 */
public class EuclideanSquaredVectorDistance implements VectorDistance {
    @Override
    public double calculateDistance(double[] vector1, double[] vector2) {
        if (vector1.length != vector2.length) throw new IllegalArgumentException("Only supports vectors of same size");
        double distance = 0;
        for (int i = 0; i < vector1.length; i++) {
            double difference = vector1[i] - vector2[i];
            distance += difference * difference;
        }
        return distance;
    }
}
