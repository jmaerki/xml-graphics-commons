package org.apache.xmlgraphics.image.loader.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.ByteArrayInputStream;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;

import org.apache.xmlgraphics.image.loader.ImageContext;
import org.apache.xmlgraphics.image.loader.ImageInfo;
import org.apache.xmlgraphics.image.loader.ImageSource;
import org.apache.xmlgraphics.image.loader.MockImageContext;
import org.junit.Test;

public class PreloaderEPSTest {

    @Test
    public void testEPSPreloaderOnEOF() throws Exception {
        ImageContext context = MockImageContext.newSafeInstance();

        PreloaderEPS preloader = new PreloaderEPS();
        ImageInputStream in = ImageIO.createImageInputStream(
                new ByteArrayInputStream(new byte[0]));
        ImageInfo info = preloader.preloadImage("test:eps:1",
                new ImageSource(in, "test:eps:1", true), context);
        assertNull(info);
        //Expect reset to beginning of stream
        assertEquals(0L, in.getStreamPosition());

        in = ImageIO.createImageInputStream(new ByteArrayInputStream("%!".getBytes()));
        info = preloader.preloadImage("test:eps:2",
                new ImageSource(in, "test:eps:2", true), context);
        assertNull(info);
        //Expect reset to beginning of stream
        assertEquals(0L, in.getStreamPosition());

    }

}
