package eco.core.algorithm.metaheuristic.aco;

import java.util.logging.Logger;

import eco.core.algorithm.Algorithm;
import eco.core.problem.Problem;
import eco.core.problem.Solution;
import eco.core.problem.Solutions;
import eco.core.problem.Variable;
import eco.core.util.random.RandomGenerator;

/**
 * Ant Colony Optimization (ACO) algorithm.
 */
public class AntColony extends Algorithm<Variable<Double>> {

    private static final Logger LOGGER = Logger.getLogger(AntColony.class.getName());

    protected Double alpha = 1.0; // Influencia de la feromona
    protected Double beta = 2.0;  // Influencia de la heurística
    protected Double evaporationRate = 0.5;
    protected Double q = 100.0;     // Cantidad de feromona depositada
    protected Double initPheromone = 1.0;
    protected Integer numStates = 100;

    protected int numAnts;
    protected Solutions<Variable<Double>> ants;
    protected Double bestFitness = Double.POSITIVE_INFINITY;
    protected Solution<Variable<Double>> bestSolution;

    private double[][] pheromones;
    protected int currentIteration = 0;
    protected int maxIterations = 1000;
    protected boolean stopWhenSolved = false;

    /**
     * Constructor.
     * @param problem Problem to solve.
     * @param numAnts Number of ants.
     * @param maxIterations Maximum number of iterations.
     * @param stopWhenSolved Stop when the optimal solution (0) is found.
     */
    public AntColony(Problem<Variable<Double>> problem, int numAnts, int maxIterations, boolean stopWhenSolved) {
        super(problem);
        this.numAnts = numAnts;
        this.maxIterations = maxIterations;
        this.stopWhenSolved = stopWhenSolved;
    }

    @Override
    public void initialize(Solutions<Variable<Double>> myants) {
        if (myants == null) {
            ants = problem.newRandomSetOfSolutions(numAnts);
        } else {
            ants = myants;
        }
        pheromones = new double[problem.getNumberOfVariables()][numStates]; // 100 posibles estados por variable
        for (int i = 0; i < pheromones.length; i++) {
            for (int j = 0; j < pheromones[i].length; j++) {
                pheromones[i][j] = initPheromone;
            }
        }
        problem.evaluate(ants);
        this.currentIteration = 0;
        this.bestFitness = ants.get(0).getObjective(0);
        this.bestSolution = ants.get(0).clone();
    }

    @Override
    public void step() {
        currentIteration++;
        for (int a=0; a < numAnts; a++) {
            Solution<Variable<Double>> ant = ants.get(a);
            for (int v=0; v < problem.getNumberOfVariables(); v++) {
                double lowerBound = problem.getLowerBound(v);
                double upperBound = problem.getUpperBound(v);
                Double nextState = chooseNextState(v, lowerBound, upperBound);
                ant.getVariables().get(v).setValue(nextState);
            }
        }
        // Update pheromones
        for (int v = 0; v < problem.getNumberOfVariables(); v++) {
            for (int s = 0; s < numStates; s++) {
                pheromones[v][s] *= (1 - evaporationRate);
            }
        }
        for (int a = 0; a < numAnts; a++) {
            Solution<Variable<Double>> ant = ants.get(a);
            problem.evaluate(ant);
            if (ant.getObjective(0) < bestFitness) {
                bestFitness = ant.getObjectives().get(0);
                bestSolution = ant.clone();
            }
            for (int v = 0; v < problem.getNumberOfVariables(); v++) {
                int s = (int) ((ant.getVariable(v).getValue().doubleValue() - problem.getLowerBound(v))
                        / (problem.getUpperBound(v) - problem.getLowerBound(v)) * (numStates-1));
                pheromones[v][s] += q / ant.getObjective(0);
            }
        }
    }

    @Override
    public Solutions<Variable<Double>> execute() {
        int nextPercentageReport = 10;
        while (currentIteration < maxIterations) {
            step();
            int percentage = Math.round((currentIteration * 100) / maxIterations);
            Double bestObj = bestSolution.getObjective(0);
            if (percentage == nextPercentageReport) {
                LOGGER.info(percentage + "% performed ..." + " -- Best fitness: " + bestObj);
                nextPercentageReport += 10;
            }
            if (stopWhenSolved) {
                if (bestObj <= 0) {
                    LOGGER.info("Optimal solution found in " + currentIteration + " generations.");
                    break;
                }
            }
        }
        Solutions<Variable<Double>> solutions = new Solutions<>();
        solutions.add(bestSolution);
        return solutions;
    }

    /**
     * Choose the next state for a variable.
     * @param v Variable index.
     * @param lowerBound Lower bound of the variable.
     * @param upperBound Upper bound of the variable.
     * @return Next state.
     */
    private Double chooseNextState(int v, double lowerBound, double upperBound) {
        double[] probabilities = new double[numStates];
        double sum = 0.0;

        for (int i = 0; i < numStates; i++) {
            probabilities[i] = Math.pow(pheromones[v][i], alpha);
            sum += probabilities[i];
        }

        double choice = RandomGenerator.nextDouble(sum);
        sum = 0.0;
        for (int i = 0; i < numStates; i++) {
            sum += probabilities[i];
            if (sum >= choice) {
                return i / (numStates-1.0) * (upperBound - lowerBound) + lowerBound;
            }
        }

        return lowerBound; // Fallback

    }
    
}
