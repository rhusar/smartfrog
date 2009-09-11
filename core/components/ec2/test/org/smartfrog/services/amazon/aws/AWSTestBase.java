package org.smartfrog.services.amazon.aws;

import org.smartfrog.test.DeployingTestBase;

/**
 *
 */
public class AWSTestBase extends DeployingTestBase {
    public static final String AWSID = "test.awsid";
    public static final String AWSKEY = "test.awskey";
    public static final String EC2_ENABLED = "test.ec2.enabled";

    public AWSTestBase(String name) {
        super(name);
    }

    protected String getAwsID() {
        return System.getProperty(AWSID);
    }

    protected String getAwsKey() {
        return System.getProperty(AWSKEY);
    }

    protected boolean isEC2Enabled() {
        return Boolean.getBoolean(EC2_ENABLED);
    }
}
