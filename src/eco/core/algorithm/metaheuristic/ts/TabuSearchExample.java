/*
* Copyright (C) 2024 José Luis Risco Martín <jlrisco@ucm.es>
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

package eco.core.algorithm.metaheuristic.ts;

import java.util.logging.Level;

import eco.core.benchmarks.Rastringin;
import eco.core.operator.generator.NeighborGenerator;
import eco.core.operator.generator.DefaultNeighborGenerator;
import eco.core.problem.Solution;
import eco.core.problem.Solutions;
import eco.core.problem.Variable;
import eco.core.util.logger.HeroLogger;

public class TabuSearchExample {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		HeroLogger.setup(Level.FINE);
		// First create the problem
		Rastringin problem = new Rastringin(4);
		// Second create the algorithm
		NeighborGenerator<Variable<Double>> neighborGen = new DefaultNeighborGenerator<>(problem, 20);
		TabuSearch<Variable<Double>> ts = new TabuSearch<>(problem, 1000, 100, true, neighborGen);
		ts.initialize();
		Solutions<Variable<Double>> solutions = ts.execute();
		Solution<Variable<Double>> bestSolution = solutions.get(0);
		System.out.println("Fitness = " + bestSolution.getObjectives().get(0));
		//System.out.println("solutions.size()="+ solutions.size());
		//System.out.println(solutions.toString());
		//System.out.println("solutions.size()="+ solutions.size());
	}
}
