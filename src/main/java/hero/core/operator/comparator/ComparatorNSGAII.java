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
package hero.core.operator.comparator;

import java.util.Comparator;

import hero.core.operator.assigner.CrowdingDistance;
import hero.core.operator.assigner.FrontsExtractor;
import hero.core.problem.Solution;
import hero.core.problem.Variable;

public class ComparatorNSGAII<V extends Variable<?>> implements Comparator<Solution<V>> {

	public ComparatorNSGAII() {
	}

	public int compare(Solution<V> left, Solution<V> right) {
		Integer rankLeft = left.getProperties().get(FrontsExtractor.propertyRank).intValue();
		Integer rankRight = right.getProperties().get(FrontsExtractor.propertyRank).intValue();

		int comp = rankLeft.compareTo(rankRight);
		if (comp == -1) {
			return -1;
		} else if (comp == 0) {
			Double crowdedDistanceLeft = left.getProperties().get(CrowdingDistance.propertyCrowdingDistance).doubleValue();
			Double crowdedDistanceRight = right.getProperties().get(CrowdingDistance.propertyCrowdingDistance).doubleValue();
			comp = crowdedDistanceLeft.compareTo(crowdedDistanceRight);
			if (comp == 1) {
				return -1;
			} else if (comp == 0) {
				return 0;
			} else {
				return 1;
			}
		} else {
			return 1;
		}
	}
}
