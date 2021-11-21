package br.com.jefersonalmeida;

import br.com.jefersonalmeida.config.EnvConfig;
import br.com.jefersonalmeida.stack.ClusterStack;
import br.com.jefersonalmeida.stack.RdsMySQLStack;
import br.com.jefersonalmeida.stack.Service01Stack;
import br.com.jefersonalmeida.stack.VpcStack;
import software.amazon.awscdk.core.App;
import software.amazon.awscdk.core.StackProps;

import java.util.HashMap;
import java.util.Map;

public class AwsCdkApp {
    public static void main(final String[] args) {
        App app = new App();

        Map<String, String> tags = new HashMap<>();
        tags.put("resource", "course-spring");
        tags.put("origin", "aws-cdk");

        VpcStack vpcStack = new VpcStack(
                app,
                "VpcStack",
                StackProps.builder()
                        .env(EnvConfig.getEnvironment())
                        .description("VpcStack criada a partir do curso de Spring")
                        .tags(tags)
                        .build()
        );

        ClusterStack clusterStack = new ClusterStack(
                app,
                "ClusterStack",
                vpcStack.getVpc(),
                StackProps.builder()
                        .env(EnvConfig.getEnvironment())
                        .description("ClusterStack criada a partir do curso de Spring")
                        .tags(tags)
                        .build()
        );
        clusterStack.addDependency(vpcStack);

        RdsMySQLStack rdsMySQLStack = new RdsMySQLStack(
                app,
                "RdsMySQLStack",
                vpcStack.getVpc(),
                StackProps.builder()
                        .env(EnvConfig.getEnvironment())
                        .description("RdsMySQLStack criada a partir do curso de Spring")
                        .tags(tags)
                        .build()
        );
        rdsMySQLStack.addDependency(vpcStack);

        Service01Stack service01Stack = new Service01Stack(
                app,
                "Service01Stack",
                clusterStack.getCluster(),
                StackProps.builder()
                        .env(EnvConfig.getEnvironment())
                        .description("Service01Stack criada a partir do curso de Spring")
                        .tags(tags)
                        .build()
        );
        service01Stack.addDependency(clusterStack);
        service01Stack.addDependency(rdsMySQLStack);

        app.synth();
    }
}
