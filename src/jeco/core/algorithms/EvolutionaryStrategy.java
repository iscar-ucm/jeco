/*
* File: EvolutionaryStrategy.java
* Author: José Luis Risco Martín <jlrisco@ucm.es>
* Created: 2010/05/10 (YYYY/MM/DD)
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

package jeco.core.algorithms;

import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

import jeco.core.benchmarks.Rastringin;
import jeco.core.operator.comparator.SimpleDominance;
import jeco.core.operator.crossover.SinglePointCrossover;
import jeco.core.operator.mutation.MutationOperator;
import jeco.core.operator.mutation.PolynomialMutation;
import jeco.core.operator.selection.BinaryTournament;
import jeco.core.operator.selection.SelectionOperator;
import jeco.core.problem.Problem;
import jeco.core.problem.Solution;
import jeco.core.problem.Solutions;
import jeco.core.problem.Variable;
import jeco.core.util.logger.JecoLogger;

/**
 * Evolutionary Strategy algorithm.
 * 
 */
public class EvolutionaryStrategy<V extends Variable<?>> extends Algorithm<V> {

    private static final Logger LOGGER = Logger.getLogger(EvolutionaryStrategy.class.getName());

    /**
     * Selection types
     */
    public static final int SELECTION_PLUS = 1;
    public static final int SELECTION_DEFAULT = SELECTION_PLUS;

    /**
     * Recombination offspring number
     */
    public static final int RHO_DEFAULT = 1;

    /////////////////////////////////////////////////////////////////////////
    /**
     * Stop if the optimal solution is found
     */
    protected boolean stopWhenSolved;
    /**
     * Maximum number of generations
     */
    protected int maxGenerations;
    /**
     * Population size
     */
    protected int mu;
    /**
     * Selection type
     */
    protected int selectionType;
    /**
     * Recombination offspring number
     */
    protected int rho;
    /**
     * Number of offspring
     */
    protected int lambda;
    /////////////////////////////////////////////////////////////////////////
    /**
     * Dominance operator
     */
    protected SimpleDominance<V> dominance = new SimpleDominance<V>();
    /**
     * Current generation
     */
    protected int currentGeneration;
    /**
     * Population
     */
    protected Solutions<V> muPopulation;
    /**
     * Mutation operator
     */
    protected MutationOperator<V> mutationOperator;
    /**
     * Crossover operator
     */
    protected SinglePointCrossover<V> crossoverOperator;
    /**
     * Selection operator
     */
    protected SelectionOperator<V> selectionOperator;

    /**
     * Constructor
     * @param problem Problem to solve
     * @param mutationOperator Mutation operator
     * @param mu Population size
     * @param rho Recombination offspring number
     * @param selectionType Selection type
     * @param lambda Number of offspring
     * @param maxGenerations Maximum number of generations
     * @param stopWhenSolved Stop if the optimal solution is found
     */
    public EvolutionaryStrategy(Problem<V> problem, MutationOperator<V> mutationOperator, int mu, int rho, int selectionType, int lambda, int maxGenerations, boolean stopWhenSolved) {
        super(problem);
        this.mu = mu;
        this.rho = rho;
        this.selectionType = selectionType;
        this.lambda = lambda;
        this.maxGenerations = maxGenerations;
        this.mutationOperator = mutationOperator;
        this.crossoverOperator = new SinglePointCrossover<>(problem, SinglePointCrossover.DEFAULT_FIXED_CROSSOVER_POINT, SinglePointCrossover.DEFAULT_PROBABILITY, SinglePointCrossover.ALLOW_REPETITION);
        this.selectionOperator = new BinaryTournament<>(new SimpleDominance<>());
        this.stopWhenSolved = stopWhenSolved;
    }

    /**
     * Constructor
     * @param problem Problem to solve
     * @param mutationOperator Mutation operator
     * @param mu Population size
     * @param rho Recombination offspring number
     * @param selectionType Selection type
     * @param lambda Number of offspring
     * @param maxGenerations Maximum number of generations
     */
    public EvolutionaryStrategy(Problem<V> problem, MutationOperator<V> mutationOperator, int mu, int lambda, int maxGenerations) {
        this(problem, mutationOperator, mu, 1, SELECTION_PLUS, lambda, maxGenerations, true);
    }

    @Override
    public void initialize(Solutions<V> initialSolutions) {
        if (initialSolutions == null) {
            muPopulation = problem.newRandomSetOfSolutions(mu);
        } else {
            muPopulation = initialSolutions;
        }
        problem.evaluate(muPopulation);
        currentGeneration = 0;
    }

    @Override
    public Solutions<V> execute() {
        int nextPercentageReport = 10;
        while (currentGeneration < maxGenerations) {
            step();
            int percentage = Math.round((currentGeneration * 100) / maxGenerations);
            if (percentage == nextPercentageReport) {
                LOGGER.info(percentage + "% performed ...");
                nextPercentageReport += 10;
            }
            if (stopWhenSolved) {
                Double bestObj = muPopulation.get(0).getObjectives().get(0);
                if (bestObj <= 0) {
                    LOGGER.info("Optimal solution found in " + currentGeneration + " generations.");
                    break;
                }
            }
        }
        return muPopulation;
    }

    @Override
    public void step() {
        currentGeneration++;
        // Create the offSpring solutionSet  
        Solutions<V> lambdaPopulation = new Solutions<>();
        Solution<V> parent1, parent2;
        while (lambdaPopulation.size() < lambda) {
            // We apply recombination if rho is 2 or greater
            // In this version we only apply a traditional recombination of two parents
            Solutions<V> offSpring = new Solutions<>();
            parent1 = selectionOperator.execute(muPopulation).get(0);
            if (rho > RHO_DEFAULT) {
                // obtain 2º parent
                parent2 = selectionOperator.execute(muPopulation).get(0);
                offSpring.addAll(crossoverOperator.execute(parent1, parent2));
            } else {
                offSpring.add(parent1.clone());
            }
            for (Solution<V> solution : offSpring) {
                mutationOperator.execute(solution);
                lambdaPopulation.add(solution);
            }
        } // for
        problem.evaluate(lambdaPopulation);
        // Selection
        if (selectionType == SELECTION_PLUS) {
            lambdaPopulation.addAll(muPopulation);
        }
        // Reorder and reduce:
        Collections.sort(lambdaPopulation, dominance);
        while (lambdaPopulation.size() > mu) {
            lambdaPopulation.remove(lambdaPopulation.size() - 1);
        }
        muPopulation = lambdaPopulation;
    } // step

    public static void main(String[] args) {
        JecoLogger.setup(Level.INFO);
        // First create the problem
        Rastringin problem = new Rastringin(4);
        // Second create the algorithm
        PolynomialMutation<Variable<Double>> mutationOp = new PolynomialMutation<Variable<Double>>(problem);
        EvolutionaryStrategy<Variable<Double>> algorithm = new EvolutionaryStrategy<Variable<Double>>(problem,
                mutationOp, 5, 1, EvolutionaryStrategy.SELECTION_PLUS, 10, 250, true);
        algorithm.initialize();
        Solutions<Variable<Double>> solutions = algorithm.execute();
        for (Solution<Variable<Double>> solution : solutions) {
            LOGGER.info("Fitness = " + solution.getObjectives().get(0));
        }
    }
}
