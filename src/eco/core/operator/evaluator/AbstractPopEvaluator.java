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
package eco.core.operator.evaluator;

import java.util.ArrayList;

/**
 * @author José Luis Risco Martín
 * @author Josué Pagán Ortiz
 */
public abstract class AbstractPopEvaluator {
    
    protected ArrayList<double[]> dataTable;
    protected int[] dataLimits;
    protected ArrayList <String> featuresNames;
    
    public abstract void evaluateExpression(int idxExpr);
    public abstract double evaluate(int idxExpr, int k);
    
    public void setDataTable(ArrayList<double[]> dataTable) {
        this.dataTable = dataTable;
    }
    
    public void setDataLimits(int[] dataLimits) {
        this.dataLimits = dataLimits;
    }
    
    public void setFeaturesNames(ArrayList names){
        this.featuresNames = names;
    }
    
    public ArrayList<double[]> getDataTable() {
        return dataTable;
    }
    
    public int[] getDataLimits(int ex, int f) {
        int[] limits = new int[2];
        limits[0] = dataLimits[4*ex+2*f];
        limits[1] = dataLimits[4*ex+2*f+1];
        return limits;
    }
    
    public int getDataLimits(String name) {
        int limit = Integer.MAX_VALUE;
        if ((featuresNames.indexOf(name) > 0) && (featuresNames.indexOf(name) != Integer.MAX_VALUE)){
            limit = featuresNames.indexOf(name);
        }        
        return limit;
    }
    
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
