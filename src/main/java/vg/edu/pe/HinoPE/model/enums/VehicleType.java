package vg.edu.pe.HinoPE.model.enums;

public enum VehicleType {
    CAMION("camion"),
    BUS("bus");

    private final String value;

    VehicleType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static VehicleType fromValue(String value) {
        for (VehicleType type : VehicleType.values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid VehicleType: " + value);
    }
}
