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
import science.aist.machinelearning.algorithm.gp.InterruptibleNode;
import science.aist.machinelearning.core.options.Descriptor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Generic For that runs depending on the numbers in the first two children. Will collect data from the third child and
 * return it as a collection.
 *
 * @author Daniel Wilfing
 * @since 1.0
 */
public class ForCollectionNode<T> extends GenericFunctionalCollectionGPGraphNode<T> implements InterruptibleNode {

    /**
     * interrupt flag that stops the loop
     */
    private boolean interrupt = false;

    public ForCollectionNode(Class<T> clazz) {
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
    public Collection<T> calculateValue() {
        int max = ((Double) getChildNodes().get(0).execute()).intValue();
        ArrayList<T> objects = new ArrayList<>();

        for (int i = 0; i < max; i++) {
            objects.add((T) getChildNodes().get(1).execute());

            if (interrupt) {
                break;
            }
        }

        return objects;
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
