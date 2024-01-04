/*
* Copyright (C) 2024 José Luis Risco Martín <jlrisco@ucm.es>
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

package eco.core.algorithm.metaheuristic.ts;

import java.util.Collections;
import java.util.logging.Logger;

import eco.core.algorithm.Algorithm;
import eco.core.operator.comparator.ObjectiveComparator;
import eco.core.operator.generator.NeighborGenerator;
import eco.core.problem.Problem;
import eco.core.problem.Solution;
import eco.core.problem.Solutions;
import eco.core.problem.Variable;

public class TabuSearch<V extends Variable<?>> extends Algorithm<V> {
    private static final Logger LOGGER = Logger.getLogger(TabuSearch.class.getName());

    protected Solution<V> currentSolution;
    protected Solution<V> bestSolution;
    protected Integer maxIterations;
    protected Integer tabuSize;
    protected Boolean stopWhenSolved;

    protected Integer currentIteration;
    protected Solutions<V> tabuList = new Solutions<>();

    protected ObjectiveComparator<V> comparator = new ObjectiveComparator<>(0);
    protected NeighborGenerator<V> neighborGenerator;


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
}
