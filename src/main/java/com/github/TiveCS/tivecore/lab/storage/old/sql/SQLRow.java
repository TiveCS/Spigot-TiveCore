package com.github.TiveCS.tivecore.lab.storage.old.sql;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

// Store all column data from a row in a table
public class SQLRow {

    private SQLTable table;
    private UUID uniqueId;

    private HashMap<SQLColumn, Object> column = new HashMap<>();

    public SQLRow(SQLTable table){
        this.table = table;
        this.uniqueId = UUID.randomUUID();
    }

    //-------------------------------------------

    public void inputData(SQLColumn column, Object value){

        table.getContents().put(this.uniqueId, this);

        this.column.put(column, value);

        column.getData().put(this.uniqueId, value);
    }

    public void removeData(SQLColumn removedColumn){
        removedColumn.getData().remove(this.uniqueId);
        this.column.remove(removedColumn);
    }

    public void removeAllData(){
        table.getContents().remove(this.uniqueId);

        for (SQLColumn c : column.keySet()){
            c.getData().remove(this.uniqueId);
        }

        this.column.clear();
    }

    //-------------------------------------------

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder().append("SQLRow{")
                .append("table_name=").append(table.getTableName())
                .append(", row_uniqueId=").append(uniqueId)
                .append(", row_data={");

        List<SQLColumn> cls = new ArrayList<>(column.keySet());
        int size = cls.size();

        for (int i = 0; i < size; i++){
            SQLColumn c = cls.get(i);
            Object o = c.getData().getOrDefault(this.uniqueId, null);

            if (o != null) {
                b.append(c.getColumnName()).append("=");

                if (o instanceof String){
                    b.append("'").append(o).append("'");
                }else{
                    b.append(o);
                }

            }else{
                continue;
            }

            if (i + 1 < size){
                b.append(", ");
            }else{
                b.append("}");
            }
        }
        b.append("}");

        return b.toString();
    }


    //-------------------------------------------

    public SQLTable getTable() {
        return table;
    }

    public UUID getUniqueId() {
        return uniqueId;
    }

    public HashMap<SQLColumn, Object> getColumn() {
        return column;
    }
}
