package com.smarttools.database;

public class TestConectDynamo {
    public static void main(String args[]) {
        System.out.println(DinamoDbContext.existsTable("USERS"));
    }
}
