package integration;

import static org.junit.Assert.assertEquals;

import java.time.Instant;
import java.util.Date;

import org.junit.Test;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.CreateQueueResult;

import aws.example.sqs.SQSService;

public class TestSQS {
    
    @Test(expected = Exception.class)
    public void testException() {
        System.out.println("hello");
        //CreateQueueResult res = 
        SQSService.create("testQueue"+(new Date().getTime()), null);
    }

    @Test
    public void testSQSEndToEnd() {
        String QUEUE_NAME = "test-integration-end-to-end-"+Instant.now().toEpochMilli();
        AmazonSQS sqs = null;
        try {
            sqs = AmazonSQSClientBuilder.standard()
            .withRegion(Regions.US_EAST_1)
            .build();
            
            System.out.println("creating queue "+QUEUE_NAME);
            CreateQueueResult qres = SQSService.create(QUEUE_NAME, sqs);
            
            int i = qres.getQueueUrl().lastIndexOf("/");
            String urlReceived = qres.getQueueUrl().substring(i+1);
            System.out.println("urlReceived = "+urlReceived);
            assertEquals(QUEUE_NAME, urlReceived);

        } catch (Exception e) {
            System.out.println(e);
        } finally {
            try {
                SQSService.delete(QUEUE_NAME, sqs);
            } catch (Exception e) {
                System.out.println(e);
            }
        }
        
    }
}
