package view;

import model.Surat;
import model.SuratMasuk;
import model.SuratKeluar;
import manager.PengelolaSurat;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class AppFrame extends JFrame {
    private final PengelolaSurat pengelola;
    private boolean isSuratMasukActive = true; // true: Surat Masuk, false: Surat Keluar

    // Colors
    private final Color primaryColor = new Color(21, 101, 216); // Royal Blue
    private final Color hoverColor = new Color(229, 231, 235); // Light Gray
    private final Color sidebarBg = new Color(243, 244, 246); // Off-white Gray
    private final Color mainBg = Color.WHITE;
    private final Color borderGray = new Color(209, 213, 219);
    private final Color textDark = new Color(17, 24, 39);
    private final Color textGray = new Color(107, 114, 128);

    // Sidebar Buttons
    private JButton btnSuratMasuk;
    private JButton btnSuratKeluar;
    private JButton btnExportPdf;
    private JButton btnKeluar;

    // Main Components
    private JLabel lblTitle;
    private JButton btnTambah;
    private JButton btnEdit;
    private JButton btnHapus;
    private JButton btnRefresh;
    private JTextField txtSearch;

    // Filter Components
    private JLabel lblFilterStakeholder;
    private JComboBox<String> cbFilterStakeholder;
    private JComboBox<String> cbFilterBulan;
    private JComboBox<String> cbFilterTahun;
    private JComboBox<String> cbSortTanggal;

    // Table
    private JTable table;
    private DefaultTableModel tableModel;
    private List<Surat> filteredSuratList; // Current filtered & sorted list

    // Detail Panel Value Labels
    private JLabel lblValId;
    private JLabel lblValNomor;
    private JLabel lblValTglSurat;
    private JLabel lblValTglTransaksi;
    private JLabel lblTglTransaksiTitle;
    private JLabel lblValStakeholder;
    private JLabel lblStakeholderTitle;
    private final JLabel lblValPerihal;
    private final JLabel lblValKeterangan;

    // Footer Labels
    private JLabel lblTotalData;
    private JLabel lblDateTime;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    public AppFrame(PengelolaSurat pengelola) {
        this.pengelola = pengelola;
        this.filteredSuratList = new ArrayList<>();

        setTitle("Aplikasi Pengelolaan Surat");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Initialize UI Elements
        lblValPerihal = new JLabel("-");
        lblValKeterangan = new JLabel("-");

        // 1. Sidebar (West)
        add(createSidebarPanel(), BorderLayout.WEST);

        // 2. Main Content (Center)
        add(createMainContentPanel(), BorderLayout.CENTER);

        // 3. Footer (South)
        add(createFooterPanel(), BorderLayout.SOUTH);

        // Load Initial Data
        refreshData();

        // Start Footer Clock Timer
        startClock();
    }

    private JPanel createSidebarPanel() {
        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(200, 700));
        panel.setBackground(sidebarBg);
        panel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, borderGray));
        panel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new java.awt.Insets(10, 15, 10, 15);
        gbc.weightx = 1.0;
        gbc.gridx = 0;
        gbc.gridy = 0;

        // Title/Header
        JLabel lblAppTitle = new JLabel("Aplikasi Surat");
        lblAppTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblAppTitle.setForeground(primaryColor);
        lblAppTitle.setBorder(BorderFactory.createEmptyBorder(10, 0, 15, 0));
        panel.add(lblAppTitle, gbc);

        // Section: MENU
        gbc.gridy++;
        JLabel lblMenuSection = new JLabel("MENU");
        lblMenuSection.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblMenuSection.setForeground(textGray);
        panel.add(lblMenuSection, gbc);

        gbc.gridy++;
        btnSuratMasuk = createMenuButton("📥 Surat Masuk", true);
        panel.add(btnSuratMasuk, gbc);

        gbc.gridy++;
        btnSuratKeluar = createMenuButton("📤 Surat Keluar", false);
        panel.add(btnSuratKeluar, gbc);

        // Spacer
        gbc.gridy++;
        gbc.weighty = 0.05;
        panel.add(Box.createVerticalStrut(20), gbc);
        gbc.weighty = 0;

        // Section: LAPORAN
        gbc.gridy++;
        JLabel lblLaporanSection = new JLabel("LAPORAN");
        lblLaporanSection.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblLaporanSection.setForeground(textGray);
        panel.add(lblLaporanSection, gbc);

        gbc.gridy++;
        btnExportPdf = createMenuButton("📄 Export PDF", false);
        panel.add(btnExportPdf, gbc);

        // Spacer
        gbc.gridy++;
        gbc.weighty = 0.05;
        panel.add(Box.createVerticalStrut(20), gbc);
        gbc.weighty = 0;

        // Section: KELUAR
        gbc.gridy++;
        JLabel lblKeluarSection = new JLabel("KELUAR");
        lblKeluarSection.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblKeluarSection.setForeground(textGray);
        panel.add(lblKeluarSection, gbc);

        gbc.gridy++;
        btnKeluar = createMenuButton("🚪 Keluar", false);
        panel.add(btnKeluar, gbc);

        // Push everything to top
        gbc.gridy++;
        gbc.weighty = 1.0;
        panel.add(new JLabel(""), gbc);

        // Button Click Listeners
        btnSuratMasuk.addActionListener(e -> {
            isSuratMasukActive = true;
            updateSidebarSelection();
            lblTitle.setText("Data Surat Masuk");
            lblTglTransaksiTitle.setText("Tanggal Masuk");
            lblStakeholderTitle.setText("Pengirim");
            lblFilterStakeholder.setText("Filter Pengirim:");
            refreshData();
        });

        btnSuratKeluar.addActionListener(e -> {
            isSuratMasukActive = false;
            updateSidebarSelection();
            lblTitle.setText("Data Surat Keluar");
            lblTglTransaksiTitle.setText("Tanggal Keluar");
            lblStakeholderTitle.setText("Penerima");
            lblFilterStakeholder.setText("Filter Penerima:");
            refreshData();
        });

        btnExportPdf.addActionListener(e -> exportToPdfFlow());

        btnKeluar.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Apakah Anda yakin ingin keluar?",
                    "Konfirmasi Keluar",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );
            if (confirm == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        });

        return panel;
    }

    private JButton createMenuButton(String text, boolean isActive) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Styling based on state
        if (isActive) {
            btn.setBackground(primaryColor);
            btn.setForeground(Color.WHITE);
            btn.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(primaryColor, 1),
                    BorderFactory.createEmptyBorder(8, 12, 8, 12)
            ));
        } else {
            btn.setBackground(Color.WHITE);
            btn.setForeground(textDark);
            btn.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(borderGray, 1),
                    BorderFactory.createEmptyBorder(8, 12, 8, 12)
            ));
        }

        // Simple hover effect
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (btn.getBackground() != primaryColor) {
                    btn.setBackground(hoverColor);
                }
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (btn.getBackground() != primaryColor) {
                    btn.setBackground(Color.WHITE);
                }
            }
        });

        return btn;
    }

    private void updateSidebarSelection() {
        // Reset all
        btnSuratMasuk.setBackground(Color.WHITE);
        btnSuratMasuk.setForeground(textDark);
        btnSuratMasuk.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderGray, 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));

        btnSuratKeluar.setBackground(Color.WHITE);
        btnSuratKeluar.setForeground(textDark);
        btnSuratKeluar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderGray, 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));

        // Set active
        if (isSuratMasukActive) {
            btnSuratMasuk.setBackground(primaryColor);
            btnSuratMasuk.setForeground(Color.WHITE);
            btnSuratMasuk.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(primaryColor, 1),
                    BorderFactory.createEmptyBorder(8, 12, 8, 12)
            ));
        } else {
            btnSuratKeluar.setBackground(primaryColor);
            btnSuratKeluar.setForeground(Color.WHITE);
            btnSuratKeluar.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(primaryColor, 1),
                    BorderFactory.createEmptyBorder(8, 12, 8, 12)
            ));
        }
    }

    private JPanel createMainContentPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(mainBg);
        panel.setBorder(new EmptyBorder(15, 20, 15, 20));

        // 2a. Upper controls panel (BoxLayout Vertical)
        JPanel northPanel = new JPanel();
        northPanel.setBackground(mainBg);
        northPanel.setLayout(new BoxLayout(northPanel, BoxLayout.Y_AXIS));

        // Row 1: Title, CRUD Buttons, and Search
        JPanel row1 = new JPanel(new BorderLayout());
        row1.setBackground(mainBg);
        row1.setBorder(new EmptyBorder(0, 0, 10, 0));

        lblTitle = new JLabel("Data Surat Masuk");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(textDark);
        row1.add(lblTitle, BorderLayout.WEST);

        // FlowPanel for Action buttons + Search field
        JPanel actionAndSearchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionAndSearchPanel.setBackground(mainBg);

        btnTambah = createActionButton("Tambah");
        btnEdit = createActionButton("Edit");
        btnHapus = createActionButton("Hapus");
        btnRefresh = createActionButton("Refresh");

        actionAndSearchPanel.add(btnTambah);
        actionAndSearchPanel.add(btnEdit);
        actionAndSearchPanel.add(btnHapus);
        actionAndSearchPanel.add(btnRefresh);

        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        searchPanel.setBackground(mainBg);
        JLabel lblSearch = new JLabel("Cari:");
        lblSearch.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtSearch = new JTextField(15);
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtSearch.setToolTipText("Cari nomor surat, pengirim, perihal...");

        searchPanel.add(lblSearch);
        searchPanel.add(txtSearch);
        actionAndSearchPanel.add(searchPanel);

        row1.add(actionAndSearchPanel, BorderLayout.EAST);
        northPanel.add(row1);

        // Row 2: Filters Panel
        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        row2.setBackground(mainBg);
        row2.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, borderGray));

        lblFilterStakeholder = new JLabel("Filter Pengirim:");
        lblFilterStakeholder.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        cbFilterStakeholder = new JComboBox<>(new String[]{"Semua"});
        styleComboBox(cbFilterStakeholder);

        JLabel lblBulan = new JLabel("Bulan:");
        lblBulan.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        cbFilterBulan = new JComboBox<>(new String[]{
                "Semua", "Januari", "Februari", "Maret", "April", "Mei", "Juni",
                "Juli", "Agustus", "September", "Oktober", "November", "Desember"
        });
        styleComboBox(cbFilterBulan);

        JLabel lblTahun = new JLabel("Tahun:");
        lblTahun.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        cbFilterTahun = new JComboBox<>(new String[]{"Semua"});
        styleComboBox(cbFilterTahun);

        JLabel lblSort = new JLabel("Urutkan Tanggal:");
        lblSort.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        cbSortTanggal = new JComboBox<>(new String[]{"Terbaru - Terlama", "Terlama - Terbaru"});
        styleComboBox(cbSortTanggal);

        row2.add(lblFilterStakeholder);
        row2.add(cbFilterStakeholder);
        row2.add(lblBulan);
        row2.add(cbFilterBulan);
        row2.add(lblTahun);
        row2.add(cbFilterTahun);
        row2.add(lblSort);
        row2.add(cbSortTanggal);

        northPanel.add(row2);
        panel.add(northPanel, BorderLayout.NORTH);

        // 2b. Central Table
        tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.setRowHeight(30);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setGridColor(new Color(230, 230, 230));
        table.setShowGrid(true);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        // Styling headers
        JTableHeader tableHeader = table.getTableHeader();
        tableHeader.setFont(new Font("Segoe UI", Font.BOLD, 12));
        tableHeader.setBackground(new Color(243, 244, 246));
        tableHeader.setForeground(textDark);
        tableHeader.setPreferredSize(new Dimension(tableHeader.getWidth(), 30));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(borderGray));
        scrollPane.setBackground(mainBg);
        scrollPane.getViewport().setBackground(mainBg);
        scrollPane.setPreferredSize(new Dimension(800, 300));

        panel.add(scrollPane, BorderLayout.CENTER);

        // 2c. South Detail Panel
        panel.add(createDetailPanel(), BorderLayout.SOUTH);

        // Wire Event Listeners for Filters
        ActionListener filterListener = e -> applyFiltersAndSearch();
        cbFilterStakeholder.addActionListener(filterListener);
        cbFilterBulan.addActionListener(filterListener);
        cbFilterTahun.addActionListener(filterListener);
        cbSortTanggal.addActionListener(filterListener);

        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { applyFiltersAndSearch(); }
            @Override
            public void removeUpdate(DocumentEvent e) { applyFiltersAndSearch(); }
            @Override
            public void changedUpdate(DocumentEvent e) { applyFiltersAndSearch(); }
        });

        // Table Row Selection Listener
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateDetailPanel();
            }
        });

        // Wire CRUD Button Actions
        btnTambah.addActionListener(e -> openAddDialog());
        btnEdit.addActionListener(e -> openEditDialog());
        btnHapus.addActionListener(e -> performDelete());
        btnRefresh.addActionListener(e -> {
            txtSearch.setText("");
            cbFilterStakeholder.setSelectedIndex(0);
            cbFilterBulan.setSelectedIndex(0);
            cbFilterTahun.setSelectedIndex(0);
            cbSortTanggal.setSelectedIndex(0);
            pengelola.loadData();
            refreshData();
            JOptionPane.showMessageDialog(this, "Data berhasil diperbarui!", "Refresh", JOptionPane.INFORMATION_MESSAGE);
        });

        return panel;
    }

    private JPanel createDetailPanel() {
        JPanel detailOuterPanel = new JPanel(new BorderLayout());
        detailOuterPanel.setBackground(mainBg);
        detailOuterPanel.setBorder(new EmptyBorder(15, 0, 0, 0));

        JPanel detailPanel = new JPanel(new GridBagLayout());
        detailPanel.setBackground(mainBg);

        TitledBorder titledBorder = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(borderGray, 1),
                "Detail Surat",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 12),
                primaryColor
        );
        detailPanel.setBorder(BorderFactory.createCompoundBorder(
                titledBorder,
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 15, 8, 15);
        gbc.anchor = GridBagConstraints.WEST;

        // Columns layout
        // Col 0: Labels, Col 1: Values (Left Side)
        // Col 2: Labels, Col 3: Values (Right Side)

        // Row 0: ID Surat & Pengirim/Penerima
        gbc.gridy = 0;
        gbc.gridx = 0;
        gbc.weightx = 0.1;
        detailPanel.add(new JLabel("ID Surat"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.4;
        lblValId = new JLabel("-");
        lblValId.setFont(new Font("Segoe UI", Font.BOLD, 12));
        detailPanel.add(lblValId, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0.1;
        lblStakeholderTitle = new JLabel("Pengirim");
        detailPanel.add(lblStakeholderTitle, gbc);
        gbc.gridx = 3;
        gbc.weightx = 0.4;
        lblValStakeholder = new JLabel("-");
        lblValStakeholder.setFont(new Font("Segoe UI", Font.BOLD, 12));
        detailPanel.add(lblValStakeholder, gbc);

        // Row 1: Nomor Surat & Perihal
        gbc.gridy = 1;
        gbc.gridx = 0;
        detailPanel.add(new JLabel("Nomor Surat"), gbc);
        gbc.gridx = 1;
        lblValNomor = new JLabel("-");
        lblValNomor.setFont(new Font("Segoe UI", Font.BOLD, 12));
        detailPanel.add(lblValNomor, gbc);

        gbc.gridx = 2;
        detailPanel.add(new JLabel("Perihal"), gbc);
        gbc.gridx = 3;
        lblValPerihal.setFont(new Font("Segoe UI", Font.BOLD, 12));
        detailPanel.add(lblValPerihal, gbc);

        // Row 2: Tanggal Surat & Keterangan
        gbc.gridy = 2;
        gbc.gridx = 0;
        detailPanel.add(new JLabel("Tanggal Surat"), gbc);
        gbc.gridx = 1;
        lblValTglSurat = new JLabel("-");
        lblValTglSurat.setFont(new Font("Segoe UI", Font.BOLD, 12));
        detailPanel.add(lblValTglSurat, gbc);

        gbc.gridx = 2;
        detailPanel.add(new JLabel("Keterangan"), gbc);
        gbc.gridx = 3;
        lblValKeterangan.setFont(new Font("Segoe UI", Font.BOLD, 12));
        detailPanel.add(lblValKeterangan, gbc);

        // Row 3: Tanggal Masuk/Keluar
        gbc.gridy = 3;
        gbc.gridx = 0;
        lblTglTransaksiTitle = new JLabel("Tanggal Masuk");
        detailPanel.add(lblTglTransaksiTitle, gbc);
        gbc.gridx = 1;
        lblValTglTransaksi = new JLabel("-");
        lblValTglTransaksi.setFont(new Font("Segoe UI", Font.BOLD, 12));
        detailPanel.add(lblValTglTransaksi, gbc);

        detailOuterPanel.add(detailPanel, BorderLayout.CENTER);
        return detailOuterPanel;
    }

    private JPanel createFooterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(sidebarBg);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, borderGray),
                BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));

        lblTotalData = new JLabel("Total Data: 0");
        lblTotalData.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblTotalData.setForeground(textDark);

        lblDateTime = new JLabel("");
        lblDateTime.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblDateTime.setForeground(textDark);

        panel.add(lblTotalData, BorderLayout.WEST);
        panel.add(lblDateTime, BorderLayout.EAST);
        return panel;
    }

    private JButton createActionButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btn.setFocusPainted(false);
        btn.setBackground(Color.WHITE);
        btn.setForeground(textDark);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderGray, 1),
                BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(hoverColor);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(Color.WHITE);
            }
        });
        return btn;
    }

    private void styleComboBox(JComboBox<String> cb) {
        cb.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        cb.setBackground(Color.WHITE);
        cb.setForeground(textDark);
        cb.setPreferredSize(new Dimension(130, 24));
    }

    private void startClock() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        javax.swing.Timer timer = new javax.swing.Timer(1000, e -> {
            lblDateTime.setText(sdf.format(new Date()));
        });
        timer.start();
    }

    // Refresh data lists, reload filter contents, update Table Model and labels
    private void refreshData() {
        // Build table columns
        String stakeholderHeader = isSuratMasukActive ? "Pengirim" : "Penerima";
        String tglTransaksiHeader = isSuratMasukActive ? "Tanggal Masuk" : "Tanggal Keluar";

        String[] columns = {
                "No", "ID Surat", "Nomor Surat", "Tanggal Surat", tglTransaksiHeader, stakeholderHeader, "Perihal", "Keterangan"
        };

        tableModel.setColumnIdentifiers(columns);

        // Auto align table columns
        setupTableRenderers();

        // Populate Filters (Stakeholder and Year dropdowns based on data)
        populateDropdownFilters();

        // Load data list
        applyFiltersAndSearch();
    }

    private void setupTableRenderers() {
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, col);
                c.setBackground(isSelected ? new Color(225, 235, 255) : (row % 2 == 0 ? Color.WHITE : new Color(248, 250, 252)));
                c.setForeground(textDark);
                setHorizontalAlignment(SwingConstants.CENTER);
                setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
                return c;
            }
        };

        DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, col);
                c.setBackground(isSelected ? new Color(225, 235, 255) : (row % 2 == 0 ? Color.WHITE : new Color(248, 250, 252)));
                c.setForeground(textDark);
                setHorizontalAlignment(SwingConstants.LEFT);
                setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                return c;
            }
        };

        for (int i = 0; i < table.getColumnCount(); i++) {
            if (i == 0 || i == 1 || i == 3 || i == 4) {
                table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
            } else {
                table.getColumnModel().getColumn(i).setCellRenderer(leftRenderer);
            }
        }

        // Set column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(40);  // No
        table.getColumnModel().getColumn(1).setPreferredWidth(80);  // ID
        table.getColumnModel().getColumn(2).setPreferredWidth(120); // Nomor
        table.getColumnModel().getColumn(3).setPreferredWidth(100); // Tanggal Surat
        table.getColumnModel().getColumn(4).setPreferredWidth(100); // Tanggal Transaksi
        table.getColumnModel().getColumn(5).setPreferredWidth(150); // Stakeholder
        table.getColumnModel().getColumn(6).setPreferredWidth(180); // Perihal
        table.getColumnModel().getColumn(7).setPreferredWidth(100); // Keterangan
    }

    private void populateDropdownFilters() {
        // Temporal remove action listeners to prevent trigger on populate
        ActionListener[] stakeListeners = cbFilterStakeholder.getActionListeners();
        for (ActionListener al : stakeListeners) cbFilterStakeholder.removeActionListener(al);

        ActionListener[] yearListeners = cbFilterTahun.getActionListeners();
        for (ActionListener al : yearListeners) cbFilterTahun.removeActionListener(al);

        // Keep selected values if any
        String selStakeholder = (String) cbFilterStakeholder.getSelectedItem();
        String selTahun = (String) cbFilterTahun.getSelectedItem();

        cbFilterStakeholder.removeAllItems();
        cbFilterStakeholder.addItem("Semua");

        cbFilterTahun.removeAllItems();
        cbFilterTahun.addItem("Semua");

        Set<String> stakeholders = new TreeSet<>();
        Set<String> years = new TreeSet<>();

        if (isSuratMasukActive) {
            for (SuratMasuk sm : pengelola.getDaftarSuratMasuk()) {
                stakeholders.add(sm.getPengirim());
                String y = getYearFromDate(sm.getTanggalMasuk());
                if (y != null) years.add(y);
            }
        } else {
            for (SuratKeluar sk : pengelola.getDaftarSuratKeluar()) {
                stakeholders.add(sk.getPenerima());
                String y = getYearFromDate(sk.getTanggalKeluar());
                if (y != null) years.add(y);
            }
        }

        for (String s : stakeholders) cbFilterStakeholder.addItem(s);
        for (String y : years) cbFilterTahun.addItem(y);

        // Restore selected
        if (selStakeholder != null) {
            cbFilterStakeholder.setSelectedItem(selStakeholder);
            if (cbFilterStakeholder.getSelectedIndex() == -1) cbFilterStakeholder.setSelectedIndex(0);
        }
        if (selTahun != null) {
            cbFilterTahun.setSelectedItem(selTahun);
            if (cbFilterTahun.getSelectedIndex() == -1) cbFilterTahun.setSelectedIndex(0);
        }

        // Restore action listeners
        for (ActionListener al : stakeListeners) cbFilterStakeholder.addActionListener(al);
        for (ActionListener al : yearListeners) cbFilterTahun.addActionListener(al);
    }

    private String getYearFromDate(String dateStr) {
        if (dateStr == null || dateStr.length() < 10) return null;
        String[] parts = dateStr.split("-");
        if (parts.length == 3) {
            return parts[2];
        }
        return null;
    }

    private void applyFiltersAndSearch() {
        String query = txtSearch.getText().toLowerCase();
        String filterStakeholder = (String) cbFilterStakeholder.getSelectedItem();
        int filterBulanIdx = cbFilterBulan.getSelectedIndex(); // 0: Semua, 1: Jan, 2: Feb, etc.
        String filterTahun = (String) cbFilterTahun.getSelectedItem();
        boolean isNewestFirst = cbSortTanggal.getSelectedIndex() == 0;

        List<Surat> list;
        if (isSuratMasukActive) {
            list = new ArrayList<>(pengelola.getDaftarSuratMasuk());
        } else {
            list = new ArrayList<>(pengelola.getDaftarSuratKeluar());
        }

        // 1. Search filter (nomorSurat, stakeholder, perihal)
        if (!query.isEmpty()) {
            list = list.stream().filter(s ->
                    s.getNomorSurat().toLowerCase().contains(query) ||
                    s.getStakeholder().toLowerCase().contains(query) ||
                    s.getPerihal().toLowerCase().contains(query)
            ).collect(Collectors.toList());
        }

        // 2. Stakeholder filter
        if (filterStakeholder != null && !filterStakeholder.equals("Semua")) {
            list = list.stream().filter(s -> s.getStakeholder().equals(filterStakeholder)).collect(Collectors.toList());
        }

        // 3. Month filter
        if (filterBulanIdx > 0) {
            list = list.stream().filter(s -> {
                String tgl = s.getTanggalTransaksi(); // dd-MM-yyyy
                if (tgl != null && tgl.length() == 10) {
                    try {
                        int month = Integer.parseInt(tgl.split("-")[1]);
                        return month == filterBulanIdx;
                    } catch (NumberFormatException e) {
                        return false;
                    }
                }
                return false;
            }).collect(Collectors.toList());
        }

        // 4. Year filter
        if (filterTahun != null && !filterTahun.equals("Semua")) {
            list = list.stream().filter(s -> {
                String tgl = s.getTanggalTransaksi();
                if (tgl != null && tgl.length() == 10) {
                    return tgl.endsWith(filterTahun);
                }
                return false;
            }).collect(Collectors.toList());
        }

        // 5. Date Sort
        list.sort((s1, s2) -> {
            LocalDate d1 = parseDate(s1.getTanggalTransaksi());
            LocalDate d2 = parseDate(s2.getTanggalTransaksi());
            if (isNewestFirst) {
                return d2.compareTo(d1); // Descending
            } else {
                return d1.compareTo(d2); // Ascending
            }
        });

        filteredSuratList = list;

        // Render Table Rows
        tableModel.setRowCount(0);
        int no = 1;
        for (Surat s : filteredSuratList) {
            tableModel.addRow(new Object[]{
                    no++,
                    s.getIdSurat(),
                    s.getNomorSurat(),
                    s.getTanggalSurat(),
                    s.getTanggalTransaksi(),
                    s.getStakeholder(),
                    s.getPerihal(),
                    s.getKeterangan()
            });
        }

        lblTotalData.setText("Total Data: " + filteredSuratList.size());
        updateDetailPanel();
    }

    private LocalDate parseDate(String dateStr) {
        try {
            return LocalDate.parse(dateStr, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            return LocalDate.MIN;
        }
    }

    private void updateDetailPanel() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1 && selectedRow < filteredSuratList.size()) {
            Surat s = filteredSuratList.get(selectedRow);
            lblValId.setText(s.getIdSurat());
            lblValNomor.setText(s.getNomorSurat());
            lblValTglSurat.setText(s.getTanggalSurat());
            lblValTglTransaksi.setText(s.getTanggalTransaksi());
            lblValStakeholder.setText(s.getStakeholder());
            lblValPerihal.setText(s.getPerihal());
            lblValKeterangan.setText(s.getKeterangan());
        } else {
            // Clear details
            lblValId.setText("-");
            lblValNomor.setText("-");
            lblValTglSurat.setText("-");
            lblValTglTransaksi.setText("-");
            lblValStakeholder.setText("-");
            lblValPerihal.setText("-");
            lblValKeterangan.setText("-");
        }
    }

    private void openAddDialog() {
        String generatedId = pengelola.generateNextId(isSuratMasukActive);
        JDialog dialog = new JDialog(this, "Tambah " + (isSuratMasukActive ? "Surat Masuk" : "Surat Keluar"), true);
        dialog.setSize(450, 450);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 15, 10, 15);
        gbc.anchor = GridBagConstraints.WEST;

        // Form Fields
        gbc.gridy = 0; gbc.gridx = 0; gbc.weightx = 0.3;
        dialog.add(new JLabel("ID Surat"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        JTextField txtId = new JTextField(generatedId);
        txtId.setEnabled(false);
        dialog.add(txtId, gbc);

        gbc.gridy++; gbc.gridx = 0;
        dialog.add(new JLabel("Nomor Surat"), gbc);
        gbc.gridx = 1;
        JTextField txtNomor = new JTextField();
        dialog.add(txtNomor, gbc);

        gbc.gridy++; gbc.gridx = 0;
        dialog.add(new JLabel("Tanggal Surat"), gbc);
        gbc.gridx = 1;
        String today = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
        JTextField txtTglSurat = new JTextField(today);
        dialog.add(txtTglSurat, gbc);

        gbc.gridy++; gbc.gridx = 0;
        JLabel lblTglTx = new JLabel(isSuratMasukActive ? "Tanggal Masuk" : "Tanggal Keluar");
        dialog.add(lblTglTx, gbc);
        gbc.gridx = 1;
        JTextField txtTglTx = new JTextField(today);
        dialog.add(txtTglTx, gbc);

        gbc.gridy++; gbc.gridx = 0;
        JLabel lblStake = new JLabel(isSuratMasukActive ? "Pengirim" : "Penerima");
        dialog.add(lblStake, gbc);
        gbc.gridx = 1;
        JTextField txtStake = new JTextField();
        dialog.add(txtStake, gbc);

        gbc.gridy++; gbc.gridx = 0;
        dialog.add(new JLabel("Perihal"), gbc);
        gbc.gridx = 1;
        JTextField txtPerihal = new JTextField();
        dialog.add(txtPerihal, gbc);

        gbc.gridy++; gbc.gridx = 0;
        dialog.add(new JLabel("Keterangan"), gbc);
        gbc.gridx = 1;
        JTextField txtKeterangan = new JTextField("-");
        dialog.add(txtKeterangan, gbc);

        // Buttons
        gbc.gridy++; gbc.gridx = 0; gbc.gridwidth = 2;
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setBackground(dialog.getBackground());

        JButton btnBatal = new JButton("Batal");
        JButton btnSimpan = new JButton("Simpan");

        btnPanel.add(btnBatal);
        btnPanel.add(btnSimpan);
        dialog.add(btnPanel, gbc);

        // Listeners
        btnBatal.addActionListener(e -> dialog.dispose());
        btnSimpan.addActionListener(e -> {
            String nomor = txtNomor.getText().trim();
            String tglSurat = txtTglSurat.getText().trim();
            String tglTx = txtTglTx.getText().trim();
            String stakeholder = txtStake.getText().trim();
            String perihal = txtPerihal.getText().trim();
            String keterangan = txtKeterangan.getText().trim();

            if (nomor.isEmpty() || tglSurat.isEmpty() || tglTx.isEmpty() || stakeholder.isEmpty() || perihal.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Mohon lengkapi semua field wajib!", "Peringatan", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (!isValidDate(tglSurat) || !isValidDate(tglTx)) {
                JOptionPane.showMessageDialog(dialog, "Format tanggal harus dd-MM-yyyy! Contoh: 10-01-2025", "Peringatan", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Create Surat Masuk/Keluar object
            Surat baru;
            if (isSuratMasukActive) {
                baru = new SuratMasuk(generatedId, nomor, tglSurat, tglTx, stakeholder, perihal, keterangan);
            } else {
                baru = new SuratKeluar(generatedId, nomor, tglSurat, tglTx, stakeholder, perihal, keterangan);
            }

            pengelola.tambahSurat(baru);
            dialog.dispose();
            refreshData();
            JOptionPane.showMessageDialog(this, "Data berhasil disimpan!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
        });

        dialog.setVisible(true);
    }

    private void openEditDialog() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1 || selectedRow >= filteredSuratList.size()) {
            JOptionPane.showMessageDialog(this, "Pilih baris data yang ingin diedit terlebih dahulu!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Surat s = filteredSuratList.get(selectedRow);

        JDialog dialog = new JDialog(this, "Edit " + (isSuratMasukActive ? "Surat Masuk" : "Surat Keluar"), true);
        dialog.setSize(450, 450);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 15, 10, 15);
        gbc.anchor = GridBagConstraints.WEST;

        // Form Fields
        gbc.gridy = 0; gbc.gridx = 0; gbc.weightx = 0.3;
        dialog.add(new JLabel("ID Surat"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        JTextField txtId = new JTextField(s.getIdSurat());
        txtId.setEnabled(false);
        dialog.add(txtId, gbc);

        gbc.gridy++; gbc.gridx = 0;
        dialog.add(new JLabel("Nomor Surat"), gbc);
        gbc.gridx = 1;
        JTextField txtNomor = new JTextField(s.getNomorSurat());
        dialog.add(txtNomor, gbc);

        gbc.gridy++; gbc.gridx = 0;
        dialog.add(new JLabel("Tanggal Surat"), gbc);
        gbc.gridx = 1;
        JTextField txtTglSurat = new JTextField(s.getTanggalSurat());
        dialog.add(txtTglSurat, gbc);

        gbc.gridy++; gbc.gridx = 0;
        JLabel lblTglTx = new JLabel(isSuratMasukActive ? "Tanggal Masuk" : "Tanggal Keluar");
        dialog.add(lblTglTx, gbc);
        gbc.gridx = 1;
        JTextField txtTglTx = new JTextField(s.getTanggalTransaksi());
        dialog.add(txtTglTx, gbc);

        gbc.gridy++; gbc.gridx = 0;
        JLabel lblStake = new JLabel(isSuratMasukActive ? "Pengirim" : "Penerima");
        dialog.add(lblStake, gbc);
        gbc.gridx = 1;
        JTextField txtStake = new JTextField(s.getStakeholder());
        dialog.add(txtStake, gbc);

        gbc.gridy++; gbc.gridx = 0;
        dialog.add(new JLabel("Perihal"), gbc);
        gbc.gridx = 1;
        JTextField txtPerihal = new JTextField(s.getPerihal());
        dialog.add(txtPerihal, gbc);

        gbc.gridy++; gbc.gridx = 0;
        dialog.add(new JLabel("Keterangan"), gbc);
        gbc.gridx = 1;
        JTextField txtKeterangan = new JTextField(s.getKeterangan());
        dialog.add(txtKeterangan, gbc);

        // Buttons
        gbc.gridy++; gbc.gridx = 0; gbc.gridwidth = 2;
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setBackground(dialog.getBackground());

        JButton btnBatal = new JButton("Batal");
        JButton btnSimpan = new JButton("Simpan");

        btnPanel.add(btnBatal);
        btnPanel.add(btnSimpan);
        dialog.add(btnPanel, gbc);

        // Listeners
        btnBatal.addActionListener(e -> dialog.dispose());
        btnSimpan.addActionListener(e -> {
            String nomor = txtNomor.getText().trim();
            String tglSurat = txtTglSurat.getText().trim();
            String tglTx = txtTglTx.getText().trim();
            String stakeholder = txtStake.getText().trim();
            String perihal = txtPerihal.getText().trim();
            String keterangan = txtKeterangan.getText().trim();

            if (nomor.isEmpty() || tglSurat.isEmpty() || tglTx.isEmpty() || stakeholder.isEmpty() || perihal.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Mohon lengkapi semua field wajib!", "Peringatan", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (!isValidDate(tglSurat) || !isValidDate(tglTx)) {
                JOptionPane.showMessageDialog(dialog, "Format tanggal harus dd-MM-yyyy! Contoh: 10-01-2025", "Peringatan", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Surat baru;
            if (isSuratMasukActive) {
                baru = new SuratMasuk(s.getIdSurat(), nomor, tglSurat, tglTx, stakeholder, perihal, keterangan);
            } else {
                baru = new SuratKeluar(s.getIdSurat(), nomor, tglSurat, tglTx, stakeholder, perihal, keterangan);
            }

            pengelola.editSurat(s.getIdSurat(), baru);
            dialog.dispose();
            refreshData();
            JOptionPane.showMessageDialog(this, "Data berhasil diperbarui!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
        });

        dialog.setVisible(true);
    }

    private void performDelete() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1 || selectedRow >= filteredSuratList.size()) {
            JOptionPane.showMessageDialog(this, "Pilih baris data yang ingin dihapus terlebih dahulu!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Surat s = filteredSuratList.get(selectedRow);

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Apakah Anda yakin ingin menghapus data dengan ID " + s.getIdSurat() + "?",
                "Konfirmasi Hapus",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            pengelola.hapusSurat(s.getIdSurat());
            refreshData();
            JOptionPane.showMessageDialog(this, "Data berhasil dihapus!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private boolean isValidDate(String dateStr) {
        try {
            LocalDate.parse(dateStr, DATE_FORMATTER);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    private void exportToPdfFlow() {
        if (filteredSuratList.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tidak ada data untuk diexport!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String jenis = isSuratMasukActive ? "Surat Masuk" : "Surat Keluar";
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Pilih Metode Cetak/Export:\n\n- YES: Buka dialog Cetak Sistem (Print ke PDF, Printer, dll.)\n- NO: Simpan sebagai berkas Laporan HTML\n- CANCEL: Batal",
                "Export Laporan " + jenis,
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            // Native Print
            try {
                boolean printComplete = table.print(
                        JTable.PrintMode.FIT_WIDTH,
                        new java.text.MessageFormat("Laporan Data " + jenis),
                        new java.text.MessageFormat("Halaman {0}")
                );
                if (printComplete) {
                    JOptionPane.showMessageDialog(this, "Cetak berhasil dilakukan!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (java.awt.print.PrinterException e) {
                JOptionPane.showMessageDialog(this, "Gagal mencetak: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else if (confirm == JOptionPane.NO_OPTION) {
            // HTML Export - with JFileChooser to choose folder/file
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Pilih Folder dan Lokasi Simpan Laporan");
            
            String defaultFilename = "laporan_" + (isSuratMasukActive ? "masuk" : "keluar") + ".html";
            fileChooser.setSelectedFile(new java.io.File(defaultFilename));

            int userSelection = fileChooser.showSaveDialog(this);
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                java.io.File fileToSave = fileChooser.getSelectedFile();
                String absolutePath = fileToSave.getAbsolutePath();

                // Append .html extension if missing
                if (!absolutePath.toLowerCase().endsWith(".html")) {
                    fileToSave = new java.io.File(absolutePath + ".html");
                }

                try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileToSave))) {
                    bw.write("<html><head><title>Laporan Data " + jenis + "</title>");
                    bw.write("<style>");
                    bw.write("body { font-family: 'Segoe UI', Arial, sans-serif; margin: 40px; color: #111827; }");
                    bw.write("h2 { color: #1565D8; border-bottom: 2px solid #1565D8; padding-bottom: 10px; }");
                    bw.write("table { width: 100%; border-collapse: collapse; margin-top: 20px; }");
                    bw.write("th, td { border: 1px solid #D1D5DB; padding: 10px; text-align: left; font-size: 13px; }");
                    bw.write("th { background-color: #F3F4F6; font-weight: bold; }");
                    bw.write("tr:nth-child(even) { background-color: #F8FAFC; }");
                    bw.write(".footer { margin-top: 30px; font-size: 11px; color: #6B7280; text-align: right; }");
                    bw.write("</style></head><body>");

                    bw.write("<h2>Laporan Data " + jenis + "</h2>");
                    bw.write("<p>Total Data: " + filteredSuratList.size() + "</p>");
                    bw.write("<p>Waktu Unduh: " + new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date()) + "</p>");

                    bw.write("<table><thead><tr>");
                    bw.write("<th>No</th><th>ID Surat</th><th>Nomor Surat</th><th>Tanggal Surat</th>");
                    bw.write("<th>" + (isSuratMasukActive ? "Tanggal Masuk" : "Tanggal Keluar") + "</th>");
                    bw.write("<th>" + (isSuratMasukActive ? "Pengirim" : "Penerima") + "</th>");
                    bw.write("<th>Perihal</th><th>Keterangan</th>");
                    bw.write("</tr></thead><tbody>");

                    int no = 1;
                    for (Surat s : filteredSuratList) {
                        bw.write("<tr>");
                        bw.write("<td>" + no++ + "</td>");
                        bw.write("<td>" + s.getIdSurat() + "</td>");
                        bw.write("<td>" + s.getNomorSurat() + "</td>");
                        bw.write("<td>" + s.getTanggalSurat() + "</td>");
                        bw.write("<td>" + s.getTanggalTransaksi() + "</td>");
                        bw.write("<td>" + s.getStakeholder() + "</td>");
                        bw.write("<td>" + s.getPerihal() + "</td>");
                        bw.write("<td>" + s.getKeterangan() + "</td>");
                        bw.write("</tr>");
                    }
                    bw.write("</tbody></table>");
                    bw.write("<div class='footer'>Dicetak otomatis oleh Aplikasi Pengelolaan Surat</div>");
                    bw.write("</body></html>");

                    JOptionPane.showMessageDialog(
                            this,
                            "Laporan berhasil diexport ke:\n" + fileToSave.getAbsolutePath() +
                            "\n\nAnda dapat membuka berkas tersebut di peramban web dan menyimpannya sebagai PDF (Ctrl+P -> Save as PDF).",
                            "Sukses Export HTML",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(this, "Gagal mengekspor berkas: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
}
