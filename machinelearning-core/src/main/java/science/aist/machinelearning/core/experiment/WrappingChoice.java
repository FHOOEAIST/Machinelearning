package science.aist.machinelearning.core.experiment;

import science.aist.machinelearning.core.options.Descriptor;

import java.util.Collection;
import java.util.LinkedList;

/**
 * Helper class for experiment. The Single Choice Option selects one of the given options
 *
 * @param <O> options that can be chosen
 * @author Oliver Krauss
 * @since 1.0
 */
public class WrappingChoice<O> extends AbstractChoice<O> {

    private Choice<O> choice;

    public WrappingChoice(Choice choice) {
        super(choice.getName());
        this.choice = choice;
    }

    @Override
    public ExperimentIdentifier getCurrentIdentifier() {
        return choice.getCurrentIdentifier();
    }

    @Override
    public O next() {
        O value = choice.next();
        return value == null ? null : (O) new Descriptor(value);
    }

    @Override
    public O current() {
        O value = choice.current();
        return value == null ? null : (O) new Descriptor(value);
    }


    @Override
    public boolean hasNext() {
        return choice.hasNext();
    }

    @Override
    public void reset() {
        choice.reset();
    }


    public void setChoice(Choice<O> choice) {
        if (choice == null) {
            throw new IllegalArgumentException("choice must never be null");
        }
        choice.setParent(this);
        this.choice = choice;
    }

    @Override
    public Collection<Choice> getChildren() {
        LinkedList<Choice> choices = new LinkedList<>();
        choices.add(choice);
        return choices;
    }

    @Override
    protected void replaceChild(Choice<O> oldChoice, Choice<O> newChoice) {
        oldChoice.setParent(null);
        this.choice = newChoice;
    }


    @Override
    public Choice findConfig(String name) {
        return choice.findConfig(name);
    }

}
