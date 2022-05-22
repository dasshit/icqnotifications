package io.jenkins.plugins.icqnotifications;

import hudson.Extension;
import hudson.ExtensionList;
import hudson.util.FormValidation;
import jenkins.model.GlobalConfiguration;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.QueryParameter;

import javax.security.auth.login.Configuration;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Example of Jenkins global configuration.
 */
@Extension
public class JenkinsIcqNotificationsConfiguration extends GlobalConfiguration {

    /** @return the singleton instance */
    public static JenkinsIcqNotificationsConfiguration get() {
        return ExtensionList.lookupSingleton(JenkinsIcqNotificationsConfiguration.class);
    }
    final public static String PLUGIN_DISPLAY_NAME = "ICQ/VK Teams Notifications";

    private String botToken;

    private String botApiUrl = "https://api.icq.net/bot/v1";
    private String parseMode = "HTML";

    public JenkinsIcqNotificationsConfiguration() {
        // When Jenkins is restarted, load any saved configuration from disk.
        load();
    }

    /** @return the currently configured botToken, if any */
    public String getBotToken() {
        return botToken;
    }

    /**
     * Together with {@link #getBotToken}, binds to entry in {@code config.jelly}.
     * @param botToken the new value of this field
     */
    @DataBoundSetter
    public void setBotToken(String botToken) {
        this.botToken = botToken;
        save();
    }

    /** @return the currently configured botToken, if any */
    public String getBotApiUrl() {
        return botApiUrl;
    }

    /**
     * Together with {@link #getBotToken}, binds to entry in {@code config.jelly}.
     * @param botApiUrl the new value of this field
     */
    @DataBoundSetter
    public void setBotApiUrl(String botApiUrl) {
        this.botApiUrl = botApiUrl;
        save();
    }

    /** @return the currently configured botToken, if any */
    public String getParseMode() {
        return parseMode;
    }

    /**
     * Together with {@link #getBotToken}, binds to entry in {@code config.jelly}.
     * @param parseMode the new value of this field
     */
    @DataBoundSetter
    public void setParseMode(String parseMode) {
        this.parseMode = parseMode;
        save();
    }

    public FormValidation doTestConnection(@QueryParameter String botToken, @QueryParameter String botApiUrl) throws IOException {

        try (CloseableHttpClient httpClient = HttpClients.custom()
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
                .build()) {

            String msgUrl = botApiUrl + "/self/get";

            HttpGet request = new HttpGet(msgUrl);

            URI uri = new URIBuilder(request.getURI())
                    .addParameter("token", botToken)
                    .build();

            request.setURI(uri);

            CloseableHttpResponse response = httpClient.execute(request);

            final int responseStatusCode = response.getStatusLine().getStatusCode();

            int[] errorStatusCodeList = {400, 401, 403, 404, 500, 504};

            if (ArrayUtils.contains(errorStatusCodeList, responseStatusCode)) {
                return FormValidation.error(response.toString());
            }

            return FormValidation.ok(response.toString());

        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

    }

}
