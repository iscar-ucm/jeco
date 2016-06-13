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
package hero.core.algorithm.metaheuristic.moga;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.logging.Logger;
import hero.core.algorithm.Algorithm;
import hero.core.operator.assigner.CrowdingDistance;
import hero.core.operator.assigner.FrontsExtractor;
import hero.core.operator.comparator.ComparatorNSGAII;
import hero.core.operator.comparator.SolutionDominance;
import hero.core.operator.crossover.CrossoverOperator;
import hero.core.operator.mutation.MutationOperator;
import hero.core.operator.selection.SelectionOperator;
import hero.core.problem.Problem;
import hero.core.problem.Solution;
import hero.core.problem.Solutions;
import hero.core.problem.Variable;

/**
 *
 *
 * Input parameters: - MAX_GENERATIONS - MAX_POPULATION_SIZE
 *
 * Operators: - CROSSOVER: Crossover operator - MUTATION: Mutation operator -
 * SELECTION: Selection operator
 *
 * @author José L. Risco-Martín
 *
 */
public class NSGAII<V extends Variable<?>> extends Algorithm<V> {

    private static final Logger logger = Logger.getLogger(NSGAII.class.getName());
    /////////////////////////////////////////////////////////////////////////
    protected int maxGenerations;
    protected int maxPopulationSize;
    /////////////////////////////////////////////////////////////////////////
    protected Comparator<Solution<V>> dominance;
    protected int currentGeneration;
    protected Solutions<V> population;

    public Solutions<V> getPopulation() {
        return population;
    }
    protected MutationOperator<V> mutationOperator;
    protected CrossoverOperator<V> crossoverOperator;
    protected SelectionOperator<V> selectionOperator;

    public NSGAII(Problem<V> problem, int maxPopulationSize, int maxGenerations, MutationOperator<V> mutationOperator, CrossoverOperator<V> crossoverOperator, SelectionOperator<V> selectionOperator) {
        super(problem);
        this.maxPopulationSize = maxPopulationSize;
        this.maxGenerations = maxGenerations;
        this.mutationOperator = mutationOperator;
        this.crossoverOperator = crossoverOperator;
        this.selectionOperator = selectionOperator;
    }

    @Override
    public void initialize() {
        dominance = new SolutionDominance<>();
        // Create the initial solutionSet
        population = problem.newRandomSetOfSolutions(maxPopulationSize);
        problem.evaluate(population);
        // Compute crowding distance
        CrowdingDistance<V> assigner = new CrowdingDistance<>(problem.getNumberOfObjectives());
        assigner.execute(population);
        currentGeneration = 0;

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

    public void setMutationOperator(MutationOperator<V> mutationOperator) {
        this.mutationOperator = mutationOperator;
    }

    public void setCrossoverOperator(CrossoverOperator<V> crossoverOperator) {
        this.crossoverOperator = crossoverOperator;
    }

    public void setSelectionOperator(SelectionOperator<V> selectionOperator) {
        this.selectionOperator = selectionOperator;
    }

    public void setMaxGenerations(int maxGenerations) {
        this.maxGenerations = maxGenerations;
    }

    public void setMaxPopulationSize(int maxPopulationSize) {
        this.maxPopulationSize = maxPopulationSize;
    }
}
