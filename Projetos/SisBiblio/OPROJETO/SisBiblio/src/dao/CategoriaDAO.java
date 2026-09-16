package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import model.Categoria;
import util.Conexao;

/**
 * DAO da entidade Categoria. Operacoes JDBC de CRUD e pesquisa
 * parcial por nome na tabela categoria.
 */
public class CategoriaDAO {

    public void inserir(Categoria c) throws SQLException {
        String sql = "INSERT INTO categoria(nome, descricao) VALUES (?, ?)";
        Connection con = Conexao.getConexao();
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, c.getNome());
            ps.setString(2, c.getDescricao());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) c.setId(rs.getInt(1));
            }
        } finally {
            Conexao.fecharConexao(con);
        }
    }

    public void alterar(Categoria c) throws SQLException {
        String sql = "UPDATE categoria SET nome = ?, descricao = ? WHERE id = ?";
        Connection con = Conexao.getConexao();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, c.getNome());
            ps.setString(2, c.getDescricao());
            ps.setInt(3, c.getId());
            ps.executeUpdate();
        } finally {
            Conexao.fecharConexao(con);
        }
    }

    public void excluir(int id) throws SQLException {
        String sql = "DELETE FROM categoria WHERE id = ?";
        Connection con = Conexao.getConexao();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } finally {
            Conexao.fecharConexao(con);
        }
    }

    public boolean existeNome(String nome, int idIgnorar) throws SQLException {
        String sql = "SELECT id FROM categoria WHERE nome = ? AND id <> ?";
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

    public List<Categoria> buscarPorNome(String filtro) throws SQLException {
        String sql = "SELECT id, nome, descricao FROM categoria WHERE nome LIKE ? ORDER BY nome";
        List<Categoria> lista = new ArrayList<>();
        Connection con = Conexao.getConexao();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, "%" + (filtro == null ? "" : filtro) + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new Categoria(rs.getInt("id"), rs.getString("nome"), rs.getString("descricao")));
                }
            }
        } finally {
            Conexao.fecharConexao(con);
        }
        return lista;
    }

    public List<Categoria> listarTodos() throws SQLException {
        return buscarPorNome("");
    }
}
