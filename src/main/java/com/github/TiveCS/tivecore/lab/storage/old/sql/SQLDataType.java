package com.github.TiveCS.tivecore.lab.storage.old.sql;

import java.util.Arrays;
import java.util.Collection;

public enum SQLDataType {

    BIGINT, BINARY, BLOB, CHAR, DATE,
    DATETIME, DECIMAL,
    DOUBLE, ENUM,
    FLOAT, INT, JSON,
    MEDIUMINT, NUMERIC,
    SET, SMALLINT,
    TEXT, TIME,
    TIMESTAMP, TINYINT,
    VARBINARY, VARCHAR, YEAR;

    public static final Collection<SQLDataType> getNumberType = Arrays.asList(
            TINYINT, SMALLINT, MEDIUMINT, INT, BIGINT,
            DECIMAL, DOUBLE, FLOAT, NUMERIC
    );

    public static final Collection<SQLDataType> getIntegerType = Arrays.asList(
            TINYINT, SMALLINT, MEDIUMINT, INT, BIGINT, NUMERIC
    );

    public static final Collection<SQLDataType> getDecimalsType = Arrays.asList(
            DECIMAL, DOUBLE, FLOAT
    );

    public static final Collection<SQLDataType> getTextType = Arrays.asList(
            CHAR, TEXT, VARCHAR
    );

    public static final Collection<SQLDataType> getTimeType = Arrays.asList(
            TIME, TIMESTAMP, YEAR
    );

    public static final Collection<SQLDataType> getDateType = Arrays.asList(
            DATE, DATETIME
    );


}
