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

package com.wcs.vaadin.cdi;

import com.wcs.vaadin.cdi.internal.Conventions;
import com.wcs.vaadin.cdi.uis.ViewStrategyUI;
import org.junit.Before;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

abstract class AbstractViewStrategyTest extends AbstractManagedCDIIntegrationTest {

    @Before
    public void setUp() throws Exception {
        String viewUri = Conventions.deriveMappingForUI(ViewStrategyUI.class);
        openWindow(viewUri);
        resetCounts();
    }


    protected void assertDestroyCounts(int count) throws IOException {
        assertViewDestroyCount(count);
        assertBeanDestroyCount(count);
    }

    protected void assertConstructCounts(int count) throws IOException {
        assertViewConstructCount(count);
        assertBeanConstructCount(count);
    }


    protected void assertViewDestroyCount(int count) throws IOException {
        assertThat(getCount(getTestedViewDestroyCounter()), is(count));
    }

    protected void assertBeanDestroyCount(int count) throws IOException {
        assertThat(getCount(ViewStrategyUI.ViewScopedBean.DESTROY_COUNT), is(count));
    }

    protected abstract String getTestedViewDestroyCounter();

    protected abstract String getTestedViewContructCounter();

    protected void assertBeanConstructCount(int count) throws IOException {
        assertThat(getCount(ViewStrategyUI.ViewScopedBean.CONSTRUCT_COUNT), is(count));
    }

    protected void assertViewConstructCount(int count) throws IOException {
        assertThat(getCount(getTestedViewContructCounter()), is(count));
    }

    protected void assertBeanValue(String expectedValue) {
        String value = findElement(ViewStrategyUI.VALUE_LABEL_ID).getText();
        assertEquals(expectedValue, value);
    }

    protected void assertNop(String btnSourceView, String btnTargetView, String beanValue)
            throws Exception {
        clickAndWait(btnSourceView);
        assertConstructCounts(1);
        assertDestroyCounts(0);
        assertBeanValue(beanValue);

        clickAndWait(btnTargetView);
        // context hold
        assertConstructCounts(1);
        assertDestroyCounts(0);
        // no navigation happened - init not called again
        assertBeanValue(beanValue);
    }

    protected void assertToOtherViewContextCreated(String btnSourceView, String srcBeanValue)
            throws Exception {
        clickAndWait(btnSourceView);
        assertConstructCounts(1);
        assertDestroyCounts(0);
        assertThat(getCount(ViewStrategyUI.OtherView.CONSTRUCT_COUNT), is(0));
        assertBeanValue(srcBeanValue);

        clickAndWait(ViewStrategyUI.OTHERVIEW_NAV_BTN_ID);
        assertViewConstructCount(1);
        assertBeanConstructCount(2);
        assertDestroyCounts(1);
        assertThat(getCount(ViewStrategyUI.OtherView.CONSTRUCT_COUNT), is(1));
        assertBeanValue(",other:");
    }

    protected void assertToTestedViewContextCreated(
            String btnSourceView, String btnTargetView, String srcBeanValue, String targetBeanValue)
            throws Exception {
        clickAndWait(btnSourceView);
        assertConstructCounts(1);
        assertDestroyCounts(0);
        assertBeanValue(srcBeanValue);

        clickAndWait(btnTargetView);
        assertViewConstructCount(2);
        assertBeanConstructCount(2);
        assertDestroyCounts(1);
        assertBeanValue(targetBeanValue);
    }
}
