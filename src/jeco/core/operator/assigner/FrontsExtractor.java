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
package jeco.core.operator.assigner;

import java.util.ArrayList;
import java.util.Comparator;

import jeco.core.problem.Solution;
import jeco.core.problem.Solutions;
import jeco.core.problem.Variable;

public class FrontsExtractor<V extends Variable<?>> {

  protected Comparator<Solution<V>> comparator;
  public static final String propertyN = "n";
  public static final String propertyRank = "rank";
  public static final String propertyIndexS = "indexS";

  public FrontsExtractor(Comparator<Solution<V>> comparator) {
    this.comparator = comparator;
  }

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
