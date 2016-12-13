package com.vaadin.cdi;

import com.vaadin.cdi.internal.Conventions;
import com.vaadin.cdi.uis.ViewNavigationUI;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ViewNavigationTest extends AbstractManagedCDIIntegrationTest {

    @Deployment(testable = false)
    public static WebArchive deployment() {
        return ArchiveProvider.createWebArchive("viewNavigation", ViewNavigationUI.class);
    }

    @Test
    public void testRevertedNavigationRevertsViewScope() throws Exception {
        String uri = Conventions.deriveMappingForUI(ViewNavigationUI.class);
        openWindow(uri);

        clickAndWait(ViewNavigationUI.REVERTED_NAV_BTN_ID);

        String value = findElement(ViewNavigationUI.VALUE_LABEL_ID).getText();
        assertEquals(ViewNavigationUI.DEFAULTVIEW_VALUE, value);
    }
}
