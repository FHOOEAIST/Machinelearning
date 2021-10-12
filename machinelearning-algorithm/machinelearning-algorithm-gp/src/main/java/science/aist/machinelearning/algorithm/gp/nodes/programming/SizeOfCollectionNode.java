/*
 * Copyright (c) 2021 the original author or authors.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package science.aist.machinelearning.algorithm.gp.nodes.programming;

import science.aist.machinelearning.algorithm.gp.FunctionalGPGraphNode;
import science.aist.machinelearning.core.options.Descriptor;

import java.util.*;

/**
 * Returns the size of a collection.
 *
 * @author Daniel Wilfing
 * @since 1.0
 */
public class SizeOfCollectionNode extends FunctionalGPGraphNode<Double> {
    @Override
    public boolean checkValidity() {
        return getChildNodes().size() == 1 &&
                getChildNodes().get(0).simpleReturnType() instanceof Collection;
    }

    @Override
    public ArrayList<Class> requiredClassesForChildren() {
        ArrayList<Class> classes = new ArrayList<>();
        classes.add(Collection.class);
        classes.add(Object.class);
        return classes;
    }

    @Override
    public Double calculateValue() {
        return (double) ((List<?>) getChildNodes().get(0).execute()).size();
    }

    @Override
    public Double simpleReturnType() {
        return 0.0;
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
