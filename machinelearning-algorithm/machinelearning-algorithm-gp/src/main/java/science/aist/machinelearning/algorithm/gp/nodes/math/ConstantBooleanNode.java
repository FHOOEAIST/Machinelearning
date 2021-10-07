package science.aist.machinelearning.algorithm.gp.nodes.math;

import science.aist.machinelearning.algorithm.gp.CacheableGPGraphNode;
import science.aist.machinelearning.algorithm.gp.ValueContainer;
import science.aist.machinelearning.core.options.Descriptor;

import java.util.HashMap;
import java.util.Map;

/**
 * Node that contains a constant Boolean.
 *
 * @author Daniel Wilfing
 * @since 1.0
 */
public class ConstantBooleanNode extends CacheableGPGraphNode<Boolean> implements ValueContainer<Boolean> {

    private Boolean constant = false;

    @Override
    public Boolean execute() {
        return getValue();
    }

    @Override
    public Boolean simpleReturnType() {
        return false;
    }

    @Override
    public Map<String, Descriptor> getOptions() {
        Map<String, Descriptor> options = new HashMap<>();

        options.put("value", new Descriptor<>(constant));

        return options;
    }

    @Override
    public boolean setOptions(Map<String, Descriptor> options) {
        for (Map.Entry<String, Descriptor> entry : options.entrySet()) {
            //check if we can successfully set the option
            if (!setOption(entry.getKey(), entry.getValue())) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Boolean calculateValue() {
        return constant;
    }

    @Override
    public boolean setOption(String name, Descriptor descriptor) {
        try {
            if (name.equals("value")) {
                setValue((Boolean) descriptor.getValue());
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public Boolean getValue() {
        return constant;
    }

    public void setValue(Boolean value) {
        this.constant = value;
    }
}
