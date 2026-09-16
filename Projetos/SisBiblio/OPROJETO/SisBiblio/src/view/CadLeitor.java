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
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

import dao.LeitorDAO;
import model.Leitor;
import util.Internacionalizacao;

/**
 * Cadastro de Leitor com CRUD e pesquisa padrao por nome
 * ou matricula. Estruturado em abas Cadastro e Pesquisa.
 */
public class CadLeitor extends JDialog {

    private static final long serialVersionUID = 1L;
    private final LeitorDAO dao = new LeitorDAO();

    private JTextField txtId, txtNome, txtCpf, txtEmail, txtTelefone, txtMatricula, txtCurso, txtPesquisa;
    private JTable tabela;
    private DefaultTableModel modelo;
    private JTabbedPane abas;

    public CadLeitor(Frame owner) {
        super(owner, Internacionalizacao.get("menu.leitor"), true);
        construirUI();
        carregarTabela("");
    }

    private void construirUI() {
        setSize(820, 540);
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
        gbc.gridx = 1; txtNome = new JTextField(30); p.add(txtNome, gbc); y++;

        gbc.gridx = 0; gbc.gridy = y; p.add(new JLabel(Internacionalizacao.get("campo.cpf") + ":"), gbc);
        gbc.gridx = 1; txtCpf = new JTextField(15); p.add(txtCpf, gbc); y++;

        gbc.gridx = 0; gbc.gridy = y; p.add(new JLabel(Internacionalizacao.get("campo.email") + ":"), gbc);
        gbc.gridx = 1; txtEmail = new JTextField(30); p.add(txtEmail, gbc); y++;

        gbc.gridx = 0; gbc.gridy = y; p.add(new JLabel(Internacionalizacao.get("campo.telefone") + ":"), gbc);
        gbc.gridx = 1; txtTelefone = new JTextField(15); p.add(txtTelefone, gbc); y++;

        gbc.gridx = 0; gbc.gridy = y; p.add(new JLabel(Internacionalizacao.get("campo.matricula") + ":"), gbc);
        gbc.gridx = 1; txtMatricula = new JTextField(15); p.add(txtMatricula, gbc); y++;

        gbc.gridx = 0; gbc.gridy = y; p.add(new JLabel(Internacionalizacao.get("campo.curso") + ":"), gbc);
        gbc.gridx = 1; txtCurso = new JTextField(25); p.add(txtCurso, gbc); y++;

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
                Internacionalizacao.get("campo.cpf"),
                Internacionalizacao.get("campo.matricula"),
                Internacionalizacao.get("campo.curso") }, 0) {
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
            List<Leitor> lista = dao.buscarPorTexto(f);
            for (Leitor l : lista) modelo.addRow(new Object[]{ l.getId(), l.getNome(), l.getCpf(), l.getMatricula(), l.getCurso() });
        } catch (SQLException ex) { erro(ex); }
    }

    private void selecionarParaEdicao() {
        int row = tabela.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, Internacionalizacao.get("msg.erro.selecionar")); return; }
        try {
            String mat = String.valueOf(modelo.getValueAt(row, 3));
            for (Leitor l : dao.buscarPorTexto(mat)) {
                if (l.getMatricula().equals(mat)) { preencherCampos(l); abas.setSelectedIndex(0); return; }
            }
        } catch (SQLException ex) { erro(ex); }
    }

    private void preencherCampos(Leitor l) {
        txtId.setText(String.valueOf(l.getId()));
        txtNome.setText(l.getNome());
        txtCpf.setText(l.getCpf());
        txtEmail.setText(l.getEmail());
        txtTelefone.setText(l.getTelefone());
        txtMatricula.setText(l.getMatricula());
        txtCurso.setText(l.getCurso());
    }

    private void limpar() {
        txtId.setText(""); txtNome.setText(""); txtCpf.setText(""); txtEmail.setText("");
        txtTelefone.setText(""); txtMatricula.setText(""); txtCurso.setText("");
        txtNome.requestFocus();
    }

    private Leitor lerCampos() {
        if (txtNome.getText().trim().isEmpty() || txtCpf.getText().trim().isEmpty() || txtMatricula.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, Internacionalizacao.get("msg.erro.campos"));
            return null;
        }
        Leitor l = new Leitor();
        l.setNome(txtNome.getText().trim());
        l.setCpf(txtCpf.getText().trim());
        l.setEmail(txtEmail.getText().trim());
        l.setTelefone(txtTelefone.getText().trim());
        l.setMatricula(txtMatricula.getText().trim());
        l.setCurso(txtCurso.getText().trim());
        return l;
    }

    private void salvar() {
        Leitor l = lerCampos(); if (l == null) return;
        try {
            if (dao.existeCpfOuMatricula(l.getCpf(), l.getMatricula(), 0)) {
                JOptionPane.showMessageDialog(this, Internacionalizacao.get("msg.erro.duplicado")); return;
            }
            dao.inserir(l);
            JOptionPane.showMessageDialog(this, Internacionalizacao.get("msg.sucesso.salvar"));
            limpar(); carregarTabela("");
        } catch (SQLException ex) { erro(ex); }
    }

    private void alterar() {
        if (txtId.getText().isEmpty()) { JOptionPane.showMessageDialog(this, Internacionalizacao.get("msg.erro.selecionar")); return; }
        Leitor l = lerCampos(); if (l == null) return;
        l.setId(Integer.parseInt(txtId.getText()));
        try {
            if (dao.existeCpfOuMatricula(l.getCpf(), l.getMatricula(), l.getId())) {
                JOptionPane.showMessageDialog(this, Internacionalizacao.get("msg.erro.duplicado")); return;
            }
            dao.alterar(l);
            JOptionPane.showMessageDialog(this, Internacionalizacao.get("msg.sucesso.alterar"));
            limpar(); carregarTabela("");
        } catch (SQLException ex) { erro(ex); }
    }

    private void excluir() {
        if (txtId.getText().isEmpty()) { JOptionPane.showMessageDialog(this, Internacionalizacao.get("msg.erro.selecionar")); return; }
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
