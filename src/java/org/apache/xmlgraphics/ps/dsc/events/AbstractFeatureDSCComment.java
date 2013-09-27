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

import java.io.IOException;

import org.apache.xmlgraphics.ps.PSGenerator;

/**
 * Represents the %%IncludeFeature DSC comment.
 */
public abstract class AbstractFeatureDSCComment extends AbstractDSCComment {

    private String featureType;
    private String option;

    /**
     * Creates a new instance.
     */
    public AbstractFeatureDSCComment() {
    }

    /**
     * Creates a new instance.
     * @param featureType the name of the feature
     * @param option the selected option
     */
    public AbstractFeatureDSCComment(String featureType, String option) {
        this.featureType = featureType;
        this.option = option;
    }

    /**
     * Returns the feature type.
     * @return the feature type
     */
    public String getFeatureType() {
        return featureType;
    }

    /**
     * Sets the feature type.
     * @param featureType the new feature type
     */
    public void setFeatureType(String featureType) {
        this.featureType = featureType;
    }

    /**
     * Returns the option.
     * @return the option
     */
    public String getOption() {
        return option;
    }

    /**
     * Sets the option.
     * @param option the new option
     */
    public void setOption(String option) {
        this.option = option;
    }

    /** {@inheritDoc} */
    public boolean hasValues() {
        return true;
    }

    /** {@inheritDoc} */
    public void parseValue(String value) {
        if (value != null && value.trim().length() > 0) {
            String[] split = value.split(" ");
            if (split.length > 0) {
                setFeatureType(split[0]);
            }
            if (split.length > 1) {
                setOption(split[1]);
            }
        }
    }

    /** {@inheritDoc} */
    public void generate(PSGenerator gen) throws IOException {
        if (getFeatureType() == null) {
            throw new IllegalStateException("No feature type was set!");
        }
        if (getOption() != null) {
            gen.writeDSCComment(getName(), new String[] {getFeatureType(), getOption()});
        } else {
            gen.writeDSCComment(getName(), getFeatureType());
        }
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return "%%" + getName() + ": " + getFeatureType() + (getOption() != null ? " " + getOption() : "");
    }

}
