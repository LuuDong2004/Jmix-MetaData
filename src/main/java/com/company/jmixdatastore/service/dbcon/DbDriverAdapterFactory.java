package com.company.jmixdatastore.service.dbcon;

import com.company.jmixdatastore.dbconnect.DbDriverAdapter;
import org.springframework.stereotype.Service;

import com.company.jmixdatastore.entity.DBType;


import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class DbDriverAdapterFactory {
    private final Map<DBType, DbDriverAdapter> adapterMap;

    public DbDriverAdapterFactory(List<DbDriverAdapter> adapters) {
        this.adapterMap = adapters.stream()
                .collect(Collectors.toMap(DbDriverAdapter::getSupportedDbType, Function.identity()));
    }

    public DbDriverAdapter getAdapter(DBType type) {
        return Optional.ofNullable(adapterMap.get(type))
                .orElseThrow(() -> new IllegalArgumentException("Unsupported DB type: " + type));
    }
}
