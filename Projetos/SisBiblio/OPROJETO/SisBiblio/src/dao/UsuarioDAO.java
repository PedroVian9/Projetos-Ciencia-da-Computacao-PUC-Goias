package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import model.Usuario;
import util.Conexao;

/**
 * DAO da entidade Usuario. Operacoes de CRUD, autenticacao
 * (login/senha) e atualizacao da preferencia de idioma.
 */
public class UsuarioDAO {

    public Usuario autenticar(String login, String senha) throws SQLException {
        String sql = "SELECT id, nome, login, senha, email, idioma FROM usuario WHERE login = ? AND senha = ?";
        Connection con = Conexao.getConexao();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, login);
            ps.setString(2, senha);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return montar(rs);
                return null;
            }
        } finally {
            Conexao.fecharConexao(con);
        }
    }

    public void inserir(Usuario u) throws SQLException {
        String sql = "INSERT INTO usuario(nome, login, senha, email, idioma) VALUES (?, ?, ?, ?, ?)";
        Connection con = Conexao.getConexao();
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preencher(ps, u);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) u.setId(rs.getInt(1));
            }
        } finally {
            Conexao.fecharConexao(con);
        }
    }

    public void alterar(Usuario u) throws SQLException {
        String sql = "UPDATE usuario SET nome=?, login=?, senha=?, email=?, idioma=? WHERE id=?";
        Connection con = Conexao.getConexao();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            preencher(ps, u);
            ps.setInt(6, u.getId());
            ps.executeUpdate();
        } finally {
            Conexao.fecharConexao(con);
        }
    }

    public void excluir(int id) throws SQLException {
        String sql = "DELETE FROM usuario WHERE id = ?";
        Connection con = Conexao.getConexao();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } finally {
            Conexao.fecharConexao(con);
        }
    }

    public boolean existeLogin(String login, int idIgnorar) throws SQLException {
        String sql = "SELECT id FROM usuario WHERE login = ? AND id <> ?";
        Connection con = Conexao.getConexao();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, login);
            ps.setInt(2, idIgnorar);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } finally {
            Conexao.fecharConexao(con);
        }
    }

    public List<Usuario> buscarPorTexto(String filtro) throws SQLException {
        String sql = "SELECT id, nome, login, senha, email, idioma FROM usuario "
                   + "WHERE nome LIKE ? OR login LIKE ? ORDER BY nome";
        List<Usuario> lista = new ArrayList<>();
        Connection con = Conexao.getConexao();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            String like = "%" + (filtro == null ? "" : filtro) + "%";
            ps.setString(1, like);
            ps.setString(2, like);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(montar(rs));
            }
        } finally {
            Conexao.fecharConexao(con);
        }
        return lista;
    }

    public List<Usuario> listarTodos() throws SQLException {
        return buscarPorTexto("");
    }

    private void preencher(PreparedStatement ps, Usuario u) throws SQLException {
        ps.setString(1, u.getNome());
        ps.setString(2, u.getLogin());
        ps.setString(3, u.getSenha());
        ps.setString(4, u.getEmail());
        ps.setString(5, u.getIdioma() == null ? "pt_BR" : u.getIdioma());
    }

    private Usuario montar(ResultSet rs) throws SQLException {
        Usuario u = new Usuario();
        u.setId(rs.getInt("id"));
        u.setNome(rs.getString("nome"));
        u.setLogin(rs.getString("login"));
        u.setSenha(rs.getString("senha"));
        u.setEmail(rs.getString("email"));
        u.setIdioma(rs.getString("idioma"));
        return u;
    }
}
