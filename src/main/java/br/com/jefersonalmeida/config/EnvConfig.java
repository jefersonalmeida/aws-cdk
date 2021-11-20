package br.com.jefersonalmeida.config;

import software.amazon.awscdk.core.Environment;

public class EnvConfig {

    public static Environment getEnvironment() {
        return Environment.builder().account("710619037984").region("sa-east-1").build();
    }
}
