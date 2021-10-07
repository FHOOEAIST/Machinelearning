package science.aist.machinelearning.algorithm.clustering.kmeans;

/**
 * <p>Calculate a VectorDistance between two vectors</p>
 *
 * @author Andreas Pointner
 * @since 1.0
 */
public interface VectorDistance {
    /**
     * Calculates the distance between two vectors
     *
     * @param vector1 vector1
     * @param vector2 vector2
     * @return the distance between vector 1 and vector 2
     */
    double calculateDistance(double[] vector1, double[] vector2);
}
