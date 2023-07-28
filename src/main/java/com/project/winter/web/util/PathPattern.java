package com.project.winter.web.util;

public class PathPattern implements Comparable<PathPattern> {

    private final String pattern;

    private final int order;

    public PathPattern(String pattern, int order) {
        this.pattern = pattern;
        this.order = order;
    }

    public int getOrder() {
        return order;
    }

    public boolean match(String pattern) {
        return (this.pattern.equals("*") || this.pattern.equals(pattern));
    }

    @Override
    public int compareTo(PathPattern o) {
        return this.order - o.order;
    }

    public static String[] getPatterns(String path) {
        if (path.startsWith("/")) path = path.substring(1);

        return path.split("/");
    }

}
