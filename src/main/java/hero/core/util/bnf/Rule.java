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
package hero.core.util.bnf;

import java.util.ArrayList;

public class Rule extends ArrayList<Production> {

    private static final long serialVersionUID = 1L;
    //Variables
    protected boolean recursive = false;// Recursive nature of rule
    protected int minimumDepth = Integer.MAX_VALUE >> 1;	// Minimum depth of parse tree for production to map to terminal symbol(s)
    protected Symbol lhs = null; //Left hand side symbol of the rule

    public Rule() {
        super();
    }

    @Override
    public Rule clone() {
        Rule clone = new Rule();
        for (Production production : this) {
            clone.add(production.clone());
        }
        clone.lhs = this.lhs.clone();
        clone.recursive = this.recursive;
        clone.minimumDepth = this.minimumDepth;
        return clone;
    }

    public String toString() {
        StringBuilder buffer = new StringBuilder();
        buffer.append(lhs.symbolString);
        buffer.append("::=");
        for (int i = 0; i < this.size(); i++) {
            Production production = this.get(i);
            buffer.append(production.toString());
            if (i < (this.size() - 1)) {
                buffer.append("|");
            }
        }
        return buffer.toString();
    }
    /*    public Rule(Rule copy){
        super(copy);
        this.lhs = copy.lhs;
        this.recursive = copy.recursive;
        this.minimumDepth = copy.minimumDepth;
    }*/

 /*    public boolean getRecursive() {
        return recursive;
    }*/
 /*    public void setRecursive(boolean newRecursive){
        recursive=newRecursive;
    }*/
 /*    public int getMinimumDepth() {
        return minimumDepth;
    }*/
 /*    public void setMinimumDepth(int newMinimumDepth){
        minimumDepth=newMinimumDepth;
    }*/

 /*    public void setLHS(Symbol s) {
        this.lhs = s;
    }*/

 /*    public Symbol getLHS() {
        return this.lhs;
    }*/
}
