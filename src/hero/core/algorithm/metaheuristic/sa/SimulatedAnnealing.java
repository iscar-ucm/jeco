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
 *  - José Manuel Colmenar Verdugo
 */
package hero.core.algorithm.metaheuristic.sa;

import hero.core.algorithm.Algorithm;
import hero.core.operator.comparator.SimpleDominance;
import hero.core.problem.Problem;
import hero.core.problem.Solution;
import hero.core.problem.Solutions;
import hero.core.problem.Variable;
import hero.core.util.random.RandomGenerator;

import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * Class implementing the simulated annealing technique for problem solving.
 *
 * Works only for one objective.
 *
 * Does not require temperature to be given because it automatically adapts the
 * parameters: Natural Optimization [de Vicente et al., 2000]
 *
 * @author J. M. Colmenar
 * @author José L. Risco Martín
 */
public class SimulatedAnnealing<T extends Variable<?>> extends Algorithm<T> {

    private static final Logger LOGGER = Logger.getLogger(SimulatedAnnealing.class.getName());

    ///////////////////////////////////////////////////////////////////////////
    protected Integer maxIterations = 10000;
    protected Integer currentIteration = 0;

    /* Cost-related attributes */
    private Double currentMinimumCost = Double.MAX_VALUE;
    private Double initialCost = Double.MAX_VALUE;
    private Double k = 1.0;
    ///////////////////////////////////////////////////////////////////////////
    protected Double targetObj = null;
    protected SimpleDominance<T> dominance = new SimpleDominance<>();
    private Solution<T> currentSolution;
    protected Solution<T> bestSolution;

    /**
     * Parameterized constructor
     *
     * @param problem Problem to be solved
     * @param maxIterations number of iterations where the search will stop.
     * @param k is the weight of the temperature (Default = 1.0)
     * @param targetObj If the algorithm reaches this obj, the optimization
     * stops (Default = Double.NEGATIVE_INFINITY)
     */
    public SimulatedAnnealing(Problem<T> problem, Integer maxIterations, Double k, Double targetObj) {
        super(problem);
        this.maxIterations = maxIterations;
        this.k = k;
        this.targetObj = targetObj;
    }

    /**
     * This constructor allows to establish the maximum number of iterations.
     *
     * @param problem Problem to be solved
     * @param maxIterations number of iterations where the search will stop.
     */
    public SimulatedAnnealing(Problem<T> problem, Integer maxIterations) {
        this(problem, maxIterations, 1.0, Double.NEGATIVE_INFINITY);
    }

    public void initialize(Solutions<T> initialSolutions) {
        if (initialSolutions == null) {
            currentSolution = this.problem.newRandomSetOfSolutions(1).get(0);
        } else {
            currentSolution = initialSolutions.get(0);
        }
        problem.evaluate(currentSolution);
        bestSolution = currentSolution.clone();
        initialCost = currentSolution.getObjective(0);
        currentIteration = 0;
    }

    @Override
    public void step() {
        currentIteration++;
        currentMinimumCost = currentSolution.getObjective(0);
        Solution<T> newSolution = newSolution();
        problem.evaluate(newSolution);
        if (dominance.compare(newSolution, bestSolution) < 0) {
            bestSolution = newSolution.clone();
        }
        if (dominance.compare(newSolution, currentSolution) < 0 || changeState(newSolution)) {
            currentSolution = newSolution;
        }
    }

    @Override
    public Solutions<T> execute() {
        int nextPercentageReport = 10;
        while (currentIteration < maxIterations) {
            step();
            int percentage = Math.round((currentIteration * 100) / maxIterations);
            Double bestObj = bestSolution.getObjectives().get(0);
            if (percentage == nextPercentageReport) {
                LOGGER.info(percentage + "% performed ..." + " -- Best fitness: " + bestObj);
                nextPercentageReport += 10;
            }
            if (bestObj <= targetObj) {
                LOGGER.info("Optimal solution found in " + currentIteration + " iterations.");
                break;
            }
        }
        Solutions<T> solutions = problem.newRandomSetOfSolutions(1);
        solutions.set(0, bestSolution);
        return solutions;
    }

    /**
     * Computes probability of changing to new solution. It considers ONLY one
     * objective for energy.
     *
     * @param newSolution possible next state
     * @return true if probability gives chance to change state, false otherwise
     */
    private boolean changeState(Solution<T> newSolution) {
        // Higher cost means new energy to be higher than old energy
        double energyDiff;
        energyDiff = newSolution.getObjective(0) - currentSolution.getObjective(0);

        // Compute probability. Must be between 0 and 1.
        double temp = k * Math.abs((currentMinimumCost - initialCost) / currentIteration);
        double prob = Math.exp(-energyDiff / temp);

        // nextDouble returns the next pseudorandom, uniformly distributed double value between 0.0 and 1.0
        return RandomGenerator.nextDouble() <= prob;
    }

    /**
     * Returns a new solution when just of the variables has changed
     *
     * @return The new solution.
     */
    private Solution<T> newSolution() {
        // Generate a brand new solution
        ArrayList<T> variables = problem.newRandomSetOfSolutions(1).get(0).getVariables();
        // Randomly choose one variable
        int i = RandomGenerator.nextInt(variables.size());
        // Clone current solution and introduce change.
        Solution newSolution = currentSolution.clone();
        newSolution.getVariable(i).setValue(variables.get(i).getValue());
        return newSolution;
    }

}
