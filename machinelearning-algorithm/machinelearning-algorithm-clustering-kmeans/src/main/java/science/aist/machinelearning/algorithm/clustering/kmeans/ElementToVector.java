package science.aist.machinelearning.algorithm.clustering.kmeans;

/**
 * <p>Maps a given element to a multidimensional vector space</p>
 *
 * @author Andreas Pointner
 * @since 1.0
 */
public interface ElementToVector<T> {
    /**
     * Maps the given element to a vector
     *
     * @param element the element to be mapped
     * @return the vector
     */
    double[] mapElementToVector(T element);
}
