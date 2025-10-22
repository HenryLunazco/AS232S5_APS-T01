package vg.edu.pe.HinoPE.model.enums;

public enum NotificationType {
    ALERT("alert"),
    MAINTENANCE("maintenance"),
    FUEL("fuel"),
    SYSTEM("system"),
    QUOTE("quote"),
    USER("user"),
    VEHICLE("vehicle"),
    SALE("sale");

    private final String value;

    NotificationType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static NotificationType fromValue(String value) {
        for (NotificationType type : NotificationType.values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid NotificationType: " + value);
    }
}
