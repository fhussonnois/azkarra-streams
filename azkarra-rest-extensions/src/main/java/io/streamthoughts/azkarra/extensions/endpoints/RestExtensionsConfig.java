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

import io.streamthoughts.azkarra.api.config.Conf;

import java.util.Optional;

/**
 * Properties to configure REST extensions.
 */
class RestExtensionsConfig {

    private static final String EXTENSION_ENABLE_PREFIX = "rest.extensions.management.";

    private final Conf config;

    /**
     * Creates a new {@link RestExtensionsConfig} instance.
     * @param config    the {@link Conf}.
     */
    RestExtensionsConfig(final Conf config) {
        this.config = config;
    }

    boolean isExtensionEnabled(final String extension) {
        return config.getOptionalBoolean(enablePropertyFor(extension)).orElse(true);
    }

    Optional<String> getExtensionProperty(final String extension, final String property) {
        return config.getOptionalString(EXTENSION_ENABLE_PREFIX + extension + "." + property);
    }

    private String enablePropertyFor(final String extension) {
        return EXTENSION_ENABLE_PREFIX + extension + ".enable";
    }
}
