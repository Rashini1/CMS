import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

class MyFrame extends JFrame implements ActionListener {

    private DefaultListModel<String> listModel;
    private JList<String> contactList;

    JPanel mainPanel, inputPanel, buttonPanel;
    JTextField nameField, phoneField;
    JButton addButton, deleteButton;

    public MyFrame() {
        listModel = new DefaultListModel<>();
        contactList = new JList<>(listModel);

        nameField = new JTextField();
        phoneField = new JTextField();

        inputPanel = new JPanel(new GridLayout(2, 2));
        inputPanel.add(new JLabel("Name:"));
        inputPanel.add(nameField);
        inputPanel.add(new JLabel("Phone Number:"));
        inputPanel.add(phoneField);

        addButton = new JButton("Add Contact");
        deleteButton = new JButton("Delete Contact");

        addButton.addActionListener(this);
        deleteButton.addActionListener(this);

        buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);

        mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(inputPanel, BorderLayout.NORTH);
        mainPanel.add(new JScrollPane(contactList), BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        loadContactsFromDatabase();  // Load data when application starts

        this.add(mainPanel);
    }

    // Load contacts when opening the app
    private void loadContactsFromDatabase() {
        try {
            Connection con = DBConnection.getConnection();
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT name, phone FROM contacts");

            while (rs.next()) {
                listModel.addElement(rs.getString("name") + " - " + rs.getString("phone"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == addButton) {
            String name = nameField.getText();
            String phoneNumber = phoneField.getText();

            try {
                Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO contacts (name, phone) VALUES (?, ?)"
                );

                ps.setString(1, name);
                ps.setString(2, phoneNumber);
                ps.executeUpdate();

                listModel.addElement(name + " - " + phoneNumber);
                JOptionPane.showMessageDialog(null, "Contact added successfully!");

                nameField.setText("");
                phoneField.setText("");

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        if (e.getSource() == deleteButton) {
            int selectedIndex = contactList.getSelectedIndex();
            if (selectedIndex != -1) {

                String selectedValue = listModel.getElementAt(selectedIndex);
                String name = selectedValue.split(" - ")[0];

                try {
                    Connection con = DBConnection.getConnection();
                    PreparedStatement ps = con.prepareStatement(
                        "DELETE FROM contacts WHERE name = ?"
                    );
                    ps.setString(1, name);
                    ps.executeUpdate();

                    listModel.remove(selectedIndex);
                    JOptionPane.showMessageDialog(null, "Contact deleted successfully!");

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}

public class Main {
    public static void main(String[] args) {
        MyFrame frame = new MyFrame();
        frame.setVisible(true);
        frame.setTitle("Contact Management System");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
