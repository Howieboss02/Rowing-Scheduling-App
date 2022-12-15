package nl.tudelft.sem.template.shared.enums;

public enum MicroservicePorts {
    AUTHENTICATION("8081"),
    EVENT("8083"),
    USER("8084");

    public final String port;

    MicroservicePorts(String port) {
        this.port = port;
    }
}
