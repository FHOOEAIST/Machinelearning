/*
 * Copyright (c) 2021 the original author or authors.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package science.aist.machinelearning.algorithm.nn;

import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.Updater;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import science.aist.machinelearning.core.Problem;
import science.aist.machinelearning.core.ProblemGene;
import science.aist.machinelearning.core.Solution;
import science.aist.machinelearning.core.SolutionGene;
import science.aist.machinelearning.problem.DoubleElement;
import science.aist.machinelearning.problem.mapping.DoubleElementToDoubleMapper;
import science.aist.machinelearning.problem.mapping.DoubleToDoubleElementMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author Andreas Pointner
 * @since 1.0
 */
public class NeuronalNetworkNeuronTest {

    private final double tolerance = 0.025;
    private Random rand;

    @BeforeClass
    public void setUp() {
        rand = new Random(1L); // using a fixed seed to make sure, that the result is always the same.
    }

    @Test
    public void testSin() {
        // given
        int trainingSize = 1000;
        List<Problem<DoubleElement>> input = new ArrayList<>();
        List<Solution<DoubleElement, DoubleElement>> output = new ArrayList<>();
        for (int i = 0; i < trainingSize; i++) {
            double val = rand.nextDouble();
            input.add(createProblem(val));
            output.add(createSolution(Math.sin(val)));
        }

        NeuralNetwork<DoubleElement, DoubleElement> neuralNetwork = new NeuralNetwork<DoubleElement, DoubleElement>(new int[]{10})
                // These are mandatory options
                .setProblemToDoubleTransformer(new DoubleElementToDoubleMapper())
                .setSolutionToDoubleTransformer(new DoubleElementToDoubleMapper())
                .setDoubleToSolutionTransformer(new DoubleToDoubleElementMapper())

                // These are specific settings
                .setEpochs(10)
                .setIterations(100)
                .setLearningRate(0.01)
                .setOptimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
                .setOutputLayerLossFunction(LossFunctions.LossFunction.MSE)
                .setWeightInit(WeightInit.XAVIER)
                .setUpdater(Updater.NESTEROVS)

                // This just shows an example, that shows how specific options, where there exists no setter, can be configured
                .setBuilderConsumer(builder -> builder.seed(12345))
                .setMultiLayerNetworkConsumer(net -> net.addListeners(new ScoreIterationListener(100)))

                // Train the network;
                .train(input, output);

        // when
        double[] res = new double[5];
        double[] expect = new double[5];
        for (int i = 0; i < 5; i++) {
            double val = rand.nextDouble();
            double solution = getSolution(neuralNetwork.solve(createProblem(val)));
            res[i] = solution;
            expect[i] = Math.sin(val);
        }

        // then
        for (int i = 0; i < 5; i++) {
            Assert.assertEquals(res[i], expect[i], tolerance);
        }
    }

    @Test
    public void testSin2() {
        // given
        int trainingSize = 1000;
        List<Problem<DoubleElement>> input = new ArrayList<>();
        List<Solution<DoubleElement, DoubleElement>> output = new ArrayList<>();
        for (int i = 0; i < trainingSize; i++) {
            double val = rand.nextDouble();
            input.add(createProblem(val));
            output.add(createSolution(Math.sin(val)));
        }

        NeuralNetwork<DoubleElement, DoubleElement> neuralNetwork =
                NeuralNetwork.load(getClass().getResourceAsStream("/sin.zip"),
                        // This should shown an example how to configure additional properties.
                        // in this case add a listener, because they are not saved.
                        net -> net.addListeners(new ScoreIterationListener(100)));

        neuralNetwork.setEpochs(5);

        // After loading it is again possible to train the network to make it even "better"
        neuralNetwork.train(input, output);

        // when
        double[] res = new double[5];
        double[] expect = new double[5];
        for (int i = 0; i < 5; i++) {
            double val = rand.nextDouble();
            double solution = getSolution(neuralNetwork.solve(createProblem(val)));
            res[i] = solution;
            expect[i] = Math.sin(val);
        }

        // then
        for (int i = 0; i < 5; i++) {
            Assert.assertEquals(res[i], expect[i], tolerance);
        }
    }

    private Problem<DoubleElement> createProblem(double val) {
        Problem<DoubleElement> pt = new Problem<>();
        ProblemGene<DoubleElement> gene = new ProblemGene<>();
        gene.setGene(new DoubleElement(val));
        List<ProblemGene<DoubleElement>> list = new ArrayList<>();
        list.add(gene);
        pt.setProblemGenes(list);
        return pt;
    }

    private Solution<DoubleElement, DoubleElement> createSolution(double val) {
        Solution<DoubleElement, DoubleElement> sl = new Solution<>();
        SolutionGene<DoubleElement, DoubleElement> gene = new SolutionGene<>();
        gene.setGene(new DoubleElement(val));
        List<SolutionGene<DoubleElement, DoubleElement>> list = new ArrayList<>();
        list.add(gene);
        sl.setSolutionGenes(list);
        return sl;
    }

    private double getSolution(Solution<DoubleElement, DoubleElement> sol) {
        return sol.getSolutionGenes().get(0).getGene().getValue();
    }
}
