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
package hero.core.operator.selection;

import java.util.Comparator;

import hero.core.operator.comparator.SolutionDominance;
import hero.core.problem.Solution;
import hero.core.problem.Solutions;
import hero.core.problem.Variable;
import hero.core.util.random.RandomGenerator;

public class BinaryTournament<T extends Variable<?>> extends SelectionOperator<T> {

    protected Comparator<Solution<T>> comparator;

    public BinaryTournament(Comparator<Solution<T>> comparator) {
        this.comparator = comparator;
    } // BinaryTournament

    public BinaryTournament() {
        this(new SolutionDominance<T>());
    } // Constructor

    public Solutions<T> execute(Solutions<T> solutions) {
        Solutions<T> result = new Solutions<T>();
        Solution<T> s1, s2;
        s1 = solutions.get(RandomGenerator.nextInt(0, solutions.size()));
        s2 = solutions.get(RandomGenerator.nextInt(0, solutions.size()));

        int flag = comparator.compare(s1, s2);
        if (flag == -1) {
            result.add(s1);
        } else if (flag == 1) {
            result.add(s2);
        } else if (RandomGenerator.nextDouble() < 0.5) {
            result.add(s1);
        } else {
            result.add(s2);
        }
        return result;
    } // execute
} // BinaryTournament

