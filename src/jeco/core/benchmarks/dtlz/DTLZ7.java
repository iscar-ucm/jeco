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
package jeco.core.benchmarks.dtlz;

import java.util.ArrayList;

import jeco.core.problem.Solution;
import jeco.core.problem.Variable;

/**
 * DTLZ7 problem
 * 
 * This class represents the DTLZ7 problem. It is a subclass of the DTLZ class.
 * The DTLZ7 problem has the following properties:
 * - Number of variables: 22
 * - Number of objectives: 3
 * - Bounds for variables: [0, 1]
 * - Type of variables: real
 * - Pareto front: convex
 * - Pareto set: convex
 * 
 * The DTLZ7 problem is defined as follows:
 * - Minimize f1, f2, f3
 * - f1 = x1
 * - f2 = x2
 * - f3 = (1 + g) * h
 * - g = 1 + 9 * sum_{i=numberOfVariables-k+1}^{numberOfVariables} x_i / k
 * - h = numberOfObjectives - sum_{i=1}^{numberOfObjectives-1} x_i / (1 + g) * (1 + sin(3 * pi * x_i))
 * - k = numberOfVariables - numberOfObjectives + 1
 * - x_i in [0, 1]
 * - i = 1, 2, ..., numberOfVariables
 * 
 */
public class DTLZ7 extends DTLZ {

    /**
     * Constructor
     * @param numberOfVariables Number of variables
     */
    public DTLZ7(Integer numberOfVariables) {
        super(numberOfVariables);
        for (int i = 0; i < numberOfVariables; i++) {
            lowerBound[i] = 0.0;
            upperBound[i] = 1.0;
        }
    } // DTLZ7

    /**
     * Constructor
     */
    public DTLZ7() {
        this(22);
    }

    @Override
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
    
    @Override
    public DTLZ7 clone() {
    	DTLZ7 clone = new DTLZ7(this.numberOfVariables);
    	for(int i=0; i<numberOfVariables; ++i) {
    		clone.lowerBound[i] = lowerBound[i];
    		clone.upperBound[i] = upperBound[i];
    	}
    	return clone;
    }

}
