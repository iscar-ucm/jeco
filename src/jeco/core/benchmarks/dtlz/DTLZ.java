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

import jeco.core.problem.Problem;
import jeco.core.problem.Solution;
import jeco.core.problem.Solutions;
import jeco.core.problem.Variable;
import jeco.core.util.random.RandomGenerator;

/**
 * Abstract class for DTLZ problems
 */
public abstract class DTLZ extends Problem<Variable<Double>> {

	/**
	 * Constructor
	 * @param numberOfVariables Number of variables
	 */
    public DTLZ(Integer numberOfVariables) {
        super(numberOfVariables, 3);
    }

	@Override
  	public Solutions<Variable<Double>> newRandomSetOfSolutions(int size) {
  		Solutions<Variable<Double>> solutions = new Solutions<Variable<Double>>();
  		for (int i=0; i<size; ++i) {
  			Solution<Variable<Double>> solI = new Solution<Variable<Double>>(numberOfObjectives);
  			for (int j = 0; j < numberOfVariables; ++j) {
  				Variable<Double> varJ = new Variable<Double>(RandomGenerator.nextDouble(lowerBound[j], upperBound[j]));
  				solI.getVariables().add(varJ);
  			}
  			solutions.add(solI);
  		}
  		return solutions;
  	}
  	
	@Override
    public void evaluate(Solutions<Variable<Double>> solutions) {
    	for(Solution<Variable<Double>> solution : solutions)
    		this.evaluate(solution);
    }

	/**
	 * Evaluate a solution
	 * @param solution Solution to evaluate
	 */
    public abstract void evaluate(Solution<Variable<Double>> solution);        
}
