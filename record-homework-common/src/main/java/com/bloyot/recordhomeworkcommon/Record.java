package com.bloyot.recordhomeworkcommon;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Data class representing a single record.
 * Note: in practice I would typically use lombok for classes like this (https://projectlombok.org/) as I find
 * it is extremely helpful in removing verbosity (reduces this class to basically a single annotation),
 * but for demonstrative purposes I will leave it out since it requires a plugin to work.
 */
public class Record {
    // the expected date format for the record
    public static final String DATE_FORMAT_STRING = "MM/dd/yyyy";
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DATE_FORMAT_STRING);

    /**
     * The persons last name
     */
    private String lastName;

    /**
     * The persons first name.
     */
    private String firstName;

    /**
     * The gender of the person in the record.
     */
    private Gender gender;

    /**
     * The person's favorite color as a string. If a fixed set of values was preferred this could be an enum, but will leave as a string
     * for flexibility purposes.
     */
    private String favoriteColor;

    /**
     * The date of birth for the person in the record. Stored as a local date since we don't have time or timezone information.
     */
    private LocalDate dateOfBirth;

    public Record(String lastName, String firstName, Gender gender, String favoriteColor, LocalDate dateOfBirth) {
        this.lastName = lastName;
        this.firstName = firstName;
        this.gender = gender;
        this.favoriteColor = favoriteColor;
        this.dateOfBirth = dateOfBirth;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public String getFavoriteColor() {
        return favoriteColor;
    }

    public void setFavoriteColor(String favoriteColor) {
        this.favoriteColor = favoriteColor;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Record record = (Record) o;
        return Objects.equals(lastName, record.lastName) &&
                Objects.equals(firstName, record.firstName) &&
                gender == record.gender &&
                Objects.equals(favoriteColor, record.favoriteColor) &&
                Objects.equals(dateOfBirth, record.dateOfBirth);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lastName, firstName, gender, favoriteColor, dateOfBirth);
    }

    @Override
    public String toString() {
        // display format was not specified as far as I can tell, so just always output in csv
        return lastName + "," + firstName + "," + gender.toString().toLowerCase() + "," + favoriteColor + "," + DATE_TIME_FORMATTER.format(dateOfBirth);
    }
}
