/*
 * Copyright (c) 2021 the original author or authors.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package science.aist.machinelearning.example.clustering.kmeans;

import org.testng.Assert;
import org.testng.annotations.Test;
import science.aist.machinelearning.algorithm.clustering.kmeans.Cluster;
import science.aist.machinelearning.algorithm.clustering.kmeans.KMeansClustering;
import science.aist.machinelearning.algorithm.clustering.kmeans.distance.EuclideanSquaredVectorDistance;
import science.aist.machinelearning.core.Gene;
import science.aist.machinelearning.core.Problem;
import science.aist.machinelearning.core.ProblemGene;
import science.aist.machinelearning.core.Solution;

import java.awt.geom.Point2D;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author Andreas Pointner
 * @since 1.0
 */
public class KMeansClusteringExample {
    @Test
    public void testClustering2dPoints() {
        // given
        Random rand = new Random(42L);
        KMeansClustering<Point2D> clustering = new KMeansClustering<>();
        clustering.setNumberOfClusters(5);
        clustering.setElementToVector(p2d -> new double[]{p2d.getX(), p2d.getY()});
        clustering.setVectorDistance(new EuclideanSquaredVectorDistance());
        clustering.setEpsilon(0.01);
        clustering.setMaxIterations(25);
        Problem<Point2D> problem = new Problem<>();
        problem.setProblemGenes(IntStream.range(0, 50)
                .mapToObj(i -> new Point2D.Double(rand.nextInt(1000), rand.nextInt(1000)))
                .map(Point2D.class::cast)
                .map(ProblemGene::new)
                .collect(Collectors.toList()));

        // when
        Solution<Cluster<Point2D>, Point2D> solution = clustering.solve(problem);

        // then
        List<Cluster<Point2D>> clusters = solution.getSolutionGenes().stream()
                .map(Gene::getGene)
                .collect(Collectors.toList());
        Assert.assertEquals(clusters.size(), 5);
        Assert.assertEquals(clusters.get(0).getElements().size(), 14);
        Assert.assertEquals(clusters.get(1).getElements().size(), 11);
        Assert.assertEquals(clusters.get(2).getElements().size(), 13);
        Assert.assertEquals(clusters.get(3).getElements().size(), 5);
        Assert.assertEquals(clusters.get(4).getElements().size(), 7);
    }
}
