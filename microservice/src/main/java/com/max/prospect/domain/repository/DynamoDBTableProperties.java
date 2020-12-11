package com.max.prospect.domain.repository;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "dynamodb")
public class DynamoDBTableProperties {
	
	private String table;
	private String partitionKey;
	private String sortkey;
	
	public String getTable() {
		return table;
	}
	public void setTable(String table) {
		this.table = table;
	}
	public String getPartitionKey() {
		return partitionKey;
	}
	public void setPartitionKey(String partitionKey) {
		this.partitionKey = partitionKey;
	}
	public String getSortkey() {
		return sortkey;
	}
	public void setSortkey(String sortkey) {
		this.sortkey = sortkey;
	}
	

}
