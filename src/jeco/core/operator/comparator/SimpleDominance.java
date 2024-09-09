/*
* File: SimpleDominance.java
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
 * Compares two solutions according to the value of their first objective.
 * 
 * @param <T> Type of the variables of the solutions.
 */
public class SimpleDominance<T extends Variable<?>> implements Comparator<Solution<T>> {

    @Override
    public int compare(Solution<T> s1, Solution<T> s2) {
        Double fLeft = s1.getObjectives().get(0);
        Double fRight = s2.getObjectives().get(0);

        if (fLeft < fRight) {
            return -1;
        }
        if (fLeft > fRight) {
            return 1;
        }
        return 0;
    }
}
