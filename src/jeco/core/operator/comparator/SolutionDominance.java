/*
* File: SolutionDominance.java
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

import java.util.ArrayList;
import java.util.Comparator;

import jeco.core.problem.Solution;
import jeco.core.problem.Variable;

/**
 * Compares two solutions according to the dominance relation. A solution
 * dominates another if it is better in all objectives.
 * 
 * @param <T> Type of the variables of the solutions.
 */
public class SolutionDominance<T extends Variable<?>> implements Comparator<Solution<T>> {

  @Override
  public int compare(Solution<T> s1, Solution<T> s2) {
    if (s2 == null) {
      return -1;
    }
    int n = Math.min(s1.getObjectives().size(), s2.getObjectives().size());
    ArrayList<Double> z1 = s1.getObjectives();
    ArrayList<Double> z2 = s2.getObjectives();

    boolean bigger = false;
    boolean smaller = false;
    boolean indiff = false;
    for (int i = 0; !(indiff) && i < n; i++) {
      if (z1.get(i) > z2.get(i)) {
        bigger = true;
      }
      if (z1.get(i) < z2.get(i)) {
        smaller = true;
      }
      indiff = (bigger && smaller);
    }

    if (smaller && !bigger) {
      return -1;
    } else if (bigger && !smaller) {
      return 1;
    }
    return 0;
  }
}
