package science.aist.machinelearning.core.experiment;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Helper class for experiment. Iterating Choice iterates over all given choices and creates ALL POSSIBLE PERMUTATIONS
 *
 * @param <O>      options that can be chosen
 * @param <SUBOPT> sub options that will be iterated over to create O
 * @author Oliver Krauss
 * @since 1.0
 */
public abstract class IteratingChoice<O, SUBOPT> extends AbstractChoice<O> {

    /**
     * all already run through iterator options
     */
    private final List<Choice> previousIterations = new LinkedList<>();
    /**
     * Class to be instantiated
     */
    protected Class<?> optionClass;
    /**
     * Is the actual object to be configured
     */
    protected O choice;
    /**
     * Contains configuration options that will be Injected into O once it is created
     */
    protected List<Choice<? extends SUBOPT>> configurationOptions = new LinkedList<>();
    /**
     * Contains all options that need to be considered but will never change
     */
    protected List<Choice<? extends SUBOPT>> fixedOptions = new LinkedList<>();
    /**
     * Iterator at which choice we are currently manipulating
     */
    private Iterator<Choice<? extends SUBOPT>> iterator;
    /**
     * current "next" of iterator
     */
    private Choice currentIteration;

    public IteratingChoice(String name, Class<?> optionClass) {
        super(name);
        this.optionClass = optionClass;
        reset();
    }

    protected abstract void rebuildObject();

    @Override
    public O next() {
        if (currentIteration == null) {
            // we are starting -> Make sure to move all single choice items to fixed choices
            new LinkedList<>(configurationOptions).forEach(x -> {
                x.next();
                if (!x.hasNext()) {
                    configurationOptions.remove(x);
                    fixedOptions.add(x);
                }
            });
            reset();

            if (iterator.hasNext()) {
                currentIteration = iterator.next();
            }
        } else {
            if (previousIterations.stream().anyMatch(Choice::hasNext)) {
                // manipulate all previous beforehand
                for (Choice previousIteration : previousIterations) {
                    if (!previousIteration.hasNext()) {
                        previousIteration.reset();
                    } else {
                        previousIteration.next();
                        break;
                    }
                }
            } else if (currentIteration.hasNext()) {
                // move iterator in current group forward
                previousIterations.forEach(Choice::reset);
                currentIteration.next();
            } else {
                // move iterator forward
                previousIterations.add(currentIteration);
                currentIteration = iterator.next();
                previousIterations.forEach(Choice::reset);
                currentIteration.next();
            }
        }
        rebuildObject();
        return choice;
    }

    @Override
    public O current() {
        return choice;
    }

    @Override
    public boolean hasNext() {
        return choice == null || iterator.hasNext() || (currentIteration != null && currentIteration.hasNext()) || previousIterations.stream().anyMatch(Choice::hasNext);
    }

    @Override
    public void reset() {
        // call reset of fixed choices in case one needs to reset its state
        fixedOptions.forEach(Choice::reset);
        // call reset on permutations
        configurationOptions.forEach(Choice::reset);
        // reset config options iterator
        iterator = configurationOptions.iterator();
        currentIteration = null;
        previousIterations.clear();
    }

    public void setConfigurationOptions(List<Choice> options) {
        if (options == null) {
            return;
        }
        options.forEach(value -> {
            if (value instanceof FixedChoice) {
                value.setParent(this);
                fixedOptions.add(value);
            } else {
                configurationOptions.add(value);
            }
        });
        reset();
    }

    public void addConfigurationOption(Choice option) {
        if (option == null) {
            return;
        }
        option.setParent(this);
        if (option instanceof FixedChoice) {
            fixedOptions.add(option);
        } else {
            configurationOptions.add(option);
        }
        reset();
    }

    @Override
    public Collection<Choice> getChildren() {
        LinkedList<Choice> list = new LinkedList<>(fixedOptions);
        list.addAll(configurationOptions);
        return list;
    }

    @Override
    protected void replaceChild(Choice<O> oldChoice, Choice<O> newChoice) {
        oldChoice.setParent(null);
        configurationOptions.remove(oldChoice);
        fixedOptions.remove(oldChoice);
        addConfigurationOption(newChoice);
    }
}
