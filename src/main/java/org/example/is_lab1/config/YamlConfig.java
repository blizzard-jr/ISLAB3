package org.example.is_lab1.config;

import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;


public final class YamlConfig {

    private static final YAMLMapper YAML_MAPPER;

    static {
        YAML_MAPPER = new YAMLMapper();
        YAML_MAPPER.findAndRegisterModules();
    }

    private YamlConfig() {
        // Utility class
    }


    public static YAMLMapper getYamlMapper() {
        return YAML_MAPPER;
    }
}


