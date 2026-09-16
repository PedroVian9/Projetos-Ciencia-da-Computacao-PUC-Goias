package model;

/**
 * Entidade Leitor. Representa o usuario da biblioteca (aluno
 * ou professor) com dados pessoais e de vinculo institucional.
 */
public class Leitor {

    private int id;
    private String nome;
    private String cpf;
    private String email;
    private String telefone;
    private String matricula;
    private String curso;

    public Leitor() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public String getMatricula() { return matricula; }
    public void setMatricula(String matricula) { this.matricula = matricula; }

    public String getCurso() { return curso; }
    public void setCurso(String curso) { this.curso = curso; }

    @Override
    public String toString() {
        return nome + " (" + matricula + ")";
    }
}
