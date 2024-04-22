package eco.core.algorithm.metaheuristic.aco;

import java.util.logging.Level;

import eco.core.benchmarks.Rastringin;
import eco.core.problem.Solution;
import eco.core.problem.Solutions;
import eco.core.problem.Variable;
import eco.core.util.logger.EcoLogger;

/**
 * Ant Colony Optimization (ACO) algorithm example.
 */
public class AntColonyExample {
    public static void main(String[] args) {
        EcoLogger.setup(Level.FINE);
        // First create the problem
        Rastringin problem = new Rastringin(4);
        // Second create the algorithm
        AntColony aco = new AntColony(problem, 100, 5000, true);
        aco.initialize();
        Solutions<Variable<Double>> solutions = aco.execute();
        for (Solution<Variable<Double>> solution : solutions) {
            System.out.println("Fitness = " + solution.getObjectives().get(0));
        }

    }
}
