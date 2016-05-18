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
package hero.algorithm.metaheuristic.moge;

import java.util.logging.Logger;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;


import hero.problem.Solution;
import hero.problem.Solutions;
import hero.problem.Variable;
import hero.util.logger.HeroLogger;

/**
 * Example
 * Pease note that using the Script Engine is too slow.
 * We recommend using an external evaluation library like JEval (sourceforge).
 * @author José Luis Risco Martín
 *
 */
public class GrammaticalEvolution_example extends AbstractProblemGE {
	private static final Logger logger = Logger.getLogger(GrammaticalEvolution_example.class.getName());

	protected ScriptEngine evaluator = null;
	protected double[] func = {0, 4, 30, 120, 340, 780, 1554}; //x^4+x^3+x^2+x

	public GrammaticalEvolution_example(String pathToBnf) {
		super(pathToBnf);
		ScriptEngineManager mgr = new ScriptEngineManager();
		evaluator = mgr.getEngineByName("JavaScript");
	}

	public void evaluate(Solution<Variable<Integer>> solution, Phenotype phenotype) {
		String originalFunction = phenotype.toString();
		double error, totError = 0, maxError = Double.NEGATIVE_INFINITY;
		for(int i=0; i<func.length; ++i) {
			String currentFunction = originalFunction.replaceAll("X", String.valueOf(i));	
			double funcI;
			try {
				String aux = evaluator.eval(currentFunction).toString();
				if(aux.equals("NaN"))
					funcI = Double.POSITIVE_INFINITY;
				else
					funcI = Double.valueOf(aux);
			} catch (NumberFormatException e) {
				logger.severe(e.getLocalizedMessage());
				funcI = Double.POSITIVE_INFINITY;
			} catch (ScriptException e) {
				logger.severe(e.getLocalizedMessage());
				funcI = Double.POSITIVE_INFINITY;
			}
			error = Math.abs(funcI-func[i]);
			totError += error;
			if(error>maxError)
				maxError = error;
		}
		solution.getObjectives().set(0, maxError);
		solution.getObjectives().set(1, totError);
	}	

  @Override
  public GrammaticalEvolution_example clone() {
  	GrammaticalEvolution_example clone = new GrammaticalEvolution_example(super.pathToBnf);
  	return clone;
  }

  public static void main(String[] args) {
	  HeroLogger.setup();
		// First create the problem
		GrammaticalEvolution_example problem = new GrammaticalEvolution_example("test/grammar_example.bnf");
		// Second create the algorithm
		GrammaticalEvolution algorithm = new GrammaticalEvolution(problem, 100, 1000);
		algorithm.initialize();
		Solutions<Variable<Integer>> solutions = algorithm.execute();
		for (Solution<Variable<Integer>> solution : solutions) {
			logger.info("Fitness = (" + solution.getObjectives().get(0) + ", " + solution.getObjectives().get(1) + ")");
			logger.info("Phenotype = (" + problem.generatePhenotype(solution).toString() + ")");
		}
	}		

}
