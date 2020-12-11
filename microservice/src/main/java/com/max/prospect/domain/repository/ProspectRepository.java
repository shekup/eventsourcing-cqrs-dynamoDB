package com.max.prospect.domain.repository;
import org.springframework.beans.factory.annotation.Autowired;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.ResourceNotFoundException;

import java.util.HashMap;
import java.util.UUID;

public class ProspectRepository {

    @Autowired
	DynamoDBTableProperties dynamoDBTableProperties;

	String tableName;
	Region region;
    DynamoDbClient ddb;
    String partitionKey;
    String sortKey;
    
    /**
     * Connection pooling is not required in DynamoDB
     * https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/SQLtoNoSQL.Accessing.html
     * @param createProspectCommand
     */
    
    /**
     * One common way to generate the ID is using UUID
     * 
     * Atomic Long can also be used here so that prospect Id are more clear and understandable, such as, create an AtomicLong with an initial value 1
     * AtomicLong atomicLong = new AtomicLong(1);
     * But it can be a problem in micro services world, in which multiple instances of application may run with each one having separate AtomicLong
     */
    
    ProspectRepository(){
    	tableName = dynamoDBTableProperties.getTable();
    	partitionKey = dynamoDBTableProperties.getPartitionKey();
    	sortKey = dynamoDBTableProperties.getSortkey();
        DynamoDbClient ddb = DynamoDbClient.builder()
                .region(region)
                .build();
    }
    
    /**
     * The expectation is client application will store this prospect Id and for get it will pass the prospect Id. 
     * @return
     */
    public UUID createProspect() {
    	UUID prospectId = UUID.randomUUID();
        HashMap<String,AttributeValue> itemValues = new HashMap<String,AttributeValue>();
        itemValues.put(partitionKey, AttributeValue.builder().s(String.valueOf(prospectId)).build());
        itemValues.put(sortKey, AttributeValue.builder().s("v1").build());
        itemValues.put("STAGE", AttributeValue.builder().s(String.valueOf(Constants.STAGE_CREATE)).build());
        PutItemRequest request = PutItemRequest.builder()
                .tableName(tableName)
                .item(itemValues)
                .build();
    	try {
            ddb.putItem(request);
        	return prospectId;
    	} catch (ResourceNotFoundException e) {
    	    System.err.format("Error: The table \"%s\" can't be found.\n", tableName);
    	    System.err.println("Be sure that it exists and that you've typed its name correctly!");
    	} catch (DynamoDbException e) {
    	    System.err.println(e.getMessage());
    	}
    	return null;
    }

    /**
    Incomplete
     */
    private String getLastVersion(UUID prospectId) {
    	 HashMap<String,AttributeValue> keyToGet = new HashMap<String,AttributeValue>();
    	 
         keyToGet.put(dynamoDBTableProperties.getPartitionKey(), AttributeValue.builder().s(String.valueOf(prospectId)).build());

         GetItemRequest request = GetItemRequest.builder()
                 .key(keyToGet)
                 .tableName(tableName)
                 .build();
         return "";
    }
    
    /**
     * Add mandatory details such as first name, last name, nationality, etc.
     * The input should be flexible - The number of items can vary from one to many
     * The status value is decided by the method
     */
    
    public void addPersonalDetails(UUID prospectId, HashMap<String, String> item_values) {
    	
    }
    
    /**
     * Add optional additional details such as marital status, employment details, etc. 
     */
    public void addAdditionalDetails(UUID prospectId, HashMap<String, String> item_values) {
    	
    }
    
    public void addAddress() {
    	
    }
    

}
