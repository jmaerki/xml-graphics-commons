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

package org.apache.xmlgraphics.image.loader.spi;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.xmlgraphics.image.loader.ImageFlavor;
import org.apache.xmlgraphics.image.loader.ImageInfo;
import org.apache.xmlgraphics.image.loader.util.Penalty;
import org.apache.xmlgraphics.util.Plugins;

import ch.jm.util.services.ServiceListener;

/**
 * This class is the registry for all implementations of the various service provider interfaces
 * for the image package.
 */
public class ImageImplRegistry {

    /** logger */
    protected static Log log = LogFactory.getLog(ImageImplRegistry.class);

    /** Infinite penalty value which shall force any implementation to become ineligible. */
    public static final int INFINITE_PENALTY = Integer.MAX_VALUE;

    /** Holds the list of preloaders */
    private List<PreloaderHolder> preloaders = new java.util.ArrayList<PreloaderHolder>();
    private int lastPreloaderIdentifier;
    private int lastPreloaderSort;

    /** Holds the list of ImageLoaderFactories */
    private Map<String, Map<ImageFlavor, List<ImageLoaderFactory>>> loaders
            = new java.util.HashMap<String, Map<ImageFlavor, List<ImageLoaderFactory>>>();

    /** Holds the list of ImageConverters */
    private List<ImageConverter> converters = new java.util.ArrayList<ImageConverter>();

    //service listeners
    private ServiceListener<ImagePreloader> preloaderListener;
    private ServiceListener<ImageLoaderFactory> loaderFactoryListener;
    private ServiceListener<ImageConverter> converterListener;

    private int converterModifications;

    /** A Map (key: implementation classes) with additional penalties to fine-tune the registry. */
    private Map<String, Penalty> additionalPenalties = new java.util.HashMap<String, Penalty>();
    //Note: String as key chosen to avoid possible class-unloading leaks

    /** Singleton instance */
    private static ImageImplRegistry defaultInstance;

    /**
     * Main constructor. This constructor allows to disable plug-in discovery for testing purposes.
     * @param discover true if implementation classes shall automatically be discovered.
     */
    public ImageImplRegistry(boolean discover) {
        if (discover) {
            setupServiceListenersForPreloaders();
            setupServiceListenersForLoaders();
            setupServiceListenersForConverters();
        }
    }

    /**
     * Main constructor.
     * @see #getDefaultInstance()
     */
    public ImageImplRegistry() {
        this(true);
    }

    /**
     * Returns the default instance of the Image implementation registry.
     * @return the default instance
     */
    public static ImageImplRegistry getDefaultInstance() {
        if (defaultInstance == null) {
            defaultInstance = new ImageImplRegistry();
        }
        return defaultInstance;
    }

    /**
     * Discovers all implementations in the application's classpath.
     * @deprecated this method should never have been public.
     */
    @Deprecated
    public void discoverClasspathImplementations() {
        //nop
    }

    private void setupServiceListenersForPreloaders() {
        //Dynamic registration of ImagePreloaders
        this.preloaderListener = new ServiceListener<ImagePreloader>() {

            public void added(ImagePreloader plugin) {
                registerPreloader(plugin);
            }

            public void removed(ImagePreloader plugin) {
                unregisterPreloader(plugin);
            }

        };
        Plugins.addListener(ImagePreloader.class, this.preloaderListener);
    }

    private void setupServiceListenersForLoaders() {
        //Dynamic registration of ImageLoaderFactories
        this.loaderFactoryListener = new ServiceListener<ImageLoaderFactory>() {

            public void added(ImageLoaderFactory plugin) {
                registerLoaderFactory(plugin);
            }

            public void removed(ImageLoaderFactory plugin) {
                unregisterLoaderFactory(plugin);
            }

        };
        Plugins.addListener(ImageLoaderFactory.class, this.loaderFactoryListener);
    }

    private void setupServiceListenersForConverters() {
        //Dynamic registration of ImageConverters
        this.converterListener = new ServiceListener<ImageConverter>() {

            public void added(ImageConverter plugin) {
                registerConverter(plugin);
            }

            public void removed(ImageConverter plugin) {
                unregisterConverter(plugin);
            }

        };
        Plugins.addListener(ImageConverter.class, this.converterListener);
    }

    /**
     * Registers a new ImagePreloader.
     * @param preloader An ImagePreloader instance
     */
    public void registerPreloader(ImagePreloader preloader) {
        if (log.isDebugEnabled()) {
            log.debug("Registered " + preloader.getClass().getName()
                    + " with priority " + preloader.getPriority());
        }
        preloaders.add(newPreloaderHolder(preloader));
    }

    /**
     * Unregisters an {@link ImagePreloader}.
     * @param preloader An {@link ImagePreloader} instance
     */
    public synchronized void unregisterPreloader(ImagePreloader preloader) {
        Iterator<PreloaderHolder> iter = preloaders.iterator();
        while (iter.hasNext()) {
            PreloaderHolder holder = iter.next();
            if (holder.preloader == preloader) {
                iter.remove();
                if (log.isDebugEnabled()) {
                    log.debug("Unregistered " + preloader.getClass().getName());
                }
                break;
            }
        }
    }

    private synchronized PreloaderHolder newPreloaderHolder(ImagePreloader preloader) {
        PreloaderHolder holder = new PreloaderHolder();
        holder.preloader = preloader;
        holder.identifier = ++lastPreloaderIdentifier;
        return holder;
    }

    /** Holder class for registered {@link ImagePreloader} instances. */
    private static class PreloaderHolder {
        private ImagePreloader preloader;
        private int identifier;

        @Override
        public String toString() {
            return preloader + " " + identifier;
        }
    }

    private synchronized void sortPreloaders() {
        if (this.lastPreloaderIdentifier != this.lastPreloaderSort) {
            Collections.sort(this.preloaders, new Comparator<PreloaderHolder>() {

                public int compare(PreloaderHolder h1, PreloaderHolder h2) {
                    long p1 = h1.preloader.getPriority();
                    p1 += getAdditionalPenalty(h1.preloader.getClass().getName()).getValue();

                    int p2 = h2.preloader.getPriority();
                    p2 += getAdditionalPenalty(h2.preloader.getClass().getName()).getValue();

                    int diff = Penalty.truncate(p1 - p2);
                    if (diff != 0) {
                        return diff;
                    } else {
                        diff = h1.identifier - h2.identifier;
                        return diff;
                    }
                }

            });
            this.lastPreloaderSort = lastPreloaderIdentifier;
        }
    }

    /**
     * Registers a new ImageLoaderFactory.
     * @param loaderFactory An ImageLoaderFactory instance
     */
    public void registerLoaderFactory(ImageLoaderFactory loaderFactory) {
        if (!loaderFactory.isAvailable()) {
            if (log.isDebugEnabled()) {
                log.debug("ImageLoaderFactory reports not available: "
                        + loaderFactory.getClass().getName());
            }
            return;
        }
        String[] mimes = loaderFactory.getSupportedMIMETypes();
        for (int i = 0, ci = mimes.length; i < ci; i++) {
            String mime = mimes[i];

            synchronized (loaders) {
                Map<ImageFlavor, List<ImageLoaderFactory>> flavorMap = loaders.get(mime);
                if (flavorMap == null) {
                    flavorMap = new java.util.HashMap<ImageFlavor, List<ImageLoaderFactory>>();
                    loaders.put(mime, flavorMap);
                }

                ImageFlavor[] flavors = loaderFactory.getSupportedFlavors(mime);
                for (int j = 0, cj = flavors.length; j < cj; j++) {
                    ImageFlavor flavor = flavors[j];

                    List<ImageLoaderFactory> factoryList = flavorMap.get(flavor);
                    if (factoryList == null) {
                        factoryList = new java.util.ArrayList<ImageLoaderFactory>();
                        flavorMap.put(flavor, factoryList);
                    }
                    factoryList.add(loaderFactory);

                    if (log.isDebugEnabled()) {
                        log.debug("Registered " + loaderFactory.getClass().getName()
                                + ": MIME = " + mime + ", Flavor = " + flavor);
                    }
                }
            }
        }
    }

    /**
     * Unregisters a new {@link ImageLoaderFactory}.
     * @param loaderFactory An {@link ImageLoaderFactory} instance
     */
    public void unregisterLoaderFactory(ImageLoaderFactory loaderFactory) {
        String[] mimes = loaderFactory.getSupportedMIMETypes();
        for (int i = 0, ci = mimes.length; i < ci; i++) {
            String mime = mimes[i];

            synchronized (loaders) {
                Map<ImageFlavor, List<ImageLoaderFactory>> flavorMap = loaders.get(mime);
                if (flavorMap == null) {
                    continue;
                }

                ImageFlavor[] flavors = loaderFactory.getSupportedFlavors(mime);
                for (int j = 0, cj = flavors.length; j < cj; j++) {
                    ImageFlavor flavor = flavors[j];

                    List<ImageLoaderFactory> factoryList = flavorMap.get(flavor);
                    if (factoryList == null) {
                        continue;
                    }
                    if (factoryList.remove(loaderFactory)) {
                        if (log.isDebugEnabled()) {
                            log.debug("Unregistered " + loaderFactory.getClass().getName()
                                    + ": MIME = " + mime + ", Flavor = " + flavor);
                        }
                    }
                }
            }
        }
    }

    /**
     * Returns the Collection of registered ImageConverter instances.
     * @return a Collection<ImageConverter>
     */
    public Collection<ImageConverter> getImageConverters() {
        return Collections.unmodifiableList(this.converters);
    }

    /**
     * Returns the number of modifications to the collection of registered ImageConverter instances.
     * This is used to detect changes in the registry concerning ImageConverters.
     * @return the number of modifications
     */
    public int getImageConverterModifications() {
        return this.converterModifications;
    }

    /**
     * Registers a new ImageConverter.
     * @param converter An ImageConverter instance
     */
    public void registerConverter(ImageConverter converter) {
        converters.add(converter);
        converterModifications++;
        if (log.isDebugEnabled()) {
            log.debug("Registered: " + converter.getClass().getName());
        }
    }

    /**
     * Unregisters an {@link ImageConverter}.
     * @param converter An {@link ImageConverter} instance
     */
    public void unregisterConverter(ImageConverter converter) {
        if (converters.remove(converter)) {
            converterModifications++;
            if (log.isDebugEnabled()) {
                log.debug("Unregistered: " + converter.getClass().getName());
            }
        }
    }

    /**
     * Returns an iterator over all registered ImagePreloader instances.
     * @return an iterator over ImagePreloader instances.
     */
    public Iterator<ImagePreloader> getPreloaderIterator() {
        sortPreloaders();
        final Iterator<PreloaderHolder> iter = this.preloaders.iterator();
        //Unpack the holders
        return new Iterator<ImagePreloader>() {

            public boolean hasNext() {
                return iter.hasNext();
            }

            public ImagePreloader next() {
                PreloaderHolder holder = iter.next();
                if (holder != null) {
                    return holder.preloader;
                } else {
                    return null;
                }
            }

            public void remove() {
                iter.remove();
            }

        };
    }

    /**
     * Returns the best ImageLoaderFactory supporting the {@link ImageInfo} and image flavor.
     * If there are multiple ImageLoaderFactories the one with the least usage penalty is selected.
     * @param imageInfo the image info object
     * @param flavor the image flavor.
     * @return an ImageLoaderFactory instance or null, if no suitable implementation was found
     */
    public ImageLoaderFactory getImageLoaderFactory(ImageInfo imageInfo, ImageFlavor flavor) {
        String mime = imageInfo.getMimeType();
        Map<ImageFlavor, List<ImageLoaderFactory>> flavorMap = loaders.get(mime);
        if (flavorMap != null) {
            List<ImageLoaderFactory> factoryList = flavorMap.get(flavor);
            if (factoryList != null && factoryList.size() > 0) {
                Iterator<ImageLoaderFactory> iter = factoryList.iterator();
                int bestPenalty = Integer.MAX_VALUE;
                ImageLoaderFactory bestFactory = null;
                while (iter.hasNext()) {
                    ImageLoaderFactory factory = iter.next();
                    if (!factory.isSupported(imageInfo)) {
                        continue;
                    }
                    ImageLoader loader = factory.newImageLoader(flavor);
                    int penalty = loader.getUsagePenalty();
                    if (penalty < bestPenalty) {
                        bestPenalty = penalty;
                        bestFactory = factory;
                    }
                }
                return bestFactory;
            }
        }
        return null;
    }

    /**
     * Returns an array of {@link ImageLoaderFactory} instances that support the MIME type
     * indicated by an {@link ImageInfo} object and can generate the given image flavor.
     * @param imageInfo the image info object
     * @param flavor the target image flavor
     * @return the array of image loader factories
     */
    public ImageLoaderFactory[] getImageLoaderFactories(ImageInfo imageInfo, ImageFlavor flavor) {
        String mime = imageInfo.getMimeType();
        Collection<ImageLoaderFactory> matches = new java.util.TreeSet<ImageLoaderFactory>(
                new ImageLoaderFactoryComparator(flavor));
        Map<ImageFlavor, List<ImageLoaderFactory>> flavorMap = loaders.get(mime);
        if (flavorMap != null) {
            for (ImageFlavor checkFlavor : flavorMap.keySet()) {
                if (checkFlavor.isCompatible(flavor)) {
                    List<ImageLoaderFactory> factoryList = flavorMap.get(checkFlavor);
                    if (factoryList != null && !factoryList.isEmpty()) {
                        for (ImageLoaderFactory factory : factoryList) {
                            if (factory.isSupported(imageInfo)) {
                                matches.add(factory);
                            }
                        }
                    }
                }
            }
        }
        if (matches.size() == 0) {
            return null;
        } else {
            return matches.toArray(new ImageLoaderFactory[matches.size()]);
        }
    }

    /** Comparator for {@link ImageLoaderFactory} classes. */
    private class ImageLoaderFactoryComparator implements Comparator<ImageLoaderFactory> {

        private ImageFlavor targetFlavor;

        public ImageLoaderFactoryComparator(ImageFlavor targetFlavor) {
            this.targetFlavor = targetFlavor;
        }

        public int compare(ImageLoaderFactory f1, ImageLoaderFactory f2) {
            ImageLoader l1 = f1.newImageLoader(targetFlavor);
            long p1 = l1.getUsagePenalty();
            p1 += getAdditionalPenalty(l1.getClass().getName()).getValue();

            ImageLoader l2 = f2.newImageLoader(targetFlavor);
            long p2 = l2.getUsagePenalty();
            p2 = getAdditionalPenalty(l2.getClass().getName()).getValue();

            //Lowest penalty first
            return Penalty.truncate(p1 - p2);
        }

    }


    /**
     * Returns an array of ImageLoaderFactory instances which support the given MIME type. The
     * instances are returned in no particular order.
     * @param mime the MIME type to find ImageLoaderFactories for
     * @return the array of ImageLoaderFactory instances
     */
    public ImageLoaderFactory[] getImageLoaderFactories(String mime) {
        Map<ImageFlavor, List<ImageLoaderFactory>> flavorMap = loaders.get(mime);
        if (flavorMap != null) {
            Set<ImageLoaderFactory> factories = new java.util.HashSet<ImageLoaderFactory>();
            for (List<ImageLoaderFactory> factoryList : flavorMap.values()) {
                factories.addAll(factoryList);
            }
            int factoryCount = factories.size();
            if (factoryCount > 0) {
                return factories.toArray(new ImageLoaderFactory[factoryCount]);
            }
        }
        return null;
    }

    /**
     * Sets an additional penalty for a particular implementation class for any of the interface
     * administered by this registry class. No checking is performed to verify if the className
     * parameter is valid.
     * @param className the fully qualified class name of the implementation class
     * @param penalty the additional penalty or null to clear any existing value
     */
    public void setAdditionalPenalty(String className, Penalty penalty) {
        if (penalty != null) {
            this.additionalPenalties.put(className, penalty);
        } else {
            this.additionalPenalties.remove(className);
        }
        this.lastPreloaderSort = -1; //Force resort, just in case this was a preloader
    }

    /**
     * Returns the additional penalty value set for a particular implementation class.
     * If no such value is set, 0 is returned.
     * @param className the fully qualified class name of the implementation class
     * @return the additional penalty value
     */
    public Penalty getAdditionalPenalty(String className) {
        Penalty p = this.additionalPenalties.get(className);
        return (p != null ? p : Penalty.ZERO_PENALTY);
    }

}
