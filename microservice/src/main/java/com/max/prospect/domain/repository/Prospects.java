package com.max.prospect.domain.repository;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

@DynamoDbBean
public class Prospects {

    private String prospectId;
    private String version;

    @DynamoDbPartitionKey
    public String getProspectId() {
        return prospectId;
    }

    public void setProspectId(String prospectId) {
        this.prospectId = prospectId;
    }

    @DynamoDbSortKey
    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
