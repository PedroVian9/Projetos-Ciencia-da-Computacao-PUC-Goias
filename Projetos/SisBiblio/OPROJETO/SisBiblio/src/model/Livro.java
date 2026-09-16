package model;

/**
 * Entidade Livro. Representa um titulo do acervo, com vinculo a um
 * autor e a uma categoria, alem de controle de quantidade total
 * e disponivel para emprestimo. Cadastro intermediario do SisBiblio.
 */
public class Livro {

    private int id;
    private String titulo;
    private String isbn;
    private int anoPublicacao;
    private String editora;
    private int qtdTotal;
    private int qtdDisponivel;
    private Autor autor;
    private Categoria categoria;

    public Livro() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public int getAnoPublicacao() { return anoPublicacao; }
    public void setAnoPublicacao(int anoPublicacao) { this.anoPublicacao = anoPublicacao; }

    public String getEditora() { return editora; }
    public void setEditora(String editora) { this.editora = editora; }

    public int getQtdTotal() { return qtdTotal; }
    public void setQtdTotal(int qtdTotal) { this.qtdTotal = qtdTotal; }

    public int getQtdDisponivel() { return qtdDisponivel; }
    public void setQtdDisponivel(int qtdDisponivel) { this.qtdDisponivel = qtdDisponivel; }

    public Autor getAutor() { return autor; }
    public void setAutor(Autor autor) { this.autor = autor; }

    public Categoria getCategoria() { return categoria; }
    public void setCategoria(Categoria categoria) { this.categoria = categoria; }

    @Override
    public String toString() {
        return titulo;
    }
}
