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
package hero.algorithm.metaheuristic.mopso;

import java.util.logging.Logger;


import hero.problem.Solutions;
import hero.problem.Variable;
import hero.problems.zdt.ZDT1;
import hero.util.logger.HeroLogger;

public class NSPSO_example {
	private static final Logger logger = Logger.getLogger(NSPSO_example.class.getName());
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		HeroLogger.setup();
		// First create the problem
		ZDT1 problem = new ZDT1(30);
		NSPSO<Variable<Double>> algorithm = new NSPSO<Variable<Double>>(problem, 100, 250, 0.4, 2.0, 2.0);
		algorithm.initialize();
		Solutions<Variable<Double>> solutions = algorithm.execute();
		logger.info("solutions.size()="+ solutions.size());
		System.out.println(solutions.toString());
	}
}
