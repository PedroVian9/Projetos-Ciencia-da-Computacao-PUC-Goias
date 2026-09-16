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

import dao.CategoriaDAO;
import model.Categoria;
import util.Internacionalizacao;

/**
 * Cadastro basico de Categoria com CRUD e pesquisa padrao
 * por nome. Estruturado em abas Cadastro e Pesquisa.
 */
public class CadCategoria extends JDialog {

    private static final long serialVersionUID = 1L;
    private final CategoriaDAO dao = new CategoriaDAO();

    private JTextField txtId, txtNome, txtDescricao, txtPesquisa;
    private JTable tabela;
    private DefaultTableModel modelo;
    private JTabbedPane abas;

    public CadCategoria(Frame owner) {
        super(owner, Internacionalizacao.get("menu.categoria"), true);
        construirUI();
        carregarTabela("");
    }

    private void construirUI() {
        setSize(700, 480);
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
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0; p.add(new JLabel(Internacionalizacao.get("campo.id") + ":"), gbc);
        gbc.gridx = 1; txtId = new JTextField(6); txtId.setEditable(false); p.add(txtId, gbc);

        gbc.gridx = 0; gbc.gridy = 1; p.add(new JLabel(Internacionalizacao.get("campo.nome") + ":"), gbc);
        gbc.gridx = 1; txtNome = new JTextField(25); p.add(txtNome, gbc);

        gbc.gridx = 0; gbc.gridy = 2; p.add(new JLabel(Internacionalizacao.get("campo.descricao") + ":"), gbc);
        gbc.gridx = 1; txtDescricao = new JTextField(30); p.add(txtDescricao, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
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
                Internacionalizacao.get("campo.descricao") }, 0) {
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
            List<Categoria> lista = dao.buscarPorNome(f);
            for (Categoria c : lista) modelo.addRow(new Object[]{ c.getId(), c.getNome(), c.getDescricao() });
        } catch (SQLException ex) { erro(ex); }
    }

    private void selecionarParaEdicao() {
        int row = tabela.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, Internacionalizacao.get("msg.erro.selecionar")); return; }
        txtId.setText(String.valueOf(modelo.getValueAt(row, 0)));
        txtNome.setText(String.valueOf(modelo.getValueAt(row, 1)));
        txtDescricao.setText(String.valueOf(modelo.getValueAt(row, 2)));
        abas.setSelectedIndex(0);
    }

    private void limpar() {
        txtId.setText(""); txtNome.setText(""); txtDescricao.setText("");
        txtNome.requestFocus();
    }

    private boolean validar() {
        if (txtNome.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, Internacionalizacao.get("msg.erro.campos"));
            return false;
        }
        return true;
    }

    private void salvar() {
        if (!validar()) return;
        try {
            if (dao.existeNome(txtNome.getText().trim(), 0)) {
                JOptionPane.showMessageDialog(this, Internacionalizacao.get("msg.erro.duplicado")); return;
            }
            dao.inserir(new Categoria(0, txtNome.getText().trim(), txtDescricao.getText().trim()));
            JOptionPane.showMessageDialog(this, Internacionalizacao.get("msg.sucesso.salvar"));
            limpar(); carregarTabela("");
        } catch (SQLException ex) { erro(ex); }
    }

    private void alterar() {
        if (txtId.getText().isEmpty()) { JOptionPane.showMessageDialog(this, Internacionalizacao.get("msg.erro.selecionar")); return; }
        if (!validar()) return;
        try {
            int id = Integer.parseInt(txtId.getText());
            if (dao.existeNome(txtNome.getText().trim(), id)) {
                JOptionPane.showMessageDialog(this, Internacionalizacao.get("msg.erro.duplicado")); return;
            }
            dao.alterar(new Categoria(id, txtNome.getText().trim(), txtDescricao.getText().trim()));
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
