package com.bloyot.recordhomeworkcli;

public enum SortType {
    GENDER("gender"),
    BIRTH_DATE("birth_date"),
    LAST_NAME("last_name");

    private final String sortType;

    private SortType(String sortType) {
        this.sortType = sortType;
    }

    public String getSortType() {
        return sortType;
    }

    /**
     * Converts a string to a SortType enum if valid.
     * @param sortTypeString - the string value to convert
     * @return the SortType enum value, or null if not a valid enum string
     */
    public static SortType toSortType(String sortTypeString) {
        if (GENDER.sortType.equalsIgnoreCase(sortTypeString)) {
            return GENDER;
        }
        if (BIRTH_DATE.sortType.equalsIgnoreCase(sortTypeString)) {
            return BIRTH_DATE;
        }
        if (LAST_NAME.sortType.equalsIgnoreCase(sortTypeString)) {
            return LAST_NAME;
        }
        return null;
    }
}
