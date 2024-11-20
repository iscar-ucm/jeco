/*
 * Copyright (C) 2010 José Luis Risco Martín <jlrisco@ucm.es> and Josué 
 * Pagán Ortiz <j.pagan@upm.es>
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
 *  - Josué Pagán Ortiz
 */
package jeco.unstable.util;

import java.util.Collections;
import java.util.List;

/**
 * This class contains a set of mathematical functions that are used in the
 * calculation of the statistics of the data.
 */
public class Maths {
    
    /**
     * This method calculates the sum of a list of numbers.
     * @param numbers List of numbers
     * @return The sum of the numbers
     */
    public static double sum(List<Double> numbers) {
        double res = 0;
        for (Double number : numbers) {
            res += number;
        }
        return res;
    }
    
    /**
     * This method calculates the sum of an array of numbers.
     * @param numbers Array of numbers
     * @return The sum of the numbers
     */
    public static double sum(double[] numbers) {
        double res = 0;
        if (!Double.isNaN(numbers[0])){
            for (Double number : numbers) {
                res += number;
            }
            return res;
        }
        else {
            return Double.POSITIVE_INFINITY;
        }
    }
    
    /**
     * This method calculates the mean of a list of numbers.
     * @param numbers List of numbers
     * @return The mean of the numbers
     */
    public static double mean(List<Double> numbers) {
        if (numbers.isEmpty()) {
            return 0;
        }
        double res = sum(numbers) / numbers.size();
        return res;
    }
    
    /**
     * This method calculates the mean of an array of numbers.
     * @param numbers Array of numbers
     * @return The mean of the numbers
     */
    public static double mean(double[] numbers) {
        if (!Double.isNaN(numbers[0])){
            double res = sum(numbers)/numbers.length;
            return res;
        }
        else {
            return Double.POSITIVE_INFINITY;
        }
    }
    
    /**
     * This method calculates the median of a list of numbers.
     * @param numbers List of numbers
     * @return The median of the numbers
     */
    public static double median(List<Double> numbers) {
        Collections.sort(numbers);
        int middle = numbers.size() / 2;
        if (numbers.size() % 2 == 1) {
            return numbers.get(middle);
        } else {
            return (numbers.get(middle - 1) + numbers.get(middle)) / 2.0;
        }
    }
    
    /**
     * This method calculates the standard deviation of a list of numbers.
     * @param numbers List of numbers
     * @return The standard deviation of the numbers
     */
    public static double std(List<Double> numbers) {
        double res = 0;
        double avg = mean(numbers);
        for(Double number : numbers) {
            res += Math.pow(number-avg, 2);
        }
        res = Math.sqrt(res/(numbers.size()-1));
        return res;
    }
    
    /**
     * This method calculates the standard deviation of an array of numbers.
     * @param numbers Array of numbers
     * @return The standard deviation of the numbers
     */
    public static double std(double[] numbers) {
        if (!Double.isNaN(numbers[0])){
            double res = 0;
            double avg = mean(numbers);
            for (int i = 0; i <= numbers.length-1; i++) {
                res += Math.pow(numbers[i] - avg, 2);
            }
            res = Math.sqrt(res/numbers.length);
            return res;
        }
        else {
            return Double.POSITIVE_INFINITY;
        }
    }
    
    /**
     * This method calculates the minimum of a list of numbers.
     * @param numbers List of numbers
     * @return The minimum of the numbers
     */
    public static double min(double[] numbers) {
        if (!Double.isNaN(numbers[0])){
            double res = Double.POSITIVE_INFINITY;
            for(int i=0; i<=numbers.length-1; i++){
                if (numbers[i] < res) {
                    res = numbers[i];
                }
            }
            return res;
        }
        else {
            return Double.POSITIVE_INFINITY;
        }
    }
    
    /**
     * This method calculates the maximum of a list of numbers.
     * @param numbers List of numbers
     * @return The maximum of the numbers
     */
    public static double max(double[] numbers) {
        if (!Double.isNaN(numbers[0])){
            double res = Double.NEGATIVE_INFINITY;
            for(int i=0; i<=numbers.length-1; i++){
                if (numbers[i] > res) {
                    res = numbers[i];
                }
            }
            return res;
        }
        else {
            return Double.POSITIVE_INFINITY;
        }
    }
    
    /**
     * This method calculates the variance of a list of numbers.
     * @param numbers List of numbers
     * @return The variance of the numbers
     */
    public static double totalVar(double[] numbers) {
        if (!Double.isNaN(numbers[0])){
            double res = 0.0;
            double[] derv = new double[numbers.length-1];
            for(int i=0; i<=numbers.length-2; i++){
                derv[i] = numbers[i+1]-numbers[i];
            }
            for (int i = 0; i <= derv.length-1; i++) {
                res += Math.abs(derv[i]);
            }
            return res;
        }
        else {
            return Double.POSITIVE_INFINITY;
        }
    }
    
    /**
     * This method calculates the power of a list of numbers.
     * @param numbers List of numbers
     * @return The power of the numbers
     */
    public static double pod(double[] numbers) {
        if (!Double.isNaN(numbers[0])){
            double res = 1.0;
            for (int i = 0; i <= numbers.length-1; i++) {
                res *= numbers[i];
            }
            return res;
        }
        else {
            return Double.POSITIVE_INFINITY;
        }
    }
    
    /**
     * This method calculates the geometric mean of a list of numbers.
     * @param numbers List of numbers
     * @return The geometric mean of the numbers
     */
    public static double geoMean(double[] numbers) {
        if (!Double.isNaN(numbers[0])){
            double res = Math.pow(pod(numbers), 1/(numbers.length));
            return res;
        }
        else {
            return Double.POSITIVE_INFINITY;
        }
    }
    
    /**
     * This method calculates the power of a list of numbers.
     * @param numbers List of numbers
     * @param exp Exponent
     * @return The power of the numbers
     */
    public static double[] pow(double[] numbers, double exp) {
        if (!Double.isNaN(numbers[0])){
            for (int i = 0; i <= numbers.length-1; i++) {
                numbers[i] = Math.pow(numbers[i], exp);
            }
            return numbers;
        }
        else {
            return new double[] {Double.NaN};
        }
    }
    
    /**
     * This method calculates the convolution of two lists of numbers.
     * @param x The first list of numbers
     * @param h The second list of numbers
     * @return The convolution of the two lists
     */
    public static double[] conv(double[] x, double[] h) {
        if ((!Double.isNaN(x[0])) && (!Double.isNaN(h[0]))){
            double[] res = new double[x.length + h.length - 1];
            for (int i = 0; i <= res.length -1; i++ )	{
                res[i] = 0;                       // set to zero before sum
                for (int j = 0; j <= h.length-1; j++ ) {
                    if ((j <= i) && ((i-j) < x.length)) {
                        res[i] += x[i - j] * h[j];    // convolve: multiply and accumulate
                    }
                }
            }
            return res;
        }
        else {
            return new double[] {Double.NaN};
        }
    }
    
    /** 
     * This method calculates the difference in a list of numbers.
     * @param numbers List of numbers
     * @return The difference of the numbers
     */
    public static double[] diff(double[] numbers) {
        if (!Double.isNaN(numbers[0])){
            double[] res = new double[numbers.length-1];
            for (int i = 1; i <= numbers.length-1; i++) {
                res[i-1] = numbers[i] - numbers[i-1];
            }
            return res;
        }
        else {
            return new double[] {Double.NaN};
        }
    }
    
    /**
     * This method calculates the absolute value of a list of numbers.
     * @param numbers List of numbers
     * @return The absolute value of the numbers
     */
    public static double[] abs(double[] numbers) {
        if (!Double.isNaN(numbers[0])){
            for (int i = 0; i <= numbers.length-1; i++) {
                numbers[i] = Math.abs(numbers[i]);
            }
            return numbers;
        }
        else {
            return new double[] {Double.NaN};
        }
    }
    
    /**
     * This method calculates the absolute value of a list of complex numbers.
     * @param c List of complex numbers
     * @return The absolute value of the complex numbers
     */
    public static double[] abs(Complex[] c) {
        if (!Double.isNaN(c[0].getReal()) && !Double.isNaN(c[0].getImag())){            
            double[] a = new double[c.length];
            
            for (int i=0; i<c.length; i++){
                a[i] = c[i].abs();
            }
            return a;  
        }
        else {
            return new double[] {Double.NaN};
        }
    }
}
