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
package hero.algorithm.metaheuristic.moga;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.logging.Logger;
import hero.algorithm.Algorithm;
import hero.operator.assigner.CrowdingDistance;
import hero.operator.assigner.FrontsExtractor;
import hero.operator.comparator.ComparatorNSGAII;
import hero.operator.comparator.SolutionDominance;
import hero.operator.crossover.CrossoverOperator;
import hero.operator.mutation.MutationOperator;
import hero.operator.selection.SelectionOperator;
import hero.problem.Problem;
import hero.problem.Solution;
import hero.problem.Solutions;
import hero.problem.Variable;

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
public class NSGAII<T extends Variable<?>> extends Algorithm<T> {

  private static final Logger logger = Logger.getLogger(NSGAII.class.getName());
  /////////////////////////////////////////////////////////////////////////
  protected int maxGenerations;
  protected int maxPopulationSize;
  /////////////////////////////////////////////////////////////////////////
  protected Comparator<Solution<T>> dominance;
  protected int currentGeneration;
  protected Solutions<T> population;

  public Solutions<T> getPopulation() {
    return population;
  }
  protected MutationOperator<T> mutationOperator;
  protected CrossoverOperator<T> crossoverOperator;
  protected SelectionOperator<T> selectionOperator;

  public NSGAII(Problem<T> problem, int maxPopulationSize, int maxGenerations, MutationOperator<T> mutationOperator, CrossoverOperator<T> crossoverOperator, SelectionOperator<T> selectionOperator) {
    super(problem);
    this.maxPopulationSize = maxPopulationSize;
    this.maxGenerations = maxGenerations;
    this.mutationOperator = mutationOperator;
    this.crossoverOperator = crossoverOperator;
    this.selectionOperator = selectionOperator;
  }

  @Override
  public void initialize() {
    dominance = new SolutionDominance<T>();
    // Create the initial solutionSet
    population = problem.newRandomSetOfSolutions(maxPopulationSize);
    problem.evaluate(population);
    // Compute crowding distance
    CrowdingDistance<T> assigner = new CrowdingDistance<T>(problem.getNumberOfObjectives());
    assigner.execute(population);
    currentGeneration = 0;

  }

  @Override
  public Solutions<T> execute() {
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

  public Solutions<T> getCurrentSolution() {
    population.reduceToNonDominated(dominance);
    return population;
  }

  public void step() {
    currentGeneration++;
    // Create the offSpring solutionSet
    if (population.size() < 2) {
      logger.severe("Generation: " + currentGeneration + ". Population size is less than 2.");
      return;
    }

    Solutions<T> childPop = new Solutions<T>();
    Solution<T> parent1, parent2;
    for (int i = 0; i < (maxPopulationSize / 2); i++) {
      //obtain parents
      parent1 = selectionOperator.execute(population).get(0);
      parent2 = selectionOperator.execute(population).get(0);
      Solutions<T> offSpring = crossoverOperator.execute(parent1, parent2);
      for (Solution<T> solution : offSpring) {
        mutationOperator.execute(solution);
        childPop.add(solution);
      }
    } // for
    problem.evaluate(childPop);

    // Create the solutionSet union of solutionSet and offSpring
    Solutions<T> mixedPop = new Solutions<T>();
    mixedPop.addAll(population);
    mixedPop.addAll(childPop);

    // Reducing the union
    population = reduce(mixedPop, maxPopulationSize);
    logger.fine("Generation " + currentGeneration + "/" + maxGenerations + "\n" + population.toString());
  } // step

  public Solutions<T> reduce(Solutions<T> pop, int maxSize) {
    FrontsExtractor<T> extractor = new FrontsExtractor<T>(dominance);
    ArrayList<Solutions<T>> fronts = extractor.execute(pop);

    Solutions<T> reducedPop = new Solutions<T>();
    CrowdingDistance<T> assigner = new CrowdingDistance<T>(problem.getNumberOfObjectives());
    Solutions<T> front;
    int i = 0;
    while (reducedPop.size() < maxSize && i < fronts.size()) {
      front = fronts.get(i);
      assigner.execute(front);
      reducedPop.addAll(front);
      i++;
    }

    ComparatorNSGAII<T> comparator = new ComparatorNSGAII<T>();
    if (reducedPop.size() > maxSize) {
      Collections.sort(reducedPop, comparator);
      while (reducedPop.size() > maxSize) {
        reducedPop.remove(reducedPop.size() - 1);
      }
    }
    return reducedPop;
  }

  public void setMutationOperator(MutationOperator<T> mutationOperator) {
    this.mutationOperator = mutationOperator;
  }

  public void setCrossoverOperator(CrossoverOperator<T> crossoverOperator) {
    this.crossoverOperator = crossoverOperator;
  }

  public void setSelectionOperator(SelectionOperator<T> selectionOperator) {
    this.selectionOperator = selectionOperator;
  }

  public void setMaxGenerations(int maxGenerations) {
    this.maxGenerations = maxGenerations;
  }

  public void setMaxPopulationSize(int maxPopulationSize) {
    this.maxPopulationSize = maxPopulationSize;
  }
}
