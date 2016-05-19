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
package hero.core.problem;

public abstract class Problem<V extends Variable<?>> {
  public static final double INFINITY = Double.POSITIVE_INFINITY;
  protected int numberOfVariables;
  protected int numberOfObjectives;
  protected double[] lowerBound;
  protected double[] upperBound;

  public Problem(int numberOfVariables, int numberOfObjectives) {
    this.numberOfVariables = numberOfVariables;
    this.numberOfObjectives = numberOfObjectives;
    this.lowerBound = new double[numberOfVariables];
    this.upperBound = new double[numberOfVariables];
  }

  public int getNumberOfVariables() {
    return numberOfVariables;
  }

  public int getNumberOfObjectives() {
    return numberOfObjectives;
  }

  public double getLowerBound(int i) {
    return lowerBound[i];
  }

  public double getUpperBound(int i) {
    return upperBound[i];
  }

  public abstract Solutions<V> newRandomSetOfSolutions(int size);

  public void evaluate(Solutions<V> solutions) {
    for(Solution<V> solution : solutions) {
      evaluate(solution);
    }
  }
  
  public abstract void evaluate(Solution<V> solution);

  @Override
  public abstract Problem<V> clone();
}
