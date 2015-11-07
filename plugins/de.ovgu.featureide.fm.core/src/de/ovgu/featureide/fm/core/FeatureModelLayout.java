/* FeatureIDE - A Framework for Feature-Oriented Software Development
 * Copyright (C) 2005-2015  FeatureIDE team, University of Magdeburg, Germany
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
package de.ovgu.featureide.fm.core;

import de.ovgu.featureide.fm.core.base.IFeatureModelLayout;

/**
 * Encapsulates layout functionality for the feature model.
 * 
 * @author soenke
 */
public class FeatureModelLayout implements IFeatureModelLayout, Cloneable {
	private boolean autoLayoutLegend;
	private boolean showHiddenFeatures;
	private boolean hasVerticalLayout;
	private FMPoint legendPos;

	private int selectedLayoutAlgorithm;
	
	public FeatureModelLayout() {
		this.autoLayoutLegend = true;
		this.showHiddenFeatures = true;
		this.hasVerticalLayout = true;
		this.legendPos = new FMPoint(0, 0);
		this.selectedLayoutAlgorithm = 1;
	}
	
	protected FeatureModelLayout(FeatureModelLayout featureModelLayout) {
		this.autoLayoutLegend = featureModelLayout.autoLayoutLegend;
		this.showHiddenFeatures = featureModelLayout.showHiddenFeatures;
		this.hasVerticalLayout = featureModelLayout.hasVerticalLayout;
		this.legendPos = new FMPoint(featureModelLayout.legendPos.getX(), featureModelLayout.legendPos.getY());
		this.selectedLayoutAlgorithm = featureModelLayout.selectedLayoutAlgorithm;
	}

	@Override
	public void setLegendAutoLayout(boolean b) {
		autoLayoutLegend = b;
	}

	@Override
	public boolean hasLegendAutoLayout() {
		return autoLayoutLegend;
	}

	@Override
	public boolean showHiddenFeatures() {
		return showHiddenFeatures;
	}

	@Override
	public void showHiddenFeatures(boolean b) {
		showHiddenFeatures = b;
	}

	@Override
	public boolean verticalLayout() {
		return hasVerticalLayout;
	}

	@Override
	public void verticalLayout(boolean b) {
		hasVerticalLayout = b;
	}

	@Override
	public FMPoint getLegendPos() {
		return legendPos;
	}

	@Override
	public void setLegendPos(int x, int y) {
		this.legendPos = new FMPoint(x, y);
	}

	@Override
	public void setLayout(int newLayoutAlgorithm) {
		selectedLayoutAlgorithm = newLayoutAlgorithm;
	}

	@Override
	public int getLayoutAlgorithm() {
		return selectedLayoutAlgorithm;
	}

	@Override
	public boolean hasFeaturesAutoLayout() {
		return (selectedLayoutAlgorithm != 0);
	}
	
	@Override
	public FeatureModelLayout clone() {
		return new FeatureModelLayout(this);
	}
}
