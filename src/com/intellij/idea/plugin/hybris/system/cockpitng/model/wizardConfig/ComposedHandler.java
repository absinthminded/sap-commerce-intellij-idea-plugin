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

// Generated on Thu Jan 19 16:23:36 CET 2023
// DTD/Schema  :    http://www.hybris.com/cockpitng/config/wizard-config

package com.intellij.idea.plugin.hybris.system.cockpitng.model.wizardConfig;

import com.intellij.idea.plugin.hybris.common.HybrisConstants;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.Namespace;
import com.intellij.util.xml.Required;
import com.intellij.util.xml.SubTagList;
import org.jetbrains.annotations.NotNull;

/**
 * http://www.hybris.com/cockpitng/config/wizard-config:ComposedHandlerType interface.
 */
@Namespace(HybrisConstants.COCKPIT_NG_NAMESPACE_KEY)
public interface ComposedHandler extends DomElement {

	/**
	 * Returns the value of the handlerBean child.
	 * @return the value of the handlerBean child.
	 */
	@NotNull
	@com.intellij.util.xml.Attribute ("handlerBean")
	@Required
	GenericAttributeValue<String> getHandlerBean();


	/**
	 * Returns the value of the handlerId child.
	 * @return the value of the handlerId child.
	 */
	@NotNull
	@com.intellij.util.xml.Attribute ("handlerId")
	@Required
	GenericAttributeValue<String> getHandlerId();


	/**
	 * Returns the list of additionalParams children.
	 * @return the list of additionalParams children.
	 */
	@NotNull
	@SubTagList ("additionalParams")
	@Required
	java.util.List<AdditionalParam> getAdditionalParamses();
	/**
	 * Adds new child to the list of additionalParams children.
	 * @return created child
	 */
	@SubTagList ("additionalParams")
	AdditionalParam addAdditionalParams();


}
