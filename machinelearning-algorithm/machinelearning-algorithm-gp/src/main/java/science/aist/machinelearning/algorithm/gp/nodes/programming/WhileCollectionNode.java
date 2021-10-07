package science.aist.machinelearning.algorithm.gp.nodes.programming;

import science.aist.machinelearning.algorithm.gp.GenericFunctionalCollectionGPGraphNode;
import science.aist.machinelearning.algorithm.gp.InterruptibleNode;
import science.aist.machinelearning.core.options.Descriptor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Generic While that runs as long as the first child ist true. Will collect data from the second child and return it as
 * a collection.
 *
 * @author Daniel Wilfing
 * @since 1.0
 */
public class WhileCollectionNode<T> extends GenericFunctionalCollectionGPGraphNode<T> implements InterruptibleNode {

    /**
     * Prevents endless whiles. Will try to fulfill the condition of the while, but will break after this number of
     * iterations have been done.
     * <p>
     * If this value is set to -1, then will ignore this condition (may cause endless while-loops).
     */
    private int maxIterations = 10000;

    /**
     * interrupt flag that stops the loop
     */
    private boolean interrupt = false;

    public WhileCollectionNode(Class<T> clazz) {
        super(clazz);
    }

    @Override
    public boolean checkValidity() {
        return getChildNodes().size() == 2 &&
                getChildNodes().get(0).simpleReturnType() instanceof Boolean &&
                clazz.isAssignableFrom(getChildNodes().get(1).simpleReturnType().getClass());
    }

    @Override
    public ArrayList<Class> requiredClassesForChildren() {
        ArrayList<Class> classes = new ArrayList<>();
        classes.add(Boolean.class);
        classes.add(clazz);
        return classes;
    }

    @Override
    public ArrayList<T> calculateValue() {
        ArrayList<T> objects = new ArrayList<>();

        int i = 0;
        while ((Boolean) getChildNodes().get(0).execute()) {
            objects.add((T) getChildNodes().get(1).execute());

            if (interrupt) {
                break;
            }

            //break if we get a certain amount of iterations
            if (maxIterations != -1 && i++ >= maxIterations) {
                break;
            }
        }

        return objects;
    }

    @Override
    public Map<String, Descriptor> getOptions() {
        Map<String, Descriptor> options = new HashMap<>();

        options.put("maxIterations", new Descriptor<>(maxIterations));

        return options;
    }

    @Override
    public boolean setOption(String name, Descriptor descriptor) {
        try {
            if (name.equals("maxIterations")) {
                setMaxIterations((Integer) descriptor.getValue());
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public void interrupt(boolean value) {
        interrupt = value;
    }

    public int getMaxIterations() {
        return maxIterations;
    }

    public void setMaxIterations(int maxIterations) {
        this.maxIterations = maxIterations;
    }
}
