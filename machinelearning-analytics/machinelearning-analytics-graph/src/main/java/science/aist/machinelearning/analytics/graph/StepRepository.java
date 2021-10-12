/*
 * Copyright (c) 2021 the original author or authors.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package science.aist.machinelearning.analytics.graph;

import org.neo4j.driver.Value;
import science.aist.machinelearning.analytics.graph.nodes.StepNode;
import science.aist.neo4j.repository.AbstractNeo4JNodeRepositoyImpl;
import science.aist.neo4j.transaction.TransactionManager;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Repository for logging steps that an algorithm takes to solve a problem
 *
 * @author Oliver Krauss
 * @since 1.0
 */
public class StepRepository extends AbstractNeo4JNodeRepositoyImpl<StepNode> {

    protected StepRepository(TransactionManager manager) {
        super(manager, "StepNode");
    }

    @Override
    protected StepNode cast(Value value, Value relatives, Value nodes) {
        StepNode node = new StepNode();
        node.setTime(value.get("time").asString(""));
        value.keys().forEach(x -> {
            if (x.startsWith("parameters.")) {
                node.addParameter(x.substring("parameters.".length()), value.get(x).asObject());
            }
        });
        return node;
    }

    @Override
    protected StepNode setId(StepNode node, Long id) {
        node.setId(id);
        return node;
    }

    @Override
    protected Long getId(StepNode node) {
        return node.getId();
    }

    @Override
    protected <T extends StepNode> T handleRelationships(T node) {
        return node;
    }

    @Override
    protected <T extends StepNode> Collection<T> handleRelationships(Collection<T> nodes) {
        nodes.forEach(this::handleRelationships);
        return nodes;
    }

    protected Map<String, Object> objectifyProperties(StepNode node) {
        Map<String, Object> map = new HashMap<>();
        map.put("time", node.getTime());
        node.getParameters().forEach((x, y) -> map.put("parameters." + x, y));
        return map;
    }

    @Override
    protected Map<String, Object> objectify(StepNode node) {
        Map<String, Object> map = new HashMap<>();
        map.put("properties", objectifyProperties(node));
        map.put("id", node.getId());
        return map;
    }
}
