package io.jenkins.plugins.icqnotifications.net;
import org.apache.commons.lang.ArrayUtils;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;

public class CustomHttpClientBuilder {

    public CustomHttpClientBuilder() {

    }

    public CloseableHttpClient build() {
        return HttpClients.custom()
                .addInterceptorLast(
                        (HttpResponseInterceptor) (response, context) -> {

                            final int responseStatusCode = response.getStatusLine().getStatusCode();

                            int[] errorStatusCodeList = {400, 401, 403, 404, 500, 504};

                            if (ArrayUtils.contains(errorStatusCodeList, responseStatusCode)) {

                                throw new IOException("Retrying request, current status code: " + responseStatusCode);
                            }

                        })
                .setRetryHandler(
                        (exception, executionCount, context) -> executionCount < 5)
                .build();
    }

}
