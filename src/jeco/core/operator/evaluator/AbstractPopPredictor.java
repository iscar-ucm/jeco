/*
* File: AbstractPopPredictor.java
* Author: José Luis Risco Martín <jlrisco@ucm.es>
* Created: 2010/09/20 (YYYY/MM/DD)
*
* Copyright (C) 2010
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
*/

package jeco.core.operator.evaluator;

import java.util.ArrayList;

import jeco.core.util.DataTable;

/**
 * Abstract class for population predictors.
 * 
 * This class defines the basic structure of a population predictor. A population
 * predictor is an object that predicts the value of a feature for a population of solutions. The predictor
 * is used by the genetic algorithm to predict the value of a feature for the population of solutions.
 */
public abstract class AbstractPopPredictor {

    /**
     * Update the predictor
     * 
     * @param data
     *            Data table
     * @param idx
     *            Index of the predictor
     */
    public abstract void updatePredictor(DataTable data, int idx);

    /**
     * Generate the class header for the predictor
     * @param threadId Thread id
     * @return The class header
     */
    public static String generateClassHeader(Integer threadId) {
        StringBuilder currentJavaFile = new StringBuilder();
        currentJavaFile.append("import java.util.ArrayList;\n\n");
        currentJavaFile.append("public class PopPredictor").append(threadId).append(" extends hero.core.operator.evaluator.AbstractPopPredictor {\n");
        return currentJavaFile.toString();
    }

    /**
     * Generate the update predictor method
     * @param phenotypes Phenotypes
     * @return The update predictor method
     */
    public static String generateUpdatePredictor(ArrayList<String> phenotypes) {
        StringBuilder currentJavaFile = new StringBuilder();
        currentJavaFile.append("\tpublic void updatePredictor(hero.core.util.DataTable data, int idx) {\n");
        currentJavaFile.append("\t\ttry {\n"); // Try
        
        // SWITCH STRUCTURE
        currentJavaFile.append("\t\t\tswitch(idx) {\n"); // Switch
        for (int i = 0; i < phenotypes.size(); ++i) {
            currentJavaFile.append("\t\t\t\tcase ").append(i).append(":\n");
            currentJavaFile.append("\t\t\t\t\tfor(int i=0; i<data.getData().size(); ++i) {\n");
            currentJavaFile.append("\t\t\t\t\t\tdouble[] row = data.getData().get(i);\n");
            currentJavaFile.append("\t\t\t\t\t\trow[data.getPredictorColumn()] = predictorNum").append(i).append("(row);\n");
            currentJavaFile.append("\t\t\t\t\t\tif(!Double.isFinite(row[data.getPredictorColumn()])) {\n");
            currentJavaFile.append("\t\t\t\t\t\t\trow[data.getPredictorColumn()] = Double.POSITIVE_INFINITY;\n");
            currentJavaFile.append("\t\t\t\t\t\t}\n");
            currentJavaFile.append("\t\t\t\t\t}\n");
            currentJavaFile.append("\t\t\t\tbreak;\n");
        }
        currentJavaFile.append("\t\t\t\tdefault:\n");
        currentJavaFile.append("\t\t\t\t\tthrow new Exception(\"There is no case for this idx.\");\n");
        currentJavaFile.append("\t\t\t}\n"); // End switch
        currentJavaFile.append("\t\t}\n"); // End try
        currentJavaFile.append("\t\tcatch (Exception ee) {\n");
        currentJavaFile.append("\t\t\t// System.err.println(ee.getLocalizedMessage());\n");
        currentJavaFile.append("\t\t}\n"); // End catch
        currentJavaFile.append("\t}\n\n");
        
        // FUNCTIONS STRUCTURE
        for (int i = 0; i < phenotypes.size(); ++i) {
            String expression = phenotypes.get(i);
            currentJavaFile.append("\tpublic double predictorNum").append(i).append("(double[] v) {\n");
            currentJavaFile.append(expression);
            currentJavaFile.append("\t}\n\n");
        }
        

        return currentJavaFile.toString();
    }

/*    public static String generateComputeNewX(ArrayList<String> phenotypes) {
        StringBuilder currentJavaFile = new StringBuilder();
        currentJavaFile.append("\tpublic double[][] computeNewX(int idx, double[][] xx) {\n");
        currentJavaFile.append("\t\tdouble[][] xxNew = new double[xx.length][];\n");
        currentJavaFile.append("\t\ttry {\n"); // Try
        currentJavaFile.append("\t\t\tswitch(idx) {\n"); // Switch

        for (int i = 0; i < phenotypes.size(); ++i) {
            String multipleExpression = phenotypes.get(i);
            String[] parts = multipleExpression.split(";");
            currentJavaFile.append("\t\t\t\tcase ").append(i).append(":\n");
            currentJavaFile.append("\t\t\t\t\tfor(int i=0; i<xx.length; ++i) {\n");
            currentJavaFile.append("\t\t\t\t\t\tdouble[] x = xx[i];\n");
            currentJavaFile.append("\t\t\t\t\t\txxNew[i] = new double[").append(parts.length).append("];\n");
            for (int j = 0; j < parts.length; ++j) {
                currentJavaFile.append("\t\t\t\t\t\txxNew[i][").append(j).append("] = ").append(parts[j]).append(";\n");
                currentJavaFile.append("\t\t\t\t\t\tif(!Double.isFinite(xxNew[i][").append(j).append("])) {\n");
                currentJavaFile.append("\t\t\t\t\t\t\txxNew[i][").append(j).append("] = 2E3;\n");
                currentJavaFile.append("\t\t\t\t\t\t}\n");
            }
            currentJavaFile.append("\t\t\t\t\t}\n");
            currentJavaFile.append("\t\t\t\tbreak;\n");
        }
        currentJavaFile.append("\t\t\t\tdefault:\n");
        currentJavaFile.append("\t\t\t\t\txxNew = null;\n");
        currentJavaFile.append("\t\t\t}\n"); // End switch
        currentJavaFile.append("\t\t}\n"); // End try
        currentJavaFile.append("\t\tcatch (Exception ee) {\n");
        currentJavaFile.append("\t\t\t// System.err.println(ee.getLocalizedMessage());\n");
        currentJavaFile.append("\t\t\txxNew = null;\n");
        currentJavaFile.append("\t\t}\n"); // End catch
        currentJavaFile.append("\t\treturn xxNew;\n");
        currentJavaFile.append("\t}\n");

        return currentJavaFile.toString();
    }*/

    /**
     * Generate the class footer for the predictor
     * @return The class footer
     */
    public static String generateClassFooter() {
        StringBuilder currentJavaFile = new StringBuilder();
        currentJavaFile.append("}\n");
        return currentJavaFile.toString();
    }

    /**
     * Generate the Java code for the class
     * @param threadId Thread id
     * @param phenotypes Phenotypes
     * @return The Java code for the class
     */
    public static String generateClassCode(Integer threadId, ArrayList<String> phenotypes) {
        StringBuilder javaCode = new StringBuilder();
        javaCode.append(generateClassHeader(threadId));
        javaCode.append(generateUpdatePredictor(phenotypes));
        //javaCode.append(generateComputeNewX(phenotypes));
        javaCode.append(generateClassFooter());
        return javaCode.toString();
    }

}
