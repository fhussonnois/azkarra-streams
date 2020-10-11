/*
 * Copyright 2019-2020 StreamThoughts.
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.streamthoughts.azkarra.extensions.endpoints;

import io.streamthoughts.azkarra.api.AzkarraContext;
import io.streamthoughts.azkarra.api.components.ComponentDescriptor;
import io.streamthoughts.azkarra.api.components.ComponentFactory;
import io.streamthoughts.azkarra.api.components.ExternalComponentClassLoader;
import io.streamthoughts.azkarra.api.components.qualifier.Qualifiers;
import io.streamthoughts.azkarra.api.config.Conf;
import io.streamthoughts.azkarra.api.server.AzkarraRestExtension;
import io.streamthoughts.azkarra.api.server.AzkarraRestExtensionContext;
import io.streamthoughts.azkarra.api.streams.TopologyProvider;
import io.streamthoughts.azkarra.http.APIVersions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import java.io.File;
import java.net.URL;
import java.util.Objects;
import java.util.Optional;

@Path(APIVersions.PATH_V1)
public class TopologyJarDownloadEndpoint {

    private static final String NAME = TopologyJarDownloadEndpoint.class.getSimpleName();
    private static final Logger LOG = LoggerFactory.getLogger(TopologyJarDownloadEndpoint.class);

    private final ComponentFactory componentFactory;

    /**
     * Creates a new {@link TopologyJarDownloadEndpoint} instance.
     *
     * @param componentFactory   the {@link ComponentFactory}.
     */
    public TopologyJarDownloadEndpoint(final ComponentFactory componentFactory) {
        this.componentFactory = Objects.requireNonNull(componentFactory, "componentFactory must not be null");
    }

    @GET
    @Path("/topologies/{type}/versions/{version}/jar")
    public Response upload(final @PathParam("type") String type,
                           final @PathParam("version") String version) {
        
        Optional<ComponentDescriptor<Object>> optionalDescriptor = componentFactory
            .findDescriptorByAlias(type, Qualifiers.byVersion(version));
        
        if (optionalDescriptor.isEmpty()) {
            throw new NotFoundException("No component found for type='" + type + "', version='" + version + "'");
        }

        var descriptor = optionalDescriptor.get();
        if (!TopologyProvider.class.isAssignableFrom(descriptor.type()) ) {
            throw new BadRequestException(
                "Component for type='"
                + type + "', version='"
                + version + "' does not implement TopologyProvider class");
        }

        var classLoader = descriptor.classLoader();
        if (!(classLoader instanceof ExternalComponentClassLoader)) {
            throw new BadRequestException(
                "Component for type='"
                + type + "', version='"
                + version + "' is not available as an external component");
        }
        return buildResponse(((ExternalComponentClassLoader) classLoader).componentLocation());
    }

    private Response buildResponse(final URL componentLocation) {
        File archive = new File(componentLocation.getFile());
        Response.ResponseBuilder response = Response.ok(archive);
        response.type("application/java-archive");
        response.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + archive.getName());
        return response.build();
    }

    public static class TopologyJarDownloadEndpointExtension implements AzkarraRestExtension {

        private static final String ID = "topologies.jars";

        private RestExtensionsConfig extensionsConfig;

        /**
         * {@inheritDoc}
         */
        @Override
        public void configure(final Conf configuration) {
            extensionsConfig = new RestExtensionsConfig(configuration);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void register(final AzkarraRestExtensionContext restContext) {
            final AzkarraContext azkarraContext = restContext.context();
            if (!extensionsConfig.isExtensionEnabled(ID)) {
                LOG.info("{} will not be registered to JAX-RS context. Endpoint not enabled.", NAME);
                return;
            }
            var endpoint = new TopologyJarDownloadEndpoint(azkarraContext.getComponentFactory());
            restContext.configurable().register(endpoint);
        }
    }
}
