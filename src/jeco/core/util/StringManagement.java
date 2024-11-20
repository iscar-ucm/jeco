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
 *  - Josué Pagán Ortiz
 */
package jeco.core.util;

/**
 * This class contains methods to manage strings.
 */
public class StringManagement {
    /**
     * This method checks if a string is an integer.
     * @param s The string to check.
     * @return True if the string is an integer, false otherwise.
     */
    public static boolean isInteger(String s) {
        return isInteger(s,10);
    }
    
    /**
     * This method checks if a string is an integer.
     * @param s The string to check.
     * @param radix The radix of the number.
     * @return True if the string is an integer, false otherwise.
     */
    public static boolean isInteger(String s, int radix) {
        if(s.isEmpty()) return false;
        for(int i = 0; i < s.length(); i++) {
            if(i == 0 && s.charAt(i) == '-') {
                if(s.length() == 1) return false;
                else continue;
            }
            if(Character.digit(s.charAt(i),radix) < 0) return false;
        }
        return true;
    }
}
    

