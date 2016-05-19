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
package hero.core.util.compiler;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Compiles source and also makes sure that reloading a compiled class does not
 * "caches" the first compiled class.
 *
 * @author José Luis Risco Martín
 * @author J. M. Colmenar
 */
public class MyLoader extends ClassLoader {

    private static final Logger logger = Logger.getLogger(MyLoader.class.getName());
    protected String compilationDir;

    public MyLoader(String compilationDir) {
        this.compilationDir = compilationDir;
    }

    @Override
    public Class<?> loadClass(String className) {
        return findClass(className);
    }

    @Override
    public Class<?> findClass(String className) {
        try {
            byte[] bytes = loadClassData(className);
            Class<?> classObject = super.defineClass(className, bytes, 0, bytes.length);
            return classObject;
        } catch (IOException ex1) {
            try {
                return super.loadClass(className);
            } catch (ClassNotFoundException ex2) {
                logger.info(ex2.getLocalizedMessage());
            }
            logger.severe(ex1.getLocalizedMessage());
            return null;
        }
    }

    private byte[] loadClassData(String className) throws IOException {
        File f = new File(compilationDir + File.separator + className + ".class");
        int size = (int) f.length();
        byte buff[] = new byte[size];
        FileInputStream fis = new FileInputStream(f);
        DataInputStream dis = new DataInputStream(fis);
        dis.readFully(buff);
        dis.close();
        return buff;
    }
}
