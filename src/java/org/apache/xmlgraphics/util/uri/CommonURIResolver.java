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

package org.apache.xmlgraphics.util.uri;

import java.util.List;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;

import org.apache.xmlgraphics.util.Plugins;

import ch.jm.util.services.ServiceListener;
import ch.jm.util.services.ServiceTracker;

/**
 * A URI Resolver which aggregates other URI resolvers found through {@link Plugins}.
 * <p>
 * This resolver will try all resolvers registered as an {@link URIResolver}
 * class. For proper operation, the registered URIResolvers must return null if
 * they cannot handle the given URI and fail fast.
 */
public class CommonURIResolver implements URIResolver {

    private final List<URIResolver> uriResolvers = new java.util.LinkedList<URIResolver>();

    private static final class DefaultInstanceHolder {
        private static final CommonURIResolver INSTANCE = new CommonURIResolver();
    }

    /**
     * Creates a new CommonURIResolver. Use this if you need support for
     * resolvers in the current context.
     * @see CommonURIResolver#getDefaultURIResolver()
     */
    public CommonURIResolver() {
        ServiceTracker<URIResolver> tracker = Plugins.getServiceTracker(URIResolver.class);
        tracker.addServiceListener(new ServiceListener<URIResolver>() {

            public void added(URIResolver resolver) {
                register(resolver);
            }

            public void removed(URIResolver resolver) {
                unregister(resolver);
            }

        });
    }

    /**
     * Retrieve the default resolver instance.
     *
     * @return the default resolver instance.
     */
    public static CommonURIResolver getDefaultURIResolver() {
        return DefaultInstanceHolder.INSTANCE;
    }

    /** {@inheritDoc} */
    public Source resolve(String href, String base) {
        synchronized (uriResolvers) {
            for (URIResolver resolver : uriResolvers) {
                try {
                    final Source result = resolver.resolve(href, base);
                    if (result != null) {
                        return result;
                    }
                } catch (TransformerException e) {
                    // Ignore.
                }
            }
        }
        return null;
    }

    /**
     * Register a given {@link URIResolver} while the software is running.
     *
     * @param uriResolver
     *            the resolver to register.
     */
    public void register(URIResolver uriResolver) {
        synchronized (uriResolvers) {
            uriResolvers.add(uriResolver);
        }
    }

    /**
     * Unregister a given {@link URIResolver} while the software is running.
     *
     * @param uriResolver
     *            the resolver to unregister.
     */
    public void unregister(URIResolver uriResolver) {
        synchronized (uriResolvers) {
            uriResolvers.remove(uriResolver);
        }
    }

}
