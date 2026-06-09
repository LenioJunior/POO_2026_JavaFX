# Cadastro de Usuário — Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Build a JavaFX registration screen for creating Aluno, Professor, and Bibliotecário users, persisted to SQL Server via Spring Data JPA.

**Architecture:** Spring Boot starts in `Application.init()` before any window opens; `FXMLLoader` delegates controller instantiation to `context::getBean` so `@Autowired` works inside controllers. Entities share one `usuarios` table via Single Table Inheritance. Validation is a `static` method on the controller for unit-testability.

**Tech Stack:** Java 21, JavaFX 21, Spring Boot 3.5, Spring Data JPA, Hibernate 6, SQL Server (Docker), JUnit 5

---

## File Map

**Modified:**
- `pom.xml` — fix `<mainClass>`
- `src/main/java/module-info.java` — add Spring/JPA module declarations
- `src/main/java/br/edu/ifsuldeminas/App.java` — Spring Boot integration + controller factory
- `src/main/java/br/edu/ifsuldeminas/PrimaryController.java` — add `@Component`, `switchToCadastro()`
- `src/main/java/br/edu/ifsuldeminas/SecondaryController.java` — add `@Component`
- `src/main/java/br/edu/ifsuldeminas/models/Usuario.java` — JPA annotations, `int id` → `Integer id`
- `src/main/java/br/edu/ifsuldeminas/models/Aluno.java` — `@Entity @DiscriminatorValue`
- `src/main/java/br/edu/ifsuldeminas/models/Professor.java` — `@Entity @DiscriminatorValue`
- `src/main/java/br/edu/ifsuldeminas/models/Bibliotecario.java` — `@Entity @DiscriminatorValue`
- `src/main/resources/br/edu/ifsuldeminas/primary.fxml` — add navigation button

**Created:**
- `src/main/resources/application.properties`
- `src/main/java/br/edu/ifsuldeminas/repositories/UsuarioRepository.java`
- `src/main/java/br/edu/ifsuldeminas/CadastroUsuarioController.java`
- `src/main/resources/br/edu/ifsuldeminas/cadastro-usuario.fxml`
- `src/test/java/br/edu/ifsuldeminas/CadastroUsuarioControllerTest.java`

---

### Task 1: Fix mainClass in pom.xml

**Files:**
- Modify: `pom.xml`

- [ ] **Step 1: Update mainClass**

Inside the `javafx-maven-plugin` `<configuration>` block, replace the current value:

```xml
<configuration>
    <mainClass>br.edu.ifsuldeminas.App</mainClass>
</configuration>
```

- [ ] **Step 2: Commit**

```bash
git add pom.xml
git commit -m "fix: correct mainClass to br.edu.ifsuldeminas.App"
```

---

### Task 2: Create application.properties

**Files:**
- Create: `src/main/resources/application.properties`

- [ ] **Step 1: Create the file**

```properties
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=JavaFX;trustServerCertificate=true
spring.datasource.username=sa
spring.datasource.password=Sql1234@%
spring.datasource.driver-class-name=com.microsoft.sqlserver.jdbc.SQLServerDriver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.SQLServerDialect

spring.main.web-application-type=none
```

`spring.main.web-application-type=none` prevents Spring Boot from starting a web server.
`trustServerCertificate=true` avoids SSL errors with the Docker SQL Server container.
`ddl-auto=update` creates the `usuarios` table automatically on first run.

- [ ] **Step 2: Commit**

```bash
git add src/main/resources/application.properties
git commit -m "feat: add application.properties for SQL Server connection"
```

---

### Task 3: Add JPA annotations to entity hierarchy

**Files:**
- Modify: `src/main/java/br/edu/ifsuldeminas/models/Usuario.java`
- Modify: `src/main/java/br/edu/ifsuldeminas/models/Aluno.java`
- Modify: `src/main/java/br/edu/ifsuldeminas/models/Professor.java`
- Modify: `src/main/java/br/edu/ifsuldeminas/models/Bibliotecario.java`

- [ ] **Step 1: Replace Usuario.java**

`id` changes from `int` to `Integer` — required for JPA identity generation (auto-increment needs a nullable type).

```java
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
```

- [ ] **Step 2: Replace Aluno.java**

```java
package br.edu.ifsuldeminas.models;

import jakarta.persistence.*;

@Entity
@DiscriminatorValue("ALUNO")
public class Aluno extends Usuario {
}
```

- [ ] **Step 3: Replace Professor.java**

```java
package br.edu.ifsuldeminas.models;

import jakarta.persistence.*;

@Entity
@DiscriminatorValue("PROFESSOR")
public class Professor extends Usuario {
}
```

- [ ] **Step 4: Replace Bibliotecario.java**

```java
package br.edu.ifsuldeminas.models;

import jakarta.persistence.*;

@Entity
@DiscriminatorValue("BIBLIOTECARIO")
public class Bibliotecario extends Usuario {
}
```

- [ ] **Step 5: Commit**

```bash
git add src/main/java/br/edu/ifsuldeminas/models/
git commit -m "feat: add JPA annotations to Usuario hierarchy (SINGLE_TABLE inheritance)"
```

---

### Task 4: Create UsuarioRepository

**Files:**
- Create: `src/main/java/br/edu/ifsuldeminas/repositories/UsuarioRepository.java`

- [ ] **Step 1: Create the interface**

Spring Data JPA auto-implements `save()`, `findById()`, `findAll()` etc. at runtime.

```java
package br.edu.ifsuldeminas.repositories;

import br.edu.ifsuldeminas.models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
}
```

- [ ] **Step 2: Commit**

```bash
git add src/main/java/br/edu/ifsuldeminas/repositories/
git commit -m "feat: add UsuarioRepository"
```

---

### Task 5: Update module-info.java

**Files:**
- Modify: `src/main/java/module-info.java`

Spring Boot and Hibernate use reflection extensively on our entity and controller packages. The `opens` directives grant that access at runtime.

- [ ] **Step 1: Replace module-info.java**

```java
module br.edu.ifsuldeminas {
    requires javafx.controls;
    requires javafx.fxml;
    requires spring.context;
    requires spring.beans;
    requires spring.boot;
    requires spring.boot.autoconfigure;
    requires spring.data.jpa;
    requires spring.tx;
    requires jakarta.persistence;

    opens br.edu.ifsuldeminas to javafx.fxml, spring.core, spring.beans, spring.context;
    opens br.edu.ifsuldeminas.models to jakarta.persistence, spring.core, org.hibernate.orm.core;
    opens br.edu.ifsuldeminas.repositories to spring.core, spring.data.jpa;

    exports br.edu.ifsuldeminas;
}
```

> **If compilation fails with "module not found"** for any Spring/Hibernate module, change each failing `requires` to `requires static` (makes it optional at compile time). As a last resort, delete `module-info.java` entirely — the app will run as an unnamed module and `--add-opens` is handled automatically by the JVM.

- [ ] **Step 2: Commit**

```bash
git add src/main/java/module-info.java
git commit -m "feat: extend module-info.java for Spring Boot and JPA"
```

---

### Task 6: Integrate Spring Boot into App.java

**Files:**
- Modify: `src/main/java/br/edu/ifsuldeminas/App.java`

`Application.init()` runs before JavaFX creates any window — the right place to start the Spring context. `headless(false)` prevents Spring Boot from forcing AWT headless mode, which would break JavaFX.

- [ ] **Step 1: Replace App.java**

```java
package br.edu.ifsuldeminas;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.IOException;

@SpringBootApplication
public class App extends Application {

    private static ConfigurableApplicationContext context;
    private static Scene scene;

    @Override
    public void init() throws Exception {
        context = new SpringApplicationBuilder(App.class)
                .headless(false)
                .run();
    }

    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("primary"), 800, 540);
        stage.setScene(scene);
        stage.setTitle("Sistema de Biblioteca");
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        context.close();
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        fxmlLoader.setControllerFactory(context::getBean);
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }
}
```

- [ ] **Step 2: Commit**

```bash
git add src/main/java/br/edu/ifsuldeminas/App.java
git commit -m "feat: integrate Spring Boot ApplicationContext into JavaFX App"
```

---

### Task 7: Make existing controllers Spring components

**Files:**
- Modify: `src/main/java/br/edu/ifsuldeminas/PrimaryController.java`
- Modify: `src/main/java/br/edu/ifsuldeminas/SecondaryController.java`

Because `loadFXML` now uses `context::getBean`, every controller class referenced in FXML must be a Spring bean. `switchToCadastro()` is also added here.

- [ ] **Step 1: Replace PrimaryController.java**

```java
package br.edu.ifsuldeminas;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.springframework.stereotype.Component;

@Component
public class PrimaryController {

    @FXML
    private Label lblInfo;

    @FXML
    private void switchToSecondary() throws IOException {
        App.setRoot("secondary");
    }

    @FXML
    private void switchToCadastro() throws IOException {
        App.setRoot("cadastro-usuario");
    }

    @FXML
    private void doAction() {
        lblInfo.setText("Olá meu querido!");
    }
}
```

- [ ] **Step 2: Replace SecondaryController.java**

```java
package br.edu.ifsuldeminas;

import java.io.IOException;
import javafx.fxml.FXML;
import org.springframework.stereotype.Component;

@Component
public class SecondaryController {

    @FXML
    private void switchToPrimary() throws IOException {
        App.setRoot("primary");
    }
}
```

- [ ] **Step 3: Commit**

```bash
git add src/main/java/br/edu/ifsuldeminas/PrimaryController.java \
        src/main/java/br/edu/ifsuldeminas/SecondaryController.java
git commit -m "feat: register existing controllers as Spring @Component"
```

---

### Task 8: Create CadastroUsuarioController (TDD)

**Files:**
- Create: `src/test/java/br/edu/ifsuldeminas/CadastroUsuarioControllerTest.java`
- Create: `src/main/java/br/edu/ifsuldeminas/CadastroUsuarioController.java`

`validarCampos` is `static` so tests run without JavaFX or Spring context.

- [ ] **Step 1: Create the test directory**

```bash
mkdir -p src/test/java/br/edu/ifsuldeminas
```

- [ ] **Step 2: Write the failing test**

Create `src/test/java/br/edu/ifsuldeminas/CadastroUsuarioControllerTest.java`:

```java
package br.edu.ifsuldeminas;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CadastroUsuarioControllerTest {

    @Test
    void nomeVazio_retornaErro() {
        assertEquals("O campo Nome é obrigatório.",
                CadastroUsuarioController.validarCampos("", "a@b.com", "3"));
    }

    @Test
    void emailSemArroba_retornaErro() {
        assertEquals("Informe um e-mail válido.",
                CadastroUsuarioController.validarCampos("João", "semArroba.com", "3"));
    }

    @Test
    void emailVazio_retornaErro() {
        assertEquals("Informe um e-mail válido.",
                CadastroUsuarioController.validarCampos("João", "", "3"));
    }

    @Test
    void limiteNegativo_retornaErro() {
        assertEquals("Limite deve ser um número inteiro não negativo.",
                CadastroUsuarioController.validarCampos("João", "a@b.com", "-1"));
    }

    @Test
    void limiteTexto_retornaErro() {
        assertEquals("Limite deve ser um número inteiro não negativo.",
                CadastroUsuarioController.validarCampos("João", "a@b.com", "abc"));
    }

    @Test
    void camposValidos_retornaNulo() {
        assertNull(CadastroUsuarioController.validarCampos(
                "Maria", "maria@ifsuldeminas.edu.br", "5"));
    }

    @Test
    void limiteZero_valido() {
        assertNull(CadastroUsuarioController.validarCampos("Ana", "ana@x.com", "0"));
    }
}
```

- [ ] **Step 3: Run the test — it must FAIL (class does not exist yet)**

```bash
mvn test -Dtest=CadastroUsuarioControllerTest 2>&1 | tail -15
```

Expected: compilation error — `cannot find symbol: CadastroUsuarioController`.

- [ ] **Step 4: Create CadastroUsuarioController.java**

```java
package br.edu.ifsuldeminas;

import br.edu.ifsuldeminas.models.*;
import br.edu.ifsuldeminas.repositories.UsuarioRepository;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Scope("prototype")
public class CadastroUsuarioController {

    @FXML private Button btnAluno;
    @FXML private Button btnProfessor;
    @FXML private Button btnBibliotecario;
    @FXML private TextField txtNome;
    @FXML private TextField txtEmail;
    @FXML private TextField txtLimite;
    @FXML private Label lblErro;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private Class<? extends Usuario> tipoSelecionado = Aluno.class;

    private static final String ESTILO_ATIVO   = "-fx-background-color:#4a90d9;-fx-text-fill:white;";
    private static final String ESTILO_INATIVO = "";

    @FXML
    private void initialize() {
        destacarBotao(btnAluno);
    }

    @FXML
    private void selecionarAluno() {
        tipoSelecionado = Aluno.class;
        destacarBotao(btnAluno);
    }

    @FXML
    private void selecionarProfessor() {
        tipoSelecionado = Professor.class;
        destacarBotao(btnProfessor);
    }

    @FXML
    private void selecionarBibliotecario() {
        tipoSelecionado = Bibliotecario.class;
        destacarBotao(btnBibliotecario);
    }

    private void destacarBotao(Button ativo) {
        btnAluno.setStyle(ESTILO_INATIVO);
        btnProfessor.setStyle(ESTILO_INATIVO);
        btnBibliotecario.setStyle(ESTILO_INATIVO);
        ativo.setStyle(ESTILO_ATIVO);
    }

    @FXML
    private void salvar() {
        String erro = validarCampos(
                txtNome.getText().trim(),
                txtEmail.getText().trim(),
                txtLimite.getText().trim());

        if (erro != null) {
            mostrarMensagem(erro, false);
            return;
        }

        Usuario usuario;
        if      (tipoSelecionado == Professor.class)      usuario = new Professor();
        else if (tipoSelecionado == Bibliotecario.class)  usuario = new Bibliotecario();
        else                                              usuario = new Aluno();

        usuario.setNome(txtNome.getText().trim());
        usuario.setEmail(txtEmail.getText().trim());
        usuario.setLimiteEmprestimo(Integer.parseInt(txtLimite.getText().trim()));

        usuarioRepository.save(usuario);
        limparFormulario();
        mostrarMensagem("Usuário cadastrado com sucesso!", true);
    }

    @FXML
    private void voltar() throws IOException {
        App.setRoot("primary");
    }

    // package-private for unit tests — no JavaFX or Spring needed
    static String validarCampos(String nome, String email, String limite) {
        if (nome.isBlank())                          return "O campo Nome é obrigatório.";
        if (email.isBlank() || !email.contains("@")) return "Informe um e-mail válido.";
        if (limite.isBlank())                        return "Limite de Empréstimo é obrigatório.";
        try {
            if (Integer.parseInt(limite) < 0)        return "Limite deve ser um número inteiro não negativo.";
        } catch (NumberFormatException e) {
                                                     return "Limite deve ser um número inteiro não negativo.";
        }
        return null;
    }

    private void limparFormulario() {
        txtNome.clear();
        txtEmail.clear();
        txtLimite.clear();
        tipoSelecionado = Aluno.class;
        destacarBotao(btnAluno);
    }

    private void mostrarMensagem(String texto, boolean sucesso) {
        lblErro.setStyle(sucesso ? "-fx-text-fill:#27ae60;" : "-fx-text-fill:#c0392b;");
        lblErro.setText(texto);
    }
}
```

- [ ] **Step 5: Run the tests — all 7 must PASS**

```bash
mvn test -Dtest=CadastroUsuarioControllerTest 2>&1 | tail -15
```

Expected output:
```
Tests run: 7, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

- [ ] **Step 6: Commit**

```bash
git add src/test/java/br/edu/ifsuldeminas/CadastroUsuarioControllerTest.java \
        src/main/java/br/edu/ifsuldeminas/CadastroUsuarioController.java
git commit -m "feat: add CadastroUsuarioController with validation (TDD)"
```

---

### Task 9: Create cadastro-usuario.fxml

**Files:**
- Create: `src/main/resources/br/edu/ifsuldeminas/cadastro-usuario.fxml`

- [ ] **Step 1: Create the FXML file**

```xml
<?xml version="1.0" encoding="UTF-8"?>

<?import javafx.geometry.Insets?>
<?import javafx.scene.control.*?>
<?import javafx.scene.layout.*?>

<VBox spacing="16" prefWidth="520"
      xmlns="http://javafx.com/javafx/21"
      xmlns:fx="http://javafx.com/fxml/1"
      fx:controller="br.edu.ifsuldeminas.CadastroUsuarioController">
    <padding><Insets top="24" right="28" bottom="24" left="28"/></padding>

    <Label text="Cadastrar Usuário"
           style="-fx-font-size:20;-fx-font-weight:bold;"/>

    <VBox spacing="6">
        <Label text="Tipo de Usuário" style="-fx-font-size:12;-fx-text-fill:#555;"/>
        <HBox>
            <Button fx:id="btnAluno" text="Aluno"
                    onAction="#selecionarAluno"
                    prefWidth="120" prefHeight="34"
                    style="-fx-background-color:#4a90d9;-fx-text-fill:white;"/>
            <Button fx:id="btnProfessor" text="Professor"
                    onAction="#selecionarProfessor"
                    prefWidth="120" prefHeight="34"/>
            <Button fx:id="btnBibliotecario" text="Bibliotecário"
                    onAction="#selecionarBibliotecario"
                    prefWidth="130" prefHeight="34"/>
        </HBox>
    </VBox>

    <VBox spacing="14"
          style="-fx-border-color:#ddd;-fx-border-radius:6;-fx-background-color:#fafafa;-fx-background-radius:6;-fx-padding:16;">
        <VBox spacing="4">
            <Label text="Nome completo *" style="-fx-font-size:11;-fx-text-fill:#555;"/>
            <TextField fx:id="txtNome" promptText="Ex: Maria da Silva" prefWidth="440"/>
        </VBox>
        <VBox spacing="4">
            <Label text="E-mail *" style="-fx-font-size:11;-fx-text-fill:#555;"/>
            <TextField fx:id="txtEmail" promptText="email@ifsuldeminas.edu.br" prefWidth="440"/>
        </VBox>
        <VBox spacing="4">
            <Label text="Limite de Empréstimo *" style="-fx-font-size:11;-fx-text-fill:#555;"/>
            <HBox spacing="10" alignment="CENTER_LEFT">
                <TextField fx:id="txtLimite" promptText="0" prefWidth="90"/>
                <Label text="número de itens" style="-fx-text-fill:#888;"/>
            </HBox>
        </VBox>
    </VBox>

    <Label fx:id="lblErro" text="" wrapText="true"
           style="-fx-text-fill:#c0392b;" minHeight="20"/>

    <HBox spacing="12">
        <Button text="Salvar" onAction="#salvar"
                prefWidth="160" prefHeight="36"
                style="-fx-background-color:#4a90d9;-fx-text-fill:white;-fx-font-size:13;"/>
        <Button text="Voltar" onAction="#voltar"
                prefWidth="90" prefHeight="36"/>
    </HBox>
</VBox>
```

- [ ] **Step 2: Commit**

```bash
git add src/main/resources/br/edu/ifsuldeminas/cadastro-usuario.fxml
git commit -m "feat: add cadastro-usuario.fxml layout"
```

---

### Task 10: Add navigation button to primary screen

**Files:**
- Modify: `src/main/resources/br/edu/ifsuldeminas/primary.fxml`

`switchToCadastro()` was already added to `PrimaryController` in Task 7.

- [ ] **Step 1: Add the button inside the Pane children**

Open `src/main/resources/br/edu/ifsuldeminas/primary.fxml`. Inside the `<children>` block of the `<Pane>`, add this button after the last existing child:

```xml
<Button text="Cadastrar Usuário"
        layoutX="24.0" layoutY="310.0"
        onAction="#switchToCadastro" />
```

Full updated `primary.fxml` for reference:

```xml
<?xml version="1.0" encoding="UTF-8"?>

<?import javafx.geometry.Insets?>
<?import javafx.scene.control.Button?>
<?import javafx.scene.control.Label?>
<?import javafx.scene.control.TableColumn?>
<?import javafx.scene.control.TableView?>
<?import javafx.scene.layout.Pane?>
<?import javafx.scene.layout.VBox?>

<VBox alignment="CENTER" prefHeight="480.0" prefWidth="695.0" spacing="20.0"
      xmlns="http://javafx.com/javafx/21" xmlns:fx="http://javafx.com/fxml/1"
      fx:controller="br.edu.ifsuldeminas.PrimaryController">
   <children>
      <Pane prefHeight="400.0" prefWidth="655.0">
         <children>
            <Button fx:id="primaryButton" layoutX="458.0" layoutY="260.0"
                    onAction="#switchToSecondary" text="Switch to Secondary View" />
            <Button layoutX="24.0" layoutY="27.0" mnemonicParsing="false"
                    onAction="#doAction" text="Set Text" />
            <Label fx:id="lblInfo" layoutX="24.0" layoutY="72.0" text="Label" />
            <TableView fx:id="table" layoutX="24.0" layoutY="98.0"
                       prefHeight="200.0" prefWidth="200.0">
              <columns>
                <TableColumn prefWidth="75.0" text="C1" />
                <TableColumn prefWidth="75.0" text="C2" />
              </columns>
            </TableView>
            <Button text="Cadastrar Usuário"
                    layoutX="24.0" layoutY="310.0"
                    onAction="#switchToCadastro" />
         </children>
      </Pane>
   </children>
   <padding>
      <Insets bottom="20.0" left="20.0" right="20.0" top="20.0" />
   </padding>
</VBox>
```

- [ ] **Step 2: Commit**

```bash
git add src/main/resources/br/edu/ifsuldeminas/primary.fxml
git commit -m "feat: add Cadastrar Usuário navigation button to primary screen"
```

---

### Task 11: Smoke test

- [ ] **Step 1: Start the SQL Server Docker container**

```bash
docker compose -f docker/docker-compose.yml up -d
```

Wait ~15 seconds for SQL Server to initialise.

- [ ] **Step 2: Build the project**

```bash
mvn compile 2>&1 | tail -20
```

Expected: `BUILD SUCCESS`. If module resolution errors appear, follow the fallback note in Task 5.

- [ ] **Step 3: Run the application**

```bash
mvn javafx:run
```

Expected: the primary screen opens with a "Cadastrar Usuário" button visible.

- [ ] **Step 4: Test the registration flow**

1. Click "Cadastrar Usuário" → registration screen opens, Aluno tab highlighted
2. Select "Professor" → tab turns blue
3. Click Salvar with empty fields → "O campo Nome é obrigatório." in red
4. Fill Nome, leave Email blank → "Informe um e-mail válido."
5. Fill all fields correctly, click Salvar → "Usuário cadastrado com sucesso!" in green, form clears
6. Click Voltar → returns to primary screen

- [ ] **Step 5: Verify the row in the database**

```bash
docker exec -it sqlserver /opt/mssql-tools18/bin/sqlcmd \
  -S localhost -U sa -P 'Sql1234@%' -No \
  -Q "SELECT id, tipo, nome, email, limite_emprestimo FROM usuarios"
```

Expected: one row matching the submitted values.

- [ ] **Step 6: Stop Docker**

```bash
docker compose -f docker/docker-compose.yml stop
```
