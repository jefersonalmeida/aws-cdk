package br.com.jefersonalmeida;

import br.com.jefersonalmeida.config.EnvConfig;
import br.com.jefersonalmeida.stack.AwsCdkECSClusterStack;
import br.com.jefersonalmeida.stack.AwsCdkVpcStack;
import software.amazon.awscdk.core.App;
import software.amazon.awscdk.core.StackProps;

import java.util.HashMap;
import java.util.Map;

public class AwsCdkApp {
    public static void main(final String[] args) {
        App app = new App();
//        new AwsCdkStack(app, "AwsCdkStack", StackProps.builder()
//                /*
//                .env(Environment.builder()
//                        .account(System.getenv("CDK_DEFAULT_ACCOUNT"))
//                        .region(System.getenv("CDK_DEFAULT_REGION"))
//                        .build())
//                */
//                .env(Environment.builder()
//                        .account("710619037984")
//                        .region("sa-east-1")
//                        .build())
//                .build());
//
        Map<String, String> tags = new HashMap<>();
        tags.put("resource", "course-spring");
        tags.put("origin", "aws-cdk");

        AwsCdkVpcStack awsCdkVpcStack = new AwsCdkVpcStack(
                app,
                "AwsCdkVpcStack",
                StackProps.builder()
                        .env(EnvConfig.getEnvironment())
                        .description("AwsCdkVpcStack criada a partir do curso de Spring")
                        .tags(tags)
                        .build()
        );

        AwsCdkECSClusterStack awsCdkECSClusterStack = new AwsCdkECSClusterStack(
                app,
                "AwsCdkECSClusterStack",
                awsCdkVpcStack.getVpc(),
                StackProps.builder()
                        .env(EnvConfig.getEnvironment())
                        .description("AwsCdkECSClusterStack criada a partir do curso de Spring")
                        .tags(tags)
                        .build()
        );
        awsCdkECSClusterStack.addDependency(awsCdkVpcStack);

        app.synth();
    }
}
