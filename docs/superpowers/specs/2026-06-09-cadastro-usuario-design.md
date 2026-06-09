# Cadastro de Usuário — Design Spec
Date: 2026-06-09

## Summary

A full-screen JavaFX registration form for creating new library users (Aluno, Professor, Bibliotecário). Navigated to from the primary screen via a button. Saves to SQL Server via Spring Data JPA.

---

## Architecture

### Navigation flow

```
Primary screen  →  [Cadastrar Usuário button]  →  Cadastro screen
Cadastro screen →  [Voltar button]             →  Primary screen
Cadastro screen →  [Salvar — success]          →  form clears, success message shown
```

### Spring + JavaFX integration

`App.java` is made `@SpringBootApplication`. It stores the `ApplicationContext` statically after boot. `loadFXML()` sets `fxmlLoader.setControllerFactory(context::getBean)` so JavaFX asks Spring to instantiate controllers — enabling `@Autowired` injection inside them.

### Files changed / created

| File | Action |
|---|---|
| `App.java` | Add `@SpringBootApplication`, store `ApplicationContext`, wire controller factory |
| `pom.xml` | Fix `<mainClass>` to `br.edu.ifsuldeminas.App` |
| `module-info.java` | Open `br.edu.ifsuldeminas` and `br.edu.ifsuldeminas.models` to Spring reflection modules |
| `Usuario.java` | Add JPA annotations |
| `Aluno.java` | Add `@Entity`, `@DiscriminatorValue("ALUNO")` |
| `Professor.java` | Add `@Entity`, `@DiscriminatorValue("PROFESSOR")` |
| `Bibliotecario.java` | Add `@Entity`, `@DiscriminatorValue("BIBLIOTECARIO")` |
| `UsuarioRepository.java` (new) | `JpaRepository<Usuario, Integer>` |
| `CadastroUsuarioController.java` (new) | Spring `@Component`, handles form |
| `cadastro-usuario.fxml` (new) | Registration form FXML |
| `primary.fxml` | Add "Cadastrar Usuário" button |
| `PrimaryController.java` | Add `switchToCadastro()` |

---

## Data Layer

### JPA inheritance strategy: Single Table

One `usuarios` table with a `tipo` discriminator column. No joins required for reads.

```
TABLE: usuarios
id (PK, auto) | tipo (ALUNO / PROFESSOR / BIBLIOTECARIO) | nome | email | limite_emprestimo
```

### Entity annotations

**`Usuario.java`:**
```java
@Entity
@Table(name = "usuarios")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo")
public abstract class Usuario {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    // nome, email, limiteEmprestimo unchanged
}
```

**Subclasses** (`Aluno`, `Professor`, `Bibliotecario`): add `@Entity` and `@DiscriminatorValue("ALUNO")` etc.

### Repository

```java
package br.edu.ifsuldeminas.repositories;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {}
```

One repository handles saving any subtype.

---

## Screen Design

### Layout (approved: option B — tab toggle)

```
┌─────────────────────────────────────────────────┐
│ ← Cadastrar Usuário                             │
├─────────────────────────────────────────────────┤
│  Tipo de Usuário                                │
│  [ Aluno ▓▓▓ ] [ Professor ] [ Bibliotecário ]  │
│                                                 │
│  ┌─────────────────────────────────────────┐   │
│  │  Nome completo *                        │   │
│  │  [________________________________]     │   │
│  │  E-mail *                               │   │
│  │  [________________________________]     │   │
│  │  Limite de Empréstimo *                 │   │
│  │  [______]  número de itens              │   │
│  └─────────────────────────────────────────┘   │
│                                                 │
│  ⚠ Mensagem de erro inline (vermelho)           │
│                                                 │
│  [    Salvar    ]  [ Voltar ]                   │
└─────────────────────────────────────────────────┘
```

### Controller behaviour (`CadastroUsuarioController`)

- Three tab buttons (`btnAluno`, `btnProfessor`, `btnBibliotecario`). Clicking one highlights it and sets `tipoSelecionado`.
- Default selection on load: `Aluno`.
- **Salvar** flow:
  1. Validate: `nome` non-blank, `email` non-blank and contains `@`, `limiteEmprestimo` non-negative integer.
  2. On validation failure: show red `lblErro` with message, do not save.
  3. On success: instantiate the correct subtype, set fields, call `usuarioRepository.save(entity)`, clear form, show green success message in `lblErro`.
- **Voltar**: calls `App.setRoot("primary")`.

### FXML file

Location: `src/main/resources/br/edu/ifsuldeminas/cadastro-usuario.fxml`  
Controller: `fx:controller="br.edu.ifsuldeminas.CadastroUsuarioController"`

---

## Validation Rules

| Field | Rule | Error message |
|---|---|---|
| Nome | Non-blank | "O campo Nome é obrigatório." |
| Email | Non-blank + contains `@` | "Informe um e-mail válido." |
| Limite de Empréstimo | Integer ≥ 0 | "Limite deve ser um número inteiro não negativo." |

---

## Module Configuration

`module-info.java` needs to open the model and controller packages to Spring so reflection-based injection and JPA work:

```java
module br.edu.ifsuldeminas {
    requires javafx.controls;
    requires javafx.fxml;
    requires spring.context;
    requires spring.beans;
    requires spring.boot;
    requires spring.boot.autoconfigure;
    requires spring.data.jpa;
    requires jakarta.persistence;

    opens br.edu.ifsuldeminas to javafx.fxml, spring.core, spring.beans, spring.context;
    opens br.edu.ifsuldeminas.models to javafx.fxml, spring.core, jakarta.persistence;
    exports br.edu.ifsuldeminas;
}
```
