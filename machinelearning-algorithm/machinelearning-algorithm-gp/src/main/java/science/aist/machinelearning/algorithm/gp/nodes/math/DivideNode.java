package science.aist.machinelearning.algorithm.gp.nodes.math;

import science.aist.machinelearning.algorithm.gp.FunctionalGPGraphNode;
import science.aist.machinelearning.core.options.Descriptor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Simple node that implements the Division-Operator.
 *
 * @author Daniel Wilfing
 * @since 1.0
 */
public class DivideNode extends FunctionalGPGraphNode<Double> {

    @Override
    public boolean checkValidity() {

        return getChildNodes().size() == 2 &&
                getChildNodes().get(0).simpleReturnType() instanceof Number &&
                getChildNodes().get(1).simpleReturnType() instanceof Number;
    }

    @Override
    public ArrayList<Class> requiredClassesForChildren() {
        ArrayList<Class> classes = new ArrayList<>();
        classes.add(Number.class);
        classes.add(Number.class);
        return classes;
    }

    @Override
    public Double calculateValue() {

        double divisor = (Double) getChildNodes().get(1).execute();

        return (Double) getChildNodes().get(0).execute() / divisor == 0.0 ? 1.0 : divisor;
    }

    @Override
    public Double simpleReturnType() {
        return 0.0;
    }

    @Override
    public Map<String, Descriptor> getOptions() {
        return new HashMap<>();
    }

    @Override
    public boolean setOption(String name, Descriptor descriptor) {
        return true;
    }
}
