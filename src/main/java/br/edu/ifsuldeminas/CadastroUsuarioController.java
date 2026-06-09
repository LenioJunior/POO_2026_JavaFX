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
