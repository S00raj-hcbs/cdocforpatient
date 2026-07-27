package com.cybermed.cdoc_patient.doctor.doctorFilter;

/**
 * filters
 */
public class FilterCommunicator {

    private static String specialty, state, language;

    private FilterCommunicator() {
        specialty = state = language = "";
    }


    private static FilterCommunicator INSTANCE = null;

    public static FilterCommunicator getInstance() {
        if (INSTANCE == null)
            INSTANCE = new FilterCommunicator();
        return INSTANCE;
    }

    public String getSpecialty() {
        return specialty;
    }

    /**
     *
     * @param _specialty doc speciality
     */
    public void setSpecialty(String _specialty) {
        if (_specialty.equals("All")) {
            specialty = "";
        } else
            specialty = _specialty;
    }

    /**
     *
     * @return state
     */
    public String getState() {
        return state;
    }

    /**
     *
     * @param _state state
     */
    public void setState(String _state) {
        if (_state.equals("All")) {
            state = "";
        } else
            state = _state;
    }

    public String getLanguage() {
        return language;
    }

    /**
     *
     * @param _language doc language
     */
    public void setLanguage(String _language) {
        if (_language.equals("All")) {
            language = "";
        } else
            language = _language;
    }
}
