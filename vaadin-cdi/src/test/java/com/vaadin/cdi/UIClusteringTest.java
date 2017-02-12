package com.vaadin.cdi;

import com.vaadin.cdi.internal.Conventions;
import com.vaadin.cdi.uis.SessionReplicationUI;
import com.vaadin.cdi.uis.UIScopedCounterUI;
import io.undertow.Undertow;
import io.undertow.server.handlers.ResponseCodeHandler;
import io.undertow.server.handlers.proxy.LoadBalancingProxyClient;
import io.undertow.server.handlers.proxy.ProxyHandler;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.container.test.api.TargetsContainer;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.descriptor.api.Descriptors;
import org.jboss.shrinkwrap.descriptor.api.webapp31.WebAppDescriptor;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import static org.junit.Assert.assertEquals;

@Category(ClusterTestCategory.class)
public class UIClusteringTest extends AbstractCDIIntegrationTest {

    private static final int PROXY_PORT = 58080;

    @Deployment(name = "node1", testable = false)
    @TargetsContainer("node-1")
    public static WebArchive deployment1() {
        return getArchive();
    }

    @Deployment(name = "node2", testable = false)
    @TargetsContainer("node-2")
    public static WebArchive deployment2() {
        return getArchive();
    }

    @OperateOnDeployment("node1")
    @ArquillianResource
    private URL url1;

    @OperateOnDeployment("node2")
    @ArquillianResource
    private URL url2;

    public static WebArchive getArchive() {
        WebArchive archive = ArchiveProvider.createWebArchive("uiscopedcounter",
                UIScopedCounterUI.class);
        WebAppDescriptor webAppDescriptor = Descriptors.create(WebAppDescriptor.class).distributable();
        archive.addAsWebInfResource(new StringAsset(webAppDescriptor.exportAsString()),
                webAppDescriptor.getDescriptorName());
        return archive;
    }

    @Test
    public void testUICounter() throws Exception {
        createReverseProxy();
        String proxyUrl = "http://localhost:" + PROXY_PORT + url1.getPath();

        String path = Conventions.deriveMappingForUI(UIScopedCounterUI.class);
        firstWindow.navigate().to(proxyUrl + path);
        waitForClient();

        String[] portsArr = new String[]{String.valueOf(url1.getPort()), String.valueOf(url2.getPort())};
        for (int i = 1; i < 10; i++) {
            clickAndWait(UIScopedCounterUI.INC_BUTTON_ID);
            assertEquals(String.valueOf(i), findElement(UIScopedCounterUI.NORMALVALUE_LABEL_ID).getText());
            assertEquals(String.valueOf(i), findElement(UIScopedCounterUI.VALUE_LABEL_ID).getText());
            assertEquals(portsArr[(i - 1) % 2], findElement(UIScopedCounterUI.PORT_LABEL_ID).getText());
        }
    }

    private void createReverseProxy() throws URISyntaxException, MalformedURLException {
        LoadBalancingProxyClient loadBalancer = new LoadBalancingProxyClient()
                .addHost(new URL(url1.getProtocol(), url1.getHost(), url1.getPort(), "").toURI())
                .addHost(new URL(url2.getProtocol(), url2.getHost(), url2.getPort(), "").toURI());
        Undertow reverseProxy = Undertow.builder()
                .addHttpListener(PROXY_PORT, "localhost")
                .setHandler(new ProxyHandler(loadBalancer, ResponseCodeHandler.HANDLE_404))
                .build();
        reverseProxy.start();
    }
}
