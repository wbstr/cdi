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

import com.wcs.vaadin.cdi.uis.ViewStrategyUI;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;

public class ViewStrategyViewNameAndParamsTest extends AbstractViewStrategyTest {

    @Deployment(testable = false)
    public static WebArchive deployment() {
        return ArchiveProvider.createWebArchive("viewStrategyViewName", ViewStrategyUI.class);
    }

    @Test
    public void testNavigationToSameViewAndParametersNop() throws Exception {
        assertNop(
                ViewStrategyUI.P1VIEWNAMEPARAMS_NAV_BTN_ID,
                ViewStrategyUI.P1VIEWNAMEPARAMS_NAV_BTN_ID,
                ",byviewnameparams:p1"
        );
    }

    @Test
    public void testNavigationToSameViewNoParametersNop() throws Exception {
        assertNop(
                ViewStrategyUI.VIEWNAMEPARAMS_NAV_BTN_ID,
                ViewStrategyUI.VIEWNAMEPARAMS_NAV_BTN_ID,
                ",byviewnameparams:"
        );
    }

    @Test
    public void testNavigationToSameViewDifferentParametersCreatesNewContext() throws Exception {
        assertToTestedViewContextCreated(
                ViewStrategyUI.P1VIEWNAMEPARAMS_NAV_BTN_ID,
                ViewStrategyUI.P2VIEWNAMEPARAMS_NAV_BTN_ID,
                ",byviewnameparams:p1",
                ",byviewnameparams:p2"
        );
    }

    @Test
    public void testNavigationToOtherViewCreatesNewContext() throws Exception {
        assertToOtherViewContextCreated(
                ViewStrategyUI.VIEWNAMEPARAMS_NAV_BTN_ID,
                ",byviewnameparams:"
        );
    }

    @Override
    protected String getTestedViewDestroyCounter() {
        return ViewStrategyUI.ByViewNameAndParametersView.DESTROY_COUNT;
    }

    @Override
    protected String getTestedViewContructCounter() {
        return ViewStrategyUI.ByViewNameAndParametersView.CONSTRUCT_COUNT;
    }
}
