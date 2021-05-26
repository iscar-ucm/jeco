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
package eco.core.benchmarks.dtlz;

import java.util.ArrayList;

import eco.core.problem.Solution;
import eco.core.problem.Variable;

public class DTLZ2 extends DTLZ {

    public DTLZ2(Integer numberOfVariables) {
        super(numberOfVariables);
        for (int i = 0; i < numberOfVariables; i++) {
            lowerBound[i] = 0.0;
            upperBound[i] = 1.0;
        }
    } // DTLZ2

    public DTLZ2() {
        this(12);
    }

    public void evaluate(Solution<Variable<Double>> solution) {
        ArrayList<Variable<Double>> variables = solution.getVariables();
        int k = numberOfVariables - numberOfObjectives + 1;
        double f = 0, g = 0;
        for (int i = numberOfVariables - k + 1; i <= numberOfVariables; ++i) {
            g += Math.pow(variables.get(i-1).getValue() - 0.5, 2);
        }

        for (int i = 1; i <= numberOfObjectives; i++) {
            f = (1 + g);
            for (int j = numberOfObjectives - i; j >= 1; j--) {
                f *= Math.cos(variables.get(j-1).getValue() * Math.PI / 2);
            }

            if (i > 1) {
                f *= Math.sin(variables.get(numberOfObjectives-i).getValue() * Math.PI / 2);
            }

            solution.getObjectives().set(i - 1, f);
        } // for
    }
    
    public DTLZ2 clone() {
    	DTLZ2 clone = new DTLZ2(this.numberOfVariables);
    	for(int i=0; i<numberOfVariables; ++i) {
    		clone.lowerBound[i] = lowerBound[i];
    		clone.upperBound[i] = upperBound[i];
    	}
    	return clone;
    }

}
