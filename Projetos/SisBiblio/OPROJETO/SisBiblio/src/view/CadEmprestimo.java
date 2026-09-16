package view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import dao.EmprestimoDAO;
import dao.LeitorDAO;
import dao.LivroDAO;
import model.Emprestimo;
import model.Leitor;
import model.Livro;
import util.Internacionalizacao;

/**
 * Cadastro intermediario de Emprestimo. Vincula leitor e livro
 * (combobox), controla as datas e o status, atualiza estoque
 * (qtd_disponivel) e oferece pesquisa padrao por leitor/livro.
 */
public class CadEmprestimo extends JDialog {

    private static final long serialVersionUID = 1L;
    private final EmprestimoDAO dao = new EmprestimoDAO();
    private final LeitorDAO leitorDao = new LeitorDAO();
    private final LivroDAO  livroDao  = new LivroDAO();

    private JTextField txtId, txtDataEmp, txtDataPrev, txtDataDev, txtPesquisa;
    private JComboBox<Leitor> cbLeitor;
    private JComboBox<Livro>  cbLivro;
    private JComboBox<String> cbStatus;
    private JTable tabela;
    private DefaultTableModel modelo;
    private JTabbedPane abas;

    public CadEmprestimo(Frame owner) {
        super(owner, Internacionalizacao.get("menu.emprestimo"), true);
        construirUI();
        carregarTabela();
    }

    private void construirUI() {
        setSize(900, 560);
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

        gbc.gridx = 0; gbc.gridy = y; p.add(new JLabel(Internacionalizacao.get("campo.leitor") + ":"), gbc);
        gbc.gridx = 1; cbLeitor = new JComboBox<>(); p.add(cbLeitor, gbc); y++;

        gbc.gridx = 0; gbc.gridy = y; p.add(new JLabel(Internacionalizacao.get("campo.livro") + ":"), gbc);
        gbc.gridx = 1; cbLivro = new JComboBox<>(); p.add(cbLivro, gbc); y++;

        gbc.gridx = 0; gbc.gridy = y; p.add(new JLabel(Internacionalizacao.get("campo.dataEmp") + " (AAAA-MM-DD):"), gbc);
        gbc.gridx = 1; txtDataEmp = new JTextField(12); txtDataEmp.setText(LocalDate.now().toString()); p.add(txtDataEmp, gbc); y++;

        gbc.gridx = 0; gbc.gridy = y; p.add(new JLabel(Internacionalizacao.get("campo.dataPrev") + " (AAAA-MM-DD):"), gbc);
        gbc.gridx = 1; txtDataPrev = new JTextField(12); txtDataPrev.setText(LocalDate.now().plusDays(14).toString()); p.add(txtDataPrev, gbc); y++;

        gbc.gridx = 0; gbc.gridy = y; p.add(new JLabel(Internacionalizacao.get("campo.dataDev") + " (AAAA-MM-DD):"), gbc);
        gbc.gridx = 1; txtDataDev = new JTextField(12); p.add(txtDataDev, gbc); y++;

        gbc.gridx = 0; gbc.gridy = y; p.add(new JLabel(Internacionalizacao.get("campo.status") + ":"), gbc);
        gbc.gridx = 1; cbStatus = new JComboBox<>(new String[]{ Emprestimo.STATUS_ABERTO, Emprestimo.STATUS_DEVOLVIDO });
        p.add(cbStatus, gbc); y++;

        gbc.gridx = 0; gbc.gridy = y; gbc.gridwidth = 2;
        JPanel bot = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnNovo    = new JButton(Internacionalizacao.get("btn.novo"));
        JButton btnSalvar  = new JButton(Internacionalizacao.get("btn.salvar"));
        JButton btnAlterar = new JButton(Internacionalizacao.get("btn.alterar"));
        JButton btnDevolver= new JButton(Internacionalizacao.get("btn.devolver"));
        JButton btnExcluir = new JButton(Internacionalizacao.get("btn.excluir"));
        JButton btnFechar  = new JButton(Internacionalizacao.get("btn.fechar"));
        bot.add(btnNovo); bot.add(btnSalvar); bot.add(btnAlterar); bot.add(btnDevolver); bot.add(btnExcluir); bot.add(btnFechar);
        p.add(bot, gbc);

        carregarCombos();

        btnNovo.addActionListener(e -> limpar());
        btnSalvar.addActionListener(e -> salvar());
        btnAlterar.addActionListener(e -> alterar());
        btnDevolver.addActionListener(e -> devolver());
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
        JButton btnBuscar = new JButton(Internacionalizacao.get("btn.pesquisar"));
        topo.add(btnBuscar);
        p.add(topo, BorderLayout.NORTH);

        modelo = new DefaultTableModel(new Object[]{
                Internacionalizacao.get("campo.id"),
                Internacionalizacao.get("campo.dataEmp"),
                Internacionalizacao.get("campo.dataPrev"),
                Internacionalizacao.get("campo.dataDev"),
                Internacionalizacao.get("campo.status"),
                Internacionalizacao.get("campo.leitor"),
                Internacionalizacao.get("campo.livro") }, 0) {
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

        btnBuscar.addActionListener(e -> carregarTabela());
        sel.addActionListener(e -> selecionarParaEdicao());
        tabela.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) selecionarParaEdicao();
            }
        });
        return p;
    }

    private void carregarCombos() {
        try {
            cbLeitor.removeAllItems();
            for (Leitor l : leitorDao.listarTodos()) cbLeitor.addItem(l);
            cbLivro.removeAllItems();
            for (Livro l : livroDao.listarTodos()) cbLivro.addItem(l);
        } catch (SQLException ex) { erro(ex); }
    }

    private void carregarTabela() {
        try {
            modelo.setRowCount(0);
            String filtro = txtPesquisa == null ? "" : txtPesquisa.getText().toLowerCase();
            for (Emprestimo e : dao.listarOrdenado(EmprestimoDAO.ORD_DATA)) {
                String linhaTxt = (e.getLeitor().getNome() + " " + e.getLivro().getTitulo()).toLowerCase();
                if (filtro.isEmpty() || linhaTxt.contains(filtro)) {
                    modelo.addRow(new Object[]{
                            e.getId(), e.getDataEmprestimo(), e.getDataPrevDevolucao(),
                            e.getDataDevolucao(), e.getStatus(),
                            e.getLeitor().getNome(), e.getLivro().getTitulo() });
                }
            }
        } catch (SQLException ex) { erro(ex); }
    }

    private void selecionarParaEdicao() {
        int row = tabela.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, Internacionalizacao.get("msg.erro.selecionar")); return; }
        try {
            int id = (int) modelo.getValueAt(row, 0);
            for (Emprestimo e : dao.listarOrdenado(EmprestimoDAO.ORD_DATA)) {
                if (e.getId() == id) { preencher(e); abas.setSelectedIndex(0); return; }
            }
        } catch (SQLException ex) { erro(ex); }
    }

    private void preencher(Emprestimo e) {
        txtId.setText(String.valueOf(e.getId()));
        txtDataEmp.setText(String.valueOf(e.getDataEmprestimo()));
        txtDataPrev.setText(String.valueOf(e.getDataPrevDevolucao()));
        txtDataDev.setText(e.getDataDevolucao() == null ? "" : e.getDataDevolucao().toString());
        cbStatus.setSelectedItem(e.getStatus());
        selecionarLeitor(e.getLeitor().getId());
        selecionarLivro(e.getLivro().getId());
    }

    private void selecionarLeitor(int id) {
        for (int i = 0; i < cbLeitor.getItemCount(); i++)
            if (cbLeitor.getItemAt(i).getId() == id) { cbLeitor.setSelectedIndex(i); return; }
    }
    private void selecionarLivro(int id) {
        for (int i = 0; i < cbLivro.getItemCount(); i++)
            if (cbLivro.getItemAt(i).getId() == id) { cbLivro.setSelectedIndex(i); return; }
    }

    private void limpar() {
        txtId.setText("");
        txtDataEmp.setText(LocalDate.now().toString());
        txtDataPrev.setText(LocalDate.now().plusDays(14).toString());
        txtDataDev.setText("");
        cbStatus.setSelectedItem(Emprestimo.STATUS_ABERTO);
    }

    private Emprestimo lerCampos() {
        if (cbLeitor.getSelectedItem() == null || cbLivro.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, Internacionalizacao.get("msg.erro.campos"));
            return null;
        }
        try {
            Emprestimo e = new Emprestimo();
            e.setLeitor((Leitor) cbLeitor.getSelectedItem());
            e.setLivro((Livro) cbLivro.getSelectedItem());
            e.setDataEmprestimo(Date.valueOf(txtDataEmp.getText().trim()));
            e.setDataPrevDevolucao(Date.valueOf(txtDataPrev.getText().trim()));
            e.setDataDevolucao(txtDataDev.getText().trim().isEmpty() ? null : Date.valueOf(txtDataDev.getText().trim()));
            e.setStatus(String.valueOf(cbStatus.getSelectedItem()));
            return e;
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, Internacionalizacao.get("msg.erro.campos") + " (data AAAA-MM-DD)");
            return null;
        }
    }

    private void salvar() {
        Emprestimo e = lerCampos(); if (e == null) return;
        if (e.getLivro().getQtdDisponivel() <= 0) {
            JOptionPane.showMessageDialog(this, Internacionalizacao.get("msg.erro.estoque")); return;
        }
        try {
            dao.inserir(e);
            livroDao.decrementarDisponivel(e.getLivro().getId());
            JOptionPane.showMessageDialog(this, Internacionalizacao.get("msg.sucesso.salvar"));
            limpar(); carregarCombos(); carregarTabela();
        } catch (SQLException ex) { erro(ex); }
    }

    private void alterar() {
        if (txtId.getText().isEmpty()) { JOptionPane.showMessageDialog(this, Internacionalizacao.get("msg.erro.selecionar")); return; }
        Emprestimo e = lerCampos(); if (e == null) return;
        e.setId(Integer.parseInt(txtId.getText()));
        try {
            dao.alterar(e);
            JOptionPane.showMessageDialog(this, Internacionalizacao.get("msg.sucesso.alterar"));
            limpar(); carregarTabela();
        } catch (SQLException ex) { erro(ex); }
    }

    private void devolver() {
        if (txtId.getText().isEmpty()) { JOptionPane.showMessageDialog(this, Internacionalizacao.get("msg.erro.selecionar")); return; }
        Emprestimo e = lerCampos(); if (e == null) return;
        if (Emprestimo.STATUS_DEVOLVIDO.equals(e.getStatus())) {
            JOptionPane.showMessageDialog(this, Internacionalizacao.get("msg.erro.geral")); return;
        }
        try {
            e.setId(Integer.parseInt(txtId.getText()));
            e.setDataDevolucao(Date.valueOf(LocalDate.now()));
            e.setStatus(Emprestimo.STATUS_DEVOLVIDO);
            dao.alterar(e);
            livroDao.incrementarDisponivel(e.getLivro().getId());
            JOptionPane.showMessageDialog(this, Internacionalizacao.get("msg.sucesso.devolucao"));
            limpar(); carregarCombos(); carregarTabela();
        } catch (SQLException ex) { erro(ex); }
    }

    private void excluir() {
        if (txtId.getText().isEmpty()) { JOptionPane.showMessageDialog(this, Internacionalizacao.get("msg.erro.selecionar")); return; }
        int op = JOptionPane.showConfirmDialog(this, Internacionalizacao.get("msg.confirma.excluir"), "?", JOptionPane.YES_NO_OPTION);
        if (op != JOptionPane.YES_OPTION) return;
        try {
            dao.excluir(Integer.parseInt(txtId.getText()));
            JOptionPane.showMessageDialog(this, Internacionalizacao.get("msg.sucesso.excluir"));
            limpar(); carregarTabela();
        } catch (SQLException ex) { erro(ex); }
    }

    private void erro(Exception ex) {
        JOptionPane.showMessageDialog(this,
                Internacionalizacao.get("msg.erro.geral") + " " + ex.getMessage(),
                "Erro", JOptionPane.ERROR_MESSAGE);
    }
}
