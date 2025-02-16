/*
 * This file is part of "SAP Commerce Developers Toolset" plugin for Intellij IDEA.
 * Copyright (C) 2023 EPAM Systems <hybrisideaplugin@epam.com>
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

// Generated on Wed Jan 18 00:35:36 CET 2023
// DTD/Schema  :    http://www.hybris.com/cockpitng/config/explorertree

package com.intellij.idea.plugin.hybris.system.cockpitng.model.widgets;

import com.intellij.idea.plugin.hybris.common.HybrisConstants;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.Namespace;
import com.intellij.util.xml.Required;
import com.intellij.util.xml.SubTagList;
import org.jetbrains.annotations.NotNull;

/**
 * http://www.hybris.com/cockpitng/config/explorertree:explorer-node interface.
 */
@Namespace(HybrisConstants.COCKPIT_NG_NAMESPACE_KEY)
public interface ExplorerNode extends DomElement, Positioned {

    String ID = "id";

    /**
     * Returns the value of the id child.
     *
     * @return the value of the id child.
     */
    @NotNull
    @com.intellij.util.xml.Attribute(ID)
    @Required
    GenericAttributeValue<String> getId();


    /**
     * Returns the value of the expanded-by-default child.
     *
     * @return the value of the expanded-by-default child.
     */
    @NotNull
    @com.intellij.util.xml.Attribute("expanded-by-default")
    GenericAttributeValue<Boolean> getExpandedByDefault();


    /**
     * Returns the value of the merge-mode child.
     *
     * @return the value of the merge-mode child.
     */
    @NotNull
    @com.intellij.util.xml.Attribute("merge-mode")
    GenericAttributeValue<String> getMergeMode();


    /**
     * Returns the list of navigation-node children.
     *
     * @return the list of navigation-node children.
     */
    @NotNull
    @SubTagList("navigation-node")
    java.util.List<NavigationNode> getNavigationNodes();

    /**
     * Adds new child to the list of navigation-node children.
     *
     * @return created child
     */
    @SubTagList("navigation-node")
    NavigationNode addNavigationNode();


    /**
     * Returns the list of type-node children.
     *
     * @return the list of type-node children.
     */
    @NotNull
    @SubTagList("type-node")
    java.util.List<TypeNode> getTypeNodes();

    /**
     * Adds new child to the list of type-node children.
     *
     * @return created child
     */
    @SubTagList("type-node")
    TypeNode addTypeNode();


    /**
     * Returns the list of dynamic-node children.
     *
     * @return the list of dynamic-node children.
     */
    @NotNull
    @SubTagList("dynamic-node")
    java.util.List<DynamicNode> getDynamicNodes();

    /**
     * Adds new child to the list of dynamic-node children.
     *
     * @return created child
     */
    @SubTagList("dynamic-node")
    DynamicNode addDynamicNode();


}
