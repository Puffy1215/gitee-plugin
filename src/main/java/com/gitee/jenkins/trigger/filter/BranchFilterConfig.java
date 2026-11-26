package com.gitee.jenkins.trigger.filter;

/**
 * @author Robin Müller
 */
public final class BranchFilterConfig {

    private final FilterType type;
    private final String includeBranchesSpec;
    private final String excludeBranchesSpec;
    private final String targetBranchRegex;

    private BranchFilterConfig(FilterType type, String includeBranchesSpec, String excludeBranchesSpec, String targetBranchRegex) {
        this.type = type;
        this.includeBranchesSpec = includeBranchesSpec;
        this.excludeBranchesSpec = excludeBranchesSpec;
        this.targetBranchRegex = targetBranchRegex;
    }

    public FilterType getType() {
        return type;
    }

    String getIncludeBranchesSpec() {
        return includeBranchesSpec;
    }

    String getExcludeBranchesSpec() {
        return excludeBranchesSpec;
    }

    String getTargetBranchRegex() {
        return targetBranchRegex;
    }

    public static class BranchFilterConfigBuilder {
        private String includeBranchesSpec;
        private String excludeBranchesSpec;
        private String targetBranchRegex;

        public static BranchFilterConfigBuilder branchFilterConfig() {
            return new BranchFilterConfigBuilder();
        }

        public BranchFilterConfigBuilder withIncludeBranchesSpec(String includeBranchesSpec) {
            this.includeBranchesSpec = includeBranchesSpec;
            return this;
        }

        public BranchFilterConfigBuilder withExcludeBranchesSpec(String excludeBranchesSpec) {
            this.excludeBranchesSpec = excludeBranchesSpec;
            return this;
        }

        public BranchFilterConfigBuilder withTargetBranchRegex(String targetBranchRegex) {
            this.targetBranchRegex = targetBranchRegex;
            return this;
        }

        public BranchFilterConfig build(FilterType type) {
            return new BranchFilterConfig(type, includeBranchesSpec, excludeBranchesSpec, targetBranchRegex);
        }
    }
}
