package com.vaadin.cdi.uis;

import com.vaadin.cdi.*;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.navigator.ViewDisplay;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

@CDIUI("")
public class ViewNavigationUI extends UI {
    public static final String REVERTED_NAV_BTN_ID = "revertednavbtn";
    public static final String SUCCESS_NAV_BTN_ID = "successnavbtn";
    public static final String VALUE_LABEL_ID = "valuelabel";
    public static final String DEFAULTVIEW_VALUE = "defaultview";
    private static final String LABEL_ID = "label";
    private static final String REVERTME = "revertme";
    private static final String SUCCESS = "success";
    public static final String CHANGEDSUCCESS_VALUE = "successother";
    public static final String SUCCESSVIEW_VALUE = "successview";
    public static final String CHANGE_VALUE_BTN_ID = "othervalue";

    @Inject
    CDINavigator navigator;
    @Inject
    ViewScopedBean bean;

    @Override
    protected void init(VaadinRequest request) {
        setSizeFull();

        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();

        final Label label = new Label("label");
        label.setId(LABEL_ID);
        layout.addComponent(label);

        final Label value = new Label();
        value.setId(VALUE_LABEL_ID);
        layout.addComponent(value);

        navigator.init(this, new ViewDisplay() {
            @Override
            public void showView(View view) {
            }
        });
        navigator.addViewChangeListener(new ViewChangeListener() {
            @Override
            public boolean beforeViewChange(ViewChangeEvent event) {
                return !event.getViewName().equals(REVERTME);
            }

            @Override
            public void afterViewChange(ViewChangeEvent event) {
            }
        });

        Button navigateRevBtn = new Button("navigateSuccBtn revert");
        navigateRevBtn.setId(REVERTED_NAV_BTN_ID);
        navigateRevBtn.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                navigator.navigateTo(REVERTME);
                value.setValue(bean.getValue());
            }
        });
        layout.addComponent(navigateRevBtn);

        Button navigateSuccBtn = new Button("navigateSuccBtn success");
        navigateSuccBtn.setId(SUCCESS_NAV_BTN_ID);
        navigateSuccBtn.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                navigator.navigateTo(SUCCESS);
                value.setValue(bean.getValue());
            }
        });
        layout.addComponent(navigateSuccBtn);

        Button changeValueBtn = new Button("changevalue");
        changeValueBtn.setId(CHANGE_VALUE_BTN_ID);
        changeValueBtn.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                bean.setValue(CHANGEDSUCCESS_VALUE);
                value.setValue(bean.getValue());
            }
        });
        layout.addComponent(changeValueBtn);

        setContent(layout);
    }

    @CDIView("")
    public static class DefaultView implements View {
        @Inject
        ViewScopedBean bean;

        @Override
        public void enter(ViewChangeListener.ViewChangeEvent event) {
            bean.setValue(DEFAULTVIEW_VALUE);
        }
    }

    @CDIView(REVERTME)
    public static class RevertMeView implements View {
        @Inject
        ViewScopedBean bean;


        @PostConstruct
        private void init() {
            bean.setValue("revertedview");
        }

        @Override
        public void enter(ViewChangeListener.ViewChangeEvent event) {
            throw new IllegalStateException("Should never happen");
        }
    }

    @CDIView(SUCCESS)
    public static class SuccessView implements View {
        @Inject
        ViewScopedBean bean;


        @PostConstruct
        private void init() {
            bean.setValue(SUCCESSVIEW_VALUE);
        }

        @Override
        public void enter(ViewChangeListener.ViewChangeEvent event) {
        }
    }

    @NormalViewScoped
    public static class ViewScopedBean {
        private String value;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }


}
