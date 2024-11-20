/*
* File: AbstractPopEvaluator.java
* Author: José Luis Risco Martín <jlrisco@ucm.es>
* Author: Josué Pagán Ortiz <j.pagan@upm.es>
* Created: 2010/09/20 (YYYY/MM/DD)
* Modified: YYYY/MM/DD by NAME
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

/**
 * Abstract class for population evaluators.
 * 
 * This class defines the basic structure of a population evaluator. A population
 * evaluator is an object that evaluates a population of solutions. The evaluator
 * is used by the genetic algorithm to evaluate the population of solutions.
 */
public abstract class AbstractPopEvaluator {
    
    /**
     * Data table
     */
    protected ArrayList<double[]> dataTable;
    /**
     * Data limits
     */
    protected int[] dataLimits;
    /**
     * Features names
     */
    protected ArrayList <String> featuresNames;
    
    /**
     * Evaluate the expression
     * 
     * @param idxExpr
     *            Index of the expression
     */
    public abstract void evaluateExpression(int idxExpr);

    /**
     * Evaluate the expression
     * 
     * @param idxExpr
     *            Index of the expression
     * @param k
     *            Index of the data
     */
    public abstract double evaluate(int idxExpr, int k);
    
    /**
     * Set the data table
     * @param dataTable Data table
     */
    public void setDataTable(ArrayList<double[]> dataTable) {
        this.dataTable = dataTable;
    }
    
    /**
     * Set the data limits
     * @param dataLimits Data limits
     */
    public void setDataLimits(int[] dataLimits) {
        this.dataLimits = dataLimits;
    }
    
    /**
     * Set the features names
     * @param names Features names
     */
    public void setFeaturesNames(ArrayList names){
        this.featuresNames = names;
    }
    
    /**
     * Get the data table
     * @return Data table
     */
    public ArrayList<double[]> getDataTable() {
        return dataTable;
    }
    
    /**
     * Get the data limits
     * @return Data limits
     */
    public int[] getDataLimits(int ex, int f) {
        int[] limits = new int[2];
        limits[0] = dataLimits[4*ex+2*f];
        limits[1] = dataLimits[4*ex+2*f+1];
        return limits;
    }
    
    /**
     * Get the data limits
     * @param name Feature name
     * @return Data limits
     */
    public int getDataLimits(String name) {
        int limit = Integer.MAX_VALUE;
        if ((featuresNames.indexOf(name) > 0) && (featuresNames.indexOf(name) != Integer.MAX_VALUE)){
            limit = featuresNames.indexOf(name);
        }        
        return limit;
    }
    
    /**
     * Get the data table
     * @param name Feature name
     * @param k Index of the data
     * @return Data table
     */
    public double getDataTable(String name, int k){
        int idxVar = getDataLimits(name);
        if (idxVar != Integer.MAX_VALUE){
            if (k < 0) {
                return dataTable.get(0)[idxVar];
            }
            else if (k >= dataTable.size()) {
                return dataTable.get(dataTable.size()-1)[idxVar];
            }
            else {
                return dataTable.get(k)[idxVar];
            }
        } else {
            return Double.NaN;
        }
    }

    /**
     * Get the data table
     * @param idxVar Index of the variable
     * @param k Index of the data
     * @return Data table
     */
    public double getDataTable(int idxVar, int k){
        if (k < 0) {            
           return dataTable.get(0)[idxVar];
        }
        else if (k >= dataTable.size()) {
            return dataTable.get(dataTable.size()-1)[idxVar];
        }
        else {
            return dataTable.get(k)[idxVar];
        }
    }
}
