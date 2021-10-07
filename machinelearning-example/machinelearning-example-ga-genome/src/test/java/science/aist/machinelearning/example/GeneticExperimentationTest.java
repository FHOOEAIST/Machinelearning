package science.aist.machinelearning.example;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import science.aist.machinelearning.algorithm.ga.GeneticAlgorithm;
import science.aist.machinelearning.algorithm.ga.crossover.UniformCrossover;
import science.aist.machinelearning.algorithm.ga.selector.TournamentSelector;
import science.aist.machinelearning.algorithm.mutation.RandomGeneMutator;
import science.aist.machinelearning.core.Algorithm;
import science.aist.machinelearning.core.Problem;
import science.aist.machinelearning.core.experiment.*;
import science.aist.machinelearning.core.fitness.CachetEvaluator;
import science.aist.machinelearning.core.fitness.GenericEvaluatorImpl;
import science.aist.machinelearning.core.logging.LoggingConf;
import science.aist.machinelearning.core.mapping.OneToOneSolutionCreator;
import science.aist.machinelearning.core.mapping.SolutionCreator;
import science.aist.machinelearning.core.options.Descriptor;
import science.aist.machinelearning.problem.genome.Element;
import science.aist.machinelearning.problem.genome.fitness.ElementEqualityCachet;
import science.aist.machinelearning.problem.genome.mapping.RandomGeneCreator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author Oliver Krauss
 * @since 1.0
 */
public class GeneticExperimentationTest {

    private final GeneticAlgorithm<Element[], Element[]> algorithm = new GeneticAlgorithm<>();

    private final GenericEvaluatorImpl<Element[], Element[]> evaluator = new GenericEvaluatorImpl<>();

    private final ElementEqualityCachet elementEqualityCachet = new ElementEqualityCachet();

    private final SolutionCreator<Element[], Element[]> solutionCreator = new OneToOneSolutionCreator<>();

    private final RandomGeneCreator geneCreator = new RandomGeneCreator();

    private final UniformCrossover crossover = new UniformCrossover();

    private final TournamentSelector<Element[], Element[]> tournamentSelector = new TournamentSelector<>();

    private final RandomGeneMutator mutator = new RandomGeneMutator();

    private Element[] problem = null;

    @BeforeClass
    public void setUp() {

        LoggingConf.setLoggingToRootLevel();

        // configure mutator
        mutator.setEvaluator(evaluator);
        mutator.setMutationsPerSolution(1);

        // configure selector
        tournamentSelector.setTournamentSize(10);

        // configure evaluator
        Map<CachetEvaluator<Element[], Element[]>, Double> cachets = new HashMap<>();
        cachets.put(elementEqualityCachet, 1.0);
        evaluator.setCachetEvaluators(cachets);

        solutionCreator.setGeneCreator(geneCreator);

        // configure alg
        algorithm.setElites(1);
        algorithm.setMaximumGenerations(100);
        algorithm.setMutationProbability(0.05);
        algorithm.setPopulationSize(100);

        algorithm.setEvaluator(evaluator);
        algorithm.setSolutionCreator(solutionCreator);
        algorithm.setCrossover(crossover);
        algorithm.setGenMutator(mutator);
        algorithm.setSelector(tournamentSelector);

        // given
        problem = new Element[20];
        for (int i = 0; i < 20; i++) {
            if (i >= 15) {
                problem[i] = new Element('T');
            } else if (i >= 10) {
                problem[i] = new Element('G');
            } else if (i >= 5) {
                problem[i] = new Element('C');
            } else {
                problem[i] = new Element('A');
            }
        }

        elementEqualityCachet.setTargetSequence("GTACCCGTACCCGTACCCTT");
    }

    @Test
    public void testGenerateConfigurableSimple() {
        // given
        // already done in before class

        // when
        ConfigurableChoice<GeneticAlgorithm<Element[], Element[]>> configurationChoices = Experiment.createConfigurationChoices("ga", algorithm, false, false);

        // then
        Assert.assertNotNull(configurationChoices);
        Assert.assertNotNull(configurationChoices.findConfig("ga.elites"));
        Assert.assertEquals(((Descriptor) configurationChoices.findConfig("ga.elites").current()).getValue(), 1);
    }

    @Test
    public void testGenerateConfigurableWithRanges() {
        // given
        // already done in before class

        // when
        ConfigurableChoice<GeneticAlgorithm<Element[], Element[]>> configurationChoices = Experiment.createConfigurationChoices("ga", algorithm, true, false);

        // then
        Assert.assertNotNull(configurationChoices);
        Assert.assertNotNull(configurationChoices.findConfig("ga.crossover"));
        Assert.assertTrue(configurationChoices.findConfig("ga.crossover").getClass().getName().contains("FixedChoice"));
        Assert.assertNotNull(configurationChoices.findConfig("ga.elites"));
        Assert.assertNull(configurationChoices.findConfig("ga.elites").current());
        Assert.assertEquals(((Descriptor) configurationChoices.findConfig("ga.elites").next()).getValue(), 1);
        Assert.assertEquals(((Descriptor) configurationChoices.findConfig("ga.elites").next()).getValue(), 3);
        Assert.assertEquals(((Descriptor) configurationChoices.findConfig("ga.elites").current()).getValue(), 3);
    }

    @Test
    public void testGenerateConfigurableWithExchangableValues() {
        // given
        // already done in before class

        // when
        ConfigurableChoice<GeneticAlgorithm<Element[], Element[]>> configurationChoices = Experiment.createConfigurationChoices("ga", algorithm, false, true);

        // then
        Assert.assertNotNull(configurationChoices);
        Assert.assertNotNull(configurationChoices.findConfig("ga.crossover"));
        Assert.assertFalse(configurationChoices.findConfig("ga.crossover").getClass().getName().contains("FixedChoice"));
        Assert.assertNull(configurationChoices.findConfig("ga.crossover").current());
        Assert.assertTrue(configurationChoices.findConfig("ga.crossover").hasNext());
        Assert.assertNotNull((configurationChoices.findConfig("ga.crossover").next()));
        Assert.assertEquals(configurationChoices.findConfig("ga.crossover").current().getClass(), crossover.getClass());
        Assert.assertFalse(configurationChoices.findConfig("ga.crossover").hasNext());
    }

    @Test
    public void testGenerateConfigFull() {
        // given
        // already done in before class

        // when
        ConfigurableChoice<GeneticAlgorithm<Element[], Element[]>> configurationChoices = Experiment.createConfigurationChoices("ga", algorithm, true, true);

        // then
        Assert.assertNotNull(configurationChoices);
        Assert.assertNotNull(configurationChoices.findConfig("ga.crossover"));
        Assert.assertFalse(configurationChoices.findConfig("ga.crossover").getClass().getName().contains("FixedChoice"));
        Assert.assertNull(configurationChoices.findConfig("ga.crossover").current());
        Assert.assertTrue(configurationChoices.findConfig("ga.crossover").hasNext());
        Assert.assertNotNull((configurationChoices.findConfig("ga.crossover").next()));
        Assert.assertEquals(configurationChoices.findConfig("ga.crossover").current().getClass(), crossover.getClass());
        Assert.assertNotNull(configurationChoices.findConfig("ga.elites"));
        Assert.assertNull(configurationChoices.findConfig("ga.elites").current());
        Assert.assertEquals(((Descriptor) configurationChoices.findConfig("ga.elites").next()).getValue(), 1);
        Assert.assertEquals(((Descriptor) configurationChoices.findConfig("ga.elites").next()).getValue(), 3);
        Assert.assertEquals(((Descriptor) configurationChoices.findConfig("ga.elites").current()).getValue(), 3);
    }

    @Test
    public void testReplacementOfConfig() {
        // given
        ConfigurableChoice<GeneticAlgorithm<Element[], Element[]>> choices = Experiment.createConfigurationChoices("ga", algorithm, false, false);
        SingleChoiceConfig<Integer> conf = new SingleChoiceConfig<>("elites");
        conf.addChoice(1);
        conf.addChoice(2);

        // when
        choices.findConfig("ga.elites").replace(conf);

        // then
        Assert.assertEquals(conf, choices.findConfig("ga.elites"));
    }

    @Test
    public void testRunMinimalexperiment() {
        // given
        ConfigurableChoice<Algorithm<Element[], Element[]>> configurationChoices = Experiment.createConfigurationChoices("ga", algorithm, false, false);

        Experiment<Element[], Element[]> experiment = new Experiment();
        experiment.addAlgorithm(configurationChoices);
        experiment.setRepeats(3);
        experiment.addProblem(Experiment.createProblemChoices("problem", problem));

        // when
        ExperimentResult<Element[], Element[]> experimentResult = experiment.conductExperiment();

        // then
        Assert.assertNotNull(experimentResult);
        Assert.assertEquals(experimentResult.getResults().size(), 1);
        Assert.assertTrue(experimentResult.getResults().values().stream().allMatch(x -> x.size() == 3));
    }

    @Test
    public void testRunExperimentPrepChoices() {
        // given
        ConfigurableChoice<Algorithm<Element[], Element[]>> configurationChoices = Experiment.createConfigurationChoices("ga", algorithm, true, false);

        Experiment<Element[], Element[]> experiment = new Experiment();
        experiment.addAlgorithm(configurationChoices);
        experiment.setRepeats(3);
        experiment.addProblem(Experiment.createProblemChoices("problem", problem));

        // when
        ExperimentResult<Element[], Element[]> experimentResult = experiment.conductExperiment();

        // then
        Assert.assertNotNull(experimentResult);
        Assert.assertEquals(experimentResult.getResults().size(), 81);
        Assert.assertTrue(experimentResult.getResults().values().stream().allMatch(x -> x.size() == 3));
    }

    @Test
    public void testRunExperimentValuesExchangeable() {
        // given
        ConfigurableChoice<Algorithm<Element[], Element[]>> configurationChoices = Experiment.createConfigurationChoices("ga", algorithm, false, true);

        Experiment<Element[], Element[]> experiment = new Experiment();
        experiment.addAlgorithm(configurationChoices);
        experiment.setRepeats(3);
        experiment.addProblem(Experiment.createProblemChoices("problem", problem));

        // when
        ExperimentResult<Element[], Element[]> experimentResult = experiment.conductExperiment();

        // then
        Assert.assertNotNull(experimentResult);
        Assert.assertEquals(experimentResult.getResults().size(), 1);
        Assert.assertTrue(experimentResult.getResults().values().stream().allMatch(x -> x.size() == 3));
    }

    @Test
    public void testRunExperimentDoubleAlg() {
        // given
        ConfigurableChoice<Algorithm<Element[], Element[]>> configurationChoices = Experiment.createConfigurationChoices("ga", algorithm, true, true);
        ConfigurableChoice<Algorithm<Element[], Element[]>> configurationChoices2 = Experiment.createConfigurationChoices("gu", algorithm, true, true);

        Experiment<Element[], Element[]> experiment = new Experiment();
        experiment.addAlgorithm(configurationChoices);
        experiment.addAlgorithm(configurationChoices2);
        experiment.setRepeats(3);
        experiment.addProblem(Experiment.createProblemChoices("problem", problem));

        // when
        ExperimentResult<Element[], Element[]> experimentResult = experiment.conductExperiment();

        // then
        Assert.assertNotNull(experimentResult);
        Assert.assertEquals(experimentResult.getResults().size(), 10368);
        Assert.assertTrue(experimentResult.getResults().values().stream().allMatch(x -> x.size() == 3));
    }

    @Test
    public void testRunMultiProblem() {
        // given
        ConfigurableChoice<Algorithm<Element[], Element[]>> configurationChoices = Experiment.createConfigurationChoices("ga", algorithm, false, false);

        Experiment<Element[], Element[]> experiment = new Experiment();
        experiment.addAlgorithm(configurationChoices);
        experiment.setRepeats(3);

        Element[] problem2 = new Element[20];
        for (int i = 0; i < 20; i++) {
            if (i >= 15) {
                problem2[i] = new Element('T');
            } else if (i >= 10) {
                problem2[i] = new Element('G');
            } else if (i >= 5) {
                problem2[i] = new Element('C');
            } else {
                problem2[i] = new Element('A');
            }
        }
        experiment.addProblem(Experiment.createProblemChoices("problem", problem));
        experiment.addProblem(Experiment.createProblemChoices("problem2", problem2));

        // when
        ExperimentResult<Element[], Element[]> experimentResult = experiment.conductExperiment();

        // then
        Assert.assertNotNull(experimentResult);
        Assert.assertEquals(experimentResult.getResults().size(), 2);
        Assert.assertTrue(experimentResult.getResults().values().stream().allMatch(x -> x.size() == 3));
    }

    @Test
    public void testRunExperimentGroup() {
        // given
        ConfigurableChoice<Algorithm<Element[], Element[]>> configurationChoices = Experiment.createConfigurationChoices("ga", algorithm, true, true);
        ConfigurableChoice<Algorithm<Element[], Element[]>> configurationChoices2 = Experiment.createConfigurationChoices("ga", algorithm, true, true);

        Choice<Problem<Element[]>> problem = Experiment.createProblemChoices("problem", this.problem);

        Experiment<Element[], Element[]> experiment = new Experiment();
        experiment.addAlgorithm(configurationChoices);
        experiment.setRepeats(3);
        experiment.addProblem(problem);

        Experiment<Element[], Element[]> experiment2 = new Experiment();
        experiment2.addAlgorithm(configurationChoices2);
        experiment2.setRepeats(3);
        experiment2.addProblem(problem);

        ExperimentGroup<Element[], Element[]> experimentGroup = new ExperimentGroup<>();
        experimentGroup.addExperiment(experiment);
        experimentGroup.addExperiment(experiment2);

        // when
        ExperimentResult<Element[], Element[]> experimentResult = experimentGroup.conductExperiment();

        // then
        Assert.assertNotNull(experimentResult);
        Assert.assertEquals(experimentResult.getResults().size(), 5184);
        Assert.assertTrue(experimentResult.getResults().values().stream().allMatch(x -> x.size() == 6));
    }

    @Test
    public void testConfigurableProblem() {
        // given
        // I am too lazy to actually create a whole algorithm for this, so we will just test Configurable Problem
        UniformCrossover a = new UniformCrossover();
        UniformCrossover b = new UniformCrossover();
        List<UniformCrossover> crossover = new ArrayList<>();
        crossover.add(a);
        crossover.add(b);

        // when
        Choice<Problem<UniformCrossover>> crossoverProblem = Experiment.createProblemChoices("crossoverProblem", crossover, true, true);

        // then
        Assert.assertNotNull(crossoverProblem);
        Assert.assertTrue(crossoverProblem.hasNext());
        int i = 0;
        while (crossoverProblem.hasNext()) {
            Assert.assertNotNull(crossoverProblem.next());
            i++;
        }
        Assert.assertEquals(i, 16);
    }
}
