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
package hero.problems.dtlz;

import java.util.ArrayList;

import hero.problem.Solution;
import hero.problem.Variable;

public class DTLZ7 extends DTLZ {

    public DTLZ7(Integer numberOfVariables) {
        super(numberOfVariables);
        for (int i = 0; i < numberOfVariables; i++) {
            lowerBound[i] = 0.0;
            upperBound[i] = 1.0;
        }
    } // DTLZ7

    public DTLZ7() {
        this(22);
    }

    public void evaluate(Solution<Variable<Double>> solution) {
        int k = numberOfVariables - numberOfObjectives + 1;

        double g = 0.0;
        ArrayList<Variable<Double>> variables = solution.getVariables();
        for (int i = numberOfVariables - k + 1; i <= numberOfVariables; i++) {
            g += variables.get(i - 1).getValue();
        }
        g = 1.0 + 9.0 * g / k;

        for (int i = 1; i <= numberOfObjectives - 1; i++) {
            solution.getObjectives().set(i - 1, variables.get(i - 1).getValue());
        }

        double h = 0.0;
        double xJ_1 = 0.0;
        for (int j = 1; j <= numberOfObjectives - 1; j++) {
            xJ_1 = variables.get(j - 1).getValue();
            h += xJ_1 / (1.0 + g) * (1.0 + Math.sin(3.0 * Math.PI * xJ_1));
        }

        h = numberOfObjectives - h;
        solution.getObjectives().set(numberOfObjectives - 1, (1 + g) * h);
    }
    
    public DTLZ7 clone() {
    	DTLZ7 clone = new DTLZ7(this.numberOfVariables);
    	for(int i=0; i<numberOfVariables; ++i) {
    		clone.lowerBound[i] = lowerBound[i];
    		clone.upperBound[i] = upperBound[i];
    	}
    	return clone;
    }

}
