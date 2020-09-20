package org.stressfoot.stress.sense.models;

public class Connection {
    private String connectionType;
    private String  address;

    public void setConnectionType(String connectionType) {
        this.connectionType = connectionType;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getConnectionType() {
        return connectionType;
    }

    public String getAddress() {
        return address;
    }
}
