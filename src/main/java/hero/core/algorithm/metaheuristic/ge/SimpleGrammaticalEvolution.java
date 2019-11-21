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
 *  - J. M. Colmenar
 *  - José Luis Risco Martín
 */
package hero.core.algorithm.metaheuristic.ge;

import hero.core.algorithm.Algorithm;
import hero.core.algorithm.metaheuristic.ga.SimpleGeneticAlgorithm;
import hero.core.operator.comparator.SimpleDominance;
import hero.core.operator.crossover.SinglePointCrossover;
import hero.core.operator.mutation.IntegerFlipMutation;
import hero.core.operator.selection.BinaryTournament;
import hero.core.problem.Problem;
import hero.core.problem.Solutions;
import hero.core.problem.Variable;

/**
 * Grammatical evolution using just one objective.
 *
 * @author J. M. Colmenar
 */
public class SimpleGrammaticalEvolution extends Algorithm<Variable<Integer>> {

    SimpleGeneticAlgorithm<Variable<Integer>> algorithm;

    public SimpleGrammaticalEvolution(Problem<Variable<Integer>> problem, int maxPopulationSize, int maxGenerations, double probMutation, double probCrossover) {
        super(problem);

        // Algorithm operators
        IntegerFlipMutation<Variable<Integer>> mutationOperator = new IntegerFlipMutation<>(problem, probMutation);
        SinglePointCrossover<Variable<Integer>> crossoverOperator = new SinglePointCrossover<>(problem, SinglePointCrossover.DEFAULT_FIXED_CROSSOVER_POINT, probCrossover, SinglePointCrossover.ALLOW_REPETITION);
        SimpleDominance<Variable<Integer>> comparator = new SimpleDominance<>();
        BinaryTournament<Variable<Integer>> selectionOp = new BinaryTournament<>(comparator);

        algorithm = new SimpleGeneticAlgorithm<>(problem,
                maxPopulationSize, maxGenerations, true, mutationOperator, crossoverOperator, selectionOp);

    }

    @Override
    public void initialize(Solutions<Variable<Integer>> initialSolutions) {
        algorithm.initialize(initialSolutions);
    }

    @Override
    public void initialize() {
        algorithm.initialize();
    }

    @Override
    public void step() {
        algorithm.step();
    }

    @Override
    public Solutions<Variable<Integer>> execute() {
        return algorithm.execute();
    }

}
