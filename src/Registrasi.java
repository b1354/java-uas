import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class Registrasi implements ActionListener, MouseListener {
    private JFrame frame;
    private String DB_URL = "jdbc:mysql://localhost:3306/vis3_uas_2";
    private String DB_USER = "root";
    private String DB_PASS = "";

    public JButton buttonCari = new JButton("cari");
    public JButton buttonTambah = new JButton("tambah");
    public JButton buttonUbah = new JButton("ubah");
    public JButton buttonHapus = new JButton("hapus");

    public JTextField textKode = new JTextField();
    public JTextField textNama = new JTextField();
    public JTextField textMhs = new JTextField();
    public JTextField textJurusan = new JTextField();

    public JTable table;
    public JScrollPane sp;

    public Registrasi() {
        frame = new JFrame("Registrasi Mahsiswa");

        JLabel judul = new JLabel("UAS Visual III (JAVA) ");
        JLabel judul2 = new JLabel("Bayu Rizki/02032211073");
        JLabel labelNama = new JLabel("Dosen Pembimbing");
        JLabel labelMhs = new JLabel("Nama Mahasiswa");
        JLabel labelNim = new JLabel("Kode");
        JLabel labelJurusan = new JLabel("Jurusan");

        frame.setLayout(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        judul.setBounds(150, 20, 150, 20);
        judul2.setBounds(140, 40, 150, 20);
        frame.add(judul);
        frame.add(judul2);

        // NIM
        labelNim.setBounds(45, 80, 100, 20);
        textKode.setBounds(180, 80, 200, 20);
        frame.add(labelNim);
        frame.add(textKode);

        // Nama
        labelNama.setBounds(45, 110, 100, 20);
        textNama.setBounds(180, 110, 200, 20);
        frame.add(labelNama);
        frame.add(textNama);

        // Mahasiswa
        labelMhs.setBounds(45, 140, 100, 20);
        textMhs.setBounds(180, 140, 200, 20);
        frame.add(labelMhs);
        frame.add(textMhs);

        // Jurusan
        labelJurusan.setBounds(45, 170, 100, 20);
        textJurusan.setBounds(180, 170, 200, 20);
        frame.add(labelJurusan);
        frame.add(textJurusan);

        // Cari
        buttonCari.setBounds(20, 220, 60, 20);
        buttonCari.addActionListener(this);
        frame.add(buttonCari);
        
        // Tambah
        buttonTambah.setBounds(100, 220, 80, 20);
        buttonTambah.addActionListener(this);
        frame.add(buttonTambah);

        // Hapus
        buttonHapus.setBounds(200, 220, 80, 20);
        buttonHapus.addActionListener(this);
        frame.add(buttonHapus);

        // Ubah
        buttonUbah.setBounds(300, 220, 80, 20);
        buttonUbah.addActionListener(this);
        frame.add(buttonUbah);

        // Membuat frame
        frame.setSize(410, 510);
        frame.setVisible(true);
        testConnection();

        loadData();
    }

    private void clearForm() {
        textKode.setText("");
        textNama.setText("");
        textMhs.setText("");
        textJurusan.setText("");

        if (sp.isShowing()) {
            frame.remove(sp);
        }

        loadData();
    }

    private void testConnection() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            System.out.println("terhubung degan database");
        } catch (Exception connError) {
            System.out.println("tidak dapat terhubung ke database");
        }
    }

    private boolean validate() {
        String kode = textKode.getText();
        String nama = textNama.getText();
        String mhs = textMhs.getText();
        String jurusan = textJurusan.getText();

        if (kode.isEmpty() || nama.isEmpty() || jurusan.isEmpty() || mhs.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Semua form harus diisi", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        } else if (kode.length() != 11) {
            JOptionPane.showMessageDialog(frame, "kode harus 11 digit", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    private int countMhs() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            String query = "SELECT COUNT(*) AS jumlah FROM dosen_pembimbing";
            int jumlah = 0;
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                jumlah = rs.getInt("jumlah");
            }
            return jumlah;
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return 0;
        }
    }

    private void loadData() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            String[] tableHeader = { "Kode", "Nama Dosen", "Mahasiswa", "Jurusan" };
            String query = "SELECT * FROM dosen_pembimbing";
            Statement stmt = conn.createStatement();
            ResultSet data = stmt.executeQuery(query);

            int dataCount = 0;
            Object[][] tableData = new Object[countMhs()][4];

            while (data.next()) {

                tableData[dataCount][0] = data.getString("Kode");
                tableData[dataCount][1] = data.getString("nama_dosen");
                tableData[dataCount][2] = data.getString("nama_mahasiswa");
                tableData[dataCount][3] = data.getString("jurusan");

                dataCount++;
            }

            table = new JTable(tableData, tableHeader);
            sp = new JScrollPane(table);

            table.addMouseListener(this);

            sp.setBounds(20, 250, 360, 150);
            frame.add(sp);
        } catch (Exception e) {
            System.out.println("Error : " + e.getMessage());
            JOptionPane.showMessageDialog(frame, "tidak dapat mengambil data", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String kode = textKode.getText();
        String nama = textNama.getText();
        String mhs = textMhs.getText();
        String jurusan = textJurusan.getText();

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            Statement stmt = conn.createStatement();

            if (e.getSource() == buttonCari) {
                String query = "SELECT * FROM dosen_pembimbing WHERE kode = '" + kode + "'";
                ResultSet Data = stmt.executeQuery(query);

                if (Data.next()) {
                    textNama.setText(Data.getString("nama_dosen"));
                    textMhs.setText(Data.getString("nama_mahasiswa"));
                    textJurusan.setText(Data.getString("jurusan"));
                } else {
                    JOptionPane.showMessageDialog(null, "Data tidak ditemukan");
                    clearForm();
                }
            } else if (e.getSource() == buttonTambah && validate()) {
                String query = "INSERT INTO dosen_pembimbing (kode, nama_dosen, nama_mahasiswa, jurusan) VALUES ('" + kode + "', " + "'" + nama
                        + "', " + "'"+ mhs + "', '"+ jurusan +"');";
                int updateData = stmt.executeUpdate(query);

                if (updateData > 0) {
                    JOptionPane.showMessageDialog(null, "Tambah data berhasil");
                } else {
                    JOptionPane.showMessageDialog(null, "terjadi kesalahan saat menginput data");
                }

                clearForm();
            } else if (e.getSource() == buttonUbah && validate()) {
                String query = "UPDATE dosen_pembimbing SET nama_dosen = '" + nama + "', nama_mahasiswa = '" + mhs + "', jurusan = '" + jurusan
                        + "' WHERE kode = '" + kode + "';";
                int updateData = stmt.executeUpdate(query);

                if (updateData > 0) {
                    JOptionPane.showMessageDialog(null, "update data berhasil");
                } else {
                    JOptionPane.showMessageDialog(null, "terjadi kesalahan saat mengupdate data");
                }

                clearForm();
            } else if (e.getSource() == buttonHapus) {
                String query = "DELETE FROM dosen_pembimbing WHERE kode = '" + kode + "';";
                int deleteData = stmt.executeUpdate(query);

                if (deleteData > 0) {
                    JOptionPane.showMessageDialog(null, "Hapus data berhasil");
                } else {
                    JOptionPane.showMessageDialog(null, "terjadi kesalahan saat menghapus data");
                }

                clearForm();
            }

        } catch (Exception er) {
            if (er.getMessage().contains("Duplicate entry")) {
                JOptionPane.showMessageDialog(null, "Kode sudah ada", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(frame, "Terjadi error saat melakukan operasi", "Error",
                        JOptionPane.ERROR_MESSAGE);
                System.out.println(er.getMessage());
            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // TODO Auto-generated method stub
        int baris = table.rowAtPoint(e.getPoint());
        int kolom = 0;
        String nim = table.getValueAt(baris, kolom).toString();
        String nama = table.getValueAt(baris, kolom + 1).toString();
        String mhs = table.getValueAt(baris, kolom + 2).toString();
        String jurusan = table.getValueAt(baris, kolom + 3).toString();

        textKode.setText(nim);
        textNama.setText(nama);
        textMhs.setText(mhs);
        textJurusan.setText(jurusan);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        // TODO Auto-generated method stub
        // throw new UnsupportedOperationException("Unimplemented method
        // 'mousePressed'");
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // TODO Auto-generated method stub
        // throw new UnsupportedOperationException("Unimplemented method
        // 'mouseReleased'");
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // TODO Auto-generated method stub
        // throw new UnsupportedOperationException("Unimplemented method
        // 'mouseEntered'");
    }

    @Override
    public void mouseExited(MouseEvent e) {
        // TODO Auto-generated method stub
        // throw new UnsupportedOperationException("Unimplemented method
        // 'mouseExited'");
    }
}
