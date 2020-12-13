package com.max.prospect.domain.repository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.ResourceNotFoundException;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Component
public class ProspectRepository {

    @Value("${dynamodb.table}")
	String tableName;

	Region region;

    DynamoDbClient ddb;
    @Value("${dynamodb.partitionKey}")
    String partitionKey;
    @Value("${dynamodb.sortkey}")
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
        region = Region.AP_SOUTH_1;
        ddb = DynamoDbClient.builder()
                .region(region)
                .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
                .build();
    }
    
    /**
     * The expectation is client application will store this prospect Id and for get it will pass the prospect Id.
     * AWS SDK can give 'java.net.UnknownHostException: dynamodb.ap-south-1.amazonaws.com' exception sometimes
     * Some retry mechanism should be added for UnknownHostException
     * @return
     */
    public String createProspect() {
      	String prospectId = String.valueOf(UUID.randomUUID());
        HashMap<String,AttributeValue> itemValues = new HashMap<String,AttributeValue>();
        itemValues.put(partitionKey, AttributeValue.builder().s(prospectId).build());
        itemValues.put(sortKey, AttributeValue.builder().s("v1").build());
        itemValues.put("STAGE", AttributeValue.builder().s(String.valueOf(Constants.STAGE_CREATE)).build());
        PutItemRequest request = PutItemRequest.builder()
                .tableName(tableName)
                .item(itemValues)
                .build();
    	System.out.println("Creating Prospect");
        try {
            ddb.putItem(request);
            System.out.println("Prospect created: " + prospectId);
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
        Possible values of sort key: v0, v1, v2, v3, and so on
        In rare race conditions values can be: v0, v1, v1, v2, v3, v3, v4, and so on.  That is few duplicates
        OR v0, v2, v1, v3, v4
        Duplicate or Inordered versions are not problem in DynamoDB.
        It is a requirement also to store every event without bothering about sort key value
        While retrieving if Collections.sort is used the outcome will be sorted values in alphabetical order
        considering sort key values are strings here
        v0, v1, v1, v2, v3, v4, and so on
        The get query on table such as prospect summary will handle it.
     */
    private String getLastVersion(String prospectId) throws Exception{
    	HashMap<String,AttributeValue> keyToGet = new HashMap<String,AttributeValue>();
        keyToGet.put(partitionKey, AttributeValue.builder()
                .s(prospectId).build());
        GetItemRequest request = GetItemRequest.builder()
                 .key(keyToGet)
                 .projectionExpression("sortKey")
                 .tableName(tableName)
                 .build();

        try{
            Map<String,AttributeValue> returnedItem = ddb.getItem(request).item();
            if (returnedItem != null) {
                Set<String> keys = returnedItem.keySet();
                System.out.println("Amazon DynamoDB table attributes: \n");

                for (String key1 : keys) {
                    System.out.format("%s: %s\n", key1, returnedItem.get(key1).toString());
                }
            } else {
                System.out.format("No item found with the key %s!\n", partitionKey);
                throw new Exception("Prospect not present");
            }
        }catch (DynamoDbException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }


         return "";
    }
    
    /**
     * Add mandatory details such as first name, last name, nationality, etc.
     * The input should be flexible - The number of items can vary from one to many
     * The status value is decided by the method
     */
    
    public void addPersonalDetails(String prospectId, HashMap<String,String> values) throws Exception{

        String lastVersion = getLastVersion(prospectId).substring(1);
        String newVersion = String.valueOf(Integer.parseInt(lastVersion) + 1);

        HashMap<String,AttributeValue> itemValues = new HashMap<String,AttributeValue>();
        for(String s: values.keySet()){
            itemValues.put(s, AttributeValue.builder().s(values.get(s)).build());
        }
        itemValues.put(partitionKey, AttributeValue.builder().s(prospectId).build());
        itemValues.put(sortKey, AttributeValue.builder().s(newVersion).build());
        itemValues.put("STAGE", AttributeValue.builder().s(String.valueOf(Constants.STAGE_ADD_PERSONAL_DETAILS)).build());

        PutItemRequest request = PutItemRequest.builder()
                .tableName(tableName)
                .item(itemValues)
                .build();

        System.out.println("Adding personal details");

        try {
            ddb.putItem(request);
            System.out.println("Personal details added");
        } catch (ResourceNotFoundException e) {
            System.err.format("Error: The table \"%s\" can't be found.\n", tableName);
            System.err.println("Be sure that it exists and that you've typed its name correctly!");
        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
        }

    }
    
    /**
     * Add optional additional details such as marital status, employment details, etc. 
     */
    public void addAdditionalDetails(String prospectId, HashMap<String, String> item_values) {
    	
    }
    
    public void addAddress() {
    	
    }
    

}
