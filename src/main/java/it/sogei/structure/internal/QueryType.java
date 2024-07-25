package it.sogei.structure.internal;

import lombok.Getter;

@Getter
public enum QueryType {

    SELECT("SELECT"),
    SELECT_ALL("SELECT *"),
    SELECT_COUNT("SELECT COUNT()"),
    SELECT_COUNT_ALL("SELECT COUNT(*)"),
    INSERT("INSERT"),
    UPDATE("UPDATE"),
    DELETE("DELETE");
    private final String queryType;
    QueryType(String queryType) {
        this.queryType = queryType;
    }
}
