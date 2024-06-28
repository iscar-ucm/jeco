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
 * DTLZ2 problem
 * 
 * This class represents the DTLZ2 problem. It is a subclass of the DTLZ class.
 * The DTLZ2 problem has the following properties:
 * - Number of variables: 12
 * - Number of objectives: 3
 * - Bounds for variables: [0, 1]
 * - Type of variables: real
 * - Pareto front: convex
 * - Pareto set: convex
 * 
 * The DTLZ2 problem is defined as follows:
 * - Minimize f1, f2, f3
 * - f1 = (1 + g) * cos(x1 * pi / 2) * cos(x2 * pi / 2) * ... * cos(x_{numberOfObjectives-1} * pi / 2)
 * - f2 = (1 + g) * cos(x1 * pi / 2) * cos(x2 * pi / 2) * ... * sin(x_{numberOfObjectives-1} * pi / 2)
 * - f3 = (1 + g) * cos(x1 * pi / 2) * cos(x2 * pi / 2) * ... * sin(x_{numberOfObjectives-2} * pi / 2)
 * - g = sum_{i=numberOfVariables-k+1}^{numberOfVariables} (x_i - 0.5)^2
 * - k = numberOfVariables - numberOfObjectives + 1
 * - x_i in [0, 1]
 * - i = 1, 2, ..., numberOfVariables
 * 
 */
public class DTLZ2 extends DTLZ {

    /**
     * Constructor
     * @param numberOfVariables Number of variables
     */
    public DTLZ2(Integer numberOfVariables) {
        super(numberOfVariables);
        for (int i = 0; i < numberOfVariables; i++) {
            lowerBound[i] = 0.0;
            upperBound[i] = 1.0;
        }
    } // DTLZ2

    /**
     * Constructor
     */
    public DTLZ2() {
        this(12);
    }

    @Override
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
    
    @Override
    public DTLZ2 clone() {
    	DTLZ2 clone = new DTLZ2(this.numberOfVariables);
    	for(int i=0; i<numberOfVariables; ++i) {
    		clone.lowerBound[i] = lowerBound[i];
    		clone.upperBound[i] = upperBound[i];
    	}
    	return clone;
    }

}
