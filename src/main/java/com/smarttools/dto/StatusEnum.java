package com.smarttools.dto;

public enum StatusEnum {

    EnProceso(0, "En Proceso"),
    Disponible(1, "Disponible"),
    Nuevo(2, "Nuevo");


    private String name;
    private int id;

    public int getId() {
        return id;
    }

    StatusEnum(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return this.name;
    }


}
