// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
package aws.example.sqs;

import java.util.Date;
import java.util.List;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.AmazonSQSException;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.amazonaws.services.sqs.model.ListQueuesRequest;
import com.amazonaws.services.sqs.model.ListQueuesResult;

public class UsingQueues {
    private static final String QUEUE_NAME = "testQueue" +
            new Date().getTime();

    public static void main(String[] args) {
        AmazonSQS sqs = AmazonSQSClientBuilder.standard()
            //.withCredentials(new CredentialsProvider())
            .withRegion(Regions.US_EAST_1)
            .build();
        //defaultClient();

        // Creating a Queue
        CreateQueueRequest create_request = new CreateQueueRequest(QUEUE_NAME)
                .addAttributesEntry("DelaySeconds", "60")
                .addAttributesEntry("MessageRetentionPeriod", "86400");

        try {
            sqs.createQueue(create_request);
        } catch (AmazonSQSException e) {
            if (!e.getErrorCode().equals("QueueAlreadyExists")) {
                throw e;
            }
        }

        // Get the URL for a queue
        String queue_url = sqs.getQueueUrl(QUEUE_NAME).getQueueUrl();
        System.out.println("queue_url: " + queue_url);


        // List your queues
        ListQueuesResult lq_result = sqs.listQueues();
        System.out.println("Your SQS Queue URLs:");
        for (String url : lq_result.getQueueUrls()) {
            System.out.println(url);
        }

        // List queues with filters
        List.of("Queue", "test").forEach(
            name_prefix -> { 
                ListQueuesResult lq_result_with_prefix = sqs.listQueues(new ListQueuesRequest(name_prefix));
                System.out.println("Queue URLs with prefix: " + name_prefix);
                for (String url : lq_result_with_prefix.getQueueUrls())
                    System.out.println(url);
             }
        );

        // Delete the Queue
        sqs.deleteQueue(queue_url);

        sqs.createQueue("Queue1" + new Date().getTime());
        sqs.createQueue("Queue2" + new Date().getTime());
        sqs.createQueue("MyQueue" + new Date().getTime());
        sqs.createQueue("testQueue" + new Date().getTime());
        
        List.of("Queue", "test", "MyQueue").forEach(
            name_prefix -> { 
                ListQueuesResult lq_result_with_prefix = sqs.listQueues(new ListQueuesRequest(name_prefix));
                System.out.println("Queue URLs with prefix: " + name_prefix);
                for (String url : lq_result_with_prefix.getQueueUrls()) {
                    try {
                        System.out.println("deleting "+url);
                        sqs.deleteQueue(url);
                    } catch (Exception e) {
                        System.out.println("error:" + url);
                        System.out.println(e);
                    }
                }
             }
        );
        
    }
}