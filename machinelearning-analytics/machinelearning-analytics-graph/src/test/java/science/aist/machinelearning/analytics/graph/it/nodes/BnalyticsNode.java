/*
 * Copyright (c) 2021 the original author or authors.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package science.aist.machinelearning.analytics.graph.it.nodes;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import org.neo4j.ogm.annotation.Transient;
import science.aist.machinelearning.analytics.graph.nodes.AnalyticsNode;
import science.aist.neo4j.annotations.StaticField;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Oliver Krauss
 * @since 1.0
 */
@NodeEntity(label = "BGradeAnalytics")
public class BnalyticsNode extends AnalyticsNode {

    public static String staticField;
    @StaticField
    public static String staticIncludedField;
    @Relationship
    public static AnalyticsNode staticRelationship;
    @Relationship
    public List<AnalyticsNode> unnamedMulti;
    public String dontIgnoreMe;
    @Relationship(type = "NAMED_REL")
    protected AnalyticsNode namedRel;
    @Relationship
    private AnalyticsNode unnamedRel;
    @Transient
    private String ignoreMe;
    private List<String> testArray = new ArrayList<>();

    public BnalyticsNode() {
    }

    public BnalyticsNode(String title) {
        super(title);
    }

    public static String getStaticField() {
        return staticField;
    }

    public static void setStaticField(String staticField) {
        BnalyticsNode.staticField = staticField;
    }

    public static AnalyticsNode getStaticRelationship() {
        return staticRelationship;
    }

    public static void setStaticRelationship(AnalyticsNode staticRelationship) {
        BnalyticsNode.staticRelationship = staticRelationship;
    }

    public static String getStaticIncludedField() {
        return staticIncludedField;
    }

    public static void setStaticIncludedField(String staticIncludedField) {
        BnalyticsNode.staticIncludedField = staticIncludedField;
    }

    public String getIgnoreMe() {
        return ignoreMe;
    }

    public void setIgnoreMe(String ignoreMe) {
        this.ignoreMe = ignoreMe;
    }

    public List<String> getTestArray() {
        return testArray;
    }

    public void setTestArray(List<String> testArray) {
        this.testArray = testArray;
    }

    public String getDontIgnoreMe() {
        return dontIgnoreMe;
    }

    public void setDontIgnoreMe(String dontIgnoreMe) {
        this.dontIgnoreMe = dontIgnoreMe;
    }

    public AnalyticsNode getUnnamedRel() {
        return unnamedRel;
    }

    public void setUnnamedRel(AnalyticsNode unnamedRel) {
        this.unnamedRel = unnamedRel;
    }

    public AnalyticsNode getNamedRel() {
        return namedRel;
    }

    public void setNamedRel(AnalyticsNode namedRel) {
        this.namedRel = namedRel;
    }

    public List<AnalyticsNode> getUnnamedMulti() {
        return unnamedMulti;
    }

    public void setUnnamedMulti(List<AnalyticsNode> unnamedMulti) {
        this.unnamedMulti = unnamedMulti;
    }
}
