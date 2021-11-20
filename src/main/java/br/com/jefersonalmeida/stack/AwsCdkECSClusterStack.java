package br.com.jefersonalmeida.stack;

import software.amazon.awscdk.core.Construct;
import software.amazon.awscdk.core.Stack;
import software.amazon.awscdk.core.StackProps;
import software.amazon.awscdk.services.ec2.Vpc;
import software.amazon.awscdk.services.ecs.Cluster;

public class AwsCdkECSClusterStack extends Stack {
    public AwsCdkECSClusterStack(final Construct scope, final String id, final Vpc vpc) {
        this(scope, id, vpc, null);
    }

    public AwsCdkECSClusterStack(final Construct scope, final String id, final Vpc vpc, final StackProps props) {

        super(scope, id, props);

        Cluster.Builder.create(this, id)
                .clusterName("Cluster01")
                .vpc(vpc)
                .build();
    }
}
