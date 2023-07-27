package com.project.winter.config;

import java.util.ArrayList;
import java.util.List;

public class WebMvcConfigurationSupport implements WebMvcConfigurer {

    private final List<WebMvcConfigurer> configurers = new ArrayList<>();

    public void addWebMvcConfigurers(List<WebMvcConfigurer> configurers) {
        if (!configurers.isEmpty()) {
            this.configurers.addAll(configurers);
        }
    }

    public void loadConfigurer() {

    }

}
