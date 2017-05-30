/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/* $Id$ */

package org.apache.xmlgraphics.xmp;

import org.apache.xmlgraphics.util.QName;
import org.apache.xmlgraphics.xmp.schemas.pdf.PDFAPropertyTypeXMPSchema;

/**
 * Represents an XMP property type following the data model of PDF/A-1.
 */
public class XMPPropertyType {

    private static final String LANG_ALT = "Lang Alt";

    public enum Category { INTERNAL, EXTERNAL; }

    private final QName name;
    private final String valueType;
    private final Category category;
    private final String description;

    public XMPPropertyType(QName name, String valueType,
            Category category, String description) {
        this.name = name;
        this.valueType = valueType;
        this.category = category;
        this.description = description;
    }

    public QName getName() {
        return name;
    }

    public XMPArrayType getContainerType() {
        String s = valueType.toLowerCase();
        if (s.startsWith("bag ")) {
            return XMPArrayType.BAG;
        } else if (s.startsWith("seq ")) {
            return XMPArrayType.SEQ;
        } else if (s.startsWith("alt ")) {
            return XMPArrayType.ALT;
        } else if (isLangAlt()) {
            return XMPArrayType.ALT; //Special case
        }
        return XMPArrayType.NONE;
    }

    public boolean isLangAlt() {
        return LANG_ALT.equalsIgnoreCase(valueType);
    }

    public String getValueType() {
        return valueType;
    }

    public Category getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    private QName toQName(String name) {
        return new QName(PDFAPropertyTypeXMPSchema.NAMESPACE,
                PDFAPropertyTypeXMPSchema.PREFIX, name);
    }

    public XMPStructure toXMPStructure() {
        XMPStructure struct = new XMPStructure();
        struct.setProperty(new XMPProperty(toQName("name"), name));
        struct.setProperty(new XMPProperty(toQName("valueType"), valueType));
        struct.setProperty(new XMPProperty(toQName("category"), category.toString().toLowerCase()));
        struct.setProperty(new XMPProperty(toQName("description"), description));
        return struct;
    }

    public XMPProperty normalize(Metadata metadata) {
        XMPProperty prop;
        prop = metadata.getProperty(getName());
        if (prop != null) {
            if (getContainerType() != null) {
                XMPArray array = prop.convertSimpleValueToArray(getContainerType());
                if (isLangAlt()) {
                    array.normalizeLangAlt();
                }
            }
        }
        return prop;
    }


}
