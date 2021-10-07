package science.aist.machinelearning.core;

import org.springframework.beans.factory.annotation.Required;
import science.aist.machinelearning.core.analytics.Analytics;
import science.aist.machinelearning.core.fitness.Evaluator;
import science.aist.machinelearning.core.mapping.SolutionCreator;
import science.aist.machinelearning.core.options.Descriptor;

import java.util.HashMap;
import java.util.Map;

/**
 * Abstract implementation of {@link Algorithm} containing fields common to all algorithms
 *
 * @author Oliver Krauss
 * @since 1.0
 */
public abstract class AbstractAlgorithm<ST, PT> implements Algorithm<ST, PT> {

    /**
     * Evaluator for determining the quality of a mapping
     */
    protected Evaluator<ST, PT> evaluator;

    /**
     * Solution Creator that builds a valid mapping to the problem
     */
    protected SolutionCreator<ST, PT> solutionCreator;

    /**
     * Analytics tool to log the algorithm runs
     */
    protected Analytics analytics;

    public Evaluator<ST, PT> getEvaluator() {
        return evaluator;
    }

    /**
     * Setter for dependency injection
     *
     * @param evaluator the evaluator
     */
    @Required
    public void setEvaluator(Evaluator<ST, PT> evaluator) {
        this.evaluator = evaluator;
    }

    public SolutionCreator<ST, PT> getSolutionCreator() {
        return solutionCreator;
    }

    /**
     * Setter for dependency injection
     *
     * @param solutionCreator the solution creator
     */
    @Required
    public void setSolutionCreator(SolutionCreator<ST, PT> solutionCreator) {
        this.solutionCreator = solutionCreator;
    }

    @Override
    public Analytics getAnalytics() {
        return analytics;
    }

    @Override
    public void setAnalytics(Analytics analytics) {
        this.analytics = analytics;
    }

    @Override
    public Map<String, Descriptor> getOptions() {
        Map<String, Descriptor> options = new HashMap<>();

        options.put("evaluator", new Descriptor<>(evaluator));
        options.put("solutionCreator", new Descriptor<>(solutionCreator));
        options.put("analytics", new Descriptor<>(analytics));

        options.putAll(getSpecificOptions());

        return options;
    }

    /**
     * Get specific options of the underlying implementation.
     *
     * @return map of string and descriptors
     */
    protected abstract Map<String, Descriptor> getSpecificOptions();

    @Override
    public boolean setOption(String name, Descriptor descriptor) {
        try {
            switch (name) {
                case "evaluator":
                    setEvaluator((Evaluator<ST, PT>) descriptor.getValue());
                    break;
                case "solutionCreator":
                    setSolutionCreator((SolutionCreator<ST, PT>) descriptor.getValue());
                    break;
                case "analytics":
                    setAnalytics((Analytics) descriptor.getValue());
                    break;
                default:
                    //can't find the name, so lets try to find it in the specific options
                    return setSpecificOption(name, descriptor);
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * Set the option for a parameter for the underlying implementation.
     *
     * @param name       name of the option
     * @param descriptor descriptor to set
     * @return true = successfully set option, false = failed to set the option
     */
    protected abstract boolean setSpecificOption(String name, Descriptor descriptor);
}
