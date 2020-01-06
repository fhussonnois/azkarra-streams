/*
 * Copyright 2019 StreamThoughts.
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
package io.streamthoughts.azkarra.api.components;

/**
 * Interface which is used for generating component name.
 */
public interface ComponentNameGenerator {

    /**
     * The default {@link ComponentNameGenerator} implementation.
     */
    ComponentNameGenerator DEFAULT = new ComponentNameGenerator() {
        /**
         * {@inheritDoc}
         */
        @Override
        public <T> String generate(final ComponentDescriptor<T> descriptor) {
            String className = descriptor.type().getSimpleName();
            return Character.toLowerCase(className.charAt(0)) + className.substring(1);
        }
    };

    /**
     * Generates a component name for the specified component descriptor.
     *
     * @param descriptor    the {@link SimpleComponentDescriptor} instance.
     * @param <T>           the type of the component.
     * @return              the generated component.
     */
    <T> String generate(final ComponentDescriptor<T> descriptor);
}