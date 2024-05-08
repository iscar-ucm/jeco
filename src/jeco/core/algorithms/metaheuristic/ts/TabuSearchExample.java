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

package jeco.core.algorithms.metaheuristic.ts;

import java.util.logging.Level;

import jeco.core.benchmarks.Rastringin;
import jeco.core.operator.generator.DefaultNeighborGenerator;
import jeco.core.operator.generator.NeighborGenerator;
import jeco.core.problem.Solution;
import jeco.core.problem.Solutions;
import jeco.core.problem.Variable;
import jeco.core.util.logger.JecoLogger;

public class TabuSearchExample {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		JecoLogger.setup(Level.FINE);
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
