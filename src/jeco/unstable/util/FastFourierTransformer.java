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
 *  - Josué Pagán Ortiz
 */
package jeco.unstable.util;

/**
 * This class is used to perform Fast Fourier Transforms.
 */
public class FastFourierTransformer {

    /**
     * Creates a new FastFourierTransformer.
     */
    public FastFourierTransformer() {
    }

    /**
     * Completes the array with zeros until the next power of 2.
     * 
     * @param x the array to complete.
     * @return the array completed with zeros.
     */
    public static Complex[] completeWithZero(Complex[] x) {
        int powerOfTwo = 1;
        long maxPowerOfTo = 2147483648L;
        while (powerOfTwo < x.length && powerOfTwo < maxPowerOfTo) {
            powerOfTwo *= 2;
        }
        Complex[] xx = new Complex[powerOfTwo];
        for (int i = 0; i < x.length; ++i) {
            xx[i] = x[i];
        }
        for (int i = x.length; i < powerOfTwo; ++i) {
            xx[i] = new Complex(0, 0);
        }
        return xx;
    }
    
    /**
     * Completes the array with zeros until the next power of 2.
     * @param cc the array to complete.
     * @param length the length of the array to complete.
     * @return the array completed with zeros.
     */
    public static Complex[] zeroPadding(Complex[] cc, int length) {
        Complex ZERO = new Complex(0, 0);

        int finalLength = cc.length;
        while (finalLength < length) {
            finalLength += 1;
        }
        
        Complex[] c = new Complex[finalLength];
        
        if (cc.length < length){
            for (int i = 0; i < cc.length; ++i) {
                c[i] = cc[i];
            }
            for (int i = cc.length; i < length; ++i) {
                c[i] = ZERO;
            }
        } else {
            c = cc;
        }
        // Complete 'till the next power of 2
        return completeWithZero(c);
    }

    /**
     * Transforms an array of doubles into an array of Complex numbers.
     * @param x the array of doubles.
     * @return the array of Complex numbers.
     */
    public static Complex[] doubleToComplex(double[] x) {
        Complex[] c = new Complex[x.length];
        
        for (int i=0; i<x.length; i++) {
            c[i] = new Complex(x[i], 0);
        }
        return c;
    }
    
    /**
     * Computes the FFT of the array of Complex numbers.
     * @param xx the array of Complex numbers.
     * @return the FFT of the array of Complex numbers.
     */
    public static Complex[] fft(Complex[] xx) {
        Complex[] x = completeWithZero(xx);
        int N = x.length;
        
        // base case
        if (N == 1) {
            return new Complex[]{x[0]};
        }
        
        // radix 2 Cooley-Tukey FFT
        if (N % 2 != 0) {
            throw new RuntimeException("N is not a power of 2");
        }
        
        // fft of even terms
        Complex[] even = new Complex[N / 2];
        for (int k = 0; k < N / 2; k++) {
            even[k] = x[2 * k];
        }
        Complex[] q = fft(even);
        
        // fft of odd terms
        Complex[] odd = even;  // reuse the array
        for (int k = 0; k < N / 2; k++) {
            odd[k] = x[2 * k + 1];
        }
        Complex[] r = fft(odd);
        
        // combine
        Complex[] y = new Complex[N];
        for (int k = 0; k < N / 2; k++) {
            double kth = -2 * k * Math.PI / N;
            Complex wk = new Complex(Math.cos(kth), Math.sin(kth));
            y[k] = q[k].plus(wk.times(r[k]));
            y[k + N / 2] = q[k].minus(wk.times(r[k]));
        }
        return y;
    }
    
    /**
     * Computes the FFT of the array of doubles.
     * @param x the array of doubles.
     * @return the FFT of the array of doubles.
     */
    public static Complex[] fft(double[] x) {
        return fft(doubleToComplex(x));
    }
    
    /**
     * Computes the inverse FFT of the array of Complex numbers.
     * @param x the array of Complex numbers.
     * @return the inverse FFT of the array of Complex numbers.
     */
    public static Complex[] ifft(Complex[] x) {
        int N = x.length;
        Complex[] y = new Complex[N];
        
        // take conjugate
        for (int i = 0; i < N; i++) {
            y[i] = x[i].conjugate();
        }
        
        // compute forward FFT
        y = fft(y);
        
        // take conjugate again
        for (int i = 0; i < N; i++) {
            y[i] = y[i].conjugate();
        }
        
        // divide by N
        for (int i = 0; i < N; i++) {
            y[i] = y[i].times(1.0 / N);
        }
        
        return y;
    }
    
    /**
     * Computes the inverse FFT of the array of doubles.
     * @param x the array of doubles.
     * @return the inverse FFT of the array of doubles.
     */
    public static Complex[] ifft(double[] x) {
        return ifft(doubleToComplex(x));
    }
    
    /**
     * Computes the circular convolution of two arrays of Complex numbers.
     * @param xx the first array of Complex numbers.
     * @param yy the second array of Complex numbers.
     * @return the circular convolution of the two arrays of Complex numbers.
     */
    public static Complex[] cconvolve(Complex[] xx, Complex[] yy) {
        // Pad x and y with 0s so that they have same length
        // and are powers of 2
        Complex[] x = zeroPadding(xx, yy.length);
        Complex[] y = zeroPadding(yy, xx.length);
        
        // compute FFT of each sequence
        Complex[] a = fft(x);
        Complex[] b = fft(y);
        
        if (a.length != b.length) {
            System.out.println("Dimensions don't agree"); 
        }

        // point-wise multiply
        Complex[] c = new Complex[a.length];
        for (int i = 0; i < a.length; i++) {
            c[i] = a[i].times(b[i]);
        }
        
        // compute inverse FFT
        return ifft(c);
    }
    

    /**
     * Computes the circular convolution of two arrays of doubles.
     * @param x the first array of doubles.
     * @param y the second array of doubles.
     * @return the circular convolution of the two arrays of doubles.
     */
    public static Complex[] cconvolve(double[] x, double[] y) {
        return cconvolve(doubleToComplex(x), doubleToComplex(y));
    }
    
    /**
     * Computes the linear convolution of two arrays of Complex numbers.
     * @param x the first array of Complex numbers.
     * @param y the second array of Complex numbers.
     * @return the linear convolution of the two arrays of Complex numbers.
     */
    public static Complex[] convolve(Complex[] x, Complex[] y) {
        Complex ZERO = new Complex(0, 0);

        Complex[] a = new Complex[2 * x.length];
        for (int i = 0; i < x.length; i++) {
            a[i] = x[i];
        }
        for (int i = x.length; i < 2 * x.length; i++) {
            a[i] = ZERO;
        }

        Complex[] b = new Complex[2 * y.length];
        for (int i = 0; i < y.length; i++) {
            b[i] = y[i];
        }
        for (int i = y.length; i < 2 * y.length; i++) {
            b[i] = ZERO;
        }

        return cconvolve(a, b);
    }

    /**
     * Computes the linear convolution of two arrays of doubles.
     * @param x the first array of doubles.
     * @param y the second array of doubles.
     * @return the linear convolution of the two arrays of doubles.
     */
    public static Complex[] convolve(double[] x, double[] y) {
        return convolve(doubleToComplex(x), doubleToComplex(y));
    }

    /**
     * Shows the array of Complex numbers.
     * @param x the array of Complex numbers.
     * @param title the title of the array.
     */
    public static void show(Complex[] x, String title) {
        System.out.println(title);
        System.out.println("-------------------");
        for (Complex x1 : x) {
            System.out.println(x1);
        }
        System.out.println();
    }
}
