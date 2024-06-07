/*
* File: NSGAII.java
* Author: José Luis Risco Martín <jlrisco@ucm.es>
* Created: 2010/06/07 (YYYY/MM/DD)
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.logging.Logger;

import jeco.core.benchmarks.dtlz.DTLZ1;
import jeco.core.operator.assigner.CrowdingDistance;
import jeco.core.operator.assigner.FrontsExtractor;
import jeco.core.operator.comparator.ComparatorNSGAII;
import jeco.core.operator.comparator.SolutionDominance;
import jeco.core.operator.crossover.CrossoverOperator;
import jeco.core.operator.crossover.SBXCrossover;
import jeco.core.operator.mutation.MutationOperator;
import jeco.core.operator.mutation.PolynomialMutation;
import jeco.core.operator.selection.BinaryTournamentNSGAII;
import jeco.core.operator.selection.SelectionOperator;
import jeco.core.problem.Problem;
import jeco.core.problem.Solution;
import jeco.core.problem.Solutions;
import jeco.core.problem.Variable;
import jeco.core.util.logger.JecoLogger;

/**
 * NSGA-II algorithm
 * 
 */
public class NSGAII<V extends Variable<?>> extends Algorithm<V> {

    private static final Logger logger = Logger.getLogger(NSGAII.class.getName());
    /////////////////////////////////////////////////////////////////////////
    /**
     * Maximum number of generations
     */
    protected int maxGenerations;
    /**
     * Maximum population size
     */
    protected int maxPopulationSize;
    /////////////////////////////////////////////////////////////////////////
    /**
     * Dominance comparator
     */
    protected Comparator<Solution<V>> dominance;
    /**
     * Current generation
     */
    protected int currentGeneration;
    /**
     * Population
     */
    protected Solutions<V> population;

    /**
     * Get the population
     * @return the population
     */
    public Solutions<V> getPopulation() {
        return population;        
    }

    /**
     * Mutation operator
     */
    protected MutationOperator<V> mutationOperator;
    /**
     * Crossover operator
     */
    protected CrossoverOperator<V> crossoverOperator;
    /**
     * Selection operator
     */
    protected SelectionOperator<V> selectionOperator;

    /**
     * Constructor
     * @param problem Problem to solve
     * @param maxPopulationSize Maximum population size
     * @param maxGenerations Maximum number of generations
     * @param mutationOperator Mutation operator
     * @param crossoverOperator Crossover operator
     * @param selectionOperator Selection operator
     */
    public NSGAII(Problem<V> problem, int maxPopulationSize, int maxGenerations, MutationOperator<V> mutationOperator, CrossoverOperator<V> crossoverOperator, SelectionOperator<V> selectionOperator) {
        super(problem);
        this.maxPopulationSize = maxPopulationSize;
        this.maxGenerations = maxGenerations;
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
        dominance = new SolutionDominance<>();
        problem.evaluate(population);
        // Compute crowding distance
        CrowdingDistance<V> assigner = new CrowdingDistance<>(problem.getNumberOfObjectives());
        assigner.execute(population);
        currentGeneration = 0;
    }

    @Override
    public Solutions<V> execute() {
        int nextPercentageReport = 10;
        while (currentGeneration < maxGenerations) {
            step();
            int percentage = Math.round((currentGeneration * 100) / maxGenerations);
            if (percentage == nextPercentageReport) {
                logger.info(percentage + "% performed ...");
                nextPercentageReport += 10;
            }

        }
        return this.getCurrentSolution();
    }

    /**
     * Get the current solution. It reduces the population to the set of non-dominated solutions.
     * @return the population of non-dominated solutions
     */
    public Solutions<V> getCurrentSolution() {
        population.reduceToNonDominated(dominance);
        return population;
    }

    @Override
    public void step() {
        currentGeneration++;
        // Create the offSpring solutionSet
        if (population.size() < 2) {
            logger.severe("Generation: " + currentGeneration + ". Population size is less than 2.");
            return;
        }

        Solutions<V> childPop = new Solutions<V>();
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

        // Create the solutionSet union of solutionSet and offSpring
        Solutions<V> mixedPop = new Solutions<V>();
        mixedPop.addAll(population);
        mixedPop.addAll(childPop);

        // Reducing the union
        population = reduce(mixedPop, maxPopulationSize);
        logger.fine("Generation " + currentGeneration + "/" + maxGenerations + "\n" + population.toString());
    } // step

    /**
     * Reduce the population to a maximum size
     * @param pop Population
     * @param maxSize Maximum size
     * @return Reduced population
     */
    public Solutions<V> reduce(Solutions<V> pop, int maxSize) {
        FrontsExtractor<V> extractor = new FrontsExtractor<V>(dominance);
        ArrayList<Solutions<V>> fronts = extractor.execute(pop);

        Solutions<V> reducedPop = new Solutions<V>();
        CrowdingDistance<V> assigner = new CrowdingDistance<V>(problem.getNumberOfObjectives());
        Solutions<V> front;
        int i = 0;
        while (reducedPop.size() < maxSize && i < fronts.size()) {
            front = fronts.get(i);
            assigner.execute(front);
            reducedPop.addAll(front);
            i++;
        }

        ComparatorNSGAII<V> comparator = new ComparatorNSGAII<>();
        if (reducedPop.size() > maxSize) {
            Collections.sort(reducedPop, comparator);
            while (reducedPop.size() > maxSize) {
                reducedPop.remove(reducedPop.size() - 1);
            }
        }
        return reducedPop;
    }

    /**
     * Set the mutation operator
     * @param mutationOperator Mutation operator
     */
    public void setMutationOperator(MutationOperator<V> mutationOperator) {
        this.mutationOperator = mutationOperator;
    }

    /**
     * Set the crossover operator
     * @param crossoverOperator Crossover operator
     */
    public void setCrossoverOperator(CrossoverOperator<V> crossoverOperator) {
        this.crossoverOperator = crossoverOperator;
    }

    /**
     * Set the selection operator
     * @param selectionOperator Selection operator
     */
    public void setSelectionOperator(SelectionOperator<V> selectionOperator) {
        this.selectionOperator = selectionOperator;
    }

    /**
     * Set the maximum number of generations
     * @param maxGenerations Maximum number of generations
     */
    public void setMaxGenerations(int maxGenerations) {
        this.maxGenerations = maxGenerations;
    }

    /**
     * Set the maximum population size
     * @param maxPopulationSize Maximum population size
     */
    public void setMaxPopulationSize(int maxPopulationSize) {
        this.maxPopulationSize = maxPopulationSize;
    }

    public static void main(String[] args) {
		JecoLogger.setup();
		// First create the problem
		DTLZ1 problem = new DTLZ1(30);
		// Second create the algorithm
		NSGAII<Variable<Double>> algorithm = new NSGAII<Variable<Double>>(problem, 100, 250, new PolynomialMutation<Variable<Double>>(problem), new SBXCrossover<Variable<Double>>(problem), new BinaryTournamentNSGAII<Variable<Double>>());
		algorithm.initialize();
		Solutions<Variable<Double>> solutions = algorithm.execute();
		logger.info("solutions.size()="+ solutions.size());
		System.out.println(solutions.toString());
	}
}
