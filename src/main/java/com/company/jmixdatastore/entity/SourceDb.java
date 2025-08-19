
package com.company.jmixdatastore.entity;

import io.jmix.core.entity.annotation.JmixGeneratedValue;
import io.jmix.core.metamodel.annotation.InstanceName;
import io.jmix.core.metamodel.annotation.JmixEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@JmixEntity
@Table(name = "SOURCE_DB")
@Entity
public class SourceDb {
    @JmixGeneratedValue
    @Column(name = "ID", nullable = false)
    @Id
    private UUID id;

    @InstanceName
    @Column(name = "NAME", nullable = false)
    @NotNull
    private String name;

    @Column(name = "HOST")
    private String host;

    @Column(name = "URL")
    private String url;

    @Column(name = "PORT")
    private String port;

    @NotNull(message = "Username can not be blank !!")
    @Column(name = "USERNAME", nullable = false)
    private String username;

    @NotNull(message = "Password can not be blank !!")
    @Column(name = "PASSWORD", nullable = false)
    private String password;

    @Column(name = "DBNAME")
    private String dbname;

    @Column(name = "DATATYPE")
    private String dbtype;

    public void setPort(String port) {
        this.port = port;
    }

    public String getPort() {
        return port;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public DBType getDbtype() {
        return dbtype == null ? null : DBType.fromId(dbtype);
    }

    public void setDbtype(DBType datatype) {
        this.dbtype = datatype == null ? null : datatype.getId();
    }

    public String getDbname() {
        return dbname;
    }

    public void setDbname(String dbname) {
        this.dbname = dbname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

}
