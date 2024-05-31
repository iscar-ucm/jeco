/*
* File: GrammaticalEvolution.java
* Author: José Luis Risco Martín <jlrisco@ucm.es>
* Author: José M. Colmenar <josemanuel.colmenar@urjc.es>
* Created: 2010/05/31 (YYYY/MM/DD)
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

import jeco.core.algorithms.metaheuristic.moga.NSGAII;
import jeco.core.operator.assigner.CrowdingDistance;
import jeco.core.operator.assigner.FrontsExtractor;
import jeco.core.operator.comparator.ComparatorNSGAII;
import jeco.core.operator.comparator.SolutionDominance;
import jeco.core.operator.crossover.CrossoverOperator;
import jeco.core.operator.crossover.SinglePointCrossover;
import jeco.core.operator.mutation.IntegerFlipMutation;
import jeco.core.operator.mutation.MutationOperator;
import jeco.core.operator.selection.BinaryTournamentNSGAII;
import jeco.core.operator.selection.SelectionOperator;
import jeco.core.problem.Problem;
import jeco.core.problem.Solution;
import jeco.core.problem.Solutions;
import jeco.core.problem.Variable;

/**
 * Multi-objective Grammatical Evolution Algorithm. Based on NSGA-II
 *
 */
public class GrammaticalEvolution extends Algorithm<Variable<Integer>> {

    public static final Logger LOGGER = Logger.getLogger(NSGAII.class.getName());

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
    protected Comparator<Solution<Variable<Integer>>> dominance;
    /**
     * Current generation
     */
    protected int currentGeneration;
    /**
     * Population
     */
    protected Solutions<Variable<Integer>> population;

    /**
     * Get the current population
     * @return the current population
     */
    public Solutions<Variable<Integer>> getPopulation() {
        return population;
    }
    /**
     * Mutation operator
     */
    protected MutationOperator<Variable<Integer>> mutationOperator;
    /**
     * Crossover operator
     */
    protected CrossoverOperator<Variable<Integer>> crossoverOperator;
    /**
     * Selection operator
     */
    protected SelectionOperator<Variable<Integer>> selectionOperator;

    /**
     * Constructor
     *
     * @param problem Problem to solve
     * @param maxPopulationSize Maximum population size
     * @param maxGenerations Maximum number of generations
     * @param probMutation Probability of mutation
     * @param probCrossover Probability of crossover
     */
    public GrammaticalEvolution(Problem<Variable<Integer>> problem, int maxPopulationSize, int maxGenerations, double probMutation, double probCrossover) {
        super(problem);
        this.maxPopulationSize = maxPopulationSize;
        this.maxGenerations = maxGenerations;
        this.mutationOperator = new IntegerFlipMutation<Variable<Integer>>(problem, probMutation);
        this.crossoverOperator = new SinglePointCrossover<Variable<Integer>>(problem, SinglePointCrossover.DEFAULT_FIXED_CROSSOVER_POINT, probCrossover, SinglePointCrossover.ALLOW_REPETITION);
        this.selectionOperator = new BinaryTournamentNSGAII<Variable<Integer>>();
    }

    /**
     * Constructor
     *
     * @param problem Problem to solve
     * @param maxPopulationSize Maximum population size
     * @param maxGenerations Maximum number of generations
     */
    public GrammaticalEvolution(Problem<Variable<Integer>> problem, int maxPopulationSize, int maxGenerations) {
        this(problem, maxPopulationSize, maxGenerations, 1.0 / problem.getNumberOfVariables(), SinglePointCrossover.DEFAULT_PROBABILITY);
    }

    @Override
    public void initialize(Solutions<Variable<Integer>> initialSolutions) {
        // Create the initial solutionSet
        if (initialSolutions == null) {
            population = problem.newRandomSetOfSolutions(maxPopulationSize);
        } else {
            population = initialSolutions;
        }
        problem.evaluate(population);
        dominance = new SolutionDominance<>();
        // Compute crowding distance
        CrowdingDistance<Variable<Integer>> assigner = new CrowdingDistance<Variable<Integer>>(problem.getNumberOfObjectives());
        assigner.execute(population);
        currentGeneration = 0;
    }

    @Override
    public Solutions<Variable<Integer>> execute() {
        int nextPercentageReport = 10;
        while (currentGeneration < maxGenerations) {
            step();
            int percentage = Math.round((currentGeneration * 100) / maxGenerations);
            if (percentage == nextPercentageReport) {
                LOGGER.info(percentage + "% performed ...");
                LOGGER.info("@ # Gen. " + currentGeneration + ", objective values:");
                // Print current population
                Solutions<Variable<Integer>> pop = this.getPopulation();
                for (Solution<Variable<Integer>> s : pop) {
                    for (int i = 0; i < s.getObjectives().size(); i++) {
                        LOGGER.fine(s.getObjective(i) + ";");
                    }
                }
                nextPercentageReport += 10;
            }
        }
        return this.getCurrentSolution();
    }

    /**
     * Get the current non-dominated population
     * @return the current non-dominated population
     */
    public Solutions<Variable<Integer>> getCurrentSolution() {
        population.reduceToNonDominated(dominance);
        return population;
    }

    @Override
    public void step() {
        currentGeneration++;
        // Create the offSpring solutionSet
        if (population.size() < 2) {
            LOGGER.severe("Generation: " + currentGeneration + ". Population size is less than 2.");
            return;
        }

        Solutions<Variable<Integer>> childPop = new Solutions<Variable<Integer>>();
        Solution<Variable<Integer>> parent1, parent2;
        for (int i = 0; i < (maxPopulationSize / 2); i++) {
            //obtain parents
            parent1 = selectionOperator.execute(population).get(0);
            parent2 = selectionOperator.execute(population).get(0);
            Solutions<Variable<Integer>> offSpring = crossoverOperator.execute(parent1, parent2);
            for (Solution<Variable<Integer>> solution : offSpring) {
                mutationOperator.execute(solution);
                childPop.add(solution);
            }
        } // for
        problem.evaluate(childPop);

        // Create the solutionSet union of solutionSet and offSpring
        Solutions<Variable<Integer>> mixedPop = new Solutions<Variable<Integer>>();
        mixedPop.addAll(population);
        mixedPop.addAll(childPop);

        // Reducing the union
        population = reduce(mixedPop, maxPopulationSize);
        LOGGER.fine("Generation " + currentGeneration + "/" + maxGenerations + "\n" + population.toString());
    } // step

    /**
     * Reduce the population to a maximum size
     * @param pop Population
     * @param maxSize Maximum size
     * @return Reduced population
     */
    public Solutions<Variable<Integer>> reduce(Solutions<Variable<Integer>> pop, int maxSize) {
        FrontsExtractor<Variable<Integer>> extractor = new FrontsExtractor<Variable<Integer>>(dominance);
        ArrayList<Solutions<Variable<Integer>>> fronts = extractor.execute(pop);

        Solutions<Variable<Integer>> reducedPop = new Solutions<Variable<Integer>>();
        CrowdingDistance<Variable<Integer>> assigner = new CrowdingDistance<Variable<Integer>>(problem.getNumberOfObjectives());
        Solutions<Variable<Integer>> front;
        int i = 0;
        while (reducedPop.size() < maxSize && i < fronts.size()) {
            front = fronts.get(i);
            assigner.execute(front);
            reducedPop.addAll(front);
            i++;
        }

        ComparatorNSGAII<Variable<Integer>> comparator = new ComparatorNSGAII<Variable<Integer>>();
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
    public void setMutationOperator(MutationOperator<Variable<Integer>> mutationOperator) {
        this.mutationOperator = mutationOperator;
    }

    /**
     * Set the crossover operator
     * @param crossoverOperator Crossover operator
     */
    public void setCrossoverOperator(CrossoverOperator<Variable<Integer>> crossoverOperator) {
        this.crossoverOperator = crossoverOperator;
    }

    public void setSelectionOperator(SelectionOperator<Variable<Integer>> selectionOperator) {
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
}
