package com.github.TiveCS.tivecore.lab.storage.old.sql;

import java.util.HashMap;
import java.util.UUID;

public class SQLColumn {

    private SQLTable table;

    private String columnName;
    private SQLDataType dataType;
    private int maxDataLength;

    private boolean isExists = false, isNotNull = false, isAutoIncrement = false;

    private HashMap<UUID, Object> data = new HashMap<>();

    //--------------------------------------

    public SQLColumn(SQLTable table, String columnName, SQLDataType dataType){
        this(table, columnName, dataType, 0, false, false);
    }

    public SQLColumn(SQLTable table, String columnName, SQLDataType dataType, int maxDataLength){
        this(table, columnName, dataType, maxDataLength, false, false);
    }

    public SQLColumn(SQLTable table, String columnName, SQLDataType dataType, int maxDataLength, boolean isNotNull, boolean isAutoIncrement){
        this.table = table;
        this.columnName = columnName;
        this.dataType = dataType;
        this.maxDataLength = maxDataLength;

        this.isNotNull = isNotNull;
        this.isAutoIncrement = (isAutoIncrement && dataType.equals(SQLDataType.INT)) ? true : false;

    }

    //--------------------------------------

    public StringBuilder generateSqlQuery(){
        StringBuilder b = new StringBuilder();

        b.append(columnName).append(" ").append(dataType.name());
        if (maxDataLength > 0){
            b.append("(").append(maxDataLength).append(")");
        }
        if (isAutoIncrement){
            b.append(" AUTO INCREMENT");
        }
        if (isNotNull){
            b.append(" NOT NULL");
        }

        return b;
    }

    @Override
    public String toString() {
        return new StringBuilder().append("SQLColumn{").append("table=").append(table).append(", columnName='").append(columnName).append('\'').append(", dataType=").append(dataType).append(", maxDataLength=").append(maxDataLength).append(", isExists=").append(isExists).append(", isNotNull=").append(isNotNull).append(", isAutoIncrement=").append(isAutoIncrement).append(", data=").append(data).append('}').toString();
    }

    //--------------------------------------

    public void setExists(boolean exists) {
        isExists = exists;
    }

    public void setMaxDataLength(int maxDataLength) {
        this.maxDataLength = maxDataLength;
    }

    public void setAutoIncrement(boolean autoIncrement) {
        isAutoIncrement = autoIncrement;
    }

    public void setNotNull(boolean notNull) {
        isNotNull = notNull;
    }

    //--------------------------------------

    public boolean isNotNull() {
        return isNotNull;
    }

    public boolean isExists() {
        return isExists;
    }

    public boolean isAutoIncrement() {
        return isAutoIncrement;
    }


    //--------------------------------------

    public String getColumnName() {
        return columnName;
    }

    public SQLTable getTable() {
        return table;
    }

    public SQLDataType getDataType() {
        return dataType;
    }

    public int getMaxDataLength() {
        return maxDataLength;
    }

    public HashMap<UUID, Object> getData() {
        return data;
    }
}
