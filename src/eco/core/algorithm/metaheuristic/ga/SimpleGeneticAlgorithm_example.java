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
package eco.core.algorithm.metaheuristic.ga;

import java.util.logging.Level;

import eco.core.benchmarks.Rastringin;
import eco.core.operator.comparator.SimpleDominance;
import eco.core.operator.crossover.SBXCrossover;
import eco.core.operator.mutation.PolynomialMutation;
import eco.core.operator.selection.BinaryTournament;
import eco.core.problem.Solution;
import eco.core.problem.Solutions;
import eco.core.problem.Variable;
import eco.core.util.logger.EcoLogger;

public class SimpleGeneticAlgorithm_example {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		EcoLogger.setup(Level.FINE);
		// First create the problem
		Rastringin problem = new Rastringin(4);
		// Second create the algorithm
		PolynomialMutation<Variable<Double>> mutationOp = new PolynomialMutation<>(problem);
		SBXCrossover<Variable<Double>> crossoverOp = new SBXCrossover<>(problem);
		SimpleDominance<Variable<Double>> comparator = new SimpleDominance<>();
		BinaryTournament<Variable<Double>> selectionOp = new BinaryTournament<>(comparator);
		SimpleGeneticAlgorithm<Variable<Double>> ga = new SimpleGeneticAlgorithm<>(problem, 100, 5000, true, mutationOp, crossoverOp, selectionOp);
		ga.initialize();
		Solutions<Variable<Double>> solutions = ga.execute();
		for(Solution<Variable<Double>> solution : solutions) {
			System.out.println("Fitness = " + solution.getObjectives().get(0));
		}
		//System.out.println("solutions.size()="+ solutions.size());
		//System.out.println(solutions.toString());
		//System.out.println("solutions.size()="+ solutions.size());
	}
}
