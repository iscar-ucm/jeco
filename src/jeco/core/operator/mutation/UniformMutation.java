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
package jeco.core.operator.mutation;

import java.util.ArrayList;

import jeco.core.problem.Problem;
import jeco.core.problem.Solution;
import jeco.core.problem.Variable;
import jeco.core.util.random.RandomGenerator;

/**
 * UniformMutation mutation operator. It changes the value of a variable
 * with a given probability.
 * 
 * @param <T> Variable type.
 */
public class UniformMutation<T extends Variable<Double>> extends MutationOperator<T> {
	public static final double DEFAULT_PERTURBATION_INDEX = 0.5;

	/**
	 * Stores the value used in a uniform mutation operator
	 */
	protected double perturbationIndex;
	/**
	 * Stores the probability of mutation
	 */
	protected double probability;
	/**
	 * Stores the problem to solve
	 */
	protected Problem<T> problem;

	/**
	 * Constructor
	 * Creates a new uniform mutation operator instance
	 * 
	 * @param problem          The problem to solve
	 * @param probability      The probability of mutation
	 * @param perturbationIndex The perturbation index
	 */
	public UniformMutation(Problem<T> problem, double probability, double perturbationIndex) {
		super(probability);
		this.problem = problem;
		this.perturbationIndex = perturbationIndex;
	} // UniformMutation

	/**
	 * Constructor
	 * Creates a new uniform mutation operator instance
	 * 
	 * @param problem     The problem to solve
	 */
	public UniformMutation(Problem<T> problem) {
		this(problem, 1.0/problem.getNumberOfVariables(), DEFAULT_PERTURBATION_INDEX);
	}

	@Override
	public Solution<T> execute(Solution<T> solution) {
		ArrayList<T> variables = solution.getVariables();
		for (int i = 0; i < variables.size(); ++i) {
			T variable = variables.get(i);
			if (RandomGenerator.nextDouble() < probability) {
				double rand = RandomGenerator.nextDouble();
				double tmp = (rand - 0.5) * perturbationIndex;

				tmp += variable.getValue();

				if (tmp < problem.getLowerBound(i)) {
					tmp = problem.getLowerBound(i);
				} else if (tmp > problem.getUpperBound(i)) {
					tmp = problem.getUpperBound(i);
				}

				variable.setValue(tmp);
			}
		}

		return solution;
	} // execute
} // UniformMutation

