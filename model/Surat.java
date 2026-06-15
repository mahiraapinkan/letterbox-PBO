package model;

import java.io.Serializable;

public abstract class Surat implements Serializable {
    private static final long serialVersionUID = 1L;

    protected String idSurat;
    protected String nomorSurat;
    protected String tanggalSurat;
    protected String perihal;
    protected String keterangan;

    public Surat(String idSurat, String nomorSurat, String tanggalSurat,
            String perihal, String keterangan) {
        this.idSurat = idSurat;
        this.nomorSurat = nomorSurat;
        this.tanggalSurat = tanggalSurat;
        this.perihal = perihal;
        this.keterangan = keterangan;
    }

    public String getIdSurat() {
        return idSurat;
    }

    public void setIdSurat(String idSurat) {
        this.idSurat = idSurat;
    }

    public String getNomorSurat() {
        return nomorSurat;
    }

    public void setNomorSurat(String nomorSurat) {
        this.nomorSurat = nomorSurat;
    }

    public String getTanggalSurat() {
        return tanggalSurat;
    }

    public void setTanggalSurat(String tanggalSurat) {
        this.tanggalSurat = tanggalSurat;
    }

    public String getPerihal() {
        return perihal;
    }

    public void setPerihal(String perihal) {
        this.perihal = perihal;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }

    public abstract String getTanggalTransaksi();

    public abstract String getStakeholder();

    public abstract String getJenisSurat();
}
