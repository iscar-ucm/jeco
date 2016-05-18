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
package hero.problem;

public class Variable<T> {
    protected T value;

    public Variable(T value) {
        this.value = value;
    }

    public T getValue() { return value; }

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
