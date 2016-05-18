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
package hero.operator.mutation;

import hero.problem.Solution;
import hero.problem.Variable;
import hero.util.random.RandomGenerator;
/**
 *
 * @author cia
 */ 

//Solutions must be numeric
public class SwapMutationDouble<T extends Variable<?>> extends MutationOperator<T> {

	protected double probability;

	/**
	 * Constructor
	 * Creates a new IntegerFlipMutation mutation operator instance
	 */
	public SwapMutationDouble(double probability) {
		super(probability);
	} // IntegerFlipMutation

	public Solution<T> execute(Solution<T> solution) {

		int halfsize = solution.getVariables().size() /2;
		int offset = 0;
		if (RandomGenerator.nextDouble() < probability) {
			offset=halfsize;}

		if (RandomGenerator.nextDouble() < probability) {
			int indexI = RandomGenerator.nextInt(halfsize) + offset;
			int indexJ = RandomGenerator.nextInt(halfsize) + offset;
			if (indexI != indexJ) {
				T varI = solution.getVariables().get(indexI);
				solution.getVariables().set(indexI, solution.getVariables().get(indexJ));
				solution.getVariables().set(indexJ, varI);
			}
		}
		return solution;
	} // execute

} // IntegerFlipMutation

