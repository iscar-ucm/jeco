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
package jeco.core.algorithms.ge_templates;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

import jeco.core.algorithms.SimpleGeneticAlgorithm;
import jeco.core.operator.comparator.SimpleDominance;
import jeco.core.operator.crossover.SinglePointCrossover;
import jeco.core.operator.evaluator.AbstractPopPredictor;
import jeco.core.operator.mutation.IntegerFlipMutation;
import jeco.core.operator.selection.BinaryTournament;
import jeco.core.problem.GrammaticalEvolutionAbstractProblem;
import jeco.core.problem.GrammaticalEvolutionPhenotype;
import jeco.core.problem.Solution;
import jeco.core.problem.Solutions;
import jeco.core.problem.Variable;
import jeco.core.util.DataTable;
import jeco.core.util.compiler.MyCompiler;
import jeco.core.util.compiler.MyLoader;
import jeco.core.util.logger.JecoLogger;

/**
 * Class to develop "static" (non-temporal) models
 * This class must be carefully revised and tested. I just copied/pasted a functional
 * version that I developed for Patricia.
 */
public class GrammaticalEvolutionStaticModel extends GrammaticalEvolutionAbstractProblem {

    /**
     * Logger
     */
    private static final Logger LOGGER = Logger.getLogger(GrammaticalEvolutionStaticModel.class.getName());

    /**
     * Path to the BNF file
     */
    protected String bnfFilePath;
    /**
     * Thread ID
     */
    protected int threadId;
    /**
     * Compiler
     */
    protected MyCompiler compiler;
    /**
     * Data table
     */
    protected DataTable dataTable;
    /**
     * Predictor
     */
    protected AbstractPopPredictor predictor;
    /**
     * Best fitness
     */
    protected double bestFitness = Double.POSITIVE_INFINITY;

    /**
     * Constructor
     * @param bnfFilePath Path to the BNF file
     * @param dataPath Path to the data file
     * @param compilationDir Path to the compilation directory
     * @param classPathSeparator Class path separator
     * @param threadId Thread ID
     * @throws IOException If there is an error reading the BNF file
     */
    public GrammaticalEvolutionStaticModel(String bnfFilePath, String dataPath, String compilationDir, String classPathSeparator, int threadId) throws IOException {
        super(bnfFilePath, 1);
        this.bnfFilePath = bnfFilePath;
        this.threadId = threadId;
        compiler = new MyCompiler(compilationDir, classPathSeparator);
        dataTable = new DataTable(this, dataPath);
    }

    /**
     * Constructor
     * @param bnfFilePath Path to the BNF file
     * @param dataPath Path to the data file
     * @param compilationDir Path to the compilation directory
     * @param classPathSeparator Class path separator
     * @throws IOException If there is an error reading the BNF file
     */
    public GrammaticalEvolutionStaticModel(String bnfFilePath, String dataPath, String compilationDir, String classPathSeparator) throws IOException {
        this(bnfFilePath, dataPath, compilationDir, classPathSeparator, 1);
    }

    /**
     * Generate the code and compile it
     * @param solutions Solutions
     * @throws Exception If there is an error generating the code or compiling it
     */
    public void generateCodeAndCompile(Solutions<Variable<Integer>> solutions) throws Exception {
        // Phenotype generation
        ArrayList<String> phenotypes = new ArrayList<>();
        for (Solution<Variable<Integer>> solution : solutions) {
            GrammaticalEvolutionPhenotype phenotype = super.generatePhenotype(solution);
            if (super.correctSol) {
                phenotypes.add(phenotype.toString());
            } else {
                phenotypes.add("return 0;");
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

    @Override
    public void evaluate(Solutions<Variable<Integer>> solutions) {
        try {
            this.generateCodeAndCompile(solutions);
            // And now we evaluate all the solutions with the compiled file:
            predictor = (AbstractPopPredictor) (new MyLoader(compiler.getWorkDir())).loadClass("PopPredictor" + threadId).getDeclaredConstructor().newInstance();
            for (int i = 0; i < solutions.size(); ++i) {
                predictor.updatePredictor(dataTable, i);
                //double fit = dataTable.computeFIT();
                double fit = 0.0;
                for(double[] row : dataTable.getData()) {
                    if(row[0]!=row[dataTable.getPredictorColumn()]) {
                        fit++;
                    }
                }
                if (fit < bestFitness) {
                    bestFitness = fit;
                    LOGGER.info("Best FIT=" + Math.round(100 * (1 - bestFitness)) + "%");
                }
                solutions.get(i).getObjectives().set(0, fit);
            }
        } catch (Exception ex) {
            Logger.getLogger(GrammaticalEvolutionStaticModel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void evaluate(Solution<Variable<Integer>> solution, GrammaticalEvolutionPhenotype phenotype) {
        LOGGER.severe("The solutions should be already evaluated. You should not see this message.");
    }

    @Override
    public GrammaticalEvolutionStaticModel clone() {
        GrammaticalEvolutionStaticModel clone = null;
        try {
            clone = new GrammaticalEvolutionStaticModel(bnfFilePath, dataTable.getPath(), compiler.getWorkDir(), compiler.getClassPathSeparator(), threadId + 1);
        } catch (IOException ex) {
            Logger.getLogger(GrammaticalEvolutionStaticModel.class.getName()).log(Level.SEVERE, null, ex);
        }
        return clone;
    }

    public static void main(String[] args) {
        int numIndividuals = 100;
        int numGenerations = 5000;
        try {
            File clsDir = new File("dist");
            URLClassLoader sysloader = (URLClassLoader) ClassLoader.getSystemClassLoader();
            Class<URLClassLoader> sysclass = URLClassLoader.class;
            Method method = sysclass.getDeclaredMethod("addURL", new Class[]{URL.class});
            method.setAccessible(true);
            method.invoke(sysloader, new Object[]{clsDir.toURI().toURL()});
        } catch (Exception ex) {
            LOGGER.severe(ex.getLocalizedMessage());
        }
        JecoLogger.setup(Level.INFO);

        GrammaticalEvolutionStaticModel problem = null;
        try {
            String bnfFilePath = "lib" + File.separator + GrammaticalEvolutionStaticModel.class.getSimpleName() + ".bnf";
            String dataPath = "lib" + File.separator + GrammaticalEvolutionStaticModel.class.getSimpleName() + ".csv";
            String compilationDir = "dist";
            String classPathSeparator = ":";
            problem = new GrammaticalEvolutionStaticModel(bnfFilePath, dataPath, compilationDir, classPathSeparator);
        } catch (IOException ex) {
            Logger.getLogger(GrammaticalEvolutionStaticModel.class.getName()).log(Level.SEVERE, null, ex);
        }
        // Second create the algorithm
        IntegerFlipMutation<Variable<Integer>> mutationOperator = new IntegerFlipMutation<>(problem, 1.0 / problem.reader.getRules().size());
        SinglePointCrossover<Variable<Integer>> crossoverOperator = new SinglePointCrossover<>(problem, SinglePointCrossover.DEFAULT_FIXED_CROSSOVER_POINT, SinglePointCrossover.DEFAULT_PROBABILITY, SinglePointCrossover.AVOID_REPETITION_IN_FRONT);
        SimpleDominance<Variable<Integer>> comparator = new SimpleDominance<>();
        BinaryTournament<Variable<Integer>> selectionOp = new BinaryTournament<>(comparator);
        SimpleGeneticAlgorithm<Variable<Integer>> algorithm = new SimpleGeneticAlgorithm<>(problem, numIndividuals, numGenerations, true, mutationOperator, crossoverOperator, selectionOp);
        algorithm.initialize();
        Solutions<Variable<Integer>> solutions = algorithm.execute();
        LOGGER.info("Solutions[0] with fitness " + solutions.get(0).getObjective(0));
        LOGGER.info("Solutions[0] with expression " + problem.generatePhenotype(solutions.get(0)).toString());
    }
}
