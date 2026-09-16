package view;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.SQLException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

import dao.UsuarioDAO;
import model.Usuario;
import util.Internacionalizacao;

/**
 * Tela de Login. Autentica o usuario via UsuarioDAO; se valido,
 * carrega o idioma de preferencia e abre o MenuPrincipal.
 */
public class TelaLogin extends JFrame {

    private static final long serialVersionUID = 1L;
    private JTextField txtLogin;
    private JPasswordField txtSenha;

    public TelaLogin() {
        super(Internacionalizacao.get("app.titulo"));
        construirUI();
    }

    private void construirUI() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(420, 280);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JLabel titulo = new JLabel(Internacionalizacao.get("login.titulo"), SwingConstants.CENTER);
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 18f));
        titulo.setBorder(BorderFactory.createEmptyBorder(15, 10, 5, 10));
        add(titulo, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        form.add(new JLabel(Internacionalizacao.get("login.usuario") + ":"), gbc);
        gbc.gridx = 1;
        txtLogin = new JTextField(15);
        form.add(txtLogin, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        form.add(new JLabel(Internacionalizacao.get("login.senha") + ":"), gbc);
        gbc.gridx = 1;
        txtSenha = new JPasswordField(15);
        form.add(txtSenha, gbc);

        add(form, BorderLayout.CENTER);

        JPanel botoes = new JPanel();
        JButton btnEntrar   = new JButton(Internacionalizacao.get("login.botao"));
        JButton btnCancelar = new JButton(Internacionalizacao.get("login.cancelar"));
        botoes.add(btnEntrar);
        botoes.add(btnCancelar);
        add(botoes, BorderLayout.SOUTH);

        btnEntrar.addActionListener(e -> efetuarLogin());
        btnCancelar.addActionListener(e -> System.exit(0));
        getRootPane().setDefaultButton(btnEntrar);
    }

    private void efetuarLogin() {
        String login = txtLogin.getText().trim();
        String senha = new String(txtSenha.getPassword());

        if (login.isEmpty() || senha.isEmpty()) {
            JOptionPane.showMessageDialog(this, Internacionalizacao.get("login.campos.obrigatorios"),
                    Internacionalizacao.get("login.titulo"), JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Usuario u = new UsuarioDAO().autenticar(login, senha);
            if (u == null) {
                JOptionPane.showMessageDialog(this, Internacionalizacao.get("login.erro"),
                        Internacionalizacao.get("login.titulo"), JOptionPane.ERROR_MESSAGE);
                return;
            }
            Internacionalizacao.setIdioma(u.getIdioma());
            dispose();
            new MenuPrincipal(u).setVisible(true);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    Internacionalizacao.get("msg.erro.geral") + " " + ex.getMessage(),
                    Internacionalizacao.get("login.titulo"), JOptionPane.ERROR_MESSAGE);
        }
    }
}
