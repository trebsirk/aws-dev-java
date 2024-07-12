package integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.time.Instant;

import org.junit.Before;
import org.junit.Test;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.IllegalBucketNameException;

import aws.example.s3.CreateBucket;
import aws.example.s3.DeleteBucket;

public class TestS3 {

    @Before
    public void before() {
        System.out.println("before...");
    }

    @Test //(timeout=10000)
    public void testBucketCreateDelete() {
        Instant instant = Instant.now();
        Regions r = Regions.DEFAULT_REGION;
        final String bucket_name = "Test-S3-integration-"+instant.toEpochMilli();
        System.out.println("bucket_name: "+bucket_name);
        Exception e = assertThrows(
            IllegalBucketNameException.class, 
            () -> CreateBucket.createBucket(bucket_name, r));
        
        String expectedMessage = "Bucket name should not contain uppercase characters";
        String actualMessage = e.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
        
        final String bucket_name_lower = bucket_name.toLowerCase();
        System.out.println("bucket_name_lower: "+bucket_name_lower);
        Bucket b = CreateBucket.createBucket(bucket_name_lower, r);
        assertEquals(bucket_name_lower, b.getName());

        DeleteBucket.deleteBucket(bucket_name_lower, r);

        b = CreateBucket.getBucket(bucket_name, r);
        assertEquals(b, null);
        
    }
}
