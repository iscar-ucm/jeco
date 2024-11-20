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
 */
package jeco.core.util.bnf;

/**
 * Class to represent a symbol in a grammar
 * 
 * @version 1.0
 */
public class Symbol {
	/**
	 * Enum to represent the type of the symbol
	 */
	public static enum SYMBOL_TYPE {
		NT_SYMBOL, T_SYMBOL
	};

	// Variables
	/**
	 * Symbol type
	 */
	protected SYMBOL_TYPE type; // Symbol type
	/**
	 * Symbol string
	 */
	protected String symbolString;

	/*
	 * public Symbol() { this.type = SYMBOL_TYPE.T_SYMBOL; this.symbolString = "";
	 * }
	 */

	/**
	 * Constructor
	 * 
	 * @param symbolString Symbol string
	 * @param type         Symbol type
	 */
	public Symbol(String symbolString, SYMBOL_TYPE type) {
		this.type = type;
		this.symbolString = symbolString;
	}

	/**
	 * Constructor
	 * 
	 * @param type Symbol type
	 */
	public Symbol(SYMBOL_TYPE type) {
		this("", type);
	}

	/**
	 * Constructor
	 */
	public Symbol() {
		this(Symbol.SYMBOL_TYPE.T_SYMBOL);
	}

	@Override
	public Symbol clone() {
		Symbol clone = new Symbol(this.symbolString, this.type);
		return clone;
	}

	/**
	 * Check if a symbol is equal to another
	 * 
	 * @param right Symbol to compare
	 * @return True if the symbols are equal, false otherwise
	 */
	public boolean equals(Symbol right) {
		return symbolString.equals(right.symbolString) && (type == right.type);
	}

	/**
	 * Checks if a symbol is terminal
	 * 
	 * @return True if the symbol is terminal, false otherwise
	 */
	public boolean isTerminal() {
		return type == SYMBOL_TYPE.T_SYMBOL;
	}

	/*
	 * public boolean equals(Symbol newSymbol) { //Check the symbolString and the
	 * type return (getSymbolString().equals(newSymbol.getSymbolString())) &&
	 * (getType() == newSymbol.getType()); }
	 * 
	 * public boolean equals(String newSymbol) { //Check the symbolString and the
	 * type return (getSymbolString().equals(newSymbol)); }
	 */

	/*
	 * public void clear() { this.symbolString = null; this.type = null; }
	 */

	@Override
	public String toString() {
		return this.symbolString;
	}
}
