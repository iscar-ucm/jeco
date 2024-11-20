/*
* File: ObjectiveComparator.java
* Author: José Luis Risco Martín <jlrisco@ucm.es>
* Created: 2010/09/09 (YYYY/MM/DD)
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


package jeco.core.operator.comparator;

import java.util.Comparator;

import jeco.core.problem.Solution;
import jeco.core.problem.Variable;

/**
 * Compares two solutions according to the value of one of their objectives.
 * 
 * @param <V> Type of the variables of the solutions.
 */
public class ObjectiveComparator<V extends Variable<?>> implements Comparator<Solution<V>> {

	protected int obj = 0;

	public ObjectiveComparator(int obj) {
		this.obj = obj;
	}

	public int compare(Solution<V> left, Solution<V> right) {
		if (left.getObjectives().get(obj) < right.getObjectives().get(obj)) {
			return -1;
		} else if (left.getObjectives().get(obj) > right.getObjectives().get(obj)) {
			return 1;
		} else {
			return 0;
		}
	}

	public void setObj(int obj) {
		this.obj = obj;
	}
}
