package com.wcs.vaadin.cdi;

import com.wcs.vaadin.cdi.internal.Conventions;
import com.wcs.vaadin.cdi.uis.ViewStrategyInitUI;
import com.wcs.vaadin.cdi.uis.ViewStrategyUI;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ViewStrategiesUiInitTest extends AbstractManagedCDIIntegrationTest {

    @Deployment(testable = false)
    public static WebArchive deployment() {
        return ArchiveProvider.createWebArchive("viewStrategiesUiInit", ViewStrategyInitUI.class);
    }

    @Before
    public void setUp() throws Exception {
        String viewUri = Conventions.deriveMappingForUI(ViewStrategyUI.class)+"#!home/p1";
        openWindow(viewUri);
    }

    @Test
    public void testViewNameStrategyUpAfterUiInit() throws Exception {
        clickAndWait(ViewStrategyInitUI.VIEWNAME_BTN_ID);
        final String result = findElement(ViewStrategyInitUI.OUTPUT_ID).getText();
        Assert.assertEquals("true", result);
    }

    @Test
    public void testViewNameAndParametersStrategyUpAfterUiInit() throws Exception {
        clickAndWait(ViewStrategyInitUI.VIEWNAMEPARAMS_BTN_ID);
        final String result = findElement(ViewStrategyInitUI.OUTPUT_ID).getText();
        Assert.assertEquals("true", result);
    }
}
