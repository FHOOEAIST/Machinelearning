package science.aist.machinelearning.core.experiment;

/**
 * Contains a choice to be statically injected. It will NOT count towards the permutations
 *
 * @author Oliver Krauss
 * @since 1.0
 */
public class FixedChoice<O> extends AbstractChoice<O> {

    /**
     * Choice to be statically injected PLEASE NOTE: IF your choice has a state (ex. Stateful Crossover) this will screw
     * up the experiment You should implement a specialized fixed choice that will implement the "reset"
     */
    O choice;

    public FixedChoice(String name, O choice) {
        super(name);
        this.choice = choice;
    }

    @Override
    public O next() {
        return choice;
    }

    @Override
    public O current() {
        return choice;
    }

    @Override
    public boolean hasNext() {
        return false;
    }

    @Override
    public void reset() {
        // do nothing
    }
}
