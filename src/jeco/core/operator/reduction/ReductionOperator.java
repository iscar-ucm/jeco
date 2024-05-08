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
package jeco.core.operator.reduction;

import java.util.Collections;

import jeco.core.operator.comparator.SolutionDominance;
import jeco.core.problem.Solutions;
import jeco.core.problem.Variable;

/**
 * ReplacementOperation removes replacementSize Individuals from the population
 * @author José L. Risco-Martín
 */
public class ReductionOperator<T extends Variable<?>> {

	public enum REDUCTION_TYPE {

		STEADY_STATE, GENERATIONAL
	};
	protected int reductionSize;
	protected double valueD;
	protected REDUCTION_TYPE reductionType;

	/** Creates a new instance of ReplacementOperation
	 * @param replacementSize size
	 */
	public ReductionOperator(int replacementSize) {
		this.reductionSize = replacementSize;
		this.valueD = -1.0;
	}

	public ReductionOperator(double valueD) throws Exception {
		if (valueD < 0 || valueD > 1) {
			throw new Exception("valueD must be in the range [0,1]");
		}
		this.reductionSize = -1;
		this.valueD = valueD;
	}

	public ReductionOperator(REDUCTION_TYPE replacementType) {
		this.reductionSize = -1;
		this.valueD = -1.0;
		this.reductionType = replacementType;
	}

	public ReductionOperator() {
		this(REDUCTION_TYPE.GENERATIONAL);
	}

	public Solutions<T> execute(Solutions<T> arg) {
		Solutions<T> solutions = new Solutions<T>();
		solutions.addAll(arg);


		int popSize = solutions.size();

		if (reductionSize <= 0) {
			if (valueD >= 0) {
				reductionSize = (int) (popSize * valueD);
			} else if (reductionType.equals(REDUCTION_TYPE.STEADY_STATE)) {
				valueD = 1.0 / popSize;
				reductionSize = (int) (popSize * valueD);
			} else if (reductionType.equals(REDUCTION_TYPE.GENERATIONAL)) {
				valueD = 1.0;
				reductionSize = (int) (popSize * valueD);
			}
		}

		SolutionDominance<T> comparator = new SolutionDominance<T>();
		Collections.sort(solutions, comparator);

		for (int i = 0; i < reductionSize; ++i) {
			solutions.remove(solutions.size() - 1);
		}
		return solutions;
	}
}
