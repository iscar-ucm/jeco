/*
* File: Problem.java
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

/**
 * Class representing a problem.
 */
public abstract class Problem<V extends Variable<?>> {
  /**
   * Constant representing infinity.
   */
  public static final double INFINITY = Double.POSITIVE_INFINITY;
  /**
   * Number of variables of the problem.
   */
  protected int numberOfVariables;
  /**
   * Number of objectives of the problem.
   */
  protected int numberOfObjectives;
  /**
   * Lower bound of the variables.
   */
  protected double[] lowerBound;
  /**
   * Upper bound of the variables.
   */
  protected double[] upperBound;

  /**
   * Constructor.
   * @param numberOfVariables Number of variables.
   * @param numberOfObjectives Number of objectives.
   */
  public Problem(int numberOfVariables, int numberOfObjectives) {
    this.numberOfVariables = numberOfVariables;
    this.numberOfObjectives = numberOfObjectives;
    this.lowerBound = new double[numberOfVariables];
    this.upperBound = new double[numberOfVariables];
  }

  /**
   * Get the number of variables.
   * @return Number of variables.
   */
  public int getNumberOfVariables() {
    return numberOfVariables;
  }

   /**
   * Get the number of objectives.
   * @return Number of objectives.
   */
  public int getNumberOfObjectives() {
    return numberOfObjectives;
  }

  /**
   * Get the lower bound of a variable.
   * @param i Index of the variable.
   * @return Lower bound of the variable.
   */
  public double getLowerBound(int i) {
    return lowerBound[i];
  }

  /**
   * Get the upper bound of a variable.
   * @param i Index of the variable.
   * @return Upper bound of the variable.
   */
  public double getUpperBound(int i) {
    return upperBound[i];
  }

  /**
   * Function to create a new random set of solutions.
   * @param size Size of the set.
   * @return New random set of solutions.
   */
  public abstract Solutions<V> newRandomSetOfSolutions(int size);

  /**
   * Function to evaluate a set of solutions.
   * @param solutions Set of solutions to evaluate.
   */
  public void evaluate(Solutions<V> solutions) {
    for(Solution<V> solution : solutions) {
      evaluate(solution);
    }
  }
  
  /**
   * Function to evaluate a solution.
   * @param solution Solution to evaluate.
   */
  public abstract void evaluate(Solution<V> solution);

  @Override
  public abstract Problem<V> clone();
}
