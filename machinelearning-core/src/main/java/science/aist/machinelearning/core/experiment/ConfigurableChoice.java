package science.aist.machinelearning.core.experiment;

import science.aist.machinelearning.core.Configurable;
import science.aist.machinelearning.core.options.Descriptor;

/**
 * Helper class for experiment. The configurable choice contains a CLASS that will be instantiated and configured as
 * given
 *
 * @param <O> options that can be chosen
 * @author Oliver Krauss
 * @since 1.0
 */
public class ConfigurableChoice<O extends Configurable> extends IteratingChoice<O, Descriptor> {

    public ConfigurableChoice(String name, Class<? extends Configurable> optionClass) {
        super(name, optionClass);
    }

    @Override
    protected void rebuildObject() {
        // re-build the entire object to ENSURE that no global state is held
        try {
            choice = (O) optionClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        fixedOptions.forEach(x -> {
                    x.reset();
                    choice.setOption(x.getName(), x.next());
                }
        );
        configurationOptions.forEach(x -> choice.setOption(x.getName(), x.current() == null ? x.next() : x.current()));
    }

}
