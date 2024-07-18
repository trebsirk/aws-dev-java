package it;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.time.Instant;
import java.util.Date;
import java.util.List;

import org.junit.Test;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.CreateQueueResult;
import com.amazonaws.services.sqs.model.Message;

import aws.example.sqs.SQSService;

public class TestSQS {
    
    @Test(expected = Exception.class)
    public void testException() {
        SQSService.create("testQueue"+(new Date().getTime()), null);
    }

    @Test
    public void testSendAndReceiveAndDeleteMessage() {
        // mvn -Dtest=TestSQS#testSendAndReceiveAndDeleteMessage test
        final String QUEUE_NAME = "sqs-dev-demo";
        final String testMsg = "test message";
        AmazonSQS sqs = null;
        try {
            sqs = AmazonSQSClientBuilder.standard()
            .withRegion(Regions.US_EAST_1)
            .build();
            
            String queueUrl = sqs.getQueueUrl(QUEUE_NAME).getQueueUrl();
            
            /*
            GetQueueAttributesRequest req = new GetQueueAttributesRequest();
            req = req.withQueueUrl(queueUrl);
            GetQueueAttributesResult res = sqs.getQueueAttributes(req);
            System.out.println("GetQueueAttributesResult");
            System.out.println(res);
             */
            
            // test send
            boolean sendOkay = SQSService.send(testMsg, queueUrl, sqs);
            assertTrue(sendOkay);

            // test receive
            List<Message> msgs = SQSService.receive(queueUrl, sqs);
            for (int i = 0; i < 10; i++) {
                System.out.println("get message [attempt="+i+"]");
                Thread.sleep(2000);
                msgs = SQSService.receive(queueUrl, sqs);
                if (msgs.size() > 0) break;
            }
            
            System.out.println("msgs.size = "+msgs.size());
            List<String> ms = msgs.stream().map(m -> m.getBody()).toList();//.collect(Collections.list)
            ms.forEach(s -> {
                System.out.println("found message: "+s);
            });
            String msgReceived = ms.get(0);
            assertEquals(msgReceived, testMsg);

            // test delete
            boolean msgDeleted = SQSService.deleteMessage(msgs.get(0), queueUrl, sqs);
            assertTrue(msgDeleted);

        } catch (Exception e) {
            System.out.println(e);
        }
        
    }

    @Test
    public void testEndToEnd() {
        // mvn -Dtest=TestSQS#testEndToEnd test
        final String QUEUE_NAME = "test-integration-end-to-end-"+Instant.now().toEpochMilli();
        final String testMsg = "test message";
        AmazonSQS sqs = null;
        try {
            sqs = AmazonSQSClientBuilder.standard()
            .withRegion(Regions.US_EAST_1)
            .build();
            
            System.out.println("creating queue "+QUEUE_NAME);
            CreateQueueResult qres = SQSService.create(QUEUE_NAME, sqs);
            assertNotEquals(qres, null);
            
            int i = qres.getQueueUrl().lastIndexOf("/");
            String urlReceived = qres.getQueueUrl().substring(i+1);
            assertEquals(QUEUE_NAME, urlReceived);
            System.out.println("urlReceived = "+urlReceived);

            boolean sendOkay = SQSService.send(testMsg, qres.getQueueUrl(), sqs);
            assertTrue(sendOkay);
            Thread.sleep(15000);
            List<Message> msgs = SQSService.receive(qres.getQueueUrl(), sqs);
            System.out.println("msgs.size = "+msgs.size());
            List<String> ms = msgs.stream().map(m -> m.getBody()).toList();//.collect(Collections.list)
            ms.forEach(s -> {
                System.out.println("found message: "+s);
            });
            String msgReceived = ms.get(0);
            assertEquals(msgReceived, testMsg);
            

        } catch (Exception e) {
            System.out.println(e);
        } finally {
            try {
                SQSService.deleteQueue(QUEUE_NAME, sqs);
            } catch (Exception e) {
                System.out.println(e);
            }
        }
        
    }
}
