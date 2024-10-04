/*
 * Copyright (C) 2010 José Luis Risco Martín <jlrisco@ucm.es>
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
package jeco.core.operator.selection;

import jeco.core.problem.Solutions;
import jeco.core.problem.Variable;

/**
 * Abstract class for selection operators.
 */
public abstract class SelectionOperator<T extends Variable<?>> {

    /**
     * Execute the selection operator.
     * @param solutions Set of solutions to select from.
     * @return Selected solutions.
     */
    abstract public Solutions<T> execute(Solutions<T> solutions);
}
