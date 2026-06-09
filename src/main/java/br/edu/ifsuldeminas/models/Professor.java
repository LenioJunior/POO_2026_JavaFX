package br.edu.ifsuldeminas.models;

import jakarta.persistence.*;

@Entity
@DiscriminatorValue("PROFESSOR")
public class Professor extends Usuario {
}
