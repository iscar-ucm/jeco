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
package hero.algorithm;

import hero.problem.Problem;
import hero.problem.Solutions;
import hero.problem.Variable;

/**
 *
 * @author José L. Risco-Martín
 * @param <V>
 *
 */
public abstract class Algorithm<V extends Variable<?>> {

  protected Problem<V> problem = null;
  
  public Algorithm(Problem<V> problem) {
    this.problem = problem;
  }

  public void setProblem(Problem<V> problem) {
    this.problem = problem;
  }

  public abstract void initialize();

  public abstract void step();

  public abstract Solutions<V> execute();
}
