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
package hero.core.problems;

import java.util.logging.Logger;

import hero.core.problem.Problem;
import hero.core.problem.Solution;
import hero.core.problem.Solutions;
import hero.core.problem.Variable;
import hero.core.util.random.RandomGenerator;

public class Rastringin extends Problem<Variable<Double>> {

    private static final Logger logger = Logger.getLogger(Rastringin.class.getName());

    protected double bestValue = Double.POSITIVE_INFINITY;

    public Rastringin(Integer numberOfVariables) {
        super(numberOfVariables, 1);
        for (int i = 0; i < numberOfVariables; i++) {
            lowerBound[i] = -5.12;
            upperBound[i] = 5.12;
        }
    }

    public Solutions<Variable<Double>> newRandomSetOfSolutions(int size) {
        Solutions<Variable<Double>> solutions = new Solutions<Variable<Double>>();
        for (int i = 0; i < size; ++i) {
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
        for (Solution<Variable<Double>> solution : solutions) {
            evaluate(solution);
        }
    }

    @Override
    public void evaluate(Solution<Variable<Double>> solution) {
        double fitness = 10 * super.numberOfVariables;
        for (int i = 0; i < numberOfVariables; ++i) {
            double xi = solution.getVariables().get(i).getValue();
            fitness += Math.pow(xi, 2) - 10 * Math.cos(2 * Math.PI * xi);
        }
        solution.getObjectives().set(0, fitness);
        if (fitness < bestValue) {
            logger.info("Best value found: " + fitness);
            bestValue = fitness;
        }
    }

    public Rastringin clone() {
        Rastringin clone = new Rastringin(this.numberOfVariables);
        for (int i = 0; i < numberOfVariables; ++i) {
            clone.lowerBound[i] = lowerBound[i];
            clone.upperBound[i] = upperBound[i];
        }
        return clone;
    }
}
