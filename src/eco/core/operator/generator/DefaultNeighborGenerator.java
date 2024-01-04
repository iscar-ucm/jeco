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


package eco.core.operator.generator;

import eco.core.problem.Problem;
import eco.core.problem.Solution;
import eco.core.problem.Solutions;
import eco.core.problem.Variable;

public class DefaultNeighborGenerator<V extends Variable<?>> extends NeighborGenerator<V> {
    protected Problem<V> problem;

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
