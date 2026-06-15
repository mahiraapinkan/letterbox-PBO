package model;

public class SuratMasuk extends Surat {
    private static final long serialVersionUID = 1L;
    
    private String tanggalMasuk;
    private String pengirim;

    public SuratMasuk(String idSurat, String nomorSurat, String tanggalSurat, String tanggalMasuk, String pengirim, String perihal, String keterangan) {
        super(idSurat, nomorSurat, tanggalSurat, perihal, keterangan);
        this.tanggalMasuk = tanggalMasuk;
        this.pengirim = pengirim;
    }

    public String getTanggalMasuk() {
        return tanggalMasuk;
    }

    public void setTanggalMasuk(String tanggalMasuk) {
        this.tanggalMasuk = tanggalMasuk;
    }

    public String getPengirim() {
        return pengirim;
    }

    public void setPengirim(String pengirim) {
        this.pengirim = pengirim;
    }

    @Override
    public String getTanggalTransaksi() {
        return tanggalMasuk;
    }

    @Override
    public String getStakeholder() {
        return pengirim;
    }

    @Override
    public String getJenisSurat() {
        return "Surat Masuk";
    }
}
