package science.aist.machinelearning.algorithm.clustering.kmeans;

import java.util.List;

/**
 * <p>Cluster that is going to be returned from a clustering algorithm</p>
 *
 * @author Andreas Pointner
 * @since 1.0
 */
public interface Cluster<T> {
    /**
     * @return the elements in the cluster
     */
    List<T> getElements();

    /**
     * @return the cluster center
     */
    double[] getClusterCenter();
}
