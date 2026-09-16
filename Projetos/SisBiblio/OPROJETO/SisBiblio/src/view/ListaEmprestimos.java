package view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.sql.SQLException;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import dao.EmprestimoDAO;
import model.Emprestimo;
import util.Internacionalizacao;

/**
 * Janela de listagem de emprestimos. Permite ordenacao por
 * data do emprestimo ou por nome do leitor (combobox). Cumpre
 * o requisito de janela de listagem com pelo menos duas opcoes
 * de ordenacao.
 */
public class ListaEmprestimos extends JDialog {

    private static final long serialVersionUID = 1L;
    private final EmprestimoDAO dao = new EmprestimoDAO();

    private JComboBox<String> cbOrdem;
    private DefaultTableModel modelo;

    public ListaEmprestimos(Frame owner) {
        super(owner, Internacionalizacao.get("lista.titulo"), true);
        construirUI();
        recarregar();
    }

    private void construirUI() {
        setSize(900, 520);
        setLocationRelativeTo(getOwner());
        setLayout(new BorderLayout());

        JPanel topo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topo.add(new JLabel(Internacionalizacao.get("lista.ordenarPor") + ":"));
        cbOrdem = new JComboBox<>(new String[]{
                Internacionalizacao.get("lista.ordem.data"),
                Internacionalizacao.get("lista.ordem.leitor")
        });
        topo.add(cbOrdem);
        JButton btnAtualizar = new JButton(Internacionalizacao.get("btn.pesquisar"));
        topo.add(btnAtualizar);
        add(topo, BorderLayout.NORTH);

        modelo = new DefaultTableModel(new Object[]{
                Internacionalizacao.get("campo.id"),
                Internacionalizacao.get("campo.dataEmp"),
                Internacionalizacao.get("campo.dataPrev"),
                Internacionalizacao.get("campo.dataDev"),
                Internacionalizacao.get("campo.status"),
                Internacionalizacao.get("campo.leitor"),
                Internacionalizacao.get("campo.livro"),
                Internacionalizacao.get("campo.matricula"),
                Internacionalizacao.get("campo.isbn")
        }, 0) {
            private static final long serialVersionUID = 1L;
            public boolean isCellEditable(int r, int c) { return false; }
        };
        add(new JScrollPane(new JTable(modelo)), BorderLayout.CENTER);

        JPanel sul = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnFechar = new JButton(Internacionalizacao.get("btn.fechar"));
        sul.add(btnFechar);
        add(sul, BorderLayout.SOUTH);

        btnAtualizar.addActionListener(e -> recarregar());
        cbOrdem.addActionListener(e -> recarregar());
        btnFechar.addActionListener(e -> dispose());
    }

    private void recarregar() {
        try {
            String ordem = cbOrdem.getSelectedIndex() == 1
                    ? EmprestimoDAO.ORD_LEITOR : EmprestimoDAO.ORD_DATA;
            List<Emprestimo> lista = dao.listarOrdenado(ordem);
            modelo.setRowCount(0);
            for (Emprestimo e : lista) {
                modelo.addRow(new Object[]{
                        e.getId(),
                        e.getDataEmprestimo(),
                        e.getDataPrevDevolucao(),
                        e.getDataDevolucao(),
                        e.getStatus(),
                        e.getLeitor().getNome(),
                        e.getLivro().getTitulo(),
                        e.getLeitor().getMatricula(),
                        e.getLivro().getIsbn()
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    Internacionalizacao.get("msg.erro.geral") + " " + ex.getMessage());
        }
    }
}
