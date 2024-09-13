/*
* File: SinglePointCrossover.java
* Author: José Luis Risco Martín <jlrisco@ucm.es>
* Created: 2010/09/13 (YYYY/MM/DD)
*
* Copyright (C) 2010
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package jeco.core.operator.crossover;

import jeco.core.problem.Problem;
import jeco.core.problem.Solution;
import jeco.core.problem.Solutions;
import jeco.core.problem.Variable;
import jeco.core.util.random.RandomGenerator;

/**
 * Class for single point crossover.
 * 
 * This class implements the single point crossover operator. The crossover point
 * is selected randomly. The operator can be configured to use a fixed crossover
 * point or not. If not, the crossover point is selected randomly for each pair
 * of parents.
 */
public class SinglePointCrossover<T extends Variable<?>> extends CrossoverOperator<T> {

    public static final double DEFAULT_PROBABILITY = 0.9;
    public static final boolean DEFAULT_FIXED_CROSSOVER_POINT = true;
    public static final int ALLOW_REPETITION = 0;
    public static final int AVOID_REPETITION_IN_SET = 1;
    public static final int AVOID_REPETITION_IN_FRONT = 2;
    
    /**
     * Probability of crossover
     */
    protected double probability;
    /**
     * Fixed crossover point
     */
    protected boolean fixedCrossoverPoint;
    /**
     * Repetition mode. It can be ALLOW_REPETITION, AVOID_REPETITION_IN_SET or AVOID_REPETITION_IN_FRONT
     */
    protected int repetition;
    /**
     * Problem
     */
    protected Problem<T> problem;

    /**
     * Constructor
     * @param problem Problem being optimized
     * @param fixedCrossoverPoint Fixed crossover point
     * @param probability Probability of crossover
     * @param repetition Repetition mode
     */
    public SinglePointCrossover(Problem<T> problem, boolean fixedCrossoverPoint, double probability, int repetition) {
        this.problem = problem;
        this.fixedCrossoverPoint = fixedCrossoverPoint;
        this.probability = probability;
        this.repetition = repetition;
    }  // SinglePointCrossover

    /**
     * Constructor
     * @param problem Problem being optimized
     * @param fixedCrossoverPoint Fixed crossover point
     * @param probability Probability of crossover
     */
    public SinglePointCrossover(Problem<T> problem, boolean fixedCrossoverPoint, double probability) {
        this(problem, fixedCrossoverPoint, probability, ALLOW_REPETITION);
    }

    /**
     * Constructor
     * @param problem Problem being optimized
     * @param fixedCrossoverPoint Fixed crossover point
     */
    public SinglePointCrossover(Problem<T> problem) {
        this(problem, DEFAULT_FIXED_CROSSOVER_POINT, DEFAULT_PROBABILITY, ALLOW_REPETITION);
    } // SinglePointCrossover

    /**
     * Computes the crossover point
     *
     * @param sol1Length Length of chromosome 1
     * @param sol2Length Length of chromosome 2
     * @return Crossover point
     */
    private int computeCrossoverPoint(int sol1Length, int sol2Length) {
        int point = 0;
        if (sol1Length < sol2Length) {
            point = RandomGenerator.nextInt(0, sol1Length);
        } else {
            point = RandomGenerator.nextInt(0, sol1Length);
        }
        return point;
    }

    /**
     * Makes a new solution
     *
     * @param sol1 Solution 1
     * @param sol2 Solution 2
     */
    private void makeNewSolution(Solution<T> sol1, Solution<T> sol2) {
        int sol1Length = sol1.getVariables().size();
        int sol2Length = sol2.getVariables().size();

        if (repetition == AVOID_REPETITION_IN_SET) {
            boolean equals = true;
            int minLength = Math.min(sol1Length, sol2Length);
            for (int i = 0; i < minLength && equals; ++i) {
                equals = equals && sol1.getVariable(i).equals(sol2.getVariable(i));
            }
            if (equals) {
                Solution<T> tmp2 = problem.newRandomSetOfSolutions(1).get(0);
                for(int i=0; i<sol2Length; ++i) {
                    sol2.getVariables().set(i, tmp2.getVariable(i));
                }
                return;
            }
        }
        else if (repetition == AVOID_REPETITION_IN_FRONT) {
            boolean equals = true;
            for (int i = 0; i < problem.getNumberOfObjectives() && equals; ++i) {
                equals = equals && (sol1.getObjective(i).equals(sol2.getObjective(i)));
            }
            if (equals) {
                Solution<T> tmp2 = problem.newRandomSetOfSolutions(1).get(0);
                for(int i=0; i<sol2Length; ++i) {
                    sol2.getVariables().set(i, tmp2.getVariable(i));
                }
                return;
            }
        }

        if (fixedCrossoverPoint) {
            int point1 = computeCrossoverPoint(sol1Length, sol2Length);
            T tmp1, tmp2;
            for (int i = 0; i < point1; i++) {
                tmp1 = sol1.getVariables().get(i);
                tmp2 = sol2.getVariables().get(i);
                sol1.getVariables().set(i, tmp2);
                sol2.getVariables().set(i, tmp1);
            }
        } else {
            // TODO: review this algorithm !!!
            int point1 = RandomGenerator.nextInt(sol1Length);
            int point2 = RandomGenerator.nextInt(sol2Length);

            Solution<T> tmp1 = sol1.clone();
            Solution<T> tmp2 = sol2.clone();

            int index1 = 0;
            int index2 = 0;

            for (int i = 0; i < point1; i++) {
                sol1.getVariables().set(index1++, tmp1.getVariables().get(i));
            }
            for (int i = point2; i < sol2Length; i++) {
                sol1.getVariables().set(index1++, tmp2.getVariables().get(i));
            }
            for (int i = 0; i < point2; i++) {
                sol2.getVariables().set(index2++, tmp2.getVariables().get(i));
            }
            for (int i = point1; i < sol1Length; i++) {
                sol2.getVariables().set(index2++, tmp1.getVariables().get(i));
            }
        }
    }

    /**
     * Executes the operation
     *
     * @param probability Probability of crossover
     * @param parent1 Parent 1
     * @param parent2 Parent 2
     * @return Offspring
     */
    public Solutions<T> doCrossover(double probability, Solution<T> parent1, Solution<T> parent2) {

        Solutions<T> offSpring = new Solutions<T>();

        Solution<T> offSpring0 = parent1.clone();
        Solution<T> offSpring1 = parent2.clone();

        if (RandomGenerator.nextDouble() <= probability) {
            if (RandomGenerator.nextBoolean()) {
                this.makeNewSolution(offSpring0, offSpring1);
            } else {
                this.makeNewSolution(offSpring1, offSpring0); // if
            }
        }
        offSpring.add(offSpring0);
        offSpring.add(offSpring1);
        return offSpring;
    } // doCrossover

    /**
     * Executes the operation
     *
     * @param object An object containing an array of two parents
     * @return An object containing the offSprings
     */
    public Solutions<T> execute(Solution<T> parent1, Solution<T> parent2) {
        return doCrossover(probability, parent1, parent2);
    } // execute

    public void setProbability(double probability) {
        this.probability = probability;
    }
} // SBXCrossover

