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
package io.streamthoughts.azkarra.api.events.reactive.internal;

import java.util.Objects;

/**
 * A long-based {@link SubscriptionId}.
 *
 * @since 0.8.0
 */
public class LongSubscriptionId implements SubscriptionId {

    private final long id;

    /**
     * Creates a new {@link LongSubscriptionId} instance.
     * @param id   the identifier.
     */
    LongSubscriptionId(final long id) {
        this.id = id;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object get() {
        return id;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LongSubscriptionId)) return false;
        LongSubscriptionId that = (LongSubscriptionId) o;
        return id == that.id;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "LongSubscriptionId{id=" + id + '}';
    }
}
