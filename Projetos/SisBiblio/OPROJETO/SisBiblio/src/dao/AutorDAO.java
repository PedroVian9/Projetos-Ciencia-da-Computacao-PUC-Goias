package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import model.Autor;
import util.Conexao;

/**
 * DAO da entidade Autor. Encapsula as operacoes JDBC de CRUD
 * e pesquisa parcial por nome na tabela autor.
 */
public class AutorDAO {

    public void inserir(Autor a) throws SQLException {
        String sql = "INSERT INTO autor(nome, nacionalidade) VALUES (?, ?)";
        Connection con = Conexao.getConexao();
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, a.getNome());
            ps.setString(2, a.getNacionalidade());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) a.setId(rs.getInt(1));
            }
        } finally {
            Conexao.fecharConexao(con);
        }
    }

    public void alterar(Autor a) throws SQLException {
        String sql = "UPDATE autor SET nome = ?, nacionalidade = ? WHERE id = ?";
        Connection con = Conexao.getConexao();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, a.getNome());
            ps.setString(2, a.getNacionalidade());
            ps.setInt(3, a.getId());
            ps.executeUpdate();
        } finally {
            Conexao.fecharConexao(con);
        }
    }

    public void excluir(int id) throws SQLException {
        String sql = "DELETE FROM autor WHERE id = ?";
        Connection con = Conexao.getConexao();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } finally {
            Conexao.fecharConexao(con);
        }
    }

    public boolean existeNome(String nome, int idIgnorar) throws SQLException {
        String sql = "SELECT id FROM autor WHERE nome = ? AND id <> ?";
        Connection con = Conexao.getConexao();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nome);
            ps.setInt(2, idIgnorar);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } finally {
            Conexao.fecharConexao(con);
        }
    }

    public List<Autor> buscarPorNome(String filtro) throws SQLException {
        String sql = "SELECT id, nome, nacionalidade FROM autor WHERE nome LIKE ? ORDER BY nome";
        List<Autor> lista = new ArrayList<>();
        Connection con = Conexao.getConexao();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, "%" + (filtro == null ? "" : filtro) + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new Autor(rs.getInt("id"), rs.getString("nome"), rs.getString("nacionalidade")));
                }
            }
        } finally {
            Conexao.fecharConexao(con);
        }
        return lista;
    }

    public List<Autor> listarTodos() throws SQLException {
        return buscarPorNome("");
    }
}
