package com;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.apache.http.HttpStatus;

public class Mock {

    private WireMockServer wiremockServerItem = new WireMockServer();

    public void initAndWireStubs() {
        wiremockServerItem = new WireMockServer(WireMockConfiguration.wireMockConfig().port(8083));
        wiremockServerItem.start();
        stubCreateCountry();
    }

    public void stop() {
        wiremockServerItem.stop();
    }

    public void stubCreateCountry() {
        wiremockServerItem.stubFor(post(
                urlEqualTo("/api/create/country"))
                .withHeader("Content-Type", equalTo("application/json; charset=UTF-8"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.SC_OK)
                ));
    }

}
