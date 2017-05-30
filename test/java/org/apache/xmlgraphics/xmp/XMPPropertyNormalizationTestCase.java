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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.apache.xmlgraphics.xmp.schemas.DublinCoreAdapter;
import org.apache.xmlgraphics.xmp.schemas.DublinCoreSchema;
import org.junit.Test;

/**
 * Test normalization of XMP properties (ex. automatic conversion of simple values to arrays).
 */
public class XMPPropertyNormalizationTestCase {

    @Test
    public void testNormalization1() {
        Metadata xmp = new Metadata();
        DublinCoreAdapter dc = DublinCoreSchema.getAdapter(xmp);
        dc.addCreator("Creator1");
        XMPProperty prop = xmp.getProperty(DublinCoreSchema.NAMESPACE, "creator");
        assertNotNull(prop);
        assertNotNull("Must be an array as it's a 'Seq ProperName'", prop.getArrayValue());

        xmp = new Metadata();
        xmp.setProperty(new XMPProperty(
                DublinCoreSchema.NAMESPACE, "creator", "Creator1"));
        prop = xmp.getProperty(DublinCoreSchema.NAMESPACE, "creator");
        assertNotNull(prop);
        assertEquals("Creator1", prop.getValue());
        assertNull(prop.getArrayValue()); //Simple value so far

        prop = new XMPProperty(DublinCoreSchema.NAMESPACE, "description", "Beschreibung");
        xmp.setProperty(prop);

        //Now normalizing (usually done as preparation for serializing)
        XMPSchemaRegistry.getInstance().normalize(xmp);

        prop = xmp.getProperty(DublinCoreSchema.NAMESPACE, "creator");
        assertTrue(prop.getValue() instanceof XMPArray);
        assertNotNull("Must be an array as it's a 'Seq ProperName'", prop.getArrayValue());
        assertEquals(XMPArrayType.SEQ, prop.getArrayValue().getType());
        assertEquals(1, prop.getArrayValue().getSize());
        assertEquals("Creator1", prop.getArrayValue().getValue(0));

        prop = xmp.getProperty(DublinCoreSchema.NAMESPACE, "description");
        assertTrue(prop.getValue() instanceof XMPArray);
        assertNotNull("Must be an array as it's a 'Lang Alt'", prop.getArrayValue());
        assertEquals(XMPArrayType.ALT, prop.getArrayValue().getType());
        assertEquals(1, prop.getArrayValue().getSize());
        assertTrue(prop.isQualifiedProperty());
        assertEquals("Beschreibung", prop.getArrayValue().getLangValue("x-default"));
    }

}
