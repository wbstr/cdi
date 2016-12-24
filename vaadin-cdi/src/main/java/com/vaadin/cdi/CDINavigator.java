package com.vaadin.cdi;

import com.vaadin.cdi.internal.ActiveViewContextHolder;
import com.vaadin.cdi.internal.CDIUtil;
import com.vaadin.cdi.internal.VaadinViewChangeEvent;
import com.vaadin.cdi.internal.ViewScopedContext;
import com.vaadin.navigator.*;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.SingleComponentContainer;
import com.vaadin.ui.UI;
import com.vaadin.util.CurrentInstance;

import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import java.util.logging.Logger;

@NormalUIScoped
public class CDINavigator extends Navigator {
    @Inject
    private BeanManager beanManager;

    @Inject
    private ActiveViewContextHolder activeViewContextHolder;

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
    protected void fireAfterViewChange(ViewChangeListener.ViewChangeEvent event) {
        getLogger().fine(
                "Changing view from " + event.getOldView() + " to "
                        + event.getNewView());
        long sessionId = CDIUtil.getSessionId();
        int uiId = getUI().getUIId();
        String viewName = event.getViewName();
        activeViewContextHolder.switchTo(event.getViewName());
        beanManager.fireEvent(new VaadinViewChangeEvent(sessionId, uiId, viewName));
        super.fireAfterViewChange(event);
    }

    @Override
    protected void revertNavigation() {
        super.revertNavigation();
        activeViewContextHolder.revert();
    }

    private Logger getLogger() {
        return Logger.getLogger(this.getClass().getName());
    }
}
