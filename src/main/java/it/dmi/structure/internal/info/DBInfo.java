package it.dmi.structure.internal.info;

public record DBInfo(String driverName, String url, String user, String password,
                     String sqlScript) implements Info {

    @Override
    public String driverName() {
        return driverName;
    }
}
