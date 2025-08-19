package com.company.jmixdatastore.service.dbcon;

import com.company.jmixdatastore.entity.DBType;
import com.company.jmixdatastore.entity.SourceDb;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
@Service
public class DbConnectFactory {
    private final Map<DBType, DbConnect> map;

    public DbConnectFactory(List<DbConnect> implementations) {
        this.map = implementations.stream()
                .collect(Collectors.toMap(DbConnect::getSupportedDbType, Function.identity()));
    }

    public DbConnect get(SourceDb sourceDb) {
        return map.get(sourceDb.getDbtype());
    }
}
