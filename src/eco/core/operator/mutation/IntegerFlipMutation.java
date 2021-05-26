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

import eco.core.problem.Problem;
import eco.core.problem.Solution;
import eco.core.problem.Variable;
import eco.core.util.random.RandomGenerator;

//Solutions must be numeric
public class IntegerFlipMutation<T extends Variable<Integer>> extends MutationOperator<T> {
	protected Problem<T> problem;

	/**
	 * Constructor
	 * Creates a new IntegerFlipMutation mutation operator instance
	 */
	public IntegerFlipMutation(Problem<T> problem, double probability) {
		super(probability);
		this.problem = problem;
	} // IntegerFlipMutation

  @Override
	public Solution<T> execute(Solution<T> solution) {
		for (int i = 0; i < solution.getVariables().size(); i++) {
			if (RandomGenerator.nextDouble() < probability) {
				int lowerBound = (int)Math.round(problem.getLowerBound(i));
				int upperBound = (int)Math.round(problem.getUpperBound(i));
				solution.getVariables().get(i).setValue(RandomGenerator.nextInteger(lowerBound, upperBound));
			}
		}
		return solution;
	} // execute
} // IntegerFlipMutation

