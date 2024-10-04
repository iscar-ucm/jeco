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
package jeco.core.operator.selection;

import java.util.Comparator;

import jeco.core.operator.comparator.SolutionDominance;
import jeco.core.problem.Solution;
import jeco.core.problem.Solutions;
import jeco.core.problem.Variable;
import jeco.core.util.random.RandomGenerator;

/**
 * BinaryTournament is a selection operator that selects the best individual
 * from a pair of individuals
 * @param <T> Variable type
 */
public class BinaryTournament<T extends Variable<?>> extends SelectionOperator<T> {

    /**
     * Comparator used to compare individuals
     */
    protected Comparator<Solution<T>> comparator;

    /**
     * Creates a new instance of BinaryTournament
     * @param comparator Comparator
     */
    public BinaryTournament(Comparator<Solution<T>> comparator) {
        this.comparator = comparator;
    } // BinaryTournament

    /**
     * Creates a new instance of BinaryTournament
     */
    public BinaryTournament() {
        this(new SolutionDominance<T>());
    } // Constructor

    @Override
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

