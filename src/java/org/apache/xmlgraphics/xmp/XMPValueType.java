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
import org.apache.xmlgraphics.xmp.schemas.pdf.PDFAValueTypeXMPSchema;

/**
 * Represents an XMP value type following the data model of PDF/A-1.
 */
public class XMPValueType {

    private final String type;
    private final String namespace;
    private final String prefix;
    private final String description;

    //private final List<XMPField> fields = new java.util.ArrayList<XMPField>();

    public XMPValueType(String type, String namespace, String prefix, String description) {
        this.type = type;
        this.namespace = namespace;
        this.prefix = prefix;
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public String getNamespace() {
        return namespace;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getDescription() {
        return description;
    }

    private QName toQName(String name) {
        return new QName(PDFAValueTypeXMPSchema.NAMESPACE, PDFAValueTypeXMPSchema.PREFIX, name);
    }

    public XMPStructure toXMPStructure() {
        XMPStructure struct = new XMPStructure();
        struct.setProperty(new XMPProperty(toQName("type"), type));
        struct.setProperty(new XMPProperty(toQName("namespace"), namespace));
        struct.setProperty(new XMPProperty(toQName("prefix"), prefix));
        struct.setProperty(new XMPProperty(toQName("description"), description));
        //struct.setProperty(new XMPProperty(toQName("field"), ...));
        return struct;
    }
}
