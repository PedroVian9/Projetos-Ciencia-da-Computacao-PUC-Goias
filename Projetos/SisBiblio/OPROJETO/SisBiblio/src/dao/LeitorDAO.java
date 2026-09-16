package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import model.Leitor;
import util.Conexao;

/**
 * DAO da entidade Leitor. Operacoes JDBC de CRUD e pesquisa
 * parcial por nome ou matricula na tabela leitor.
 */
public class LeitorDAO {

    public void inserir(Leitor l) throws SQLException {
        String sql = "INSERT INTO leitor(nome, cpf, email, telefone, matricula, curso) VALUES (?, ?, ?, ?, ?, ?)";
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

    public void alterar(Leitor l) throws SQLException {
        String sql = "UPDATE leitor SET nome=?, cpf=?, email=?, telefone=?, matricula=?, curso=? WHERE id=?";
        Connection con = Conexao.getConexao();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            preencher(ps, l);
            ps.setInt(7, l.getId());
            ps.executeUpdate();
        } finally {
            Conexao.fecharConexao(con);
        }
    }

    public void excluir(int id) throws SQLException {
        String sql = "DELETE FROM leitor WHERE id = ?";
        Connection con = Conexao.getConexao();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } finally {
            Conexao.fecharConexao(con);
        }
    }

    public boolean existeCpfOuMatricula(String cpf, String matricula, int idIgnorar) throws SQLException {
        String sql = "SELECT id FROM leitor WHERE (cpf = ? OR matricula = ?) AND id <> ?";
        Connection con = Conexao.getConexao();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, cpf);
            ps.setString(2, matricula);
            ps.setInt(3, idIgnorar);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } finally {
            Conexao.fecharConexao(con);
        }
    }

    public List<Leitor> buscarPorTexto(String filtro) throws SQLException {
        String sql = "SELECT id, nome, cpf, email, telefone, matricula, curso "
                   + "FROM leitor WHERE nome LIKE ? OR matricula LIKE ? ORDER BY nome";
        List<Leitor> lista = new ArrayList<>();
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

    public List<Leitor> listarTodos() throws SQLException {
        return buscarPorTexto("");
    }

    private void preencher(PreparedStatement ps, Leitor l) throws SQLException {
        ps.setString(1, l.getNome());
        ps.setString(2, l.getCpf());
        ps.setString(3, l.getEmail());
        ps.setString(4, l.getTelefone());
        ps.setString(5, l.getMatricula());
        ps.setString(6, l.getCurso());
    }

    private Leitor montar(ResultSet rs) throws SQLException {
        Leitor l = new Leitor();
        l.setId(rs.getInt("id"));
        l.setNome(rs.getString("nome"));
        l.setCpf(rs.getString("cpf"));
        l.setEmail(rs.getString("email"));
        l.setTelefone(rs.getString("telefone"));
        l.setMatricula(rs.getString("matricula"));
        l.setCurso(rs.getString("curso"));
        return l;
    }
}
