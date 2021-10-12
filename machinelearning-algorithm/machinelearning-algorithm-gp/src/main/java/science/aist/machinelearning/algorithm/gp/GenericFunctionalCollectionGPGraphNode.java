/*
 * Copyright (c) 2021 the original author or authors.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package science.aist.machinelearning.algorithm.gp;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Abstract class for the implementation of generic functional nodes, that return collections of generics. Generic
 * functional nodes can return any class, but should usually return numbers, booleans and solutions.
 *
 * @author Daniel Wilfing
 * @since 1.0
 */
public abstract class GenericFunctionalCollectionGPGraphNode<T> extends FunctionalGPGraphNode<Collection<T>> {

    protected Class<T> clazz;

    public GenericFunctionalCollectionGPGraphNode(Class<T> clazz) {
        this.clazz = clazz;
    }

    public ArrayList<T> simpleReturnType() {
        return new ArrayList<>();
    }

    public Class<T> getClazz() {
        return clazz;
    }
}
