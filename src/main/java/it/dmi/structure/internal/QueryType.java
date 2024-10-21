package it.dmi.structure.internal;

import lombok.Getter;

@Getter
public enum QueryType {

    SELECT("SELECT"),
    SELECT_COUNT("COUNT"),
    INSERT("INSERT"),
    UPDATE("UPDATE"),
    DELETE("DELETE"),
    NOT_SUPPORTED("NOT_SUPPORTED");

    public final String queryType;
    QueryType(String queryType) {
        this.queryType = queryType;
    }
}
