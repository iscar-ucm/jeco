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
package hero.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Logger;
import hero.algorithm.metaheuristic.moge.AbstractProblemGE;
import hero.operator.evaluator.AbstractPopEvaluator;
import hero.problem.Solution;
import hero.problem.Variable;

/**
 * Class to manage a normalized data table. Originally, the data table is passed
 * to this class as a regular data table. After the constructor, the data table
 * is normalized in the interval [1,2].
 *
 * @author José Luis Risco Martín
 */
public class DataTable {

    private static final Logger LOGGER = Logger.getLogger(DataTable.class.getName());

    protected AbstractProblemGE problem;
    protected String dataPath = null;
    protected ArrayList<double[]> dataTable = new ArrayList<>();
    protected int idxBegin = -1;
    protected int idxEnd = -1;
    protected int numInputColumns = 0;
    protected int numTotalColumns = 0;
    protected double[] xLs = null;
    protected double[] xHs = null;

    protected double bestFitness = Double.POSITIVE_INFINITY;

    public DataTable(AbstractProblemGE problem, String dataPath, int idxBegin, int idxEnd) throws IOException {
        this.problem = problem;
        this.dataPath = dataPath;
        LOGGER.info("Reading data file ...");
        fillDataTable(dataPath, dataTable);
        this.idxBegin = (idxBegin == -1) ? 0 : idxBegin;
        this.idxEnd = (idxEnd == -1) ? dataTable.size() : idxEnd;
        LOGGER.info("Evaluation interval: [" + this.idxBegin + "," + this.idxEnd + ")");
        LOGGER.info("... done.");
    }

    public DataTable(AbstractProblemGE problem, String trainingPath) throws IOException {
        this(problem, trainingPath, -1, -1);
    }

    public final void fillDataTable(String dataPath, ArrayList<double[]> dataTable) throws IOException {
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
            dataTable.add(dataLine);
        }
        reader.close();
    }

    public double evaluate(AbstractPopEvaluator evaluator, Solution<Variable<Integer>> solution, int idx) {
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
    }

    public double computeFitness(AbstractPopEvaluator evaluator, int idx) {
        evaluator.evaluateExpression(idx);
        ArrayList<double[]> timeTable = evaluator.getDataTable();
        return computeFitness(timeTable);
    }

    public final void normalize(double yL, double yH) {
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
    }

    public double computeFitness(ArrayList<double[]> timeTable) {
        double meanXref = 0.0;
        for (int i = idxBegin; i < idxEnd; ++i) {
            meanXref += timeTable.get(i)[0];
        }
        meanXref = meanXref / (idxEnd - idxBegin);

        double num = 0.0, den = 0.0;
        double fitness = 0;
        for (int i = idxBegin; i < idxEnd; ++i) {
            num += Math.pow(timeTable.get(i)[0] - timeTable.get(i)[numInputColumns], 2.0);
            den += Math.pow(timeTable.get(i)[0] - meanXref, 2.0);
        }
        fitness = (Math.sqrt(num) / Math.sqrt(den));
        return fitness;
    }

    public ArrayList<double[]> getTrainingTable() {
        return dataTable;
    }

}
