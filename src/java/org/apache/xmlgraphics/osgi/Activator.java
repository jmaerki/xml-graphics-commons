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

package org.apache.xmlgraphics.osgi;
/*
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

/* $Id: Activator.java 2363 2012-05-31 06:40:42Z jeremias $ */

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import org.apache.xmlgraphics.util.Plugins;

import ch.jm.util.services.osgi.ServicesOSGi;

/**
 * OSGi bundle activator. It replaces the default service discovery backend with an OSGi-based
 * implementation.
 * <p>
 * IMPORTANT: Please don't combine the {@link Plugins} singleton with the BundleActivator,
 * because that would create a hard runtime dependency on the OSGi API. If you keep the
 * two separate, the OSGi JARs won't be necessary in non-OSGi operation.
 */
public class Activator implements BundleActivator {

    private volatile ServicesOSGi services;

    /** {@inheritDoc} */
    public void start(BundleContext context) throws Exception {
        this.services = new ServicesOSGi(context);
        Plugins.setServicesBackend(this.services);
    }

    /** {@inheritDoc} */
    public void stop(BundleContext context) throws Exception {
        Plugins.setServicesBackend(null);
        if (this.services != null) {
            this.services.close();
            this.services = null;
        }
    }

}
