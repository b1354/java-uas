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
    private String DB_URL = "jdbc:mysql://localhost:3306/vis3_uas";
    private String DB_USER = "root";
    private String DB_PASS = "";

    public JButton buttonCari = new JButton("cari");
    public JButton buttonTambah = new JButton("tambah");
    public JButton buttonUbah = new JButton("ubah");
    public JButton buttonHapus = new JButton("hapus");

    public JTextField textNim = new JTextField();
    public JTextField textNama = new JTextField();
    public JTextField textJurusan = new JTextField();

    public JTable table;
    public JScrollPane sp;

    public Registrasi() {
        frame = new JFrame("Registrasi Mahsiswa");

        JLabel judul = new JLabel("Registrasi");
        JLabel labelNama = new JLabel("Nama");
        JLabel labelNim = new JLabel("NIM");
        JLabel labelJurusan = new JLabel("Jurusan");

        frame.setLayout(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        judul.setBounds(170, 20, 100, 20);
        frame.add(judul);

        // NIM
        labelNim.setBounds(45, 60, 40, 20);
        textNim.setBounds(100, 60, 190, 20);
        frame.add(labelNim);
        frame.add(textNim);

        // Nama
        labelNama.setBounds(45, 90, 40, 20);
        textNama.setBounds(100, 90, 260, 20);
        frame.add(labelNama);
        frame.add(textNama);

        // Jurusan
        labelJurusan.setBounds(45, 120, 60, 20);
        textJurusan.setBounds(100, 120, 260, 20);
        frame.add(labelJurusan);
        frame.add(textJurusan);

        // Cari
        buttonCari.setBounds(300, 60, 60, 20);
        buttonCari.addActionListener(this);
        frame.add(buttonCari);

        // Tambah
        buttonTambah.setBounds(100, 160, 80, 20);
        buttonTambah.addActionListener(this);
        frame.add(buttonTambah);

        // Hapus
        buttonHapus.setBounds(190, 160, 80, 20);
        buttonHapus.addActionListener(this);
        frame.add(buttonHapus);

        // Ubah
        buttonUbah.setBounds(280, 160, 80, 20);
        buttonUbah.addActionListener(this);
        frame.add(buttonUbah);

        // Membuat frame
        frame.setSize(410, 410);
        frame.setVisible(true);
        testConnection();

        loadData();
    }

    private void clearForm() {
        textNim.setText("");
        textNama.setText("");
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
        String nim = textNim.getText();
        String nama = textNama.getText();
        String jurusan = textJurusan.getText();

        if (nim.isEmpty() || nama.isEmpty() || jurusan.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Semua form harus diisi", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        } else if (nim.length() != 11) {
            JOptionPane.showMessageDialog(frame, "NIM harus 11 digit", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    private int countMhs() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            String query = "SELECT COUNT(*) AS jumlah FROM mahasiswa";
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
            String[] tableHeader = { "nim", "nama", "jurusan" };
            String query = "SELECT * FROM mahasiswa";
            Statement stmt = conn.createStatement();
            ResultSet data = stmt.executeQuery(query);

            int dataCount = 0;
            Object[][] tableData = new Object[countMhs()][3];

            while (data.next()) {

                tableData[dataCount][0] = data.getString("NIM");
                tableData[dataCount][1] = data.getString("nama");
                tableData[dataCount][2] = data.getString("jurusan");

                dataCount++;
            }

            table = new JTable(tableData, tableHeader);
            sp = new JScrollPane(table);

            table.addMouseListener(this);

            sp.setBounds(20, 200, 360, 150);
            frame.add(sp);
        } catch (Exception e) {
            System.out.println("Error : " + e.getMessage());
            JOptionPane.showMessageDialog(frame, "tidak dapat mengambil data", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String nim = textNim.getText();
        String nama = textNama.getText();
        String jurusan = textJurusan.getText();

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            Statement stmt = conn.createStatement();

            if (e.getSource() == buttonCari) {
                String query = "SELECT * FROM mahasiswa WHERE nim = '" + nim + "'";
                ResultSet Data = stmt.executeQuery(query);

                if (Data.next()) {
                    textNama.setText(Data.getString(2));
                    textJurusan.setText(Data.getString(3));
                } else {
                    JOptionPane.showMessageDialog(null, "Data tidak ditemukan");
                    clearForm();
                }
            } else if (e.getSource() == buttonTambah && validate()) {
                String query = "INSERT INTO mahasiswa (nim, nama, jurusan) VALUES ('" + nim + "'" + ", " + "'" + nama
                        + "'" + ", " + "'"
                        + jurusan + "');";
                int updateData = stmt.executeUpdate(query);

                if (updateData > 0) {
                    JOptionPane.showMessageDialog(null, "Tambah data berhasil");
                } else {
                    JOptionPane.showMessageDialog(null, "terjadi kesalahan saat menginput data");
                }

                clearForm();
            } else if (e.getSource() == buttonUbah && validate()) {
                String query = "UPDATE mahasiswa SET nama = '" + nama + "', jurusan = '" + jurusan
                        + "' WHERE nim = '" + nim + "';";
                int updateData = stmt.executeUpdate(query);

                if (updateData > 0) {
                    JOptionPane.showMessageDialog(null, "update data berhasil");
                } else {
                    JOptionPane.showMessageDialog(null, "terjadi kesalahan saat mengupdate data");
                }

                clearForm();
            } else if (e.getSource() == buttonHapus) {
                String query = "DELETE FROM mahasiswa WHERE nim = '" + nim + "';";
                int deleteData = stmt.executeUpdate(query);

                if (deleteData > 0) {
                    JOptionPane.showMessageDialog(null, "Hapus data berhasil");
                } else {
                    JOptionPane.showMessageDialog(null, "terjadi kesalahan saat menghapus data");
                }

                clearForm();
            }

        } catch (Exception er) {
            JOptionPane.showMessageDialog(frame, "Terjadi error saat melakukan operasi", "Error",
                    JOptionPane.ERROR_MESSAGE);
            System.out.println(er.getMessage());
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // TODO Auto-generated method stub
        int baris = table.rowAtPoint(e.getPoint());
        int kolom = 0;
        String nim = table.getValueAt(baris, kolom).toString();
        String nama = table.getValueAt(baris, kolom + 1).toString();
        String jurusan = table.getValueAt(baris, kolom + 2).toString();

        textNim.setText(nim);
        textNama.setText(nama);
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
