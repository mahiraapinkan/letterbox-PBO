package model;

public class SuratKeluar extends Surat {
    private static final long serialVersionUID = 1L;
    
    private String tanggalKeluar;
    private String penerima;

    public SuratKeluar(String idSurat, String nomorSurat, String tanggalSurat, String tanggalKeluar, String penerima, String perihal, String keterangan) {
        super(idSurat, nomorSurat, tanggalSurat, perihal, keterangan);
        this.tanggalKeluar = tanggalKeluar;
        this.penerima = penerima;
    }

    public String getTanggalKeluar() {
        return tanggalKeluar;
    }

    public void setTanggalKeluar(String tanggalKeluar) {
        this.tanggalKeluar = tanggalKeluar;
    }

    public String getPenerima() {
        return penerima;
    }

    public void setPenerima(String penerima) {
        this.penerima = penerima;
    }

    @Override
    public String getTanggalTransaksi() {
        return tanggalKeluar;
    }

    @Override
    public String getStakeholder() {
        return penerima;
    }

    @Override
    public String getJenisSurat() {
        return "Surat Keluar";
    }
}
