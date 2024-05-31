/*
* File: GrammaticalEvolutionProblem.java
* Author: José Luis Risco Martín <jlrisco@ucm.es>
* Created: 2010/05/31 (YYYY/MM/DD)
*
* Copyright (C) 2010
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
*/

package jeco.core.benchmarks;

import java.util.logging.Logger;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import jeco.core.algorithms.GrammaticalEvolution;
import jeco.core.problem.GrammaticalEvolutionAbstractProblem;
import jeco.core.problem.GrammaticalEvolutionPhenotype;
import jeco.core.problem.Solution;
import jeco.core.problem.Solutions;
import jeco.core.problem.Variable;
import jeco.core.util.logger.JecoLogger;

import java.io.File;

/**
 * This class represents a simple example for the Grammatical Evolution algorithm.
 * 
 * The problem is to find a function that fits the given data, which is the function
 * f(x) = x^4 + x^3 + x^2 + x
 * 
 * The grammar is defined in the file "lib/grammar_example.bnf".
 * The function is evaluated using the ScriptEngine class from the Java API.
 * Note that this engine is not the most efficient way to evaluate functions.
 */
public class GrammaticalEvolutionProblem extends GrammaticalEvolutionAbstractProblem {

    private static final Logger logger = Logger.getLogger(GrammaticalEvolutionProblem.class.getName());

    /**
     * The evaluator is used to evaluate the function.
     */
    protected ScriptEngine evaluator = null;
    /**
     * The data to fit the function.
     */
    protected double[] func = {0, 4, 30, 120, 340, 780, 1554}; //x^4+x^3+x^2+x

    /**
     * Constructor of the class.
     * 
     * @param pathToBnf The path to the grammar file.
     */
    public GrammaticalEvolutionProblem(String pathToBnf) {
        super(pathToBnf);
        ScriptEngineManager mgr = new ScriptEngineManager();
        evaluator = mgr.getEngineByName("JavaScript");
    }

    @Override
    public void evaluate(Solution<Variable<Integer>> solution, GrammaticalEvolutionPhenotype phenotype) {
        String originalFunction = phenotype.toString();
        double error, totError = 0, maxError = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < func.length; ++i) {
            String currentFunction = originalFunction.replaceAll("X", String.valueOf(i));
            double funcI;
            try {
                String aux = evaluator.eval(currentFunction).toString();
                if (aux.equals("NaN")) {
                    funcI = Double.POSITIVE_INFINITY;
                } else {
                    funcI = Double.valueOf(aux);
                }
            } catch (NumberFormatException e) {
                logger.severe(e.getLocalizedMessage());
                funcI = Double.POSITIVE_INFINITY;
            } catch (ScriptException e) {
                logger.severe(e.getLocalizedMessage());
                funcI = Double.POSITIVE_INFINITY;
            }
            error = Math.abs(funcI - func[i]);
            totError += error;
            if (error > maxError) {
                maxError = error;
            }
        }
        solution.getObjectives().set(0, maxError);
        solution.getObjectives().set(1, totError);
    }

    @Override
    public GrammaticalEvolutionProblem clone() {
        GrammaticalEvolutionProblem clone = new GrammaticalEvolutionProblem(super.pathToBnf);
        return clone;
    }

    public static void main(String[] args) {
        JecoLogger.setup();
        // First create the problem
        GrammaticalEvolutionProblem problem = new GrammaticalEvolutionProblem("lib" + File.separator + "grammar_example.bnf");
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
