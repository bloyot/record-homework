package com.bloyot.recordhomeworkapi;

/**
 * Class that indicates how to create a record, providing the delimiter and the data
 */
public class CreateRecordCommand {

    private String data;
    private String delimiter;

    public CreateRecordCommand(String data, String delimiter) {
        this.data = data;
        this.delimiter = delimiter;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getDelimiter() {
        return delimiter;
    }

    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }
}
