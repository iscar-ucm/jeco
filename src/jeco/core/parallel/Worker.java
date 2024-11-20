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
package jeco.core.parallel;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import jeco.core.problem.Problem;
import jeco.core.problem.Solution;
import jeco.core.problem.Solutions;
import jeco.core.problem.Variable;

/**
 * Worker thread for parallel evaluation of solutions.
 * 
 * @param <V> Variable type.
 */
public class Worker<V extends Variable<?>> extends Thread {

    private static final Logger logger = Logger.getLogger(Worker.class.getName());

    /**
     * Problem to be solved.
     */
    protected Problem<V> problem;
    /**
     * Shared queue of solutions.
     */
    protected LinkedBlockingQueue<Solution<V>> sharedQueue = null;
    /**
     * Number of solutions to be evaluated.
     */
    protected int numSolutions = 1;

    /**
     * Constructor.
     * 
     * @param problem Problem to be solved.
     * @param sharedQueue Shared queue of solutions.
     * @param numSolutions Number of solutions to be evaluated.
     */
    public Worker(Problem<V> problem, LinkedBlockingQueue<Solution<V>> sharedQueue, int numSolutions) {
        this.problem = problem;
        this.sharedQueue = sharedQueue;
        this.numSolutions = numSolutions;
    }

    @Override
    public void run() {
        Solutions<V> solutions = new Solutions<>();
        try {
            for (int i = 0; i < numSolutions; ++i) {
                Solution<V> solution = sharedQueue.poll(3, TimeUnit.SECONDS);
                if (solution != null) {
                    solutions.add(solution);
                }
            }
            problem.evaluate(solutions);
            solutions.clear();
        } catch (InterruptedException e) {
            logger.severe(e.getLocalizedMessage());
            logger.severe("Thread " + super.getId() + " has been interrupted. Shuting down ...");
        }

    }
}
