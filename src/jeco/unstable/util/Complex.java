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
 * This class represents a complex number.
 */
public class Complex {

    /**
     * The real part of the complex number.
     */
    protected double real;
    /**
     * The imaginary part of the complex number.
     */
    protected double imag;

    /**
     * Creates a new complex number with the given real and imaginary parts.
     * 
     * @param real the real part of the complex number.
     * @param imag the imaginary part of the complex number.
     */
    public Complex(double real, double imag) {
        this.real = real;
        this.imag = imag;
    }
    
    @Override
    public Complex clone() {
        Complex clone = new Complex(this.real, this.imag);
        return clone;
    }

    // return a string representation of the invoking Complex object
    @Override
    public String toString() {
        if (imag == 0) {
            return real + "";
        }
        if (real == 0) {
            return imag + "i";
        }
        if (imag < 0) {
            return real + " - " + (-imag) + "i";
        }
        return real + " + " + imag + "i";
    }

    /**
     * Returns the absolute value of the complex number.
     * @return the absolute value of the complex number.
     */
    public double abs() {
        return Math.hypot(real, imag);
    }  // Math.sqrt(re*re + im*im)

    /**
     * Returns the phase of the complex number.
     * @return the phase of the complex number.
     */
    public double phase() {
        return Math.atan2(imag, real);
    }  // between -pi and pi

    /**
     * Returns the sum of the invoking complex number and the complex number b.
     * @param b the complex number to add.
     * @return the sum of the invoking complex number and the complex number b.
     */
    public Complex plus(Complex b) {
        Complex a = this;             // invoking object
        double re = a.real + b.real;
        double im = a.imag + b.imag;
        return new Complex(re, im);
    }

    /**
     * Returns the difference of the invoking complex number and the complex number b.
     * @param b the complex number to subtract.
     * @return the difference of the invoking complex number and the complex number b.
     */
    public Complex minus(Complex b) {
        Complex a = this;
        double re = a.real - b.real;
        double im = a.imag - b.imag;
        return new Complex(re, im);
    }

    /**
     * Returns the product of the invoking complex number and the complex number b.
     * @param b the complex number to multiply.
     * @return the product of the invoking complex number and the complex number b.
     */
    public Complex times(Complex b) {
        Complex a = this;
        double re = a.real * b.real - a.imag * b.imag;
        double im = a.real * b.imag + a.imag * b.real;
        return new Complex(re, im);
    }

    /**
     * Returns the product of the invoking complex number and the scalar alpha.
     * @param alpha the scalar to multiply.
     * @return the product of the invoking complex number and the scalar alpha.
     */
    public Complex times(double alpha) {
        return new Complex(alpha * real, alpha * imag);
    }

    /**
     * Returns the complex conjugate of the invoking complex number.
     * @return the complex conjugate of the invoking complex number.
     */
    public Complex conjugate() {
        return new Complex(real, -imag);
    }

    /**
     * Returns the reciprocal of the invoking complex number.
     * @return the reciprocal of the invoking complex number.
     */
    public Complex reciprocal() {
        double scale = real * real + imag * imag;
        return new Complex(real / scale, -imag / scale);
    }

    /**
     * Returns the real part of the complex number.
     * @return the real part of the complex number.
     */
    public double getReal() {
        return real;
    }

    /**
     * Returns the imaginary part of the complex number.
     * @return the imaginary part of the complex number.
     */
    public double getImag() {
        return imag;
    }

    /**
     * Returns the complex number that results from dividing the invoking complex number by the complex number b.
     * @param b the complex number to divide by.
     * @return the complex number that results from dividing the invoking complex number by the complex number b.
     */
    public Complex divides(Complex b) {
        Complex a = this;
        return a.times(b.reciprocal());
    }

    /**
     * Returns the exponential of the invoking complex number.
     * @return the exponential of the invoking complex number.
     */
    public Complex exp() {
        return new Complex(Math.exp(real) * Math.cos(imag), Math.exp(real) * Math.sin(imag));
    }

    /**
     * Returns the sine of the invoking complex number.
     * @return the sine of the invoking complex number.
     */
    public Complex sin() {
        return new Complex(Math.sin(real) * Math.cosh(imag), Math.cos(real) * Math.sinh(imag));
    }

    /**
     * Returns the cosine of the invoking complex number.
     * @return the cosine of the invoking complex number.
     */
    public Complex cos() {
        return new Complex(Math.cos(real) * Math.cosh(imag), -Math.sin(real) * Math.sinh(imag));
    }

    /**
     * Returns the tangent of the invoking complex number.
     * @return the tangent of the invoking complex number.
     */
    public Complex tan() {
        return sin().divides(cos());
    }

    /**
     * A static version of the plus method.
     * @param a the first complex number.
     * @param b the second complex number.
     * @return the sum of the two complex numbers.
     */
    public static Complex plus(Complex a, Complex b) {
        double re = a.real + b.real;
        double im = a.imag + b.imag;
        Complex sum = new Complex(re, im);
        return sum;
    }
}
