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
import io.streamthoughts.azkarra.api.components.ComponentScanner;
import io.streamthoughts.azkarra.api.config.Conf;
import io.streamthoughts.azkarra.api.server.AzkarraRestExtension;
import io.streamthoughts.azkarra.api.server.AzkarraRestExtensionContext;
import io.streamthoughts.azkarra.http.APIVersions;
import io.streamthoughts.azkarra.http.error.BadRequestException;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.util.Objects;

@Path(APIVersions.PATH_V1)
public class ComponentsJarUploadEndpoint {

    private static final String NAME = ComponentsJarUploadEndpoint.class.getSimpleName();
    private static final Logger LOG = LoggerFactory.getLogger(ComponentsJarUploadEndpoint.class);

    private final ComponentJarUploader uploader;

    /**
     * Creates a new {@link ComponentsJarUploadEndpoint} instance.
     *
     * @param uploader   the {@link ComponentJarUploader}.
     */
    public ComponentsJarUploadEndpoint(final ComponentJarUploader uploader) {
        this.uploader = Objects.requireNonNull(uploader, "uploader must not be null");
    }

    @POST
    @Path("/components")
    @Consumes("multipart/form-data")
    public Response upload(@FormDataParam("jar") InputStream is,
                                           @FormDataParam("jar") FormDataContentDisposition detail) {
        var fileName = detail.getFileName();
        if (!ComponentJarUploader.isJarExtension(fileName)) {
            throw new BadRequestException("Unsupported file extension, expected .jar, was " + fileName);
        }
        uploader.upload(fileName, is);
        return Response.noContent().build();
    }

    public static class ComponentsJarUploadEndpointExtension implements AzkarraRestExtension {

        private static final String ID = "components";
        private static final String DEFAULT_COMPONENTS_PATH = "/tmp/azkarra/components/";

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
            var path = extensionsConfig.getExtensionProperty(ID, "path").orElse(DEFAULT_COMPONENTS_PATH);
            var component = azkarraContext.getComponent(ComponentScanner.class);
            var endpoint = new ComponentsJarUploadEndpoint(new ComponentJarUploader(component, path));
            restContext.configurable().register(endpoint);
        }
    }
}
