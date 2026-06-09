package br.edu.ifsuldeminas.models;

import jakarta.persistence.*;

@Entity
@DiscriminatorValue("ALUNO")
public class Aluno extends Usuario {
}
