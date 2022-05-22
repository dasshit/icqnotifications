package io.jenkins.plugins.icqnotifications;

import hudson.Extension;
import hudson.ExtensionList;
import hudson.util.FormValidation;
import jenkins.model.GlobalConfiguration;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.QueryParameter;

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

    public FormValidation doCheck(@QueryParameter String value) {
        if (StringUtils.isEmpty(value)) {
            return FormValidation.warning("Please specify a label.");
        }
        return FormValidation.ok();
    }

}
