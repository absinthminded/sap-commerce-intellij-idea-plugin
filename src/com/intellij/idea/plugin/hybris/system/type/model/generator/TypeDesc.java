/*
 * This file is part of "hybris integration" plugin for Intellij IDEA.
 * Copyright (C) 2014-2016 Alexander Bartash <AlexanderBartash@gmail.com>
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

/*
 * XSD/DTD Model generator tool
 *
 * By Gregory Shrago
 * 2002 - 2006
 */
package com.intellij.idea.plugin.hybris.system.type.model.generator;

import java.util.Map;
import java.util.TreeMap;

public class TypeDesc {

    public enum TypeEnum {
        CLASS, ENUM, GROUP_INTERFACE
    }

    public TypeDesc(String xsName, String xsNamespace, String name, TypeEnum type) {
        this.xsName = xsName;
        this.xsNamespace = xsNamespace;
        this.name = name;
        this.type = type;
    }

    TypeEnum type;
    final String xsName;
    final String xsNamespace;
    final String name;
    final Map<String, FieldDesc> fdMap = new TreeMap<String, FieldDesc>();
    boolean duplicates;
    String documentation;
    TypeDesc[] supers;

    public String toString() {
        return (type == TypeEnum.ENUM ? "enum" : "type") + ": " + name + ";" + xsName + ";";
    }
}
