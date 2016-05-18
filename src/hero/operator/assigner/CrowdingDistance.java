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
package hero.operator.assigner;

import java.util.Collections;

import hero.operator.comparator.ObjectiveComparator;
import hero.problem.Solutions;
import hero.problem.Variable;

public class CrowdingDistance<V extends Variable<?>> {

    protected int numberOfObjectives;
    public static final String propertyCrowdingDistance = "crowdingDistance";

    public CrowdingDistance(int numberOfObjectives) {
        this.numberOfObjectives = numberOfObjectives;
    }

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
