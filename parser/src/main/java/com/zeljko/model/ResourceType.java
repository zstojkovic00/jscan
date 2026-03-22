package com.zeljko.model;

import java.util.Arrays;

public enum ResourceType {

    // JDBC
    CONNECTION("Connection"),
    STATEMENT("Statement"),
    PREPARED_STATEMENT("PreparedStatement"),
    CALLABLE_STATEMENT("CallableStatement"),
    RESULT_SET("ResultSet"),

    // IO
    INPUT_STREAM("InputStream"),
    OUTPUT_STREAM("OutputStream"),
    FILE_INPUT_STREAM("FileInputStream"),
    FILE_OUTPUT_STREAM("FileOutputStream"),
    BUFFERED_READER("BufferedReader"),
    BUFFERED_WRITER("BufferedWriter");

    private final String name;

    ResourceType(String name) {
        this.name = name;
    }

    public static boolean isResource(String type) {
        return Arrays.stream(values()).anyMatch(r -> r.name.equals(type));
    }
}
