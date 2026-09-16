package view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.SQLException;
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

import dao.AutorDAO;
import dao.CategoriaDAO;
import dao.LivroDAO;
import model.Autor;
import model.Categoria;
import model.Livro;
import util.Internacionalizacao;

/**
 * Cadastro intermediario de Livro com CRUD. Possui pesquisa
 * complexa por codigo, titulo ou ISBN (via combobox + textfield)
 * e relacionamento com Autor e Categoria atraves de comboboxes.
 */
public class CadLivro extends JDialog {

    private static final long serialVersionUID = 1L;
    private final LivroDAO dao = new LivroDAO();
    private final AutorDAO autorDao = new AutorDAO();
    private final CategoriaDAO catDao = new CategoriaDAO();

    private JTextField txtId, txtTitulo, txtIsbn, txtAno, txtEditora, txtQtdTotal, txtQtdDisp;
    private JComboBox<Autor> cbAutor;
    private JComboBox<Categoria> cbCategoria;

    private JComboBox<String> cbFiltro;
    private JTextField txtValor;
    private JTable tabela;
    private DefaultTableModel modelo;
    private JTabbedPane abas;

    public CadLivro(Frame owner) {
        super(owner, Internacionalizacao.get("menu.livro"), true);
        construirUI();
        carregarTabela(LivroDAO.FILTRO_TODOS, "");
    }

    private void construirUI() {
        setSize(840, 560);
        setLocationRelativeTo(getOwner());
        setLayout(new BorderLayout());
        abas = new JTabbedPane();
        abas.addTab(Internacionalizacao.get("aba.cadastro"), painelCadastro());
        abas.addTab(Internacionalizacao.get("aba.pesquisa"), painelPesquisaComplexa());
        add(abas);
    }

    private JPanel painelCadastro() {
        JPanel p = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.anchor = GridBagConstraints.WEST;

        int y = 0;
        gbc.gridx = 0; gbc.gridy = y; p.add(new JLabel(Internacionalizacao.get("campo.id") + ":"), gbc);
        gbc.gridx = 1; txtId = new JTextField(6); txtId.setEditable(false); p.add(txtId, gbc); y++;

        gbc.gridx = 0; gbc.gridy = y; p.add(new JLabel(Internacionalizacao.get("campo.titulo") + ":"), gbc);
        gbc.gridx = 1; txtTitulo = new JTextField(30); p.add(txtTitulo, gbc); y++;

        gbc.gridx = 0; gbc.gridy = y; p.add(new JLabel(Internacionalizacao.get("campo.isbn") + ":"), gbc);
        gbc.gridx = 1; txtIsbn = new JTextField(18); p.add(txtIsbn, gbc); y++;

        gbc.gridx = 0; gbc.gridy = y; p.add(new JLabel(Internacionalizacao.get("campo.ano") + ":"), gbc);
        gbc.gridx = 1; txtAno = new JTextField(6); apenasNumeros(txtAno); p.add(txtAno, gbc); y++;

        gbc.gridx = 0; gbc.gridy = y; p.add(new JLabel(Internacionalizacao.get("campo.editora") + ":"), gbc);
        gbc.gridx = 1; txtEditora = new JTextField(25); p.add(txtEditora, gbc); y++;

        gbc.gridx = 0; gbc.gridy = y; p.add(new JLabel(Internacionalizacao.get("campo.qtdTotal") + ":"), gbc);
        gbc.gridx = 1; txtQtdTotal = new JTextField(4); apenasNumeros(txtQtdTotal); p.add(txtQtdTotal, gbc); y++;

        gbc.gridx = 0; gbc.gridy = y; p.add(new JLabel(Internacionalizacao.get("campo.qtdDisponivel") + ":"), gbc);
        gbc.gridx = 1; txtQtdDisp = new JTextField(4); apenasNumeros(txtQtdDisp); p.add(txtQtdDisp, gbc); y++;

        gbc.gridx = 0; gbc.gridy = y; p.add(new JLabel(Internacionalizacao.get("campo.autor") + ":"), gbc);
        gbc.gridx = 1; cbAutor = new JComboBox<>(); p.add(cbAutor, gbc); y++;

        gbc.gridx = 0; gbc.gridy = y; p.add(new JLabel(Internacionalizacao.get("campo.categoria") + ":"), gbc);
        gbc.gridx = 1; cbCategoria = new JComboBox<>(); p.add(cbCategoria, gbc); y++;

        carregarCombos();

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

    private JPanel painelPesquisaComplexa() {
        JPanel p = new JPanel(new BorderLayout(5, 5));

        JPanel topo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topo.add(new JLabel(Internacionalizacao.get("pesquisa.por") + ":"));
        cbFiltro = new JComboBox<>(new String[]{
                Internacionalizacao.get("pesquisa.todos"),
                Internacionalizacao.get("pesquisa.codigo"),
                Internacionalizacao.get("pesquisa.titulo"),
                Internacionalizacao.get("pesquisa.isbn")
        });
        topo.add(cbFiltro);
        txtValor = new JTextField(20);
        topo.add(txtValor);
        JButton btnBuscar = new JButton(Internacionalizacao.get("btn.pesquisar"));
        topo.add(btnBuscar);
        p.add(topo, BorderLayout.NORTH);

        modelo = new DefaultTableModel(new Object[]{
                Internacionalizacao.get("campo.id"),
                Internacionalizacao.get("campo.titulo"),
                Internacionalizacao.get("campo.isbn"),
                Internacionalizacao.get("campo.ano"),
                Internacionalizacao.get("campo.editora"),
                Internacionalizacao.get("campo.autor"),
                Internacionalizacao.get("campo.categoria"),
                Internacionalizacao.get("campo.qtdTotal"),
                Internacionalizacao.get("campo.qtdDisponivel")
        }, 0) {
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

        btnBuscar.addActionListener(e -> executarPesquisa());
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
            cbAutor.removeAllItems();
            for (Autor a : autorDao.listarTodos()) cbAutor.addItem(a);

            cbCategoria.removeAllItems();
            for (Categoria c : catDao.listarTodos()) cbCategoria.addItem(c);
        } catch (SQLException ex) { erro(ex); }
    }

    private void executarPesquisa() {
        String sel = String.valueOf(cbFiltro.getSelectedItem());
        String tipo;
        if (sel.equals(Internacionalizacao.get("pesquisa.codigo")))      tipo = LivroDAO.FILTRO_CODIGO;
        else if (sel.equals(Internacionalizacao.get("pesquisa.titulo"))) tipo = LivroDAO.FILTRO_TITULO;
        else if (sel.equals(Internacionalizacao.get("pesquisa.isbn")))   tipo = LivroDAO.FILTRO_ISBN;
        else                                                              tipo = LivroDAO.FILTRO_TODOS;

        String valor = txtValor.getText().trim();
        if (LivroDAO.FILTRO_CODIGO.equals(tipo)) {
            try { Integer.parseInt(valor); }
            catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                        Internacionalizacao.get("msg.erro.numero").replace("{0}",
                                Internacionalizacao.get("campo.id")));
                return;
            }
        }
        carregarTabela(tipo, valor);
    }

    private void carregarTabela(String tipo, String valor) {
        try {
            modelo.setRowCount(0);
            List<Livro> lista = dao.buscarPorFiltro(tipo, valor);
            for (Livro l : lista) {
                modelo.addRow(new Object[]{
                        l.getId(), l.getTitulo(), l.getIsbn(), l.getAnoPublicacao(),
                        l.getEditora(), l.getAutor().getNome(), l.getCategoria().getNome(),
                        l.getQtdTotal(), l.getQtdDisponivel()
                });
            }
        } catch (SQLException ex) { erro(ex); }
    }

    private void selecionarParaEdicao() {
        int row = tabela.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, Internacionalizacao.get("msg.erro.selecionar")); return; }
        try {
            int id = (int) modelo.getValueAt(row, 0);
            List<Livro> lista = dao.buscarPorFiltro(LivroDAO.FILTRO_CODIGO, String.valueOf(id));
            if (lista.isEmpty()) return;
            Livro l = lista.get(0);
            txtId.setText(String.valueOf(l.getId()));
            txtTitulo.setText(l.getTitulo());
            txtIsbn.setText(l.getIsbn());
            txtAno.setText(String.valueOf(l.getAnoPublicacao()));
            txtEditora.setText(l.getEditora());
            txtQtdTotal.setText(String.valueOf(l.getQtdTotal()));
            txtQtdDisp.setText(String.valueOf(l.getQtdDisponivel()));
            selecionarItemComboPorId(cbAutor,     l.getAutor().getId());
            selecionarItemComboPorId(cbCategoria, l.getCategoria().getId());
            abas.setSelectedIndex(0);
        } catch (SQLException ex) { erro(ex); }
    }

    private void selecionarItemComboPorId(JComboBox<?> cb, int id) {
        for (int i = 0; i < cb.getItemCount(); i++) {
            Object o = cb.getItemAt(i);
            int oid = (o instanceof Autor) ? ((Autor) o).getId() : ((Categoria) o).getId();
            if (oid == id) { cb.setSelectedIndex(i); return; }
        }
    }

    private void limpar() {
        txtId.setText(""); txtTitulo.setText(""); txtIsbn.setText("");
        txtAno.setText(""); txtEditora.setText("");
        txtQtdTotal.setText(""); txtQtdDisp.setText("");
        if (cbAutor.getItemCount() > 0)     cbAutor.setSelectedIndex(0);
        if (cbCategoria.getItemCount() > 0) cbCategoria.setSelectedIndex(0);
        txtTitulo.requestFocus();
    }

    private Livro lerCampos() {
        if (txtTitulo.getText().trim().isEmpty()
                || txtIsbn.getText().trim().isEmpty()
                || txtAno.getText().trim().isEmpty()
                || txtEditora.getText().trim().isEmpty()
                || txtQtdTotal.getText().trim().isEmpty()
                || txtQtdDisp.getText().trim().isEmpty()
                || cbAutor.getSelectedItem() == null
                || cbCategoria.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, Internacionalizacao.get("msg.erro.campos"));
            return null;
        }
        try {
            Livro l = new Livro();
            l.setTitulo(txtTitulo.getText().trim());
            l.setIsbn(txtIsbn.getText().trim());
            l.setAnoPublicacao(Integer.parseInt(txtAno.getText().trim()));
            l.setEditora(txtEditora.getText().trim());
            l.setQtdTotal(Integer.parseInt(txtQtdTotal.getText().trim()));
            l.setQtdDisponivel(Integer.parseInt(txtQtdDisp.getText().trim()));
            l.setAutor((Autor) cbAutor.getSelectedItem());
            l.setCategoria((Categoria) cbCategoria.getSelectedItem());
            return l;
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    Internacionalizacao.get("msg.erro.numero").replace("{0}",
                            Internacionalizacao.get("campo.ano") + "/" +
                            Internacionalizacao.get("campo.qtdTotal") + "/" +
                            Internacionalizacao.get("campo.qtdDisponivel")));
            return null;
        }
    }

    private void salvar() {
        Livro l = lerCampos(); if (l == null) return;
        try {
            if (dao.existeIsbn(l.getIsbn(), 0)) {
                JOptionPane.showMessageDialog(this, Internacionalizacao.get("msg.erro.duplicado")); return;
            }
            dao.inserir(l);
            JOptionPane.showMessageDialog(this, Internacionalizacao.get("msg.sucesso.salvar"));
            limpar(); carregarTabela(LivroDAO.FILTRO_TODOS, "");
        } catch (SQLException ex) { erro(ex); }
    }

    private void alterar() {
        if (txtId.getText().isEmpty()) { JOptionPane.showMessageDialog(this, Internacionalizacao.get("msg.erro.selecionar")); return; }
        Livro l = lerCampos(); if (l == null) return;
        l.setId(Integer.parseInt(txtId.getText()));
        try {
            if (dao.existeIsbn(l.getIsbn(), l.getId())) {
                JOptionPane.showMessageDialog(this, Internacionalizacao.get("msg.erro.duplicado")); return;
            }
            dao.alterar(l);
            JOptionPane.showMessageDialog(this, Internacionalizacao.get("msg.sucesso.alterar"));
            limpar(); carregarTabela(LivroDAO.FILTRO_TODOS, "");
        } catch (SQLException ex) { erro(ex); }
    }

    private void excluir() {
        if (txtId.getText().isEmpty()) { JOptionPane.showMessageDialog(this, Internacionalizacao.get("msg.erro.selecionar")); return; }
        int op = JOptionPane.showConfirmDialog(this, Internacionalizacao.get("msg.confirma.excluir"), "?", JOptionPane.YES_NO_OPTION);
        if (op != JOptionPane.YES_OPTION) return;
        try {
            dao.excluir(Integer.parseInt(txtId.getText()));
            JOptionPane.showMessageDialog(this, Internacionalizacao.get("msg.sucesso.excluir"));
            limpar(); carregarTabela(LivroDAO.FILTRO_TODOS, "");
        } catch (SQLException ex) { erro(ex); }
    }

    private void apenasNumeros(JTextField campo) {
        campo.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c) && c != KeyEvent.VK_BACK_SPACE) e.consume();
            }
        });
    }

    private void erro(Exception ex) {
        JOptionPane.showMessageDialog(this,
                Internacionalizacao.get("msg.erro.geral") + " " + ex.getMessage(),
                "Erro", JOptionPane.ERROR_MESSAGE);
    }
}
