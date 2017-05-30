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

import java.util.Map;

import org.apache.xmlgraphics.util.QName;
import org.apache.xmlgraphics.xmp.merge.MergeRuleSet;

/**
 * Base class for schema implementations that provide user-friendly access to XMP values.
 */
public class XMPSchema {

    private static MergeRuleSet defaultMergeRuleSet = new MergeRuleSet();

    private final String namespace;
    private final String prefix;
    private final String description;

    protected final Map<String, XMPValueType> valueTypes
            = new java.util.HashMap<String, XMPValueType>();
    protected final Map<QName, XMPPropertyType> propDefs
            = new java.util.HashMap<QName, XMPPropertyType>();

    /**
     * Constructs a new XMP schema object.
     * @param namespace the namespace URI for the schema
     * @param preferredPrefix the preferred prefix for the schema
     */
    public XMPSchema(String namespace, String preferredPrefix) {
        this(namespace, preferredPrefix, null);
    }

    /**
     * Constructs a new XMP schema object.
     * @param namespace the namespace URI for the schema
     * @param preferredPrefix the preferred prefix for the schema
     * @param description an optional description of the schema
     */
    public XMPSchema(String namespace, String preferredPrefix, String description) {
        this.namespace = namespace;
        this.prefix = preferredPrefix;
        this.description = description;
    }

    /** @return the namespace URI of the schema */
    public String getNamespace() {
        return this.namespace;
    }

    /** @return the preferred prefix of the schema */
    public String getPreferredPrefix() {
        return this.prefix;
    }

    /** @return the description of the schema (or null if not set) */
    public String getDescription() {
        return this.description;
    }

    /**
     * Returns the QName for a property of this schema.
     * @param propName the property name
     * @return the QName for the property
     */
    protected QName getQName(String propName) {
        return new QName(getNamespace(), propName);
    }

    /** @return the default merge rule set for this XMP schema. */
    public MergeRuleSet getDefaultMergeRuleSet() {
        return defaultMergeRuleSet;
    }

    protected void addType(XMPPropertyType type) {
        propDefs.put(type.getName(), type);
    }

    /**
     * Normalizes the property structure to their defined types. For example, a
     * property of type "Seq ProperName" might be represented as a simple value instead
     * of an array, but newer XMP standard versions require that the type must be
     * adhered to.
     * @param metadata the metadata to normalize
     */
    public void normalize(Metadata metadata) {
        //Do nothing. Subclasses should do the normalization
        for (XMPPropertyType type : propDefs.values()) {
            type.normalize(metadata);
        }
    }

}
