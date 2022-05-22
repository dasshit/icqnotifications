package io.jenkins.plugins.icqnotifications;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.EnvVars;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractProject;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import hudson.util.FormValidation;
import jenkins.tasks.SimpleBuildStep;
import org.apache.commons.lang.ArrayUtils;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.QueryParameter;

import javax.servlet.ServletException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;


public class JenkinsIcqBuilder extends Builder implements SimpleBuildStep {

    private String CHAT_ID;
    private String MESSAGE;

    @DataBoundConstructor
    public JenkinsIcqBuilder(String CHAT_ID, String MESSAGE) {

        this.CHAT_ID = CHAT_ID;
        this.MESSAGE = MESSAGE;

    }

    @DataBoundSetter
    public void setCHAT_ID(String CHAT_ID) { this.CHAT_ID = CHAT_ID; }
    public String getCHAT_ID() { return CHAT_ID; }

    @DataBoundSetter
    public void setMESSAGE(String MESSAGE) { this.MESSAGE = MESSAGE; }
    public String getMESSAGE() { return MESSAGE; }

    @Override
    public void perform(@NonNull Run<?, ?> run, @NonNull FilePath workspace, @NonNull EnvVars env, @NonNull Launcher launcher, TaskListener listener) throws InterruptedException, IOException {

        try (CloseableHttpClient httpClient = HttpClients.custom()
                .addInterceptorLast(
                        (HttpResponseInterceptor) (response, context) -> {

                    final int responseStatusCode = response.getStatusLine().getStatusCode();

                    int[] errorStatusCodeList = {400, 401, 403, 404, 500, 504};

                    if (ArrayUtils.contains(errorStatusCodeList, responseStatusCode)) {

                        listener.getLogger().println("Retrying request, current status code: " + responseStatusCode);

                        throw new IOException("Retrying request, current status code: " + responseStatusCode);
                    }

                })
                .setRetryHandler(
                        (exception, executionCount, context) -> executionCount < 5)
                .build()) {

            String msgUrl = JenkinsIcqNotificationsConfiguration.get().getBotApiUrl() + "/messages/sendText";

            HttpGet request = new HttpGet(msgUrl);

            URI uri = new URIBuilder(request.getURI())
                    .addParameter("token", JenkinsIcqNotificationsConfiguration.get().getBotToken())
                    .addParameter("chatId", CHAT_ID)
                    .addParameter("text", MESSAGE)
                    .addParameter("parseMode", JenkinsIcqNotificationsConfiguration.get().getParseMode())
                    .build();

            request.setURI(uri);

            listener.getLogger().println(uri.toString());

            CloseableHttpResponse response = httpClient.execute(request);

            String result = EntityUtils.toString(response.getEntity());

            listener.getLogger().println(result);

        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

    }

    @Symbol("imSendMessage")
    @Extension
    public static class DescriptorImpl extends BuildStepDescriptor<Builder> {

        public FormValidation doCheckForm(@QueryParameter String CHAT_ID, @QueryParameter String MESSAGE)
                throws IOException, ServletException {
            if (CHAT_ID.length() == 0)
                return FormValidation.error("Empty CHAT_ID is not possible");
            if (MESSAGE.length() == 0)
                return FormValidation.error("Empty MESSAGE is not possible");
            return FormValidation.ok();
        }

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }

        @NonNull
        @Override
        public String getDisplayName() {
            return io.jenkins.plugins.icqnotifications.JenkinsIcqNotificationsConfiguration.PLUGIN_DISPLAY_NAME;
        }
    }

}
