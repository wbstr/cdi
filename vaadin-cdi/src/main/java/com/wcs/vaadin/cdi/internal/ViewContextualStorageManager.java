package com.wcs.vaadin.cdi.internal;

import com.wcs.vaadin.cdi.NormalUIScoped;
import org.apache.deltaspike.core.util.context.AbstractContext;
import org.apache.deltaspike.core.util.context.ContextualStorage;

import javax.annotation.PreDestroy;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.function.Supplier;

/**
 * Manage and store ContextualStorage for view context.
 * This class is responsible for
 * - selecting the active view context
 * - creating, and providing the ContextualStorage for it
 * - destroying contextual instances
 */
@NormalUIScoped
public class ViewContextualStorageManager implements Serializable {
    private Storage openingContext;
    private Storage currentContext;
    @Inject
    private BeanManager beanManager;

    public void applyChange() {
        destroy(currentContext);
        currentContext = openingContext;
        openingContext = null;
    }

    public <T> T prepareChange(Supplier<T> taskInOpeningContext) {
        destroy(openingContext);
        openingContext = new Storage();
        final Storage temp = currentContext;
        currentContext = openingContext;
        final T result = taskInOpeningContext.get();
        currentContext = temp;
        return result;
    }

    public void revertChange() {
        destroy(openingContext);
        openingContext = null;
    }

    public ContextualStorage getContextualStorage(boolean createIfNotExist) {
        return currentContext.getContextualStorage(beanManager, createIfNotExist);
    }

    public boolean isActive() {
        return currentContext != null;
    }

    @PreDestroy
    private void preDestroy() {
        destroy(openingContext);
        destroy(currentContext);
    }

    private void destroy(Storage storage) {
        if (storage != null) {
            storage.destroy();
        }
    }

    private static class Storage implements Serializable {
        private ContextualStorage contextualStorage;

        private ContextualStorage getContextualStorage(BeanManager beanManager, boolean createIfNotExist) {
            if (createIfNotExist && contextualStorage == null) {
                contextualStorage = new VaadinContextualStorage(beanManager);
            }
            return contextualStorage;
        }

        private void destroy() {
            if (contextualStorage != null) {
                AbstractContext.destroyAllActive(contextualStorage);
            }
        }
    }

}
