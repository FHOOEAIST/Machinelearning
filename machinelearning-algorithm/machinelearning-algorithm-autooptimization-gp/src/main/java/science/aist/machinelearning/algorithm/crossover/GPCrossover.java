package science.aist.machinelearning.algorithm.crossover;

import science.aist.machinelearning.algorithm.ga.Crossover;
import science.aist.machinelearning.algorithm.ga.Selector;
import science.aist.machinelearning.algorithm.gp.FunctionalGPGraphNode;
import science.aist.machinelearning.algorithm.gp.GPGraphNode;
import science.aist.machinelearning.algorithm.gp.nodes.basic.ResultNode;
import science.aist.machinelearning.algorithm.gp.util.BasicNodeUtil;
import science.aist.machinelearning.algorithm.gp.util.GPValidator;
import science.aist.machinelearning.core.ProblemGene;
import science.aist.machinelearning.core.Solution;
import science.aist.machinelearning.core.SolutionGene;
import science.aist.machinelearning.problem.GPProblem;

import java.util.*;

/**
 * Crossover for GP-graphs. Tries to find a node that can be traded and then exchanges them.
 *
 * @author Daniel Wilfing
 * @since 1.0
 */
public class GPCrossover implements Crossover<ResultNode, GPProblem> {

    private final Random r = new Random();

    @Override
    public Solution<ResultNode, GPProblem> breed(List<Solution<ResultNode, GPProblem>> population, Selector<ResultNode, GPProblem> selector) {

        if (population != null && population.size() > 0 && selector != null) {
            if (population.size() == 1) {
                return population.get(0);
            }

            Solution<ResultNode, GPProblem> a = selector.select(population);
            Solution<ResultNode, GPProblem> b = selector.select(population);

            //create deep copy of the graphs, or the crossover would cause changes in both graphs
            ResultNode aRoot = BasicNodeUtil.deepCopyForGraph(a.getSolutionGenes().get(0).getGene());
            ResultNode bRoot = BasicNodeUtil.deepCopyForGraph(b.getSolutionGenes().get(0).getGene());

            //find node that returns the same thing in both graphs (except ResultNode)
            Map<Class, ArrayList<GPGraphNode>> typesOfA = BasicNodeUtil.nodesWithReturnType(aRoot);
            Map<Class, ArrayList<GPGraphNode>> typesOfB = BasicNodeUtil.nodesWithReturnType(bRoot);

            //if either graph has no returnType (graph is only a terminal), then we can't crossover
            if (typesOfA.keySet().size() == 0 || typesOfB.keySet().size() == 0) {
                return population.get(r.nextInt(2));
            }

            //pick a random class
            //do this by first shuffling, then taking one class after another from the first set
            ArrayList<Class> classesToShuffle = new ArrayList<>(typesOfA.keySet());
            Collections.shuffle(classesToShuffle);

            Class sharedClass = null;
            for (Class clazz : classesToShuffle) {
                if (typesOfB.containsKey(clazz)) {
                    sharedClass = clazz;
                    break;
                }
            }

            //if the graphs don't share a class, return a random solution
            if (sharedClass == null) {
                return population.get(r.nextInt(2));
            }

            //replace a random node in a with a random node in b
            ArrayList<GPGraphNode> aNodes = typesOfA.get(sharedClass);
            GPGraphNode aNode = aNodes.get(r.nextInt(aNodes.size()));

            ArrayList<GPGraphNode> bNodes = typesOfB.get(sharedClass);
            GPGraphNode bNode = bNodes.get(r.nextInt(bNodes.size()));

            replaceAllNodesWithOtherNode(aRoot, aNode, bNode, new ArrayList<>());

            //if something still went wrong when combining (possible with some weird collection combinations)
            //then we drop the crossover and return a random solution
            if (!GPValidator.validateGraph(aRoot)) {
                GPValidator.validateGraph(aRoot);
                return population.get(r.nextInt(2));
            }

            //return the new solution
            Solution<ResultNode, GPProblem> crossoverSolution = new Solution<>();
            List<ProblemGene<GPProblem>> problemGenes = a.getSolutionGenes().get(0).getProblemGenes();
            crossoverSolution.addGene(new SolutionGene<>(aRoot, problemGenes));

            return crossoverSolution;
        }

        return null;
    }

    /**
     * Checks if one of the children is the node that should be replaced. Will then replace it with the substitute
     * Node.
     *
     * @param currentNode    check children of this node
     * @param replaceNode    node that should get replaced
     * @param substituteNode new node for replacement
     * @param visitedNodes   nodes that have already been checked
     */
    private void replaceAllNodesWithOtherNode(GPGraphNode currentNode, GPGraphNode replaceNode, GPGraphNode substituteNode, Collection<GPGraphNode> visitedNodes) {

        if (currentNode instanceof FunctionalGPGraphNode) {
            FunctionalGPGraphNode castedNode = (FunctionalGPGraphNode) currentNode;

            for (ListIterator<GPGraphNode> iterator = ((ArrayList<GPGraphNode>) castedNode.getChildNodes()).listIterator(); iterator.hasNext(); ) {
                GPGraphNode child = iterator.next();

                //if the child is equal the node to replace, then replace it with the substitute
                if (child == replaceNode) {
                    iterator.set(substituteNode);
                }
                if (!visitedNodes.contains(child)) {
                    visitedNodes.add(child);
                    replaceAllNodesWithOtherNode(child, replaceNode, substituteNode, visitedNodes);
                }
            }
        }

    }

}
