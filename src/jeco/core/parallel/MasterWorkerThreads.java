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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import jeco.core.algorithms.Algorithm;
import jeco.core.algorithms.GrammaticalEvolution;
import jeco.core.benchmarks.GrammaticalEvolutionProblem;
import jeco.core.problem.Problem;
import jeco.core.problem.Solution;
import jeco.core.problem.Solutions;
import jeco.core.problem.Variable;
import jeco.core.util.logger.JecoLogger;

import java.io.File;

/**
 * Master/worker pattern for parallel evaluation of solutions.
 * 
 * @param <V> Variable type.
 */
public class MasterWorkerThreads<V extends Variable<?>> extends Problem<V> {

    private static final Logger logger = Logger.getLogger(MasterWorkerThreads.class.getName());
    /**
     * Algorithm to be executed.
     */
    protected Algorithm<V> algorithm = null;
    /**
     * Problem to be solved.
     */
    protected Problem<V> problem = null;
    /**
     * Shared queue of solutions.
     */
    protected LinkedBlockingQueue<Solution<V>> sharedQueue = new LinkedBlockingQueue<>();
    /**
     * Clones of the problem for each worker.
     */
    protected ArrayList<Problem<V>> problemClones = new ArrayList<>();
    /**
     * Number of workers.
     */
    protected Integer numWorkers = null;

    /**
     * Constructor.
     * 
     * @param algorithm Algorithm to be executed.
     * @param problem Problem to be solved.
     * @param numWorkers Number of workers.
     */
    public MasterWorkerThreads(Algorithm<V> algorithm, Problem<V> problem, Integer numWorkers) {
        super(problem.getNumberOfVariables(), problem.getNumberOfObjectives());
        for (int i = 0; i < numberOfVariables; ++i) {
            super.lowerBound[i] = problem.getLowerBound(i);
            super.upperBound[i] = problem.getUpperBound(i);
        }
        this.algorithm = algorithm;
        this.problem = problem;
        this.numWorkers = numWorkers;
        for (int i = 0; i < numWorkers; ++i) {
            problemClones.add(problem.clone());
        }
    }

    /**
     * Constructor.
     * 
     * @param algorithm Algorithm to be executed.
     * @param problem Problem to be solved.
     */
    public MasterWorkerThreads(Algorithm<V> algorithm, Problem<V> problem) {
        this(algorithm, problem, Runtime.getRuntime().availableProcessors());
    }

    @Override
    public void evaluate(Solutions<V> solutions) {
        sharedQueue.addAll(solutions);
        LinkedList<Worker<V>> workers = new LinkedList<>();
        int solutionsPerWorker = solutions.size()/numWorkers;
        int remainingSolutions = solutions.size()%numWorkers;
        for (int i = 0; i < numWorkers; ++i) {
            if(i==(numWorkers-1)) {
                solutionsPerWorker = solutionsPerWorker + remainingSolutions;
            }
            Worker<V> worker = new Worker<>(problemClones.get(i), sharedQueue, solutionsPerWorker);
            workers.add(worker);
            worker.start();
        }
        for (Worker<V> worker : workers) {
            try {
                worker.join();
            } catch (InterruptedException e) {
                logger.severe(e.getLocalizedMessage());
                logger.severe("Main thread cannot join to: " + worker.getId());
            }
        }
    }

    @Override
    public void evaluate(Solution<V> solution) {
        logger.log(Level.SEVERE, this.getClass().getSimpleName() + "::evaluate() - I do not know why I am here, doing nothing to evaluate solution");
    }

    @Override
    public Solutions<V> newRandomSetOfSolutions(int size) {
        return problem.newRandomSetOfSolutions(size);
    }

    /**
     * Execute the algorithm.
     * @return Solutions.
     */
    public Solutions<V> execute() {
        algorithm.setProblem(this);
        algorithm.initialize();
        return algorithm.execute();
    }

    @Override
    public Problem<V> clone() {
        logger.severe("This master cannot be cloned.");
        return null;
    }

    public static void main(String[] args) {
        JecoLogger.setup();
        long begin = System.currentTimeMillis();
        // First create the problem
        GrammaticalEvolutionProblem problem = new GrammaticalEvolutionProblem("lib" + File.separator + "grammar_example.bnf");
        // Second create the algorithm
        GrammaticalEvolution algorithm = new GrammaticalEvolution(problem, 100, 250);
        // Now the master/worker
        MasterWorkerThreads<Variable<Integer>> masterWorker = new MasterWorkerThreads<Variable<Integer>>(algorithm, problem, 4);
        Solutions<Variable<Integer>> solutions = masterWorker.execute();
        for (Solution<Variable<Integer>> solution : solutions) {
            logger.info("Fitness = (" + solution.getObjectives().get(0) + ", " + solution.getObjectives().get(1) + ")");
            logger.info("Phenotype = (" + problem.generatePhenotype(solution).toString() + ")");
        }
        long end = System.currentTimeMillis();
        logger.info("Time: " + ((end - begin) / 1000.0) + " seconds");
    }
}
