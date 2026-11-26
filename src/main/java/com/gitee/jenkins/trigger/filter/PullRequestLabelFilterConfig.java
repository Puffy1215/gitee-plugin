package com.gitee.jenkins.trigger.filter;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

/**
 * @author Robin Müller
 */
public class PullRequestLabelFilterConfig {

    private FilterType type;
    private String include;
    private String exclude;
    private String regex;

    public PullRequestLabelFilterConfig(FilterType type, String include, String exclude, String Regex) {
        this.type = type;
        this.include = include;
        this.exclude = exclude;
        this.regex = regex;
    }

    @DataBoundConstructor
    public PullRequestLabelFilterConfig() { }

    public FilterType getType() {
         return type;
    }

    public String getInclude() {
        return include;
    }

    public String getExclude() {
        return exclude;
    }

    public String getRegex() {
        return regex;
    }

    @DataBoundSetter
    public void setInclude(String include) {
        this.include = include;
    }

    @DataBoundSetter
    public void setExclude(String exclude) {
        this.exclude = exclude;
    }

    @DataBoundSetter 
    public void setRegex(String regex) {
        this.regex = regex;
    }

    public static class PullRequestLabelFilterConfigBuilder {
        private String includeLabelSpec;
        private String excludeLabelSpec;
        private String regex;

        public static PullRequestLabelFilterConfigBuilder pullRequestFilterConfig() {
            return new PullRequestLabelFilterConfigBuilder();
        }

        public PullRequestLabelFilterConfigBuilder withIncludeBranchesSpec(String includeLabelSpec) {
            this.includeLabelSpec = includeLabelSpec;
            return this;
        }

        public PullRequestLabelFilterConfigBuilder withExcludeBranchesSpec(String excludeLabelSpec) {
            this.excludeLabelSpec = excludeLabelSpec;
            return this;
        }

        public PullRequestLabelFilterConfigBuilder withRegex(String regex) {
            this.regex = regex;
            return this;
        }

        public PullRequestLabelFilterConfig build(FilterType type) {
            return new PullRequestLabelFilterConfig(type, includeLabelSpec, excludeLabelSpec, regex);
        }
    }
}
