package com.example.amadej.gledaliscecheckin;

public class Predstava {
    private int id_predstave;
    private String ime_predstave;

    public Predstava(String ime_predstave) {
        this.ime_predstave = ime_predstave;
    }

    public Predstava(int id_predstave, String ime_predstave) {
        this.id_predstave = id_predstave;
        this.ime_predstave = ime_predstave;
    }

    public long getId_predstave() {
        return id_predstave;
    }

    public void setId_predstave(int id_predstave) {
        this.id_predstave = id_predstave;
    }

    public String getIme_predstave() {
        return ime_predstave;
    }

    public void setIme_predstave(String ime_predstave) {
        this.ime_predstave = ime_predstave;
    }
}
