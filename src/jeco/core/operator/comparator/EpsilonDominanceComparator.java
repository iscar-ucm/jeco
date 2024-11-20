/*
* File: EpsilonDominanceComparator.java
* Author: José Luis Risco Martín <jlrisco@ucm.es>
* Created: 2010/09/09 (YYYY/MM/DD)
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

package jeco.core.operator.comparator;

import java.util.ArrayList;
import java.util.Comparator;

import jeco.core.problem.Solution;
import jeco.core.problem.Variable;

/**
 * Compares two solutions according to the epsilon-dominance relation. A solution
 * dominates another if it is better in all objectives, and it is at least
 * epsilon better in one of them.
 * 
 * @param <V> Type of the variables of the solutions.
 */
public class EpsilonDominanceComparator<V extends Variable<?>> implements Comparator<Solution<V>> {

	private double eta;

	public EpsilonDominanceComparator(double eta) {
		this.eta = eta;
	}

	public int compare(Solution<V> s1, Solution<V> s2) {
		int dominate1; // dominate1 indicates if some objective of solution1
		// dominates the same objective in solution2. dominate2
		int dominate2; // is the complementary of dominate1.

		dominate1 = 0;
		dominate2 = 0;

		int flag;
		double value1, value2;
		// Idem number of violated constraint. Apply a dominance Test
		int n = Math.min(s1.getObjectives().size(), s2.getObjectives().size());
		ArrayList<Double> z1 = s1.getObjectives();
		ArrayList<Double> z2 = s2.getObjectives();
		for (int i = 0; i < n; i++) {
			value1 = z1.get(i);
			value2 = z2.get(i);

			if (value1 / (1 + eta) < value2) {
				flag = -1;
			} else if (value1 / (1 + eta) > value2) {
				flag = 1;
			} else {
				flag = 0;
			}

			if (flag == -1) {
				dominate1 = 1;
			}
			if (flag == 1) {
				dominate2 = 1;
			}
		}

		if (dominate1 == dominate2) {
			return 0; // No one dominates the other
		}
		if (dominate1 == 1) {
			return -1; // solution1 dominates
		}
		return 1;    // solution2 dominates
	} // compare
} // EpsilonDominanceComparator

