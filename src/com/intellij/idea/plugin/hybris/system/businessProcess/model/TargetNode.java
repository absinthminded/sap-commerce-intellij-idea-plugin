/*
 * This file is part of "SAP Commerce Developers Toolset" plugin for Intellij IDEA.
 * Copyright (C) 2019 EPAM Systems <hybrisideaplugin@epam.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

// Generated on Tue Jan 10 21:54:19 CET 2023
// DTD/Schema  :    http://www.hybris.de/xsd/processdefinition

package com.intellij.idea.plugin.hybris.system.businessProcess.model;

import com.intellij.idea.plugin.hybris.system.businessProcess.util.xml.BpNavigableElementConverter;
import com.intellij.util.xml.*;
import com.intellij.util.xml.DomElement;
import org.jetbrains.annotations.NotNull;

/**
 * http://www.hybris.de/xsd/processdefinition:targetNode interface.
 * <pre>
 * <h3>Type http://www.hybris.de/xsd/processdefinition:targetNode documentation</h3>
 * Define target node for split.
 * </pre>
 */
public interface TargetNode extends DomElement {

	/**
	 * Returns the value of the name child.
	 * @return the value of the name child.
	 */
	@NotNull
	@Attribute ("name")
	@Required
	@Convert(BpNavigableElementConverter.class)
	GenericAttributeValue<String> getName();


}
