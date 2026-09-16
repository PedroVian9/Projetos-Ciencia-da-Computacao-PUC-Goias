package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

import model.Usuario;
import util.Internacionalizacao;

/**
 * Janela de Menu principal. Apresenta menus para os cadastros
 * (basico e intermediario), listagem de emprestimos e cadastro
 * de usuarios do sistema; usa metodos auxiliares para abrir telas.
 */
public class MenuPrincipal extends JFrame {

    private static final long serialVersionUID = 1L;
    private final Usuario usuarioLogado;

    public MenuPrincipal(Usuario u) {
        super(Internacionalizacao.get("app.titulo"));
        this.usuarioLogado = u;
        construirUI();
    }

    private void construirUI() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(900, 560);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        setJMenuBar(montarMenu());

        JLabel titulo = new JLabel(Internacionalizacao.get("app.titulo"), SwingConstants.CENTER);
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 22f));
        titulo.setForeground(new Color(0, 60, 130));

        JLabel sub = new JLabel(Internacionalizacao.get("app.subtitulo"), SwingConstants.CENTER);
        sub.setFont(sub.getFont().deriveFont(Font.ITALIC, 14f));

        JLabel bem = new JLabel(Internacionalizacao.get("menu.bemvindo") + " " + usuarioLogado.getNome(), SwingConstants.CENTER);
        bem.setFont(bem.getFont().deriveFont(Font.PLAIN, 14f));

        javax.swing.JPanel painel = new javax.swing.JPanel();
        painel.setLayout(new java.awt.GridLayout(3, 1, 5, 5));
        painel.setBorder(javax.swing.BorderFactory.createEmptyBorder(60, 20, 60, 20));
        painel.add(titulo);
        painel.add(sub);
        painel.add(bem);

        add(painel, BorderLayout.CENTER);
    }

    private JMenuBar montarMenu() {
        JMenuBar bar = new JMenuBar();

        JMenu mCad = new JMenu(Internacionalizacao.get("menu.cadastros"));
        mCad.add(criarItem(Internacionalizacao.get("menu.autor"),      e -> new CadAutor(this).setVisible(true)));
        mCad.add(criarItem(Internacionalizacao.get("menu.categoria"),  e -> new CadCategoria(this).setVisible(true)));
        mCad.add(criarItem(Internacionalizacao.get("menu.livro"),      e -> new CadLivro(this).setVisible(true)));
        mCad.add(criarItem(Internacionalizacao.get("menu.leitor"),     e -> new CadLeitor(this).setVisible(true)));
        mCad.add(criarItem(Internacionalizacao.get("menu.emprestimo"), e -> new CadEmprestimo(this).setVisible(true)));
        bar.add(mCad);

        JMenu mList = new JMenu(Internacionalizacao.get("menu.listagens"));
        mList.add(criarItem(Internacionalizacao.get("menu.listaEmprestimos"),
                e -> new ListaEmprestimos(this).setVisible(true)));
        bar.add(mList);

        JMenu mUsu = new JMenu(Internacionalizacao.get("menu.usuarios"));
        mUsu.add(criarItem(Internacionalizacao.get("menu.usuarios"),
                e -> new CadUsuario(this, usuarioLogado).setVisible(true)));
        bar.add(mUsu);

        JMenu mSair = new JMenu(Internacionalizacao.get("menu.sair"));
        mSair.add(criarItem(Internacionalizacao.get("menu.sair"), e -> System.exit(0)));
        bar.add(mSair);

        return bar;
    }

    private JMenuItem criarItem(String texto, java.awt.event.ActionListener al) {
        JMenuItem it = new JMenuItem(texto);
        it.addActionListener(al);
        return it;
    }
}
