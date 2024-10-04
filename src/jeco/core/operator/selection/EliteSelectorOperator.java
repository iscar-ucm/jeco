/*
 * Copyright (C) 2010 José Luis Risco Martín <jlrisco@ucm.es>
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

import java.util.Collections;

import jeco.core.operator.comparator.SolutionDominance;
import jeco.core.problem.Solution;
import jeco.core.problem.Solutions;
import jeco.core.problem.Variable;

/**
 * EliteSelectorOperator selects the best individuals from the population.
 * To this end, it sorts the population according to the dominance relation
 * and selects the best individuals.
 * 
 * @param <T> Variable type
 */
public class EliteSelectorOperator<T extends Variable<?>> extends SelectionOperator<T> {

    /**
     * Default number of elite individuals
     */
    public static final int DEFAULT_ELITE_SIZE = 10;
    /**
     * Number of elite individuals
     */
    protected int eliteSize;

    /**
     * Creates a new instance of EliteSelectorOperator
     * @param eliteSize number of elite individuals
     */
    public EliteSelectorOperator(int eliteSize) {
        this.eliteSize = eliteSize;
    }

    /**
     * Creates a new instance of EliteSelectorOperator
     */
    public EliteSelectorOperator() {
        this(DEFAULT_ELITE_SIZE);
    }

    @Override
    public Solutions<T> execute(Solutions<T> arg) {
        Solutions<T> solutions = new Solutions<T>();
        solutions.addAll(arg);

        SolutionDominance<T> comparator = new SolutionDominance<T>();
        Collections.sort(solutions, comparator);
        int popSize = solutions.size();

        Solutions<T> eliteSolutions = new Solutions<T>();
        int index = 0;
        while (index < eliteSize && index < popSize) {
            Solution<T> solution = solutions.get(index).clone();
            eliteSolutions.add(solution);
            index++;
        }
        eliteSolutions.reduceToNonDominated(comparator);
        return eliteSolutions;
    }
}
