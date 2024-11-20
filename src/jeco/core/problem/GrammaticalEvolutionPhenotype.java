/*
* File: GrammaticalEvolutionPhenotype.java
* Author: José Luis Risco Martín <jlrisco@ucm.es>
* Created: 2010/05/31 (YYYY/MM/DD)
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

import java.util.LinkedList;

/**
 * Class to represent the phenotype of a GE individual
 */
public class GrammaticalEvolutionPhenotype extends LinkedList<String> {

	public String toString() {
		StringBuilder buffer = new StringBuilder();
		for(String symbol : this) {
			buffer.append(symbol);
		}
		return buffer.toString();
	}
}
