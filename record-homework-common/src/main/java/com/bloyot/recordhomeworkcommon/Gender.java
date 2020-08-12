package com.bloyot.recordhomeworkcommon;

/**
 * The gender of the user for a record. In practice, for inclusivity, it is best to include options for none and other (and perhaps others), but
 * for example purposes I will use only male and female.
 */
public enum Gender {
    MALE("male"),
    FEMALE("female");

    private final String gender;

    private Gender(String gender) {
        this.gender = gender;
    }

    public String getGender() {
        return gender;
    }

    /**
     * Converts a string to a Gender enum if valid.
     * @param genderString - the string value to convert
     * @return the Gender enum value, or null if not a valid enum string
     */
    public static Gender toGender(String genderString) {
        if (MALE.gender.equalsIgnoreCase(genderString)) {
            return MALE;
        }
        if (FEMALE.gender.equalsIgnoreCase(genderString)) {
            return FEMALE;
        }
        return null;
    }

}
