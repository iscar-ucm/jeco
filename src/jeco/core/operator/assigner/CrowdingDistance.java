/*
* File: CrowdingDistance.java
* Author: José Luis Risco Martín <jlrisco@ucm.es>
* Created: 2010/07/30 (YYYY/MM/DD)
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

package jeco.core.operator.assigner;

import java.util.Collections;

import jeco.core.operator.comparator.ObjectiveComparator;
import jeco.core.problem.Solutions;
import jeco.core.problem.Variable;

/**
 * Assigns the crowding distance to the solutions in the population.
 * The crowding distance is calculated as the sum of the distances between the
 * solutions in the population in each objective.
 * The crowding distance is stored in the property "crowdingDistance" of the
 * solutions.
 * 
 */
public class CrowdingDistance<V extends Variable<?>> {

    /**
     * Number of objectives of the problem.
     */
    protected int numberOfObjectives;
    /**
     * Property name for the crowding distance.
     */
    public static final String propertyCrowdingDistance = "crowdingDistance";

    /**
     * Constructor.
     * @param numberOfObjectives Number of objectives of the problem.
     */
    public CrowdingDistance(int numberOfObjectives) {
        this.numberOfObjectives = numberOfObjectives;
    }

    /**
     * Assigns the crowding distance to the solutions in the population.
     * The crowding distance is calculated as the sum of the distances between the
     * solutions in the population in each objective.
     * The crowding distance is stored in the property "crowdingDistance" of the
     * solutions.
     * @param arg Population of solutions.
     * @return Population of solutions with the crowding distance assigned.
     */
    public Solutions<V> execute(Solutions<V> arg) {
        Solutions<V> solutions = new Solutions<V>();
        solutions.addAll(arg);

        int size = solutions.size();
        if (size == 0) {
            return solutions;
        }

        if (size == 1) {
            solutions.get(0).getProperties().put(propertyCrowdingDistance, Double.POSITIVE_INFINITY);
            return solutions;
        } // if

        if (size == 2) {
            solutions.get(0).getProperties().put(propertyCrowdingDistance, Double.POSITIVE_INFINITY);
            solutions.get(1).getProperties().put(propertyCrowdingDistance, Double.POSITIVE_INFINITY);
            return solutions;
        } // if

        for (int i = 0; i < size; ++i) {
            solutions.get(i).getProperties().put(propertyCrowdingDistance, 0.0);
        }

        double objetiveMaxn;
        double objetiveMinn;
        double distance = 0.0;

        for (int i = 0; i < numberOfObjectives; ++i) {
            // Sort the population by objective i
            Collections.sort(solutions, new ObjectiveComparator<V>(i));
            objetiveMinn = solutions.get(0).getObjectives().get(i);
            objetiveMaxn = solutions.get(size - 1).getObjectives().get(i);

            //Set the crowding distance
            solutions.get(0).getProperties().put(propertyCrowdingDistance, Double.POSITIVE_INFINITY);
            solutions.get(size - 1).getProperties().put(propertyCrowdingDistance, Double.POSITIVE_INFINITY);

            for (int j = 1; j < size - 1; j++) {
                distance = solutions.get(j + 1).getObjectives().get(i) - solutions.get(j - 1).getObjectives().get(i);
                distance = distance / (objetiveMaxn - objetiveMinn);
                distance += solutions.get(j).getProperties().get(propertyCrowdingDistance).doubleValue();
                solutions.get(j).getProperties().put(propertyCrowdingDistance, distance);
            } // for
        } // for

        return solutions;
    }
}
