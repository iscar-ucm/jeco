/*
* File: TabuSearch.java
* Author: José Luis Risco Martín <jlrisco@ucm.es>
* Created: 2024/05/10 (YYYY/MM/DD)
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

import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

import jeco.core.benchmarks.Rastringin;
import jeco.core.operator.comparator.ObjectiveComparator;
import jeco.core.operator.generator.DefaultNeighborGenerator;
import jeco.core.operator.generator.NeighborGenerator;
import jeco.core.problem.Problem;
import jeco.core.problem.Solution;
import jeco.core.problem.Solutions;
import jeco.core.problem.Variable;
import jeco.core.util.logger.JecoLogger;

/**
 * Tabu search algorithm.
 * 
 * @param <V> Type of the variables of the problem.
 */
public class TabuSearch<V extends Variable<?>> extends Algorithm<V> {
    private static final Logger LOGGER = Logger.getLogger(TabuSearch.class.getName());

    /**
     * Current solution.
     */
    protected Solution<V> currentSolution;
    /**
     * Best solution found.
     */
    protected Solution<V> bestSolution;
    /**
     * Maximum number of iterations.
     */
    protected Integer maxIterations;
    /**
     * Size of the tabu list.
     */
    protected Integer tabuSize;
    /**
     * Stop when the optimal solution is found.
     */
    protected Boolean stopWhenSolved;
    /**
     * Current iteration.
     */
    protected Integer currentIteration;
    /**
     * Tabu list.
     */
    protected Solutions<V> tabuList = new Solutions<>();
    /**
     * Comparator for the objectives.
     */
    protected ObjectiveComparator<V> comparator = new ObjectiveComparator<>(0);
    /**
     * Neighbor generator.
     */
    protected NeighborGenerator<V> neighborGenerator;

    /**
     * Constructor.
     * 
     * @param problem Problem to solve.
     * @param maxIterations Maximum number of iterations.
     * @param tabuSize Size of the tabu list.
     * @param stopWhenSolved Stop when the optimal solution is found.
     * @param neighborGenerator Neighbor generator.
     */
    public TabuSearch(Problem<V> problem, Integer maxIterations, Integer tabuSize, Boolean stopWhenSolved, NeighborGenerator<V> neighborGenerator) {
        super(problem);
        this.maxIterations = maxIterations;
        this.tabuSize = tabuSize;
        this.stopWhenSolved = stopWhenSolved;
        this.neighborGenerator = neighborGenerator;
    }

    @Override
    public void initialize(Solutions<V> initialSolutions) {
        if(initialSolutions == null) {
            currentSolution = problem.newRandomSetOfSolutions(1).get(0);
        } else {
            currentSolution = initialSolutions.get(0);
        }
        problem.evaluate(currentSolution);
        bestSolution = currentSolution.clone();
        currentIteration = 0;
    }

    @Override
    public void step() {
        Solutions<V> neighbors = neighborGenerator.execute(currentSolution);
        problem.evaluate(neighbors);
        // Let's sort the neighbors by fitness
        Collections.sort(neighbors, comparator);
        Solution<V> bestNeighbor = null;
        // Let's check if the best neighbor is in the tabu list
        while(bestNeighbor == null && !neighbors.isEmpty()) {
            bestNeighbor = neighbors.remove(0);
            if(tabuList.contains(bestNeighbor)) {
                bestNeighbor = null;
            }
        }
        if (bestNeighbor != null) {
            if (bestNeighbor.getObjectives().get(0) < bestSolution.getObjectives().get(0)) {
                bestSolution = bestNeighbor.clone();
            }
            currentSolution = bestNeighbor.clone();
            tabuList.add(0, bestNeighbor); // Add the best neighbor to the tabu list
            if(tabuList.size() > tabuSize) {
                tabuList.remove(tabuList.size() - 1);
            }
        }
        currentIteration++;
    }

    @Override
    public Solutions<V> execute() {
        int percentageReport = 10;
        while(currentIteration < maxIterations) {
            step();
            int percentage = Math.round((currentIteration * 100) / maxIterations);
            Double bestObj = bestSolution.getObjectives().get(0);
            if(percentage == percentageReport) {
                LOGGER.info(percentage + "% performed ..." + " -- Best fitness: " + bestObj);
                percentageReport += 10;
            }
            if(stopWhenSolved) {
                if(bestObj <= 0) {
                    LOGGER.info("Optimal solution found in " + currentIteration + " iterations.");
                    break;
                }
            }
        }
        Solutions<V> solutions = new Solutions<>();
        solutions.add(bestSolution);
        return solutions;
    }

    public static void main(String[] args) {
		JecoLogger.setup(Level.FINE);
		// First create the problem
		Rastringin problem = new Rastringin(4);
		// Second create the algorithm
		NeighborGenerator<Variable<Double>> neighborGen = new DefaultNeighborGenerator<>(problem, 20);
		TabuSearch<Variable<Double>> ts = new TabuSearch<>(problem, 1000, 100, true, neighborGen);
		ts.initialize();
		Solutions<Variable<Double>> solutions = ts.execute();
		Solution<Variable<Double>> bestSolution = solutions.get(0);
		System.out.println("Fitness = " + bestSolution.getObjectives().get(0));
		//System.out.println("solutions.size()="+ solutions.size());
		//System.out.println(solutions.toString());
		//System.out.println("solutions.size()="+ solutions.size());
	}
}
