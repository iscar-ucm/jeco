/*
* Copyright (C) 2024 José Luis Risco Martín <jlrisco@ucm.es>
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


package jeco.core.operator.generator;

import jeco.core.problem.Problem;
import jeco.core.problem.Solution;
import jeco.core.problem.Solutions;
import jeco.core.problem.Variable;

/**
 * Default neighbor generator. It generates a set of random solutions.
 * @param <V> Variable type.
 */
public class DefaultNeighborGenerator<V extends Variable<?>> extends NeighborGenerator<V> {
    protected Problem<V> problem;

    /**
     * Constructor
     * @param problem The problem
     * @param neighborsSize The number of neighbors to generate
     */
    public DefaultNeighborGenerator(Problem<V> problem, Integer neighborsSize) {
        super(neighborsSize);
        this.problem = problem;
    }

    @Override
    public Solutions<V> execute(Solution<V> solution) {
        Solutions<V> neighbors = problem.newRandomSetOfSolutions(neighborsSize);
        return neighbors;
    }
}
