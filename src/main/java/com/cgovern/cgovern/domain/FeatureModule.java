package com.cgovern.cgovern.domain;

public enum FeatureModule {
    CI("ci", "Continuous Integration", "Build, lint and package on every change."),
    CD("cd", "Continuous Deployment", "Promote artifacts through environments."),
    CT("ct", "Continuous Testing", "Regression, contract and performance suites."),
    CM("cm", "Continuous Monitoring", "Health, SLOs and alert routing."),
    AGENTIC("agentic", "Agentic Monitoring", "Autonomous agents that watch, diagnose and recommend.");

    private final String slug;
    private final String title;
    private final String summary;

    FeatureModule(String slug, String title, String summary) {
        this.slug = slug;
        this.title = title;
        this.summary = summary;
    }

    public String getSlug() {
        return slug;
    }

    public String getTitle() {
        return title;
    }

    public String getSummary() {
        return summary;
    }

    public static FeatureModule fromSlug(String slug) {
        for (FeatureModule module : values()) {
            if (module.slug.equalsIgnoreCase(slug)) {
                return module;
            }
        }
        throw new IllegalArgumentException("Unknown feature: " + slug);
    }
}
