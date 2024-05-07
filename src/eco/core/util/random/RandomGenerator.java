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
package eco.core.util.random;

import java.util.Random;

public class RandomGenerator {

    protected static Random randomGenerator = new Random();

    public static void setSeed(long seed) {
        randomGenerator.setSeed(seed);
    }

    public static double nextDouble() {
        return randomGenerator.nextDouble();
    }

    public static double nextDouble(double lowerBound, double upperBound) {
        return lowerBound + (upperBound - lowerBound) * randomGenerator.nextDouble();
    }

    public static double nextDouble(double upperBound) {
        return upperBound * randomGenerator.nextDouble();
    }

    public static int nextInt(int lowerBound, int upperBound) {
        return ((upperBound-lowerBound)<=0)? 0 : lowerBound + randomGenerator.nextInt(upperBound-lowerBound);
    }

    public static int nextInteger(int lowerBound, int upperBound) {
        return nextInt(lowerBound, upperBound);
    }

    public static int nextInt(int upperBound) {
        return randomGenerator.nextInt(upperBound);
    }

    public static int nextInteger(int upperBound) {
        return nextInt(upperBound);
    }

    public static boolean nextBoolean() {
        return randomGenerator.nextBoolean();
    }

    public static int[] intPermutation(int length) {
        int[] aux = new int[length];
        int[] result = new int[length];

		// First, create an array from 0 to length - 1. 
        // Also is needed to create an random array of size length
        for (int i = 0; i < length; i++) {
            result[i] = i;
            aux[i] = RandomGenerator.nextInt(0, length);
        } // for

		// Sort the random array with effect in result, and then we obtain a
        // permutation array between 0 and length - 1
        for (int i = 0; i < length; i++) {
            for (int j = i + 1; j < length; j++) {
                if (aux[i] > aux[j]) {
                    int tmp;
                    tmp = aux[i];
                    aux[i] = aux[j];
                    aux[j] = tmp;
                    tmp = result[i];
                    result[i] = result[j];
                    result[j] = tmp;
                } // if
            } // for
        } // for

        return result;
    }// intPermutation
    
    public static void main(String[] args) {
        for(int i=-10; i<10; ++i) {
            System.out.println("RandInt(0," + i + ")="+RandomGenerator.nextInt(0, i));
        }
    }

}
