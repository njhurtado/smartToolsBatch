package com.smarttools.database;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.DescribeTableResult;
import org.apache.commons.lang3.StringUtils;

public class DinamoDbContext {

    private static final String ENV_PRODUCCION = "PRODUCCION";
    private static final String LOCAL_ENDPOINT = "http://localhost:8000";
    private static AmazonDynamoDB client;
    private static String dynamoUser;
    private static String dynamoPassword;
    //private static DynamoDBMapper mapper;

    private DinamoDbContext() {
        // -- Evita la instanciacion
    }

    public static AmazonDynamoDB getClient() {
        // System.getProperty("ENV")
        /*String ENV = System.getProperty("ENV");
        if (ENV.equals(ENV_PRODUCCION)) {
            client = AmazonDynamoDBClientBuilder.standard().
                    withRegion(Regions.DEFAULT_REGION).build();
        } else {
            //docker run -p 8000:8000 amazon/dynamodb-local
            System.setProperty("aws.accessKeyId", "super-access-key");
            System.setProperty("aws.secretKey", "super-secret-key");

            client = AmazonDynamoDBClientBuilder.standard().withEndpointConfiguration(
                    new AwsClientBuilder.EndpointConfiguration(LOCAL_ENDPOINT, "us-west-2"))
                    .build();

        }*/
    	
    	 dynamoUser = System.getProperty("dynamoUsr");
         dynamoPassword = System.getProperty("dynamoPwd");
    	
    	
    	/*client = AmazonDynamoDBClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(
    			new BasicAWSCredentials(dynamoUser, dynamoPassword))).
    				withRegion(Regions.EU_WEST_2).build();*/
    	
    	client = AmazonDynamoDBClientBuilder.standard()
                .withRegion(Regions.US_WEST_2)
                .withCredentials(new AWSStaticCredentialsProvider(
            			new BasicAWSCredentials(dynamoUser, dynamoPassword)))
                .build();
    	
        return client;
    }


    public static DynamoDBMapper getMapper() {
        return new DynamoDBMapper(getClient());
    }

    public static boolean existsTable(String tableName) {
        if (StringUtils.isBlank(tableName)) {
            return false;
        }
        try {
            DescribeTableResult res = getClient().describeTable(tableName);
            return res != null;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


}
