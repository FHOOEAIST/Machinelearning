/*
 * Copyright (c) 2021 the original author or authors.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package science.aist.machinelearning.analytics.graph.it;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;
import science.aist.machinelearning.analytics.graph.nodes.AnalyticsNode;
import science.aist.machinelearning.analytics.graph.nodes.StepNode;
import science.aist.neo4j.Neo4jRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Benchmark test for comparing spring-data with native neo4j Note: We are trying this out on the neo4j server, as
 * performance measurements on the in-memory db have no impact on real world performance for us. Note: We do not yet
 * have automation for this benchmark. Please manually CLEAR the db before starting this test for fair starting
 * conditions
 *
 * @author Oliver Krauss
 * @since 1.0
 */
@ContextConfiguration(locations = {"classpath:repositoryConfig.xml"})
public class GraphAnalyticsBenchmarkBulk extends AbstractTestNGSpringContextTests {

    private static final long AMOUNT = 20000L;

    @Autowired
    private Neo4jRepository<AnalyticsNode, Long> analyticsRepository;

    // Note we are intentionally testing single-inserts to compare the CREATE statement
    @Test(enabled = false)
    public void testWriteNodesSaveAll() {
        long startTime = System.currentTimeMillis();
        List<AnalyticsNode> node = new ArrayList<>();

        for (int i = 0; i < AMOUNT; i++) {
            // given
            node.add(new AnalyticsNode("TITLE"));
        }

        // when
        analyticsRepository.saveAll(node);

        // then
        long estimatedTime = System.currentTimeMillis() - startTime;
        System.out.println("This took us " + estimatedTime);
    }

    @Test(enabled = false, dependsOnMethods = "testWriteNodesSaveAll")
    public void testReadNodesAll() {
        // given

        // when
        int count = 0;
        long startTime = System.currentTimeMillis();
        Iterable<AnalyticsNode> all = analyticsRepository.findAll();

        long estimatedTime = System.currentTimeMillis() - startTime;
        System.out.println("This took us " + estimatedTime);

        // then
        for (AnalyticsNode x : all) {
            count++;
        }
        //Assert.assertEquals(count, AMOUNT);
    }

    @Test(enabled = false, dependsOnMethods = "testReadNodesAll")
    public void testWriteRelationshipAll() {
        // given
        AnalyticsNode node = new AnalyticsNode();
        for (int i = 0; i < AMOUNT; i++) {
            node.addStep(new StepNode());
        }

        // when
        long startTime = System.currentTimeMillis();
        analyticsRepository.save(node);

        // then
        long estimatedTime = System.currentTimeMillis() - startTime;
        System.out.println("This took us " + estimatedTime);
    }
}
