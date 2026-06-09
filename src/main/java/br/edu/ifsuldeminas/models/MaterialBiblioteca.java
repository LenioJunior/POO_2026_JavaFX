package br.edu.ifsuldeminas.models;

public abstract class MaterialBiblioteca {
    private int codigo;
    private String titulo;
    private boolean disponivel;
    private int prazoEmprestimo;
    
    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public boolean isDisponivel() {
        return disponivel;
    }

    public void setDisponivel(boolean disponivel) {
        this.disponivel = disponivel;
    }

    public void setPrazoEmprestimo(int prazoEmprestimo) {
        this.prazoEmprestimo = prazoEmprestimo;
    }

    public int getPrazoEmprestimo() {
        return prazoEmprestimo;
    }
}
