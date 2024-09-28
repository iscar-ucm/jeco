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

import jeco.core.problem.Solution;
import jeco.core.problem.Solutions;
import jeco.core.problem.Variable;
import jeco.core.util.random.RandomGenerator;

/**
 * Class designed to generate neighbors. It swaps two variables in the solution. Thus, it generates only one neighbor.
 * 
 * @param <V> Variable type.
 */
public class SwapNeighborGenerator<V extends Variable<?>> extends NeighborGenerator<V> {
    public SwapNeighborGenerator(Integer neighborsSize) {
        super(neighborsSize);
    }

    @Override
    public Solutions<V> execute(Solution<V> solution) {
        Solutions<V> neighbors = new Solutions<>();
        for(int i=0; i<neighborsSize; ++i) {
            Solution<V> tmp = solution.clone();
            int pos1 = RandomGenerator.nextInt(tmp.getVariables().size());
            int pos2 = RandomGenerator.nextInt(tmp.getVariables().size());
            while(pos1 == pos2) {
                pos2 = RandomGenerator.nextInt(tmp.getVariables().size());
            }
            V var1 = tmp.getVariables().get(pos1);
            V var2 = tmp.getVariables().get(pos2);
            tmp.getVariables().set(pos1, var2);
            tmp.getVariables().set(pos2, var1);
            neighbors.add(tmp);
        }
        return neighbors;
    }
}
