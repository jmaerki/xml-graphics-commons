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

package org.apache.xmlgraphics.ps.dsc;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.xmlgraphics.ps.PSGenerator;
import org.apache.xmlgraphics.ps.dsc.events.DSCEvent;
import org.junit.Test;

/**
 * Tests the listener functionality on the DSC parser.
 */
public class NestedDocumentTestCase {

    /**
     * Tests {@link DefaultNestedDocumentHandler}.
     * @throws Exception if an error occurs
     */
    @Test
    public void testNestedDocumentHandler() throws Exception {
        URL url = getClass().getResource("test-nested1.txt");
        List<String> resultLines;
        InputStream in = url.openStream();
        try {
            ByteArrayOutputStream baout = new ByteArrayOutputStream();
            PSGenerator gen = new PSGenerator(baout);

            DSCParser parser = new DSCParser(in);
            parser.addListener(new DefaultNestedDocumentHandler(gen));
            parseAndPassThrough(parser, gen);

            String ps = baout.toString("ISO-8859-1");
            //System.out.println(ps);
            resultLines = Arrays.asList(ps.split("\\r?\\n"));
        } finally {
            IOUtils.closeQuietly(in);
        }
        List<String> sourceLines;
        in = url.openStream();
        try {
            sourceLines = IOUtils.readLines(in, "ISO-8859-1");
        } finally {
            IOUtils.closeQuietly(in);
        }
        assertEquals(sourceLines, resultLines);
    }

    private void parseAndPassThrough(DSCParser parser, PSGenerator gen)
            throws IOException, DSCException {
        //Just run the whole file through the parser and let the listeners do their job
        while (parser.hasNext()) {
            DSCEvent event = parser.nextEvent();
            event.generate(gen);
        }
        gen.flush();
    }

}
