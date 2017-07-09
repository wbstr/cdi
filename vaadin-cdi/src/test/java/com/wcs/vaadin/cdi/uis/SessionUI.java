package com.wcs.vaadin.cdi.uis;


import com.wcs.vaadin.cdi.CDIUI;
import com.wcs.vaadin.cdi.VaadinSessionScoped;
import com.wcs.vaadin.cdi.internal.Counter;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import javax.annotation.PreDestroy;
import javax.inject.Inject;
import java.io.Serializable;

@CDIUI
public class SessionUI extends UI {

    public static final String SETVALUEBTN_ID = "setvalbtn";
    public static final String VALUELABEL_ID = "label";
    public static final String VALUE = "session";
    public static final String INVALIDATEBTN_ID = "invalidatebtn";
    public static final String HTTP_INVALIDATEBTN_ID = "httpinvalidatebtn";
    public static final String EXPIREBTN_ID = "expirebtn";

    @Inject
    SessionScopedBean sessionScopedBean;

    @Override
    protected void init(VaadinRequest request) {
        setSizeFull();
        VerticalLayout layout = new VerticalLayout();
        setContent(layout);
        layout.setSizeFull();

        Button setBtn = new Button("set");
        setBtn.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                sessionScopedBean.setValue(VALUE);
            }
        });
        setBtn.setId(SETVALUEBTN_ID);
        layout.addComponent(setBtn);

        Button invalidateBtn = new Button("invalidate");
        invalidateBtn.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                VaadinSession.getCurrent().close();
            }
        });
        invalidateBtn.setId(INVALIDATEBTN_ID);
        layout.addComponent(invalidateBtn);

        Button httpInvalidateBtn = new Button("httpinvalidate");
        httpInvalidateBtn.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                VaadinSession.getCurrent().getSession().invalidate();
            }
        });
        httpInvalidateBtn.setId(HTTP_INVALIDATEBTN_ID);
        layout.addComponent(httpInvalidateBtn);

        Button expireBtn = new Button("httpexpire");
        expireBtn.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                VaadinSession.getCurrent().getSession().setMaxInactiveInterval(1);
            }
        });
        expireBtn.setId(EXPIREBTN_ID);
        layout.addComponent(expireBtn);

        Label label = new Label();
        label.setValue(sessionScopedBean.getValue()); // bean instantiated here
        label.setId(VALUELABEL_ID);
        layout.addComponent(label);
    }

    @VaadinSessionScoped
    public static class SessionScopedBean implements Serializable {
        public static final String DESTROY_COUNT = "SessionScopedBeanDestroy";

        @Inject
        Counter counter;

        private String value;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        @PreDestroy
        private void preDestroy() {
            counter.increment(DESTROY_COUNT);
        }
    }
}
