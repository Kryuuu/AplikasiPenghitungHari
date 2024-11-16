
import javax.swing.JComboBox;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.time.Year;
import java.time.format.TextStyle;
import java.util.Locale;
import java.time.temporal.ChronoUnit;



public class PenghitunganHariFrame extends javax.swing.JFrame {

    
    // Format untuk bahasa Indonesia
    private final Locale indonesianLocale = new Locale("id", "ID");
    
    // Deklarasi variabel kontrol untuk menghindari pembaruan siklis
    private boolean isUpdating = false;

    public PenghitunganHariFrame() {
        initComponents();
        
        // Set tahunSpinner untuk menggunakan SpinnerNumberModel, memastikan nilai berupa Integer
        tahunSpinner.setModel(new SpinnerNumberModel(2023, 1900, 2100, 1));
        
        // Gunakan variabel kontrol agar perubahan awal tidak memicu listener
        isUpdating = true;
        
        // Set tanggalAwalCalendar ke tanggal saat ini
        tanggalAwalCalendar.setDate(new Date());
        
        // Ambil tanggal saat ini
        LocalDate currentDate = LocalDate.now();

        // Sinkronkan tahunSpinner dan bulanComboBox dengan tanggal saat ini
        tahunSpinner.setValue(currentDate.getYear());
        bulanComboBox.setSelectedIndex(currentDate.getMonthValue() - 1);

        // Nonaktifkan variabel kontrol setelah inisialisasi
        isUpdating = false;
        
        // Event listener untuk bulanComboBox
        bulanComboBox.addActionListener(evt -> {
            if (!isUpdating) {
                updateCalendarFromSpinnerComboBox();
                hitungJumlahHariDanKabisat();
            }
        });
        
        // Event listener untuk tahunSpinner
        tahunSpinner.addChangeListener(evt -> {
            if (!isUpdating) {
                updateCalendarFromSpinnerComboBox();
                hitungJumlahHariDanKabisat();
            }
        });

        // Event listener untuk perubahan tanggal di tanggalAwalCalendar
        tanggalAwalCalendar.addPropertyChangeListener("calendar", evt -> {
            if (!isUpdating) {
            updateSpinnerComboBoxFromCalendar();
            hitungSelisihHari(); // Menghitung selisih hari setiap kali tanggal awal berubah
            }
        });

        // Event listener untuk perubahan tanggal di tanggalAkhirCalendar
        tanggalAkhirCalendar.addPropertyChangeListener("calendar", evt -> {
            hitungSelisihHari(); // Menghitung selisih hari setiap kali tanggal akhir berubah
        });


    }
    
public int getJumlahHari(int tahun, int bulan) {
    YearMonth yearMonth = YearMonth.of(tahun, bulan); // bulan dalam bentuk 1-12
    return yearMonth.lengthOfMonth(); // Mengembalikan jumlah hari dalam bulan
}
    
public void hitungJumlahHari() {
    int bulan = bulanComboBox.getSelectedIndex() + 1;
    int tahun = (int) tahunSpinner.getValue();

    YearMonth yearMonth = YearMonth.of(tahun, bulan);
    int jumlahHari = yearMonth.lengthOfMonth();
    hasilLabel.setText("Jumlah Hari: " + jumlahHari);

    // Mendapatkan hari pertama dan terakhir dalam bulan tersebut
    LocalDate tanggalPertama = yearMonth.atDay(1);
    LocalDate tanggalTerakhir = yearMonth.atEndOfMonth();

    // Mengubah hari pertama dan terakhir ke bahasa Indonesia
    String hariPertama = tanggalPertama.getDayOfWeek().getDisplayName(TextStyle.FULL, indonesianLocale).toUpperCase();
    String hariTerakhir = tanggalTerakhir.getDayOfWeek().getDisplayName(TextStyle.FULL, indonesianLocale).toUpperCase();

    hariPertamaLabel.setText("Hari Pertama: " + hariPertama);
    hariTerakhirLabel.setText("Hari Terakhir: " + hariTerakhir);
}

    
public boolean isTahunKabisat(int tahun) {
    return Year.of(tahun).isLeap();
}
    
public void hitungJumlahHariDanKabisat() {
    int tahun = (int) tahunSpinner.getValue(); // Ambil tahun dari JSpinner
    int bulanIndex = bulanComboBox.getSelectedIndex(); // Ambil bulan dari JComboBox (0-11)
    String bulanNama = bulanComboBox.getItemAt(bulanIndex); // Ambil nama bulan dari JComboBox

    // Mengecek apakah tahun adalah tahun kabisat
    boolean kabisat = isTahunKabisat(tahun);
    String statusKabisat = kabisat ? "Tahun Kabisat" : "Bukan Tahun Kabisat";
    
    // Update label dengan informasi tahun dan bulan
    kabisatLabel.setText(statusKabisat + " - Tahun: " + tahun + ", Bulan: " + bulanNama);
}




    
    // Metode untuk memperbarui JCalendar dari JComboBox dan JSpinner
private void updateCalendarFromSpinnerComboBox() {
    if (isUpdating) return; // Jika sedang memperbarui, keluar dari metode ini
    isUpdating = true;

    int bulan = bulanComboBox.getSelectedIndex(); // Bulan di JComboBox dimulai dari 0 (Januari)
    int tahun = (int) tahunSpinner.getValue(); // Ambil tahun sebagai Integer

    // Atur tanggal di JCalendar sesuai dengan bulan dan tahun yang dipilih
    LocalDate tanggalBaru = LocalDate.of(tahun, bulan + 1, 1); // bulan + 1 karena LocalDate mulai dari 1 (Januari)
    Date tanggal = Date.from(tanggalBaru.atStartOfDay(ZoneId.systemDefault()).toInstant());
    tanggalAwalCalendar.setDate(tanggal);

    isUpdating = false;
}




    
private void updateSpinnerComboBoxFromCalendar() {
    if (isUpdating) return; // Jika sedang memperbarui, keluar dari metode ini
    isUpdating = true;

    // Mendapatkan tanggal yang dipilih dari JCalendar
    LocalDate selectedDate = tanggalAwalCalendar.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

    // Atur bulan dan tahun pada JComboBox dan JSpinner sesuai dengan tanggal di JCalendar
    bulanComboBox.setSelectedIndex(selectedDate.getMonthValue() - 1); // Bulan dimulai dari 0 di JComboBox
    tahunSpinner.setValue(selectedDate.getYear()); // Set tahun pada tahunSpinner

    // Memperbarui jumlah hari dan status kabisat
    hitungJumlahHariDanKabisat();

    isUpdating = false;
}

public void hitungSelisihHari() {
    // Ambil tanggal awal dan tanggal akhir dari JCalendar
    LocalDate tanggalAwal = tanggalAwalCalendar.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    LocalDate tanggalAkhir = tanggalAkhirCalendar.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

    // Hitung selisih hari
    long selisihHari = ChronoUnit.DAYS.between(tanggalAwal, tanggalAkhir);

    // Perbarui label selisih hari
    selisihHariLabel.setText("Selisih Hari: " + selisihHari + " hari");
}





    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        hasilLabel = new javax.swing.JLabel();
        bulanComboBox = new javax.swing.JComboBox<>();
        hitungButton = new javax.swing.JButton();
        tanggalAwalCalendar = new com.toedter.calendar.JCalendar();
        hariPertamaLabel = new javax.swing.JLabel();
        hariTerakhirLabel = new javax.swing.JLabel();
        kabisatLabel = new javax.swing.JLabel();
        tahunSpinner = new javax.swing.JSpinner();
        tanggalAkhirCalendar = new com.toedter.calendar.JCalendar();
        selisihHariLabel = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Aplikasi Perhitungan Hari", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 24))); // NOI18N
        jPanel1.setLayout(new java.awt.GridBagLayout());

        jLabel1.setText("Pilih Bulan");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.ipadx = 34;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(21, 26, 0, 0);
        jPanel1.add(jLabel1, gridBagConstraints);

        jLabel2.setText("Masukkan Tahun");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(72, 26, 0, 0);
        jPanel1.add(jLabel2, gridBagConstraints);

        hasilLabel.setText("Jumlah Hari");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.ipadx = 27;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(11, 24, 0, 0);
        jPanel1.add(hasilLabel, gridBagConstraints);

        bulanComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Januari", "Februari", "Maret", "April", "Mei", "Juni", "Juli", "Agustus", "September", "Oktober", "November", "Desember" }));
        bulanComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bulanComboBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(16, 32, 0, 0);
        jPanel1.add(bulanComboBox, gridBagConstraints);

        hitungButton.setText("Hitung");
        hitungButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hitungButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.ipadx = 125;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 12, 0, 0);
        jPanel1.add(hitungButton, gridBagConstraints);

        tanggalAwalCalendar.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                tanggalAwalCalendarPropertyChange(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.gridheight = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(7, 6, 0, 0);
        jPanel1.add(tanggalAwalCalendar, gridBagConstraints);

        hariPertamaLabel.setText("Hari Pertama");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 15;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.ipadx = 134;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(18, 82, 0, 0);
        jPanel1.add(hariPertamaLabel, gridBagConstraints);

        hariTerakhirLabel.setText("Hari Terakhir");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 17;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.ipadx = 132;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(18, 11, 84, 0);
        jPanel1.add(hariTerakhirLabel, gridBagConstraints);

        kabisatLabel.setText("Tahun Kabisat");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 15;
        gridBagConstraints.gridwidth = 8;
        gridBagConstraints.ipadx = 170;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(16, 16, 0, 0);
        jPanel1.add(kabisatLabel, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.ipadx = 51;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(67, 20, 0, 0);
        jPanel1.add(tahunSpinner, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.gridheight = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(7, 6, 0, 0);
        jPanel1.add(tanggalAkhirCalendar, gridBagConstraints);

        selisihHariLabel.setText("Selisih Hari");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 11;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.ipadx = 156;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(11, 20, 0, 230);
        jPanel1.add(selisihHariLabel, gridBagConstraints);

        jLabel3.setText("Kalender 1");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(126, 85, 0, 0);
        jPanel1.add(jLabel3, gridBagConstraints);

        jLabel4.setText("Kalender 2");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 85, 0, 0);
        jPanel1.add(jLabel4, gridBagConstraints);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void bulanComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bulanComboBoxActionPerformed
        updateCalendarFromSpinnerComboBox();
        hitungJumlahHariDanKabisat();
    }//GEN-LAST:event_bulanComboBoxActionPerformed

    private void hitungButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hitungButtonActionPerformed
        hitungJumlahHari();
    }//GEN-LAST:event_hitungButtonActionPerformed

    private void tanggalAwalCalendarPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_tanggalAwalCalendarPropertyChange
          // Memperbarui JComboBox dan JSpinner
         hitungJumlahHariDanKabisat(); // Memperbarui jumlah hari dan status kabisat
    }//GEN-LAST:event_tanggalAwalCalendarPropertyChange

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(PenghitunganHariFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(PenghitunganHariFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(PenghitunganHariFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(PenghitunganHariFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new PenghitunganHariFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> bulanComboBox;
    private javax.swing.JLabel hariPertamaLabel;
    private javax.swing.JLabel hariTerakhirLabel;
    private javax.swing.JLabel hasilLabel;
    private javax.swing.JButton hitungButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel kabisatLabel;
    private javax.swing.JLabel selisihHariLabel;
    private javax.swing.JSpinner tahunSpinner;
    private com.toedter.calendar.JCalendar tanggalAkhirCalendar;
    private com.toedter.calendar.JCalendar tanggalAwalCalendar;
    // End of variables declaration//GEN-END:variables
}
