package manager;

import model.Surat;
import model.SuratMasuk;
import model.SuratKeluar;

import java.util.ArrayList;
import java.util.List;

public class PengelolaSurat {

    private List<SuratMasuk> daftarSuratMasuk;
    private List<SuratKeluar> daftarSuratKeluar;

    public PengelolaSurat() {
        daftarSuratMasuk = new ArrayList<>();
        daftarSuratKeluar = new ArrayList<>();

        // DATA DUMMY SURAT MASUK
        daftarSuratMasuk.add(
                new SuratMasuk(
                        "SM001",
                        "001/SM/2026",
                        "01-06-2026",
                        "02-06-2026",
                        "Fakultas Teknik",
                        "Undangan Rapat",
                        "-"));

        daftarSuratMasuk.add(
                new SuratMasuk(
                        "SM002",
                        "002/SM/2026",
                        "05-06-2026",
                        "06-06-2026",
                        "BEM",
                        "Permohonan Izin",
                        "-"));
        daftarSuratMasuk.add(
                new SuratMasuk(
                        "SM003",
                        "003/SM/2026",
                        "12-06-2026",
                        "13-06-2026",
                        "Universitas Indonesia",
                        "Permohonan Kerjasama",
                        "-"));

        daftarSuratMasuk.add(
                new SuratMasuk(
                        "SM004",
                        "004/SM/2026",
                        "15-06-2026",
                        "16-06-2026",
                        "PT Maju Jaya",
                        "Penawaran Kerjasama",
                        "-"));

        daftarSuratMasuk.add(
                new SuratMasuk(
                        "SM005",
                        "005/SM/2026",
                        "18-06-2026",
                        "19-06-2026",
                        "Dinas Pendidikan",
                        "Surat Edaran",
                        "Penting"));

        // DATA DUMMY SURAT KELUAR
        daftarSuratKeluar.add(
                new SuratKeluar(
                        "SK001",
                        "001/SK/2026",
                        "03-06-2026",
                        "04-06-2026",
                        "Dekan",
                        "Pemberitahuan",
                        "-"));

        daftarSuratKeluar.add(
                new SuratKeluar(
                        "SK002",
                        "002/SK/2026",
                        "10-06-2026",
                        "11-06-2026",
                        "Mahasiswa",
                        "Undangan Seminar",
                        "-"));
        daftarSuratKeluar.add(
                new SuratKeluar(
                        "SK003",
                        "003/SK/2026",
                        "14-06-2026",
                        "15-06-2026",
                        "PT Sejahtera",
                        "Balasan Kerjasama",
                        "-"));

        daftarSuratKeluar.add(
                new SuratKeluar(
                        "SK004",
                        "004/SK/2026",
                        "17-06-2026",
                        "18-06-2026",
                        "Universitas Indonesia",
                        "Persetujuan Kegiatan",
                        "-"));

        daftarSuratKeluar.add(
                new SuratKeluar(
                        "SK005",
                        "005/SK/2026",
                        "20-06-2026",
                        "21-06-2026",
                        "Dinas Pendidikan",
                        "Laporan Kegiatan",
                        "-"));
    }

    public void loadData() {
        // Kosongkan dulu, jangan isi apa-apa
    }

    public List<SuratMasuk> getDaftarSuratMasuk() {
        return daftarSuratMasuk;
    }

    public List<SuratKeluar> getDaftarSuratKeluar() {
        return daftarSuratKeluar;
    }

    public String generateNextId(boolean suratMasuk) {
        if (suratMasuk) {
            return "SM" + String.format("%03d", daftarSuratMasuk.size() + 1);
        } else {
            return "SK" + String.format("%03d", daftarSuratKeluar.size() + 1);
        }
    }

    public void tambahSurat(Surat surat) {
        if (surat instanceof SuratMasuk) {
            daftarSuratMasuk.add((SuratMasuk) surat);
        } else if (surat instanceof SuratKeluar) {
            daftarSuratKeluar.add((SuratKeluar) surat);
        }
    }

    public void editSurat(String idSurat, Surat suratBaru) {

        if (suratBaru instanceof SuratMasuk) {
            for (int i = 0; i < daftarSuratMasuk.size(); i++) {
                if (daftarSuratMasuk.get(i).getIdSurat().equals(idSurat)) {
                    daftarSuratMasuk.set(i, (SuratMasuk) suratBaru);
                    return;
                }
            }
        }

        if (suratBaru instanceof SuratKeluar) {
            for (int i = 0; i < daftarSuratKeluar.size(); i++) {
                if (daftarSuratKeluar.get(i).getIdSurat().equals(idSurat)) {
                    daftarSuratKeluar.set(i, (SuratKeluar) suratBaru);
                    return;
                }
            }
        }
    }

    public void hapusSurat(String idSurat) {
        daftarSuratMasuk.removeIf(s -> s.getIdSurat().equals(idSurat));
        daftarSuratKeluar.removeIf(s -> s.getIdSurat().equals(idSurat));
    }
}