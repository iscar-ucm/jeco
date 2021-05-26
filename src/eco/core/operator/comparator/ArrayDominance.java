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
package eco.core.operator.comparator;

import java.util.ArrayList;
import java.util.Comparator;

public class ArrayDominance implements Comparator<ArrayList<Double>> {

  @Override
  public int compare(ArrayList<Double> left, ArrayList<Double> right) {
    int i, n;
    double diff;
    n = Math.min(left.size(), right.size());

    boolean bigger = false;
    boolean smaller = false;
    boolean indiff = false;
    for (i = 0; !(indiff) && i < n; i++) {
      diff = left.get(i) - right.get(i);
      if (diff > 0) {
        bigger = true;
      }
      if (diff <= 0) {
        smaller = true;
      }
      indiff = (bigger && smaller);
    }
    if (indiff) {
      return 0;
    } else if (smaller) {
      return -1;
    }
    return 1;
  }
}
