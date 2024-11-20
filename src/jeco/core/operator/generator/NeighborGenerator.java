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

/**
 * Class designed to generate neighbors, initially for Tabu Search, 
 * but it can be used for other algorithms.
 * 
 * This class is abstract because it needs to be extended to implement
 * the execute method.
 */
public abstract class NeighborGenerator<V extends Variable<?>> {
    /** The number of neighbors to generate */
    protected Integer neighborsSize;

    /**
     * Constructor
     * @param neighborsSize The number of neighbors to generate
     */
    public NeighborGenerator(Integer neighborsSize) {
        this.neighborsSize = neighborsSize;
    }

    /**
     * Get the number of neighbors to generate
     * @return The number of neighbors to generate
     */
    public Integer getNeighborsSize() {
        return neighborsSize;        
    }

    /**
     * Set the number of neighbors to generate
     * @param neighborsSize The number of neighbors to generate
     */
    public void setNeighborsSize(Integer neighborsSize) {
        this.neighborsSize = neighborsSize;
    }

    /**
     * Method to generate neighbors
     * @param solution The solution to generate neighbors from
     * @return A set of neighbors
     */
    abstract public Solutions<V> execute(Solution<V> solution);
}
