package science.aist.machinelearning.algorithm.clustering.kmeans;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import science.aist.machinelearning.core.*;
import science.aist.machinelearning.core.analytics.Analytics;
import science.aist.machinelearning.core.options.Descriptor;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <p>Kmeans Clustering</p>
 *
 * @author Andreas Pointner
 * @since 1.0
 */
public class KMeansClustering<T> implements Algorithm<Cluster<T>, T> {
    /**
     * Logger
     */
    private final Logger logger = LoggerFactory.getLogger(getClass());
    /**
     * Random function to pick initial seeds for the clusters
     */
    private final Random random = new Random(42);
    /**
     * Reference to analytics
     */
    private Analytics analytics;
    /**
     * Number of Clusters to be created
     */
    private int numberOfClusters;
    /**
     * Map the element to a double vector
     */
    private ElementToVector<T> elementToVector;
    /**
     * the function with which the distance between two vectors is calculated
     */
    private VectorDistance vectorDistance;
    /**
     * How many elements relative to all elements have to change until the algorithm converges
     */
    private double epsilon = 0.01;

    /**
     * How many maxIterations are maximal allowed before the algorithm stops
     */
    private int maxIterations = 25;

    @Override
    public Solution<Cluster<T>, T> solve(Problem<T> problem) {
        return solve(problem, null);
    }

    @Override
    public Solution<Cluster<T>, T> solve(Problem<T> problem, Solution<Cluster<T>, T> bestSolution) {
        if (analytics != null) analytics.startAnalytics();
        if (bestSolution != null) {
            logger.warn("Best Solution is ignored in current implementation");
        }
        if (analytics != null) analytics.logProblem(problem);


        List<T> elements = problem
                .getProblemGenes()
                .stream()
                .map(Gene::getGene)
                .collect(Collectors.toList());

        Map<T, double[]> elementVectorCache = elements
                .stream()
                .collect(Collectors.toMap(Function.identity(), elementToVector::mapElementToVector));

        int size = elements.size();

        List<ElementInCluster> elementInClusters = new ArrayList<>();
        List<KMeansCluster<T>> result = new ArrayList<>();

        // First iteration assign them all to a cluster
        // Create the clusters with random initial seed
        for (int i = 0; i < Math.min(numberOfClusters, size); i++) {
            int elemSize = elements.size();
            T element = elements.remove(elemSize > 1 ? random.nextInt(elemSize - 1) : 0);
            double[] elementVector = elementVectorCache.get(element);
            KMeansCluster<T> cluster = createCluster(element, elementVector);
            result.add(cluster);
            elementInClusters.add(new ElementInCluster(element, cluster));
        }

        // Assign the remaining elements to the clusters
        for (T element : elements) {
            double[] elementVector = elementVectorCache.get(element);
            result.stream()
                    .min(Comparator.comparingDouble(c -> vectorDistance.calculateDistance(c.getClusterCenter(), elementVector)))
                    .ifPresent(c -> {
                        addElementToCluster(c, element, elementVector);
                        elementInClusters.add(new ElementInCluster(element, c));
                    });
        }

        int iteration = 1;
        AtomicInteger changes = new AtomicInteger(0);
        double delta = 1;
        // Next maxIterations, iterate again over the elements and check if they need to be reassigned.
        while (delta > this.epsilon && iteration < this.maxIterations) {
            changes.set(0);
            for (ElementInCluster elementInCluster : elementInClusters) {
                T element = elementInCluster.element;
                double[] elementVector = elementVectorCache.get(element);
                result.stream()
                        .min(Comparator.comparingDouble(c -> vectorDistance.calculateDistance(c.getClusterCenter(), elementVector)))
                        .filter(c -> c != elementInCluster.cluster)
                        .ifPresent(c -> {
                            removeElementFromCluster(elementInCluster.cluster, element, elementVector);
                            addElementToCluster(c, element, elementVector);
                            elementInCluster.cluster = c;
                            changes.getAndIncrement();
                        });
            }
            delta = (double) changes.get() / (double) size;
            iteration++;
            logger.debug("Iteration: {} [MaxIterations: {}]", iteration, maxIterations);
            logger.debug("Current epsilon: {} [Epsilon: {}]", delta, epsilon);
        }

        // Create the solution
        Solution<Cluster<T>, T> solution = new Solution<>();
        solution.setSolutionGenes(result
                .stream()
                .map((Function<Cluster<T>, SolutionGene<Cluster<T>, T>>) SolutionGene::new)
                .collect(Collectors.toList())
        );

        if (analytics != null) analytics.logSolution(solution);
        if (analytics != null) analytics.finishAnalytics();

        return solution;
    }

    /**
     * gets value of field {@link KMeansClustering#analytics}
     *
     * @return value of field analytics
     * @see KMeansClustering#analytics
     */
    @Override
    public Analytics getAnalytics() {
        return analytics;
    }

    /**
     * sets value of field {@link KMeansClustering#analytics}
     *
     * @param analytics value of field analytics
     * @see KMeansClustering#analytics
     */
    @Override
    public void setAnalytics(Analytics analytics) {
        this.analytics = analytics;
    }

    @Override
    public Map<String, Descriptor> getOptions() {
        return Stream
                .of(
                        Pair.of("numberOfClusters", getNumberOfClusters()),
                        Pair.of("elementToVector", getElementToVector()),
                        Pair.of("vectorDistance", getVectorDistance())
                )
                .map(p -> Pair.of(p.getLeft(), new Descriptor<>(p.getRight())))
                .collect(Collectors.toMap(Pair::getLeft, Pair::getRight));
    }

    @Override
    public boolean setOptions(Map<String, Descriptor> options) {
        return options.entrySet()
                .stream()
                .allMatch(p -> setOption(p.getKey(), p.getValue()));
    }

    @Override
    public boolean setOption(String name, Descriptor descriptor) {
        try {
            Arrays.stream(getClass().getMethods())
                    .filter(m -> m.getName().equals("set" + name.substring(0, 1).toUpperCase() + name.substring(1)))
                    .findFirst()
                    .orElseThrow(NoSuchMethodException::new)
                    .invoke(this, descriptor.getValue());
            return true;
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            logger.warn("Could not set specified option", e);
            return false;
        }
    }

    private KMeansCluster<T> createCluster(T firstElement, double[] vectorFirstElement) {
        KMeansCluster<T> cluster = new KMeansCluster<>();
        int length = vectorFirstElement.length;
        cluster.getElementsModfi().add(firstElement);
        cluster.setClusterCenter(new double[length]);
        cluster.setClusterVectorSum(new double[length]);
        System.arraycopy(vectorFirstElement, 0, cluster.getClusterCenter(), 0, length);
        System.arraycopy(vectorFirstElement, 0, cluster.getClusterVectorSum(), 0, length);
        return cluster;
    }

    /**
     * Adds a element to the cluster
     *
     * @param element       the element to add the cluster
     * @param elementVector the vector of the element
     */
    void addElementToCluster(KMeansCluster<T> cluster, T element, double[] elementVector) {
        cluster.getElementsModfi().add(element);
        int nrOfElementsInCluster = cluster.getElementsModfi().size();
        for (int i = 0; i < cluster.getClusterVectorSum().length; i++) {
            cluster.getClusterVectorSum()[i] += elementVector[i];
            cluster.getClusterCenter()[i] = cluster.getClusterVectorSum()[i] / nrOfElementsInCluster;
        }
    }

    /**
     * Remove a element from the cluster
     *
     * @param element       the element to be removed form the cluster
     * @param elementVector the vector of the element
     */
    void removeElementFromCluster(KMeansCluster<T> cluster, T element, double[] elementVector) {
        if (!cluster.getElementsModfi().remove(element)) {
            throw new IllegalStateException("Trying to remove a element from the cluster which is not in the cluster.");
        }
        int nrOfElementsInCluster = cluster.getElementsModfi().size();
        for (int i = 0; i < cluster.getClusterVectorSum().length; i++) {
            cluster.getClusterVectorSum()[i] -= elementVector[i];
            cluster.getClusterCenter()[i] = cluster.getClusterVectorSum()[i] / nrOfElementsInCluster;
        }
    }

    /**
     * gets value of field {@link KMeansClustering#numberOfClusters}
     *
     * @return value of field numberOfClusters
     * @see KMeansClustering#numberOfClusters
     */
    public int getNumberOfClusters() {
        return numberOfClusters;
    }

    /**
     * sets value of field {@link KMeansClustering#numberOfClusters}
     *
     * @param numberOfClusters value of field numberOfClusters
     * @see KMeansClustering#numberOfClusters
     */
    @Required
    public void setNumberOfClusters(int numberOfClusters) {
        this.numberOfClusters = numberOfClusters;
    }

    /**
     * gets value of field {@link KMeansClustering#elementToVector}
     *
     * @return value of field elementToVector
     * @see KMeansClustering#elementToVector
     */
    public ElementToVector<T> getElementToVector() {
        return elementToVector;
    }

    /**
     * sets value of field {@link KMeansClustering#elementToVector}
     *
     * @param elementToVector value of field elementToVector
     * @see KMeansClustering#elementToVector
     */
    @Required
    public void setElementToVector(ElementToVector<T> elementToVector) {
        this.elementToVector = elementToVector;
    }

    /**
     * gets value of field {@link KMeansClustering#vectorDistance}
     *
     * @return value of field vectorDistance
     * @see KMeansClustering#vectorDistance
     */
    public VectorDistance getVectorDistance() {
        return vectorDistance;
    }

    /**
     * sets value of field {@link KMeansClustering#vectorDistance}
     *
     * @param vectorDistance value of field vectorDistance
     * @see KMeansClustering#vectorDistance
     */
    @Required
    public void setVectorDistance(VectorDistance vectorDistance) {
        this.vectorDistance = vectorDistance;
    }

    /**
     * sets {@link Random#setSeed(long)} the seed for the random selection of staring elements
     *
     * @param seed value of field seed
     * @see KMeansClustering#random
     */
    public void setSeed(int seed) {
        random.setSeed(seed);
    }

    /**
     * gets value of field {@link KMeansClustering#epsilon}
     *
     * @return value of field epsilon
     * @see KMeansClustering#epsilon
     */
    public double getEpsilon() {
        return epsilon;
    }

    /**
     * sets value of field {@link KMeansClustering#epsilon}
     *
     * @param epsilon value of field epsilon
     * @see KMeansClustering#epsilon
     */
    public void setEpsilon(double epsilon) {
        this.epsilon = epsilon;
    }

    /**
     * gets value of field {@link KMeansClustering#maxIterations}
     *
     * @return value of field maxIterations
     * @see KMeansClustering#maxIterations
     */
    public int getMaxIterations() {
        return maxIterations;
    }

    /**
     * sets value of field {@link KMeansClustering#maxIterations}
     *
     * @param maxIterations value of field maxIterations
     * @see KMeansClustering#maxIterations
     */
    public void setMaxIterations(int maxIterations) {
        this.maxIterations = maxIterations;
    }

    /**
     * Wrapper class to store which element is currently in which cluster
     */
    private class ElementInCluster {
        T element;
        KMeansCluster<T> cluster;

        ElementInCluster(T element, KMeansCluster<T> cluster) {
            this.element = element;
            this.cluster = cluster;
        }
    }
}
