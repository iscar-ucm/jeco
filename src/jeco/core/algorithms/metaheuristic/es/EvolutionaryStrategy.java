/*
 * Copyright (C) 2010-2016 José Luis Risco Martín <jlrisco@ucm.es>
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
 *
 * Contributors:
 *  - José Luis Risco Martín
 */
package jeco.core.algorithms.metaheuristic.es;

import java.util.Collections;
import java.util.logging.Logger;

import jeco.core.algorithms.Algorithm;
import jeco.core.operator.comparator.SimpleDominance;
import jeco.core.operator.crossover.SinglePointCrossover;
import jeco.core.operator.mutation.MutationOperator;
import jeco.core.operator.selection.BinaryTournament;
import jeco.core.operator.selection.SelectionOperator;
import jeco.core.problem.Problem;
import jeco.core.problem.Solution;
import jeco.core.problem.Solutions;
import jeco.core.problem.Variable;

public class EvolutionaryStrategy<V extends Variable<?>> extends Algorithm<V> {

    private static final Logger logger = Logger.getLogger(EvolutionaryStrategy.class.getName());

    public static final int SELECTION_COMMA = 0;
    public static final int SELECTION_PLUS = 1;
    public static final int SELECTION_DEFAULT = SELECTION_PLUS;

    public static final int RHO_DEFAULT = 1;

    /////////////////////////////////////////////////////////////////////////
    protected boolean stopWhenSolved;
    protected int maxGenerations;
    protected int mu;
    protected int selectionType;
    protected int rho;
    protected int lambda;
    /////////////////////////////////////////////////////////////////////////
    protected SimpleDominance<V> dominance = new SimpleDominance<V>();
    protected int currentGeneration;
    protected Solutions<V> muPopulation;
    protected MutationOperator<V> mutationOperator;
    protected SinglePointCrossover<V> crossoverOperator;
    protected SelectionOperator<V> selectionOperator;

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
                logger.info(percentage + "% performed ...");
                nextPercentageReport += 10;
            }
            if (stopWhenSolved) {
                Double bestObj = muPopulation.get(0).getObjectives().get(0);
                if (bestObj <= 0) {
                    logger.info("Optimal solution found in " + currentGeneration + " generations.");
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

}
