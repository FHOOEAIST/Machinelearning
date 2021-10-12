/*
 * Copyright (c) 2021 the original author or authors.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package science.aist.machinelearning.algorithm.cmaes;

import science.aist.machinelearning.algorithm.cmaes.operator.RealValuedSolutionBuilder;
import science.aist.machinelearning.core.AbstractAlgorithm;
import science.aist.machinelearning.core.Problem;
import science.aist.machinelearning.core.Solution;
import science.aist.machinelearning.core.options.Descriptor;
import science.aist.machinelearning.core.util.RandomUtil;

import java.util.*;

/**
 * CMA-ES which is essentially an evolution strategy that uses a gradient descent to move through the search space.
 * <p>
 * CREDITS: This code is (obviously) adapted from the original source code http://cma.gforge.inria.fr/cmaes_sourcecode_page.html
 * as well as MOEA https://github.com/MOEAFramework/MOEAFramework/blob/master/src/org/moeaframework/algorithm/CMAES.java#L98
 * <p>
 * // TODO #79 large swaths of this are copy pastes from genetic algorithm. We might want to create a
 * "populationbasedalgorithms" base class // TODO #80 the code from MOEA actually implements variable bounds that
 * restrict solutions to a "feasible" space I omitted this, but we may need it // TODO #81 MOEA (and this impl) actually
 * skips A TON of the stuff the original C impl is doing, such as finishing conditions, or aborting when the algorithm
 * stagnates. We don't need it exactly but it is cool stuff.
 *
 * @param <ST> Gene type of the solution
 * @param <PT> Gene type of the Problem
 * @author Oliver Krauss
 * @since 1.0
 */
public class CovarianceMatrixAdaptionEvolutionStrategy<ST, PT extends FloatValueProblem> extends AbstractAlgorithm<ST, PT> {

    // region Options

    /**
     * Current population in the GA context
     */
    private final List<Solution<ST, PT>> population = new ArrayList<>();
    /**
     * Best currently known solution
     */
    protected Solution<ST, PT> bestSolution;
    /**
     * Termination Criteria on how many generations may be evaluated
     */
    private int maximumGenerations;
    /**
     * Number of current generation, increasing with each algorithm step
     */
    private int currentGeneration;
    /**
     * Operator responsible to get from double[] to ST
     */
    private RealValuedSolutionBuilder<ST, PT> solutionBuilder;
    /**
     * if the initializeLog function has already been called
     */
    private boolean logInitialized = false;
    /**
     * if the finalizeLog function has already been called
     */
    private boolean logFinalized = false;

    // endregion Options

    // region Logs
    /**
     * the initial position in the search space that CMA-ES will start out from
     */
    private double[] initialSearchPosition = null;
    /**
     * Standard deviation for solution variables
     */
    private double standardDeviation = 0.5;
    /**
     * The number of iterations in which only the covariance diagonal is used. This enhancement helps speed up the
     * algorithm when there are many decision variables.  Set to {@code 0} to always use the full covariance matrix.
     */
    private int diagonalIterations = -1;
    /**
     * Size of population ;
     */
    private int populationSize = 50;

    // endregion Logs

    // region generic code
    /**
     * Evolution path of the current generation
     */
    private double[] evolutionPath;
    /**
     * Scaling factor for matrices
     */
    private double[] scalingFactor;
    /**
     * Evolution path of the next generation
     */
    private double[] evolutionPathNextGeneration;
    /**
     * Coordinate system for covarianceMatrix
     */
    private double[][] covarianceCoordinateSystem;
    /**
     * Covariance Matrix giving CMA-ES its name :D
     */
    private double[][] covarianceMatrix;

    // endregion generic code

    // region CMA-ES specific code
    /**
     * Centroid of the current population
     */
    private double[] distributionCentroid;
    /**
     * Expected distribution of variables to be optimized ||N(0, I)||
     */
    private double chiSquaredDistributionOfVariables = -1;
    /**
     * Amount of individuals used in crossover operation
     */
    private int crossoverParentCount = -1;
    /**
     * Weighting for crossover operation
     */
    private double[] crossoverWeights;
    /**
     * Effectiveness of crossover weights
     */
    private double varianceEffectiveness = -1;
    /**
     * Step size cumulation parameter
     */
    private double cumulationStepSize = -1;
    /**
     * cumulation parameter
     */
    private double cumulation = -1;
    /**
     * The learning rate of the algorithm
     */
    private double learningRate = -1;
    /**
     * The learning rate during diagonal mode.
     */
    private double learningRateDiagonal = -1;
    /**
     * Value of how the stepSize will be reduced
     */
    private double stepSizeDampening = -1;
    /**
     * last time the eigenvalues were updated
     */
    private double lastEigenupdate;
    /**
     * if activated CMAES will ensure it stays numerically stable
     */
    private boolean checkConsistency = true;

    /**
     * Symmetric Householder reduction to tridiagonal form, taken from JAMA package.
     * <p>
     * This is derived from the Algol procedures matrixToTridiagonalReduction by Bowdler, Martin, Reinsch, and
     * Wilkinson, Handbook for Auto. Comp., Vol.ii-Linear Algebra, and the corresponding Fortran subroutine in EISPACK.
     */
    public static void matrixToTridiagonalReduction(int variableCount, double[][] covarianceCoordinateSystem, double[] scalingFactor, double[] diagonal) {
        System.arraycopy(covarianceCoordinateSystem[variableCount - 1], 0, scalingFactor, 0, variableCount);

        // Householder reduction to tridiagonal form.
        for (int i = variableCount - 1; i > 0; i--) {

            // Scale to avoid under/overflow.
            double scale = 0.0;
            double newScalingFactor = 0.0;
            for (int k = 0; k < i; k++) {
                scale = scale + Math.abs(scalingFactor[k]);
            }
            if (scale == 0.0) {
                diagonal[i] = scalingFactor[i - 1];
                for (int j = 0; j < i; j++) {
                    scalingFactor[j] = covarianceCoordinateSystem[i - 1][j];
                    covarianceCoordinateSystem[i][j] = 0.0;
                    covarianceCoordinateSystem[j][i] = 0.0;
                }
            } else {
                // Generate Householder vector.
                for (int k = 0; k < i; k++) {
                    scalingFactor[k] /= scale;
                    newScalingFactor += scalingFactor[k] * scalingFactor[k];
                }
                double ScalingFactorI = scalingFactor[i - 1];
                double newDiagonalValue = Math.sqrt(newScalingFactor);
                if (ScalingFactorI > 0) {
                    newDiagonalValue = -newDiagonalValue;
                }
                diagonal[i] = scale * newDiagonalValue;
                newScalingFactor = newScalingFactor - ScalingFactorI * newDiagonalValue;
                scalingFactor[i - 1] = ScalingFactorI - newDiagonalValue;
                for (int j = 0; j < i; j++) {
                    diagonal[j] = 0.0;
                }

                // Apply similarity transformation to remaining columns.
                for (int j = 0; j < i; j++) {
                    ScalingFactorI = scalingFactor[j];
                    covarianceCoordinateSystem[j][i] = ScalingFactorI;
                    newDiagonalValue = diagonal[j] + covarianceCoordinateSystem[j][j] * ScalingFactorI;
                    for (int k = j + 1; k <= i - 1; k++) {
                        newDiagonalValue += covarianceCoordinateSystem[k][j] * scalingFactor[k];
                        diagonal[k] += covarianceCoordinateSystem[k][j] * ScalingFactorI;
                    }
                    diagonal[j] = newDiagonalValue;
                }
                ScalingFactorI = 0.0;
                for (int j = 0; j < i; j++) {
                    diagonal[j] /= newScalingFactor;
                    ScalingFactorI += diagonal[j] * scalingFactor[j];
                }
                double diagonalAdaption = ScalingFactorI / (newScalingFactor + newScalingFactor);
                for (int j = 0; j < i; j++) {
                    diagonal[j] -= diagonalAdaption * scalingFactor[j];
                }
                for (int j = 0; j < i; j++) {
                    ScalingFactorI = scalingFactor[j];
                    newDiagonalValue = diagonal[j];
                    for (int k = j; k <= i - 1; k++) {
                        covarianceCoordinateSystem[k][j] -= (ScalingFactorI * diagonal[k] + newDiagonalValue * scalingFactor[k]);
                    }
                    scalingFactor[j] = covarianceCoordinateSystem[i - 1][j];
                    covarianceCoordinateSystem[i][j] = 0.0;
                }
            }
            scalingFactor[i] = newScalingFactor;
        }

        // Accumulate transformations.
        for (int i = 0; i < variableCount - 1; i++) {
            covarianceCoordinateSystem[variableCount - 1][i] = covarianceCoordinateSystem[i][i];
            covarianceCoordinateSystem[i][i] = 1.0;
            double h = scalingFactor[i + 1];
            if (h != 0.0) {
                for (int k = 0; k <= i; k++) {
                    scalingFactor[k] = covarianceCoordinateSystem[k][i + 1] / h;
                }
                for (int j = 0; j <= i; j++) {
                    double g = 0.0;
                    for (int k = 0; k <= i; k++) {
                        g += covarianceCoordinateSystem[k][i + 1] * covarianceCoordinateSystem[k][j];
                    }
                    for (int k = 0; k <= i; k++) {
                        covarianceCoordinateSystem[k][j] -= g * scalingFactor[k];
                    }
                }
            }
            for (int k = 0; k <= i; k++) {
                covarianceCoordinateSystem[k][i + 1] = 0.0;
            }
        }
        for (int j = 0; j < variableCount; j++) {
            scalingFactor[j] = covarianceCoordinateSystem[variableCount - 1][j];
            covarianceCoordinateSystem[variableCount - 1][j] = 0.0;
        }
        covarianceCoordinateSystem[variableCount - 1][variableCount - 1] = 1.0;
        diagonal[0] = 0.0;
    }

    /**
     * Symmetric tridiagonal QL algorithm, taken from JAMA package.
     * <p>
     * This is derived from the Algol procedures computeEigenvaluesOfTridiagonal, by Bowdler, Martin, Reinsch, and
     * Wilkinson, Handbook for Auto. Comp., Vol.ii-Linear Algebra, and the corresponding Fortran subroutine in EISPACK.
     */
    public static void computeEigenvaluesOfTridiagonal(int variableCount, double[] scalingFactor, double[] diagonal, double[][] covarianceCoordinateSystem) {
        System.arraycopy(diagonal, 1, diagonal, 0, variableCount - 1);
        diagonal[variableCount - 1] = 0.0;

        double scalingFactorAdaption = 0.0;
        double subiagonalElement = 0.0;
        final double eps = Math.pow(2.0, -52.0);
        for (int l = 0; l < variableCount; l++) {
            // Find small subdiagonal element
            subiagonalElement = Math.max(subiagonalElement, Math.abs(scalingFactor[l]) + Math.abs(diagonal[l]));
            int m = l;
            while (m < variableCount) {
                if (Math.abs(diagonal[m]) <= eps * subiagonalElement) {
                    break;
                }
                m++;
            }

            // If m == l, scalingFactor[l] is an eigenvalue,
            // otherwise, iterate.
            if (m > l) {
                int iter = 0;
                do {
                    iter = iter + 1;  // (Could check iteration count here.)

                    // Compute implicit shift
                    double eigenvalue = scalingFactor[l];
                    double adaptionValue = (scalingFactor[l + 1] - eigenvalue) / (2.0 * diagonal[l]);
                    double adaptionValueHypothenuse = hypotenuse(adaptionValue, 1.0);
                    if (adaptionValue < 0) {
                        adaptionValueHypothenuse = -adaptionValueHypothenuse;
                    }
                    scalingFactor[l] = diagonal[l] / (adaptionValue + adaptionValueHypothenuse);
                    scalingFactor[l + 1] = diagonal[l] * (adaptionValue + adaptionValueHypothenuse);
                    double nextScalingFactor = scalingFactor[l + 1];
                    double eigenvalueAdaption = eigenvalue - scalingFactor[l];
                    for (int i = l + 2; i < variableCount; i++) {
                        scalingFactor[i] -= eigenvalueAdaption;
                    }
                    scalingFactorAdaption = scalingFactorAdaption + eigenvalueAdaption;

                    // Implicit QL transformation.
                    adaptionValue = scalingFactor[m];
                    double c = 1.0;
                    double c2 = c;
                    double c3 = c;
                    double nextDiagonal = diagonal[l + 1];
                    double diagonalAdaption = 0.0;
                    double nextDiagonalAdaption = 0.0;
                    for (int i = m - 1; i >= l; i--) {
                        c3 = c2;
                        c2 = c;
                        nextDiagonalAdaption = diagonalAdaption;
                        eigenvalue = c * diagonal[i];
                        eigenvalueAdaption = c * adaptionValue;
                        adaptionValueHypothenuse = hypotenuse(adaptionValue, diagonal[i]);
                        diagonal[i + 1] = diagonalAdaption * adaptionValueHypothenuse;
                        diagonalAdaption = diagonal[i] / adaptionValueHypothenuse;
                        c = adaptionValue / adaptionValueHypothenuse;
                        adaptionValue = c * scalingFactor[i] - diagonalAdaption * eigenvalue;
                        scalingFactor[i + 1] = eigenvalueAdaption + diagonalAdaption * (c * eigenvalue + diagonalAdaption * scalingFactor[i]);

                        // Accumulate transformation.
                        for (int k = 0; k < variableCount; k++) {
                            eigenvalueAdaption = covarianceCoordinateSystem[k][i + 1];
                            covarianceCoordinateSystem[k][i + 1] = diagonalAdaption * covarianceCoordinateSystem[k][i] + c * eigenvalueAdaption;
                            covarianceCoordinateSystem[k][i] = c * covarianceCoordinateSystem[k][i] - diagonalAdaption * eigenvalueAdaption;
                        }
                    }
                    adaptionValue = -diagonalAdaption * nextDiagonalAdaption * c3 * nextDiagonal * diagonal[l] / nextScalingFactor;
                    diagonal[l] = diagonalAdaption * adaptionValue;
                    scalingFactor[l] = c * adaptionValue;

                    // Check for convergence.
                } while (Math.abs(diagonal[l]) > eps * subiagonalElement);
            }
            scalingFactor[l] = scalingFactor[l] + scalingFactorAdaption;
            diagonal[l] = 0.0;
        }

        // Sort eigenvalues and corresponding vectors.
        for (int i = 0; i < variableCount - 1; i++) {
            int k = i;
            double newCovarianceCoordinate = scalingFactor[i];
            for (int j = i + 1; j < variableCount; j++) {
                if (scalingFactor[j] < newCovarianceCoordinate) { // NH find smallest k>i
                    k = j;
                    newCovarianceCoordinate = scalingFactor[j];
                }
            }
            if (k != i) {
                scalingFactor[k] = scalingFactor[i]; // swap k and i
                scalingFactor[i] = newCovarianceCoordinate;
                for (int j = 0; j < variableCount; j++) {
                    newCovarianceCoordinate = covarianceCoordinateSystem[j][i];
                    covarianceCoordinateSystem[j][i] = covarianceCoordinateSystem[j][k];
                    covarianceCoordinateSystem[j][k] = newCovarianceCoordinate;
                }
            }
        }
    }

    /**
     * Exhaustive test of the output of the eigendecomposition.  Needs O(n^3) operations.
     *
     * @return the number of detected inaccuracies
     */
    private static int checkEigenSystem(int variableCount, double[][] covarianceMatrix, double[] scalingFactor, double[][] covarianceCoordinateSystem) {

        /* compute covarianceCoordinateSystem scalingFactor covarianceCoordinateSystem^T and covarianceCoordinateSystem covarianceCoordinateSystem^T to check */
        int k;
        int errors = 0;
        double cc;
        double dd;

        for (int i = 0; i < variableCount; ++i) {
            for (int j = 0; j < variableCount; ++j) {
                for (cc = 0., dd = 0., k = 0; k < variableCount; ++k) {
                    cc += scalingFactor[k] * covarianceCoordinateSystem[i][k] * covarianceCoordinateSystem[j][k];
                    dd += covarianceCoordinateSystem[i][k] * covarianceCoordinateSystem[j][k];
                }
                /* check here, is the normalization the right one? */
                if (Math.abs(cc - covarianceMatrix[Math.max(i, j)][Math.min(i, j)]) / Math.sqrt(covarianceMatrix[i][i] * covarianceMatrix[j][j]) > 1e-10
                        && Math.abs(cc - covarianceMatrix[Math.max(i, j)][Math.min(i, j)]) > 1e-9) { /* quite large */
                    // TODO #50 replace with warning
                    System.err.println("imprecise result detected " + i + " " + j + " " + cc + " " + covarianceMatrix[Math.max(i, j)][Math.min(i, j)] + " " + (cc - covarianceMatrix[Math.max(i, j)][Math.min(i, j)]));
                    ++errors;
                }
                if (Math.abs(dd - (i == j ? 1 : 0)) > 1e-10) {
                    // TODO #50 replace with warning
                    System.err.println("imprecise result detected (covarianceCoordinateSystem not orthog.) " + i + " " + j + " " + dd);
                    ++errors;
                }
            }
        }
        return errors;
    }

    /**
     * Compute sqrt(a^2 + b^2) without under/overflow.
     */
    private static double hypotenuse(double a, double b) {
        double hypot = 0;
        if (Math.abs(a) > Math.abs(b)) {
            hypot = b / a;
            hypot = Math.abs(a) * Math.sqrt(1 + hypot * hypot);
        } else if (b != 0) {
            hypot = a / b;
            hypot = Math.abs(b) * Math.sqrt(1 + hypot * hypot);
        }
        return hypot;
    }

    @Override
    protected Map<String, Descriptor> getSpecificOptions() {
        Map<String, Descriptor> options = new HashMap<>();

        options.put("maximumGenerations", new Descriptor<>(maximumGenerations));
        options.put("checkConsistency", new Descriptor<>(checkConsistency));
        options.put("solutionBuilder", new Descriptor<>(solutionBuilder));
        options.put("initialSearchPosition", new Descriptor<>(initialSearchPosition));
        options.put("diagonalIterations", new Descriptor<>(diagonalIterations));
        options.put("populationSize", new Descriptor<>(populationSize));
        options.put("cumulationStepSize", new Descriptor<>(cumulationStepSize));
        options.put("cumulation", new Descriptor<>(cumulation));
        options.put("learningRate", new Descriptor<>(learningRate));
        options.put("learningRateDiagonal", new Descriptor<>(learningRateDiagonal));
        options.put("stepSizeDampening", new Descriptor<>(stepSizeDampening));
        options.put("standardDeviation", new Descriptor<>(standardDeviation));

        return options;
    }

    @Override
    protected boolean setSpecificOption(String name, Descriptor descriptor) {
        try {
            switch (name) {
                case "maximumGenerations":
                    setMaximumGenerations((Integer) descriptor.getValue());
                    break;
                case "checkConsistency":
                    setCheckConsistency((Boolean) descriptor.getValue());
                    break;
                case "solutionBuilder":
                    setSolutionBuilder((RealValuedSolutionBuilder<ST, PT>) descriptor.getValue());
                    break;
                case "initialSearchPosition":
                    setInitialSearchPosition((double[]) descriptor.getValue());
                    break;
                case "diagonalIterations":
                    setDiagonalIterations((Integer) descriptor.getValue());
                    break;
                case "populationSize":
                    setPopulationSize((Integer) descriptor.getValue());
                    break;
                case "cumulationStepSize":
                    setCumulationStepSize((Double) descriptor.getValue());
                    break;
                case "cumulation":
                    setCumulation((Double) descriptor.getValue());
                    break;
                case "learningRate":
                    setLearningRate((Double) descriptor.getValue());
                    break;
                case "learningRateDiagonal":
                    setLearningRateDiagonal((Double) descriptor.getValue());
                    break;
                case "stepSizeDampening":
                    setStepSizeDampening((Double) descriptor.getValue());
                    break;
                case "standardDeviation":
                    setStandardDeviation((Double) descriptor.getValue());
                    break;
                default:
                    // TODO #50 replace with log
                    System.out.println("WARNING: Option " + name + " unknown");
            }

        } catch (Exception e) {
            // TODO #50 replace with log
            System.out.println("WARNING: Option " + name + " could not be set");
            return false;
        }
        return true;
    }

    /**
     * Creates the log headers, and logs the current algorithm configuration
     *
     * @param problem that is being solved in this log
     */
    protected void initializeLog(Problem<PT> problem) {
        if (analytics != null && !logInitialized) {
            logInitialized = true;
            analytics.startAnalytics();
            analytics.logParam("problemSize", problem.getProblemSize());
            List<String> headers = new ArrayList<>();
            headers.add("best quality");
            headers.add("worst quality");
            headers.add("average quality");
            analytics.logAlgorithmStepHeaders(headers);
            analytics.logProblem(problem);
        }
    }

    /**
     * Helper function that is called after the last generation was created
     */
    protected void finalizeLog(Problem<PT> problem) {
        if (logFinalized) {
            return;
        }

        if (analytics != null) {
            getAnalytics().logSolution(bestSolution);
            analytics.finishAnalytics();
        }
        logFinalized = true;
    }

    /**
     * Logs one single step of the algorithm, in case of GA this means statistics about the current generation
     *
     * @param givenSolution currently best known solution
     * @param population    current population
     */
    private void analyticsStep(Solution<ST, PT> givenSolution, List<Solution<ST, PT>> population) {
        if (analytics != null) {
            Solution<ST, PT> worstSolution = population.stream().max(Comparator.comparingDouble(Solution::getQuality)).orElse(givenSolution);
            double averageQuality = population.stream().mapToDouble(Solution::getQuality).average().orElse(0.0);
            List<String> values = new ArrayList<>();
            values.add(String.valueOf(givenSolution.getQuality()));
            values.add(String.valueOf(worstSolution.getQuality()));
            values.add(String.valueOf(averageQuality));
            analytics.logAlgorithmStep(values);
        }
    }

    @Override
    public Solution<ST, PT> solve(Problem<PT> problem) {
        if (problem == null || problem.getProblemGenes() == null || problem.getProblemGenes().isEmpty()) {
            return null;
        }

        initializeLog(problem);
        // we assume the current best solution simply at the 0 position or the initial search position
        bestSolution = solutionBuilder.transformToSolution(initialSearchPosition != null ?
                initialSearchPosition :
                new double[problem.getProblemGenes().get(0).getGene().getVariableCount()], problem);
        evaluator.evaluateQuality(bestSolution);

        return solve(problem, bestSolution);
    }

    @Override
    public Solution<ST, PT> solve(Problem<PT> problem, Solution<ST, PT> bestSolution) {
        if (problem == null || problem.getProblemGenes() == null || problem.getProblemGenes().isEmpty() || bestSolution == null) {
            return null;
        }
        initializeLog(problem);
        // make sure that the best solution was evaluated
        if (bestSolution.getCachets().isEmpty()) {
            getEvaluator().evaluateQuality(bestSolution);
        }
        this.bestSolution = bestSolution;

        // if the first step was not as of yet executed -> do it now
        if (currentGeneration == 0) {
            initialize(problem);
        }

        // run all steps
        while (currentGeneration < maximumGenerations) {
            bestSolution = nextGeneration(problem);
        }
        finalizeLog(problem);

        return bestSolution;
    }

    /**
     * Calculate the bestQuality of the two solutions. Returns the mapping with the better quality.
     *
     * @param solution      mapping to compare
     * @param givenSolution previous best mapping to compare
     * @return new best mapping
     */
    public Solution<ST, PT> bestQuality(Solution<ST, PT> solution, Solution<ST, PT> givenSolution) {
        evaluator.evaluateQuality(solution);

        if (solution.getQuality() < givenSolution.getQuality()) {
            bestSolution = solution;
        }

        return bestSolution;
    }

    public void setMaximumGenerations(int maximumGenerations) {
        this.maximumGenerations = maximumGenerations;
    }

    /**
     * creates the next generation
     */
    public Solution<ST, PT> nextGeneration(Problem<PT> problem) {
        int variableCount = problem.getProblemGenes().get(0).getGene().getVariableCount();

        // update variables if necessary
        if (currentGeneration - lastEigenupdate > 1.0 / learningRate / variableCount / 5.0) {
            eigendecomposition(variableCount);
        }
        if (checkConsistency) {
            performConsistencyUpdate(variableCount);
        }

        // remove the current population
        population.clear();

        // sample distribution
        for (int i = 0; i < populationSize; i++) {
            double[] solutionVariables = new double[variableCount];

            // do mutation
            if (diagonalIterations > currentGeneration) {
                // mutate fast
                for (int j = 0; j < variableCount; j++) {
                    solutionVariables[j] = distributionCentroid[j] + standardDeviation * scalingFactor[j] * RandomUtil.random.nextGaussian();
                }
            } else {
                // mutate better (but slower)

                // initialize the mutation vector
                double[] randomVariableMutation = new double[variableCount];
                for (int j = 0; j < variableCount; j++) {
                    randomVariableMutation[j] = scalingFactor[j] * RandomUtil.random.nextGaussian();
                }

                // apply the mutation vector
                for (int j = 0; j < variableCount; j++) {
                    double sum = 0.0;
                    for (int k = 0; k < variableCount; k++) {
                        sum += covarianceCoordinateSystem[j][k] * randomVariableMutation[k];
                    }
                    solutionVariables[j] = distributionCentroid[j] + standardDeviation * sum;
                }
            }

            // create and evaluate new solution
            Solution<ST, PT> newSolution = solutionBuilder.transformToSolution(solutionVariables, problem);
            bestSolution = bestQuality(newSolution, bestSolution);
            population.add(newSolution);
        }

        currentGeneration++;

        // update internal values
        updateDistribution(variableCount);

        // log and return best
        analyticsStep(bestSolution, population);
        return bestSolution;
    }

    // region Weird Math Stuff

    /**
     * Updates the internal values based on the new population
     *
     * @param variableCount amount of variables in the current problem
     */
    private void updateDistribution(int variableCount) {
        double[] oldDistributionCentroid = Arrays.copyOf(distributionCentroid, distributionCentroid.length);
        double[] evaluationPathAdaption = new double[variableCount];

        // sort population
        population.sort(Comparator.comparingDouble(Solution::getQuality));

        // calculate new distribution centroid
        for (int i = 0; i < variableCount; i++) {
            distributionCentroid[i] = 0;

            Iterator<Solution<ST, PT>> iterator = population.iterator();
            for (int j = 0; j < crossoverParentCount; j++) {
                Solution<ST, PT> s = iterator.next();
                distributionCentroid[i] += crossoverWeights[j] * solutionBuilder.getVaribleFromOriginalVector(s, i);
            }

            evaluationPathAdaption[i] = Math.sqrt(varianceEffectiveness) * (distributionCentroid[i] - oldDistributionCentroid[i]) / standardDeviation;
        }

        // cumulation for standardDeviation(evolutionPathNextGeneration)
        if (diagonalIterations > currentGeneration) {
            // fast
            for (int i = 0; i < variableCount; i++) {
                evolutionPathNextGeneration[i] = (1.0 - cumulationStepSize) *
                        evolutionPathNextGeneration[i] + Math.sqrt(cumulationStepSize * (2.0 - cumulationStepSize)) * evaluationPathAdaption[i] / scalingFactor[i];
            }
        } else {
            // slow
            double[] coordinateSystemInfluence = new double[variableCount];
            for (int i = 0; i < variableCount; i++) {
                double sum = 0.0;
                for (int j = 0; j < variableCount; j++) {
                    sum += covarianceCoordinateSystem[j][i] * evaluationPathAdaption[j];
                }
                coordinateSystemInfluence[i] = sum / scalingFactor[i];
            }

            for (int i = 0; i < variableCount; i++) {
                double sum = 0.0;
                for (int j = 0; j < variableCount; j++) {
                    sum += covarianceCoordinateSystem[i][j] * coordinateSystemInfluence[j];
                }
                evolutionPathNextGeneration[i] = (1.0 - cumulationStepSize) * evolutionPathNextGeneration[i] + Math.sqrt(cumulationStepSize * (2.0 - cumulationStepSize)) * sum;
            }
        }

        // calculate norm(evolutionPath^2)
        double evolutionPathCubeSum = 0;
        for (int i = 0; i < variableCount; i++) {
            evolutionPathCubeSum += evolutionPathNextGeneration[i] * evolutionPathNextGeneration[i];
        }

        // cumulation for covariance matrix
        int cumulationIsAdded = 0;
        if (Math.sqrt(evolutionPathCubeSum) /
                Math.sqrt(1.0 - Math.pow(1.0 - cumulationStepSize, 2.0 * currentGeneration)) /
                chiSquaredDistributionOfVariables < 1.4 + 2.0 /
                (variableCount + 1)) {
            cumulationIsAdded = 1;
        }
        for (int i = 0; i < variableCount; i++) {
            evolutionPath[i] = (1.0 - cumulation) * evolutionPath[i] + cumulationIsAdded * Math.sqrt(cumulation * (2.0 - cumulation)) * evaluationPathAdaption[i];
        }

        // update of covariance matrix
        for (int i = 0; i < variableCount; i++) {
            for (int j = (diagonalIterations >= currentGeneration ? i : 0); j <= i; j++) {
                covarianceMatrix[i][j] =
                        (1.0 - (diagonalIterations >= currentGeneration ? learningRateDiagonal : learningRate)) *
                                covarianceMatrix[i][j] + learningRate * (1.0 / varianceEffectiveness) *
                                (evolutionPath[i] * evolutionPath[j] + (1 - cumulationIsAdded) * cumulation * (2.0 - cumulation) *
                                        covarianceMatrix[i][j]);
                for (int k = 0; k < crossoverParentCount; k++) {
                    Solution<ST, PT> s = population.get(k);
                    covarianceMatrix[i][j] += learningRate * (1 - 1.0 / varianceEffectiveness) * crossoverWeights[k] *
                            (solutionBuilder.getVaribleFromOriginalVector(s, i) - oldDistributionCentroid[i]) *
                            (solutionBuilder.getVaribleFromOriginalVector(s, j) - oldDistributionCentroid[j]) / standardDeviation / standardDeviation;
                }
            }
        }

        // update of standardDeviation
        standardDeviation *= Math.exp(((Math.sqrt(evolutionPathCubeSum) / chiSquaredDistributionOfVariables) - 1) *
                cumulationStepSize / stepSizeDampening);
    }

    /**
     * Updates eigenvalue decomposition for covarianceCoordinateSystem and scalingFactor Look here for eigenvalues:
     * https://en.wikipedia.org/wiki/Eigenvalues_and_eigenvectors
     *
     * @param variableCount amount of variables in the current problem
     */
    private void eigendecomposition(int variableCount) {
        lastEigenupdate = currentGeneration;

        // update scaling factor
        if (diagonalIterations >= currentGeneration) {
            // fast
            for (int i = 0; i < variableCount; i++) {
                scalingFactor[i] = Math.sqrt(covarianceMatrix[i][i]);
            }
        } else {
            // slow

            // update coordinate system from covarianceMatrix
            for (int i = 0; i < variableCount; i++) {
                for (int j = 0; j <= i; j++) {
                    covarianceCoordinateSystem[i][j] = covarianceCoordinateSystem[j][i] = covarianceMatrix[i][j];
                }
            }

            // do eigenvalue decomposition
            double[] diagonal = new double[variableCount];
            matrixToTridiagonalReduction(variableCount, covarianceCoordinateSystem, scalingFactor, diagonal);
            computeEigenvaluesOfTridiagonal(variableCount, scalingFactor, diagonal, covarianceCoordinateSystem);

            if (checkConsistency) {
                checkEigenSystem(variableCount, covarianceMatrix, scalingFactor, covarianceCoordinateSystem);
            }

            // assign scaling factor to eigenvalue square roots
            for (int i = 0; i < variableCount; i++) {
                if (scalingFactor[i] < 0) {
                    // TODO #50 replace this with a warning
                    System.err.println("an eigenvalue has become negative");
                    scalingFactor[i] = 0;
                }
                scalingFactor[i] = Math.sqrt(scalingFactor[i]);
            }
        }
    }

    /**
     * checks and fixes inconsistency issues
     */
    private void performConsistencyUpdate(int variableCount) {
        // check if fitness landscape is too flat
        // TODO: #81 I think regular GA could GREATLY benefit from a check like this, however there it would be just a check, nothing we can do about it
        if (!population.isEmpty()) {
            population.sort(Comparator.comparingDouble(Solution::getQuality));
            if (Double.compare(population.get(0).getQuality(), population.get(Math.min(populationSize - 1, populationSize / 2 + 1) - 1).getQuality()) == 0) {
                // TODO #50 replace this with a WARN log
                System.err.println("WARNING: Re-Considering FitnessFunction advised. Fitness landscape is too flat. increasing deviation in the meantime");
                standardDeviation *= Math.exp(0.2 + cumulationStepSize / stepSizeDampening);
            }
        }

        // check if our scaling factors are between 1e-6 and 1e4 (the sensible range)
        double scalingFactorFix = 1.0;
        boolean scalingFactorisWrong = false;
        double maxScale = Arrays.stream(scalingFactor).max().orElse(0);
        double minScale = Arrays.stream(scalingFactor).min().orElse(0);
        if (maxScale < 1e-6) {
            scalingFactorFix = 1.0 / maxScale;
            scalingFactorisWrong = true;
        } else if (minScale > 1e4) {
            scalingFactorFix = 1.0 / minScale;
            scalingFactorisWrong = true;
        }
        // no else needed as we checked both overflows of the valid range already

        // if scaling factors don't make sense fix em
        if (scalingFactorisWrong) {
            standardDeviation /= scalingFactorFix;
            for (int i = 0; i < variableCount; i++) {
                evolutionPath[i] *= scalingFactorFix;
                scalingFactor[i] *= scalingFactorFix;

                for (int j = 0; j <= i; j++) {
                    covarianceMatrix[i][j] *= scalingFactorFix * scalingFactorFix;
                }
            }
        }
    }

    /**
     * Initializes all variables of the algorithm to sensible values.
     *
     * @param problem to be initialized for
     */
    public void initialize(Problem<PT> problem) {
        // determine variableCount that play into covariance matrix
        int variableCount = problem.getProblemGenes().get(0).getGene().getVariableCount();
        if (variableCount <= 0) {
            throw new IllegalArgumentException("We cannot optimize nothing! variable count must be > 0");
        }

        // validate initial search position
        if (initialSearchPosition != null && variableCount != initialSearchPosition.length) {
            throw new IllegalArgumentException("The initial search position must have the same dimensions as given in the problem");
        }

        // set the diagonal iterations speedup
        if (diagonalIterations < 0) {
            diagonalIterations = 150 * variableCount / populationSize;
        }

        // initialize the fields this evolution strategy needs
        scalingFactor = new double[variableCount];
        evolutionPath = new double[variableCount];
        evolutionPathNextGeneration = new double[variableCount];
        covarianceCoordinateSystem = new double[variableCount][variableCount];
        covarianceMatrix = new double[variableCount][variableCount];
        Arrays.fill(scalingFactor, 1);
        // fill in the diagonals
        for (int i = 0; i < variableCount; i++) {
            covarianceCoordinateSystem[i][i] = 1;
            covarianceMatrix[i][i] = 1;
        }

        // initialize the centroid
        distributionCentroid = new double[variableCount];
        if (initialSearchPosition == null) {
            for (int i = 0; i < variableCount; i++) {
                double offset = standardDeviation * scalingFactor[i];
                distributionCentroid[i] = offset + RandomUtil.random.nextDouble();
            }
        } else {
            for (int i = 0; i < variableCount; i++) {
                distributionCentroid[i] = initialSearchPosition[i] + standardDeviation * scalingFactor[i] * RandomUtil.random.nextGaussian();
            }
        }

        // initialize variables for calculation
        chiSquaredDistributionOfVariables = Math.sqrt(variableCount) * (1.0 - 1.0 / (4.0 * variableCount) + 1.0 / (21.0 * variableCount * variableCount));
        crossoverParentCount = (int) Math.floor(populationSize / 2.0);
        crossoverWeights = new double[crossoverParentCount];

        // initialize crossover weights
        for (int i = 0; i < crossoverParentCount; i++) {
            crossoverWeights[i] = Math.log(crossoverParentCount + 1) - Math.log(i + 1);
        }
        double sum = Arrays.stream(crossoverWeights).sum();
        for (int i = 0; i < crossoverParentCount; i++) {
            crossoverWeights[i] /= sum;
        }
        double sumSq = Arrays.stream(crossoverWeights).map(x -> x * x).sum();
        varianceEffectiveness = 1.0 / sumSq;

        if (cumulationStepSize < 0) {
            cumulationStepSize = (varianceEffectiveness + 2) / (variableCount + varianceEffectiveness + 3);
        }

        if (stepSizeDampening < 0) {
            stepSizeDampening = (1 + 2 * Math.max(0, Math.sqrt((varianceEffectiveness - 1.0) / (variableCount + 1)) - 1)) + cumulationStepSize;
        }

        if (cumulation < 0) {
            cumulation = 4.0 / (variableCount + 4.0);
        }

        if (learningRate < 0) {
            learningRate = 2.0 / (variableCount + 1.41) / (variableCount + 1.41) /
                    varianceEffectiveness + (1 - (1.0 / varianceEffectiveness)) *
                    Math.min(1, (2 * varianceEffectiveness - 1) /
                            (varianceEffectiveness + (variableCount + 2) * (variableCount + 2)));
        }

        if (learningRateDiagonal < 0) {
            learningRateDiagonal = Math.min(1, learningRate * (variableCount + 1.5) / 3.0);
        }

        // verify all parameters actually are correctly initialized

        if (populationSize <= 1) {
            throw new IllegalArgumentException("population size must be more than 1 (this is a genetic algorithm!)");
        }

        if (crossoverParentCount < 1) {
            throw new IllegalArgumentException("number of parents used in recombination must be smaller or equal to populationSize");
        }

        if ((cumulationStepSize <= 0) || (cumulationStepSize > 1)) {
            throw new IllegalArgumentException("cumulationStepSize must be between 0 and 1");
        }

        if (stepSizeDampening <= 0) {
            throw new IllegalArgumentException("step size damping parameter must be > 0");
        }

        if ((cumulation <= 0) || (cumulation > 1)) {
            throw new IllegalArgumentException("cumulation must be between 0 and 1");
        }

        if (varianceEffectiveness < 0) {
            throw new IllegalArgumentException("varianceEffectiveness must be > 0");
        }

        if (learningRate < 0) {
            throw new IllegalArgumentException("learning rate must be > 0");
        }

        if (learningRateDiagonal < 0) {
            throw new IllegalArgumentException("diagonal learning rate must be > 0");
        }

        if (standardDeviation <= 0) {
            throw new IllegalArgumentException("initial standard deviation, must be > 0");
        }

        if (Arrays.stream(scalingFactor).anyMatch(x -> x <= 0)) {
            throw new IllegalArgumentException("initial standard deviations must be positive");
        }
    }

    // endregion Weird Math Stuff

    // endregion CMA-ES specific code

    public void setCheckConsistency(boolean checkConsistency) {
        this.checkConsistency = checkConsistency;
    }

    public void setSolutionBuilder(RealValuedSolutionBuilder<ST, PT> solutionBuilder) {
        this.solutionBuilder = solutionBuilder;
    }

    public void setInitialSearchPosition(double[] initialSearchPosition) {
        this.initialSearchPosition = initialSearchPosition;
    }

    public void setStandardDeviation(double standardDeviation) {
        this.standardDeviation = standardDeviation;
    }

    public void setDiagonalIterations(int diagonalIterations) {
        this.diagonalIterations = diagonalIterations;
    }

    public void setStepSizeDampening(double stepSizeDampening) {
        this.stepSizeDampening = stepSizeDampening;
    }

    public void setLearningRateDiagonal(double learningRateDiagonal) {
        this.learningRateDiagonal = learningRateDiagonal;
    }

    public void setLearningRate(double learningRate) {
        this.learningRate = learningRate;
    }

    public void setCumulation(double cumulation) {
        this.cumulation = cumulation;
    }

    public void setCumulationStepSize(double cumulationStepSize) {
        this.cumulationStepSize = cumulationStepSize;
    }

    public void setPopulationSize(int populationSize) {
        this.populationSize = populationSize;
    }
}
