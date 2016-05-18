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

import java.util.logging.Logger;

import hero.operator.crossover.SBXCrossover;
import hero.operator.mutation.PolynomialMutation;
import hero.operator.selection.BinaryTournament;
import hero.problem.Solutions;
import hero.problem.Variable;
import hero.problems.zdt.ZDT1;

public class SPEA2_example {
	private static final Logger logger = Logger.getLogger(SPEA2_example.class.getName());
	public static void main(String[] args) {
		// First create the problem
		ZDT1 zdt1 = new ZDT1(30);
		// Second create the algorithm
		SPEA2<Variable<Double>> spea2 = new SPEA2<Variable<Double>>(zdt1, 100, 250, new PolynomialMutation<Variable<Double>>(zdt1), new SBXCrossover<Variable<Double>>(zdt1), new BinaryTournament<Variable<Double>>());
		Solutions<Variable<Double>> solutions = spea2.execute();
		logger.info("solutions.size()="+ solutions.size());
		System.out.println(solutions.toString());
	}
} // SPEA2_main.java

