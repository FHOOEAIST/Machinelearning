/*
 * Copyright (c) 2021 the original author or authors.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package science.aist.machinelearning.example;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import science.aist.machinelearning.constraint.Constraint;
import science.aist.machinelearning.constraint.ConstraintCalculation;
import science.aist.machinelearning.constraint.RootConstraint;
import science.aist.machinelearning.tree.ConstraintPriorityTreeNode;

/**
 * Test to check functionality of {@link ConstraintPriorityTreeNode}.
 * <p>
 * Builds the following tree (result | constraint)
 * <p>
 * 0 | ""
 * <p>
 * 1 | "a"           2 | "b"
 * <p>
 * 3 | "aa"    4 |"ab"  5 | "ba"    6 | "bb"
 * <p>
 * E.g. string with the value "aa" leads to the result 3.
 *
 * @author Daniel Wilfing
 * @since 1.0
 */
public class ConstraintPriorityTreeTest {

    ConstraintPriorityTreeNode<Integer, String> root = new ConstraintPriorityTreeNode<>();

    @BeforeClass
    public void setup() {

        //create nodes and set constraints
        root.setCalculation(new CalculationRoot());
        root.setConstraint(new RootConstraint<>());

        ConstraintPriorityTreeNode<Integer, String> row1node1 = new ConstraintPriorityTreeNode<>();
        row1node1.setConstraint(new ConstraintRow1Node1());
        row1node1.setCalculation(new CalculationRow1Node1());

        ConstraintPriorityTreeNode<Integer, String> row1node2 = new ConstraintPriorityTreeNode<>();
        row1node2.setConstraint(new ConstraintRow1Node2());
        row1node2.setCalculation(new CalculationRow1Node2());

        ConstraintPriorityTreeNode<Integer, String> row2node1 = new ConstraintPriorityTreeNode<>();
        row2node1.setConstraint(new ConstraintRow2Node1());
        row2node1.setCalculation(new CalculationRow2Node1());

        ConstraintPriorityTreeNode<Integer, String> row2node2 = new ConstraintPriorityTreeNode<>();
        row2node2.setConstraint(new ConstraintRow2Node2());
        row2node2.setCalculation(new CalculationRow2Node2());

        ConstraintPriorityTreeNode<Integer, String> row2node3 = new ConstraintPriorityTreeNode<>();
        row2node3.setConstraint(new ConstraintRow2Node3());
        row2node3.setCalculation(new CalculationRow2Node3());

        ConstraintPriorityTreeNode<Integer, String> row2node4 = new ConstraintPriorityTreeNode<>();
        row2node4.setConstraint(new ConstraintRow2Node4());
        row2node4.setCalculation(new CalculationRow2Node4());

        //create tree hierarchy
        root.getChildNodes().add(row1node1);
        root.getChildNodes().add(row1node2);

        row1node1.getChildNodes().add(row2node1);
        row1node1.getChildNodes().add(row2node2);

        row1node2.getChildNodes().add(row2node3);
        row1node2.getChildNodes().add(row2node4);
    }

    @Test
    public void testEmptyString() {
        //given

        //when
        Integer result = root.evaluate("");

        //then
        Assert.assertEquals(result.intValue(), 0);
    }

    @Test
    public void testAllValues() {
        //given

        //when
        Integer result1 = root.evaluate("a");
        Integer result2 = root.evaluate("b");
        Integer result3 = root.evaluate("aa");
        Integer result4 = root.evaluate("ab");
        Integer result5 = root.evaluate("ba");
        Integer result6 = root.evaluate("bb");

        //then
        Assert.assertEquals(result1.intValue(), 1);
        Assert.assertEquals(result2.intValue(), 2);
        Assert.assertEquals(result3.intValue(), 3);
        Assert.assertEquals(result4.intValue(), 4);
        Assert.assertEquals(result5.intValue(), 5);
        Assert.assertEquals(result6.intValue(), 6);
    }

    @Test
    public void testUnknown() {
        //given

        //when
        Integer result1 = root.evaluate("c");

        //then
        Assert.assertEquals(result1.intValue(), 0);
    }

    @Test
    public void testSubUnknown() {
        //given

        //when
        Integer result1 = root.evaluate("ac");

        //then
        Assert.assertEquals(result1.intValue(), 1);
    }

    @Test
    public void testSubSubUnknown() {
        //given

        //when
        Integer result1 = root.evaluate("abc");

        //then
        Assert.assertEquals(result1.intValue(), 4);
    }

    private static class ConstraintRow1Node1 implements Constraint<String> {

        @Override
        public boolean evaluate(String object) {
            return object.length() >= 1 && object.charAt(0) == 'a';
        }
    }

    private static class ConstraintRow1Node2 implements Constraint<String> {

        @Override
        public boolean evaluate(String object) {
            return object.length() >= 1 && object.charAt(0) == 'b';
        }
    }

    private static class ConstraintRow2Node1 implements Constraint<String> {

        @Override
        public boolean evaluate(String object) {
            return object.length() >= 2 && object.charAt(1) == 'a';
        }
    }

    private static class ConstraintRow2Node2 implements Constraint<String> {

        @Override
        public boolean evaluate(String object) {
            return object.length() >= 2 && object.charAt(1) == 'b';
        }
    }

    private static class ConstraintRow2Node3 implements Constraint<String> {

        @Override
        public boolean evaluate(String object) {
            return object.length() >= 2 && object.charAt(1) == 'a';
        }
    }

    private static class ConstraintRow2Node4 implements Constraint<String> {

        @Override
        public boolean evaluate(String object) {
            return object.length() >= 2 && object.charAt(1) == 'b';
        }
    }

    private static class CalculationRoot implements ConstraintCalculation<Integer, String> {

        @Override
        public Integer calculate(String object) {
            return 0;
        }
    }

    private static class CalculationRow1Node1 implements ConstraintCalculation<Integer, String> {

        @Override
        public Integer calculate(String object) {
            return 1;
        }
    }

    private static class CalculationRow1Node2 implements ConstraintCalculation<Integer, String> {

        @Override
        public Integer calculate(String object) {
            return 2;
        }
    }

    private static class CalculationRow2Node1 implements ConstraintCalculation<Integer, String> {

        @Override
        public Integer calculate(String object) {
            return 3;
        }
    }

    private static class CalculationRow2Node2 implements ConstraintCalculation<Integer, String> {

        @Override
        public Integer calculate(String object) {
            return 4;
        }
    }

    private static class CalculationRow2Node3 implements ConstraintCalculation<Integer, String> {

        @Override
        public Integer calculate(String object) {
            return 5;
        }
    }

    private static class CalculationRow2Node4 implements ConstraintCalculation<Integer, String> {

        @Override
        public Integer calculate(String object) {
            return 6;
        }
    }


}
