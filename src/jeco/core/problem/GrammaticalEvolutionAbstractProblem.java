/*
* File: GrammaticalEvolutionAbstractProblem.java
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

package jeco.core.problem;

import java.util.LinkedList;

import jeco.core.util.bnf.BnfReader;
import jeco.core.util.bnf.Production;
import jeco.core.util.bnf.Rule;
import jeco.core.util.bnf.Symbol;
import jeco.core.util.random.RandomGenerator;

/**
 * Abstract class for grammatical evolution problems.
 */
public abstract class GrammaticalEvolutionAbstractProblem extends Problem<Variable<Integer>> {

    /**
     * Default length of the chromosome.
     */
    public static final int CHROMOSOME_LENGTH_DEFAULT = 100;
    /**
     * Default upper bound for the codons.
     */
    public static final int CODON_UPPER_BOUND_DEFAULT = 256;
    /**
     * Default maximum number of wrappings.
     */
    public static final int MAX_CNT_WRAPPINGS_DEFAULT = 3;
    /**
     * Default number of objectives.
     */
    public static final int NUM_OF_OBJECTIVES_DEFAULT = 2;

    /**
     * Path to the BNF file.
     */
    protected String pathToBnf;
    /**
     * BNF reader.
     */
    protected BnfReader reader;
    /**
     * Maximum number of wrappings.
     */
    protected int maxCntWrappings = MAX_CNT_WRAPPINGS_DEFAULT;
    /**
     * Current index.
     */
    protected int currentIdx;
    /**
     * Current wrapping.
     */
    protected int currentWrp;
    /**
     * Correct solution or not.
     */
    protected boolean correctSol;

    /**
     * Constructor.
     *
     * @param pathToBnf Path to the BNF file.
     * @param numberOfObjectives Number of objectives.
     * @param chromosomeLength Length of the chromosome.
     * @param maxCntWrappings Maximum number of wrappings.
     * @param codonUpperBound Upper bound for the codons.
     */
    public GrammaticalEvolutionAbstractProblem(String pathToBnf, int numberOfObjectives, int chromosomeLength, int maxCntWrappings, int codonUpperBound) {
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

    /**
     * Constructor.
     * @param pathToBnf Path to the BNF file.
     * @param numberOfObjectives Number of objectives.
     */
    public GrammaticalEvolutionAbstractProblem(String pathToBnf, int numberOfObjectives) {
        this(pathToBnf, numberOfObjectives, CHROMOSOME_LENGTH_DEFAULT, MAX_CNT_WRAPPINGS_DEFAULT, CODON_UPPER_BOUND_DEFAULT);
    }

    /**
     * Constructor.
     * @param pathToBnf Path to the BNF file.
     */
    public GrammaticalEvolutionAbstractProblem(String pathToBnf) {
        this(pathToBnf, NUM_OF_OBJECTIVES_DEFAULT, CHROMOSOME_LENGTH_DEFAULT, MAX_CNT_WRAPPINGS_DEFAULT, CODON_UPPER_BOUND_DEFAULT);
    }

    /**
     * Evaluate the solution.
     * @param solution Solution to evaluate.
     * @param phenotype Phenotype of the solution.
     */
    abstract public void evaluate(Solution<Variable<Integer>> solution, GrammaticalEvolutionPhenotype phenotype);

    @Override
    public void evaluate(Solutions<Variable<Integer>> solutions) {
        for (Solution<Variable<Integer>> solution : solutions) {
            evaluate(solution);
        }
    }

    @Override
    public void evaluate(Solution<Variable<Integer>> solution) {
        GrammaticalEvolutionPhenotype phenotype = generatePhenotype(solution);
        if (correctSol) {
            evaluate(solution, phenotype);
        } else {
            for (int i = 0; i < super.numberOfObjectives; ++i) {
                solution.getObjectives().set(i, Double.POSITIVE_INFINITY);
            }
        }
    }

    /**
     * Generate the phenotype of the solution.
     * @param solution Solution.
     * @return Phenotype of the solution.
     */
    public GrammaticalEvolutionPhenotype generatePhenotype(Solution<Variable<Integer>> solution) {
        currentIdx = 0;
        currentWrp = 0;
        correctSol = true;
        GrammaticalEvolutionPhenotype phenotype = new GrammaticalEvolutionPhenotype();
        Rule firstRule = reader.getRules().get(0);
        Production firstProduction = firstRule.get(solution.getVariables().get(currentIdx++).getValue() % firstRule.size());
        processProduction(firstProduction, solution, phenotype);
        return phenotype;
    }

    /**
     * Process the production.
     * @param currentProduction Current production.
     * @param solution Solution.
     * @param phenotype Phenotype.
     */
    public void processProduction(Production currentProduction, Solution<Variable<Integer>> solution, LinkedList<String> phenotype) {
        if (!correctSol) {
            return;
        }
        for (Symbol symbol : currentProduction) {
            if (symbol.isTerminal()) {
                phenotype.add(symbol.toString());
            } else {
                if (currentIdx >= solution.getVariables().size() && currentWrp < maxCntWrappings) {
                    currentIdx = 0;
                    currentWrp++;
                }
                if (currentIdx < solution.getVariables().size()) {
                    Rule rule = reader.findRule(symbol);
                    Production production = rule.get(solution.getVariables().get(currentIdx++).getValue() % rule.size());
                    processProduction(production, solution, phenotype);
                } else {
                    correctSol = false;
                    return;
                }
            }
        }
    }

    @Override
    public Solutions<Variable<Integer>> newRandomSetOfSolutions(int size) {
        Solutions<Variable<Integer>> solutions = new Solutions<>();
        for (int i = 0; i < size; ++i) {
            Solution<Variable<Integer>> solI = new Solution<>(numberOfObjectives);
            for (int j = 0; j < numberOfVariables; ++j) {
                Variable<Integer> varJ = new Variable<>(RandomGenerator.nextInteger((int) upperBound[j]));
                solI.getVariables().add(varJ);
            }
            solutions.add(solI);
        }
        return solutions;
    }

}
