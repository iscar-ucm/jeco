/*
* File: ScatterSearch.java
* Author: José Luis Risco Martín <jlrisco@ucm.es>
* Created: 2024/03/20 (YYYY/MM/DD)
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
import jeco.core.operator.comparator.SimpleDominance;
import jeco.core.operator.mutation.MutationOperator;
import jeco.core.operator.mutation.PolynomialMutation;
import jeco.core.operator.selection.DiversificationRandom;
import jeco.core.operator.selection.SelectionOperator;
import jeco.core.problem.Problem;
import jeco.core.problem.Solution;
import jeco.core.problem.Solutions;
import jeco.core.problem.Variable;
import jeco.core.util.logger.JecoLogger;

/**
 * Scatter Search algorithm
 * 
 * @param <V> Type of the variables
 */
public class ScatterSearch<V extends Variable<?>> extends Algorithm<V> {

    private static final Logger LOGGER = Logger.getLogger(ScatterSearch.class.getName());

    protected Solutions<V> population;
    protected Integer maxGenerations;
    protected Integer maxPopulationSize;
    protected Boolean stopWhenSolved;
    protected Integer currentGeneration = null;
    protected SelectionOperator<V> diversificationOperator;
    protected MutationOperator<V> improvementOperator;
    protected SimpleDominance<V> dominance = new SimpleDominance<>();

    /**
     * Constructor
     * @param problem The problem to solve
     * @param maxPopulationSize The maximum population size
     * @param maxGenerations The maximum number of generations
     * @param stopWhenSolved Stop when the optimal solution is found
     * @param diversificationOperator The diversification operator
     * @param improvementOperator The improvement operator
     */
    public ScatterSearch(Problem<V> problem, Integer maxPopulationSize, Integer maxGenerations, Boolean stopWhenSolved, SelectionOperator<V> diversificationOperator, MutationOperator<V> improvementOperator) {
        super(problem);
        this.maxGenerations = maxGenerations;
        this.maxPopulationSize = maxPopulationSize;
        this.stopWhenSolved = stopWhenSolved;
        this.diversificationOperator = diversificationOperator;
        this.improvementOperator = improvementOperator;
    }

    @Override
    public void initialize(Solutions<V> initialSolutions) {
        if (initialSolutions == null) {
            population = problem.newRandomSetOfSolutions(maxPopulationSize);
        } else {
            population = initialSolutions;
        }
        problem.evaluate(population);
        population.sort(dominance);
        currentGeneration = 0;
    }

    @Override
    public void step() {
        // Diversification
        Solutions<V> diversePop = diversificationOperator.execute(population);
        for (Solution<V> solution : diversePop) {
            improvementOperator.execute(solution);
        }
        population.addAll(diversePop);
        problem.evaluate(population);
        reduce(population, maxPopulationSize);
        currentGeneration++;
    }

    /**
     * Reduce the population to the maximum size
     * @param population The population
     * @param maxPopulationSize The maximum population size
     */
    private void reduce(Solutions<V> population, int maxPopulationSize) {
        // SOrt the population according to the dominance comparator
        population.sort(dominance);
        // Remove the last solutions exceeding the maximum population size
        while (population.size() > maxPopulationSize) {
            population.remove(population.size() - 1);
        }
    }

    @Override
    public Solutions<V> execute() {
        int nextPercentageReport = 10;
        while (currentGeneration < maxGenerations) {
            step();
            int percentage = Math.round((currentGeneration * 100) / maxGenerations);
            Double bestObj = population.get(0).getObjectives().get(0);
            if (percentage == nextPercentageReport) {
                LOGGER.info(percentage + "% performed ..." + " -- Best fitness: " + bestObj);
                nextPercentageReport += 10;
            }
            if (stopWhenSolved) {
                if (bestObj <= 0) {
                    LOGGER.info("Optimal solution found in " + currentGeneration + " generations.");
                    break;
                }
            }
        }
        return population;
    }

    public static void main(String[] args) {
        JecoLogger.setup(Level.FINE);
        // First create the problem
        Rastringin problem = new Rastringin(4);
        // Second create the algorithm
        DiversificationRandom<Variable<Double>> diversificationOp = new DiversificationRandom<>();
        PolynomialMutation<Variable<Double>> improvementOp = new PolynomialMutation<>(problem);
        ScatterSearch<Variable<Double>> ss = new ScatterSearch<>(problem, 100, 5000, true, diversificationOp, improvementOp);
        ss.initialize();
        Solutions<Variable<Double>> solutions = ss.execute();
        for (Solution<Variable<Double>> solution : solutions) {
            System.out.println("Fitness = " + solution.getObjectives().get(0));
        }
    }
}
