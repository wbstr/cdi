package com.vaadin.cdi.uis;


import com.vaadin.cdi.CDIUI;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@CDIUI
public class SessionReplicationUI extends UI {

    public static final String SETVALUEBTN_ID = "setvalbtn";
    public static final String HTTPVALUELABEL_ID = "httplabel";
    public static final String VAADINVALUELABEL_ID = "vaadinlabel";
    public static final String VALUE = "session";

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
                VaadinSession vaadinSession = VaadinSession.getCurrent();
                vaadinSession.setAttribute("test",VALUE);
                vaadinSession.getSession().setAttribute("test",VALUE);
            }
        });

        setBtn.setId(SETVALUEBTN_ID);
        layout.addComponent(setBtn);

        Label vaadinLabel = new Label();
        vaadinLabel.setValue((String) VaadinSession.getCurrent().getAttribute("test"));
        vaadinLabel.setId(VAADINVALUELABEL_ID);
        layout.addComponent(vaadinLabel);

        Label httpLabel = new Label();
        httpLabel.setValue((String) VaadinSession.getCurrent().getSession().getAttribute("test"));
        httpLabel.setId(HTTPVALUELABEL_ID);
        layout.addComponent(httpLabel);
    }

}
