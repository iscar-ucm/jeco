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
 * DTLZ3 problem
 * 
 * This class represents the DTLZ3 problem. It is a subclass of the DTLZ class.
 * The DTLZ3 problem has the following properties:
 * - Number of variables: 12
 * - Number of objectives: 3
 * - Bounds for variables: [0, 1]
 * - Type of variables: real
 * - Pareto front: convex
 * - Pareto set: convex
 * 
 * The DTLZ3 problem is defined as follows:
 * - Minimize f1, f2, f3
 * - f1 = (1 + g) * cos(x1 * pi / 2) * cos(x2 * pi / 2) * ... * cos(xM-2 * pi / 2)
 * - f2 = (1 + g) * cos(x1 * pi / 2) * cos(x2 * pi / 2) * ... * sin(xM-2 * pi / 2)
 * - f3 = (1 + g) * cos(x1 * pi / 2) * cos(x2 * pi / 2) * ... * sin(xM-2 * pi / 2)
 * - g = 100 * (k + sum_{i=numberOfVariables-k+1}^{numberOfVariables} (x_i - 0.5)^2 - cos(20 * pi * (x_i - 0.5)))
 * - k = numberOfVariables - numberOfObjectives + 1
 * - x_i in [0, 1]
 * - i = 1, 2, ..., numberOfVariables
 * 
 */
public class DTLZ3 extends DTLZ {

    /**
     * Constructor
     * @param numberOfVariables Number of variables
     */
    public DTLZ3(Integer numberOfVariables) {
        super(numberOfVariables);
        for (int i = 0; i < numberOfVariables; i++) {
            lowerBound[i] = 0.0;
            upperBound[i] = 1.0;
        }
    } // DTLZ3

    /**
     * Constructor
     */
    public DTLZ3() {
        this(12);
    }

    @Override
    public void evaluate(Solution<Variable<Double>> solution) {
        ArrayList<Variable<Double>> variables = solution.getVariables();

        double[] x = new double[numberOfVariables];
        double[] f = new double[numberOfObjectives];
        int k = numberOfVariables - numberOfObjectives + 1;

        for (int i = 0; i < numberOfVariables; i++) {
            x[i] = variables.get(i).getValue();
        }

        double g = 0.0;
        for (int i = numberOfVariables - k; i < numberOfVariables; i++) {
            g += (x[i] - 0.5) * (x[i] - 0.5) - Math.cos(20.0 * Math.PI * (x[i] - 0.5));
        }

        g = 100.0 * (k + g);
        for (int i = 0; i < numberOfObjectives; i++) {
            f[i] = 1.0 + g;
        }

        for (int i = 0; i < numberOfObjectives; i++) {
            for (int j = 0; j < numberOfObjectives - (i + 1); j++) {
                f[i] *= java.lang.Math.cos(x[j] * 0.5 * java.lang.Math.PI);
            }
            if (i != 0) {
                int aux = numberOfObjectives - (i + 1);
                f[i] *= java.lang.Math.sin(x[aux] * 0.5 * java.lang.Math.PI);
            } // if
        } //for

        for (int i = 0; i < numberOfObjectives; i++) {
            solution.getObjectives().set(i, f[i]);
        }
    }
    
    @Override
    public DTLZ3 clone() {
    	DTLZ3 clone = new DTLZ3(this.numberOfVariables);
    	for(int i=0; i<numberOfVariables; ++i) {
    		clone.lowerBound[i] = lowerBound[i];
    		clone.upperBound[i] = upperBound[i];
    	}
    	return clone;
    }

}
