package science.aist.machinelearning.core.experiment;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Helper class for experiment. The Single Choice Option selects one of the given options
 *
 * @param <O> options that can be chosen
 * @author Oliver Krauss
 * @since 1.0
 */
public class SingleChoiceConfig<O> extends AbstractChoice<O> {

    protected List<O> choices = new ArrayList<>();

    private int current = -1;

    public SingleChoiceConfig(String name) {
        super(name);
    }

    @Override
    public O next() {
        return choices.size() < current ? null : choices.get(++current);
    }

    @Override
    public O current() {
        return choices.size() < current || current < 0 ? null : choices.get(current);
    }

    @Override
    public boolean hasNext() {
        return choices != null && choices.size() > current + 1;
    }

    @Override
    public void reset() {
        current = -1;
    }

    public void setChoices(List<O> choices) {
        this.choices = Objects.requireNonNullElseGet(choices, ArrayList::new);
    }

    public void addChoice(O choice) {
        this.choices.add(choice);
    }

}
