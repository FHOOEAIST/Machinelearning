/*
 * Copyright (c) 2021 the original author or authors.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package science.aist.machinelearning.algorithm.gp.nodes.programming;

import science.aist.machinelearning.algorithm.gp.GenericFunctionalCollectionGPGraphNode;
import science.aist.machinelearning.core.options.Descriptor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Adds the contents of the collections into a new collection and returns it.
 *
 * @author Daniel Wilfing
 * @since 1.0
 */
public class CollectionMergeNode<T> extends GenericFunctionalCollectionGPGraphNode<T> {

    public CollectionMergeNode(Class<T> clazz) {
        super(clazz);
    }

    @Override
    public boolean checkValidity() {
        //check if we have the currect number of nodes and the children return us collections
        if (getChildNodes().size() == 2 &&
                getChildNodes().get(0) instanceof GenericFunctionalCollectionGPGraphNode &&
                getChildNodes().get(1) instanceof GenericFunctionalCollectionGPGraphNode
        ) {

            GenericFunctionalCollectionGPGraphNode node1Casted = (GenericFunctionalCollectionGPGraphNode) getChildNodes().get(0);
            GenericFunctionalCollectionGPGraphNode node2Casted = (GenericFunctionalCollectionGPGraphNode) getChildNodes().get(1);

            //check if the entries inside the collections returned are the same type we want
            return node1Casted.getClazz().equals(clazz) && node2Casted.getClazz().equals(clazz);
        }

        return false;
    }

    @Override
    public ArrayList<Class> requiredClassesForChildren() {
        ArrayList<Class> classes = new ArrayList<>();
        classes.add(Collection.class);
        classes.add(clazz);
        classes.add(Collection.class);
        classes.add(clazz);
        return classes;
    }

    @Override
    public ArrayList<T> calculateValue() {
        ArrayList<T> list1 = new ArrayList<>((ArrayList<T>) getChildNodes().get(0).execute());
        list1.addAll((ArrayList<T>) getChildNodes().get(1).execute());
        return list1;
    }

    @Override
    public ArrayList<T> simpleReturnType() {
        return new ArrayList<>();
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
