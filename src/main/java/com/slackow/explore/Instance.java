package com.slackow.explore;

public class Instance {
    private final String name;
    private final String path;
    private final String group;
    private final int worldCount;
    private final boolean isThisOne;

    public Instance(String name, String path, String group, int worldCount, boolean isThisOne) {
        this.name = name;
        this.path = path;
        this.group = group == null ? "N/A" : group;
        this.worldCount = worldCount;
        this.isThisOne = isThisOne;
    }

    public String getPath() {
        return path;
    }

    public String getName() {
        return name;
    }

    public String getGroup() {
        return group;
    }

    public int getWorldCount() {
        return worldCount;
    }

    public boolean isThisOne() {
        return isThisOne;
    }
}
