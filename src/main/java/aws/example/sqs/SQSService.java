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
import com.amazonaws.services.sqs.model.CreateQueueResult;
import com.amazonaws.services.sqs.model.DeleteMessageResult;
import com.amazonaws.services.sqs.model.ListQueuesRequest;
import com.amazonaws.services.sqs.model.ListQueuesResult;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageResult;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageResult;

public class SQSService {

    private AmazonSQS q;

    public SQSService(AmazonSQS q) {
        this.q = q;
    }

    
    public static CreateQueueResult create(String queueName, AmazonSQS q) {
         CreateQueueRequest create_request = new CreateQueueRequest(queueName)
            .addAttributesEntry("DelaySeconds", "60")
            .addAttributesEntry("MessageRetentionPeriod", "86400");

        try {
            CreateQueueResult res = q.createQueue(create_request);
            return res;
        } catch (AmazonSQSException e) {
            if (!e.getErrorCode().equals("QueueAlreadyExists")) {
                throw e;
            }
        }
        return null;
    }

    public static boolean send(String msg, String queueUrl, AmazonSQS q) {
        try {
            SendMessageRequest sendMessageRequest = new SendMessageRequest(queueUrl, msg)
            .withSdkRequestTimeout(5000);
            System.out.println("sending msg "+msg);
            SendMessageResult res = q.sendMessage(sendMessageRequest);
            System.out.println("messageId = "+res.getMessageId());
            return true;
        } catch (Exception e) {
            System.out.println(e);
            return false;
        }
    }

    public static void deleteQueue(String url, AmazonSQS sqs) {
        try {
            System.out.println("deleting "+url);
            sqs.deleteQueue(url);
            System.out.println(url+" deleted");
        } catch (Exception e) {
            System.out.println("error:" + url);
            System.out.println(e);
        }
    }

    public static List<Message> receive(String queueUrl, AmazonSQS sqs) {
        List<Message> msgs = null;
        try {
            System.out.println("receiving from "+queueUrl);
            ReceiveMessageResult res = sqs.receiveMessage(queueUrl);
            System.out.println("received "+res.toString());
            msgs = res.getMessages(); 
            
        } catch (Exception e) {
            System.out.println("error:" + queueUrl);
            System.out.println(e);
        }
        return msgs;
    }

    public static String processMessage(Message m) {
        try {
           return m.getBody();
        } catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }

    public static boolean deleteMessage(Message m, String url, AmazonSQS sqs) {
        try {
            System.out.println("deleting message \'"+m.getBody()+"\'");
            DeleteMessageResult r = sqs.deleteMessage(url, m.getReceiptHandle());
            System.out.println("message deleted");
            int httpStatusCode = r.getSdkHttpMetadata().getHttpStatusCode();
            System.out.println("httpStatusCode = "+httpStatusCode);
            switch (httpStatusCode) {
                case 200: return true;
                default: return false;
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return false;
    }

    private static final String QUEUE_NAME = "testQueue" +
            new Date().getTime();

    public static void main(String[] args) {
        AmazonSQS sqs = AmazonSQSClientBuilder.standard()
            //.withCredentials(new CredentialsProvider())
            .withRegion(Regions.US_EAST_1)
            .build();
        //defaultClient();
        CreateQueueResult qres = create(QUEUE_NAME, sqs);
       

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