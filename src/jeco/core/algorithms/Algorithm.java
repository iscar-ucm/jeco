/*
* File: Algorithm.java
* Author: José Luis Risco Martín <jlrisco@ucm.es>
* Created: 2010/05/08 (YYYY/MM/DD)
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
package jeco.core.algorithms;

import jeco.core.problem.Problem;
import jeco.core.problem.Solutions;
import jeco.core.problem.Variable;

/**
 * Abstract class for optimization algorithms.
 * 
 * @param <V> Variable type
 */
public abstract class Algorithm<V extends Variable<?>> {

  /**
   * Problem to be solved
   */
  protected Problem<V> problem = null;
  
  /**
   * Constructor
   * @param problem Problem to be solved
   */
  public Algorithm(Problem<V> problem) {
    this.problem = problem;
  }

  /**
   * Sets the problem to be solved
   * @param problem Problem to be solved
   */
  public void setProblem(Problem<V> problem) {
    this.problem = problem;
  }
  
  /**
   * Initializes the algorithm
   * @param initialSolutions initial population. If null, a random population is generated.
   */
  public abstract void initialize(Solutions<V> initialSolutions); 

  /**
   * Initializes the algorithm with a random population
   */
  public void initialize() {
      this.initialize(null);
  }

  /**
   * Executes one step of the algorithm
   */
  public abstract void step();

  /**
   * Executes the algorithm
   * @return Set of solutions obtained
   */
  public abstract Solutions<V> execute();
}
