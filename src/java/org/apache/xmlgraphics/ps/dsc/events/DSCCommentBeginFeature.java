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

/* $Id: DSCCommentIncludeFeature.java 3198 2013-07-23 12:41:47Z jeremias $ */

package org.apache.xmlgraphics.ps.dsc.events;

import org.apache.xmlgraphics.ps.DSCConstants;

/**
 * Represents the %%BeginFeature DSC comment.
 */
public class DSCCommentBeginFeature extends AbstractFeatureDSCComment {

    /**
     * Creates a new instance.
     */
    public DSCCommentBeginFeature() {
        super();
    }

    /**
     * Creates a new instance.
     * @param featureType the name of the feature
     * @param option the selected option
     */
    public DSCCommentBeginFeature(String featureType, String option) {
        super(featureType, option);
    }

    /**
     * Creates a new %%BeginFeature from another feature comment.
     * @param feature the template
     */
    public DSCCommentBeginFeature(AbstractFeatureDSCComment feature) {
        this(feature.getFeatureType(), feature.getOption());
    }

    /** {@inheritDoc} */
    public String getName() {
        return DSCConstants.BEGIN_FEATURE;
    }

}
