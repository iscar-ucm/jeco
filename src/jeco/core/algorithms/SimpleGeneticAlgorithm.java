/*
* File: SimpleGeneticAlgorithm.java
* Author: José Luis Risco Martín <jlrisco@ucm.es>
* Created: 2010/05/17 (YYYY/MM/DD)
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
import jeco.core.operator.crossover.CrossoverOperator;
import jeco.core.operator.crossover.SBXCrossover;
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
 * This class implements a simple genetic algorithm.
 */
public class SimpleGeneticAlgorithm<V extends Variable<?>> extends Algorithm<V> {

    private static final Logger LOGGER = Logger.getLogger(SimpleGeneticAlgorithm.class.getName());

    /////////////////////////////////////////////////////////////////////////
    /**
     * Stop when the optimal solution is found.
     */
    protected Boolean stopWhenSolved = null;
    /**
     * Maximum number of generations.
     */
    protected Integer maxGenerations = null;
    /**
     * Maximum population size.
     */
    protected Integer maxPopulationSize = null;
    /**
     * Current generation.
     */
    protected Integer currentGeneration = null;
    /////////////////////////////////////////////////////////////////////////
    /**
     * Dominance comparator.
     */
    protected SimpleDominance<V> dominance = new SimpleDominance<>();
    /**
     * Population of solutions.
     */
    protected Solutions<V> population;
    /**
     * Leaders of the population.
     */
    protected Solutions<V> leaders;
    /**
     * Mutation operator.
     */
    protected MutationOperator<V> mutationOperator;
    /**
     * Crossover operator.
     */
    protected CrossoverOperator<V> crossoverOperator;
    /**
     * Selection operator.
     */
    protected SelectionOperator<V> selectionOperator;

    /**
     * Constructor.
     * @param problem Problem to solve.
     * @param maxPopulationSize Maximum population size.
     * @param maxGenerations Maximum number of generations.
     * @param stopWhenSolved Stop when the optimal solution is found.
     * @param mutationOperator Mutation operator.
     * @param crossoverOperator Crossover operator.
     * @param selectionOperator Selection operator.
     */
    public SimpleGeneticAlgorithm(Problem<V> problem, Integer maxPopulationSize, Integer maxGenerations, Boolean stopWhenSolved, MutationOperator<V> mutationOperator, CrossoverOperator<V> crossoverOperator, SelectionOperator<V> selectionOperator) {
        super(problem);
        this.maxGenerations = maxGenerations;
        this.maxPopulationSize = maxPopulationSize;
        this.stopWhenSolved = stopWhenSolved;
        this.mutationOperator = mutationOperator;
        this.crossoverOperator = crossoverOperator;
        this.selectionOperator = selectionOperator;
    }

    @Override
    public void initialize(Solutions<V> initialSolutions) {
        if (initialSolutions == null) {
            population = problem.newRandomSetOfSolutions(maxPopulationSize);
        } else {
            population = initialSolutions;
        }
        problem.evaluate(population);
        leaders = new Solutions<>();
        for (Solution<V> solution : population) {
            leaders.add(solution.clone());
        }
        reduceLeaders();
        currentGeneration = 0;
    }

    @Override
    public Solutions<V> execute() {
        int nextPercentageReport = 10;
        while (currentGeneration < maxGenerations) {
            step();
            int percentage = Math.round((currentGeneration * 100) / maxGenerations);
            Double bestObj = leaders.get(0).getObjectives().get(0);
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

        return leaders;
    }

    @Override
    public void step() {
        currentGeneration++;
        // Create the offSpring solutionSet        
        Solutions<V> childPop = new Solutions<>();
        Solution<V> parent1, parent2;
        for (int i = 0; i < (maxPopulationSize / 2); i++) {
            //obtain parents
            parent1 = selectionOperator.execute(population).get(0);
            parent2 = selectionOperator.execute(population).get(0);
            Solutions<V> offSpring = crossoverOperator.execute(parent1, parent2);
            for (Solution<V> solution : offSpring) {
                mutationOperator.execute(solution);
                childPop.add(solution);
            }
        } // for
        problem.evaluate(childPop);
        population = childPop;
        //Actualize the archive
        for (Solution<V> solution : population) {
            Solution<V> clone = solution.clone();
            leaders.add(clone);
        }
        reduceLeaders();
    }

    /**
     * Reduce the leaders to the maximum population size.
     */
    public void reduceLeaders() {
        Collections.sort(leaders, dominance);
        // Remove repetitions:
        int compare;
        Solution<V> solI;
        Solution<V> solJ;
        for (int i = 0; i < leaders.size() - 1; i++) {
            solI = leaders.get(i);
            for (int j = i + 1; j < leaders.size(); j++) {
                solJ = leaders.get(j);
                compare = dominance.compare(solI, solJ);
                if (compare == 0) { // i == j, just one copy
                    leaders.remove(j--);
                }
            }
        }
        if (leaders.size() <= maxPopulationSize) {
            return;
        }
        while (leaders.size() > maxPopulationSize) {
            leaders.remove(leaders.size() - 1);
        }
    }

    /**
     * Get the population of solutions.
     * @return Population of solutions.
     */
    public Solutions<V> getSolutions() {
        return population;
    }

    /**
     * Get the leaders of the whole optimization.
     * @return Leaders of the whole optimization.
     */
    public Solutions<V> getLeaders() {
        return leaders;
    }

    public static void main(String[] args) {
        JecoLogger.setup(Level.FINE);
        // First create the problem
        Rastringin problem = new Rastringin(4);
        // Second create the algorithm
        PolynomialMutation<Variable<Double>> mutationOp = new PolynomialMutation<>(problem);
        SBXCrossover<Variable<Double>> crossoverOp = new SBXCrossover<>(problem);
        SimpleDominance<Variable<Double>> comparator = new SimpleDominance<>();
        BinaryTournament<Variable<Double>> selectionOp = new BinaryTournament<>(comparator);
        SimpleGeneticAlgorithm<Variable<Double>> ga = new SimpleGeneticAlgorithm<>(problem, 100, 5000, true, mutationOp,
                crossoverOp, selectionOp);
        ga.initialize();
        Solutions<Variable<Double>> solutions = ga.execute();
        for (Solution<Variable<Double>> solution : solutions) {
            System.out.println("Fitness = " + solution.getObjectives().get(0));
        }
        // System.out.println("solutions.size()="+ solutions.size());
        // System.out.println(solutions.toString());
        // System.out.println("solutions.size()="+ solutions.size());
    }

}
