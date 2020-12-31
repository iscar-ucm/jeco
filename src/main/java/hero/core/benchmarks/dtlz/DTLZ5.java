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
package hero.core.benchmarks.dtlz;

import java.util.ArrayList;

import hero.core.problem.Solution;
import hero.core.problem.Variable;

public class DTLZ5 extends DTLZ {

    public DTLZ5(Integer numberOfVariables) {
        super(numberOfVariables);
        for (int i = 0; i < numberOfVariables; i++) {
            lowerBound[i] = 0.0;
            upperBound[i] = 1.0;
        }
    } // DTLZ5

    public DTLZ5() {
        this(12);
    }

    public void evaluate(Solution<Variable<Double>> solution) {
        double[] theta = new double[numberOfObjectives];
        int k = numberOfVariables - numberOfObjectives + 1; // For 3-objective test functions

        double g = 0;
        ArrayList<Variable<Double>> variables = solution.getVariables();
        for (int i = numberOfVariables - k + 1; i <= numberOfVariables; i++) {
            g += Math.pow(variables.get(i - 1).getValue() - 0.5, 2);
        }

        double t = Math.PI / (4.0 * (1.0 + g));
        theta[0] = variables.get(0).getValue() * Math.PI / 2.0;

        for (int i = 2; i <= (numberOfObjectives - 1); i++) {
            theta[i - 1] = t * (1.0 + 2.0 * g * variables.get(i - 1).getValue());
        }

        for (int i = 1; i <= numberOfObjectives; i++) {
            double f = (1 + g);
            for (int j = numberOfObjectives - i; j >= 1; j--) {
                f *= Math.cos(theta[j - 1]);
            }
            if (i > 1) {
                f *= Math.sin(theta[numberOfObjectives - i]);
            }
            solution.getObjectives().set(i - 1, f);
        } // for
    }
    
    public DTLZ5 clone() {
    	DTLZ5 clone = new DTLZ5(this.numberOfVariables);
    	for(int i=0; i<numberOfVariables; ++i) {
    		clone.lowerBound[i] = lowerBound[i];
    		clone.upperBound[i] = upperBound[i];
    	}
    	return clone;
    }
}
