package com.wcs.vaadin.cdi;

import com.vaadin.navigator.NavigationStateManager;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.navigator.ViewDisplay;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.SingleComponentContainer;
import com.vaadin.ui.UI;
import com.wcs.vaadin.cdi.internal.ViewContextualStorageManager;

import javax.inject.Inject;

@NormalUIScoped
public class CDINavigator extends Navigator {

    @Inject
    private ViewContextualStorageManager viewContextualStorageManager;

    @Inject
    private CDIViewProvider cdiViewProvider;

    @Override
    public void init(UI ui, NavigationStateManager stateManager, ViewDisplay display) {
        super.init(ui, stateManager, display);
        addProvider(cdiViewProvider);
    }

    public void init(UI ui, ViewDisplay display) {
        init(ui, new UriFragmentManager(ui.getPage()), display);
    }

    public void init(UI ui, SingleComponentContainer container) {
        init(ui, new SingleComponentContainerViewDisplay(container));
    }

    public void init(UI ui, ComponentContainer container) {
        init(ui, new ComponentContainerViewDisplay(container));
    }

    @Override
    protected boolean fireBeforeViewChange(ViewChangeListener.ViewChangeEvent event) {
        final boolean navigationAllowed = super.fireBeforeViewChange(event);
        if (navigationAllowed) {
            viewContextualStorageManager.applyChange(event);
        } else {
            viewContextualStorageManager.revertChange(event);
        }
        return navigationAllowed;
    }

}
