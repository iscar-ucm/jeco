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
package hero.core.operator.evaluator;

import java.util.ArrayList;

/**
 *
 * @author José Luis Risco Martín
 */
public abstract class AbstractPopPredictor {

    public abstract double[] computeYP(int idx, double[][] xx);

    public abstract double[][] computeNewX(int idx, double[][] xx);

    public static String generateClassHeader(Integer threadId) {
        StringBuilder currentJavaFile = new StringBuilder();
        currentJavaFile.append("import java.util.ArrayList;\n\n");
        currentJavaFile.append("public class PopPredictor").append(threadId).append(" extends algorithm.AbstractPopPredictor {\n");
        return currentJavaFile.toString();
    }

    public static String generateComputeYp(ArrayList<String> phenotypes) {
        StringBuilder currentJavaFile = new StringBuilder();
        currentJavaFile.append("\tpublic double[] computeYP(int idx, double[][] xx) {\n");
        currentJavaFile.append("\t\tdouble[] yP = new double[xx.length];\n");
        currentJavaFile.append("\t\tfor(int i=0; i<yP.length; ++i) {\n");
        currentJavaFile.append("\t\t\tyP[i] = Double.NaN;\n");
        currentJavaFile.append("\t\t}\n");
        currentJavaFile.append("\t\ttry {\n"); // Try
        currentJavaFile.append("\t\t\tswitch(idx) {\n"); // Switch

        for (int i = 0; i < phenotypes.size(); ++i) {
            String multipleExpression = phenotypes.get(i);
            String[] parts = multipleExpression.split(";");
            String singleExpression = parts[0];
            currentJavaFile.append("\t\t\t\tcase ").append(i).append(":\n");
            currentJavaFile.append("\t\t\t\t\tfor(int i=0; i<yP.length; ++i) {\n");
            currentJavaFile.append("\t\t\t\t\t\tdouble[] x = xx[i];\n");
            currentJavaFile.append("\t\t\t\t\t\tyP[i] = ").append(singleExpression).append(";\n");
            currentJavaFile.append("\t\t\t\t\t\tif(!Double.isFinite(yP[i])) {\n");
            currentJavaFile.append("\t\t\t\t\t\t\tyP[i] = Double.POSITIVE_INFINITY;\n");
            currentJavaFile.append("\t\t\t\t\t\t}\n");
            currentJavaFile.append("\t\t\t\t\t}\n");
            currentJavaFile.append("\t\t\t\tbreak;\n");
        }
        currentJavaFile.append("\t\t\t\tdefault:\n");
        currentJavaFile.append("\t\t\t\t\tyP = null;\n");
        currentJavaFile.append("\t\t\t}\n"); // End switch
        currentJavaFile.append("\t\t}\n"); // End try
        currentJavaFile.append("\t\tcatch (Exception ee) {\n");
        currentJavaFile.append("\t\t\t// System.err.println(ee.getLocalizedMessage());\n");
        currentJavaFile.append("\t\t\tyP = null;\n");
        currentJavaFile.append("\t\t}\n"); // End catch
        currentJavaFile.append("\t\treturn yP;\n");
        currentJavaFile.append("\t}\n");

        return currentJavaFile.toString();
    }

    public static String generateComputeNewX(ArrayList<String> phenotypes) {
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
    }

    public static String generateClassFooter() {
        StringBuilder currentJavaFile = new StringBuilder();
        currentJavaFile.append("}\n");
        return currentJavaFile.toString();
    }

    public static String generateClassCode(Integer threadId, ArrayList<String> phenotypes) {
        StringBuilder javaCode = new StringBuilder();
        javaCode.append(generateClassHeader(threadId));
        javaCode.append(generateComputeYp(phenotypes));
        javaCode.append(generateComputeNewX(phenotypes));
        javaCode.append(generateClassFooter());
        return javaCode.toString();
    }

}
