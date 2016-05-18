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
/**
 * El operador de mutación también requiere genéricos porque se accede a las variables.
 * @author jlrisco
 * @param <T>
 */
public abstract class MutationOperator<T extends Variable<?>> {
	protected double probability;
	
	public MutationOperator(double probability) {
		this.probability = probability;
	}
	
	public void setProbability(double probability) {
		this.probability = probability;
	}

	abstract public Solution<T> execute(Solution<T> solution);
}
