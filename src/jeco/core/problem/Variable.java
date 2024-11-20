/*
* File: Variable.java
* Author: José Luis Risco Martín <jlrisco@ucm.es>
* Created: 2010/04/26 (YYYY/MM/DD)
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
package jeco.core.problem;

/**
 * Class representing a variable in a problem.
 * @param <T> Type of the variable.
 */
public class Variable<T> {
     /**
     * Value of the variable.
     */
    protected T value;

     /**
     * Constructor.
     * @param value Value of the variable.
     */
    public Variable(T value) {
        this.value = value;
    }

    /**
     * Get the value of the variable.
     * @return Value of the variable.
     */
    public T getValue() { return value; }

    /**
     * Set the value of the variable.
     * @param value Value of the variable.
     */
    public void setValue(T value) { this.value = value; }
    
    @Override
    public Variable<T> clone() {
        return new Variable<T>(value);
    }

    @SuppressWarnings("unchecked")
	@Override
    public boolean equals(Object right) {
        Variable<T> var = (Variable<T>)right;
        return this.value.equals(var.value);
    }
}
