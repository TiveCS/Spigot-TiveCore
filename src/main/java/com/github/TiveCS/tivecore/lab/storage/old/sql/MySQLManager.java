package com.github.TiveCS.tivecore.lab.storage.old.sql;

import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class MySQLManager {

    private Connection connection;

    private String host, username, password;
    private int port = 3306;

    private boolean useOldVersion = false, autoConnect = false;

    private String catalog = null;
    private HashMap<String, HashMap<String, SQLTable>> catalogTables = new HashMap<>(); // CATALOG=<TABLE NAME, TABLE>
    private SQLTable loadedTable = null;

    public MySQLManager(String host, int port, String username, String password, boolean useOldVersion, boolean autoConnect){
        this(host, port, username, password, useOldVersion, autoConnect, null);
    }

    public MySQLManager(String host, int port, String username, String password , boolean useOldVersion, boolean autoConnect, String catalog){
        this.host = host;
        this.username = username;
        this.password = password;
        this.port = port;

        this.catalog = catalog;

        this.autoConnect = autoConnect;
        this.useOldVersion = useOldVersion;

        if (autoConnect){
            try {
                openConnection();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    //-------------------------------------------

    public SQLRow insertInto(String catalog, String tableName, HashMap<String, Object> columnValues, boolean update) throws SQLException {
        if (connection != null && !connection.isClosed()){
            SQLTable selectedTable = getTable(catalog, tableName);
            return insertInto(selectedTable, columnValues, update);
        }
        return null;
    }
    public SQLRow insertInto(SQLTable table, HashMap<String, Object> columnValues, boolean update) throws SQLException {
        if (connection != null && !connection.isClosed() && table != null){
            return table.insert(columnValues, update);
        }
        return null;
    }

    public void update(String catalog, String tableName, HashMap<String, Object> columnValues, List<Object> conditionValues, List<SQLCondition> conditions, List<SQLColumn> columnCondition, boolean update) throws SQLException {
        if (connection != null && !connection.isClosed()){
            SQLTable selectedTable = getTable(catalog, tableName);
            update(selectedTable, columnValues, conditionValues, conditions, columnCondition, update);
        }
    }
    public void update(SQLTable table, HashMap<String, Object> columnValues, List<Object> conditionValues, List<SQLCondition> conditions, List<SQLColumn> columnCondition, boolean update) throws SQLException {
        if (connection != null && !connection.isClosed() && table != null){
            table.update(columnValues, conditionValues, conditions, columnCondition, update);
        }
    }
    public void update(SQLTable table, HashMap<String, Object> columnValues, List<UUID> targetRows, boolean update) throws SQLException {
        if (connection != null && !connection.isClosed() && table != null){
            table.update(columnValues, targetRows, update);
        }
    }

    public void delete(String catalog, String tableName, List<Object> conditionValues, List<SQLCondition> conditions, List<SQLColumn> columnCondition, boolean update) throws SQLException {
        if (connection != null && !connection.isClosed()){
            getTable(catalog, tableName).delete(conditionValues, conditions, columnCondition, update);
        }
    }
    public void delete(SQLTable table, List<Object> conditionValues, List<SQLCondition> conditions, List<SQLColumn> columnCondition, boolean update) throws SQLException {
        if (connection != null && !connection.isClosed()){
            table.delete(conditionValues, conditions, columnCondition, update);
        }
    }
    public void delete(SQLTable table, List<UUID> targetRows, boolean update) throws SQLException {
        if (connection != null && !connection.isClosed()){
            table.delete(targetRows, update);
        }
    }

    //-------------------------------------------

    public void openConnection() throws ClassNotFoundException, SQLException {
        if (connection != null && !connection.isClosed()){
            return;
        }

        synchronized (this) {
            if (useOldVersion) {
                Class.forName("com.mysql.jdbc.Driver");
            }else{
                Class.forName("com.mysql.cj.jdbc.Driver");
            }

            connection = DriverManager.getConnection(generateConnectionUrl(), username, password);
            if (this.catalog != null) {
                changeCatalog(this.catalog);
            }
        }
    }

    public void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()){
            connection.close();
        }
    }

    public String generateConnectionUrl(){
        StringBuilder b = new StringBuilder();

        b.append("jdbc:mysql://").append(host).append(":").append(port).append("/");
        if (this.catalog != null){
            b.append(catalog);
        }

        return b.toString();
    }

    //-------------------------------------------

    public void changeCatalog(String newCatalog) throws SQLException {
        if (connection != null && !connection.isClosed()){
            connection.setCatalog(newCatalog);
            this.catalog = connection.getCatalog();
        }
    }

    public void changeTable(String table){
        changeTable(this.catalog, table);
    }
    public void changeTable(String catalog, String table){
        if (catalog == null || table == null){
            return;
        }

        if (catalogTables.containsKey(catalog)){
            HashMap<String, SQLTable> map = catalogTables.get(catalog);
            if (map.containsKey(table)){
                this.loadedTable = map.get(table);
            }
        }
    }

    public void loadCatalogTables() throws SQLException {
        if (connection != null && !connection.isClosed() && catalog != null){
            DatabaseMetaData mt = connection.getMetaData();
            ResultSet resultTable = mt.getTables(catalog, null, "%", new String[]{"TABLE"});

            HashMap<String, SQLTable> data = catalogTables.getOrDefault(catalog, new HashMap<>());
            while(resultTable.next()){
                String t = resultTable.getString(3);

                SQLTable table = new SQLTable(this, catalog, t, true);
                table.loadAllData();

                data.put(table.getTableName(), table);
            }

            catalogTables.put(catalog, data);
        }
    }

    //-------------------------------------------

    public void createTable(SQLTable table) throws SQLException {
        if (connection != null && !connection.isClosed()){

            StringBuilder sql = table.generateCreateTableQuery();

            Statement s = connection.createStatement();
            s.executeUpdate(sql.toString());

        }
    }

    public void dropTable(String tableName) throws SQLException {
        if (connection != null && !connection.isClosed()){
            StringBuilder sql = new StringBuilder("DROP TABLE IF EXISTS ").append(tableName);
            connection.createStatement().executeUpdate(sql.toString());
        }
    }

    public void truncateTable(String tableName) throws SQLException{
        if (connection != null && !connection.isClosed()){
            StringBuilder sql = new StringBuilder("TRUNCATE TABLE IF EXISTS ").append(tableName);
            connection.createStatement().executeUpdate(sql.toString());
        }
    }

    //-------------------------------------------
    //                  UTILITY

    public SQLTable getTable(String catalog, String tableName){
        SQLTable t = null;

        if (catalogTables.containsKey(catalog)){
            HashMap<String, SQLTable> ct = catalogTables.get(catalog);
            if (ct.containsKey(tableName)){
                return ct.get(tableName);
            }
        }

        return t;
    }

    public HashMap<String, Object> convertCatalogTablesToMap(){
        HashMap<String, Object> map = new HashMap<>();

        for (String catalog : catalogTables.keySet()){
            HashMap<String, SQLTable> tables = catalogTables.get(catalog);
            for (String tableName : tables.keySet()){
                SQLTable table = tables.get(tableName);
                HashMap<UUID, SQLRow> rows = table.getContents();
                for (UUID uuid : rows.keySet()) {
                    SQLRow row = rows.get(uuid);
                    StringBuilder path = new StringBuilder(catalog).append(".").append(tableName).append(".").append(uuid.toString());

                    map.put(path.toString(), row);
                }
            }
        }

        return map;
    }

    public HashMap<String, Object> convertTableToMap(SQLTable table){
        HashMap<String, Object> map = new HashMap<>();

        if (table != null){
            HashMap<UUID, SQLRow> contents = table.getContents();
            for (UUID uuid : contents.keySet()){
                StringBuilder path = new StringBuilder(table.getTableName()).append(".").append(table.getTableName()).append(".").append(uuid.toString());
                map.put(path.toString(), contents.get(uuid));
            }
        }

        return map;
    }

    //-------------------------------------------

    public void setHost(String host) {
        this.host = host;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setUseOldVersion(boolean useOldVersion) {
        this.useOldVersion = useOldVersion;
    }

    //-------------------------------------------

    public String getCatalog() {
        return catalog;
    }

    public SQLTable getLoadedTable() {
        return loadedTable;
    }

    public HashMap<String, HashMap<String, SQLTable>> getCatalogTables() {
        return catalogTables;
    }

    public Connection getConnection() {
        return connection;
    }
}
