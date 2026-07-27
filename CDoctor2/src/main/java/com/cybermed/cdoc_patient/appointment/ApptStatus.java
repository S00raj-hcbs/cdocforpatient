package com.cybermed.cdoc_patient.appointment;

public enum ApptStatus {
    All("-1", "All"),
    Completed("2", "Completed"),
    Billed("3", "Billed"),
    InWaitingRoom("6", "In Waiting Room"),
    Cancelled("1", "Cancelled"),
    BookedAppointment("0", "Booked Appointment");

    private String code;
    private String name;

    ApptStatus(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public static ApptStatus create(String name) {
        switch (name) {
            case "Completed":
                return Completed;
            case "Billed":
                return Billed;
            case "In Waiting Room":
                return InWaitingRoom;
            case "Cancelled":
                return Cancelled;
            case "Booked Appointment":
                return BookedAppointment;
            default:
                return All;
        }
    }

}
