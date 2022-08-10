package com.slackow.explore;

public class Instance {
    private final String name;
    private final String path;
    private final String group;
    private final int itemCount;
    private final boolean isThisOne;

    public Instance(String name, String path, String group, int itemCount, boolean isThisOne) {
        this.name = name;
        this.path = path;
        this.group = group == null ? "N/A" : group;
        this.itemCount = itemCount;
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

    public int getItemCount() {
        return itemCount;
    }

    public boolean isThisOne() {
        return isThisOne;
    }
}
