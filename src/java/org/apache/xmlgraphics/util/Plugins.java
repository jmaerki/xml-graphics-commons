package org.apache.xmlgraphics.util;
import ch.jm.util.services.ServiceListener;
import ch.jm.util.services.Services;
import ch.jm.util.services.backend.ServicesBackend;

/*
 * Copyright 2012 Jeremias Maerki, Switzerland
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
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

/**
 * This class provides access to service/plug-in discovery.
 * <p>
 * Only use this class for plug-in interfaces defined in XML Graphics Commons! This
 * is to avoid class incompatibilities if there are multiple versions of the same
 * library deployed in an OSGi framework.
 * <p>
 * IMPORTANT: Please don't combine the singleton with the BundleActivator, because
 * that would create a hard runtime dependency on the OSGi API. If you keep the
 * two separate, the OSGi JARs won't be necessary in non-OSGi operation.
 */
public class Plugins {

    /**
     * This is the services singleton.
     */
    private static final Services SERVICES = new Services();

    /**
     * Replaces the services backend.
     * @param backend the new backend to use
     */
    public static void setServicesBackend(ServicesBackend backend) {
        SERVICES.setServicesBackend(backend);
    }

    /**
     * Convenience method to register a {@link ServiceListener} for a service provider
     * interface. This can be used when you have no way of knowing when a service
     * discovery client is no longer in use. In this case, we rely on garbage collection.
     * @param <T> the service provider interface
     * @param providerIntf the service provider interface
     * @param listener the service listener
     */
    public static <T> void addListener(Class<T> providerIntf, ServiceListener<T> listener) {
        SERVICES.addListener(providerIntf, listener);
    }

}
