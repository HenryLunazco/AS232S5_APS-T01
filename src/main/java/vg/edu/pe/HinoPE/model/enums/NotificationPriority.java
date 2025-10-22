package vg.edu.pe.HinoPE.model.enums;

public enum NotificationPriority {
    ALTA("alta"),
    MEDIA("media"),
    BAJA("baja");

    private final String value;

    NotificationPriority(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static NotificationPriority fromValue(String value) {
        for (NotificationPriority priority : NotificationPriority.values()) {
            if (priority.value.equalsIgnoreCase(value)) {
                return priority;
            }
        }
        throw new IllegalArgumentException("Invalid NotificationPriority: " + value);
    }
}
