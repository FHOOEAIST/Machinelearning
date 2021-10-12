/*
 * Copyright (c) 2021 the original author or authors.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package science.aist.machinelearning.algorithm.gp.util;

import science.aist.machinelearning.algorithm.gp.*;
import science.aist.machinelearning.algorithm.gp.nodes.basic.ResultNode;
import science.aist.machinelearning.algorithm.gp.nodes.heuristic.CrossoverNode;
import science.aist.machinelearning.algorithm.gp.nodes.heuristic.MutatorNode;
import science.aist.machinelearning.algorithm.gp.nodes.heuristic.SolutionCreatorNode;
import science.aist.machinelearning.algorithm.gp.nodes.programming.*;
import science.aist.machinelearning.core.Solution;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * Contains methods that are useful for many different nodes.
 *
 * @author Daniel Wilfing
 * @since 1.0
 */
public class BasicNodeUtil {

    /**
     * Removes all the objects with the given value from the collection.
     *
     * @param collection collection to filer
     * @param remove     object to remove from the collection
     * @param <T>        Generic-type
     */
    public static <T> void removeAllGivenValueFromCollection(Collection<T> collection, T remove) {
        collection.removeIf(next -> next == remove);
    }

    /**
     * Reconstructs a copy of the given graph. Starts by the ResultNode and builds its way up.
     *
     * @param originalRoot root of the graph to copy
     * @return newly built graph which is exactly the same as the given graph
     */
    public static ResultNode deepCopyForGraph(ResultNode originalRoot) {
        ResultNode root = new ResultNode();
        try {
            reconstructNode(originalRoot, root, new HashMap<>());
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return root;
    }

    /**
     * Reconstructs a copy of the given graph.
     *
     * @param originalNode node of the graph to copy
     * @param newNode      node of the new graph that gets built
     * @param oldToNew     map of old to new nodes
     */
    private static void reconstructNode(GPGraphNode originalNode, GPGraphNode newNode, Map<GPGraphNode, GPGraphNode> oldToNew) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {

        //set the settings
        newNode.setOptions(originalNode.getOptions());

        //activate caching
        if (originalNode instanceof CacheableGPGraphNode && ((CacheableGPGraphNode) originalNode).isCached()) {
            ((CacheableGPGraphNode) newNode).setCached(true);
        }

        //find and reconstruct children
        if (originalNode instanceof FunctionalGPGraphNode) {
            FunctionalGPGraphNode castedOriginalNode = (FunctionalGPGraphNode) originalNode;
            FunctionalGPGraphNode castedNewNode = (FunctionalGPGraphNode) newNode;

            //go through the children
            for (GPGraphNode node : (ArrayList<GPGraphNode>) castedOriginalNode.getChildNodes()) {
                if (!oldToNew.containsKey(node)) {
                    GPGraphNode newChild;
                    //if the node is generic, we have to call a specifc constructor
                    if (node instanceof GenericFunctionalGPGraphNode) {
                        GenericFunctionalGPGraphNode genericNode = (GenericFunctionalGPGraphNode) node;
                        newChild = node.getClass().getConstructor(Class.class).newInstance(genericNode.getClazz());
                    } else if (node instanceof GenericFunctionalCollectionGPGraphNode) {
                        GenericFunctionalCollectionGPGraphNode genericNode = (GenericFunctionalCollectionGPGraphNode) node;
                        newChild = node.getClass().getConstructor(Class.class).newInstance(genericNode.getClazz());
                    }
                    //otherwise, call basic constructor
                    else {
                        newChild = node.getClass().getConstructor().newInstance();
                    }

                    oldToNew.put(node, newChild);

                    reconstructNode(node, newChild, oldToNew);
                }
                castedNewNode.addChildNode(oldToNew.get(node));
            }

        }

    }

    /**
     * Counts how many nodes there are in a graph and returns the number.
     *
     * @param node start counting from this graph
     * @return number of nodes in the graph
     */
    public static int numberOfNodesInGraph(GPGraphNode node) {
        Collection<GPGraphNode> nodes = new HashSet<>();
        startCountingNodes(node, nodes);
        return nodes.size();
    }

    /**
     * Counts how many nodes there are in a graph.
     *
     * @param node         count this graph and children
     * @param visitedNodes remember which nodes have already been visited
     */
    private static void startCountingNodes(GPGraphNode node, Collection<GPGraphNode> visitedNodes) {
        visitedNodes.add(node);

        if (node instanceof FunctionalGPGraphNode) {
            FunctionalGPGraphNode castNode = (FunctionalGPGraphNode) node;

            for (GPGraphNode child : (ArrayList<GPGraphNode>) castNode.getChildNodes()) {
                if (!visitedNodes.contains(child)) {
                    startCountingNodes(child, visitedNodes);
                }
            }
        }
    }

    /**
     * Returns the depth of a specific node. Requires root to correctly calculate the depth.
     *
     * @param root            root of the graph
     * @param nodeToFindDepth find depth of this node
     * @return first found depth of the node in the graph, -1 if it coulnd't be found
     */
    public static int depthOfNode(ResultNode root, GPGraphNode nodeToFindDepth) {
        return depthOfNode(root, nodeToFindDepth, new ArrayList<>(), 0);
    }

    /***
     * Returns the depth of a specific node.
     *
     * @param node              current node to check
     * @param find              find depth of this node
     * @param visitedNodes      nodes that have been checked before
     * @param currentDepth      current depth of the search
     * @return the depth of a specific node
     */
    private static int depthOfNode(GPGraphNode node, GPGraphNode find, Collection<GPGraphNode> visitedNodes, int currentDepth) {

        if (node == find) {
            return currentDepth;
        }

        if (node instanceof FunctionalGPGraphNode) {
            FunctionalGPGraphNode castedNode = (FunctionalGPGraphNode) node;

            for (GPGraphNode child : (ArrayList<GPGraphNode>) castedNode.getChildNodes()) {
                if (!visitedNodes.contains(child)) {
                    visitedNodes.add(child);
                    int depthOfChild = depthOfNode(child, find, visitedNodes, currentDepth + 1);

                    if (depthOfChild != -1) {
                        return depthOfChild;
                    }
                }
            }
        }
        return -1;
    }

    /**
     * Creates a map with returnTypes and their fitting nodes that have been found in the graph.
     *
     * @param node node to search returnTypes in
     * @return Map containing the classes of return-methods and their fitting nodes
     */
    public static Map<Class, ArrayList<GPGraphNode>> nodesWithReturnType(GPGraphNode node) {
        Map<Class, ArrayList<GPGraphNode>> returnTypes = new HashMap<>();
        findAndSortByReturnType(node, returnTypes, new ArrayList<>());
        return returnTypes;
    }

    /**
     * Checks the returnType of the node and collects them into map.
     *
     * @param node         node to check
     * @param foundNodes   map with returnTypes and nodes
     * @param visitedNodes nodes that have already been checked
     */
    private static void findAndSortByReturnType(GPGraphNode node, Map<Class, ArrayList<GPGraphNode>> foundNodes, ArrayList<GPGraphNode> visitedNodes) {

        //add the currentNode to the existing ones, so other nodes may call it
        //but only if its not the ResultNode, that one should never by used by any other node
        if (!node.getClass().equals(ResultNode.class)) {

            Class currentClass;
            if (node.simpleReturnType() instanceof Collection) {
                currentClass = Collection.class;
            } else if (node.simpleReturnType() instanceof Number) {
                currentClass = Number.class;
            } else {
                currentClass = node.simpleReturnType().getClass();
            }

            //check if we already have a list of nodes that return a certain class
            if (!foundNodes.containsKey(currentClass)) {
                foundNodes.put(currentClass, new ArrayList<>());
            }
            ArrayList<GPGraphNode> nodesForSpecificClass = foundNodes.get(currentClass);
            //check if we already added this node before (happens if we reuse an older node)
            if (!nodesForSpecificClass.contains(node)) {
                nodesForSpecificClass.add(node);
            }
        }

        //check for children of the graph
        if (node instanceof FunctionalGPGraphNode) {
            FunctionalGPGraphNode castedNode = (FunctionalGPGraphNode) node;

            for (GPGraphNode child : (ArrayList<GPGraphNode>) castedNode.getChildNodes()) {
                if (!visitedNodes.contains(child)) {
                    findAndSortByReturnType(child, foundNodes, visitedNodes);
                }
            }
        }
    }

    /**
     * Resets all caches of the graph. Finds cacheable nodes and resets their values, thus forcing a new calculation.
     *
     * @param node node and graph to reset
     */
    public static void resetCaches(GPGraphNode node) {
        resetCaches(node, new ArrayList<>());
    }

    /**
     * Resets all caches of the graph. Finds cacheable nodes and resets their values, thus forcing a new calculation.
     *
     * @param node         node and graph to reset
     * @param visitedNodes nodes that have already been checked
     */
    private static void resetCaches(GPGraphNode node, Collection<GPGraphNode> visitedNodes) {

        //check if we the current node caches its result
        if (node instanceof CacheableGPGraphNode) {
            //if it can cache the result, set the value back to null
            CacheableGPGraphNode castedNode = (CacheableGPGraphNode) node;
            castedNode.setCachedValue(null);

            //afterwards, check if the current node has some children
            if (castedNode instanceof FunctionalGPGraphNode) {
                FunctionalGPGraphNode functionalNode = (FunctionalGPGraphNode) castedNode;

                for (GPGraphNode child : (ArrayList<GPGraphNode>) functionalNode.getChildNodes()) {
                    if (!visitedNodes.contains(child)) {
                        visitedNodes.add(child);
                        resetCaches(child, visitedNodes);
                    }
                }
            }
        }
    }

    /**
     * Checks if the node is an InterruptibleNode. If yes, will try to call its interrupt-method.
     *
     * @param node  node and graph to interrupt
     * @param value true = interrupt the graph, false = don't interrupt, stop interruption
     */
    public static void interruptGraph(GPGraphNode node, boolean value) {
        interruptGraph(node, new ArrayList<>(), value);
    }

    /**
     * Checks if the node is an InterruptibleNode. If yes, will try to call its interrupt-method.
     *
     * @param node         node and graph to interrupt
     * @param visitedNodes the visited nodes
     */
    private static void interruptGraph(GPGraphNode node, Collection<GPGraphNode> visitedNodes, boolean value) {
        if (node instanceof InterruptibleNode) {
            InterruptibleNode casted = (InterruptibleNode) node;
            casted.interrupt(value);
        }

        //afterwards, check if the current node has some children
        if (node instanceof FunctionalGPGraphNode) {
            FunctionalGPGraphNode functionalNode = (FunctionalGPGraphNode) node;

            for (GPGraphNode child : (ArrayList<GPGraphNode>) functionalNode.getChildNodes()) {
                if (!visitedNodes.contains(child)) {
                    visitedNodes.add(child);
                    interruptGraph(child, visitedNodes, value);
                }
            }
        }
    }

    /**
     * Will calculate how many solutions will probably be created during the execution of a heuristic.
     *
     * @param node node to check for solutions
     * @return number of solutions created by a heuristic
     */
    public static int solutionsCreatedByGraph(GPGraphNode node) {
        if (node instanceof SolutionCreatorNode) {
            return 1;
        }

        if (node instanceof FunctionalGPGraphNode) {

            FunctionalGPGraphNode castedNode = (FunctionalGPGraphNode) node;
            ArrayList<GPGraphNode> children = (ArrayList<GPGraphNode>) castedNode.getChildNodes();

            if (node instanceof CollectionMergeNode) {
                return solutionsCreatedByGraph(children.get(0)) + solutionsCreatedByGraph(children.get(1));
            } else if (node instanceof ForNode) {
                ForNode forNode = (ForNode) castedNode;

                if (forNode.getClazz().equals(Solution.class)) {
                    int max = ((Double) children.get(0).execute()).intValue();
                    return max * solutionsCreatedByGraph(children.get(1));
                }
            } else if (node instanceof ForCollectionNode) {
                ForCollectionNode forCollectionNode = (ForCollectionNode) castedNode;

                if (forCollectionNode.getClazz().equals(Solution.class)) {
                    int max = ((Double) children.get(0).execute()).intValue();
                    return max * solutionsCreatedByGraph(children.get(1));
                }
            } else if (node instanceof WhileNode) {
                WhileNode whileNode = (WhileNode) node;

                if (whileNode.getClazz().equals(Solution.class)) {
                    int max = whileNode.getMaxIterations();
                    return max * solutionsCreatedByGraph(children.get(1));
                }
            } else if (node instanceof WhileCollectionNode) {
                WhileCollectionNode whileCollectionNode = (WhileCollectionNode) node;

                if (whileCollectionNode.getClazz().equals(Solution.class)) {
                    int max = whileCollectionNode.getMaxIterations();
                    return max * solutionsCreatedByGraph(children.get(1));
                }
            } else if (node instanceof MutatorNode || node instanceof CrossoverNode) {
                return 1 + solutionsCreatedByGraph(children.get(0));
            } else {
                int solutions = 0;
                for (GPGraphNode child : children) {
                    solutions += solutionsCreatedByGraph(child);
                }
                return solutions;
            }
        }


        return 0;
    }
}
