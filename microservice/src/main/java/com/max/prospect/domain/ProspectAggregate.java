package com.max.prospect.domain;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.max.prospect.application.commands.CreateProspectCommand;

public class ProspectAggregate {
	
	static AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().build();
    static DynamoDB dynamoDB = new DynamoDB(client);
    static Table table = dynamoDB.getTable("Prospect");
    
    /**
     * Connection pooling is not required in DynamoDB
     * https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/SQLtoNoSQL.Accessing.html
     * @param createProspectCommand
     */
	
	public void createPropspectHandler(CreateProspectCommand createProspectCommand) {
		
	}

}
