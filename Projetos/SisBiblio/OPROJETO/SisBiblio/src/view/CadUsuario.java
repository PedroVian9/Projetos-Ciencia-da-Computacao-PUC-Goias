package view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.SQLException;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

import dao.UsuarioDAO;
import model.Usuario;
import util.Internacionalizacao;

/**
 * Cadastro de Usuarios do sistema (login/senha). Inclui a
 * configuracao de idioma (PT/EN) atendendo ao requisito de
 * internacionalizacao via janela de cadastro.
 */
public class CadUsuario extends JDialog {

    private static final long serialVersionUID = 1L;
    private final UsuarioDAO dao = new UsuarioDAO();
    private final Usuario logado;

    private JTextField txtId, txtNome, txtLogin, txtEmail, txtPesquisa;
    private JPasswordField txtSenha;
    private JComboBox<String> cbIdioma;
    private JTable tabela;
    private DefaultTableModel modelo;
    private JTabbedPane abas;

    public CadUsuario(Frame owner, Usuario logado) {
        super(owner, Internacionalizacao.get("menu.usuarios"), true);
        this.logado = logado;
        construirUI();
        carregarTabela("");
    }

    private void construirUI() {
        setSize(760, 500);
        setLocationRelativeTo(getOwner());
        setLayout(new BorderLayout());
        abas = new JTabbedPane();
        abas.addTab(Internacionalizacao.get("aba.cadastro"), painelCad());
        abas.addTab(Internacionalizacao.get("aba.pesquisa"), painelPesq());
        add(abas);
    }

    private JPanel painelCad() {
        JPanel p = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.anchor = GridBagConstraints.WEST;

        int y = 0;
        gbc.gridx = 0; gbc.gridy = y; p.add(new JLabel(Internacionalizacao.get("campo.id") + ":"), gbc);
        gbc.gridx = 1; txtId = new JTextField(6); txtId.setEditable(false); p.add(txtId, gbc); y++;

        gbc.gridx = 0; gbc.gridy = y; p.add(new JLabel(Internacionalizacao.get("campo.nome") + ":"), gbc);
        gbc.gridx = 1; txtNome = new JTextField(25); p.add(txtNome, gbc); y++;

        gbc.gridx = 0; gbc.gridy = y; p.add(new JLabel(Internacionalizacao.get("campo.login") + ":"), gbc);
        gbc.gridx = 1; txtLogin = new JTextField(15); p.add(txtLogin, gbc); y++;

        gbc.gridx = 0; gbc.gridy = y; p.add(new JLabel(Internacionalizacao.get("campo.senha") + ":"), gbc);
        gbc.gridx = 1; txtSenha = new JPasswordField(15); p.add(txtSenha, gbc); y++;

        gbc.gridx = 0; gbc.gridy = y; p.add(new JLabel(Internacionalizacao.get("campo.email") + ":"), gbc);
        gbc.gridx = 1; txtEmail = new JTextField(25); p.add(txtEmail, gbc); y++;

        gbc.gridx = 0; gbc.gridy = y; p.add(new JLabel(Internacionalizacao.get("campo.idioma") + ":"), gbc);
        gbc.gridx = 1; cbIdioma = new JComboBox<>(new String[]{
                Internacionalizacao.get("i18n.pt"),
                Internacionalizacao.get("i18n.en") });
        p.add(cbIdioma, gbc); y++;

        gbc.gridx = 0; gbc.gridy = y; gbc.gridwidth = 2;
        JPanel bot = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnNovo    = new JButton(Internacionalizacao.get("btn.novo"));
        JButton btnSalvar  = new JButton(Internacionalizacao.get("btn.salvar"));
        JButton btnAlterar = new JButton(Internacionalizacao.get("btn.alterar"));
        JButton btnExcluir = new JButton(Internacionalizacao.get("btn.excluir"));
        JButton btnFechar  = new JButton(Internacionalizacao.get("btn.fechar"));
        bot.add(btnNovo); bot.add(btnSalvar); bot.add(btnAlterar); bot.add(btnExcluir); bot.add(btnFechar);
        p.add(bot, gbc);

        btnNovo.addActionListener(e -> limpar());
        btnSalvar.addActionListener(e -> salvar());
        btnAlterar.addActionListener(e -> alterar());
        btnExcluir.addActionListener(e -> excluir());
        btnFechar.addActionListener(e -> dispose());

        return p;
    }

    private JPanel painelPesq() {
        JPanel p = new JPanel(new BorderLayout(5, 5));
        JPanel topo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topo.add(new JLabel(Internacionalizacao.get("btn.pesquisar") + ":"));
        txtPesquisa = new JTextField(25);
        topo.add(txtPesquisa);
        p.add(topo, BorderLayout.NORTH);

        modelo = new DefaultTableModel(new Object[]{
                Internacionalizacao.get("campo.id"),
                Internacionalizacao.get("campo.nome"),
                Internacionalizacao.get("campo.login"),
                Internacionalizacao.get("campo.email"),
                Internacionalizacao.get("campo.idioma") }, 0) {
            private static final long serialVersionUID = 1L;
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tabela = new JTable(modelo);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        p.add(new JScrollPane(tabela), BorderLayout.CENTER);

        JPanel sul = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton sel = new JButton(Internacionalizacao.get("btn.selecionar"));
        sul.add(sel);
        p.add(sul, BorderLayout.SOUTH);

        txtPesquisa.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e)  { carregarTabela(txtPesquisa.getText()); }
            public void removeUpdate(DocumentEvent e)  { carregarTabela(txtPesquisa.getText()); }
            public void changedUpdate(DocumentEvent e) { carregarTabela(txtPesquisa.getText()); }
        });
        sel.addActionListener(e -> selecionarParaEdicao());
        tabela.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) selecionarParaEdicao();
            }
        });
        return p;
    }

    private void carregarTabela(String f) {
        try {
            modelo.setRowCount(0);
            List<Usuario> lista = dao.buscarPorTexto(f);
            for (Usuario u : lista) {
                modelo.addRow(new Object[]{ u.getId(), u.getNome(), u.getLogin(), u.getEmail(), u.getIdioma() });
            }
        } catch (SQLException ex) { erro(ex); }
    }

    private void selecionarParaEdicao() {
        int row = tabela.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, Internacionalizacao.get("msg.erro.selecionar")); return; }
        try {
            int id = (int) modelo.getValueAt(row, 0);
            for (Usuario u : dao.listarTodos()) {
                if (u.getId() == id) { preencher(u); abas.setSelectedIndex(0); return; }
            }
        } catch (SQLException ex) { erro(ex); }
    }

    private void preencher(Usuario u) {
        txtId.setText(String.valueOf(u.getId()));
        txtNome.setText(u.getNome());
        txtLogin.setText(u.getLogin());
        txtSenha.setText(u.getSenha());
        txtEmail.setText(u.getEmail());
        cbIdioma.setSelectedIndex("en_US".equalsIgnoreCase(u.getIdioma()) ? 1 : 0);
    }

    private void limpar() {
        txtId.setText(""); txtNome.setText(""); txtLogin.setText("");
        txtSenha.setText(""); txtEmail.setText("");
        cbIdioma.setSelectedIndex(0);
        txtNome.requestFocus();
    }

    private Usuario lerCampos() {
        String nome = txtNome.getText().trim();
        String login = txtLogin.getText().trim();
        String senha = new String(txtSenha.getPassword());
        if (nome.isEmpty() || login.isEmpty() || senha.isEmpty()) {
            JOptionPane.showMessageDialog(this, Internacionalizacao.get("msg.erro.campos"));
            return null;
        }
        Usuario u = new Usuario();
        u.setNome(nome);
        u.setLogin(login);
        u.setSenha(senha);
        u.setEmail(txtEmail.getText().trim());
        u.setIdioma(cbIdioma.getSelectedIndex() == 1 ? "en_US" : "pt_BR");
        return u;
    }

    private void salvar() {
        Usuario u = lerCampos(); if (u == null) return;
        try {
            if (dao.existeLogin(u.getLogin(), 0)) {
                JOptionPane.showMessageDialog(this, Internacionalizacao.get("msg.erro.duplicado")); return;
            }
            dao.inserir(u);
            JOptionPane.showMessageDialog(this, Internacionalizacao.get("msg.sucesso.salvar"));
            limpar(); carregarTabela("");
        } catch (SQLException ex) { erro(ex); }
    }

    private void alterar() {
        if (txtId.getText().isEmpty()) { JOptionPane.showMessageDialog(this, Internacionalizacao.get("msg.erro.selecionar")); return; }
        Usuario u = lerCampos(); if (u == null) return;
        u.setId(Integer.parseInt(txtId.getText()));
        try {
            if (dao.existeLogin(u.getLogin(), u.getId())) {
                JOptionPane.showMessageDialog(this, Internacionalizacao.get("msg.erro.duplicado")); return;
            }
            dao.alterar(u);
            if (logado != null && logado.getId() == u.getId()) {
                Internacionalizacao.setIdioma(u.getIdioma());
                JOptionPane.showMessageDialog(this, Internacionalizacao.get("i18n.trocado"));
            } else {
                JOptionPane.showMessageDialog(this, Internacionalizacao.get("msg.sucesso.alterar"));
            }
            limpar(); carregarTabela("");
        } catch (SQLException ex) { erro(ex); }
    }

    private void excluir() {
        if (txtId.getText().isEmpty()) { JOptionPane.showMessageDialog(this, Internacionalizacao.get("msg.erro.selecionar")); return; }
        if (logado != null && logado.getId() == Integer.parseInt(txtId.getText())) {
            JOptionPane.showMessageDialog(this, Internacionalizacao.get("msg.erro.geral") + " (auto-exclusao)"); return;
        }
        int op = JOptionPane.showConfirmDialog(this, Internacionalizacao.get("msg.confirma.excluir"), "?", JOptionPane.YES_NO_OPTION);
        if (op != JOptionPane.YES_OPTION) return;
        try {
            dao.excluir(Integer.parseInt(txtId.getText()));
            JOptionPane.showMessageDialog(this, Internacionalizacao.get("msg.sucesso.excluir"));
            limpar(); carregarTabela("");
        } catch (SQLException ex) { erro(ex); }
    }

    private void erro(Exception ex) {
        JOptionPane.showMessageDialog(this,
                Internacionalizacao.get("msg.erro.geral") + " " + ex.getMessage(),
                "Erro", JOptionPane.ERROR_MESSAGE);
    }
}
