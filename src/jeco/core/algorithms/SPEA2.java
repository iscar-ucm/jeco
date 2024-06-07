/*
* File: SPEA2.java
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
import java.util.HashSet;
import java.util.logging.Logger;

import jeco.core.benchmarks.zdt.ZDT1;
import jeco.core.operator.comparator.ArrayDominance;
import jeco.core.operator.comparator.PropertyComparator;
import jeco.core.operator.comparator.SolutionDominance;
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

/**
 * SPEA2 algorithm
 * 
 */
public class SPEA2<T extends Variable<?>> extends Algorithm<T> {
    private static final Logger logger = Logger.getLogger(SPEA2.class.getName());
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
    protected Comparator<Solution<T>> dominance;
    /**
     * Current generation
     */
    protected int currentGeneration;
    /**
     * Population
     */
    protected Solutions<T> population;
    /**
     * External archive
     */
    protected Solutions<T> archive;
    /**
     * Mutation operator
     */
    protected MutationOperator<T> mutationOperator;
    /**
     * Crossover operator
     */
    protected CrossoverOperator<T> crossoverOperator;
    /**
     * Selection operator
     */
    protected SelectionOperator<T> selectionOperator;
    /**
     * K/sigma value
     */
    protected int K;

    /**
     * Constructor
     * @param problem Problem to solve
     * @param maxPopulationSize Maximum population size
     * @param maxGenerations Maximum number of generations
     * @param mutationOperator Mutation operator
     * @param crossoverOperator Crossover operator
     * @param selectionOperator Selection operator
     */
    public SPEA2(Problem<T> problem, int maxPopulationSize, int maxGenerations, MutationOperator<T> mutationOperator, CrossoverOperator<T> crossoverOperator, SelectionOperator<T> selectionOperator) {
        super(problem);
        this.maxPopulationSize = maxPopulationSize;
        this.maxGenerations = maxGenerations;
        this.mutationOperator = mutationOperator;
        this.crossoverOperator = crossoverOperator;
        this.selectionOperator = selectionOperator;
    }

    @Override
    public void initialize(Solutions<T> initialSolutions) {
        if (initialSolutions == null) {
            population = problem.newRandomSetOfSolutions(maxPopulationSize);
        } else {
            population = initialSolutions;
        }
        dominance = new SolutionDominance<T>();
        K = (int) Math.sqrt(maxPopulationSize + maxPopulationSize);
        //Initialize the variables
        archive = new Solutions<T>();
        problem.evaluate(population);
        currentGeneration = 0;
    }

    @Override
    public Solutions<T> execute() {
        while (currentGeneration < maxGenerations) {
            step();
        }
        archive.reduceToNonDominated(dominance);
        return archive;
    } // execute

    @Override
    public void step() {
        currentGeneration++;

        Solutions<T> union = new Solutions<T>();
        union.addAll(population);
        union.addAll(archive);
        assignFitness(union);
        Solutions<T> unionReduced = reduceByFitness(union);
        if (unionReduced.size() < maxPopulationSize) // External archive size
        {
            expand(unionReduced, union, maxPopulationSize - unionReduced.size());
        } else if (unionReduced.size() > maxPopulationSize) {
            unionReduced = reduce(unionReduced, maxPopulationSize);
        }

        archive = unionReduced;
        if (currentGeneration == maxGenerations) {
            return;
        }

        // Create a new offspringPopulation
        Solutions<T> offSpringSolutionSet = new Solutions<T>();
        Solution<T> parent1, parent2;
        while (offSpringSolutionSet.size() < maxPopulationSize) {
            parent1 = selectionOperator.execute(archive).get(0);
            parent2 = selectionOperator.execute(archive).get(0);
            //make the crossover
            Solutions<T> offSpring = crossoverOperator.execute(parent1, parent2);
            // We only add one of the two children:
            for (Solution<T> solution : offSpring) {
                mutationOperator.execute(solution);
                offSpringSolutionSet.add(solution);
            }
        } // while
        problem.evaluate(offSpringSolutionSet);
        // End Create a offSpring solutionSet
        population = offSpringSolutionSet;
    }

    /**
     * Assigns the fitness to the solutions
     * @param solutions Solutions to assign the fitness
     */
    public void assignFitness(Solutions<T> solutions) {
        int i, j, popSize = solutions.size();
        int strength[] = new int[popSize];
        int raw[] = new int[popSize];
        double density[] = new double[popSize];
        double sigma;
        Solution<T> solI;
        int compare;
        double fitness;

        for (i = 0; i < popSize; ++i) {
            strength[i] = 0;
            raw[i] = 0;
            density[i] = 0;
        }

        // Assigns strength
        for (i = 0; i < popSize; ++i) {
            solI = solutions.get(i);
            for (j = 0; j < popSize; ++j) {
                if (i == j) {
                    continue;
                }
                compare = dominance.compare(solI, solutions.get(j));
                if (compare < 0) {
                    strength[i]++;
                }
            }
        }

        // Assigns raw fitness
        for (i = 0; i < popSize; ++i) {
            solI = solutions.get(i);
            for (j = 0; j < popSize; ++j) {
                if (i == j) {
                    continue;
                }
                compare = dominance.compare(solI, solutions.get(j));
                if (compare == 1 || compare == 2) {
                    raw[i] += strength[j];
                }
            }
        }

        // Assigns density
        for (i = 0; i < popSize; ++i) {
            sigma = computeSigma(i, solutions);
            density[i] = 1 / (sigma + 2);
            fitness = raw[i] + density[i];
            solutions.get(i).getProperties().put("fitness", fitness);
        }
    }

    /**
     * Computes the sigma value for a solution
     * @param i Solution index
     * @param solutions Solutions
     * @return Sigma value
     */
    private double computeSigma(int i, Solutions<T> solutions) {
        return computeSigmas(i, solutions).get(K);
    }

    /**
     * Computes the euclidean distance between two solutions
     * @param sol1 Solution 1
     * @param sol2 Solution 2
     * @return Euclidean distance
     */
    public double euclideanDistance(Solution<T> sol1, Solution<T> sol2) {
        int nObjs = Math.min(sol1.getObjectives().size(), sol2.getObjectives().size());

        double sum = 0;
        for (int i = 0; i < nObjs; i++) {
            sum += ((sol1.getObjectives().get(i) - sol2.getObjectives().get(i)) * (sol1.getObjectives().get(i) - sol2.getObjectives().get(i)));
        }
        return Math.sqrt(sum);
    }

    /**
     * Computes the sigmas for a solution
     * @param i Solution index
     * @param solutions Solutions
     * @return Sigmas
     */
    private ArrayList<Double> computeSigmas(int i, Solutions<T> solutions) {
        int popSize = solutions.size();
        int j;
        double distance;
        ArrayList<Double> distancesToI = new ArrayList<Double>();
        Solution<T> solI = solutions.get(i);
        for (j = 0; j < popSize; j++) {
            distance = euclideanDistance(solI, solutions.get(j));
            distancesToI.add(distance);
        }
        Collections.sort(distancesToI);
        return distancesToI;
    }

    /**
     * Reduces the population by fitness
     * @param pop Population
     * @return Reduced population
     */
    public Solutions<T> reduceByFitness(Solutions<T> pop) {
        Solutions<T> result = new Solutions<T>();
        Solution<T> indI;
        for (int i = 0; i < pop.size(); ++i) {
            indI = pop.get(i);
            if (indI.getProperties().get("fitness").doubleValue() < 1) {
                result.add(indI);
            }
        }
        return result;
    }

    /**
     * Expands the population
     * @param pop Population
     * @param all All solutions
     * @param nElems Number of elements to expand
     */
    public void expand(Solutions<T> pop, Solutions<T> all, int nElems) {
        int i = 0, count = 0, allSize = all.size();
        Solution<T> indI;
        Collections.sort(all, new PropertyComparator<T>("fitness"));
        for (i = 0; i < allSize; ++i) {
            indI = all.get(i);
            if (indI.getProperties().get("fitness").doubleValue() >= 1) {
                pop.add(indI);
                count++;
                if (count == nElems) {
                    break;
                }
            }
        }
    }

    /**
     * Reduces the population to a maximum size
     * @param pop Population
     * @param maxSize Maximum size
     * @return Reduced population
     */
    public Solutions<T> reduce(Solutions<T> pop, int maxSize) {
        int i, min;
        ArrayList<ArrayList<Double>> allSigmas = new ArrayList<ArrayList<Double>>();
        HashSet<Integer> erased = new HashSet<Integer>();
        int toErase = pop.size() - maxSize;
        ArrayDominance comparator = new ArrayDominance();

        for (i = 0; i < pop.size(); i++) {
            allSigmas.add(computeSigmas(i, pop));
        }

        while (erased.size() < toErase) {
            min = 0;
            while (erased.contains(min)) {
                min++;
            }
            for (i = 0; i < pop.size(); i++) {
                if (i == min || erased.contains(i)) {
                    continue;
                }
                if (comparator.compare(allSigmas.get(i), allSigmas.get(min)) == -1) {
                    min = i;
                }
            }
            erased.add(min);
        }

        Solutions<T> result = new Solutions<T>();
        for (i = 0; i < pop.size(); i++) {
            if (!erased.contains(i)) {
                result.add(pop.get(i));
            }
        }

        return result;
    }

    /**
     * Sets the mutation operator
     * @param mutationOperator Mutation operator
     */
    public void setMutationOperator(MutationOperator<T> mutationOperator) {
        this.mutationOperator = mutationOperator;
    }

    /**
     * Sets the crossover operator
     * @param crossoverOperator Crossover operator
     */
    public void setCrossoverOperator(CrossoverOperator<T> crossoverOperator) {
        this.crossoverOperator = crossoverOperator;
    }

    /**
     * Sets the selection operator
     * @param selectionOperator Selection operator
     */
    public void setSelectionOperator(SelectionOperator<T> selectionOperator) {
        this.selectionOperator = selectionOperator;
    }

    /**
     * Sets the maximum number of generations
     * @param maxGenerations Maximum number of generations
     */
    public void setMaxGenerations(int maxGenerations) {
        this.maxGenerations = maxGenerations;
    }

    /**
     * Sets the maximum population size
     * @param maxPopulationSize Maximum population size
     */
    public void setMaxPopulationSize(int maxPopulationSize) {
        this.maxPopulationSize = maxPopulationSize;
    }

    public static void main(String[] args) {
		// First create the problem
		ZDT1 zdt1 = new ZDT1(30);
		// Second create the algorithm
		SPEA2<Variable<Double>> spea2 = new SPEA2<Variable<Double>>(zdt1, 100, 250, new PolynomialMutation<Variable<Double>>(zdt1), new SBXCrossover<Variable<Double>>(zdt1), new BinaryTournament<Variable<Double>>());
		Solutions<Variable<Double>> solutions = spea2.execute();
		logger.info("solutions.size()="+ solutions.size());
		System.out.println(solutions.toString());
	}
} // Spea2

