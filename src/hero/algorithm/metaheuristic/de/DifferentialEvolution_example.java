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
 *  - J. M. Colmenar
 *  - José Luis Risco Martín
 */
package hero.algorithm.metaheuristic.de;

import java.util.logging.Level;
import java.util.logging.Logger;

import hero.problem.Solution;
import hero.problem.Solutions;
import hero.problem.Variable;
import hero.problems.Rastringin;
import hero.util.logger.HeroLogger;

/**
 * Test class and example of Differential Evolution use.
 *
 * @author J. M. Colmenar
 */
public class DifferentialEvolution_example {

    private static final Logger LOGGER = Logger.getLogger(DifferentialEvolution_example.class.getName());

    public static void main(String[] args) {
        HeroLogger.setup(Level.INFO);
        // First create the problem
        Rastringin problem = new Rastringin(4);
        // Second create the algorithm:
        /* DifferentialEvolution(Problem<Variable<Double>> problem, Integer maxPopulationSize, Integer maxGenerations, Boolean stopWhenSolved,
            Double mutationFactor, Double recombinationFactor) */
        DifferentialEvolution algorithm = new DifferentialEvolution(problem, 20, 250, true, 1.0, 0.5);
        algorithm.initialize();
        Solutions<Variable<Double>> solutions = algorithm.execute();
        for (Solution<Variable<Double>> solution : solutions) {
            LOGGER.info("Fitness = " + solution.getObjectives().get(0));
        }
    }
}
