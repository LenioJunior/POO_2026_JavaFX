# Biblioteca — Sistema de Gerenciamento de Biblioteca

Aplicação desktop desenvolvida em **JavaFX 21** com **Spring Boot 3.5** e persistência em **SQL Server**, criada como projeto acadêmico no IFSULDEMINAS.

---

## Visão Geral

O sistema permite o cadastro e gerenciamento de usuários de uma biblioteca (Alunos, Professores e Bibliotecários) e seus materiais (Livros, DVDs e Revistas). A interface gráfica é construída com JavaFX e a camada de dados usa Spring Data JPA, com o banco de dados SQL Server rodando em Docker.

---

## Stack

| Camada | Tecnologia |
|---|---|
| Interface gráfica | JavaFX 21 + FXML |
| Injeção de dependência | Spring Boot 3.5 / Spring Context |
| Persistência | Spring Data JPA + Hibernate 6 |
| Banco de dados | SQL Server (via Docker) |
| Linguagem | Java 21 (módulo JPMS) |
| Build | Maven (via `mvnw`) |
| Testes | JUnit 5 |

---

## Extensões do Visual Studio Code

| Extensão | Descrição | Publicador |
|---|---|---|
| [FXML Language Mode](https://marketplace.visualstudio.com/items?itemName=sosuisha.fxml-language-mode) | Language support for FXML (JavaFX) files | sosuisha |
| [JavaFX Support](https://marketplace.visualstudio.com/items?itemName=shrey150.javafx-support) | Fixes "Language Support for Java(TM) by Red Hat" when using JavaFX | Shrey Pandya |
| [XML](https://marketplace.visualstudio.com/items?itemName=redhat.vscode-xml) | XML Language Support by Red Hat | Red Hat |

---

## Configuração do Visual Code

- Utilizar a opção **Ctrl + Shift + P** — Preferences - Open User Settings (JSON)
- Ir até o final do arquivo e adicionar uma vírgula no final da última linha 
- Colar o texto: **"javafx.sceneBuilder.path": "/opt/scenebuilder/bin/SceneBuilder"**
- Lembre-se de ajustar o texto colado para a pasta onde foi feita o download.
- Se a instalação foi executada pelo script, será na pasta do usuário, dentro de Downloads/Tools, onde terá uma pasta SceneBuilder

---


## Arquitetura

A aplicação usa um padrão de integração Spring + JavaFX:

1. `App.java` estende `javafx.application.Application`.
2. No método `init()`, o contexto Spring é iniciado via `SpringApplicationBuilder` antes que qualquer janela seja exibida.
3. O `FXMLLoader` usa `context::getBean` como fábrica de controladores, de modo que todos os controladores JavaFX recebem injeção de dependência do Spring normalmente.
4. Controladores são anotados com `@Component @Scope("prototype")` para que uma nova instância seja criada a cada vez que uma tela é carregada.

```
App.init()  ──►  SpringApplicationBuilder.run()  ──►  ApplicationContext
App.start() ──►  FXMLLoader (controllerFactory = context::getBean)
                   └──► Controller com @Autowired injetado pelo Spring
```

---

## Módulos de Domínio

### Usuários (`br.edu.ifsuldeminas.models`)

Hierarquia com herança JPA do tipo **SINGLE_TABLE** — todos os tipos compartilham a tabela `usuarios`, diferenciados pela coluna `tipo`.

```
Usuario (abstract, @Entity)
├── Aluno          — tipo = "ALUNO"
├── Professor      — tipo = "PROFESSOR"
└── Bibliotecario  — tipo = "BIBLIOTECARIO"
```

Campos comuns: `id`, `nome`, `email`, `limiteEmprestimo`.

### Materiais de Biblioteca (`br.edu.ifsuldeminas.models`)

Hierarquia de domínio (sem persistência JPA implementada ainda):

```
MaterialBiblioteca (abstract)
├── Livro    — adiciona campo `autor`
├── DVD
└── Revista
```

Campos comuns: `codigo`, `titulo`, `disponivel`, `prazoEmprestimo`.

### Repositórios (`br.edu.ifsuldeminas.repositories`)

| Interface | Descrição |
|---|---|
| `UsuarioRepository` | CRUD completo via `JpaRepository<Usuario, Integer>` |

---

## Telas

### Tela Principal (`primary.fxml` / `PrimaryController`)

Tela de entrada do sistema. Contém atalhos para as demais seções.

- Botão **Set Text** — demonstração de interação com label
- Botão **Switch to Secondary View** — navega para a tela secundária
- Botão **Cadastrar Usuário** — navega para a tela de cadastro de usuários

### Tela Secundária (`secondary.fxml` / `SecondaryController`)

Tela auxiliar de exemplo. Contém apenas navegação de volta para a tela principal.

### Cadastro de Usuário (`cadastro-usuario.fxml` / `CadastroUsuarioController`)

Tela para criação de novos usuários no sistema.

**Campos:**
- **Tipo de Usuário** — seleção por botões de alternância: Aluno / Professor / Bibliotecário
- **Nome completo** — texto livre, obrigatório
- **E-mail** — deve conter `@`, obrigatório
- **Limite de Empréstimo** — número inteiro não negativo, obrigatório

**Comportamento:**
- O botão do tipo selecionado fica destacado em azul
- Erros de validação são exibidos em vermelho abaixo do formulário
- Ao salvar com sucesso, o formulário é limpo e uma mensagem verde confirma o cadastro
- O botão **Voltar** retorna à tela principal sem salvar

**Validação** (`validarCampos`): método estático testável de forma unitária, sem dependência de JavaFX ou Spring.

---

## Configuração e Execução

### Pré-requisitos

- Java 21
- Docker

### 1. Subir o banco de dados

```bash
docker compose -f docker/docker-compose.yml up -d
```

Isso sobe um container SQL Server na porta `1433` com o banco `JavaFX` criado automaticamente.

### 2. Executar a aplicação

```bash
mvn javafx:run
```

O Spring Boot criará/atualizará as tabelas automaticamente via `spring.jpa.hibernate.ddl-auto=update`.

### 3. Executar os testes

```bash
mvn clean test
```

---

## Estrutura de Arquivos

```
src/main/java/br/edu/ifsuldeminas/
├── App.java                          # Ponto de entrada; integração Spring + JavaFX
├── PrimaryController.java            # Controlador da tela principal
├── SecondaryController.java          # Controlador da tela secundária
├── CadastroUsuarioController.java    # Controlador da tela de cadastro
├── models/
│   ├── Usuario.java                  # Entidade JPA base (SINGLE_TABLE)
│   ├── Aluno.java
│   ├── Professor.java
│   ├── Bibliotecario.java
│   ├── MaterialBiblioteca.java       # Domínio de materiais (sem JPA ainda)
│   ├── Livro.java
│   ├── DVD.java
│   └── Revista.java
└── repositories/
    └── UsuarioRepository.java        # Spring Data JPA

src/main/resources/br/edu/ifsuldeminas/
├── primary.fxml
├── secondary.fxml
└── cadastro-usuario.fxml

src/main/resources/
└── application.properties            # Datasource e configurações JPA

src/test/java/br/edu/ifsuldeminas/
└── CadastroUsuarioControllerTest.java # 7 testes unitários de validação

docker/
└── docker-compose.yml                # SQL Server container
```

---

## Banco de Dados

A string de conexão está em `src/main/resources/application.properties`:

```
jdbc:sqlserver://localhost:1433;databaseName=JavaFX;trustServerCertificate=true
```

Usuário: `sa` / Senha: `Sql1234@%`

O Hibernate gerencia o schema automaticamente (`ddl-auto=update`). A tabela principal criada é:

```sql
usuarios (id, tipo, nome, email, limite_emprestimo)
```
