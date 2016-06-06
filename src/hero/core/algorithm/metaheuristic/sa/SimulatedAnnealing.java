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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import hero.core.algorithm.Algorithm;
import hero.core.operator.comparator.SimpleDominance;
import hero.core.problem.Problem;
import hero.core.problem.Solution;
import hero.core.problem.Solutions;
import hero.core.problem.Variable;
import hero.core.util.random.RandomGenerator;

/**
 * Class implementing the simulated annealing technique for problem solving.
 *
 * Works only for one objective.
 *
 * Does not require temperature to be given because it automatically adapts the
 * parameters: Natural Optimization [de Vicente et al., 2000]
 *
 * @author J. M. Colmenar
 */
public class SimulatedAnnealing<T extends Variable<?>> extends Algorithm<T> {

    private static final Logger LOGGER = Logger.getLogger(SimulatedAnnealing.class.getName());

    ///////////////////////////////////////////////////////////////////////////
    protected long maxIterations = 10000;
    protected long currentMoves = 0;

    private boolean change;
    private long numChanges = 0;
    private final int LOG_RATIO = 1000;

    /* Cost-related attributes */
    private double currentMinimumCost = Double.MAX_VALUE;
    private double initialCost = Double.MAX_VALUE;
    private double k = 1.0;
    ///////////////////////////////////////////////////////////////////////////
    protected SimpleDominance<T> dominance = new SimpleDominance<>();
    private Solutions<T> population;
    protected Solutions<T> leaders;

    /**
     * Parameterized constructor
     *
     * @param maxIterations number of iterations where the search will stop.
     * @param k is the weight of the temperature
     */
    public SimulatedAnnealing(Problem<T> problem, Long maxIterations, double k) {
        super(problem);
        this.maxIterations = maxIterations;
        this.k = k;
    }

    /**
     * This constructor allows to establish the maximum number of iterations.
     *
     * @param problem
     * @param maxIterations number of iterations where the search will stop.
     */
    public SimulatedAnnealing(Problem<T> problem, Long maxIterations) {
        this(problem, maxIterations, 1.0);
    }

    @Override
    public void initialize() {
        // Start from a random solution
        population = this.problem.newRandomSetOfSolutions(1);
        leaders = new Solutions<>();
        problem.evaluate(population);
        leaders.add(population.get(0).clone());

        numChanges = 0;
        change = false;

        // Logging SA parameters
        StringBuilder logStr = new StringBuilder();
        logStr.append("\n# SA Parameters:");
        logStr.append("\n-> K (weight for temperature): ").append(k);
        logStr.append("\n-> Max. iterations: ").append(maxIterations);
        logStr.append("\n");
        LOGGER.info(logStr.toString());
        initialCost = population.get(0).getObjective(0);
    }

    @Override
    public void step() {
        Solution<T> currentSolution = population.get(0);
        currentMinimumCost = currentSolution.getObjective(0);

        // Obtain a neighbour (next state)
        Solution<T> newSolution = neighbourSolution(currentSolution);
        problem.evaluate(newSolution);
        currentMoves++;
        /* Compute neighbour's (state) energy and check if move to
         the neighbour (state) */
 /* If new solution is has best objetive value, change */
        if (dominance.compare(newSolution, currentSolution) < 0) {
            change = true;
        } else // If new solution is worse, change depends on probability
        {
            if (changeState(currentSolution, newSolution)) {
                change = true;
            }
        }

        if (change) {
            numChanges++;
            population.set(0, newSolution);
            // Logs detail only if solution changes and following the ratio
            if ((numChanges % LOG_RATIO) == 0) {
                // Screen and also backups solution to XML file
                String logStr = "\n# SA -- Iterations: " + currentMoves + " -- Current SA Temperature: " + Double.toString(getTemperature()) + "\n";
                logStr += "-- Current Best Solution: " + currentSolution + "\n";
                logStr += "Time: " + getCurrentTimeInSeconds() + " seconds.\n";
                LOGGER.log(Level.INFO, logStr);
            }
            change = false;
        }

    }

    @Override
    public Solutions<T> execute() {
        boolean stopSA = false;

        while (!stopSA) {
            step();

            if (maxSeconds == 0) {
                stopSA = (currentMoves == maxIterations);
            } else {
                stopSA = getCurrentTimeInSeconds() >= maxSeconds;
            }

        }

        logObjectives();
        String logStr = "\n# TOTAL SA -- Iterations: " + currentMoves + " -- Current SA Temperature: " + Double.toString(getTemperature()) + "\n";
        logStr += "TOTAL Time: " + getCurrentTimeInSeconds() + " seconds.\n";
        LOGGER.log(Level.INFO, logStr);

        return population;

    }

    /**
     * Computes probability of changing to new solution. It considers ONLY one
     * objective for energy.
     *
     * @param currentSolution current state
     * @param newSolution possible next state
     * @return true if probability gives chance to change state, false otherwise
     */
    private boolean changeState(Solution<T> currentSolution, Solution<T> newSolution) {

        // Higher cost means new energy to be higher than old energy
        double energyDiff;
        energyDiff = newSolution.getObjective(0) - currentSolution.getObjective(0);

        // Compute probability. Must be between 0 and 1.
        double temp = getTemperature();
        double prob = Math.exp(-energyDiff / temp);

        // nextDouble returns the next pseudorandom, uniformly distributed double value between 0.0 and 1.0
        if (RandomGenerator.nextDouble() <= prob) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Obtains the temperature, which is naturally adapted to evolution of the
     * search.
     */
    private double getTemperature() {
        return k * Math.abs((currentMinimumCost - initialCost) / currentMoves);

    }

    /**
     * Logs time and objective values of the best solution to the current
     * logFile
     */
    private void logObjectives() {
        // File log code
        try {
            // Appending
            BufferedWriter writer = new BufferedWriter(new FileWriter(new File(logFile), true));
            // # Time Objective
            writer.write(getCurrentTimeInSeconds() + " " + population.get(BESTSOL).getObjective(OBJECTIVE) + "\n");
            writer.close();
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Returns current time in seconds since algorithm started.
     *
     * @return
     */
    private Double getCurrentTimeInSeconds() {
        return ((System.currentTimeMillis() - startTime) / 1000.0);
    }

    /**
     * Returns a solution where just one variable was changed:
     *
     * @return
     */
    private Solution<T> neighbourSolution(Solution<T> currentSolution) {
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
