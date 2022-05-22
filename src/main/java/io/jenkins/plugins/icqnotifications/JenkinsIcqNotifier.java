package io.jenkins.plugins.icqnotifications;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import hudson.Launcher;
import hudson.model.*;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Notifier;
import hudson.tasks.Publisher;
import hudson.util.FormValidation;
import io.jenkins.plugins.icqnotifications.net.CustomHttpClientBuilder;
import io.jenkins.plugins.icqnotifications.utils.IcqBaseButton;
import io.jenkins.plugins.icqnotifications.utils.IcqKeyBoard;
import io.jenkins.plugins.icqnotifications.utils.IcqUrlButton;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.QueryParameter;

import javax.servlet.ServletException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Objects;


public class JenkinsIcqNotifier extends Notifier {

    private String CHAT_ID;
    private String MESSAGE;

    @DataBoundConstructor
    public JenkinsIcqNotifier(String CHAT_ID, String MESSAGE) {

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
    public boolean perform(
            final AbstractBuild build,
            final Launcher launcher, final BuildListener listener) throws IOException{
        // logic to be executed by plugin

        try (CloseableHttpClient httpClient = new CustomHttpClientBuilder().build()) {

            String msgUrl = JenkinsIcqNotificationsConfiguration.get().getBotApiUrl() + "/messages/sendText";

            HttpGet request = new HttpGet(msgUrl);

            IcqKeyBoard keyboard = new IcqKeyBoard();

            ArrayList<IcqBaseButton> buttonsRow = new ArrayList<IcqBaseButton>();

            buttonsRow.add(
                    new IcqUrlButton()
                            .setText("Build URL")
                            .setUrl(build.getUrl())
            );

            final String finalMessageText = MESSAGE + "\n\nResult: " + Objects.requireNonNull(build.getResult());

            keyboard.addButtonsRow(buttonsRow);

            URI uri = new URIBuilder(request.getURI())
                    .addParameter("token", JenkinsIcqNotificationsConfiguration.get().getBotToken())
                    .addParameter("chatId", CHAT_ID)
                    .addParameter("text", finalMessageText)
                    .addParameter("parseMode", JenkinsIcqNotificationsConfiguration.get().getParseMode())
                    .addParameter("inlineKeyboardMarkup", keyboard.toString())
                    .build();

            request.setURI(uri);

            listener.getLogger().println(uri.toString());

            CloseableHttpResponse response = httpClient.execute(request);

            String result = EntityUtils.toString(response.getEntity());

            listener.getLogger().println(result);

        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        return true;
    }

    @Extension
    public static class DescriptorImpl extends BuildStepDescriptor<Publisher> {

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
