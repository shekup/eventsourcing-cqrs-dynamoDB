package com.max.prospect.domain.repository;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
import com.amazonaws.services.dynamodbv2.model.GetItemRequest;
import com.amazonaws.services.dynamodbv2.model.ResourceNotFoundException;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;

public class ProspectRepository {
	
	AmazonDynamoDB client;
    DynamoDB dynamoDB;
    Table table;
    DynamoDBTableProperties dynamoDBTableProperties;
   
    
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
    	client = AmazonDynamoDBClientBuilder.standard().build();
    	dynamoDB = new DynamoDB(client);
    	dynamoDBTableProperties = new DynamoDBTableProperties();
    	table = dynamoDB.getTable(dynamoDBTableProperties.getTable());
    }
    
    /**
     * The expectation is client application will store this prospect Id and for get it will pass the prospect Id. 
     * @return
     */
    public UUID createProspect() {
    	UUID prospectId = UUID.randomUUID();
    	try {
    		PutItemOutcome outcome = table.putItem(new Item().withPrimaryKey(dynamoDBTableProperties.getPartitionKey(), prospectId, dynamoDBTableProperties.getSortkey(), "v1").
        			withString("STAGE", Constants.STAGE_CREATE));
        	System.out.println("PutItem succeeded:\n" + outcome.getPutItemResult());
        	return prospectId;
    	} catch (ResourceNotFoundException e) {
    	    System.err.format("Error: The table \"%s\" can't be found.\n", table.getTableName());
    	    System.err.println("Be sure that it exists and that you've typed its name correctly!");
    	} catch (AmazonServiceException e) {
    	    System.err.println(e.getMessage());
    	}
    	return null;
    }
    
    private String getLastVersion(UUID prospectId) {
    	 HashMap<String,AttributeValue> keyToGet = new HashMap<String,AttributeValue>();
    	 
         keyToGet.put(dynamoDBTableProperties.getPartitionKey(), AttributeValue.builder().s(prospectId).build());

         GetItemRequest request = GetItemRequest.builder()
                 .key(keyToGet)
                 .tableName(table)
                 .build();
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
