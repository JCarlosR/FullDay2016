package com.youtube.sorcjc.fullday2016.model;

import com.google.gson.annotations.SerializedName;

public class Speaker {
/*
{
    "id":1,
    "name":"Mg. Karla Vanessa Barreto Stein",
    "company":"Advisory Services de EY",
    "position":"Gerente",
    "email":"karlabarretostein@gmail.com",
    "profile":"https:\/\/pe.linkedin.com\/in\/mg-karla-vanessa-barreto-stein-72b2a674",
    "image":"1.jpg",
    "description":"Manager de la divisi\u00f3n de Advisory Services de EY. Magister en Direcci\u00f3n y Gesti\u00f3n de Tecnolog\u00edas de la Informaci\u00f3n en la Universidad Nacional Mayor de San Marcos. Cuenta con m\u00e1s de 9 a\u00f1os de experiencia en los campos de Direcci\u00f3n y Gesti\u00f3n de Proyectos de Consultor\u00eda, Auditor\u00eda, Seguridad y TI.",
    "enable":1,
    "created_at":"2016-11-07 21:45:53",
    "updated_at":"2016-11-07 21:45:53"
}
*/

    private int id;
    private String name;
    private String company;
    private String position;
    private String email;
    private String image;
    private String description;

    @SerializedName("first_paper")
    private Paper firstPaper;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Paper getFirstPaper() {
        return firstPaper;
    }

    public void setFirstPaper(Paper firstPaper) {
        this.firstPaper = firstPaper;
    }
}
