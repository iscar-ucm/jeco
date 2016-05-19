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
package hero.core.algorithm.metaheuristic.moge;

import java.util.LinkedList;
import hero.core.problem.Problem;
import hero.core.problem.Solution;
import hero.core.problem.Solutions;
import hero.core.problem.Variable;
import hero.core.util.bnf.BnfReader;
import hero.core.util.bnf.Production;
import hero.core.util.bnf.Rule;
import hero.core.util.bnf.Symbol;
import hero.core.util.random.RandomGenerator;

public abstract class AbstractProblemGE extends Problem<Variable<Integer>> {

	public static final int CHROMOSOME_LENGTH_DEFAULT = 100;	
	public static final int CODON_UPPER_BOUND_DEFAULT = 256;
	public static final int MAX_CNT_WRAPPINGS_DEFAULT = 3;
	public static final int NUM_OF_OBJECTIVES_DEFAULT = 2;

	protected String pathToBnf;
	protected BnfReader reader;
	protected int maxCntWrappings = MAX_CNT_WRAPPINGS_DEFAULT;
	protected int currentIdx;
	protected int currentWrp;
	protected boolean correctSol;

	public AbstractProblemGE(String pathToBnf, int numberOfObjectives, int chromosomeLength, int maxCntWrappings, int codonUpperBound) {
		super(chromosomeLength, numberOfObjectives);
		this.pathToBnf = pathToBnf;
		reader = new BnfReader();
		reader.load(pathToBnf);
		this.maxCntWrappings = maxCntWrappings;
		for (int i = 0; i < numberOfVariables; i++) {
			lowerBound[i] = 0;
			upperBound[i] = codonUpperBound;
		}
	}

	public AbstractProblemGE(String pathToBnf, int numberOfObjectives) {
		this(pathToBnf, numberOfObjectives, CHROMOSOME_LENGTH_DEFAULT, MAX_CNT_WRAPPINGS_DEFAULT, CODON_UPPER_BOUND_DEFAULT);
	}

	public AbstractProblemGE(String pathToBnf) {
		this(pathToBnf, NUM_OF_OBJECTIVES_DEFAULT, CHROMOSOME_LENGTH_DEFAULT, MAX_CNT_WRAPPINGS_DEFAULT, CODON_UPPER_BOUND_DEFAULT);
	}

	abstract public void evaluate(Solution<Variable<Integer>> solution, Phenotype phenotype);
	
	public void evaluate(Solutions<Variable<Integer>> solutions) {
		for(Solution<Variable<Integer>> solution : solutions)
			evaluate(solution);
	}

	public void evaluate(Solution<Variable<Integer>> solution) {
		Phenotype phenotype = generatePhenotype(solution);
		if(correctSol)
			evaluate(solution, phenotype);
		else {
			for(int i=0; i<super.numberOfObjectives; ++i) {
				solution.getObjectives().set(i, Double.POSITIVE_INFINITY);
			}
		}
	}

	public Phenotype generatePhenotype(Solution<Variable<Integer>> solution) {
		currentIdx = 0;
		currentWrp = 0;
		correctSol = true;
		Phenotype phenotype = new Phenotype();
		Rule firstRule = reader.getRules().get(0);
		Production firstProduction = firstRule.get(solution.getVariables().get(currentIdx++).getValue() % firstRule.size());
		processProduction(firstProduction, solution, phenotype);
		return phenotype;
	}

	public void processProduction(Production currentProduction, Solution<Variable<Integer>> solution, LinkedList<String> phenotype) {
		if(!correctSol)
			return;
		for (Symbol symbol : currentProduction) {
			if (symbol.isTerminal()) {
				phenotype.add(symbol.toString());
			} else {
				if(currentIdx >= solution.getVariables().size() && currentWrp<maxCntWrappings) {
					currentIdx = 0;
					currentWrp++;
				}
				if (currentIdx < solution.getVariables().size()) {
					Rule rule = reader.findRule(symbol);
					Production production = rule.get(solution.getVariables().get(currentIdx++).getValue() % rule.size());
					processProduction(production, solution, phenotype);
				}
				else {
					correctSol = false;
					return;
				}
			}
		}
	}

	public Solutions<Variable<Integer>> newRandomSetOfSolutions(int size) {
		Solutions<Variable<Integer>> solutions = new Solutions<Variable<Integer>>();
		for (int i=0; i<size; ++i) {
			Solution<Variable<Integer>> solI = new Solution<Variable<Integer>>(numberOfObjectives);
			for (int j = 0; j < numberOfVariables; ++j) {
				Variable<Integer> varJ = new Variable<Integer>(RandomGenerator.nextInteger((int) upperBound[j])); 
				solI.getVariables().add(varJ);
			}
			solutions.add(solI);
		}
		return solutions;
	}

}
