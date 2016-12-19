/*
 * Copyright 2000-2013 Vaadin Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.cdi.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import javax.enterprise.context.spi.Contextual;
import javax.enterprise.inject.spi.BeanManager;

import org.apache.deltaspike.core.util.context.AbstractContext;
import org.apache.deltaspike.core.util.context.ContextualStorage;

import com.vaadin.server.VaadinSession;

/**
 * UIScopedContext is the context for @UIScoped beans.
 */
public abstract class AbstractVaadinContext extends AbstractContext {

    private TreeMap<Long, VaadinUICloseEvent> uiCloseQueue = new TreeMap<Long, VaadinUICloseEvent>();

    private final Object cleanupLock = new Object();

    private static final int CLEANUP_DELAY = 5000;

    private BeanManager beanManager;
    private Map<Long, SessionData> storageMap = new ConcurrentHashMap<Long, SessionData>();

    public static class StorageKey {
        protected final int uiId;

        public StorageKey(int uiId) {
            this.uiId = uiId;
        }

        public int getUiId() {
            return uiId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof StorageKey)) return false;

            StorageKey key = (StorageKey) o;

            return uiId == key.uiId;

        }

        @Override
        public int hashCode() {
            return uiId;
        }

        @Override
        public String toString() {
            return "StorageKey{" +
                    "uiId=" + uiId +
                    '}';
        }
    }

    protected static class SessionData {

        private Map<StorageKey, ContextualStorage> storageMap = new ConcurrentHashMap<StorageKey, ContextualStorage>();

        public SessionData() {
        }

        public Map<StorageKey, ContextualStorage> getStorageMap() {
            return storageMap;
        }

    }

    public AbstractVaadinContext(final BeanManager beanManager) {
        super(beanManager);
        this.beanManager = beanManager;
    }

    @Override
    public boolean isActive() {
        return true;
    }

    protected synchronized SessionData getSessionData(VaadinSession session,
            boolean createIfNotExist) {
        if (session == null) {
            return null;
        }
        long sessionId = CDIUtil.getSessionId(session);
        return getSessionData(sessionId, createIfNotExist);
    }

    protected synchronized SessionData getSessionData(boolean createIfNotExist) {
        return getSessionData(VaadinSession.getCurrent(), createIfNotExist);
    }

    protected synchronized SessionData getSessionData(long sessionId,
            boolean createIfNotExist) {
        if (storageMap.containsKey(sessionId)) {
            return storageMap.get(sessionId);
        } else {
            if (createIfNotExist) {
                SessionData data = new SessionData();
                storageMap.put(sessionId, data);
                return data;
            } else {
                return null;
            }
        }
    }

    @Override
    protected synchronized ContextualStorage getContextualStorage(Contextual<?> contextual, boolean createIfNotExist) {
        getLogger().fine("Retrieving contextual storage for " + contextual);

        SessionData sessionData = getSessionData(createIfNotExist);
        if (sessionData == null) {
            if (createIfNotExist) {
                throw new IllegalStateException(
                        "Session data not recoverable for " + contextual);
            } else {
                // noop
                return null;
            }
        }

        Map<StorageKey, ContextualStorage> map = sessionData.getStorageMap();
        if (map == null) {
            return null;
        }

        StorageKey key = getStorageKey(contextual);
        if (map.containsKey(key)) {
            return map.get(key);
        } else if (createIfNotExist) {
            ContextualStorage storage = new VaadinContextualStorage(getBeanManager(),
                    true);
            map.put(key, storage);
            return storage;
        } else {
            return null;
        }

    }

    protected abstract StorageKey getStorageKey(Contextual<?> contextual);

    void dropSessionData(VaadinSessionDestroyEvent event) {
        long sessionId = event.getSessionId();
        getLogger().fine("Dropping session data for session: " + sessionId);

        SessionData sessionData = storageMap.remove(sessionId);
        if (sessionData != null) {
            synchronized (sessionData) {
                Collection<ContextualStorage> storages = sessionData.storageMap.values();
                for (ContextualStorage storage : storages) {
                    destroyAllActive(storage);
                }
            }
        }
    }

    private synchronized void destroyUIContext(SessionData sessionData, int uiId) {
        getLogger().fine("Destroying UI context for UI: " + uiId);

        for (Entry<StorageKey, ContextualStorage> entry : new ArrayList<Entry<StorageKey, ContextualStorage>>(
                sessionData.getStorageMap().entrySet())) {
            StorageKey key = entry.getKey();
            if (key.getUiId() == uiId) {
                final ContextualStorage contextualStorage = entry.getValue();
                destroyAllActive(contextualStorage);
                sessionData.storageMap.remove(key);
            }
        }
    }

    void queueUICloseEvent(VaadinUICloseEvent event) {
        synchronized (cleanupLock) {
            // We introduce a cleanup delay because the UI gets referred to
            // later in the core cleanup process. If the UI is proxied this will
            // cause a new UI to be initialized in some CDI implementations (for
            // example Apache OpenWebBeans 1.2.1)
            long closeTime = System.currentTimeMillis() + CLEANUP_DELAY;
            while (uiCloseQueue.get(closeTime) != null) {
                closeTime++;
            }
            uiCloseQueue.put(closeTime, event);
        }
    }

    void uiCloseCleanup() {
        // Remove the UI's that have been previously queued for closing. We need
        // to protect the UI context from deletion long enough that the core
        // framework has time to do it's own cleanup.
        // We run the cleanup process from VaadinCDIServletService after the
        // results of the latest query have been sent. We do it this way to
        // avoid using a background thread and to maintain cross-implementation
        // compatibility.
        Collection<Entry<Long, VaadinUICloseEvent>> entries = null;
        synchronized (cleanupLock) {
            long currentTime = System.currentTimeMillis();
            SortedMap<Long, VaadinUICloseEvent> subMap = uiCloseQueue
                    .headMap(currentTime);
            entries = new ArrayList<Map.Entry<Long, VaadinUICloseEvent>>(
                    subMap.entrySet());
            // Remove the entries from the underlying uiCloseQueue
            subMap.clear();
        }
        if (entries != null && !entries.isEmpty()) {
            for (Entry<Long, VaadinUICloseEvent> entry : entries) {
                VaadinUICloseEvent event = entry.getValue();
                SessionData sessionData = getSessionData(event.getSessionId(),
                        false);
                if (sessionData != null) {
                    destroyUIContext(sessionData, event.getUiId());
                }
            }
        }

    }

    protected abstract Logger getLogger();

    public BeanManager getBeanManager() {
        return beanManager;
    }
}
