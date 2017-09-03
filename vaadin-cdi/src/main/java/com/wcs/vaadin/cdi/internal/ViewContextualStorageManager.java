package com.wcs.vaadin.cdi.internal;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.wcs.vaadin.cdi.CDIView;
import com.wcs.vaadin.cdi.NormalUIScoped;
import com.wcs.vaadin.cdi.ViewContextStrategy;
import com.wcs.vaadin.cdi.ViewContextStrategy.ViewState;
import org.apache.deltaspike.core.api.provider.BeanProvider;
import org.apache.deltaspike.core.util.context.AbstractContext;
import org.apache.deltaspike.core.util.context.ContextualStorage;

import javax.annotation.PreDestroy;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import java.io.Serializable;

/**
 * Manage and store ContextualStorage for view context.
 * This class is responsible for
 * - selecting the active view context
 * - creating, and providing the ContextualStorage for it
 * - destroying contextual instances
 */
@NormalUIScoped
public class ViewContextualStorageManager implements Serializable {
    private final static Storage CLOSED = new ClosedStorage();
    private Storage openingContext = CLOSED;
    private Storage currentContext = CLOSED;
    @Inject
    private BeanManager beanManager;

    public void applyChange(ViewChangeListener.ViewChangeEvent event) {
        final ViewState viewState = new ViewState(event.getViewName(), event.getParameters());
        if (!currentContext.contains(viewState)) {
            currentContext.destroy();
            currentContext = openingContext;
            openingContext = CLOSED;
        }
    }

    public View prepareChange(Bean viewBean, ViewState viewState) {
        final Class beanClass = viewBean.getBeanClass();
        final Storage temp = currentContext;
        if (!currentContext.contains(viewState)) {
            openingContext.destroy();
            openingContext = new Storage(getViewContextStrategy(beanClass));
            currentContext = openingContext;
        }
        final View view = (View) BeanProvider.getContextualReference(beanClass, viewBean);
        currentContext = temp;
        return view;
    }

    private ViewContextStrategy getViewContextStrategy(Class<?> beanClass) {
        final CDIView viewAnnotation = beanClass.getAnnotation(CDIView.class);
        return BeanProvider.getContextualReference(viewAnnotation.contextStrategy());
    }

    public void revertChange(ViewChangeListener.ViewChangeEvent event) {
        final ViewState viewState = new ViewState(event.getViewName(), event.getParameters());
        if (openingContext.contains(viewState)) {
            openingContext.destroy();
            openingContext = CLOSED;
        }
    }

    public ContextualStorage getContextualStorage(boolean createIfNotExist) {
        return currentContext.getContextualStorage(beanManager, createIfNotExist);
    }

    public boolean isActive() {
        return currentContext != CLOSED;
    }

    @PreDestroy
    private void preDestroy() {
        openingContext.destroy();
        currentContext.destroy();
    }

    private static class Storage implements Serializable {
        ContextualStorage contextualStorage;
        final ViewContextStrategy strategy;

        Storage(ViewContextStrategy strategy) {
            this.strategy = strategy;
        }

        ContextualStorage getContextualStorage(BeanManager beanManager, boolean createIfNotExist) {
            if (createIfNotExist && contextualStorage == null) {
                contextualStorage = new VaadinContextualStorage(beanManager);
            }
            return contextualStorage;
        }

        void destroy() {
            if (contextualStorage != null) {
                AbstractContext.destroyAllActive(contextualStorage);
            }
        }

        boolean contains(ViewState state) {
            return strategy.contains(state);
        }
    }

    private static class ClosedStorage extends Storage {
        ClosedStorage() {
            super((ViewContextStrategy) state -> false);
        }

        @Override
        ContextualStorage getContextualStorage(BeanManager beanManager, boolean createIfNotExist) {
            throw new IllegalStateException("storage is closed");
        }
    }

}
