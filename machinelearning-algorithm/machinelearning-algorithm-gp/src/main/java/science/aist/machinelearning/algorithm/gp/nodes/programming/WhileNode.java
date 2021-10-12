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
import science.aist.machinelearning.algorithm.gp.InterruptibleNode;
import science.aist.machinelearning.core.options.Descriptor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Generic While that runs as long as the first child ist true. Will return the last result of the second child (useful
 * with caching).
 *
 * @author Daniel Wilfing
 * @since 1.0
 */
public class WhileNode<T> extends GenericFunctionalGPGraphNode<T> implements InterruptibleNode {

    /**
     * Prevents endless whiles. Will try to fulfill the condition of the while, but will break after this number of
     * iterations have been done.
     * <p>
     * If this value is set to -1, then will ignore this condition (may cause endless while-loops).
     */
    private int maxIterations = 10000;

    /**
     * interrupt flag that stops the loop
     */
    private boolean interrupt = false;

    public WhileNode(Class<T> clazz) {
        super(clazz);
    }

    @Override
    public boolean checkValidity() {
        return getChildNodes().size() == 2 &&
                getChildNodes().get(0).simpleReturnType() instanceof Boolean &&
                clazz.isAssignableFrom(getChildNodes().get(1).simpleReturnType().getClass());
    }

    @Override
    public ArrayList<Class> requiredClassesForChildren() {
        ArrayList<Class> classes = new ArrayList<>();
        classes.add(Boolean.class);
        classes.add(clazz);
        return classes;
    }

    @Override
    public T calculateValue() {
        T object = null;

        int i = 0;
        while ((Boolean) getChildNodes().get(0).execute()) {
            object = (T) getChildNodes().get(1).execute();

            if (interrupt) {
                break;
            }

            //break if we get a certain amount of iterations
            if (maxIterations != -1 && i++ >= maxIterations) {
                break;
            }
        }

        return object;
    }

    @Override
    public Map<String, Descriptor> getOptions() {
        Map<String, Descriptor> options = new HashMap<>();

        options.put("maxIterations", new Descriptor<>(maxIterations));

        return options;
    }

    @Override
    public boolean setOption(String name, Descriptor descriptor) {
        try {
            if (name.equals("maxIterations")) {
                setMaxIterations((Integer) descriptor.getValue());
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public void interrupt(boolean value) {
        interrupt = value;
    }

    public int getMaxIterations() {
        return maxIterations;
    }

    public void setMaxIterations(int maxIterations) {
        this.maxIterations = maxIterations;
    }
}
