package science.aist.machinelearning.algorithm.clustering.kmeans.distance;

import science.aist.machinelearning.algorithm.clustering.kmeans.VectorDistance;

/**
 * <p>Calculates the euclidean distance between two vectors</p>
 *
 * @author Andreas Pointner
 * @since 1.0
 */
public class EuclideanVectorDistance implements VectorDistance {
    /**
     * Euclidean distance is just the square root of the squared euclidean distance, so we use this function as a
     * starting point
     */
    private final EuclideanSquaredVectorDistance euclideanSquaredVectorDistance = new EuclideanSquaredVectorDistance();

    @Override
    public double calculateDistance(double[] vector1, double[] vector2) {
        return Math.sqrt(euclideanSquaredVectorDistance.calculateDistance(vector1, vector2));
    }
}
