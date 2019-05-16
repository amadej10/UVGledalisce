package com.example.amadej.gledaliscecheckin;

public class Gledalec {
    private String ime;
    private String priimek;
    private String vrsta;
    private String sedez;
    private String telefon;
    private String email;
    private int id;
    private int stevilo_obiskov;

    /**
     * @param ime
     * @param priimek
     * @param vrsta
     * @param sedez
     */
    public Gledalec(String ime, String priimek, String vrsta, String sedez) {
        this.ime = ime;
        this.priimek = priimek;
        this.vrsta = vrsta;
        this.sedez = sedez;
    }
    /**
     * @param ime
     * @param priimek
     * @param vrsta
     * @param sedez
     * @param telefon
     * @param email
     */
    public Gledalec(String ime, String priimek, String vrsta, String sedez, String telefon, String email) {
        this.ime = ime;
        this.priimek = priimek;
        this.vrsta = vrsta;
        this.sedez = sedez;
        this.telefon = telefon;
        this.email = email;

    }

    /**
     * @param id
     * @param ime
     * @param priimek
     * @param vrsta
     * @param sedez
     * @param stevilo_obiskov
     */
    public Gledalec(int id, String ime, String priimek, String vrsta, String sedez, int stevilo_obiskov) {
        this.ime = ime;
        this.priimek = priimek;
        this.vrsta = vrsta;
        this.sedez = sedez;
        this.id = id;
        this.stevilo_obiskov = stevilo_obiskov;
    }

    public String getIme() {
        return ime;
    }

    public void setIme(String ime) {
        this.ime = ime;
    }

    public String getPriimek() {
        return priimek;
    }

    public void setPriimek(String priimek) {
        this.priimek = priimek;
    }

    public String getVrsta() {
        return vrsta;
    }

    public void setVrsta(String vrsta) {
        this.vrsta = vrsta;
    }

    public String getSedez() {
        return sedez;
    }

    public void setSedez(String sedez) {
        this.sedez = sedez;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStevilo_obiskov() {
        return stevilo_obiskov;
    }

    public void setStevilo_obiskov(int stevilo_obiskov) {
        this.stevilo_obiskov = stevilo_obiskov;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefon() {
        return telefon;
    }

    public void setTelefon(String telefon) {
        this.telefon = telefon;
    }
}
