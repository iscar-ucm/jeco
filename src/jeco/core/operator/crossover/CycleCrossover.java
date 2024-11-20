/*
* File: CycleCrossover.java
* Author: José Luis Risco Martín <jlrisco@ucm.es>
* Created: 2010/09/13 (YYYY/MM/DD)
*
* Copyright (C) 2010
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
*/

package jeco.core.operator.crossover;

import java.util.LinkedList;

import jeco.core.problem.Solution;
import jeco.core.problem.Solutions;
import jeco.core.problem.Variable;
import jeco.core.util.random.RandomGenerator;

/**
 * Class for cycle crossover.
 * 
 * This class implements the cycle crossover operator. The cycle crossover operator
 * is a genetic operator that combines two parents to generate two offsprings. The
 * operator is based on the cycle crossover operator for permutations. The operator
 * is applied to the variables of the solutions.
 */
public class CycleCrossover<V extends Variable<?>> extends CrossoverOperator<V> {

	public static final double DEFAULT_PROBABILITY = 0.9;
	/**
	 * Probability of crossover
	 */
	protected double probability;

	/**
	 * Constructor
	 */
	public CycleCrossover() {
		probability = DEFAULT_PROBABILITY;
	}

	/**
	 * Constructor
	 * 
	 * @param probability
	 *            Probability of crossover
	 */
	public CycleCrossover(double probability) {
		this.probability = probability;
	}

	/**
	 * Looks for the position of a variable in a solution
	 * @param parent The solution
	 * @param variable The variable
	 * @return The position of the variable in the solution
	 */
	private Integer lookForPosition(Solution<V> parent, V variable) {
		if (variable == null) {
			return 0;
		}
		V varJ = null;
		for (int j = 0; j < parent.getVariables().size(); ++j) {
			varJ = parent.getVariables().get(j);
			if (variable.equals(varJ)) {
				return j;
			}
		}
		return -1;
	}

	/**
	 * Executes the operation
	 * @param probability Probability of crossover
	 * @param parent1 The first parent
	 * @param parent2 The second parent
	 * @return The offsprings
	 */
	public Solutions<V> doCrossover(double probability, Solution<V> parent1, Solution<V> parent2) {

		Solutions<V> offSpring = new Solutions<V>();

		offSpring.add(parent1.clone());
		offSpring.add(parent2.clone());

		if (RandomGenerator.nextDouble() <= probability) {
			// We obtain the cycle, first allele:
				Integer currentPos = 0;
			LinkedList<Integer> cycle = new LinkedList<Integer>();
			cycle.add(currentPos);

			V variable = parent2.getVariables().get(currentPos);
			currentPos = lookForPosition(parent1, variable);
			while (currentPos != 0) {
				cycle.add(currentPos);
				variable = parent2.getVariables().get(currentPos);
				currentPos = lookForPosition(parent1, variable);
			}

			Solution<V> clon1 = parent1.clone();
			Solution<V> clon2 = parent2.clone();
			for (int i = 0; i < parent1.getVariables().size(); ++i) {
				if (cycle.contains(i)) {
					offSpring.get(0).getVariables().set(i, clon1.getVariables().get(i));
					offSpring.get(1).getVariables().set(i, clon2.getVariables().get(i));
				} else {
					offSpring.get(0).getVariables().set(i, clon2.getVariables().get(i));
					offSpring.get(1).getVariables().set(i, clon1.getVariables().get(i));
				}
			}
		}
		return offSpring;
	} // doCrossover

	/**
	 * Executes the operation
	 * @param parent1 The first parent
	 * @param parent2 The second parent
	 * @return The offsprings
	 */
	public Solutions<V> execute(Solution<V> parent1, Solution<V> parent2) {
		return doCrossover(probability, parent1, parent2);
	} // execute
} // CX

