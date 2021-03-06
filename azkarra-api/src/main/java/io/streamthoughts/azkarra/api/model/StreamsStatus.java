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
package io.streamthoughts.azkarra.api.model;

import org.apache.kafka.streams.processor.ThreadMetadata;

import java.util.Set;

public class StreamsStatus {

    private final String applicationId;
    private final String state;
    private final Set<ThreadMetadata> threads;

    /**
     * Creates a new {@link StreamsStatus} instance.
     *
     * @param threads   the set of {@link ThreadMetadata} instance.
     */
    public StreamsStatus(final String applicationId,
                         final String state,
                         final Set<ThreadMetadata> threads) {
        this.applicationId = applicationId;
        this.state = state;
        this.threads = threads;
    }

    public String getId() {
        return applicationId;
    }

    public String getState() {
        return state;
    }

    public Set<ThreadMetadata> getThreads() {
        return threads;
    }
}
