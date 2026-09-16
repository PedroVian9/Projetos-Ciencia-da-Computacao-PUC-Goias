package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import model.Autor;
import model.Categoria;
import model.Livro;
import util.Conexao;

/**
 * DAO da entidade Livro. Operacoes JDBC de CRUD e pesquisa
 * complexa por codigo, titulo ou ISBN, com JOIN para autor
 * e categoria (cadastro intermediario).
 */
public class LivroDAO {

    public static final String FILTRO_CODIGO = "codigo";
    public static final String FILTRO_TITULO = "titulo";
    public static final String FILTRO_ISBN   = "isbn";
    public static final String FILTRO_TODOS  = "todos";

    public void inserir(Livro l) throws SQLException {
        String sql = "INSERT INTO livro(titulo, isbn, ano_publicacao, editora, qtd_total, qtd_disponivel, id_autor, id_categoria) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        Connection con = Conexao.getConexao();
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preencher(ps, l);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) l.setId(rs.getInt(1));
            }
        } finally {
            Conexao.fecharConexao(con);
        }
    }

    public void alterar(Livro l) throws SQLException {
        String sql = "UPDATE livro SET titulo=?, isbn=?, ano_publicacao=?, editora=?, qtd_total=?, qtd_disponivel=?, id_autor=?, id_categoria=? WHERE id=?";
        Connection con = Conexao.getConexao();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            preencher(ps, l);
            ps.setInt(9, l.getId());
            ps.executeUpdate();
        } finally {
            Conexao.fecharConexao(con);
        }
    }

    public void excluir(int id) throws SQLException {
        String sql = "DELETE FROM livro WHERE id = ?";
        Connection con = Conexao.getConexao();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } finally {
            Conexao.fecharConexao(con);
        }
    }

    public boolean existeIsbn(String isbn, int idIgnorar) throws SQLException {
        String sql = "SELECT id FROM livro WHERE isbn = ? AND id <> ?";
        Connection con = Conexao.getConexao();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, isbn);
            ps.setInt(2, idIgnorar);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } finally {
            Conexao.fecharConexao(con);
        }
    }

    /**
     * Pesquisa complexa: o tipo determina a coluna a comparar.
     * codigo -> id (igualdade); titulo/isbn -> LIKE; todos -> sem filtro.
     */
    public List<Livro> buscarPorFiltro(String tipo, String valor) throws SQLException {
        StringBuilder sql = new StringBuilder(
            "SELECT l.id, l.titulo, l.isbn, l.ano_publicacao, l.editora, l.qtd_total, l.qtd_disponivel, "
          + "a.id AS aid, a.nome AS anome, a.nacionalidade AS anac, "
          + "c.id AS cid, c.nome AS cnome, c.descricao AS cdesc "
          + "FROM livro l "
          + "JOIN autor a     ON a.id = l.id_autor "
          + "JOIN categoria c ON c.id = l.id_categoria ");

        if (FILTRO_CODIGO.equals(tipo))      sql.append("WHERE l.id = ? ");
        else if (FILTRO_TITULO.equals(tipo)) sql.append("WHERE l.titulo LIKE ? ");
        else if (FILTRO_ISBN.equals(tipo))   sql.append("WHERE l.isbn   LIKE ? ");
        sql.append("ORDER BY l.titulo");

        List<Livro> lista = new ArrayList<>();
        Connection con = Conexao.getConexao();
        try (PreparedStatement ps = con.prepareStatement(sql.toString())) {
            if (FILTRO_CODIGO.equals(tipo)) {
                ps.setInt(1, Integer.parseInt(valor));
            } else if (FILTRO_TITULO.equals(tipo) || FILTRO_ISBN.equals(tipo)) {
                ps.setString(1, "%" + (valor == null ? "" : valor) + "%");
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(montar(rs));
            }
        } finally {
            Conexao.fecharConexao(con);
        }
        return lista;
    }

    public List<Livro> listarTodos() throws SQLException {
        return buscarPorFiltro(FILTRO_TODOS, "");
    }

    public void decrementarDisponivel(int idLivro) throws SQLException {
        ajustarDisponivel(idLivro, -1);
    }

    public void incrementarDisponivel(int idLivro) throws SQLException {
        ajustarDisponivel(idLivro, +1);
    }

    private void ajustarDisponivel(int idLivro, int delta) throws SQLException {
        String sql = "UPDATE livro SET qtd_disponivel = qtd_disponivel + ? WHERE id = ?";
        Connection con = Conexao.getConexao();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, delta);
            ps.setInt(2, idLivro);
            ps.executeUpdate();
        } finally {
            Conexao.fecharConexao(con);
        }
    }

    private void preencher(PreparedStatement ps, Livro l) throws SQLException {
        ps.setString(1, l.getTitulo());
        ps.setString(2, l.getIsbn());
        ps.setInt   (3, l.getAnoPublicacao());
        ps.setString(4, l.getEditora());
        ps.setInt   (5, l.getQtdTotal());
        ps.setInt   (6, l.getQtdDisponivel());
        ps.setInt   (7, l.getAutor().getId());
        ps.setInt   (8, l.getCategoria().getId());
    }

    private Livro montar(ResultSet rs) throws SQLException {
        Livro l = new Livro();
        l.setId(rs.getInt("id"));
        l.setTitulo(rs.getString("titulo"));
        l.setIsbn(rs.getString("isbn"));
        l.setAnoPublicacao(rs.getInt("ano_publicacao"));
        l.setEditora(rs.getString("editora"));
        l.setQtdTotal(rs.getInt("qtd_total"));
        l.setQtdDisponivel(rs.getInt("qtd_disponivel"));
        l.setAutor(new Autor(rs.getInt("aid"), rs.getString("anome"), rs.getString("anac")));
        l.setCategoria(new Categoria(rs.getInt("cid"), rs.getString("cnome"), rs.getString("cdesc")));
        return l;
    }
}
