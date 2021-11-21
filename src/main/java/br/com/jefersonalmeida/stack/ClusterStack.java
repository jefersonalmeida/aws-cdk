package br.com.jefersonalmeida.stack;

import software.amazon.awscdk.core.Construct;
import software.amazon.awscdk.core.Stack;
import software.amazon.awscdk.core.StackProps;
import software.amazon.awscdk.services.ec2.Vpc;
import software.amazon.awscdk.services.ecs.Cluster;

public class ClusterStack extends Stack {

    private final Cluster cluster;

    public ClusterStack(final Construct scope, final String id, final Vpc vpc) {
        this(scope, id, vpc, null);
    }

    public ClusterStack(final Construct scope, final String id, final Vpc vpc, final StackProps props) {
        super(scope, id, props);

        cluster = Cluster.Builder.create(this, id).clusterName("cluster-01").vpc(vpc).build();
    }

    public Cluster getCluster() {
        return cluster;
    }
}
