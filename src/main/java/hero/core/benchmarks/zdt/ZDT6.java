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
package hero.core.benchmarks.zdt;

import java.util.ArrayList;
import java.util.logging.Logger;

import hero.core.problem.Solution;
import hero.core.problem.Solutions;
import hero.core.problem.Variable;

public class ZDT6 extends ZDT {

    private static final Logger logger = Logger.getLogger(ZDT6.class.getName());

    public ZDT6(Integer numberOfVariables) {
        super(numberOfVariables);
        for (int i = 0; i < numberOfVariables; i++) {
            lowerBound[i] = 0.0;
            upperBound[i] = 1.0;
        }
    } // ZDT1

    public ZDT6() {
        this(10);
    }

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

    public Solutions<Variable<Double>> computeParetoOptimalFront(int n) {
        // TODO: Finish this function
        logger.severe("This function is not finished");
        return null;
    }
    public ZDT6 clone() {
    	ZDT6 clone = new ZDT6(this.numberOfVariables);
    	for(int i=0; i<numberOfVariables; ++i) {
    		clone.lowerBound[i] = lowerBound[i];
    		clone.upperBound[i] = upperBound[i];
    	}
    	return clone;
    }
}
