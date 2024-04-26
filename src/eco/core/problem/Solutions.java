/*
* File: Solutions.java
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

package eco.core.problem;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;

/**
 * Class representing a set of solutions in a problem.
 * @param <V> Type of the variables.
 */
public class Solutions<V extends Variable<?>> extends ArrayList<Solution<V>> {

  /**
   * Constructor.
   */
  public Solutions() {
    super();
  }

  /**
   * Keep this set of solutions non-dominated. Returns the set of dominated
   * solutions.
   *
   * @param comparator Comparator used.
   * @return The set of dominated solutions.
   */
  public Solutions<V> reduceToNonDominated(Comparator<Solution<V>> comparator) {
    Solutions<V> rest = new Solutions<V>();
    int compare;
    Solution<V> solI;
    Solution<V> solJ;
    for (int i = 0; i < size() - 1; i++) {
      solI = get(i);
      for (int j = i + 1; j < size(); j++) {
        solJ = get(j);
        compare = comparator.compare(solI, solJ);
        if (compare < 0) { // i dominates j
          rest.add(solJ);
          remove(j--);
        } else if (compare > 0) { // j dominates i
          rest.add(solI);
          remove(i--);
          j = size();
        } else if (solI.equals(solJ)) { // both are equal, just one copy
          remove(j--);
        }
      }
    }
    return rest;
  }

  @Override
  public String toString() {
    StringBuilder buffer = new StringBuilder();
    for (Solution<V> solution : this) {
      buffer.append(solution.toString());
      buffer.append("\n");
    }
    return buffer.toString();
  }

  /**
   * Function that reads a set of solutions from a file.
   * @param filePath File path
   * @return The set of solutions in the archive.
   * @throws IOException 
   * @throws java.lang.Exception
   */
  public static Solutions<Variable<?>> readFrontFromFile(String filePath) throws IOException {
      Solutions<Variable<?>> solutions = new Solutions<Variable<?>>();
      BufferedReader reader = new BufferedReader(new FileReader(new File(filePath)));
      String line = reader.readLine();
      while (line != null) {
          String[] objectives = line.split(" ");
          if (line.length() > 0 && objectives != null && objectives.length > 0) {
              Solution<Variable<?>> solution = new Solution<Variable<?>>(objectives.length);
              for (int i = 0; i < objectives.length; ++i) {
                  solution.getObjectives().set(i, Double.valueOf(objectives[i]));
              }
              solutions.add(solution);
          }
          line = reader.readLine();
      }
      reader.close();
      return solutions;
  }

  /**
   * Function that reads N sets of solutions from a file.
   *
   * @param filePath File path
   * @return The set of solutions in the archive. Each solution set is separated
   * in the file by a blank line.
   */
  public static ArrayList<Solutions<Variable<?>>> readFrontsFromFile(String filePath) throws FileNotFoundException, IOException {
    ArrayList<Solutions<Variable<?>>> result = new ArrayList<Solutions<Variable<?>>>();
    BufferedReader reader = new BufferedReader(new FileReader(new File(filePath)));
    String line = reader.readLine();
    Solutions<Variable<?>> solutions = new Solutions<Variable<?>>();
    while (line != null) {
      String[] objectives = line.split(" ");
      if (line.length() <= 0 || objectives == null || objectives.length == 0) {
        if (solutions.size() > 0) {
          result.add(solutions);
        }
        solutions = new Solutions<Variable<?>>();
      } else {
        Solution<Variable<?>> solution = new Solution<Variable<?>>(objectives.length);
        for (int i = 0; i < objectives.length; ++i) {
          solution.getObjectives().set(i, Double.valueOf(objectives[i]));
        }
        solutions.add(solution);
      }
      line = reader.readLine();
    }
    reader.close();
    return result;
  }
  
  /**
   * Function that normalizes a set of fronts in the given interval.
   * @param setOfSolutions Set of fronts
   * @param dim Number of objectives
   * @param lowerBound lower bound
   * @param upperBound upper bound
   */
  public static void normalize(ArrayList<Solutions<Variable<?>>> setOfSolutions, int dim, double lowerBound, double upperBound) {
      double[] mins = new double[dim];
      double[] maxs = new double[dim];
      for (int i = 0; i < dim; ++i) {
          mins[i] = Double.POSITIVE_INFINITY;
          maxs[i] = Double.NEGATIVE_INFINITY;
      }

      Solutions<Variable<?>> allTheSolutions = new Solutions<Variable<?>>();
      for (Solutions<Variable<?>> solutions : setOfSolutions) {
          allTheSolutions.addAll(solutions);
      }
      // Get the maximum and minimun values:
      for (Solution<Variable<?>> solution : allTheSolutions) {
          for (int i = 0; i < dim; ++i) {
              double objI = solution.getObjectives().get(i);
              if (objI < mins[i]) {
                  mins[i] = objI;
              }
              if (objI > maxs[i]) {
                  maxs[i] = objI;
              }
          }
      }

      // Normalize:
      for (Solution<Variable<?>> solution : allTheSolutions) {
          for (int i = 0; i < dim; ++i) {
              double objI = solution.getObjectives().get(i);
              double newObjI = 1.0 + (objI - mins[i]) / (maxs[i] - mins[i]);
              solution.getObjectives().set(i, newObjI);
          }
      }

  }

  /**
   * Function that normalize a set of fronts in the interval [1,2]
   * @param setOfSolutions Set of fronts
   * @param dim Number of objectives
   */
  public static void normalize(ArrayList<Solutions<Variable<?>>> setOfSolutions, int dim) {
      Solutions.normalize(setOfSolutions, dim, 1.0, 2.0);
  }  
}
