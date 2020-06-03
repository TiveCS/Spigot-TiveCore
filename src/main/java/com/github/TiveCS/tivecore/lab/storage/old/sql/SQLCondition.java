package com.github.TiveCS.tivecore.lab.storage.old.sql;

import java.util.Collection;

public enum SQLCondition {

    LARGER(">", SQLDataType.getNumberType, null), LARGER_EQUALS(">=", SQLDataType.getNumberType, null),
    SMALLER("<", SQLDataType.getNumberType, null), SMALLER_EQUALS("<=", SQLDataType.getNumberType, null),
    EQUALS("=", null, null), NOT_EQUALS("!=", null, null);

    private String symbol = "";
    private Collection<SQLDataType> allowedDataType;
    private Collection<SQLDataType> disallowedDataType;

    SQLCondition(String symbol, Collection<SQLDataType> allowedDataType, Collection<SQLDataType> disallowedDataType){
        this.symbol = symbol;
        this.allowedDataType = allowedDataType;
        this.disallowedDataType = disallowedDataType;
    }

    public static SQLCondition matchSymbol(String symbol){
        for (SQLCondition c : values()){
            if (c.getSymbol().equals(symbol)){
                return c;
            }
        }
        return null;
    }

    public boolean checkAllowed(final SQLDataType dataType){
        if (dataType == null){
            return false;
        }

        if (disallowedDataType == null && allowedDataType == null){
            return true;
        }
        if (allowedDataType.contains(dataType)){
            return true;
        }


        return false;
    }

    public String getSymbol() {
        return symbol;
    }
}
