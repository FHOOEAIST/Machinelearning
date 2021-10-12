/*
 * Copyright (c) 2021 the original author or authors.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package science.aist.machinelearning.algorithm.gp.nodes.programming;


import science.aist.machinelearning.algorithm.gp.GenericFunctionalGPGraphNode;
import science.aist.machinelearning.core.options.Descriptor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Depending on the boolean, will return either the first T or the second. true = return first T false = return second
 * T
 *
 * @author Daniel Wilfing
 * @since 1.0
 */
public class IfThenElseNode<T> extends GenericFunctionalGPGraphNode<T> {

    public IfThenElseNode(Class<T> clazz) {
        super(clazz);
    }

    @Override
    public boolean checkValidity() {

        return getChildNodes().size() == 3 &&
                getChildNodes().get(0).simpleReturnType() instanceof Boolean &&
                clazz.isAssignableFrom(getChildNodes().get(1).simpleReturnType().getClass()) &&
                clazz.isAssignableFrom(getChildNodes().get(2).simpleReturnType().getClass());
    }

    @Override
    public ArrayList<Class> requiredClassesForChildren() {
        ArrayList<Class> classes = new ArrayList<>();
        classes.add(Boolean.class);
        classes.add(clazz);
        classes.add(clazz);
        return classes;
    }

    @Override
    public T calculateValue() {
        if ((Boolean) getChildNodes().get(0).execute()) {
            return (T) getChildNodes().get(1).execute();
        } else {
            return (T) getChildNodes().get(2).execute();
        }
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
