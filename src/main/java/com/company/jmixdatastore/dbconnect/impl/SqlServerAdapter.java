package com.company.jmixdatastore.dbconnect.impl;

import com.company.jmixdatastore.dbconnect.DbDriverAdapter;
import com.company.jmixdatastore.dbconnect.DbDriverAdapter;
import com.company.jmixdatastore.entity.DBType;
import com.company.jmixdatastore.entity.SourceDb;
import org.springframework.stereotype.Component;

@Component
public class SqlServerAdapter implements DbDriverAdapter {
    @Override
    public DBType getSupportedDbType() {
        return DBType.SQLSERVER;
    }

    @Override
    public String buildJdbcUrl(SourceDb sourceDb) {
        return String.format(
                "jdbc:sqlserver://%s:%s;databaseName=%s;encrypt=true;trustServerCertificate=true;",
                sourceDb.getHost(),
                sourceDb.getPort(),
                sourceDb.getDbname()
        );
    }


    @Override
    public String getDriverClassName() {
        return "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    }
}
