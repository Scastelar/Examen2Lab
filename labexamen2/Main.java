package labexamen2;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.DefaultListModel;

public class Main extends JFrame implements ActionListener {

    PSNUsers psn;
    boolean active = false;

    JLabel titulo = new JLabel("Usuarios");
    JFrame addFrame, trophFrame;
    JPanel addPanel, trophPanel;
    JTextField userTxt, juegoTxt, trofeoTxt;
    JButton agregarUser = new JButton("Agregar Usuario");
    JButton agregarTrophy = new JButton("Agregar Trofeo");
    JButton eliminar = new JButton("Eliminar Usuario");
    JButton salir = new JButton("Salir");
    JButton addUser = new JButton("Agregar");
    JButton addTrofeo = new JButton("Agregar Trofeo");
    JTextArea area = new JTextArea();
    JList<String> tipoTrofeo, usuariosList;
    DefaultListModel<String> usuariosModel;

    public Main() {
        psn = new PSNUsers();
        addFrame = addUserFrame();
        trophFrame = addTrophyFrame();
        setSize(700, 550);
        setLayout(null);
        setLocationRelativeTo(this);
        getContentPane().setBackground(new Color(255, 249, 249));

        titulo.setBounds(300, 20, 250, 30);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 30));
        titulo.setForeground(new Color(242, 191, 191));
        add(titulo);

        agregarUser.setBounds(60, 80, 150, 30);
        agregarUser.addActionListener(this);
        add(agregarUser);

        agregarTrophy.setBounds(265, 80, 150, 30);
        agregarTrophy.addActionListener(this);
        add(agregarTrophy);

        eliminar.setBounds(470, 80, 150, 30);
        eliminar.addActionListener(this);
        add(eliminar);

        usuariosModel = new DefaultListModel<>();
        usuariosList = new JList<>(usuariosModel);
        usuariosList.addListSelectionListener(e -> updateText());
        JScrollPane usuariosScroll = new JScrollPane(usuariosList);
        usuariosScroll.setBounds(60, 150, 150, 100);
        add(usuariosScroll);

        area = new JTextArea(10, 30);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(area);
        scrollPane.setBounds(265, 150, 355, 300);
        add(scrollPane);

        salir.setBounds(360, 460, 150, 25);
        salir.addActionListener(this);
        add(salir);

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
        actualizar();
    }

    public void actionPerformed(ActionEvent e) {
        if (agregarUser == e.getSource()) {
            userTxt.setText("");
            addFrame.setVisible(true);
        
        } else if (agregarTrophy == e.getSource()) {
            if (psn.contar == 0) {
                JOptionPane.showMessageDialog(this, "No hay usuarios.");
                return;
            }
            juegoTxt.setText("");
            trofeoTxt.setText("");
            trophFrame.setVisible(true);
        
        } else if (eliminar == e.getSource()) {
            try {
                int choice = JOptionPane.showConfirmDialog(this, "Desea eliminar el usuario actual?");
                if (choice != JOptionPane.YES_OPTION) {
                    return;
                }
                String username = usuariosList.getSelectedValue();
                psn.deactivateUser(username);
                usuariosModel.removeElement(username); 
                actualizar();
                area.setText("Este Usuario fue desactivado");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        
        } else if (salir == e.getSource()) {
            JOptionPane.showMessageDialog(null, "Salida exitosa!");
            System.exit(0);
        
        } else if (addUser == e.getSource()) {
            String username = userTxt.getText().trim();
            if (username.isBlank()) {
                JOptionPane.showMessageDialog(this, "Error: Campo vacio.");
                return;
            }

            if (psn.addUser(username)) {
                JOptionPane.showMessageDialog(this, "Usuario agregado");
                addFrame.dispose();
                actualizar();
            }
        
        } else if (addTrofeo == e.getSource()) {
            String tipo = tipoTrofeo.getSelectedValue();
            Trophy trofeoTipo = Trophy.valueOf(tipo.toUpperCase());
            String juego = juegoTxt.getText().trim();
            String trofeoNombre = trofeoTxt.getText().trim();
            String username = usuariosList.getSelectedValue();

            if (juego.isBlank() || trofeoNombre.isBlank()) {
                JOptionPane.showMessageDialog(this, "Error: Campo vacio.");
                return;
            }

            psn.addTrophieTo(username, juego, trofeoNombre, trofeoTipo);
            trophFrame.dispose();
            updateText();
        }
    }

    private void actualizar() {
        active = false;
        Entry temp = psn.user.inicio;
        usuariosModel.clear();

        while (temp != null) {
            usuariosModel.addElement(temp.username);
            temp = temp.siguiente;
        }
        active = true;
        updateText();
    }

    private void updateText() {
        if (!usuariosList.isSelectionEmpty()) {
            String usuario = usuariosList.getSelectedValue();
            area.setText(psn.playerInfo(usuario));
        }
    }

    private JFrame addUserFrame() {
        JFrame frame = new JFrame("Agregar Usuarios");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(this);
        addPanel = new JPanel();
        JLabel label = new JLabel("Agregar Usuario");
        userTxt = new JTextField(20);

        addUser.addActionListener(this);

        addPanel.add(label);
        addPanel.add(userTxt);
        addPanel.add(addUser);

        frame.add(addPanel);
        frame.pack();
        return frame;
    }

    private JFrame addTrophyFrame() {
        JFrame frame = new JFrame("Agregar Trofeos");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(this);
        trophPanel = new JPanel();
        tipoTrofeo = new JList<>(new String[]{"Platino", "Oro", "Plata", "Bronce"});
        juegoTxt = new JTextField(20);
        trofeoTxt = new JTextField(20);

        addTrofeo.addActionListener(this);

        JScrollPane tipoScroll = new JScrollPane(tipoTrofeo);
        tipoScroll.setPreferredSize(new java.awt.Dimension(100, 80));

        trophPanel.add(new JLabel("Nombre del Juego"));
        trophPanel.add(juegoTxt);
        trophPanel.add(new JLabel("Nombre del Trofeo"));
        trophPanel.add(trofeoTxt);
        trophPanel.add(new JLabel("Tipo"));
        trophPanel.add(tipoScroll);
        trophPanel.add(addTrofeo);

        frame.add(trophPanel);
        frame.pack();
        return frame;
    }

    public static void main(String[] args) {
        Main frame = new Main();
    }
}