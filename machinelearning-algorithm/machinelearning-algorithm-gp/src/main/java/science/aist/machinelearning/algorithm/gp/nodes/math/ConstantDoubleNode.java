package science.aist.machinelearning.algorithm.gp.nodes.math;


import science.aist.machinelearning.algorithm.gp.CacheableGPGraphNode;
import science.aist.machinelearning.algorithm.gp.ValueContainer;
import science.aist.machinelearning.core.options.Descriptor;

import java.util.HashMap;
import java.util.Map;

/**
 * Node that contains a constant double.
 *
 * @author Daniel Wilfing
 * @since 1.0
 */
public class ConstantDoubleNode extends CacheableGPGraphNode<Double> implements ValueContainer<Double> {

    protected Double constant = 0.0;

    @Override
    public Double execute() {
        return getValue();
    }

    @Override
    public Double simpleReturnType() {
        return 0.0;
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
    public Double calculateValue() {
        return constant;
    }

    @Override
    public boolean setOption(String name, Descriptor descriptor) {
        try {
            if (name.equals("value")) {
                setValue((Double) descriptor.getValue());
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public Double getValue() {
        return constant;
    }

    public void setValue(Double value) {
        this.constant = value;
    }
}
