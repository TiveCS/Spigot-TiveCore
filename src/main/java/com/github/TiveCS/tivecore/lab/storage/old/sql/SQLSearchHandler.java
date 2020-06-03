package com.github.TiveCS.tivecore.lab.storage.old.sql;

import java.util.*;

public class SQLSearchHandler {

    public static List<SQLRow> searchRowByCondition(List<Object> conditionValues, List<SQLCondition> conditions, List<SQLColumn> conditionColumns){
        int valueSize = conditionValues.size(), operatorSize = conditions.size(), columnSize = conditionColumns.size();
        Set<SQLRow> rows = new LinkedHashSet<SQLRow>();

        boolean allSameSize = valueSize != 0 && valueSize == operatorSize && valueSize == columnSize;

        if (allSameSize){
            for (int i = 0; i < valueSize; i++){
                Object v = conditionValues.get(i);
                SQLCondition con = conditions.get(i);
                SQLColumn col = conditionColumns.get(i);

                rows.addAll(searchRowByCondition(v, con, col));
            }
        }

        return new ArrayList<>(rows);
    }

    public static List<SQLRow> searchRowByCondition(SQLTable table, Object conditionValue, SQLCondition condition, String conditionColumn){
        SQLColumn column = table.getColumns().get(conditionColumn);
        if (column != null) {
            return searchRowByCondition(conditionValue, condition, column);
        }else{
            return new ArrayList<>();
        }
    }
    public static List<SQLRow> searchRowByCondition(Object conditionValue, SQLCondition condition, SQLColumn conditionColumn){

        List<SQLRow> rows = new ArrayList<>();

        HashMap<UUID, Object> d = conditionColumn.getData();
        for (UUID uuid : d.keySet()){
            Object dv = d.get(uuid);
            boolean pass = false;
            if (conditionValue instanceof Number && dv instanceof Number){
                double v = Double.parseDouble(dv.toString()), cv = Double.parseDouble(conditionValue.toString());
                switch (condition){
                    case NOT_EQUALS:
                        pass = cv != v;
                        break;
                    case EQUALS:
                        pass = cv == v;
                        break;
                    case SMALLER_EQUALS:
                        pass = cv <= v;
                        break;
                    case LARGER_EQUALS:
                        pass = cv >= v;
                        break;
                    case SMALLER:
                        pass = cv < v;
                        break;
                    case LARGER:
                        pass = v > cv;
                        break;
                }
            }
            else{
                switch (condition){
                    case NOT_EQUALS:
                        pass = !conditionValue.equals(dv);
                        break;
                    case EQUALS:
                        pass = conditionValue.equals(dv);
                        break;
                }
            }

            if (pass){
                rows.add(conditionColumn.getTable().getContents().get(uuid));
            }
        }

        return rows;

    }

}
