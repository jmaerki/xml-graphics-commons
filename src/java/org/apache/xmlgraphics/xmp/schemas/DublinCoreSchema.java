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

package org.apache.xmlgraphics.xmp.schemas;

import org.apache.xmlgraphics.util.QName;
import org.apache.xmlgraphics.xmp.Metadata;
import org.apache.xmlgraphics.xmp.XMPConstants;
import org.apache.xmlgraphics.xmp.XMPPropertyType;
import org.apache.xmlgraphics.xmp.XMPPropertyType.Category;
import org.apache.xmlgraphics.xmp.XMPSchema;
import org.apache.xmlgraphics.xmp.merge.ArrayAddPropertyMerger;
import org.apache.xmlgraphics.xmp.merge.MergeRuleSet;

/**
 * Schema class for Dublin Core.
 */
public class DublinCoreSchema extends XMPSchema {

    /** Namespace URI for Dublin Core */
    public static final String NAMESPACE = XMPConstants.DUBLIN_CORE_NAMESPACE;

    private static MergeRuleSet dcMergeRuleSet;

    static {
        dcMergeRuleSet = new MergeRuleSet();
        //Dates are added up not replaced
        dcMergeRuleSet.addRule(new QName(NAMESPACE, "date"), new ArrayAddPropertyMerger());
    }

    /** Creates a new schema instance for Dublin Core. */
    public DublinCoreSchema() {
        super(NAMESPACE, "dc");
        addType(new XMPPropertyType(new QName(NAMESPACE, DublinCoreAdapter.CONTRIBUTOR),
                "bag ProperName", Category.EXTERNAL,
                "Contributors to the resource (other than the authors)."));
        addType(new XMPPropertyType(new QName(NAMESPACE, DublinCoreAdapter.COVERAGE),
                "Text", Category.EXTERNAL,
                "The extent or scope of the resource."));
        addType(new XMPPropertyType(new QName(NAMESPACE, DublinCoreAdapter.CREATOR),
                "seq ProperName", Category.EXTERNAL,
                "The authors of the resource (listed in order of precedence, if significant)."));
        addType(new XMPPropertyType(new QName(NAMESPACE, DublinCoreAdapter.DATE),
                "seq Date", Category.EXTERNAL,
                "Date(s) that something interesting happened to the resource."));
        addType(new XMPPropertyType(new QName(NAMESPACE, DublinCoreAdapter.DESCRIPTION),
                "Lang Alt", Category.EXTERNAL,
                "A textual description of the content of the resource."
                        + " Multiple values may be present for different languages."));
        addType(new XMPPropertyType(new QName(NAMESPACE, DublinCoreAdapter.FORMAT),
                "MIMEType", Category.INTERNAL,
                "The file format used when saving the resource. Tools"
                    + " and applications should set this property to the save"
                    + " format of the data. It may include appropriate"
                    + " qualifiers."));
        addType(new XMPPropertyType(new QName(NAMESPACE, DublinCoreAdapter.IDENTIFIER),
                "Text", Category.EXTERNAL,
                "Unique identifier of the resource."));
        addType(new XMPPropertyType(new QName(NAMESPACE, DublinCoreAdapter.LANGUAGE),
                "bag Locale", Category.INTERNAL,
                "An unordered array specifying the languages used in the resource."));
        addType(new XMPPropertyType(new QName(NAMESPACE, DublinCoreAdapter.PUBLISHER),
                "bag ProperName", Category.EXTERNAL,
                "Publishers."));
        addType(new XMPPropertyType(new QName(NAMESPACE, DublinCoreAdapter.RELATION),
                "bag Text", Category.EXTERNAL,
                "Relationships to other documents."));
        addType(new XMPPropertyType(new QName(NAMESPACE, DublinCoreAdapter.RIGHTS),
                "Lang Alt", Category.EXTERNAL,
                "Informal rights statement, selected by language."));
        addType(new XMPPropertyType(new QName(NAMESPACE, DublinCoreAdapter.SOURCE),
                "Text", Category.EXTERNAL,
                "Unique identifier of the work from which this resource was derived."));
        addType(new XMPPropertyType(new QName(NAMESPACE, DublinCoreAdapter.SUBJECT),
                "bag Text", Category.EXTERNAL,
                "An unordered array of descriptive phrases or keywords that specify the"
                    + " topic of the content of the resource."));
        addType(new XMPPropertyType(new QName(NAMESPACE, DublinCoreAdapter.TITLE),
                "Lang Alt", Category.EXTERNAL,
                "The title of the document, or the name given to the resource."
                    + " Typically, it will be a name by which the resource is formally known."));
        addType(new XMPPropertyType(new QName(NAMESPACE, DublinCoreAdapter.TYPE),
                "bag open Choice", Category.EXTERNAL,
                "A document type; for example, novel, poem, or working paper."));
    }

    /**
     * Creates and returns an adapter for this schema around the given metadata object.
     * @param meta the metadata object
     * @return the newly instantiated adapter
     */
    public static DublinCoreAdapter getAdapter(Metadata meta) {
        return new DublinCoreAdapter(meta);
    }

    /** @see org.apache.xmlgraphics.xmp.XMPSchema#getDefaultMergeRuleSet() */
    @Override
    public MergeRuleSet getDefaultMergeRuleSet() {
        return dcMergeRuleSet;
    }

    /** {@inheritDoc} */
    @Override
    public void normalize(Metadata metadata) {
        super.normalize(metadata);
        getAdapter(metadata).normalize();
    }

}
