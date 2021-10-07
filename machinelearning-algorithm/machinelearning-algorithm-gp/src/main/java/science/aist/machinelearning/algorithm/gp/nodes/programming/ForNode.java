package science.aist.machinelearning.algorithm.gp.nodes.programming;

import science.aist.machinelearning.algorithm.gp.GenericFunctionalGPGraphNode;
import science.aist.machinelearning.algorithm.gp.InterruptibleNode;
import science.aist.machinelearning.core.options.Descriptor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Generic For that runs depending on the numbers in the first two children. Will return the last data given by the
 * third child (useful in combination with caching).
 *
 * @author Daniel Wilfing
 * @since 1.0
 */
public class ForNode<T> extends GenericFunctionalGPGraphNode<T> implements InterruptibleNode {

    /**
     * interrupt flag that stops the loop
     */
    private boolean interrupt = false;

    public ForNode(Class<T> clazz) {
        super(clazz);
    }

    @Override
    public boolean checkValidity() {
        return getChildNodes().size() == 2 &&
                getChildNodes().get(0).simpleReturnType() instanceof Number &&
                clazz.isAssignableFrom(getChildNodes().get(1).simpleReturnType().getClass());
    }

    @Override
    public ArrayList<Class> requiredClassesForChildren() {
        ArrayList<Class> classes = new ArrayList<>();
        classes.add(Number.class);
        classes.add(clazz);
        return classes;
    }

    @Override
    public T calculateValue() {
        int max = ((Double) getChildNodes().get(0).execute()).intValue();
        T object = null;


        for (int i = 0; i < max; i++) {
            object = (T) getChildNodes().get(1).execute();

            if (interrupt) {
                break;
            }
        }

        return object;
    }

    @Override
    public Map<String, Descriptor> getOptions() {
        return new HashMap<>();
    }

    @Override
    public boolean setOption(String name, Descriptor descriptor) {
        return true;
    }

    @Override
    public void interrupt(boolean value) {
        interrupt = value;
    }
}
