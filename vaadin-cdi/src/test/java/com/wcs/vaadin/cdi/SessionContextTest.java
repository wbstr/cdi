package com.wcs.vaadin.cdi;

import com.wcs.vaadin.cdi.internal.Conventions;
import com.wcs.vaadin.cdi.uis.SessionUI;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class SessionContextTest extends AbstractManagedCDIIntegrationTest {
    @Deployment(testable = false)
    public static WebArchive deployment() {
        return ArchiveProvider.createWebArchive("sessionScope",
                SessionUI.class);
    }

    @Before
    public void setUp() throws IOException {
        resetCounts();
        String uri = Conventions.deriveMappingForUI(SessionUI.class);
        openWindow(uri);
    }

    @Test
    public void testUIsAccessSameSession() throws Exception {
        Assert.assertEquals("", findElement(SessionUI.VALUELABEL_ID).getText());
        clickAndWait(SessionUI.SETVALUEBTN_ID);
        refreshWindow();//creates new UI
        Assert.assertEquals(SessionUI.VALUE, findElement(SessionUI.VALUELABEL_ID).getText());
    }

    @Test
    public void testVaadinSessionCloseDestroysSessionContext() throws Exception {
        Assert.assertEquals(0, getCount(SessionUI.SessionScopedBean.DESTROY_COUNT));
        clickAndWait(SessionUI.INVALIDATEBTN_ID);
        Assert.assertEquals(1, getCount(SessionUI.SessionScopedBean.DESTROY_COUNT));
    }

    @Test
    public void testHttpSessionCloseDestroysSessionContext() throws Exception {
        Assert.assertEquals(0, getCount(SessionUI.SessionScopedBean.DESTROY_COUNT));
        clickAndWait(SessionUI.HTTP_INVALIDATEBTN_ID);
        Assert.assertEquals(1, getCount(SessionUI.SessionScopedBean.DESTROY_COUNT));
    }

    @Test
    public void testHttpSessionExpirationDestroysSessionContext() throws Exception {
        Assert.assertEquals(0, getCount(SessionUI.SessionScopedBean.DESTROY_COUNT));
        clickAndWait(SessionUI.EXPIREBTN_ID);
        boolean destroyed = false;
        for (int i=0; i<60; i++) {
            Thread.sleep(1000);
            if (getCount(SessionUI.SessionScopedBean.DESTROY_COUNT) > 0) {
                System.out.printf("session expired after %d seconds\n", i);
                destroyed = true;
                break;
            }
        }
        Assert.assertTrue(destroyed);
    }
}
