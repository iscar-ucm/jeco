/*
* File: Solution.java
* Author: José Luis Risco Martín <jlrisco@ucm.es>
* Created: 2010/04/26 (YYYY/MM/DD)
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
package jeco.core.problem;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * Class representing a solution in a problem.
 * @param <V> Type of the variables.
 */
public class Solution<V extends Variable<?>> {

  /**
   * Variables of the solution.
   */
  protected ArrayList<V> variables = new ArrayList<V>();
  /**
   * Objectives of the solution.
   */
  protected ArrayList<Double> objectives = new ArrayList<Double>();
  /**
   * User properties of the solution.
   */
  protected HashMap<String, Number> properties = new HashMap<String, Number>();

  /**
   * Constructor.
   * @param numberOfObjectives Number of objectives.
   */
  public Solution(int numberOfObjectives) {
    for (int i = 0; i < numberOfObjectives; ++i) {
      objectives.add(0.0);
    }
  }

  /**
   * Get the variables of the solution.
   * @return Variables of the solution.
   */
  public ArrayList<V> getVariables() {
    return variables;
  }

  /**
   * Gets a given decision variable.
   * @param idx Index of the variable.
   * @return The variable.
   */
  public V getVariable(int idx) {
    return variables.get(idx);
  }

  /**
   * Returns the set of objectives.
   * @return The set of objectives.
   */
  public ArrayList<Double> getObjectives() {
    return objectives;
  }

  /**
   * Gets the value of a given objective.
   * @param idx Index of the objective.
   * @return The value of the objective.
   */
  public Double getObjective(int idx) {
    return objectives.get(idx);
  }

  /**
   * Returns the user properties of the solution.
   * @return The user properties of the solution.
   */
  public HashMap<String, Number> getProperties() {
    return properties;
  }

  @SuppressWarnings("unchecked")
  @Override
  public Solution<V> clone() {
    Solution<V> clone = new Solution<V>(objectives.size());
    for (int i = 0; i < objectives.size(); ++i) {
      clone.objectives.set(i, objectives.get(i));
    }
    for (int i = 0; i < variables.size(); ++i) {
      clone.variables.add((V) variables.get(i).clone());
    }

    for (Map.Entry<String, Number> entry : properties.entrySet()) {
      clone.properties.put(entry.getKey(), entry.getValue());
    }

    return clone;
  }

  /**
   * Compares two solutions.
   * @param solution Solution to compare.
   * @param comparator Comparator to use.
   * @return The result of the comparison.
   */
  public int compareTo(Solution<V> solution, Comparator<Solution<V>> comparator) {
    return comparator.compare(this, solution);
  }

  @SuppressWarnings("unchecked")
	@Override
  public boolean equals(Object right) {
    Solution<V> sol = (Solution<V>) right;
    int nVar = Math.min(variables.size(), sol.variables.size());
    for (int i = 0; i < nVar; ++i) {
      if (!this.getVariable(i).equals(sol.getVariable(i))) {
        return false;
      }
    }
    int nObj = Math.min(objectives.size(), sol.objectives.size());

    for (int i = 0; i < nObj; ++i) {
      if (!this.getObjective(i).equals(sol.getObjective(i))) {
        return false;
      }
    }
    return true;
  }

  @Override
  public String toString() {
    StringBuilder buffer = new StringBuilder();
    for (int i = 0; i < this.objectives.size(); ++i) {
      buffer.append(objectives.get(i)).append(" ");
    }
    return buffer.toString();
  }
}
