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
import org.apache.xmlgraphics.xmp.merge.MergeRuleSet;
import org.apache.xmlgraphics.xmp.merge.NoReplacePropertyMerger;

/**
 * XMP schema for XMP Basic.
 */
public class XMPBasicSchema extends XMPSchema {

    /** Namespace URI for XMP Basic */
    public static final String NAMESPACE = XMPConstants.XMP_BASIC_NAMESPACE;

    /** Preferred prefix for XMP Basic */
    public static final String PREFERRED_PREFIX = "xmp";

    /** Namespace URI for the qualifier for xmp:Identifier */
    public static final String QUALIFIER_NAMESPACE = "http://ns.adobe.com/xmp/Identifier/qual/1.0/";

    /** The qualified name for the qualifier for xmp:Identifier */
    public static final QName SCHEME_QUALIFIER = new QName(QUALIFIER_NAMESPACE, "xmpidq:Scheme");

    private static MergeRuleSet mergeRuleSet = new MergeRuleSet();

    /** Creates a new schema instance for Dublin Core. */
    public XMPBasicSchema() {
        super(NAMESPACE, PREFERRED_PREFIX);

        addType(new XMPPropertyType(new QName(NAMESPACE, XMPBasicAdapter.ADVISORY),
                "bag XPath", Category.EXTERNAL,
                "An unordered array specifying properties that were edited outside the"
                    + " authoring application. Each item should contain a single"
                    + " namespace and XPath separated by one ASCII space (U+0020)."));
        addType(new XMPPropertyType(new QName(NAMESPACE, XMPBasicAdapter.BASE_URL),
                "URL", Category.INTERNAL,
                "The base URL for relative URLs in the document content. If this document"
                    + " contains Internet links, and those links are relative, they are relative"
                    + " to this base URL. This property provides a standard way"
                    + " for embedded relative URLs to be interpreted by tools. Web authoring"
                    + " tools should set the value based on their notion of where URLs will be"
                    + " interpreted."));
        addType(new XMPPropertyType(new QName(NAMESPACE, XMPBasicAdapter.CREATE_DATE),
                "Date", Category.INTERNAL,
                "The date and time the resource was originally created."));
        addType(new XMPPropertyType(new QName(NAMESPACE, XMPBasicAdapter.CREATOR_TOOL),
                "AgentName", Category.INTERNAL,
                "The name of the first known tool used to create the resource. If history is"
                    + " present in the metadata, this value should be equivalent to that of"
                    + " xmpMM:History's softwareAgent property."));
        addType(new XMPPropertyType(new QName(NAMESPACE, XMPBasicAdapter.IDENTIFIER),
                "bag Text", Category.EXTERNAL,
                "An unordered array of text strings that unambiguously identify the resource"
                    + " within a given context. An array item may be qualified with xmpidq:Scheme"
                    + " to denote the formal identification system to which that identifier"
                    + " conforms."));
        addType(new XMPPropertyType(new QName(NAMESPACE, XMPBasicAdapter.LABEL),
                "Text", Category.EXTERNAL,
                "A word or short phrase that identifies a document as a member of a"
                    + "user-defined collection. Used to organize documents in a file browser."));
        addType(new XMPPropertyType(new QName(NAMESPACE, XMPBasicAdapter.METADATA_DATE),
                "Date", Category.INTERNAL,
                "The date and time that any metadata for this resource was last changed. It should"
                    + " be the same as or more recent than xmp:ModifyDate."));
        addType(new XMPPropertyType(new QName(NAMESPACE, XMPBasicAdapter.MODIFY_DATE),
                "Date", Category.INTERNAL,
                "The date and time the resource was last modified."
                    + " The value of this property is not necessarily the same as the file’s system"
                    + " modification date because it is set before the file is saved."));
        addType(new XMPPropertyType(new QName(NAMESPACE, XMPBasicAdapter.NICKNAME),
                "Text", Category.EXTERNAL,
                "A short informal name for the resource."));
        addType(new XMPPropertyType(new QName(NAMESPACE, XMPBasicAdapter.RATING),
                "Integer", Category.EXTERNAL,
                "A number that indicates a document's status relative to other documents, used"
                    + " to organize documents in a file browser. Values are user-defined within an"
                    + " application-defined range."));
        addType(new XMPPropertyType(new QName(NAMESPACE, XMPBasicAdapter.THUMBNAILS),
                "alt Thumbnail", Category.INTERNAL,
                ""));
    }

    static {
        //CreateDate shall be preserved if it exists
        mergeRuleSet.addRule(new QName(NAMESPACE, "CreateDate"), new NoReplacePropertyMerger());
    }

    /**
     * Creates and returns an adapter for this schema around the given metadata object.
     * @param meta the metadata object
     * @return the newly instantiated adapter
     */
    public static XMPBasicAdapter getAdapter(Metadata meta) {
        return new XMPBasicAdapter(meta, NAMESPACE);
    }

    /** @see org.apache.xmlgraphics.xmp.XMPSchema#getDefaultMergeRuleSet() */
    @Override
    public MergeRuleSet getDefaultMergeRuleSet() {
        return mergeRuleSet;
    }

    /** {@inheritDoc} */
    @Override
    public void normalize(Metadata metadata) {
        super.normalize(metadata);
        getAdapter(metadata).normalize();
    }

}
