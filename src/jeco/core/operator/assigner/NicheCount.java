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
package jeco.core.operator.assigner;

import java.util.Collections;

import jeco.core.operator.comparator.ObjectiveComparator;
import jeco.core.problem.Solution;
import jeco.core.problem.Solutions;
import jeco.core.problem.Variable;

public class NicheCount<V extends Variable<?>> {

	protected int numberOfObjectives;
	public static final String propertyNicheCount = "nicheCount";

	public NicheCount(int numberOfObjectives) {
		this.numberOfObjectives = numberOfObjectives;
	}

	public Solutions<V> execute(Solutions<V> solutions) {

		int size = solutions.size();
		if (size == 0) {
			return solutions;
		}

		if (size == 1) {
			solutions.get(0).getProperties().put(propertyNicheCount, 0);
			return solutions;
		} // if

		for (int i = 0; i < size; ++i) {
			solutions.get(i).getProperties().put(propertyNicheCount, 0);
		}

		double maxObjective;
		double minObjective;
		// TODO: Investigate how to compute sigmaShare in 3-objectives and more.
		double sigmaShare = 0.0;

		for (int i = 0; i < numberOfObjectives; ++i) {
			// Sort the population by objective i
			Collections.sort(solutions, new ObjectiveComparator<V>(i));
			minObjective = solutions.get(0).getObjectives().get(i);
			maxObjective = solutions.get(size - 1).getObjectives().get(i);
			sigmaShare += (maxObjective - minObjective);
		}
		sigmaShare /= (size - 1);

		double distanceIJ;
		for (int i = 0; i < size; ++i) {
			Solution<V> solI = solutions.get(i);
			for (int j = 0; j < size; ++j) {
				if (i == j) {
					continue;
				}
				Solution<V> solJ = solutions.get(j);
				distanceIJ = 0;
				for(int m = 0; m<numberOfObjectives; ++m) {
					distanceIJ += Math.pow(solI.getObjectives().get(m) - solJ.getObjectives().get(m), 2);
				}
				distanceIJ = Math.sqrt(distanceIJ);
				if (distanceIJ < sigmaShare) {
					solI.getProperties().put(propertyNicheCount, solI.getProperties().get(propertyNicheCount).intValue() + 1);
				}
			}
		}

		return solutions;
	}
}
