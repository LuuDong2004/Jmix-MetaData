package com.company.jmixdatastore.dbconnect.impl;

import com.company.jmixdatastore.dbconnect.DbDriverAdapter;
import com.company.jmixdatastore.entity.DBType;
import com.company.jmixdatastore.entity.SourceDb;

import org.springframework.stereotype.Component;

@Component
public class MySQLAdapter implements DbDriverAdapter {
    @Override
    public DBType getSupportedDbType() {
        return DBType.MYSQL;
    }

    @Override
    public String buildJdbcUrl(SourceDb sourceDb) {
        return String.format("jdbc:mysql://%s:%s/%s?useSSL=false&serverTimezone=UTC&nullCatalogMeansCurrent=true&useInformationSchema=true",
                sourceDb.getHost(), sourceDb.getPort(), sourceDb.getDbname());
    }

    @Override
    public String getDriverClassName() {
        return "com.mysql.cj.jdbc.Driver";
    }
}
