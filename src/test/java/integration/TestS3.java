package integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.time.Instant;

import org.junit.Test;

import com.amazonaws.services.s3.model.Bucket;
//import static org.junit.jupiter.api.Assertions.assertEquals;
import com.amazonaws.services.s3.model.IllegalBucketNameException;

import aws.example.s3.CreateBucket;
import aws.example.s3.DeleteBucket;

public class TestS3 {

    @Test //(timeout=10000)
    public void testBucketCreateDelete() {
        Instant instant = Instant.now();
        final String bucket_name = "Test-S3-integration-"+instant.toEpochMilli();
        System.out.println("bucket_name: "+bucket_name);
        Exception e = assertThrows(
            IllegalBucketNameException.class, 
            () -> CreateBucket.createBucket(bucket_name));
        
        String expectedMessage = "Bucket name should not contain uppercase characters";
        String actualMessage = e.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
        
        final String bucket_name_lower = bucket_name.toLowerCase();
        System.out.println("bucket_name_lower: "+bucket_name_lower);
        Bucket b = CreateBucket.createBucket(bucket_name_lower);
        assertEquals(bucket_name_lower, b.getName());

        DeleteBucket.deleteBucket(bucket_name_lower);

        b = CreateBucket.getBucket(bucket_name);
        assertEquals(b, null);
        
    }
}
