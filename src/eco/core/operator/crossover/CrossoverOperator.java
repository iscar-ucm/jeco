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
package eco.core.operator.crossover;

import eco.core.problem.Solution;
import eco.core.problem.Solutions;
import eco.core.problem.Variable;
 /**
  * El cruce sí necesita plantillas porque se accede continuamente a las variables
  * @author jlrisco
  * @param <T>
  */

public abstract class CrossoverOperator<T extends Variable<?>> {
    abstract public Solutions<T> execute(Solution<T> parent1, Solution<T> parent2);
}
