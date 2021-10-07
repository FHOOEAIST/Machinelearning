package science.aist.machinelearning.algorithm.mutation;

import science.aist.machinelearning.algorithm.gp.FunctionalGPGraphNode;
import science.aist.machinelearning.algorithm.gp.GPGraphNode;
import science.aist.machinelearning.algorithm.gp.nodes.basic.ResultNode;
import science.aist.machinelearning.algorithm.gp.util.BasicNodeUtil;
import science.aist.machinelearning.algorithm.gp.util.GPRepair;
import science.aist.machinelearning.core.SolutionGene;
import science.aist.machinelearning.problem.GPProblem;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * Replaces random node in the graph with either a fitting new node or an already one.
 *
 * @author Daniel Wilfing
 * @since 1.0
 */
public class GPReplacingNodeMutator extends RollbackRandomNGenesMutator<ResultNode, GPProblem> {

    private final Random r = new Random();

    private GPRepair repair = null;

    @Override
    protected SolutionGene<ResultNode, GPProblem> createGeneByMutation(SolutionGene<ResultNode, GPProblem> gene) {

        SolutionGene<ResultNode, GPProblem> mutation = new SolutionGene<>();
        mutation.setProblemGenes(gene.getProblemGenes());

        //create a new gene by reconstructing the graph
        ResultNode root = BasicNodeUtil.deepCopyForGraph(gene.getGene());

        //decide which node gets replaced (-1 to remove the rootNode)
        int indexOfNodeToReplace = r.nextInt(BasicNodeUtil.numberOfNodesInGraph(root) - 1) + 1;

        //find that node
        List<GPGraphNode> nodes = new ArrayList<>();
        findNodeWithIndex(root, nodes, indexOfNodeToReplace);
        GPGraphNode replaceMe = nodes.get(nodes.size() - 1);

        //remove that node from all the children
        removeSpecificChild(root, new ArrayList<>(), replaceMe);

        //repair the children
        GPProblem problem = gene.getProblemGenes().get(0).getGene();
        try {
            repair.repairGraph(root, problem.getValidGraphNodes(), problem.getTerminalGraphNodes(), problem.getFunctionalGraphNodes(), problem.getNodeSettings());
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        mutation.setGene(root);

        return mutation;
    }

    /**
     * Keeps adding nodes to the list until a certain number has been reached.
     *
     * @param startFrom    start adding nodes using this node
     * @param visitedNodes list of nodes that have been visisted
     * @param stopAt       stop after this many nodes have been visited
     */
    private void findNodeWithIndex(GPGraphNode startFrom, List<GPGraphNode> visitedNodes, int stopAt) {
        visitedNodes.add(startFrom);

        //if we have not found the correct node yet, keep looking through the children
        if (visitedNodes.size() == stopAt) {
            if (startFrom instanceof FunctionalGPGraphNode) {
                FunctionalGPGraphNode castedNode = (FunctionalGPGraphNode) startFrom;

                for (GPGraphNode child : (ArrayList<GPGraphNode>) castedNode.getChildNodes()) {
                    if (!visitedNodes.contains(child)) {
                        findNodeWithIndex(child, visitedNodes, stopAt);
                    }
                }
            }
        }
    }

    /**
     * Looks through the graph and removes the given node that should be removed. Will remove all relations with the
     * given child.
     *
     * @param node          node to check
     * @param visitedNodes  nodes that have been visited before
     * @param childToRemove node that should be removed from the children of each node
     */
    private void removeSpecificChild(GPGraphNode node, List<GPGraphNode> visitedNodes, GPGraphNode childToRemove) {
        visitedNodes.add(node);

        if (node instanceof FunctionalGPGraphNode) {
            FunctionalGPGraphNode castedNode = (FunctionalGPGraphNode) node;

            for (Iterator<GPGraphNode> iterator = ((ArrayList<GPGraphNode>) castedNode.getChildNodes()).iterator(); iterator.hasNext(); ) {

                GPGraphNode child = iterator.next();

                if (child == childToRemove) {
                    iterator.remove();
                } else if (!visitedNodes.contains(child)) {
                    removeSpecificChild(child, visitedNodes, childToRemove);
                }
            }
        }
    }


    public void setRepair(GPRepair repair) {
        this.repair = repair;
    }
}
