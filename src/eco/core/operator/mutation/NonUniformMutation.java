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
package eco.core.operator.mutation;

import java.util.ArrayList;

import eco.core.problem.Problem;
import eco.core.problem.Solution;
import eco.core.problem.Variable;
import eco.core.util.random.RandomGenerator;

//Note: Solutions must be real
public class NonUniformMutation<T extends Variable<Double>> extends MutationOperator<T> {

	public static final double DEFAULT_PERTURBATION_INDEX = 0.5;
	protected Problem<T> problem;
	/**
	 * perturbation_ stores the perturbation value used in the Non Uniform
	 * mutation operator
	 */
	private double perturbationIndex;
	/**
	 * maxIterations_ stores the maximun number of iterations.
	 */
	private int maxIterations;
	/**
	 * actualIteration_ stores the iteration in which the operator is going to be
	 * applied
	 */
	private int currentIteration;
	/**
	 * Constructor
	 * Creates a new instance of the non uniform mutation
	 */
	public NonUniformMutation(Problem<T> problem, double probability, double perturbationIndex, int currentIteration, int maxIterations) {
		super(probability);
		this.problem = problem;
		this.perturbationIndex = perturbationIndex;
		this.currentIteration = currentIteration;
		this.maxIterations = maxIterations;
	} // NonUniformMutation

	public NonUniformMutation(Problem<T> problem, int maxIterations) {
		this(problem, 1.0 / problem.getNumberOfVariables(), DEFAULT_PERTURBATION_INDEX, 0, maxIterations);
	}

	/**
	 * Executes the operation
	 * @param object An object containing a solution
	 * @return An object containing the mutated solution
	 * @throws JMException
	 */
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
	 * Calculates the delta value used in NonUniform mutation operator
	 */
	private double delta(double y, double bMutationParameter) {
		double rand = RandomGenerator.nextDouble();
		return (y * (1.0 - Math.pow(rand, Math.pow((1.0 - currentIteration / (double) maxIterations), bMutationParameter))));
	} // delta

	public void setCurrentIteration(int currentIteration) {
		this.currentIteration = currentIteration;
	}
} // NonUniformMutation

