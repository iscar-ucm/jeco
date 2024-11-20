/*
 * Copyright (C) 2010 José Luis Risco Martín <jlrisco@ucm.es>
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
package jeco.core.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Logger;

import jeco.core.problem.GrammaticalEvolutionAbstractProblem;

/**
 * This class represents a data table. It is used to store the data that will be
 * used to evaluate the fitness of the individuals.
 * 
 * The data table is a list of rows, where each row is an array of doubles. The
 * first columns are the input columns, and the last column is the output column.
 */
public class DataTable {

    private static final Logger LOGGER = Logger.getLogger(DataTable.class.getName());

    /**
     * The problem that uses this data table.
     */
    protected GrammaticalEvolutionAbstractProblem problem = null;
    /**
     * The path to the data file.
     */
    protected String path = null;
    /**
     * The data table.
     */
    protected ArrayList<double[]> data = new ArrayList<>();
    /**
     * The number of input columns.
     */
    protected int numInputColumns = 0;
    /**
     * The number of total columns.
     */
    protected int numTotalColumns = 0;

    /**
     * The best fitness found so far.
     */
    protected double bestFitness = Double.POSITIVE_INFINITY;

    /**
     * Constructor.
     * 
     * @param problem
     *            The problem that uses this data table.
     * @param dataPath
     *            The path to the data file.
     * @throws IOException
     *             If there is an error reading the data file.
     */
    public DataTable(GrammaticalEvolutionAbstractProblem problem, String dataPath) throws IOException {
        this.problem = problem;
        this.path = dataPath;
        LOGGER.info("Reading data file ...");
        loadData(dataPath);
        LOGGER.info("... done.");
    }

    /**
     * Load the data from the file.
     * 
     * @param dataPath
     *            The path to the data file.
     * @throws IOException
     *             If there is an error reading the data file.
     */
    public final void loadData(String dataPath) throws IOException {
        data.clear();
        BufferedReader reader = new BufferedReader(new FileReader(new File(dataPath)));
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.isEmpty() || line.startsWith("#")) {
                continue;
            }
            String[] parts = line.split(";");
            if (parts.length > numInputColumns) {
                numInputColumns = parts.length;
                numTotalColumns = numInputColumns + 1;
            }
            double[] dataLine = new double[numTotalColumns];
            for (int j = 0; j < numInputColumns; ++j) {
                dataLine[j] = Double.valueOf(parts[j]);
            }
            data.add(dataLine);
        }
        reader.close();
    }

    /*public double evaluate(AbstractPopEvaluator evaluator, Solution<Variable<Integer>> solution, int idx) {
        String functionAsString = problem.generatePhenotype(solution).toString();
        double fitness = computeFitness(evaluator, idx);
        if (fitness < bestFitness) {
            bestFitness = fitness;
            for (int i = 0; i < numTotalColumns; ++i) {
                if (i == 0) {
                    functionAsString = functionAsString.replaceAll("getVariable\\(" + i + ",", "yr\\(");
                } else if (i == numTotalColumns - 1) {
                    functionAsString = functionAsString.replaceAll("getVariable\\(" + i + ",", "yp\\(");
                } else {
                    functionAsString = functionAsString.replaceAll("getVariable\\(" + i + ",", "u" + i + "\\(");
                }
            }
            LOGGER.info("Best FIT=" + (100 * (1 - bestFitness)) + "; Expresion=" + functionAsString);
        }
        return fitness;
    }*/

    /*public double computeFitness(AbstractPopEvaluator evaluator, int idx) {
        evaluator.evaluateExpression(idx);
        ArrayList<double[]> timeTable = evaluator.getDataTable();
        return computeFitness(timeTable);
    }*/

   /* public final void normalize(double yL, double yH) {
        LOGGER.info("Normalizing data in [" + yL + ", " + yH + "] ...");
        xLs = new double[numInputColumns];
        xHs = new double[numInputColumns];
        for (int i = 0; i < numInputColumns; ++i) {
            xLs[i] = Double.POSITIVE_INFINITY;
            xHs[i] = Double.NEGATIVE_INFINITY;
        }
        // We compute first minimum and maximum values:
        ArrayList<double[]> fullTable = new ArrayList<>();
        fullTable.addAll(dataTable);
        for (int i = 0; i < fullTable.size(); ++i) {
            double[] row = fullTable.get(i);
            for (int j = 0; j < numInputColumns; ++j) {
                if (xLs[j] > row[j]) {
                    xLs[j] = row[j];
                }
                if (xHs[j] < row[j]) {
                    xHs[j] = row[j];
                }
            }
        }

        // Now we compute "m" and "n", being y = m*x + n
        // y is the new data
        // x is the old data
        double[] m = new double[numInputColumns];
        double[] n = new double[numInputColumns];
        for (int j = 0; j < numInputColumns; ++j) {
            m[j] = (yH - yL) / (xHs[j] - xLs[j]);
            n[j] = yL - m[j] * xLs[j];
        }
        // Finally, we normalize ...
        for (int i = 0; i < fullTable.size(); ++i) {
            double[] row = fullTable.get(i);
            for (int j = 0; j < numInputColumns; ++j) {
                row[j] = m[j] * row[j] + n[j];
            }
        }

        // ... and report the values of both xLs and xHs ...
        StringBuilder xLsAsString = new StringBuilder();
        StringBuilder xHsAsString = new StringBuilder();
        for (int j = 0; j < numInputColumns; ++j) {
            if (j > 0) {
                xLsAsString.append(", ");
                xHsAsString.append(", ");
            } else {
                xLsAsString.append("xLs=[");
                xHsAsString.append("xHs=[");
            }
            xLsAsString.append(xLs[j]);
            xHsAsString.append(xHs[j]);
        }
        xLsAsString.append("]");
        xHsAsString.append("]");
        LOGGER.info(xLsAsString.toString());
        LOGGER.info(xHsAsString.toString());
        LOGGER.info("... done.");
    }*/

    /**
     * Compute the fitness of the individuals.
     * @return The fitness of the individuals.
     */
    public double computeFIT() {
        double meanXref = 0.0;
        for (int i = 0; i < data.size(); ++i) {
            meanXref += data.get(i)[0];
        }
        meanXref = meanXref / data.size();

        double num = 0.0, den = 0.0;
        for (int i = 0; i < data.size(); ++i) {
            num += Math.pow(data.get(i)[0] - data.get(i)[numInputColumns], 2.0);
            den += Math.pow(data.get(i)[0] - meanXref, 2.0);
        }
        double fit = (Math.sqrt(num) / Math.sqrt(den));
        return fit;
    }

    /**
     * Get the data table.
     * @return The data table.
     */
    public ArrayList<double[]> getData() {
        return data;
    }
    
    /**
     * Get the path to the data file.
     * @return The path to the data file.
     */
    public String getPath() {
        return path;
    }

    /**
     * Get the number of input columns.
     * @return The number of input columns.
     */
    public int getPredictorColumn() {
        return numInputColumns;
    }
}
