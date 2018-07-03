package com.belosh.dbmanager.enity;

public class Database {
    private String host;
    private String databaseName;
    private String port;
    private String user;
    private String password;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Database database = (Database) o;

        if (host != null ? !host.equals(database.host) : database.host != null) return false;
        if (databaseName != null ? !databaseName.equals(database.databaseName) : database.databaseName != null)
            return false;
        if (port != null ? !port.equals(database.port) : database.port != null) return false;
        if (user != null ? !user.equals(database.user) : database.user != null) return false;
        return password != null ? password.equals(database.password) : database.password == null;
    }

    @Override
    public int hashCode() {
        int result = host != null ? host.hashCode() : 0;
        result = 31 * result + (databaseName != null ? databaseName.hashCode() : 0);
        result = 31 * result + (port != null ? port.hashCode() : 0);
        result = 31 * result + (user != null ? user.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Database{" +
                "host='" + host + '\'' +
                ", databaseName='" + databaseName + '\'' +
                ", port='" + port + '\'' +
                ", user='" + user + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
