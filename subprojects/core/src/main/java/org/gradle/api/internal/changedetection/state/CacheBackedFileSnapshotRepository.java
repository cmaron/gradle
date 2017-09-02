/*
 * Copyright 2011 the original author or authors.
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
package org.gradle.api.internal.changedetection.state;

import org.gradle.cache.PersistentIndexedCache;
import org.gradle.internal.id.IdGenerator;
import org.gradle.internal.serialize.Serializer;

import java.util.UUID;

public class CacheBackedFileSnapshotRepository implements FileSnapshotRepository {
    private final PersistentIndexedCache<UUID, FileCollectionSnapshot> cache;
    private final IdGenerator<UUID> idGenerator;

    public CacheBackedFileSnapshotRepository(TaskHistoryStore cacheAccess, Serializer<FileCollectionSnapshot> serializer, IdGenerator<UUID> idGenerator) {
        this.idGenerator = idGenerator;
        cache = cacheAccess.createCache("fileSnapshots", UUID.class, serializer, 12000, false);
    }

    public UUID add(FileCollectionSnapshot snapshot) {
        UUID id = idGenerator.generateId();
        cache.put(id, snapshot);
        return id;
    }

    public FileCollectionSnapshot get(UUID id) {
        FileCollectionSnapshot snapshot = cache.get(id);
        if (snapshot == null) {
            throw new IllegalArgumentException("Cannot find snapshot for id: " + id);
        }
        return snapshot;
    }

    public void remove(UUID id) {
        cache.remove(id);
    }
}
