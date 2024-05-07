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
package eco.core.algorithm.metaheuristic.ge;

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

import eco.core.algorithm.metaheuristic.ga.SimpleGeneticAlgorithm;
import eco.core.algorithm.metaheuristic.moge.AbstractProblemGE;
import eco.core.algorithm.metaheuristic.moge.Phenotype;
import eco.core.operator.comparator.SimpleDominance;
import eco.core.operator.crossover.SinglePointCrossover;
import eco.core.operator.evaluator.AbstractPopPredictor;
import eco.core.operator.mutation.IntegerFlipMutation;
import eco.core.operator.selection.BinaryTournament;
import eco.core.problem.Solution;
import eco.core.problem.Solutions;
import eco.core.problem.Variable;
import eco.core.util.DataTable;
import eco.core.util.compiler.MyCompiler;
import eco.core.util.compiler.MyLoader;
import eco.core.util.logger.EcoLogger;

/**
 * Class to develop "static" (non-temporal) models
 * This class must be carefully revised and tested. I just copied/pasted a functional
 * version that I developed for Patricia.
 */
public class GramEvalStaticModel extends AbstractProblemGE {

    private static final Logger LOGGER = Logger.getLogger(GramEvalStaticModel.class.getName());

    protected String bnfFilePath;
    protected int threadId;
    protected MyCompiler compiler;
    protected DataTable dataTable;
    protected AbstractPopPredictor predictor;
    protected double bestFitness = Double.POSITIVE_INFINITY;

    public GramEvalStaticModel(String bnfFilePath, String dataPath, String compilationDir, String classPathSeparator, int threadId) throws IOException {
        super(bnfFilePath, 1);
        this.bnfFilePath = bnfFilePath;
        this.threadId = threadId;
        compiler = new MyCompiler(compilationDir, classPathSeparator);
        dataTable = new DataTable(this, dataPath);
    }

    public GramEvalStaticModel(String bnfFilePath, String dataPath, String compilationDir, String classPathSeparator) throws IOException {
        this(bnfFilePath, dataPath, compilationDir, classPathSeparator, 1);
    }

    public void generateCodeAndCompile(Solutions<Variable<Integer>> solutions) throws Exception {
        // Phenotype generation
        ArrayList<String> phenotypes = new ArrayList<>();
        for (Solution<Variable<Integer>> solution : solutions) {
            Phenotype phenotype = super.generatePhenotype(solution);
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
            predictor = (AbstractPopPredictor) (new MyLoader(compiler.getWorkDir())).loadClass("PopPredictor" + threadId).newInstance();
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
            Logger.getLogger(GramEvalStaticModel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void evaluate(Solution<Variable<Integer>> solution, Phenotype phenotype) {
        LOGGER.severe("The solutions should be already evaluated. You should not see this message.");
    }

    @Override
    public GramEvalStaticModel clone() {
        GramEvalStaticModel clone = null;
        try {
            clone = new GramEvalStaticModel(bnfFilePath, dataTable.getPath(), compiler.getWorkDir(), compiler.getClassPathSeparator(), threadId + 1);
        } catch (IOException ex) {
            Logger.getLogger(GramEvalStaticModel.class.getName()).log(Level.SEVERE, null, ex);
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
        EcoLogger.setup(Level.INFO);

        GramEvalStaticModel problem = null;
        try {
            String bnfFilePath = "lib" + File.separator + GramEvalStaticModel.class.getSimpleName() + ".bnf";
            String dataPath = "lib" + File.separator + GramEvalStaticModel.class.getSimpleName() + ".csv";
            String compilationDir = "dist";
            String classPathSeparator = ":";
            problem = new GramEvalStaticModel(bnfFilePath, dataPath, compilationDir, classPathSeparator);
        } catch (IOException ex) {
            Logger.getLogger(GramEvalStaticModel.class.getName()).log(Level.SEVERE, null, ex);
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
