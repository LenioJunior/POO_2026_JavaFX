package br.edu.ifsuldeminas.models;

import jakarta.persistence.*;

@Entity
@Table(name = "usuarios")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo", discriminatorType = DiscriminatorType.STRING)
public abstract class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private int limiteEmprestimo;
    private String nome;
    private String email;

    public int consultarLimite() { return limiteEmprestimo; }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public int getLimiteEmprestimo() { return limiteEmprestimo; }
    public void setLimiteEmprestimo(int limiteEmprestimo) { this.limiteEmprestimo = limiteEmprestimo; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
