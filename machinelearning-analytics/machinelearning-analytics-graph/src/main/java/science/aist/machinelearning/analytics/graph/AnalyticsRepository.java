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
import org.springframework.beans.factory.annotation.Required;
import science.aist.machinelearning.analytics.graph.nodes.AnalyticsNode;
import science.aist.machinelearning.analytics.graph.nodes.StepNode;
import science.aist.neo4j.repository.AbstractNeo4JNodeRepositoyImpl;
import science.aist.neo4j.transaction.TransactionManager;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Repository storing the Base-Nodes for an analytics run
 *
 * @author Oliver Krauss
 * @since 1.0
 */
public class AnalyticsRepository extends AbstractNeo4JNodeRepositoyImpl<AnalyticsNode> {

    protected StepRepository stepRepository;

    protected AnalyticsRepository(TransactionManager manager) {
        super(manager, "AnalyticsNode");
    }

    @Override
    protected AnalyticsNode cast(Value value, Value relatives, Value nodes) {
        AnalyticsNode node = new AnalyticsNode(value.get("title").asString());
        value.keys().forEach(x -> {
            if (x.startsWith("parameters.")) {
                node.addParameter(x.substring("parameters.".length()), value.get(x).asObject());
            }
        });
        return node;
    }

    @Override
    protected AnalyticsNode setId(AnalyticsNode node, Long id) {
        node.setId(id);
        return node;
    }

    @Override
    protected Long getId(AnalyticsNode node) {
        return node.getId();
    }

    @Override
    protected <T extends AnalyticsNode> T handleRelationships(T node) {
        // create all steps that aren't in the DB
        if (node.getSteps().stream().anyMatch(x -> x.getId() == null)) {
            List<StepNode> unsyncedSteps = node.getSteps().stream().filter(s -> s.getId() == null).collect(Collectors.toList());
            stepRepository.saveAll(unsyncedSteps);
        }
        // sync the relationships to steps
        if (node.getSteps().size() > 0) {
            saveRelationshipBulk("StepNode", "EXECUTED", node.getId(), node.getSteps().stream().mapToLong(StepNode::getId).toArray());
        }

        saveRelationship("ProblemNode", "PROBLEM", node.getId(), node.getProblem() != null ? node.getProblem().getId() : null);
        saveRelationship("SolutionNode", "SOLUTION", node.getId(), node.getProblem() != null ? node.getProblem().getId() : null);

        return node;
    }

    @Override
    protected <T extends AnalyticsNode> Collection<T> handleRelationships(Collection<T> nodes) {
        nodes.forEach(this::handleRelationships);
        return nodes;
    }

    Map<String, Object> objectifyProperties(AnalyticsNode node) {
        Map<String, Object> map = new HashMap<>();
        map.put("title", node.getTitle());
        node.getParameters().forEach((x, y) -> map.put("parameters." + x, y));
        return map;
    }

    @Override
    protected Map<String, Object> objectify(AnalyticsNode node) {
        Map<String, Object> map = new HashMap<>();
        map.put("properties", objectifyProperties(node));
        map.put("id", node.getId());
        return map;
    }

    @Required
    public void setStepRepository(StepRepository stepRepository) {
        this.stepRepository = stepRepository;
    }
}
