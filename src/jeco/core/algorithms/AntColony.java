/*
* File: AntColony.java
* Author: José Luis Risco Martín <jlrisco@ucm.es>
* Created: 2024/05/08 (YYYY/MM/DD)
*
* Copyright (C) 2024
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
package jeco.core.algorithms;

import java.util.logging.Level;
import java.util.logging.Logger;

import jeco.core.benchmarks.Rastringin;
import jeco.core.problem.Problem;
import jeco.core.problem.Solution;
import jeco.core.problem.Solutions;
import jeco.core.problem.Variable;
import jeco.core.util.logger.JecoLogger;
import jeco.core.util.random.RandomGenerator;

/**
 * Ant Colony Optimization algorithm.
 */
public class AntColony extends Algorithm<Variable<Double>> {

    private static final Logger LOGGER = Logger.getLogger(AntColony.class.getName());

    /**
     * Phoromone influence.
     */
    protected Double alpha = 1.0; // Influencia de la feromona
    /**
     * Heuristic influence.
     */
    protected Double beta = 2.0;  // Influencia de la heurística
    /**
     * Evaporation rate.
     */
    protected Double evaporationRate = 0.5;
    /**
     * Quantity of pheromone deposited.
     */
    protected Double q = 100.0;     // Cantidad de feromona depositada
    /**
     * Initial pheromone.
     */
    protected Double initPheromone = 1.0;
    /**
     * Number of states.
     */
    protected Integer numStates = 100;

    /**
     * Number of ants.
     */
    protected int numAnts;
    /**
     * Ants.
     */
    protected Solutions<Variable<Double>> ants;
    /**
     * Best fitness.
     */
    protected Double bestFitness = Double.POSITIVE_INFINITY;
    /**
     * Best solution.
     */
    protected Solution<Variable<Double>> bestSolution;

    private double[][] pheromones;

    /**
     * Current iteration.
     */
    protected int currentIteration = 0;
    /**
     * Maximum number of iterations.
     */
    protected int maxIterations = 1000;
    /**
     * Stop when the optimal solution (considered zero) is found.
     */
    protected boolean stopWhenSolved = false;

    /**
     * Constructor.
     * @param problem Problem to solve.
     * @param numAnts Number of ants.
     * @param maxIterations Maximum number of iterations.
     * @param stopWhenSolved Stop when the optimal solution (0) is found.
     */
    public AntColony(Problem<Variable<Double>> problem, int numAnts, int maxIterations, boolean stopWhenSolved) {
        super(problem);
        this.numAnts = numAnts;
        this.maxIterations = maxIterations;
        this.stopWhenSolved = stopWhenSolved;
    }

    @Override
    public void initialize(Solutions<Variable<Double>> myants) {
        if (myants == null) {
            ants = problem.newRandomSetOfSolutions(numAnts);
        } else {
            ants = myants;
        }
        pheromones = new double[problem.getNumberOfVariables()][numStates]; // 100 posibles estados por variable
        for (int i = 0; i < pheromones.length; i++) {
            for (int j = 0; j < pheromones[i].length; j++) {
                pheromones[i][j] = initPheromone;
            }
        }
        problem.evaluate(ants);
        this.currentIteration = 0;
        this.bestFitness = ants.get(0).getObjective(0);
        this.bestSolution = ants.get(0).clone();
    }

    @Override
    public void step() {
        currentIteration++;
        for (int a=0; a < numAnts; a++) {
            Solution<Variable<Double>> ant = ants.get(a);
            for (int v=0; v < problem.getNumberOfVariables(); v++) {
                double lowerBound = problem.getLowerBound(v);
                double upperBound = problem.getUpperBound(v);
                Double nextState = chooseNextState(v, lowerBound, upperBound);
                ant.getVariables().get(v).setValue(nextState);
            }
        }
        // Update pheromones
        for (int v = 0; v < problem.getNumberOfVariables(); v++) {
            for (int s = 0; s < numStates; s++) {
                pheromones[v][s] *= (1 - evaporationRate);
            }
        }
        for (int a = 0; a < numAnts; a++) {
            Solution<Variable<Double>> ant = ants.get(a);
            problem.evaluate(ant);
            if (ant.getObjective(0) < bestFitness) {
                bestFitness = ant.getObjectives().get(0);
                bestSolution = ant.clone();
            }
            for (int v = 0; v < problem.getNumberOfVariables(); v++) {
                int s = (int) ((ant.getVariable(v).getValue().doubleValue() - problem.getLowerBound(v))
                        / (problem.getUpperBound(v) - problem.getLowerBound(v)) * (numStates-1));
                pheromones[v][s] += q / ant.getObjective(0);
            }
        }
    }

    @Override
    public Solutions<Variable<Double>> execute() {
        int nextPercentageReport = 10;
        while (currentIteration < maxIterations) {
            step();
            int percentage = Math.round((currentIteration * 100) / maxIterations);
            Double bestObj = bestSolution.getObjective(0);
            if (percentage == nextPercentageReport) {
                LOGGER.info(percentage + "% performed ..." + " -- Best fitness: " + bestObj);
                nextPercentageReport += 10;
            }
            if (stopWhenSolved) {
                if (bestObj <= 0) {
                    LOGGER.info("Optimal solution found in " + currentIteration + " generations.");
                    break;
                }
            }
        }
        Solutions<Variable<Double>> solutions = new Solutions<>();
        solutions.add(bestSolution);
        return solutions;
    }

    /**
     * Choose the next state for a variable.
     * @param v Variable index.
     * @param lowerBound Lower bound of the variable.
     * @param upperBound Upper bound of the variable.
     * @return Next state.
     */
    private Double chooseNextState(int v, double lowerBound, double upperBound) {
        double[] probabilities = new double[numStates];
        double sum = 0.0;

        for (int i = 0; i < numStates; i++) {
            probabilities[i] = Math.pow(pheromones[v][i], alpha);
            sum += probabilities[i];
        }

        double choice = RandomGenerator.nextDouble(sum);
        sum = 0.0;
        for (int i = 0; i < numStates; i++) {
            sum += probabilities[i];
            if (sum >= choice) {
                return i / (numStates-1.0) * (upperBound - lowerBound) + lowerBound;
            }
        }

        return lowerBound; // Fallback

    }
    
    public static void main(String[] args) {
        JecoLogger.setup(Level.FINE);
        // First create the problem
        Rastringin problem = new Rastringin(4);
        // Second create the algorithm
        AntColony aco = new AntColony(problem, 100, 5000, true);
        aco.initialize();
        Solutions<Variable<Double>> solutions = aco.execute();
        for (Solution<Variable<Double>> solution : solutions) {
            System.out.println("Fitness = " + solution.getObjectives().get(0));
        }
    }

}
