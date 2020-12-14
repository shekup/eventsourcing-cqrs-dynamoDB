package com.max.prospect.domain.repository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.mapper.StaticTableSchema;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.ResourceNotFoundException;
// Added for EnhancedQuery
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;


import java.util.*;

import static software.amazon.awssdk.enhanced.dynamodb.mapper.StaticAttributeTags.primaryPartitionKey;
import static software.amazon.awssdk.enhanced.dynamodb.mapper.StaticAttributeTags.primarySortKey;

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

    // Added for Enhanced Query
    static final TableSchema<Prospects> PROSPECTS_TABLE_SCHEMA =  StaticTableSchema.builder(Prospects.class)
            .newItemSupplier(Prospects::new)
            .addAttribute(String.class, a -> a.name("id")
                    .getter(Prospects::getProspectId)
                    .setter(Prospects::setProspectId)
                    .tags(primaryPartitionKey()))
            .addAttribute(String.class, a -> a.name("sort")
                    .getter(Prospects::getVersion)
                    .setter(Prospects::setVersion)
                    .tags(primarySortKey()))
            .build();
    
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

    private Map<String,AttributeValue> getItem(String prospectId, String version) throws Exception {

        HashMap<String,AttributeValue> keyToGet = new HashMap<String,AttributeValue>();
        // Only passing the partition key will not work as table has a composite primary key
        keyToGet.put(partitionKey, AttributeValue.builder()
                .s(prospectId).build());
        keyToGet.put(sortKey, AttributeValue.builder()
                .s(version).build());
        GetItemRequest request = GetItemRequest.builder()
                .key(keyToGet)
                .projectionExpression("VERSION")
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
                return returnedItem;
            } else {
                System.out.format("No item found with the key %s!\n", partitionKey);
                throw new Exception("Prospect not present");
            }
        }catch (DynamoDbException e) {
            System.err.println(e.getMessage());
            //    System.exit(1);
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

     get the last version is not straight as DynamoDb requires both partition key and sort key in Get Item.
     Alternate way is using EnhancedQueryRecords which require creating Bean tagged with @DynamoDbBean
     Another way is write a lambda function that is triggered when new entry is made and it updates the last version attribute

     The Alternate design of using Status as sort key seems more suitable here since it does not depends on last version.
     The insert row of personal details will have sort key of 'PERSONAL_DETAILS'.
     This can result in error if entry with same sort key already exists but it can be treated as Integrity check

     below code tries to use enhanced query but it didn't work.
     The unresolved exception is
     software.amazon.awssdk.services.dynamodb.model.DynamoDbException: Query condition missed key schema element: PROSPECT_ID (Service: DynamoDb, Status Code: 400, Request ID: 583NN2GUGSQ1V3KV5LMP2OHAHBVV4KQNSO5AEMVJF66Q9ASUAAJG, Extended Request ID: null)

     */
    /**
     * https://docs.aws.amazon.com/code-samples/latest/catalog/javav2-dynamodb-src-main-java-com-example-dynamodb-Query.java.html
     * @param prospectId
     * @return
     * @throws Exception
     */
    private String getLastVersion(String prospectId) throws Exception{

        try{
            DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder()
                    .dynamoDbClient(ddb)
                    .build();

            // Below didnt work
            //DynamoDbTable<Prospects> mappedTable = enhancedClient.table(tableName, TableSchema.fromBean(Prospects.class));
            // Trying below
            DynamoDbTable<Prospects> mappedTable = enhancedClient.table(tableName, PROSPECTS_TABLE_SCHEMA);
            QueryConditional queryConditional = QueryConditional
                    .keyEqualTo(Key.builder()
                            .partitionValue(prospectId)
                            .build());
            Iterator<Prospects> results = mappedTable.query(queryConditional).items().iterator();
            String result="";

            while (results.hasNext()) {
                Prospects rec = results.next();
                result = rec.getProspectId();
                System.out.println("The record id is " + result);
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        return "";
    }
    
    /**
     * Add mandatory details such as first name, last name, nationality, etc.
     * The input should be flexible - The number of items can vary from one to many
     * The status value is decided by the method
     */
    /**
     * The method works for any random prospect Id
     * In real implementation, the prospect Id should be validated
     * @param prospectId
     * @param values
     * @throws Exception
     */
    public void addPersonalDetails(String prospectId, HashMap<String,String> values) throws Exception{

        // Below code should be used but getLastVersion method didn't work
        // More work required to resolve it
//        String lastVersion = getLastVersion(prospectId).substring(1);
//        String newVersion = String.valueOf(Integer.parseInt(lastVersion) + 1);

        // In actual implementation.  A get should fire with prospect Id to do two thigs:
        // 1. Validate prospect Id exists 2. Get last version
        // Alternatively if STAGE is used as Sort key then only validate the Prospect Id

        String newVersion = "v1";

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
