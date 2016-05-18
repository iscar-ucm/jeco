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
package hero.algorithm.metaheuristic.ge;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import hero.algorithm.metaheuristic.ga.SimpleGeneticAlgorithm;
import hero.algorithm.metaheuristic.moge.AbstractProblemGE;
import hero.algorithm.metaheuristic.moge.Phenotype;
import hero.operator.comparator.SimpleDominance;
import hero.operator.crossover.SinglePointCrossover;
import hero.operator.evaluator.AbstractPopPredictor;
import hero.operator.mutation.IntegerFlipMutation;
import hero.operator.selection.BinaryTournament;
import hero.problem.Solution;
import hero.problem.Solutions;
import hero.problem.Variable;
import hero.util.DataTable;
import hero.util.compiler.MyCompiler;
import hero.util.compiler.MyLoader;
import hero.util.logger.HeroLogger;

/**
 * Class to develop "static" (non-temporal) models
 * This class must be carefully revised and tested. I just copied/pasted a functional
 * version that I developed for Patricia.
 */
public class GramEvalStaticModel extends AbstractProblemGE {

    private static final Logger LOGGER = Logger.getLogger(GramEvalStaticModel.class.getName());

    protected int threadId;
    protected MyCompiler compiler;
    protected DataTable dataTable;
    protected Properties properties;
    protected AbstractPopPredictor predictor;
    protected String[] varNames;
    protected double bestFitness = Double.POSITIVE_INFINITY;
    protected String bestExpression = "";

    public GramEvalStaticModel(Properties properties, int threadId) throws IOException {
        super(properties.getProperty("BnfPathFile"), 1);
        this.properties = properties; // Just for its use in "clone" member function
        this.threadId = threadId;
        compiler = new MyCompiler(properties);
        dataTable = new DataTable(this, properties.getProperty("DataPath"));
        varNames = properties.getProperty("VarNames").split(";");
    }

    public GramEvalStaticModel(Properties properties) throws IOException {
        this(properties, 1);
    }

    public void generateCodeAndCompile(Solutions<Variable<Integer>> solutions) throws Exception {
        // Phenotype generation
        ArrayList<String> phenotypes = new ArrayList<>();
        for (Solution<Variable<Integer>> solution : solutions) {
            Phenotype phenotype = super.generatePhenotype(solution);
            if (super.correctSol) {
                phenotypes.add(phenotype.toString());
            } else {
                phenotypes.add("0");
            }
        }
        // Compilation process:
        File file = new File(compiler.getWorkDir() + File.separator + "PopPredictor" + threadId + ".java");
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        writer.write(AbstractPopPredictor.generateClassCode(threadId, phenotypes));
        writer.flush();
        writer.close();
        LinkedList<String> filePaths = new LinkedList<>();
        filePaths.add(file.getAbsolutePath());
        boolean sucess = compiler.compile(filePaths);
        if (!sucess) {
            LOGGER.severe("Unable to compile, with errors:");
            LOGGER.severe(compiler.getOutput());
        }
    }

    public double computeRMSE(double[] y, double[] yP) {
        double rmse = 0;
        for (int j = 0; j < y.length; ++j) {
            rmse += Math.pow(y[j] - yP[j], 2);
        }
        rmse = Math.sqrt(rmse / y.length);
        return rmse;
    }

    @Override
    public void evaluate(Solutions<Variable<Integer>> solutions) {
        try {
            this.generateCodeAndCompile(solutions);
            // And now we evaluate all the solutions with the compiled file:
            predictor = (AbstractPopPredictor) (new MyLoader(compiler.getWorkDir())).loadClass("PopPredictor" + threadId).newInstance();
            double[][] x = null;
            double[] y = null;
            for (int i = 0; i < solutions.size(); ++i) {
                double[] yP = predictor.computeYP(i, x);
                double rmse = computeRMSE(y, yP);
                if (rmse < bestFitness) {
                    bestFitness = rmse;
                    bestExpression = super.generatePhenotype(solutions.get(i)).toString();
                    for (int j = 0; j < varNames.length - 1; ++j) {
                        bestExpression = bestExpression.replaceAll("x\\[" + j + "\\]", varNames[j]);
                    }
                    LOGGER.info("Best FIT=" + bestFitness + "; Expresion=" + bestExpression);
                }
                solutions.get(i).getObjectives().set(0, rmse);
            }
        } catch (Exception ex) {
            Logger.getLogger(GramEvalStaticModel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void validate(Solutions<Variable<Integer>> solutions) {
        try {
            this.generateCodeAndCompile(solutions);
            // And now we evaluate all the solutions with the compiled file:
            predictor = (AbstractPopPredictor) (new MyLoader(compiler.getWorkDir())).loadClass("PopPredictor" + threadId).newInstance();
            double[][] x = null;
            double[] y = null;
            for (int i = 0; i < solutions.size(); ++i) {
                double[] yP = predictor.computeYP(i, x);
                double rmse = computeRMSE(y, yP);
                solutions.get(i).getObjectives().set(0, rmse);
            }
        } catch (Exception ex) {
            Logger.getLogger(GramEvalStaticModel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void evaluate(Solution<Variable<Integer>> solution) {
        LOGGER.severe("The solutions should be already evaluated. You should not see this message.");
    }

    @Override
    public void evaluate(Solution<Variable<Integer>> solution, Phenotype phenotype) {
        LOGGER.severe("The solutions should be already evaluated. You should not see this message.");
    }

    @Override
    public GramEvalStaticModel clone() {
        GramEvalStaticModel clone = null;
        try {
            clone = new GramEvalStaticModel(properties, threadId + 1);
        } catch (IOException ex) {
            Logger.getLogger(GramEvalStaticModel.class.getName()).log(Level.SEVERE, null, ex);
        }
        return clone;
    }

    public static void main(String[] args) {
        String propertiesFilePath = "test" + File.separator + GramEvalStaticModel.class.getSimpleName() + ".properties";
        int threadId = 1;
        if (args.length == 1) {
            propertiesFilePath = args[0];
        } else if (args.length >= 2) {
            propertiesFilePath = args[0];
            threadId = Integer.valueOf(args[1]);
        }
        Properties properties = new Properties();
        try {
            properties.load(new BufferedReader(new FileReader(new File(propertiesFilePath))));
            File clsDir = new File(properties.getProperty("WorkDir"));
            URLClassLoader sysloader = (URLClassLoader) ClassLoader.getSystemClassLoader();
            Class<URLClassLoader> sysclass = URLClassLoader.class;
            Method method = sysclass.getDeclaredMethod("addURL", new Class[]{URL.class});
            method.setAccessible(true);
            method.invoke(sysloader, new Object[]{clsDir.toURI().toURL()});
        } catch (Exception ex) {
            LOGGER.severe(ex.getLocalizedMessage());
        }
        HeroLogger.setup(properties.getProperty("LoggerBasePath") + "_" + threadId + ".log", Level.parse(properties.getProperty("LoggerLevel")));

        GramEvalStaticModel problem = null;
        try {
            problem = new GramEvalStaticModel(properties, threadId);
        } catch (IOException ex) {
            Logger.getLogger(GramEvalStaticModel.class.getName()).log(Level.SEVERE, null, ex);
        }
        // Second create the algorithm
        IntegerFlipMutation<Variable<Integer>> mutationOperator = new IntegerFlipMutation<>(problem, 1.0 / problem.reader.getRules().size());
        SinglePointCrossover<Variable<Integer>> crossoverOperator = new SinglePointCrossover<>(problem, SinglePointCrossover.DEFAULT_FIXED_CROSSOVER_POINT, SinglePointCrossover.DEFAULT_PROBABILITY, SinglePointCrossover.AVOID_REPETITION_IN_FRONT);
        SimpleDominance<Variable<Integer>> comparator = new SimpleDominance<>();
        BinaryTournament<Variable<Integer>> selectionOp = new BinaryTournament<>(comparator);
        SimpleGeneticAlgorithm<Variable<Integer>> algorithm = new SimpleGeneticAlgorithm<>(problem, Integer.valueOf(properties.getProperty("NumIndividuals")), Integer.valueOf(properties.getProperty("NumGenerations")), true, mutationOperator, crossoverOperator, selectionOp);
        algorithm.initialize();
        Solutions<Variable<Integer>> solutions = algorithm.execute();
        // Now we evaluate the solution in the validation data
        LOGGER.info("Validation of solutions[0] with fitness " + solutions.get(0).getObjective(0));
        problem.validate(solutions);
        Solution<Variable<Integer>> solution = solutions.get(0);
        double validationFitness = solution.getObjectives().get(0);
        LOGGER.info("Validation fitness for solutions[0] = " + validationFitness);
    }
}
