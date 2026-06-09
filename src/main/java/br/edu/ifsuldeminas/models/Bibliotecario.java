package br.edu.ifsuldeminas.models;

import jakarta.persistence.*;

@Entity
@DiscriminatorValue("BIBLIOTECARIO")
public class Bibliotecario extends Usuario {
}
