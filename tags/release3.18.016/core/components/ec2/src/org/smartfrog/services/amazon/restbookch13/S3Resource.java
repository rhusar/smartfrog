/*
 * Copyright 2005-2007 Noelios Consulting.
 * 
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the "License"). You may not use this file except in
 * compliance with the License.
 * 
 * You can obtain a copy of the license at
 * http://www.opensource.org/licenses/cddl1.txt See the License for the specific
 * language governing permissions and limitations under the License.
 * 
 * When distributing Covered Code, include this CDDL HEADER in each file and
 * include the License file at http://www.opensource.org/licenses/cddl1.txt If
 * applicable, add the following below this CDDL HEADER, with the fields
 * enclosed by brackets "[]" replaced with your own identifying information:
 * Portions Copyright [yyyy] [name of copyright owner]
 */

package org.smartfrog.services.amazon.restbookch13;

import org.smartfrog.services.amazon.aws.AwsResource;

/**
 * Amazon S3 client. Support class handling authorized requests.
 * 
 * @author Jerome Louvel (contact@noelios.com)
 */
public class S3Resource extends AwsResource {


    public S3Resource(String awsID, String secretKey) {
        super(awsID, secretKey);
    }

    public S3Resource(S3Resource parent) {
        super(parent);
        this.host=parent.host;
    }

    private String host = "https://s3.amazonaws.com/";


    /**
     * Get the base host for these operations
     * @return
     */
    public String getHost() {
        return host;
    }
}
