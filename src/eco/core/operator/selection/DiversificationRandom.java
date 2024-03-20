/*
* File: Diversification.java
* Author: José Luis Risco Martín <jlrisco@ucm.es>
* Created: 2024/03/20 (YYYY/MM/DD)
*
* Copyright (C) 2024
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
package eco.core.operator.selection;

import eco.core.problem.Solutions;
import eco.core.problem.Variable;
import eco.core.util.random.RandomGenerator;

/**
 * Random diversification operator.
 * @param <T> Type of the variables of the solutions.
 * 
 * This operator is designed to be used in metaheuristics that require 
 * a diversification mechanism, like Scatter Search or Genetic Algorithms.
 */
public class DiversificationRandom<T extends Variable<?>> extends SelectionOperator<T> {

    /**
     * Default diversification ratio, as a percentage of the population size.
     */
    public static final Double DEFAULT_DIVERSIFICATION_RATIO = 0.1;
    /**
     * Size of the diversification set.
     */
    protected Integer diversificationSize = null;

    /**
     * Constructor.
     * @param diversificationSize Size of the diversification set.
     */
    public DiversificationRandom(Integer diversificationSize) {
        this.diversificationSize = diversificationSize;
    }

    /**
     * Constructor.
     */
    public DiversificationRandom() {
    }

    @Override
    public Solutions<T> execute(Solutions<T> solutions) {
        if (diversificationSize == null) {
            diversificationSize = (int) Math.ceil(solutions.size() * DiversificationRandom.DEFAULT_DIVERSIFICATION_RATIO);
        }
        Solutions<T> result = new Solutions<T>();
        for (int i = 0; i < diversificationSize; ++i) {
            result.add(solutions.get(RandomGenerator.nextInteger(solutions.size())).clone());
        }
        return result;
    }
}
