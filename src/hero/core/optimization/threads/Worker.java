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
package hero.core.optimization.threads;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import hero.core.problem.Problem;
import hero.core.problem.Solution;
import hero.core.problem.Solutions;
import hero.core.problem.Variable;

/**
 *
 * @author jlrisco
 */
public class Worker<V extends Variable<?>> extends Thread {

    private static final Logger logger = Logger.getLogger(Worker.class.getName());

    protected Problem<V> problem;
    protected LinkedBlockingQueue<Solution<V>> sharedQueue = null;
    protected int numSolutions = 1;

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
