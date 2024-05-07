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
package eco.core.algorithm.metaheuristic.moga;

import java.util.logging.Logger;

import eco.core.benchmarks.dtlz.DTLZ1;
import eco.core.operator.crossover.SBXCrossover;
import eco.core.operator.mutation.PolynomialMutation;
import eco.core.operator.selection.BinaryTournamentNSGAII;
import eco.core.problem.Solutions;
import eco.core.problem.Variable;
import eco.core.util.logger.EcoLogger;

public class NSGAII_example {
	private static final Logger logger = Logger.getLogger(NSGAII_example.class.getName());
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		EcoLogger.setup();
		// First create the problem
		DTLZ1 problem = new DTLZ1(30);
		// Second create the algorithm
		NSGAII<Variable<Double>> algorithm = new NSGAII<Variable<Double>>(problem, 100, 250, new PolynomialMutation<Variable<Double>>(problem), new SBXCrossover<Variable<Double>>(problem), new BinaryTournamentNSGAII<Variable<Double>>());
		algorithm.initialize();
		Solutions<Variable<Double>> solutions = algorithm.execute();
		logger.info("solutions.size()="+ solutions.size());
		System.out.println(solutions.toString());
	}
}
