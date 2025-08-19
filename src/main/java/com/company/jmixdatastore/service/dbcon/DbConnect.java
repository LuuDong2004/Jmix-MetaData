package com.company.jmixdatastore.service.dbcon;

import com.company.jmixdatastore.entity.SourceDb;

import com.company.jmixdatastore.entity.DBType;

import io.jmix.core.entity.KeyValueEntity;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;


public interface DbConnect {
    boolean connect(SourceDb sourceDb);
    List<String> loadTableList(SourceDb sourceDb);
    List<KeyValueEntity> loadTableFields(SourceDb sourceDb, String tableName);
    DBType getSupportedDbType();    
}
