package model;

import java.sql.Date;

/**
 * Entidade Emprestimo. Representa a operacao de saida de um livro
 * para um leitor, com datas de emprestimo, prevista e efetiva de
 * devolucao, alem do status (ABERTO ou DEVOLVIDO).
 */
public class Emprestimo {

    public static final String STATUS_ABERTO    = "ABERTO";
    public static final String STATUS_DEVOLVIDO = "DEVOLVIDO";

    private int id;
    private Date dataEmprestimo;
    private Date dataPrevDevolucao;
    private Date dataDevolucao;
    private String status;
    private Leitor leitor;
    private Livro livro;

    public Emprestimo() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Date getDataEmprestimo() { return dataEmprestimo; }
    public void setDataEmprestimo(Date dataEmprestimo) { this.dataEmprestimo = dataEmprestimo; }

    public Date getDataPrevDevolucao() { return dataPrevDevolucao; }
    public void setDataPrevDevolucao(Date dataPrevDevolucao) { this.dataPrevDevolucao = dataPrevDevolucao; }

    public Date getDataDevolucao() { return dataDevolucao; }
    public void setDataDevolucao(Date dataDevolucao) { this.dataDevolucao = dataDevolucao; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Leitor getLeitor() { return leitor; }
    public void setLeitor(Leitor leitor) { this.leitor = leitor; }

    public Livro getLivro() { return livro; }
    public void setLivro(Livro livro) { this.livro = livro; }
}
