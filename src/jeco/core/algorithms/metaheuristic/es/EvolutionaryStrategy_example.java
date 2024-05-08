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
package jeco.core.algorithms.metaheuristic.es;

import java.util.logging.Level;
import java.util.logging.Logger;

import jeco.core.benchmarks.Rastringin;
import jeco.core.operator.mutation.PolynomialMutation;
import jeco.core.problem.Solution;
import jeco.core.problem.Solutions;
import jeco.core.problem.Variable;
import jeco.core.util.logger.JecoLogger;

public class EvolutionaryStrategy_example {

	private static final Logger LOGGER = Logger.getLogger(EvolutionaryStrategy_example.class.getName());

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		JecoLogger.setup(Level.INFO);
		// First create the problem
		Rastringin problem = new Rastringin(4);
		// Second create the algorithm
		PolynomialMutation<Variable<Double>> mutationOp = new PolynomialMutation<Variable<Double>>(problem);
		EvolutionaryStrategy<Variable<Double>> algorithm = new EvolutionaryStrategy<Variable<Double>>(problem, mutationOp, 5, 1, EvolutionaryStrategy.SELECTION_PLUS, 10, 250, true);
		algorithm.initialize();
		Solutions<Variable<Double>> solutions = algorithm.execute();
		for (Solution<Variable<Double>> solution : solutions) {
			LOGGER.info("Fitness = " + solution.getObjectives().get(0));
		}
	}
}
