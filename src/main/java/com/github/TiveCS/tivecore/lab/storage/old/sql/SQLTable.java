package com.github.TiveCS.tivecore.lab.storage.old.sql;

import java.sql.*;
import java.util.*;

// Store all rows data from sql table
public class SQLTable {

    private String catalog, tableName;
    private MySQLManager manager;

    private HashMap<String, SQLColumn> columns = new HashMap<>();
    private SQLColumn primaryKeyColumn = null;

    private HashMap<UUID, SQLRow> contents = new HashMap<>();

    private boolean isExists = false;

    public SQLTable(MySQLManager manager, String tableName){
        this(manager, manager.getCatalog(), tableName, false);
    }

    public SQLTable(MySQLManager manager, String catalog, String tableName, boolean isExists){

        this.manager = manager;
        this.catalog = catalog;
        this.tableName = tableName;
        this.isExists = isExists;

    }

    //----------------------------------------------
    //                DATA LOADER

    public void loadColumnList(){
        this.columns.clear();
        if (!isExists){
            return;
        }

        StringBuilder sql = new StringBuilder("SELECT * FROM ").append("`").append(catalog).append("`.`").append(tableName).append("` LIMIT 1;");

        try {
            Statement s = manager.getConnection().createStatement();
            ResultSet rs = s.executeQuery(sql.toString());
            ResultSetMetaData rsm = rs.getMetaData();

            int columnSize = rsm.getColumnCount();

            for (int c = 1; c <= columnSize; c++) {

                String name = rsm.getColumnName(c);
                SQLDataType type = SQLDataType.valueOf(rsm.getColumnTypeName(c));

                int size = rsm.getColumnDisplaySize(c);
                boolean notNull = rsm.isNullable(c) == 1 ? true : false;
                boolean autoInc = rsm.isAutoIncrement(c);

                SQLColumn column = new SQLColumn(this, name, type, size, notNull, autoInc);
                this.columns.put(column.getColumnName(), column);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void loadAllData(){
        if (this.columns.isEmpty()){
            loadColumnList();
        }

        if (!isExists){
            return;
        }

        StringBuilder sql = new StringBuilder("SELECT * FROM ").append("`").append(catalog).append("`.`").append(tableName).append("`;");

        try {
            Statement s = manager.getConnection().createStatement();
            ResultSet rs = s.executeQuery(sql.toString());

            while (rs.next()){
                SQLRow row = new SQLRow(this);
                for (SQLColumn c : columns.values()){
                    row.inputData(c, rs.getObject(c.getColumnName()));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void loadAllData(int maxAmount){

    }

    //----------------------------------------------
    //               QUERY GENERATOR

    public StringBuilder generateCreateTableQuery() {
        StringBuilder sql = new StringBuilder();

        sql.append("CREATE TABLE IF NOT EXISTS `").append(catalog).append("`.`").append(tableName).append("` (");

        int columnSize = columns.size();
        for (int i = 0; i < columnSize; i++){
            SQLColumn c = columns.get(i);
            boolean isPrimary = primaryKeyColumn != null && primaryKeyColumn.equals(c);

            if (!isPrimary){
                sql.append(c.generateSqlQuery());
            }else{
                c.setAutoIncrement(false);
                c.setNotNull(false);

                sql.append(c.generateSqlQuery()).append(" PRIMARY KEY");
            }

            if (i + 1 < columnSize) {
                sql.append(",");
            }
        }

        sql.append(");");

        return sql;
    }

    //----------------------------------------------
    //               INPUT AND OUTPUT

    public SQLRow insert(HashMap<String, Object> columnValues, boolean update) throws SQLException {
        if (columnValues.isEmpty()){
            return null;
        }

        SQLRow row = new SQLRow(this);
        boolean allClear = false;
        List<String> keys = new ArrayList<>(columnValues.keySet());
        for (String column : keys){
            SQLColumn c = columns.getOrDefault(column, null);
            allClear = c != null;

            if (!allClear){
                return null;
            }
        }

        for (String column : keys){
            SQLColumn c = columns.get(column);
            row.inputData(c, columnValues.get(column));
        }

        contents.put(row.getUniqueId(), row);
        if (update){
            StringBuilder sql = new StringBuilder("INSERT INTO ").append("`").append(catalog).append("`.`").append(tableName).append("`");
            int size = keys.size();

            sql.append("(");
            for (int i = 0; i < size; i++){
                sql.append(keys.get(i));
                if (i + 1 < size){
                    sql.append(", ");
                }
            }
            sql.append(") VALUES(");

            for (int i = 0; i < size; i++){
                sql.append("?");
                if (i + 1 < size){
                    sql.append(", ");
                }
            }
            sql.append(");");

            PreparedStatement ps = manager.getConnection().prepareStatement(sql.toString());
            for (int i = 1; i <= size; i++){
                String column = keys.get(i - 1);
                Object value = columnValues.get(column);

                ps.setObject(i, value);
            }
            ps.executeUpdate();
        }
        return row;
    }

    public void update(HashMap<String, Object> columnValues, List<UUID> targetRows, boolean update) throws SQLException {
        List<SQLRow> rows = new ArrayList<>();

        if (!this.columns.keySet().containsAll(columnValues.keySet())){
            return;
        }

        for (UUID uuid : targetRows){
            SQLRow row = getContents().get(uuid);
            if (row == null){
                return;
            }
            rows.add(row);
        }

        if (update){
            StringBuilder sql = new StringBuilder("UPDATE `").append(catalog).append("`.`").append(tableName).append("`").append(" SET ");
            List<String> columns = new ArrayList<>(columnValues.keySet());
            int size = columns.size();

            for (int i = 0; i < size; i++){
                String c = columns.get(i);
                Object v = columnValues.get(c);

                sql.append(c).append("=");
                if (!(v instanceof Number)){
                    sql.append("'").append(v.toString()).append("'");
                }else{
                    sql.append(v);
                }

                if (i + 1 < size){
                    sql.append(", ");
                }
            }

            sql.append(" WHERE");
            int rowSize = rows.size();
            List<SQLColumn> tableColumn = new ArrayList<>(this.columns.values());

            for (int i = 0; i < rowSize; i++){
                SQLRow row = rows.get(i);
                sql.append(" (");

                for (int a = 0; a < this.columns.size(); a++){
                    SQLColumn tc = tableColumn.get(a);
                    Object o = row.getColumn().get(tc);

                    sql.append("`").append(tc.getColumnName()).append("`=");
                    if (!(o instanceof Number)){
                        sql.append("'").append(o.toString()).append("'");
                    }else{
                        sql.append(o);
                    }

                    if (a + 1 < this.columns.size()){
                        sql.append(" AND ");
                    }
                }

                sql.append(")");

                if (i + 1 < rowSize){
                    sql.append(" AND");
                }
            }
            sql.append(";");

            Statement s = manager.getConnection().createStatement();
            s.executeUpdate(sql.toString());
        }

        for (SQLRow row : rows){
            for (String columnParameter : columnValues.keySet()) {
                SQLColumn col = this.columns.get(columnParameter);
                row.inputData(col, columnValues.get(columnParameter));
            }
        }
    }
    public void update(HashMap<String, Object> columnValues, List<Object> conditionValue, List<SQLCondition> conditions, List<SQLColumn> columnCondition, boolean update) throws SQLException {
        if (!columnValues.isEmpty()){
            List<SQLRow> rows = SQLSearchHandler.searchRowByCondition(conditionValue, conditions, columnCondition);
            HashMap<SQLColumn, Object> usedColumn = new HashMap<>();

            for (String columnName : columnValues.keySet()){
                SQLColumn column = columns.get(columnName);
                if (column == null){
                    return;
                }
                usedColumn.put(column, columnValues.get(columnName));
            }

            for (SQLRow row : rows){
                for (SQLColumn c : usedColumn.keySet()){
                    row.inputData(c, usedColumn.get(c));
                }
            }
            if (update){
                StringBuilder sql = new StringBuilder("UPDATE `").append(catalog).append("`.`").append(tableName).append("` SET ");
                int size = usedColumn.keySet().size(), count = 0;

                for (SQLColumn c : usedColumn.keySet()){
                    Object value = usedColumn.get(c);
                    sql.append(c.getColumnName()).append("=").append(value);
                    if (count + 1 < size){
                        sql.append(", ");
                    }
                }

                int cvs = conditionValue.size();
                if (cvs > 0) {
                    sql.append(" WHERE ");
                    for (int i = 0; i < cvs; i++) {
                        Object cv = conditionValue.get(i);
                        SQLCondition cond = conditions.get(i);
                        SQLColumn condColumn = columnCondition.get(i);

                        sql.append(condColumn.getColumnName()).append(cond.getSymbol());
                        if (cv instanceof Number){
                            sql.append(cv);
                        }else{
                            sql.append("'").append(cv).append("'");
                        }

                        if (i + 1 < cvs){
                            sql.append(", ");
                        }

                    }
                }
                sql.append(";");

                Statement s = manager.getConnection().createStatement();
                s.executeUpdate(sql.toString());
            }
            return;
        }


    }

    // TODO Unfinished delete data / row
    public void delete(List<Object> conditionValue, List<SQLCondition> conditions, List<SQLColumn> columnCondition, boolean update) throws SQLException {
        List<SQLRow> rowUuid = SQLSearchHandler.searchRowByCondition(conditionValue, conditions, columnCondition);

        if (update){
            StringBuilder sql = new StringBuilder("DELETE FROM `").append(catalog).append("`.`").append(tableName).append("` WHERE ");

            List<SQLColumn> cols = new ArrayList<>(this.columns.values());
            for (SQLRow row : rowUuid){
                sql.append("(");

                for (int i = 0; i < cols.size(); i++){
                    SQLColumn c = cols.get(i);
                    Object o = row.getColumn().get(c);

                    sql.append("`").append(c.getColumnName()).append("`=");
                    if (!(o instanceof Number)){
                        sql.append("'").append(o.toString()).append("'");
                    }else{
                        sql.append(o);
                    }

                    if (i + 1 < cols.size()) {
                        sql.append(" AND ");
                    }

                }

                sql.append(")");
            }

            sql.append(";");

            Statement s = manager.getConnection().createStatement();
            s.executeUpdate(sql.toString());
        }

        for (SQLRow row : rowUuid){
            row.removeAllData();
        }
    }
    public void delete(List<UUID> targetRows, boolean update) throws SQLException {
        if (!this.contents.keySet().containsAll(targetRows)){
            return;
        }

        LinkedHashSet<UUID> set = new LinkedHashSet<>(targetRows);
        if (update){
            StringBuilder sql = new StringBuilder("DELETE FROM `").append(catalog).append("`.`").append(tableName).append("` WHERE ");
            for (UUID uuid : set){
                SQLRow row = this.contents.get(uuid);
                sql.append("(");

                HashMap<SQLColumn, Object> d = row.getColumn();
                List<SQLColumn> cls = new ArrayList<>(d.keySet());
                int ds = cls.size();
                for (int i = 0; i < ds; i++){
                    SQLColumn col = cls.get(i);
                    Object o = d.get(col);

                    sql.append("`").append(col.getColumnName()).append("`=");
                    if (!(o instanceof Number)){
                        sql.append("'").append(o.toString()).append("'");
                    }else{
                        sql.append(o);
                    }

                    if (i + 1 < ds){
                        sql.append(" AND ");
                    }
                }

                sql.append(")");
            }
            sql.append(";");

            Statement s = manager.getConnection().createStatement();
            s.executeUpdate(sql.toString());
        }

        for (UUID uuid : set){
            SQLRow row = this.contents.get(uuid);
            row.removeAllData();
        }
    }


    public void save(){

    }

    //----------------------------------------------
    //               SEARCH UTILITY

    public List<UUID> search(String[] conditions){
        return null;
    }

    //----------------------------------------------
    //              TABLE MANAGEMENT

    public void addColumn(SQLColumn... columns){
        if (!isExists){
            for (SQLColumn c : columns){
                this.columns.put(c.getColumnName(), c);
            }
        }else{
            // Edit table with new column
        }
    }

    public void removeColumn(SQLColumn... columns){
        if (!isExists){
            for (SQLColumn c : columns){
                this.columns.remove(c.getColumnName());
            }
        }else{
            // Edit table with removed column
        }
    }

    //----------------------------------------------
    //                  UTILITY


    //----------------------------------------------
    //                 SETTER

    public void setPrimaryKeyColumn(SQLColumn primaryKeyColumn) {
        this.primaryKeyColumn = primaryKeyColumn;
    }

    //----------------------------------------------
    //                 GETTER

    public SQLColumn getPrimaryKeyColumn() {
        return primaryKeyColumn;
    }

    public String getTableName() {
        return tableName;
    }

    public HashMap<UUID, SQLRow> getContents() {
        return contents;
    }

    public MySQLManager getManager() {
        return manager;
    }

    public HashMap<String, SQLColumn> getColumns() {
        return columns;
    }
}
