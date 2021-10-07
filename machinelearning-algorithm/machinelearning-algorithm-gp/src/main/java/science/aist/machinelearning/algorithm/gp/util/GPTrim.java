package science.aist.machinelearning.algorithm.gp.util;

import science.aist.machinelearning.algorithm.gp.CacheableGPGraphNode;
import science.aist.machinelearning.algorithm.gp.FunctionalGPGraphNode;
import science.aist.machinelearning.algorithm.gp.GPGraphNode;
import science.aist.machinelearning.algorithm.gp.nodes.basic.ResultNode;
import science.aist.machinelearning.algorithm.gp.nodes.math.*;
import science.aist.machinelearning.algorithm.gp.nodes.programming.CacheTraderCollectionNode;
import science.aist.machinelearning.algorithm.gp.nodes.programming.CacheTraderNode;
import science.aist.machinelearning.algorithm.gp.nodes.programming.IfThenElseNode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.ListIterator;

/**
 * Class that tries to find nodes in the graph that are unnecessary. Will then replace those nodes in hopes of
 * significantly trimming the graph.
 *
 * @author Daniel Wilfing
 * @since 1.0
 */
public class GPTrim {

    /**
     * Trims the graph by replacing unnecessary nodes with more fitting nodes.
     *
     * @param node root of the graph to trim
     * @return trimmed graph
     */
    public static ResultNode trimGraph(ResultNode node) {
        ResultNode graphToTrim = BasicNodeUtil.deepCopyForGraph(node);

        trimGraph(node, new ArrayList<>());

        return graphToTrim;
    }

    /**
     * Trims the graph by replacing unnecessary nodes with more fitting nodes. Has a set of rules and trims the graph
     * accordingly.
     *
     * @param node         node that gets trimmed
     * @param visitedNodes nodes that have already been visited and trimmed
     * @return node after trimming process
     */
    private static GPGraphNode trimGraph(GPGraphNode node, Collection<GPGraphNode> visitedNodes) {

        //we can only trim a node that has children
        if (node instanceof FunctionalGPGraphNode) {
            FunctionalGPGraphNode castedNode = (FunctionalGPGraphNode) node;

            ArrayList<GPGraphNode> children = (ArrayList<GPGraphNode>) castedNode.getChildNodes();

            for (ListIterator<GPGraphNode> childIterator = children.listIterator(); childIterator.hasNext(); ) {
                GPGraphNode child = childIterator.next();

                if (!visitedNodes.contains(child)) {
                    visitedNodes.add(child);

                    //set the node we get from the trimming process (might be a trimmed one or the old one)
                    childIterator.set(trimGraph(child, visitedNodes));
                }
            }

            //check if we can trim the current node

            //if the node is a cacheTrader, but neither children can or is caching stuff
            //then we can replace it with the first child and throw the second child away
            if (castedNode instanceof CacheTraderNode || castedNode instanceof CacheTraderCollectionNode) {

                if ((!(children.get(0) instanceof CacheableGPGraphNode) || !((CacheableGPGraphNode) children.get(0)).isCached()) &&
                        (!(children.get(1) instanceof CacheableGPGraphNode) || !((CacheableGPGraphNode) children.get(1)).isCached())) {
                    return children.get(0);
                }
            }

            //if the node is an or/and node and both children are constants that don't get cached
            //then just replace it with a constant that has already calculated the correct result
            else if (castedNode instanceof OrNode || castedNode instanceof AndNode) {

                if (children.get(0) instanceof ConstantBooleanNode && children.get(1) instanceof ConstantBooleanNode) {

                    ConstantBooleanNode node1 = (ConstantBooleanNode) children.get(0);
                    ConstantBooleanNode node2 = (ConstantBooleanNode) children.get(1);

                    if (!node1.isCached() && !node2.isCached()) {
                        ConstantBooleanNode newNode = new ConstantBooleanNode();
                        newNode.setValue(castedNode instanceof OrNode ? node1.getValue() || node2.getValue() : node1.getValue() && node2.getValue());
                        return newNode;
                    }
                }
            }

            //if the node is a less than node and both children are constant numbers that don't get cached
            //then we can replace the node with a constant boolean that has the result
            else if (castedNode instanceof LessThanNode) {

                if (children.get(0) instanceof ConstantDoubleNode && children.get(1) instanceof ConstantDoubleNode) {

                    ConstantDoubleNode node1 = (ConstantDoubleNode) children.get(0);
                    ConstantDoubleNode node2 = (ConstantDoubleNode) children.get(1);

                    if (!node1.isCached() && !node2.isCached()) {
                        ConstantBooleanNode newNode = new ConstantBooleanNode();
                        newNode.setValue(node1.getValue() < node2.getValue());
                        return newNode;
                    }
                }

            }

            //if the node is an add/subtract/exponential/square root node and both children are constant numbers that don't get cached
            //then we can replace the node with a constant number that has the result
            else if (castedNode instanceof AddNode || castedNode instanceof SubtractNode ||
                    castedNode instanceof MultiplyNode || castedNode instanceof DivideNode ||
                    castedNode instanceof ExponentiateNode || castedNode instanceof SquareRootNode) {

                if (children.get(0) instanceof ConstantDoubleNode && children.get(1) instanceof ConstantDoubleNode) {

                    ConstantDoubleNode node1 = (ConstantDoubleNode) children.get(0);
                    ConstantDoubleNode node2 = (ConstantDoubleNode) children.get(1);

                    if (!node1.isCached() && !node2.isCached()) {
                        ConstantDoubleNode newNode = new ConstantDoubleNode();

                        if (castedNode instanceof AddNode) {
                            newNode.setValue(node1.getValue() + node2.getValue());
                        } else if (castedNode instanceof SubtractNode) {
                            newNode.setValue(node1.getValue() - node2.getValue());
                        } else if (castedNode instanceof MultiplyNode) {
                            newNode.setValue(node1.getValue() * node2.getValue());
                        } else if (castedNode instanceof DivideNode) {
                            newNode.setValue(node1.getValue() / (node2.getValue() == 0 ? 1 : node2.getValue()));
                        } else if (castedNode instanceof ExponentiateNode) {
                            newNode.setValue(Math.pow(node1.getValue(), node2.getValue()));
                        } else {
                            newNode.setValue(Math.pow(node1.getValue(), 1 / node2.getValue()));
                        }
                        return newNode;
                    }
                }
            }

            //if the node is an IfThenElse node and the constraint is a constant boolean that doesn't get cached
            //then replace the if with the result depending on the boolean
            else if (castedNode instanceof IfThenElseNode) {

                if (children.get(0) instanceof ConstantBooleanNode) {

                    ConstantBooleanNode node1 = (ConstantBooleanNode) children.get(0);

                    if (!node1.isCached()) {
                        if (node1.getValue()) {
                            return children.get(1);
                        } else {
                            return children.get(2);
                        }
                    }

                }

            }

        }

        return node;
    }
}
