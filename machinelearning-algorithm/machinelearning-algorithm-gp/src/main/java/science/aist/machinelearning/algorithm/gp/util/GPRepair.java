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
import science.aist.machinelearning.core.options.Descriptor;
import science.aist.machinelearning.core.options.ListDescriptor;
import science.aist.machinelearning.core.options.MinMaxDescriptor;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * Finds missing or wrong children and repairs them. Creates either subgraphs for the missing children or may reuse
 * other children.
 *
 * @author Daniel Wilfing
 * @since 1.0
 */
public class GPRepair {

    private final Random r = new Random();
    /**
     * Max depth of the graph. If this depth is reached, will try to set terminal nodes to stop the graph from growing.
     */
    private int maxDepth = 10;
    /**
     * Minimum depth of the graph. Graph will try to only use functional nodes, until this depth has been reached. Then
     * terminal nodes can be used as well.
     */
    private int minDepth = 1;
    /**
     * Probability that a node is cached (if it is cacheable).
     */
    private double cachedNodeProbability = 0.20;
    /**
     * Probability that we reuse an old node instead of creating a new one (if possible).
     */
    private double reuseNodeProbability = 0.20;
    /**
     * Gets set to true when a node gets newly instantiated. Required so that other methods know if its a new or old
     * node.
     */
    private boolean newlyCreated = false;

    public GPRepair() {
    }

    public GPRepair(int maxDepth, int minDepth, double cachedNodeProbability, double reuseNodeProbability) {
        this.maxDepth = maxDepth;
        this.minDepth = minDepth;
        this.cachedNodeProbability = cachedNodeProbability;
        this.reuseNodeProbability = reuseNodeProbability;
    }

    /**
     * Repair this graph by finding missing children and set a correct node for them.
     *
     * @param node        node and subgraph to repair
     * @param validNodes  all nodes that can be used for repairing
     * @param terminals   terminals that should be used once a certain depth has been reached
     * @param functionals functional nodes that broaden the grap
     * @param settings    settings for the initialisation of specific nodes
     */
    public void repairGraph(GPGraphNode node, Map<Class, ArrayList<GPGraphNode>> validNodes, Map<Class, ArrayList<GPGraphNode>> terminals, Map<Class, ArrayList<GPGraphNode>> functionals, Map<Class, Map<String, Descriptor>> settings) throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {

        //find existing nodes in this subgraph and sort them according to their ReturnType
        Map<Class, ArrayList<GPGraphNode>> existingNodes = BasicNodeUtil.nodesWithReturnType(node);

        //find and repair missing children
        repairGraph(node, validNodes, terminals, functionals, existingNodes, new ArrayList<>(), 0, settings);
    }


    /**
     * Repair this graph by finding missing children and set a correct node for them.
     *
     * @param node          node and subgraph to repair
     * @param validNodes    all nodes that can be used for repairing
     * @param terminals     terminals that should be used once a certain depth has been reached
     * @param functionals   functional nodes that broaden the grap
     * @param existingNodes nodes that are available in the subgraph
     * @param visitedNodes  nodes that have been checked and repaired
     * @param currentDepth  currentDepth of the graph
     * @param settings      settings for the initialisation of specific nodes
     */
    private void repairGraph(GPGraphNode node, Map<Class, ArrayList<GPGraphNode>> validNodes, Map<Class, ArrayList<GPGraphNode>> terminals, Map<Class, ArrayList<GPGraphNode>> functionals, Map<Class, ArrayList<GPGraphNode>> existingNodes, Collection<GPGraphNode> visitedNodes, int currentDepth, Map<Class, Map<String, Descriptor>> settings) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {

        if (node instanceof FunctionalGPGraphNode) {
            FunctionalGPGraphNode castedNode = (FunctionalGPGraphNode) node;

            //check if the graph has the correct number of children and settings
            if (!castedNode.checkValidity()) {
                //set the settings
                addGraphSettings(castedNode, settings.get(castedNode.getClass()));

                //check for children
                boolean collection = false;
                ArrayList<GPGraphNode> children = (ArrayList<GPGraphNode>) castedNode.getChildNodes();
                int currentPos = 0;
                ArrayList<Class> classes = (ArrayList<Class>) castedNode.requiredClassesForChildren();
                for (Class clazz : classes) {
                    //child either doesn't exist or is wrong
                    //have to create a new fitting child and add it to the node
                    if (collection || children.size() <= currentPos || !clazz.isAssignableFrom(children.get(currentPos).simpleReturnType().getClass())) {
                        GPGraphNode newNode = null;

                        //we can pick whatever class we want, except Collection
                        if (clazz.equals(Object.class)) {
                            ArrayList<Class> keys = new ArrayList<>(validNodes.keySet());
                            //lets pick something until we get something that isn't a collection
                            while (clazz.equals(Collection.class) || clazz.equals(Object.class)) {
                                clazz = keys.get(r.nextInt(keys.size()));
                            }
                        }

                        //check if the previous class was collection
                        if (collection) {
                            collection = false;
                            newNode = pickNodeDependingOnClass(validNodes, terminals, functionals, existingNodes, castedNode, currentDepth, clazz, true);

                        }
                        //check if the current class is a collection
                        else if (clazz.equals(Collection.class)) {
                            //set collection to true and wait for the next class
                            collection = true;
                        } else {
                            newNode = pickNodeDependingOnClass(validNodes, terminals, functionals, existingNodes, castedNode, currentDepth, clazz, false);
                        }

                        //if we got a collection, then the next type will specify the collection type
                        //lets wait for the next node until we get to add a child
                        if (!collection) {
                            addGraphSettings(newNode, settings.get(newNode.getClass()));
                            ((ArrayList<GPGraphNode>) castedNode.getChildNodes()).add(currentPos, newNode);

                            if (newlyCreated) {
                                newlyCreated = false;

                                //add the currentNode to the existing ones, so other nodes may call it
                                //but only if its not the ResultNode, that one should never by used by any other node
                                if (!newNode.getClass().equals(ResultNode.class)) {

                                    Class currentClass;
                                    if (newNode.simpleReturnType() instanceof Collection) {
                                        currentClass = Collection.class;
                                    } else if (newNode.simpleReturnType() instanceof Number) {
                                        currentClass = Number.class;
                                    } else {
                                        currentClass = newNode.simpleReturnType().getClass();
                                    }

                                    //check if we already have a list of nodes that return a certain class
                                    if (!existingNodes.containsKey(currentClass)) {
                                        existingNodes.put(currentClass, new ArrayList<>());
                                    }
                                    ArrayList<GPGraphNode> nodesForSpecificClass = existingNodes.get(currentClass);
                                    //check if we already added this node before (happens if we reuse an older node)
                                    if (!nodesForSpecificClass.contains(newNode)) {
                                        nodesForSpecificClass.add(newNode);
                                    }
                                }

                                //Set the node to cached, depending on a certain probability
                                if (r.nextDouble() <= cachedNodeProbability && newNode instanceof CacheableGPGraphNode) {
                                    ((CacheableGPGraphNode) newNode).setCached(true);
                                }
                            }
                        }
                    }

                    if (!collection) {
                        currentPos++;
                    }
                }

                //remove previous children that were wrong by capping the list
                //for this, remove the collection classes (they are defined by collection.clas + value.class)
                classes.removeIf(c -> c.equals(Collection.class));
                castedNode.setChildNodes(new ArrayList<>(castedNode.getChildNodes().subList(0, classes.size())));
            }

            //after the node has been fixed, check the children out
            //start creating children for the other necessary graphs
            for (GPGraphNode child : (ArrayList<GPGraphNode>) castedNode.getChildNodes()) {
                if (!visitedNodes.contains(child)) {
                    visitedNodes.add(child);
                    repairGraph(child, validNodes, terminals, functionals, existingNodes, visitedNodes, currentDepth + 1, settings);
                }
            }

        }

    }

    /**
     * Depending on the depth, will choose the correct node to pick a new node from. Will then call a different function
     * to create the node.
     *
     * @param validNodes    all valid nodes
     * @param terminals     terminal nodes (have no children)
     * @param functionals   functional nodes that will broaden the graph
     * @param existingNodes nodes that are already in use in the graph
     * @param previousNode  previousNode who requires the new node as child
     * @param currentDepth  currentDepth of the graph
     * @param clazz         required clazz for the creation of a node
     * @param collection    true = need a node that returns us a collection, false = any other node
     * @return new node to set as a child
     */
    private GPGraphNode pickNodeDependingOnClass(Map<Class, ArrayList<GPGraphNode>> validNodes, Map<Class, ArrayList<GPGraphNode>> terminals, Map<Class, ArrayList<GPGraphNode>> functionals, Map<Class, ArrayList<GPGraphNode>> existingNodes, FunctionalGPGraphNode previousNode, int currentDepth, Class clazz, boolean collection) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {

        GPGraphNode node = null;

        //if our graph is still below the min depth, try to exclusively use functional nodes
        if (currentDepth < minDepth) {
            node = createNodeUsingNodeList(functionals, clazz, collection);
        }
        //with increasing depth, increase the chance to get terminals exclusively
        else if (r.nextInt(maxDepth) < currentDepth) {
            node = createNodeUsingNodeList(terminals, clazz, collection);
        }
        //with a certain probability, reuse other nodes in the graph
        else if (r.nextDouble() <= reuseNodeProbability) {
            //get the correct list of nodes
            ArrayList<GPGraphNode> nodesToPickFrom = existingNodes.get(collection ? Collection.class : clazz);
            if (nodesToPickFrom != null) {
                //pick a random node from said list
                node = nodesToPickFrom.get(r.nextInt(nodesToPickFrom.size()));

                //if we look for a collection, we have to get one that returns us the correct type
                if (collection && node instanceof GenericFunctionalCollectionGPGraphNode) {
                    GenericFunctionalCollectionGPGraphNode castedNode = (GenericFunctionalCollectionGPGraphNode) node;
                    if (!castedNode.getClazz().equals(clazz)) {
                        node = null;
                    }
                }

                //check if we create an endless loop
                if (node != null) {
                    //add the node, then check the validity for loops only
                    previousNode.addChildNode(node);

                    boolean remove = !GPValidator.validateGraphLoopsOnly(node);

                    //remove the node again, it gets added later on anyway and will be added to the list of existing nodes
                    previousNode.getChildNodes().remove(node);

                    //if we created a loop, set the node to null again
                    if (remove) {
                        node = null;
                    }
                }
            }
        }


        //otherwise, put anything else in there
        if (node == null) {
            node = createNodeUsingNodeList(validNodes, clazz, collection);
        }

        return node;
    }

    /**
     * Will pick a random node from the given list and create a new instance of it.
     *
     * @param nodesToPickFrom pick random node from this list that fits the class
     * @param clazz           class that we require
     * @param collection      true = need a node that reutns us a collection, false = any other node
     * @return new node to set as a child
     */
    private GPGraphNode createNodeUsingNodeList(Map<Class, ArrayList<GPGraphNode>> nodesToPickFrom, Class clazz, boolean collection) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        List<GPGraphNode> validNodes;

        //check if he have to pick a node that returns a collection
        if (collection) {
            validNodes = nodesToPickFrom.get(Collection.class);
        } else {
            validNodes = nodesToPickFrom.get(clazz);
        }

        //check if we  can pick something from this list
        if (validNodes != null && !validNodes.isEmpty()) {

            GPGraphNode chosen = validNodes.get(r.nextInt(validNodes.size()));
            newlyCreated = true;
            if (chosen instanceof GenericFunctionalCollectionGPGraphNode || chosen instanceof GenericFunctionalGPGraphNode) {
                return chosen.getClass().getConstructor(Class.class).newInstance(clazz);
            } else {
                return chosen.getClass().getConstructor().newInstance();
            }
        }

        return null;
    }

    /**
     * Add the settings of the given map to the node. Differentiates between normal descriptor, minMax- and
     * list-descriptor.
     *
     * @param node          node to add the settings to
     * @param descriptorMap map with the different descriptors
     */
    private void addGraphSettings(GPGraphNode node, Map<String, Descriptor> descriptorMap) {

        //nothing to set here
        if (descriptorMap == null) {
            return;
        }

        for (Map.Entry<String, Descriptor> entry : descriptorMap.entrySet()) {
            //if value is not null, then the value is fixed
            if (entry.getValue().getValue() != null) {
                node.setOption(entry.getKey(), entry.getValue());
            }
            //if it is null, then we have either a MinMaxDescriptor or ListDescriptor
            else {
                //check if its a MinMaxDescriptor
                if (entry.getValue() instanceof MinMaxDescriptor) {
                    MinMaxDescriptor descriptor = (MinMaxDescriptor) entry.getValue();

                    //check which type we need for calculating the random value between min and max.
                    if (descriptor.getMin().getClass() == Integer.class) {
                        MinMaxDescriptor<Integer> casted = (MinMaxDescriptor<Integer>) descriptor;
                        node.setOption(entry.getKey(), new Descriptor(r.nextInt(casted.getMax() - casted.getMin()) + casted.getMin()));
                    } else if (descriptor.getMin().getClass() == Double.class) {
                        MinMaxDescriptor<Double> casted = (MinMaxDescriptor<Double>) descriptor;
                        node.setOption(entry.getKey(), new Descriptor(casted.getMin() + (casted.getMax() - casted.getMin()) * r.nextDouble()));
                    } else if (descriptor.getMin().getClass() == Float.class) {
                        MinMaxDescriptor<Float> casted = (MinMaxDescriptor<Float>) descriptor;
                        node.setOption(entry.getKey(), new Descriptor(casted.getMin() + (casted.getMax() - casted.getMin()) * r.nextFloat()));
                    }
                }
                //if not, its a ListDescriptor
                else {
                    ListDescriptor descriptor = (ListDescriptor) entry.getValue();
                    //set a random value of the list
                    node.setOption(entry.getKey(), new Descriptor(descriptor.getValueList().get(r.nextInt(descriptor.getValueList().size()))));
                }
            }

        }

    }

    public int getMaxDepth() {
        return maxDepth;
    }

    public void setMaxDepth(int maxDepth) {
        this.maxDepth = maxDepth;
    }

    public int getMinDepth() {
        return minDepth;
    }

    public void setMinDepth(int minDepth) {
        this.minDepth = minDepth;
    }

    public double getCachedNodeProbability() {
        return cachedNodeProbability;
    }

    public void setCachedNodeProbability(double cachedNodeProbability) {
        this.cachedNodeProbability = cachedNodeProbability;
    }

    public double getReuseNodeProbability() {
        return reuseNodeProbability;
    }

    public void setReuseNodeProbability(double reuseNodeProbability) {
        this.reuseNodeProbability = reuseNodeProbability;
    }

    public Random getR() {
        return r;
    }
}

