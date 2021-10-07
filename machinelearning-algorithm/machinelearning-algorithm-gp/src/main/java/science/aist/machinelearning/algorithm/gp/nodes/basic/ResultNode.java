package science.aist.machinelearning.algorithm.gp.nodes.basic;

import science.aist.machinelearning.algorithm.gp.FunctionalGPGraphNode;
import science.aist.machinelearning.algorithm.gp.util.BasicNodeUtil;
import science.aist.machinelearning.core.Solution;
import science.aist.machinelearning.core.options.Descriptor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Root-Node of the tree that returns the result of the tree-evaluation.
 *
 * @author Daniel Wilfing
 * @since 1.0
 */
public class ResultNode extends FunctionalGPGraphNode<Solution> {

    @Override
    public boolean checkValidity() {
        return getChildNodes().size() == 1 &&
                getChildNodes().get(0).simpleReturnType() instanceof Solution;
    }

    @Override
    public ArrayList<Class> requiredClassesForChildren() {
        ArrayList<Class> classes = new ArrayList<>();
        classes.add(Solution.class);
        return classes;
    }

    @Override
    public Solution calculateValue() {

        //reset the caches
        //caches have to be reset, or multiple calls of this heuristic would still return the old results
        BasicNodeUtil.resetCaches(getChildNodes().get(0));
        BasicNodeUtil.interruptGraph(this, false);

        return (Solution) getChildNodes().get(0).execute();
    }

    @Override
    public Solution simpleReturnType() {
        return new Solution();
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
