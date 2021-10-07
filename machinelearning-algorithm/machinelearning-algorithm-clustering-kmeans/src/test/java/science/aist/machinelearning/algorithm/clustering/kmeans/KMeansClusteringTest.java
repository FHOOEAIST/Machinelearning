package science.aist.machinelearning.algorithm.clustering.kmeans;

import org.testng.Assert;
import org.testng.annotations.Test;
import science.aist.machinelearning.algorithm.clustering.kmeans.distance.EuclideanSquaredVectorDistance;
import science.aist.machinelearning.core.Gene;
import science.aist.machinelearning.core.Problem;
import science.aist.machinelearning.core.ProblemGene;
import science.aist.machinelearning.core.Solution;

import java.awt.geom.Point2D;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <p>Tests {@link KMeansClustering}</p>
 *
 * @author Andreas Pointner
 * @since 1.0
 */
public class KMeansClusteringTest {
    @Test
    public void testClustering2dPoints() {
        // given
        KMeansClustering<Point2D> clustering = new KMeansClustering<>();
        clustering.setNumberOfClusters(3);
        clustering.setElementToVector(p2d -> new double[]{p2d.getX(), p2d.getY()});
        clustering.setVectorDistance(new EuclideanSquaredVectorDistance());
        clustering.setSeed(1);
        clustering.setEpsilon(0.1);
        clustering.setMaxIterations(10);
        Point2D p1 = new Point2D.Double(1, 1);
        Point2D p2 = new Point2D.Double(2, 2);
        Point2D p3 = new Point2D.Double(1, 2);
        Point2D p4 = new Point2D.Double(5, 8);
        Point2D p5 = new Point2D.Double(9, 3);
        Point2D p6 = new Point2D.Double(17, 12);
        Point2D p7 = new Point2D.Double(15, 14);

        Problem<Point2D> problem = new Problem<>();
        problem.setProblemGenes(Stream
                .of(p1, p2, p3, p4, p5, p6, p7)
                .map(ProblemGene::new)
                .collect(Collectors.toList())
        );

        // when
        Solution<Cluster<Point2D>, Point2D> solution = clustering.solve(problem);

        // then
        List<Cluster<Point2D>> clusters = solution.getSolutionGenes().stream()
                .map(Gene::getGene)
                .collect(Collectors.toList());
        Assert.assertEquals(clusters.size(), 3);
        List<Point2D> cluster1 = clusters.get(0).getElements();
        List<Point2D> cluster2 = clusters.get(1).getElements();
        List<Point2D> cluster3 = clusters.get(2).getElements();
        Assert.assertTrue(cluster1.contains(p4));
        Assert.assertTrue(cluster1.contains(p5));
        Assert.assertTrue(cluster2.contains(p6));
        Assert.assertTrue(cluster2.contains(p7));
        Assert.assertTrue(cluster3.contains(p1));
        Assert.assertTrue(cluster3.contains(p2));
        Assert.assertTrue(cluster3.contains(p3));
    }
}
