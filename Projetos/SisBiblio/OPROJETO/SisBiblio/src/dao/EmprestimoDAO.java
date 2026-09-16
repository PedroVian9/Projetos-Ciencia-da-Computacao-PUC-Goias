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
import model.Emprestimo;
import model.Leitor;
import model.Livro;
import util.Conexao;

/**
 * DAO da entidade Emprestimo. Operacoes JDBC de CRUD da operacao
 * de emprestimo, com JOIN para leitor e livro. Suporta ordenacao
 * por data do emprestimo ou por nome do leitor (listagem).
 */
public class EmprestimoDAO {

    public static final String ORD_DATA   = "data";
    public static final String ORD_LEITOR = "leitor";

    public void inserir(Emprestimo e) throws SQLException {
        String sql = "INSERT INTO emprestimo(data_emprestimo, data_prev_devolucao, data_devolucao, status, id_leitor, id_livro) "
                   + "VALUES (?, ?, ?, ?, ?, ?)";
        Connection con = Conexao.getConexao();
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preencher(ps, e);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) e.setId(rs.getInt(1));
            }
        } finally {
            Conexao.fecharConexao(con);
        }
    }

    public void alterar(Emprestimo e) throws SQLException {
        String sql = "UPDATE emprestimo SET data_emprestimo=?, data_prev_devolucao=?, data_devolucao=?, status=?, id_leitor=?, id_livro=? WHERE id=?";
        Connection con = Conexao.getConexao();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            preencher(ps, e);
            ps.setInt(7, e.getId());
            ps.executeUpdate();
        } finally {
            Conexao.fecharConexao(con);
        }
    }

    public void excluir(int id) throws SQLException {
        String sql = "DELETE FROM emprestimo WHERE id = ?";
        Connection con = Conexao.getConexao();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } finally {
            Conexao.fecharConexao(con);
        }
    }

    public List<Emprestimo> listarOrdenado(String ordem) throws SQLException {
        String orderBy = ORD_LEITOR.equalsIgnoreCase(ordem)
                ? "le.nome ASC, e.data_emprestimo DESC"
                : "e.data_emprestimo DESC, e.id DESC";

        String sql = "SELECT e.id, e.data_emprestimo, e.data_prev_devolucao, e.data_devolucao, e.status, "
                   + " le.id AS lid, le.nome AS lnome, le.cpf AS lcpf, le.email AS lemail, le.telefone AS ltel, le.matricula AS lmat, le.curso AS lcur, "
                   + " li.id AS bid, li.titulo AS btit, li.isbn AS bisbn, li.ano_publicacao AS bano, li.editora AS bedit, li.qtd_total AS bqt, li.qtd_disponivel AS bqd, "
                   + " a.id AS aid, a.nome AS anome, a.nacionalidade AS anac, "
                   + " c.id AS cid, c.nome AS cnome, c.descricao AS cdesc "
                   + "FROM emprestimo e "
                   + "JOIN leitor le    ON le.id = e.id_leitor "
                   + "JOIN livro  li    ON li.id = e.id_livro "
                   + "JOIN autor a      ON a.id  = li.id_autor "
                   + "JOIN categoria c  ON c.id  = li.id_categoria "
                   + "ORDER BY " + orderBy;

        List<Emprestimo> lista = new ArrayList<>();
        Connection con = Conexao.getConexao();
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(montar(rs));
        } finally {
            Conexao.fecharConexao(con);
        }
        return lista;
    }

    private void preencher(PreparedStatement ps, Emprestimo e) throws SQLException {
        ps.setDate  (1, e.getDataEmprestimo());
        ps.setDate  (2, e.getDataPrevDevolucao());
        ps.setDate  (3, e.getDataDevolucao());
        ps.setString(4, e.getStatus());
        ps.setInt   (5, e.getLeitor().getId());
        ps.setInt   (6, e.getLivro().getId());
    }

    private Emprestimo montar(ResultSet rs) throws SQLException {
        Emprestimo e = new Emprestimo();
        e.setId(rs.getInt("id"));
        e.setDataEmprestimo(rs.getDate("data_emprestimo"));
        e.setDataPrevDevolucao(rs.getDate("data_prev_devolucao"));
        e.setDataDevolucao(rs.getDate("data_devolucao"));
        e.setStatus(rs.getString("status"));

        Leitor le = new Leitor();
        le.setId(rs.getInt("lid"));
        le.setNome(rs.getString("lnome"));
        le.setCpf(rs.getString("lcpf"));
        le.setEmail(rs.getString("lemail"));
        le.setTelefone(rs.getString("ltel"));
        le.setMatricula(rs.getString("lmat"));
        le.setCurso(rs.getString("lcur"));
        e.setLeitor(le);

        Livro li = new Livro();
        li.setId(rs.getInt("bid"));
        li.setTitulo(rs.getString("btit"));
        li.setIsbn(rs.getString("bisbn"));
        li.setAnoPublicacao(rs.getInt("bano"));
        li.setEditora(rs.getString("bedit"));
        li.setQtdTotal(rs.getInt("bqt"));
        li.setQtdDisponivel(rs.getInt("bqd"));
        li.setAutor(new Autor(rs.getInt("aid"), rs.getString("anome"), rs.getString("anac")));
        li.setCategoria(new Categoria(rs.getInt("cid"), rs.getString("cnome"), rs.getString("cdesc")));
        e.setLivro(li);

        return e;
    }
}
