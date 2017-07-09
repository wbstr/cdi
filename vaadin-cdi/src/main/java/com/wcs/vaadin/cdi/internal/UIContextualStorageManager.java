package com.wcs.vaadin.cdi.internal;

import com.wcs.vaadin.cdi.VaadinSessionScoped;
import com.vaadin.ui.UI;
import org.apache.deltaspike.core.util.context.AbstractContext;
import org.apache.deltaspike.core.util.context.ContextualStorage;

import javax.annotation.PreDestroy;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Manage and store ContextualStorage for UI context.
 * This class is responsible for
 * - selecting the active UI context
 * - creating, and providing the ContextualStorage for it
 * - destroying contextual instances
 */

@VaadinSessionScoped
public class UIContextualStorageManager implements Serializable {

    @Inject
    private BeanManager beanManager;
    private final Map<Integer, ContextualStorage> storageMap = new HashMap<Integer, ContextualStorage>();
    private transient Integer openingUiId;

    public ContextualStorage getContextualStorage(boolean createIfNotExist) {
        Integer uiId;
        if (openingUiId != null) {
            uiId = openingUiId;
        } else {
            uiId = UI.getCurrent().getUIId();
        }

        ContextualStorage storage = storageMap.get(uiId);
        if (storage == null && createIfNotExist) {
            storage = new VaadinContextualStorage(beanManager);
            storageMap.put(uiId, storage);
        }

        return storage;
    }

    public void prepareOpening(int uiId) {
        openingUiId = uiId;
    }

    public void cleanupOpening() {
        openingUiId = null;
    }

    public boolean isActive() {
        return UI.getCurrent() != null || openingUiId != null;
    }

    @PreDestroy
    private void destroyAll() {
        Collection<ContextualStorage> storages = storageMap.values();
        for (ContextualStorage storage : storages) {
            AbstractContext.destroyAllActive(storage);
        }
        storageMap.clear();
    }

    public void destroy(int uiId) {
        ContextualStorage storage = storageMap.remove(uiId);
        if (storage != null) {
            AbstractContext.destroyAllActive(storage);
        }
    }
}
