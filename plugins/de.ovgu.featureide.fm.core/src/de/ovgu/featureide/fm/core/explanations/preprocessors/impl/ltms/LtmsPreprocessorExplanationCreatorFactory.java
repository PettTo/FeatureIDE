/* FeatureIDE - A Framework for Feature-Oriented Software Development
 * Copyright (C) 2005-2019  FeatureIDE team, University of Magdeburg, Germany
 *
 * This file is part of FeatureIDE.
 *
 * FeatureIDE is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FeatureIDE is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with FeatureIDE.  If not, see <http://www.gnu.org/licenses/>.
 *
 * See http://featureide.cs.ovgu.de/ for further information.
 */
package de.ovgu.featureide.fm.core.explanations.preprocessors.impl.ltms;

import org.prop4j.explain.solvers.impl.ltms.Ltms;
import org.prop4j.explain.solvers.impl.ltms.LtmsSatSolverFactory;

import de.ovgu.featureide.fm.core.explanations.preprocessors.PreprocessorExplanationCreator;
import de.ovgu.featureide.fm.core.explanations.preprocessors.impl.mus.MusPreprocessorExplanationCreatorFactory;

/**
 * Provides instances of {@link PreprocessorExplanationCreator} using an {@link Ltms LTMS}.
 *
 * @author Timo G&uuml;nther
 */
public class LtmsPreprocessorExplanationCreatorFactory extends MusPreprocessorExplanationCreatorFactory {

	/**
	 * Constructs a new instance of this class.
	 */
	public LtmsPreprocessorExplanationCreatorFactory() {
		super(new LtmsSatSolverFactory());
	}
}
