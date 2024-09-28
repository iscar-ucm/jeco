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
 * This class implements a non-uniform mutation operator. The NonUniformMutation
 * operator changes the value of a variable with a given probability, but the
 * change is not uniform. The change is random and it is based on a non-uniform
 * distribution.
 * 
 * @param <T> Variable type.
 */
public class NonUniformMutation<T extends Variable<Double>> extends MutationOperator<T> {

	/**
	 * DEFAULT_PERTURBATION_INDEX stores the default perturbation index value
	 */
	public static final double DEFAULT_PERTURBATION_INDEX = 0.5;
	/**
	 * problem stores the problem to solve
	 */
	protected Problem<T> problem;
	/**
	 * perturbationIndex stores the perturbation value used in the Non Uniform
	 * mutation operator
	 */
	private double perturbationIndex;
	/**
	 * maxIterations stores the maximun number of iterations.
	 */
	private int maxIterations;
	/**
	 * currentIteration stores the iteration in which the operator is going to be
	 * applied
	 */
	private int currentIteration;
	/**
	 * Constructor
	 * Creates a new instance of the non uniform mutation
	 * 
	 * @param problem          The problem
	 * @param probability      The probability of mutation
	 * @param perturbationIndex The perturbation index
	 * @param currentIteration The current iteration
	 * @param maxIterations    The maximum number of iterations
	 */
	public NonUniformMutation(Problem<T> problem, double probability, double perturbationIndex, int currentIteration, int maxIterations) {
		super(probability);
		this.problem = problem;
		this.perturbationIndex = perturbationIndex;
		this.currentIteration = currentIteration;
		this.maxIterations = maxIterations;
	} // NonUniformMutation

	/**
	 * Constructor
	 * Creates a new instance of the non uniform mutation
	 * 
	 * @param problem          The problem
	 * @param maxIterations    The maximum number of iterations
	 */
	public NonUniformMutation(Problem<T> problem, int maxIterations) {
		this(problem, 1.0 / problem.getNumberOfVariables(), DEFAULT_PERTURBATION_INDEX, 0, maxIterations);
	}

	@Override
	public Solution<T> execute(Solution<T> solution) {
		ArrayList<T> variables = solution.getVariables();
		for (int i = 0; i < variables.size(); ++i) {
			T variable = variables.get(i);
			if (RandomGenerator.nextDouble() < probability) {
				double rand = RandomGenerator.nextDouble();
				double tmp;

				if (rand <= 0.5) {
					tmp = delta(problem.getUpperBound(i) - variable.getValue(), perturbationIndex);
					tmp += variable.getValue();
				} else {
					tmp = delta(problem.getLowerBound(i) - variable.getValue(), perturbationIndex);
					tmp += variable.getValue();
				}

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

	/**
	 * Calculates the delta value used in NonUniform mutation operator.
	 * 
	 * @param y                 The value of the variable
	 * @param bMutationParameter The mutation parameter
	 */
	private double delta(double y, double bMutationParameter) {
		double rand = RandomGenerator.nextDouble();
		return (y * (1.0 - Math.pow(rand, Math.pow((1.0 - currentIteration / (double) maxIterations), bMutationParameter))));
	} // delta

	/**
	 * Sets the current iteration
	 * @param currentIteration The current iteration
	 */
	public void setCurrentIteration(int currentIteration) {
		this.currentIteration = currentIteration;
	}
} // NonUniformMutation

