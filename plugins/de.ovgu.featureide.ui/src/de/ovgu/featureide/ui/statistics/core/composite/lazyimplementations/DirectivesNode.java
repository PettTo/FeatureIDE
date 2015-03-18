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
package de.ovgu.featureide.ui.statistics.core.composite.lazyimplementations;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;

import de.ovgu.featureide.core.fstmodel.FSTClass;
import de.ovgu.featureide.core.fstmodel.FSTModel;
import de.ovgu.featureide.ui.UIPlugin;
import de.ovgu.featureide.ui.statistics.core.composite.LazyParent;
import de.ovgu.featureide.ui.statistics.core.composite.Parent;
import de.ovgu.featureide.ui.statistics.core.composite.lazyimplementations.genericdatatypes.AbstractSortModeNode;
import de.ovgu.featureide.ui.statistics.core.composite.lazyimplementations.genericdatatypes.HashMapNode;

/**
 * TreeNode who stores the number of used preprocessor directives, directives
 * per class and features per directives.<br>
 * This node should only be used for a preprocessor project.
 * 
 * @author Dominik Hamann
 * @author Patrick Haese
 */
public class DirectivesNode extends LazyParent {
	private FSTModel fstModel;
	int numberOfLines;
	/**
	 * Mapping of lines of code to each feature.
	 */
	HashMap<String, Integer> featuresAndLines = new HashMap<String, Integer>();

	/**
	 * Constructor for a {@code DirectivesNode}.
	 * 
	 * @param description
	 *            description of the node shown in the view
	 * @param fstModel
	 *            FSTModel for the calculation
	 */
	public DirectivesNode(String description, FSTModel fstModel) {
		super(description);
		this.fstModel = fstModel;
	}

	public DirectivesNode() {

	}

	@Override
	protected void initChildren() {
		final Parent internClasses = new Parent("Classes");
		Parent project = new Parent("Project statistics");
		Integer maxNesting = 0;
		String maxNestingClass = null;

		project.addChild(new LazyParent("Number of directives") {
			@Override
			protected void initChildren() {
				new Aggregator().processAll(fstModel, this);
			}
		});

		final Aggregator aggProject = new Aggregator();
		for (FSTClass clazz : fstModel.getClasses()) {
			String className = clazz.getName();
			final int pIndex = className.lastIndexOf('/');
			className = ((pIndex > 0) ? className.substring(0, pIndex + 1).replace('/', '.') : "(default package).") + className.substring(pIndex + 1);

			final Parent classNode = new Parent(className);
			aggProject.process(clazz.getRoles(), classNode);
			internClasses.addChild(classNode);

			if (!clazz.getRoles().isEmpty()) {
				final Integer currentNesting = aggProject.getMaxNesting();
				classNode.addChild(new Parent("Maximum nesting of directives", currentNesting));
				if (currentNesting > maxNesting) {
					maxNesting = currentNesting;
					maxNestingClass = className;
				}
				aggProject.setMaxNesting(0);
			}
		}

		final Integer maximumSum = aggProject.getMaximumSum();
		final Integer minimumSum = aggProject.getMinimumSum();

		final Parent directivesPerClass = new Parent("Directives per class");
		directivesPerClass.addChild(new Parent("Maximum number of directives: " + maximumSum + " in class "
				+ searchClass(internClasses.getChildren(), maximumSum)));
		directivesPerClass.addChild(new Parent("Minimum number of directives: " + minimumSum + " in class "
				+ searchClass(internClasses.getChildren(), minimumSum)));
		directivesPerClass.addChild(new Parent("Average number of directives per class", getAverage(internClasses)));
		project.addChild(directivesPerClass);

		project.addChild(new LazyParent("Features per directive") {

			@Override
			protected void initChildren() {

				Aggregator aggregator = new Aggregator();

				aggregator.initializeDirectiveCount(fstModel);

				List<Integer> list = aggregator.getListOfNestings();
				double average = 0.0;
				for (Integer i : list) {
					average += i;
				}
				if (list.size() != 0) {
					average /= list.size();
					average *= 10;
					long rounded = Math.round(average);
					average = ((double) rounded) / 10;
				} else {
					average = 0.0;
				}

				addChild(new Parent("Maximum features per directive", aggregator.getMaxNesting()));
				addChild(new Parent("Minimum features per directive", aggregator.getMinNesting()));
				addChild(new Parent("Average features per directive", average));
			}
		});
		project.addChild(new Parent("Maximum nesting of directives: " + maxNesting + " in class " + maxNestingClass));

		addChild(project);

		Parent classes = new AbstractSortModeNode("Class statistics") {
			@Override
			protected void initChildren() {
				for (Parent child : internClasses.getChildren()) {
					addChild(child);
				}
			}
		};

		addChild(classes);
		addChild(new HashMapNode(NUMBER_OF_CODELINES + SEPARATOR + getLOC(), null, featuresAndLines));
	}

	private String searchClass(Parent[] data, Integer input) {
		for (Parent p : data) {
			if (p.getValue().equals(input)) {
				String className = p.getDescription();
				return className;
			}
		}
		return null;
	}

	private Double getAverage(Parent parent) {
		if (parent.hasChildren()) {
			Integer numberOfDirectives = 0;
			for (Parent child : parent.getChildren()) {
				numberOfDirectives += (Integer) child.getValue();
			}

			Integer numberOfChildren = parent.getChildren().length;

			double average = numberOfDirectives;

			average /= (double) numberOfChildren;
			average *= 10;
			long rounded = Math.round(average);
			average = ((double) rounded) / 10;

			return average;
		}

		return 0.0;
	}

	public int getLOC() {
		final LinkedHashSet<String> extList = fstModel.getFeatureProject().getComposer().extensions();

		try {
			fstModel.getFeatureProject().getSourceFolder().accept(new IResourceVisitor() {

				@Override
				public boolean visit(IResource resource) throws CoreException {
					if (resource instanceof IFolder) {
						return true;
					} else if (resource instanceof IFile) {
						final IFile file = (IFile) resource;
						String currFeat = "";
						if (extList.contains(file.getFileExtension())) {

							try (FileReader fr = new FileReader(file.getLocation().toString())) {
								BufferedReader br = new BufferedReader(fr);
								checkContent(currFeat, br);
								br.close();
							} catch (FileNotFoundException e) {
								e.printStackTrace();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}

					}
					return false;
				}

			});
		} catch (CoreException e) {
			UIPlugin.getDefault().logError(e);
		}

		return numberOfLines;
	}

	/**
	 * @param currFeat
	 * @param isFeat
	 * @param isComment
	 * @param br
	 * @throws IOException
	 */
	public void checkContent(String currFeat, BufferedReader br) throws IOException {
		boolean isFeat = false;
		boolean isComment = false;
		String s;
		while ((s = br.readLine()) != null) {
			s = s.trim().replaceAll("	", "").replaceAll(" ", "");
			if (!isComment) {
				if (s.startsWith("/*")) {
					isComment = true;
				} else if (s.startsWith("//#if")) {
					currFeat = s.split("//#if")[1];
					featuresAndLines.put(currFeat, 0);
					isFeat = true;
				} else if (s.startsWith("//#endif")) {
					isFeat = false;
				} else if (isFeat && !isComment && !s.equals("")) {
					if (s.startsWith("//") && !s.startsWith("//@")) {

					} else {
						featuresAndLines.put(currFeat, featuresAndLines.get(currFeat) + 1);
						numberOfLines++;
					}
				} else if (!isFeat && !s.equals("")) {
					if (!s.startsWith("//")) {
						numberOfLines++;
					}
				}
			} else {
				if (s.endsWith("*/")) {
					isComment = false;
				} else if (s.contains("*/") && !s.endsWith("*/")) {
					if (isFeat) {
						featuresAndLines.put(currFeat, featuresAndLines.get(currFeat) + 1);
						numberOfLines++;
					} else {
						numberOfLines++;
					}
					isComment = false;
				}
			}

		}
	}
}