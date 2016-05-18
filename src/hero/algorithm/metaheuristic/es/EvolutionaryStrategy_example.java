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
package hero.algorithm.metaheuristic.es;

import java.util.logging.Level;
import java.util.logging.Logger;

import hero.operator.mutation.PolynomialMutation;
import hero.problem.Solution;
import hero.problem.Solutions;
import hero.problem.Variable;
import hero.problems.Rastringin;
import hero.util.logger.HeroLogger;

public class EvolutionaryStrategy_example {

	private static final Logger LOGGER = Logger.getLogger(EvolutionaryStrategy_example.class.getName());

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		HeroLogger.setup(Level.INFO);
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
