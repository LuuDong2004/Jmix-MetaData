package com.company.jmixdatastore.dbconnect.impl;

import com.company.jmixdatastore.dbconnect.DbDriverAdapter;
import com.company.jmixdatastore.entity.DBType;
import com.company.jmixdatastore.entity.SourceDb;
import org.springframework.stereotype.Component;

@Component

public class PostgresAdapter implements DbDriverAdapter {
    @Override
    public DBType getSupportedDbType() {
        return DBType.POSTGRES;
    }

    @Override
    public String buildJdbcUrl(SourceDb sourceDb) {
        return String.format("jdbc:postgresql://%s:%s/%s",
                sourceDb.getHost(), sourceDb.getPort(), sourceDb.getDbname());

    }

    @Override
    public String getDriverClassName() {
        return "org.postgresql.Driver";
    }
}
