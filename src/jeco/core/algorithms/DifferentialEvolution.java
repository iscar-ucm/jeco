/*
* File: DifferentialEvolution.java
* Author: José M. Colmenar <josemanuel.colmenar@urjc.es>
* Created: 2010/05/08 (YYYY/MM/DD)
* Last modified: 2024/05/06 (YYYY/MM/DD) - José Luis Risco Martín <jlrisco@ucm.es>
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
import java.util.HashSet;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import jeco.core.benchmarks.Rastringin;
import jeco.core.operator.comparator.SimpleDominance;
import jeco.core.problem.Problem;
import jeco.core.problem.Solution;
import jeco.core.problem.Solutions;
import jeco.core.problem.Variable;
import jeco.core.util.logger.JecoLogger;

/**
 * Class implementing the differential evolution technique for problem solving.
 *
 * Problem manages double variables
 * 
 * @author J. M. Colmenar
 */
public class DifferentialEvolution extends Algorithm<Variable<Double>> {

    private static final Logger LOGGER = Logger.getLogger(SimpleGeneticAlgorithm.class.getName());
    private static Random rnd;

    /////////////////////////////////////////////////////////////////////////
    /**
     * If true, the values of the variables are limited to the bounds of the
     * problem.
     */
    public Boolean ceilAndFloor = false;

    /**
     * If true, the algorithm stops when the optimal solution is found.
     */
    protected Boolean stopWhenSolved = null;
    /**
     * Maximum number of generations.
     */
    protected Integer maxGenerations = null;
    /**
     * Current generation.
     */
    protected Integer currentGeneration = null;
    /**
     * Population of solutions.
     */
    protected Solutions<Variable<Double>> population;
    /////////////////////////////////////////////////////////////////////////

    /**
     * Parent number.
     */
    protected Integer np = null;            // Parent number (poblacion)
    /**
     * Mutation factor.
     */
    protected double f = 0;                 // Mutation factor
    /**
     * Recombination factor.
     */
    protected double gr = 0;                 // Recombination factor

    /////////////////////////////////////////////////////////////////////////
    /**
     * Dominance comparator.
     */
    protected SimpleDominance<Variable<Double>> dominance = new SimpleDominance<>();
    /////////////////////////////////////////////////////////////////////////
    /**
     * Already chosen indexes.
     */
    protected HashSet<Integer> alreadyChosen = new HashSet<>();

    /**
     * Class constructor.
     *
     * @param problem Problem to solve.
     * @param maxPopulationSize Population size.
     * @param maxGenerations Maximum number of generations.
     * @param stopWhenSolved Stop when the optimal solution is found.
     * @param mutationFactor Mutation factor.
     * @param recombinationFactor Recombination factor.
     */
    public DifferentialEvolution(Problem<Variable<Double>> problem, Integer maxPopulationSize, Integer maxGenerations, Boolean stopWhenSolved,
            Double mutationFactor, Double recombinationFactor) {
        super(problem);

        this.maxGenerations = maxGenerations;
        this.np = maxPopulationSize;
        this.stopWhenSolved = stopWhenSolved;

        this.f = mutationFactor;
        this.gr = recombinationFactor;

        rnd = new Random();

        if (np < 4) {
            LOGGER.severe("Differential Evolution requieres at least 4 individuals !!");
            System.exit(-1);
        }

        if ((f < 0) || (f > 2)) {
            LOGGER.severe("Differential Evolution requieres mutation factor within range [0,2]");
            System.exit(-1);
        }

        if ((gr < 0) || (gr > 1)) {
            LOGGER.severe("Differential Evolution requieres recombination factor within range [0,1]");
            System.exit(-1);
        }
    }

    @Override
    public void initialize(Solutions<Variable<Double>> initialSolutions) {
        if (initialSolutions == null) {
            population = problem.newRandomSetOfSolutions(np);
        }
        else {
            population = initialSolutions;
        }
        problem.evaluate(population);
        Collections.sort(population, dominance);
        currentGeneration = 0;
    }

    /**
     * Returns a target vector from the population selected by random not
     * selected before, and different to i.
     *
     * @param i index of the target vector
     * @return target vector
     */
    protected Solution<Variable<Double>> targetVector(int i) {

        int k = 0;

        do {
            k = rnd.nextInt(np);
        } while ((k == i) || (alreadyChosen.contains(k)));

        alreadyChosen.add(k);

        return population.get(k);

    }

    @Override
    public void step() {
        currentGeneration++;

        // Mutation phase ******************************************************
        // Create the muted population
        Solutions<Variable<Double>> noisyVectors = new Solutions<>();

        for (int i = 0; i < np; i++) {
            // Target vectors are selected:
            Solution<Variable<Double>> xa = targetVector(i);
            Solution<Variable<Double>> xb = targetVector(i);
            Solution<Variable<Double>> xc = targetVector(i);

            Solution<Variable<Double>> v = population.get(i);
            // This clone is done just to have a new solution object. Content will be overwritten.
            Solution<Variable<Double>> noisyVector = v.clone();

            for (int j = 0; j < v.getVariables().size(); j++) {
                double value = xc.getVariable(j).getValue() + (f * (xa.getVariable(j).getValue() - xb.getVariable(j).getValue()));
                if (ceilAndFloor) {
                    // Ceil and floor for mutated values
                    if (value < problem.getLowerBound(j)) {
                        value = problem.getLowerBound(j);
                    } else if (value > problem.getUpperBound(j)) {
                        value = problem.getUpperBound(j);
                    }
                }
                noisyVector.getVariable(j).setValue(value);
            }

            noisyVectors.add(noisyVector);

            // Clean selected targets:
            alreadyChosen.clear();
        }

        // Recombination phase ******************************************************
        // Create the recombined population
        Solutions<Variable<Double>> trialVectors = new Solutions<>();

        for (int i = 0; i < np; i++) {

            Solution<Variable<Double>> t = population.get(i).clone();

            for (int j = 0; j < t.getVariables().size(); j++) {
                if (rnd.nextDouble() < gr) {
                    t.getVariable(j).setValue(noisyVectors.get(i).getVariable(j).getValue());
                }
            }

            trialVectors.add(t);

        }

        // Selection phase ******************************************************
        // Current population is already evaluated. Not trial vectors:
        problem.evaluate(trialVectors);

        Solutions<Variable<Double>> newPopulation = new Solutions<>();

        for (int i = 0; i < np; i++) {

            // Minimizing !!
            if (trialVectors.get(i).getObjective(0) < population.get(i).getObjective(0)) {
                newPopulation.add(trialVectors.get(i));
            } else {
                newPopulation.add(population.get(i));
            }

        }

        // Reorder:
        population = newPopulation;
        Collections.sort(population, dominance);
    }

    @Override
    public Solutions<Variable<Double>> execute() {
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
        JecoLogger.setup(Level.INFO);
        // First create the problem
        Rastringin problem = new Rastringin(4);
        // Second create the algorithm:
        /*
         * DifferentialEvolution(Problem<Variable<Double>> problem, Integer
         * maxPopulationSize, Integer maxGenerations, Boolean stopWhenSolved,
         * Double mutationFactor, Double recombinationFactor)
         */
        DifferentialEvolution algorithm = new DifferentialEvolution(problem, 20, 250, true, 1.0, 0.5);
        algorithm.initialize();
        Solutions<Variable<Double>> solutions = algorithm.execute();
        for (Solution<Variable<Double>> solution : solutions) {
            LOGGER.info("Fitness = " + solution.getObjectives().get(0));
        }
    }
}
