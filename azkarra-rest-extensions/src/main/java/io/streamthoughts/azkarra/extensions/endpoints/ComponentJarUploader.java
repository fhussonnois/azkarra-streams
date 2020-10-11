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

import io.streamthoughts.azkarra.api.components.ComponentScanner;
import io.streamthoughts.azkarra.api.errors.AzkarraException;
import io.streamthoughts.azkarra.api.time.Time;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

class ComponentJarUploader {

    private static final Logger LOG = LoggerFactory.getLogger(ComponentJarUploader.class);

    private final String componentPath;
    private final ComponentScanner scanner;

    /**
     * Creates a new {@link ComponentJarUploader} instance.
     *
     * @param scanner       the {@link ComponentScanner} instance.
     * @param componentPath the component path.
     */
    ComponentJarUploader(final ComponentScanner scanner, final String componentPath) {
        this.scanner = Objects.requireNonNull(scanner, "scanner must not be null");
        Objects.requireNonNull(componentPath, "componentPath cannot be null");
        this.componentPath = !componentPath.endsWith("/") ? componentPath + "/" : componentPath;
    }

    void upload(final String fileName, InputStream is) {
        var parent = componentPath +  "uploaded-" + Time.SYSTEM.milliseconds();
        var target = new File(parent, fileName);
        LOG.info("Uploading new external component: {}", target);
        try {
            var componentPath = Paths.get(parent);
            Files.createDirectories(componentPath);
            saveToFile(is, target);
            scanner.scanComponentPath(componentPath);
        } catch (Exception e) {
            LOG.error("Error while loading component JAR {} to directory {}", fileName, parent,  e);
            throw new AzkarraException(e);
        }
    }

    static boolean isJarExtension(final String fileName) {
        int index = fileName.lastIndexOf('.');
        if(index > 0) {
            return fileName.substring(index + 1).toLowerCase().equals("jar");
        }
        return false;
    }

    private void saveToFile(final InputStream inStream,
                            final File target) throws IOException {
        try (OutputStream out = new FileOutputStream(target)) {
            byte[] bytes = new byte[1024];
            int read;
            while ((read = inStream.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }
            out.flush();
        }
    }
}
