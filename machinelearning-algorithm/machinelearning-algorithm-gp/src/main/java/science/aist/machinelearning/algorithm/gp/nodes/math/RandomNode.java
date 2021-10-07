package science.aist.machinelearning.algorithm.gp.nodes.math;

import science.aist.machinelearning.algorithm.gp.FunctionalGPGraphNode;
import science.aist.machinelearning.core.options.Descriptor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Simple node that creates a random number between its given children.
 *
 * @author Daniel Wilfing
 * @since 1.0
 */
public class RandomNode extends FunctionalGPGraphNode<Double> {

    private final Random r = new Random();

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
        double rangeMin = (Double) getChildNodes().get(0).execute();
        double rangeMax = (Double) getChildNodes().get(1).execute();

        if (rangeMax < rangeMin) {
            double cup = rangeMax;
            rangeMax = rangeMin;
            rangeMin = cup;
        }

        return rangeMin + (rangeMax - rangeMin) * r.nextDouble();
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
