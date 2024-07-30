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
package jeco.core.operator.assigner;

import java.util.ArrayList;
import java.util.Comparator;

import jeco.core.problem.Solution;
import jeco.core.problem.Solutions;
import jeco.core.problem.Variable;

/**
 * Extracts the fronts of non-dominated solutions from a population.
 * The fronts are stored in a list of populations.
 * The rank of the solutions is stored in the property "rank" of the solutions.
 * 
 * @param <V> Type of the variables of the solutions.
 */
public class FrontsExtractor<V extends Variable<?>> {

  /**
   * Comparator used to compare the solutions.
   */
  protected Comparator<Solution<V>> comparator;
  /**
   * Property name for the number of solutions in the population.
   */
  public static final String propertyN = "n";
  /**
   * Property name for the rank of the solutions.
   */
  public static final String propertyRank = "rank";
  /**
   * Property name for the index of the solutions.
   */
  public static final String propertyIndexS = "indexS";

  /**
   * Constructor.
   * @param comparator Comparator used to compare the solutions.
   */
  public FrontsExtractor(Comparator<Solution<V>> comparator) {
    this.comparator = comparator;
  }

  /**
   * Extracts the fronts of non-dominated solutions from a population.
   * The fronts are stored in a list of populations.
   * The rank of the solutions is stored in the property "rank" of the solutions.
   * @param arg Population of solutions.
   * @return List of populations with the fronts of non-dominated solutions.
   */
  public ArrayList<Solutions<V>> execute(Solutions<V> arg) {
    Solutions<V> solutions = new Solutions<V>();
    solutions.addAll(arg);
    ArrayList<Solutions<V>> fronts = new ArrayList<Solutions<V>>();
    Solutions<V> rest = solutions.reduceToNonDominated(comparator);
    fronts.add(solutions);
    while (rest.size() > 0) {
      solutions = rest;
      rest = solutions.reduceToNonDominated(comparator);
      fronts.add(solutions);
    }
    int rank = 1;
    for (int i = 0; i < fronts.size(); ++i) {
      Solutions<V> front = fronts.get(i);
      for (int j = 0; j < front.size(); ++j) {
        front.get(j).getProperties().put(propertyRank, rank);
      }
      rank++;
    }
    return fronts;
  }
}
