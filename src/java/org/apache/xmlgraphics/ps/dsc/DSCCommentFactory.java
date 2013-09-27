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

/* $Id: DSCCommentFactory.java 1345683 2012-06-03 14:50:33Z gadams $ */

package org.apache.xmlgraphics.ps.dsc;

import java.util.Map;

import org.apache.xmlgraphics.ps.DSCConstants;
import org.apache.xmlgraphics.ps.dsc.events.DSCComment;
import org.apache.xmlgraphics.ps.dsc.events.DSCCommentBeginDocument;
import org.apache.xmlgraphics.ps.dsc.events.DSCCommentBeginFeature;
import org.apache.xmlgraphics.ps.dsc.events.DSCCommentBeginResource;
import org.apache.xmlgraphics.ps.dsc.events.DSCCommentBoundingBox;
import org.apache.xmlgraphics.ps.dsc.events.DSCCommentDocumentNeededResources;
import org.apache.xmlgraphics.ps.dsc.events.DSCCommentDocumentSuppliedResources;
import org.apache.xmlgraphics.ps.dsc.events.DSCCommentEndComments;
import org.apache.xmlgraphics.ps.dsc.events.DSCCommentEndOfFile;
import org.apache.xmlgraphics.ps.dsc.events.DSCCommentHiResBoundingBox;
import org.apache.xmlgraphics.ps.dsc.events.DSCCommentIncludeFeature;
import org.apache.xmlgraphics.ps.dsc.events.DSCCommentIncludeResource;
import org.apache.xmlgraphics.ps.dsc.events.DSCCommentLanguageLevel;
import org.apache.xmlgraphics.ps.dsc.events.DSCCommentPage;
import org.apache.xmlgraphics.ps.dsc.events.DSCCommentPageBoundingBox;
import org.apache.xmlgraphics.ps.dsc.events.DSCCommentPageHiResBoundingBox;
import org.apache.xmlgraphics.ps.dsc.events.DSCCommentPageResources;
import org.apache.xmlgraphics.ps.dsc.events.DSCCommentPages;
import org.apache.xmlgraphics.ps.dsc.events.DSCCommentTitle;

/**
 * Factory for DSCComment subclasses.
 */
public final class DSCCommentFactory {

    private DSCCommentFactory() {
    }

    private static final Map<String, Class<? extends DSCComment>> DSC_FACTORIES = new java.util.HashMap<String, Class<? extends DSCComment>>();

    static {
        register(DSCConstants.END_COMMENTS,
                DSCCommentEndComments.class);
        register(DSCConstants.BEGIN_RESOURCE,
                DSCCommentBeginResource.class);
        register(DSCConstants.INCLUDE_RESOURCE,
                DSCCommentIncludeResource.class);
        register(DSCConstants.PAGE_RESOURCES,
                DSCCommentPageResources.class);
        register(DSCConstants.BEGIN_DOCUMENT,
                DSCCommentBeginDocument.class);
        register(DSCConstants.PAGE,
                DSCCommentPage.class);
        register(DSCConstants.PAGES,
                DSCCommentPages.class);
        register(DSCConstants.BBOX,
                DSCCommentBoundingBox.class);
        register(DSCConstants.HIRES_BBOX,
                DSCCommentHiResBoundingBox.class);
        register(DSCConstants.PAGE_BBOX,
                DSCCommentPageBoundingBox.class);
        register(DSCConstants.PAGE_HIRES_BBOX,
                DSCCommentPageHiResBoundingBox.class);
        register(DSCConstants.LANGUAGE_LEVEL,
                DSCCommentLanguageLevel.class);
        register(DSCConstants.DOCUMENT_NEEDED_RESOURCES,
                DSCCommentDocumentNeededResources.class);
        register(DSCConstants.DOCUMENT_SUPPLIED_RESOURCES,
                DSCCommentDocumentSuppliedResources.class);
        register(DSCConstants.TITLE,
                DSCCommentTitle.class);
        register(DSCConstants.EOF,
                DSCCommentEndOfFile.class);
        register(DSCConstants.BEGIN_FEATURE,
                DSCCommentBeginFeature.class);
        register(DSCConstants.INCLUDE_FEATURE,
                DSCCommentIncludeFeature.class);
        //TODO Add additional implementations as needed
    }

    /**
     * Registers a DSC comment for specialized parsing.
     * @param name the name of the DSC comment (without the "%%" prefix)
     * @param clazz the specialized class to instantiate for the comment
     */
    public static void register(String name, Class<? extends DSCComment> clazz) {
        DSC_FACTORIES.put(name, clazz);
    }

    /**
     * Creates and returns new instances for DSC comments with a given name.
     * @param name the name of the DSCComment (without the "%%" prefix)
     * @return the new instance or null if no particular subclass registered for the given
     *          DSC comment.
     */
    public static DSCComment createDSCCommentFor(String name) {
        Class<? extends DSCComment> clazz = DSC_FACTORIES.get(name);
        if (clazz == null) {
            return null;
        }
        try {
            return clazz.newInstance();
        } catch (InstantiationException e) {
            throw new RuntimeException("Error instantiating instance for '" + name + "': "
                    + e.getMessage());
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Illegal Access error while instantiating instance for '"
                    + name + "': " + e.getMessage());
        }
    }

}
