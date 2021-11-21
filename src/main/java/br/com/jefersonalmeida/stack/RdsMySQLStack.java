package br.com.jefersonalmeida.stack;

import software.amazon.awscdk.core.*;
import software.amazon.awscdk.services.ec2.*;
import software.amazon.awscdk.services.rds.*;

import java.util.Collections;

public class RdsMySQLStack extends Stack {

    private final DatabaseInstance databaseInstance;

    public RdsMySQLStack(final Construct scope, final String id, final Vpc vpc) {
        this(scope, id, vpc, null);
    }

    public RdsMySQLStack(final Construct scope, final String id, final Vpc vpc, final StackProps props) {
        super(scope, id, props);

        CfnParameter databasePassword = CfnParameter.Builder
                .create(this, "databasePassword")
                .type("String")
                .description("Senha para instância RDS")
                .build();

        ISecurityGroup securityGroup = SecurityGroup.fromSecurityGroupId(this, id, vpc.getVpcDefaultSecurityGroup());
        securityGroup.addIngressRule(Peer.anyIpv4(), Port.tcp(3386));

        Credentials credentials = Credentials.fromUsername("admin", CredentialsFromUsernameOptions.builder()
                .password(SecretValue.plainText(databasePassword.getValueAsString()))
                .build()
        );

        IInstanceEngine instanceEngine = DatabaseInstanceEngine.mysql(MySqlInstanceEngineProps.builder()
                .version(MysqlEngineVersion.VER_8_0_26)
                .build()
        );

        InstanceType instanceType = InstanceType.of(InstanceClass.BURSTABLE2, InstanceSize.MICRO);

        SubnetSelection subnetSelection = SubnetSelection.builder().subnets(vpc.getPrivateSubnets()).build();

        databaseInstance = DatabaseInstance.Builder
                .create(this, id.concat("Rds01"))
                .instanceIdentifier("aws-service-01-db")
                .engine(instanceEngine)
                .vpc(vpc)
                .credentials(credentials)
                .instanceType(instanceType)
                .multiAz(false)
                .allocatedStorage(10)
                .securityGroups(Collections.singletonList(securityGroup))
                .vpcSubnets(subnetSelection)
                .build();

        CfnOutput.Builder.create(this, "rds-endpoint")
                .exportName("rds-endpoint")
                .value(databaseInstance.getDbInstanceEndpointAddress())
                .build();

        CfnOutput.Builder.create(this, "rds-password")
                .exportName("rds-password")
                .value(databasePassword.getValueAsString())
                .build();
    }

    public DatabaseInstance getDatabaseInstance() {
        return databaseInstance;
    }
}
