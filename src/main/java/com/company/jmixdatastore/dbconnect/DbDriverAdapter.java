package com.company.jmixdatastore.dbconnect;

import com.company.jmixdatastore.entity.DBType;
import com.company.jmixdatastore.entity.SourceDb;

public interface DbDriverAdapter {
    DBType getSupportedDbType();

    String buildJdbcUrl(SourceDb sourceDb);

    String getDriverClassName();
}
