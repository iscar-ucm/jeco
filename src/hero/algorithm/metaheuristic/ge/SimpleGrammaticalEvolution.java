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
package hero.algorithm.metaheuristic.ge;

import hero.algorithm.Algorithm;
import hero.algorithm.metaheuristic.ga.SimpleGeneticAlgorithm;
import hero.operator.comparator.SimpleDominance;
import hero.operator.crossover.SinglePointCrossover;
import hero.operator.mutation.IntegerFlipMutation;
import hero.operator.selection.BinaryTournament;
import hero.problem.Problem;
import hero.problem.Solutions;
import hero.problem.Variable;

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
        IntegerFlipMutation<Variable<Integer>> mutationOperator = new IntegerFlipMutation<Variable<Integer>>(problem, probMutation);
        SinglePointCrossover<Variable<Integer>> crossoverOperator = new SinglePointCrossover<Variable<Integer>>(problem, SinglePointCrossover.DEFAULT_FIXED_CROSSOVER_POINT, probCrossover, SinglePointCrossover.ALLOW_REPETITION);
        SimpleDominance<Variable<Integer>> comparator = new SimpleDominance<Variable<Integer>>();
        BinaryTournament<Variable<Integer>> selectionOp = new BinaryTournament<Variable<Integer>>(comparator);
        
        algorithm = new SimpleGeneticAlgorithm<Variable<Integer>>(problem, 
                maxPopulationSize, maxGenerations, true, mutationOperator, crossoverOperator, selectionOp);
        
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
