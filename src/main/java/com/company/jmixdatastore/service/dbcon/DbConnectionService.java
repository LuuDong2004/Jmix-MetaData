package com.company.jmixdatastore.service.dbcon;

import com.company.jmixdatastore.dbconnect.DbDriverAdapter;
import com.company.jmixdatastore.entity.SourceDb;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
@Service

public class DbConnectionService {
    @Autowired
    private DbDriverAdapterFactory adapterFactory;

    public Connection getConnection(SourceDb sourceDb) throws SQLException {
        DbDriverAdapter adapter = adapterFactory.getAdapter(sourceDb.getDbtype());
        String url = adapter.buildJdbcUrl(sourceDb);

        try {
            Class.forName(adapter.getDriverClassName()); // cần nếu không dùng Spring Boot datasource
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Driver not found", e);
        }

        return DriverManager.getConnection(url, sourceDb.getUsername(), sourceDb.getPassword());
    }
}
