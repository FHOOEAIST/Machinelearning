package science.aist.machinelearning.analytics;

import org.springframework.beans.factory.annotation.Required;
import science.aist.machinelearning.analytics.graph.ProblemGeneRepository;
import science.aist.machinelearning.analytics.graph.nodes.AnalyticsNode;
import science.aist.machinelearning.analytics.graph.nodes.StepNode;
import science.aist.machinelearning.core.Problem;
import science.aist.machinelearning.core.Solution;
import science.aist.machinelearning.core.analytics.Analytics;
import science.aist.neo4j.Neo4jRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Implementation of {@link Analytics} publishing to neo4j As spring data is specific for each graph-db there is no
 * possibility of generalizing for all graph databases
 *
 * @author Oliver Krauss
 * @since 1.0
 */
public class GraphAnalytics implements Analytics {

    /**
     * Formatter for timings
     */
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    /**
     * Repository for storing the parent node of the run
     */
    protected Neo4jRepository<AnalyticsNode, Long> analyticsRepository;
    /**
     * Repository for storing single steps of the run
     */
    protected Neo4jRepository<StepNode, Long> stepRepository;
    /**
     * Repository for storing problem definitions
     */
    protected Neo4jRepository<Problem, Long> problemRepository;
    /**
     * Repository for storing solution definitions
     */
    protected Neo4jRepository<Solution, Long> solutionRepository;
    /**
     * Repository for storing / finding problem genes
     */
    protected ProblemGeneRepository problemGeneRepository;
    /**
     * Headers will be translated to properties of {@link StepNode}
     */
    protected List<String> stepHeaders = new ArrayList<>();

    /**
     * The main node of the analysis.
     */
    protected AnalyticsNode mainNode = null;

    /**
     * The current step being logged towards
     */
    protected StepNode currentStep = null;

    @Override
    public void startAnalytics() {
        mainNode = new AnalyticsNode("EXPERIMENT_" + LocalDateTime.now().format(formatter));
        mainNode = analyticsRepository.save(mainNode);
    }

    @Override
    public void logParam(String name, String value) {
        checkStarted();
        mainNode.addParameter(name, value);
        mainNode = analyticsRepository.save(mainNode);
    }

    @Override
    public void logAlgorithmStepHeaders(List<String> names) {
        if (names != null) {
            stepHeaders = names;
        }
    }

    @Override
    public void logAlgorithmStep(List<String> values) {
        checkStarted();
        if (values == null || stepHeaders.size() < values.size()) {
            throw new RuntimeException("Values do not correspond to pre-defined step definitions");
        }

        // create step
        StepNode step = new StepNode();
        step.setTime(LocalDateTime.now().format(formatter));
        Iterator<String> sHi = stepHeaders.iterator();
        Iterator<String> vi = values.iterator();
        while (sHi.hasNext() && vi.hasNext()) {
            step.addParameter(sHi.next(), vi.next());
        }

        // add link to run
        mainNode.addStep(step);
        mainNode = analyticsRepository.save(mainNode);

        // save
        step = stepRepository.save(step);

        // add link to previous step
        if (currentStep != null) {
            currentStep.setNextStep(step);
            currentStep = stepRepository.save(currentStep);
        }

        // update current
        currentStep = step;
    }

    @Override
    public <PT> void logProblem(Problem<PT> problem) {
        checkStarted();
        mainNode.setProblem(problem);
        mainNode = analyticsRepository.save(mainNode);
    }

    @Override
    public <GT, PT> void logSolution(Solution<GT, PT> solution) {
        checkStarted();
        mainNode.setSolution(solution);
        mainNode = analyticsRepository.save(mainNode);
    }

    /**
     * Helper function that checks if the analytics may be published
     */
    protected void checkStarted() {
        if (mainNode == null) {
            throw new RuntimeException("Parameters can't be logged while the analytics haven't started. Call startAnalytics() before using this method");
        }
    }

    @Override
    public void finishAnalytics() {
        mainNode = null;
        currentStep = null;
        stepHeaders = new ArrayList<>();
    }

    @Required
    public void setAnalyticsRepository(Neo4jRepository<AnalyticsNode, Long> analyticsRepository) {
        this.analyticsRepository = analyticsRepository;
    }

    @Required
    public void setStepRepository(Neo4jRepository<StepNode, Long> stepRepository) {
        this.stepRepository = stepRepository;
    }

    @Required
    public void setProblemRepository(Neo4jRepository<Problem, Long> problemRepository) {
        this.problemRepository = problemRepository;
    }

    @Required
    public void setSolutionRepository(Neo4jRepository<Solution, Long> solutionRepository) {
        this.solutionRepository = solutionRepository;
    }

    @Required
    public void setProblemGeneRepository(ProblemGeneRepository problemGeneRepository) {
        this.problemGeneRepository = problemGeneRepository;
    }
}
