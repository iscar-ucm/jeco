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
package jeco.core.benchmarks.zdt;

import java.util.ArrayList;
import java.util.logging.Logger;

import jeco.core.problem.Solution;
import jeco.core.problem.Solutions;
import jeco.core.problem.Variable;

/**
 * ZDT6 benchmark problem
 * 
 * ZDT6 is a multi-objective optimization problem
 * 
 * f1(x) = 1 - exp(-4 * x1) * sin(6 * pi * x1)^6
 * f2(x) = g(x) * h(f1(x), g(x))
 * g(x) = sum(xj) / (n-1)
 * h(f1, g) = 1 - (f1 / g)^2
 * 
 * xj in [0, 1]
 * 
 * Pareto optimal front: non-convex
 */
public class ZDT6 extends ZDT {

    private static final Logger logger = Logger.getLogger(ZDT6.class.getName());

    /**
     * Constructor
     * @param numberOfVariables Number of variables
     */
    public ZDT6(Integer numberOfVariables) {
        super(numberOfVariables);
        for (int i = 0; i < numberOfVariables; i++) {
            lowerBound[i] = 0.0;
            upperBound[i] = 1.0;
        }
    } // ZDT1

    /**
     * Constructor
     */
    public ZDT6() {
        this(10);
    }

    @Override
    public void evaluate(Solution<Variable<Double>> solution) {
        ArrayList<Variable<Double>> variables = solution.getVariables();
        double x0 = variables.get(0).getValue();
        double f1 = 1.0 - Math.exp(-4 * x0) * Math.pow(Math.sin(6 * Math.PI * x0), 6);
        double g = 0;
        for (int j = 1; j < numberOfVariables; ++j) {
            g += variables.get(j).getValue();
        }
        g /= (numberOfVariables - 1);
        g = Math.pow(g, 0.25);
        g *= 9;
        g += 1;
        double h = 1 - (f1 / g) * (f1 / g);
        solution.getObjectives().set(0, f1);
        solution.getObjectives().set(1, g * h);
    }

    /**
     * Compute the Pareto optimal front
     * @param n Number of points
     * @return Pareto optimal front
     */
    public Solutions<Variable<Double>> computeParetoOptimalFront(int n) {
        // TODO: Finish this function
        logger.severe("This function is not finished");
        return null;
    }

    @Override
    public ZDT6 clone() {
    	ZDT6 clone = new ZDT6(this.numberOfVariables);
    	for(int i=0; i<numberOfVariables; ++i) {
    		clone.lowerBound[i] = lowerBound[i];
    		clone.upperBound[i] = upperBound[i];
    	}
    	return clone;
    }
}
