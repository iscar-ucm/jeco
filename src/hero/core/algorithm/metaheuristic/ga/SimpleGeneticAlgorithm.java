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
package hero.core.algorithm.metaheuristic.ga;

import java.util.Collections;
import java.util.logging.Logger;
import hero.core.algorithm.Algorithm;
import hero.core.operator.comparator.SimpleDominance;
import hero.core.operator.crossover.CrossoverOperator;
import hero.core.operator.mutation.MutationOperator;
import hero.core.operator.selection.SelectionOperator;
import hero.core.problem.Problem;
import hero.core.problem.Solution;
import hero.core.problem.Solutions;
import hero.core.problem.Variable;

public class SimpleGeneticAlgorithm<V extends Variable<?>> extends Algorithm<V> {

    private static final Logger LOGGER = Logger.getLogger(SimpleGeneticAlgorithm.class.getName());

    /////////////////////////////////////////////////////////////////////////
    protected Boolean stopWhenSolved = null;
    protected Integer maxGenerations = null;
    protected Integer maxPopulationSize = null;
    protected Integer currentGeneration = null;
    /////////////////////////////////////////////////////////////////////////
    protected SimpleDominance<V> dominance = new SimpleDominance<>();
    protected Solutions<V> population;
    protected Solutions<V> leaders;
    protected MutationOperator<V> mutationOperator;
    protected CrossoverOperator<V> crossoverOperator;
    protected SelectionOperator<V> selectionOperator;

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

    public Solutions<V> getSolutions() {
        return population;
    }

    public Solutions<V> getLeaders() {
        return leaders;
    }
}
