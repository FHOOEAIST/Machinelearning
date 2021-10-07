package science.aist.machinelearning.algorithm.clustering.kmeans;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <p>Cluster from {@link KMeansClustering}</p>
 *
 * @author Andreas Pointner
 * @since 1.0
 */
public class KMeansCluster<T> implements Cluster<T> {
    /**
     * The list of elements in this cluster
     */
    private final List<T> elements = new ArrayList<>();
    /**
     * The center of the cluster
     */
    private double[] clusterCenter;
    /**
     * The sum of the elements of the cluster in each dimension
     */
    private double[] clusterVectorSum;

    KMeansCluster() {

    }

    /**
     * gets value of field {@link KMeansCluster#clusterVectorSum} This function is package protected to only be used in
     * {@link KMeansClustering}
     *
     * @return value of field clusterVectorSum
     * @see KMeansCluster#clusterVectorSum
     */
    double[] getClusterVectorSum() {
        return clusterVectorSum;
    }

    /**
     * sets value of field {@link KMeansCluster#clusterVectorSum} This function is package protected to only be used in
     * {@link KMeansClustering}
     *
     * @param clusterVectorSum value of field clusterVectorSum
     * @see KMeansCluster#clusterVectorSum
     */
    void setClusterVectorSum(double[] clusterVectorSum) {
        this.clusterVectorSum = clusterVectorSum;
    }

    /**
     * returns a modifiable version of elements This function is package protected to only be used in {@link
     * KMeansClustering}
     *
     * @return value of field elements
     * @see KMeansCluster#elements
     */
    List<T> getElementsModfi() {
        return elements;
    }

    /**
     * gets value of field {@link KMeansCluster#clusterCenter} package protected function, as it should just be used in
     * {@link KMeansClustering}
     *
     * @return value of field clusterCenter
     * @see KMeansCluster#clusterCenter
     */
    @Override
    public double[] getClusterCenter() {
        return clusterCenter;
    }

    /**
     * sets value of field {@link KMeansCluster#clusterCenter} This function is package protected to only be used in
     * {@link KMeansClustering}
     *
     * @param clusterCenter value of field clusterCenter
     * @see KMeansCluster#clusterCenter
     */
    void setClusterCenter(double[] clusterCenter) {
        this.clusterCenter = clusterCenter;
    }

    /**
     * gets value of field {@link KMeansCluster#elements}
     *
     * @return value of field elements
     * @see KMeansCluster#elements
     */
    @Override
    public List<T> getElements() {
        return Collections.unmodifiableList(elements);
    }
}
