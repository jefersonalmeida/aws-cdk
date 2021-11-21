package br.com.jefersonalmeida.stack;

import software.amazon.awscdk.core.*;
import software.amazon.awscdk.services.applicationautoscaling.EnableScalingProps;
import software.amazon.awscdk.services.ecs.*;
import software.amazon.awscdk.services.ecs.patterns.ApplicationLoadBalancedFargateService;
import software.amazon.awscdk.services.ecs.patterns.ApplicationLoadBalancedTaskImageOptions;
import software.amazon.awscdk.services.elasticloadbalancingv2.HealthCheck;
import software.amazon.awscdk.services.logs.LogGroup;
import software.amazon.awscdk.services.logs.RetentionDays;

import java.util.HashMap;
import java.util.Map;

public class Service01Stack extends Stack {

    public Service01Stack(final Construct scope, final String id, final Cluster cluster) {
        this(scope, id, cluster, null);
    }

    public Service01Stack(final Construct scope, final String id, final Cluster cluster, final StackProps props) {
        super(scope, id, props);

        Map<String, String> envVariables = new HashMap<>();
        envVariables.put("SPRING_DATASOURCE_URL", "jdbc:mariadb://"
                + Fn.importValue("rds-endpoint")
                + ":3306/aws_service_01?createDatabaseIfNotExist=true"
        );
        envVariables.put("SPRING_DATASOURCE_USERNAME", "admin");
        envVariables.put("SPRING_DATASOURCE_PASSWORD", Fn.importValue("rds-password"));


        RepositoryImage repositoryImage = ContainerImage.fromRegistry("jefersonalmeida/aws_service_01:1.0.1");

        AwsLogDriverProps logDriverProps = AwsLogDriverProps.builder()
                .logGroup(LogGroup.Builder.create(this, id.concat("LogGroup"))
                        .logGroupName(id.concat("LogGroup"))
                        .retention(RetentionDays.TWO_MONTHS)
                        .removalPolicy(RemovalPolicy.DESTROY)
                        .build()
                )
                .streamPrefix(id)
                .build();

        ApplicationLoadBalancedTaskImageOptions applicationLoadBalancedTaskImageOptions =
                ApplicationLoadBalancedTaskImageOptions.builder()
                        .containerName("aws_service_01")
                        .image(repositoryImage)
                        .containerPort(8080)
                        .logDriver(LogDriver.awsLogs(logDriverProps))
                        .environment(envVariables)
                        .build();

        ApplicationLoadBalancedFargateService applicationLoadBalancedFargateService =
                ApplicationLoadBalancedFargateService.Builder.create(this, id.concat("ALB01"))
                        .serviceName("service-01")
                        .cluster(cluster)
                        .cpu(512)
                        .memoryLimitMiB(1024)
                        .desiredCount(1)
                        .listenerPort(8080)
                        .taskImageOptions(applicationLoadBalancedTaskImageOptions)
                        .publicLoadBalancer(true)
                        .build();

        // Target Group
        HealthCheck healthCheck = new HealthCheck.Builder()
                .path("/actuator/health")
                .port(String.valueOf(8080))
                .healthyHttpCodes("200")
                .build();

        applicationLoadBalancedFargateService.getTargetGroup().configureHealthCheck(healthCheck);

        // AutoScale
        EnableScalingProps enableScalingProps = EnableScalingProps.builder()
                .minCapacity(1)
                .maxCapacity(4)
                .build();

        ScalableTaskCount scalableTaskCount =
                applicationLoadBalancedFargateService.getService().autoScaleTaskCount(enableScalingProps);

        CpuUtilizationScalingProps cpuUtilizationScalingProps = CpuUtilizationScalingProps.builder()
                .targetUtilizationPercent(50)
                .scaleInCooldown(Duration.minutes(60))
                .scaleOutCooldown(Duration.seconds(60))
                .build();

        scalableTaskCount.scaleOnCpuUtilization(id.concat("AutoScaling"), cpuUtilizationScalingProps);
    }
}
