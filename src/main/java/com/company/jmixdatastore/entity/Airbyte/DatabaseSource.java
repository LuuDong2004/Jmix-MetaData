package com.company.jmixdatastore.entity.Airbyte;
import com.company.jmixdatastore.entity.DBType;
import io.jmix.core.metamodel.annotation.JmixEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@JmixEntity
@Entity
@DiscriminatorValue("DATABASE")
@PrimaryKeyJoinColumn(name = "ID")
@Table(name = "DATABASE_SOURCE")
public class DatabaseSource extends Source {

    @NotNull
    @Column(name = "DB_TYPE")
    private String dbType;

    @NotNull
    @Column(name = "HOST")
    private String host;

    @NotNull
    @Column(name = "PORT")
    private Long port;

    @NotNull
    @Column(name = "DATABASE_")
    private String database;

    @NotNull
    @Column(name = "USERNAME")
    private String username;

    @NotNull
    @Column(name = "PASSWORD")
    private String password;

    @Column(name = "TUNNEL_CONFIG_JSON")
    private String tunnelConfigJson;

    @Column(name = "REPLICATION_JSON")
    private String replicationJson;

    @Column(name = "SSL_CONFIG_JSON")
    private String sslConfigJson;

    public String getTunnelConfigJson() {
        return tunnelConfigJson;
    }

    public void setTunnelConfigJson(String tunnelConfigJson) {
        this.tunnelConfigJson = tunnelConfigJson;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSslConfigJson() {
        return sslConfigJson;
    }

    public void setSslConfigJson(String sslConfigJson) {
        this.sslConfigJson = sslConfigJson;
    }

    public String getReplicationJson() {
        return replicationJson;
    }

    public void setReplicationJson(String replicationJson) {
        this.replicationJson = replicationJson;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public Long getPort() {
        return port;
    }

    public void setPort(Long port) {
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public DBType getDbType() {
        return dbType == null ? null : DBType.fromId(dbType);
    }

    public void setDbType(DBType dbType) {
        this.dbType = dbType == null ? null : dbType.getId();
    }

}