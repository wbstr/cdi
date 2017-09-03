/*
 * Copyright 2017 kumm.
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
 *
 */

package com.wcs.vaadin.cdi.uis;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.*;
import com.wcs.vaadin.cdi.CDINavigator;
import com.wcs.vaadin.cdi.CDIUI;
import com.wcs.vaadin.cdi.CDIView;
import com.wcs.vaadin.cdi.NormalViewScoped;
import com.wcs.vaadin.cdi.ViewContextStrategy.Always;
import com.wcs.vaadin.cdi.ViewContextStrategy.ViewName;
import com.wcs.vaadin.cdi.ViewContextStrategy.ViewNameAndParameters;
import com.wcs.vaadin.cdi.internal.Counter;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

@CDIUI("")
public class ViewStrategyUI extends UI {
    private static final String BYVIEWNAME = "byviewname";
    public static final String VIEWNAME_NAV_BTN_ID = "viewnamenavbtn";
    public static final String P1VIEWNAME_NAV_BTN_ID = "p1viewnamenavbtn";
    public static final String P2VIEWNAME_NAV_BTN_ID = "p2viewnamenavbtn";

    private static final String BYVIEWNAMEPARAMS = "byviewnameparams";
    public static final String VIEWNAMEPARAMS_NAV_BTN_ID = "viewnameparamsnavbtn";
    public static final String P1VIEWNAMEPARAMS_NAV_BTN_ID = "p1viewnameparamsnavbtn";
    public static final String P2VIEWNAMEPARAMS_NAV_BTN_ID = "p2viewnameparamsnavbtn";

    private static final String BYALWAYS = "byalways";
    public static final String ALWAYS_NAV_BTN_ID = "alwaysnavbtn";
    public static final String P1ALWAYS_NAV_BTN_ID = "p1alwaysnavbtn";
    public static final String P2ALWAYS_NAV_BTN_ID = "p2alwaysnavbtn";

    private static final String OTHER = "other";
    public static final String OTHERVIEW_NAV_BTN_ID = "otherviewnavbtn";

    public static final String VALUE_LABEL_ID = "valuelabel";
    private static final String LABEL_ID = "label";

    @Inject
    CDINavigator navigator;
    @Inject
    ViewScopedBean bean;
    private Label value;

    @Override
    protected void init(VaadinRequest request) {
        setSizeFull();

        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();

        final Label label = new Label("label");
        label.setId(LABEL_ID);
        layout.addComponent(label);

        value = new Label();
        value.setId(VALUE_LABEL_ID);
        layout.addComponent(value);

        final Panel viewDisplayPanel = new Panel();
        viewDisplayPanel.setContent(new Label());
        layout.addComponent(viewDisplayPanel);

        navigator.init(this, view -> { });

        createNavBtn(layout, VIEWNAME_NAV_BTN_ID, BYVIEWNAME);
        createNavBtn(layout, P1VIEWNAME_NAV_BTN_ID, BYVIEWNAME + "/p1");
        createNavBtn(layout, P2VIEWNAME_NAV_BTN_ID, BYVIEWNAME + "/p2");

        createNavBtn(layout, VIEWNAMEPARAMS_NAV_BTN_ID, BYVIEWNAMEPARAMS);
        createNavBtn(layout, P1VIEWNAMEPARAMS_NAV_BTN_ID, BYVIEWNAMEPARAMS + "/p1");
        createNavBtn(layout, P2VIEWNAMEPARAMS_NAV_BTN_ID, BYVIEWNAMEPARAMS + "/p2");

        createNavBtn(layout, ALWAYS_NAV_BTN_ID, BYALWAYS);
        createNavBtn(layout, P1ALWAYS_NAV_BTN_ID, BYALWAYS + "/p1");
        createNavBtn(layout, P2ALWAYS_NAV_BTN_ID, BYALWAYS + "/p2");

        createNavBtn(layout, OTHERVIEW_NAV_BTN_ID, OTHER);

        setContent(layout);
    }

    private void createNavBtn(VerticalLayout layout, String navBtnId, String targetView) {
        Button navigateBtn = new Button(navBtnId);
        navigateBtn.setId(navBtnId);
        navigateBtn.addClickListener(event -> {
            navigator.navigateTo(targetView);
            value.setValue(bean.getValue());
        });
        layout.addComponent(navigateBtn);
    }

    @CDIView("")
    public static class DefaultView implements View {
        @Override
        public void enter(ViewChangeListener.ViewChangeEvent event) {
        }
    }

    @CDIView(value = BYVIEWNAME, contextStrategy = ViewName.class)
    public static class ByViewNameView implements View {
        @Inject
        ViewScopedBean bean;
        @Inject
        Counter counter;
        public static String CONSTRUCT_COUNT = "viewnameconstructcount";
        public static String DESTROY_COUNT = "viewnamedestroycount";

        @PostConstruct
        private void init() {
            counter.increment(CONSTRUCT_COUNT);
        }

        @PreDestroy
        private void destroy() {
            counter.increment(DESTROY_COUNT);
        }

        @Override
        public void enter(ViewChangeListener.ViewChangeEvent event) {
            bean.logEnter(event);
        }
    }

    @CDIView(value = BYVIEWNAMEPARAMS, contextStrategy = ViewNameAndParameters.class)
    public static class ByViewNameAndParametersView implements View {
        @Inject
        ViewScopedBean bean;
        @Inject
        Counter counter;
        public static String CONSTRUCT_COUNT = "viewnameparamsconstructcount";
        public static String DESTROY_COUNT = "viewnameparamsdestroycount";

        @PostConstruct
        private void init() {
            counter.increment(CONSTRUCT_COUNT);
        }

        @PreDestroy
        private void destroy() {
            counter.increment(DESTROY_COUNT);
        }

        @Override
        public void enter(ViewChangeListener.ViewChangeEvent event) {
            bean.logEnter(event);
        }
    }

    @CDIView(value = BYALWAYS, contextStrategy = Always.class)
    public static class ByAlwaysView implements View {
        @Inject
        ViewScopedBean bean;
        @Inject
        Counter counter;
        public static String CONSTRUCT_COUNT = "alwaysconstructcount";
        public static String DESTROY_COUNT = "alwaysdestroycount";

        @PostConstruct
        private void init() {
            counter.increment(CONSTRUCT_COUNT);
        }

        @PreDestroy
        private void destroy() {
            counter.increment(DESTROY_COUNT);
        }

        @Override
        public void enter(ViewChangeListener.ViewChangeEvent event) {
            bean.logEnter(event);
        }
    }

    @CDIView(value = OTHER)
    public static class OtherView implements View {
        @Inject
        ViewScopedBean bean;
        @Inject
        Counter counter;
        public static String CONSTRUCT_COUNT = "otherconstructcount";
        public static String DESTROY_COUNT = "otherdestroycount";

        @PostConstruct
        private void init() {
            counter.increment(CONSTRUCT_COUNT);
        }

        @PreDestroy
        private void destroy() {
            counter.increment(DESTROY_COUNT);
        }

        @Override
        public void enter(ViewChangeListener.ViewChangeEvent event) {
            bean.logEnter(event);
        }
    }


    @NormalViewScoped
    public static class ViewScopedBean {
        @Inject
        Counter counter;
        public static String CONSTRUCT_COUNT = "beanconstructcount";
        public static String DESTROY_COUNT = "beandestroycount";

        @PostConstruct
        private void init() {
            counter.increment(CONSTRUCT_COUNT);
        }

        @PreDestroy
        private void destroy() {
            counter.increment(DESTROY_COUNT);
        }

        private String value = "";

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public void logEnter(ViewChangeListener.ViewChangeEvent event) {
            setValue(value + "," + event.getViewName() + ":" + event.getParameters());
        }
    }


}
