package br.com.jefersonalmeida.stack;

import software.amazon.awscdk.core.Construct;
import software.amazon.awscdk.core.Stack;
import software.amazon.awscdk.core.StackProps;
import software.amazon.awscdk.services.ec2.Vpc;

public class AwsCdkVpcStack extends Stack {
    public AwsCdkVpcStack(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public AwsCdkVpcStack(final Construct scope, final String id, final StackProps props) {

        super(scope, id, props);

        Vpc.Builder.create(this, "Vpc01").maxAzs(3).build();
    }
}
