package model;

/**
 * Entidade Usuario do sistema. Mantem credenciais de acesso
 * (login e senha) e a preferencia de idioma do usuario.
 */
public class Usuario {

    private int id;
    private String nome;
    private String login;
    private String senha;
    private String email;
    private String idioma;

    public Usuario() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getIdioma() { return idioma; }
    public void setIdioma(String idioma) { this.idioma = idioma; }

    @Override
    public String toString() {
        return nome + " (" + login + ")";
    }
}
