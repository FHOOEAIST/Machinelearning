/*
 * Copyright (c) 2021 the original author or authors.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package science.aist.machinelearning.example;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import science.aist.machinelearning.algorithm.crossover.GPCrossover;
import science.aist.machinelearning.algorithm.ga.Selector;
import science.aist.machinelearning.algorithm.ga.crossover.UniformCrossover;
import science.aist.machinelearning.algorithm.ga.selector.TournamentSelector;
import science.aist.machinelearning.algorithm.gp.GPGraphNode;
import science.aist.machinelearning.algorithm.gp.nodes.basic.ResultNode;
import science.aist.machinelearning.algorithm.gp.nodes.heuristic.*;
import science.aist.machinelearning.algorithm.gp.nodes.math.*;
import science.aist.machinelearning.algorithm.gp.nodes.programming.*;
import science.aist.machinelearning.algorithm.gp.util.GPRepair;
import science.aist.machinelearning.algorithm.gp.util.GPValidator;
import science.aist.machinelearning.algorithm.mutation.RandomGeneMutator;
import science.aist.machinelearning.core.Problem;
import science.aist.machinelearning.core.ProblemGene;
import science.aist.machinelearning.core.Solution;
import science.aist.machinelearning.core.SolutionGene;
import science.aist.machinelearning.core.fitness.CachetEvaluator;
import science.aist.machinelearning.core.fitness.GenericEvaluatorImpl;
import science.aist.machinelearning.core.mapping.OneToOneSolutionCreator;
import science.aist.machinelearning.core.options.Descriptor;
import science.aist.machinelearning.core.options.ListDescriptor;
import science.aist.machinelearning.core.options.MinMaxDescriptor;
import science.aist.machinelearning.problem.GPProblem;
import science.aist.machinelearning.problem.genome.Element;
import science.aist.machinelearning.problem.genome.fitness.ElementEqualityCachet;
import science.aist.machinelearning.problem.genome.mapping.RandomGeneCreator;
import science.aist.machinelearning.problem.mapping.GPGeneCreator;

import java.util.*;

/**
 * UnitTestClass for {@link GPCrossover}
 *
 * @author Daniel Wilfing
 * @since 1.0
 */
public class GPCrossoverTest {

    private final Map<Class, ArrayList<GPGraphNode>> validNodes = new HashMap<>();

    private final Map<Class, Map<String, Descriptor>> settings = new HashMap<>();

    private GPProblem problem;

    private GenericEvaluatorImpl<Element[], Element[]> evaluator;

    @BeforeClass
    public void setup() {
        //setup evaluators and other classes for the nodes

        OneToOneSolutionCreator solutionCreator = new OneToOneSolutionCreator();
        solutionCreator.setGeneCreator(new RandomGeneCreator());

        evaluator = new GenericEvaluatorImpl<>();

        RandomGeneMutator mutator = new RandomGeneMutator();
        mutator.setEvaluator(evaluator);

        // configure evaluator
        ElementEqualityCachet elementEqualityCachet = new ElementEqualityCachet();

        Map<CachetEvaluator<Element[], Element[]>, Double> cachets = new HashMap<>();
        cachets.put(elementEqualityCachet, 1.0);
        evaluator.setCachetEvaluators(cachets);

        solutionCreator.setGeneCreator(new RandomGeneCreator());

        //generate a problem to work with
        Element[] problemVal = new Element[20];
        for (int i = 0; i < 20; i++) {
            if (i >= 15) {
                problemVal[i] = new Element('T');
            } else if (i >= 10) {
                problemVal[i] = new Element('G');
            } else if (i >= 5) {
                problemVal[i] = new Element('C');
            } else {
                problemVal[i] = new Element('A');
            }
        }

        elementEqualityCachet.setTargetSequence("GTACCCGTACCCGTACCCTT");

        List<ProblemGene<Element[]>> problems = new ArrayList<>();
        problems.add(new ProblemGene<>(problemVal));

        Problem<Element[]> genomeProblem = new Problem(problems);

        UniformCrossover crossover = new UniformCrossover();

        TournamentSelector<Element[], Element[]> selector = new TournamentSelector<>();
        selector.setTournamentSize(10);


        ArrayList<GPGraphNode> returnsSolution = new ArrayList<>();
        ArrayList<GPGraphNode> returnsNumber = new ArrayList<>();
        ArrayList<GPGraphNode> returnsBoolean = new ArrayList<>();
        ArrayList<GPGraphNode> returnsCollection = new ArrayList<>();
        ArrayList<GPGraphNode> returnsObject = new ArrayList<>();

        //setting up heuristic nodes
        returnsSolution.add(new CrossoverNode());
        Map<String, Descriptor> crossoverSettings = new HashMap<>();
        crossoverSettings.put("crossover", new Descriptor<>(crossover));
        crossoverSettings.put("selector", new Descriptor<>(selector));
        crossoverSettings.put("evaluator", new Descriptor<>(evaluator));
        settings.put(CrossoverNode.class, crossoverSettings);

        returnsCollection.add(new EliteNode(Solution.class));

        returnsNumber.add(new EvaluatorQualityNode());
        Map<String, Descriptor> evaluatorQualitySettings = new HashMap<>();
        evaluatorQualitySettings.put("evaluator", new Descriptor<>(evaluator));
        settings.put(EvaluatorQualityNode.class, evaluatorQualitySettings);

        returnsSolution.add(new EvaluatorSolutionNode());
        Map<String, Descriptor> evaluatorSolutionSettings = new HashMap<>();
        evaluatorSolutionSettings.put("evaluator", new Descriptor<>(evaluator));
        settings.put(EvaluatorSolutionNode.class, evaluatorSolutionSettings);

        returnsSolution.add(new MutatorNode());
        Map<String, Descriptor> mutatorSettings = new HashMap<>();
        mutatorSettings.put("mutator", new Descriptor<>(mutator));
        mutatorSettings.put("evaluator", new Descriptor<>(evaluator));
        settings.put(MutatorNode.class, mutatorSettings);

        returnsSolution.add(new SolutionCreatorNode());
        Map<String, Descriptor> solutionCreatorSettings = new HashMap<>();
        solutionCreatorSettings.put("creator", new Descriptor<>(solutionCreator));
        solutionCreatorSettings.put("problem", new Descriptor<>(genomeProblem));
        solutionCreatorSettings.put("evaluator", new Descriptor<>(evaluator));
        settings.put(SolutionCreatorNode.class, solutionCreatorSettings);

        //setting up math nodes
        returnsNumber.add(new AddNode());

        returnsBoolean.add(new AndNode());

        returnsBoolean.add(new ConstantBooleanNode());
        Map<String, Descriptor> constantBooleanSettings = new HashMap<>();
        ArrayList<Boolean> possibleValues = new ArrayList<>();
        possibleValues.add(true);
        possibleValues.add(false);
        constantBooleanSettings.put("value", new ListDescriptor<>(possibleValues));
        settings.put(ConstantBooleanNode.class, constantBooleanSettings);

        returnsNumber.add(new ConstantDoubleNode());
        Map<String, Descriptor> constantNumberSettings = new HashMap<>();
        constantNumberSettings.put("value", new MinMaxDescriptor<>(0.0, 100.0));
        settings.put(ConstantDoubleNode.class, constantNumberSettings);

        returnsNumber.add(new DivideNode());

        returnsBoolean.add(new EqualsNode());

        returnsNumber.add(new ExponentiateNode());

        returnsBoolean.add(new LessThanNode());

        returnsNumber.add(new MultiplyNode());

        returnsBoolean.add(new OrNode());

        returnsNumber.add(new RandomNode());

        returnsNumber.add(new SquareRootNode());

        returnsNumber.add(new SubtractNode());

        //setting up programming nodes

        returnsCollection.add(new CacheTraderCollectionNode<>(Object.class));

        returnsObject.add(new CacheTraderNode<>(Object.class));

        returnsCollection.add(new CollectionMergeNode<>(Object.class));

        returnsCollection.add(new ForCollectionNode<>(Solution.class));

        returnsObject.add(new ForNode<>(Object.class));

        returnsObject.add(new IfThenElseNode<>(Object.class));

        returnsNumber.add(new SizeOfCollectionNode());

        returnsCollection.add(new WhileCollectionNode<>(Object.class));
        Map<String, Descriptor> whileCollectionSettings = new HashMap<>();
        whileCollectionSettings.put("maxIterations", new MinMaxDescriptor<>(1, 10));
        settings.put(WhileCollectionNode.class, whileCollectionSettings);

        returnsObject.add(new WhileNode<>(Object.class));
        Map<String, Descriptor> whileSettings = new HashMap<>();
        whileSettings.put("maxIterations", new MinMaxDescriptor<>(1, 10));
        settings.put(WhileCollectionNode.class, whileSettings);


        validNodes.put(Object.class, returnsObject);
        validNodes.put(Number.class, returnsNumber);
        validNodes.put(Boolean.class, returnsBoolean);
        validNodes.put(Solution.class, returnsSolution);
        validNodes.put(Collection.class, returnsCollection);


        //additional Terminals
        ArrayList<GPGraphNode> returnsCollectionTerminal = new ArrayList<>();
        returnsCollectionTerminal.add(new ForCollectionNode<>(Solution.class));
        Map<Class, ArrayList<GPGraphNode>> additionalTerminals = new HashMap<>();
        additionalTerminals.put(Collection.class, returnsCollectionTerminal);

        problem = new GPProblem(validNodes, settings, additionalTerminals);
    }

    @Test
    public void createTree() {
        //given
        GPGeneCreator geneCreator = new GPGeneCreator();
        geneCreator.setRepair(new GPRepair(10, 3, 0.20, 0.30));

        //when
        GPGraphNode root = geneCreator.createGene(new ProblemGene<>(problem));

        //then
        Assert.assertNotNull(root);
        Assert.assertTrue(GPValidator.validateGraph(root));
    }

    @Test(dependsOnMethods = "createTree")
    public void createCrossover() {
        //given
        GPGeneCreator geneCreator = new GPGeneCreator();
        geneCreator.setRepair(new GPRepair(5, 1, 0.20, 0.30));

        ResultNode node1 = geneCreator.createGene(new ProblemGene<>(problem));
        ResultNode node2 = geneCreator.createGene(new ProblemGene<>(problem));

        Solution<ResultNode, GPProblem> solution1 = new Solution<>();
        Solution<ResultNode, GPProblem> solution2 = new Solution<>();
        ArrayList<ProblemGene<GPProblem>> problemGenes = new ArrayList<>();
        problemGenes.add(new ProblemGene<>(problem));

        solution1.addGene(new SolutionGene<>(node1, problemGenes));
        solution2.addGene(new SolutionGene<>(node2, problemGenes));

        List<Solution<ResultNode, GPProblem>> solutions = new ArrayList<>();
        solutions.add(solution1);
        solutions.add(solution2);

        //when
        GPTestSelector selector = new GPTestSelector();
        GPCrossover crossover = new GPCrossover();
        Solution<ResultNode, GPProblem> crossoverSolution = crossover.breed(solutions, selector);

        Solution<Element[], Element[]> builtByCrossoverSolution = crossoverSolution.getSolutionGenes().get(0).getGene().execute();

        //then
        Assert.assertNotNull(crossoverSolution);
        Assert.assertTrue(GPValidator.validateGraph(crossoverSolution.getSolutionGenes().get(0).getGene()));
    }

    private static class GPTestSelector implements Selector<ResultNode, GPProblem> {

        boolean first = false;

        @Override
        public Solution<ResultNode, GPProblem> select(List<Solution<ResultNode, GPProblem>> population) {
            first = !first;
            if (first) {
                return population.get(0);
            }
            return population.get(1);
        }
    }
}
