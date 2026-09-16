package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Classe utilitaria responsavel por abrir e fechar a conexao JDBC
 * com o banco de dados MySQL utilizado pelo SisBiblio.
 */
public class Conexao {

    private static final String URL     = "jdbc:mysql://localhost:3306/sisbiblio?useSSL=false&serverTimezone=UTC&useUnicode=true&characterEncoding=UTF-8";
    private static final String USUARIO = "root";
    private static final String SENHA   = "root";

    public static Connection getConexao() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver JDBC do MySQL nao encontrado no classpath.", e);
        }
        return DriverManager.getConnection(URL, USUARIO, SENHA);
    }

    public static void fecharConexao(Connection con) {
        if (con != null) {
            try { con.close(); } catch (SQLException ignored) {}
        }
    }
}
