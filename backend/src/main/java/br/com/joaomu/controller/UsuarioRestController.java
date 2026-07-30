package br.com.joaomu.controller;

import org.springframework.web.bind.annotation.*;
import br.com.joaomu.entity.Usuario;
import br.com.joaomu.service.UsuarioService;
import br.com.joaomu.service.UploadService;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/usuarios")
public class UsuarioRestController extends BaseRestController<Usuario, Long> {

    private final UploadService uploadService;
    private final UsuarioService usuarioService;

    public UsuarioRestController(UsuarioService usuarioService, UploadService uploadService) {
        super(usuarioService);
        this.usuarioService = usuarioService;
        this.uploadService = uploadService;
    }

    // O POST de imagem de perfil fica aqui e não no MidiaController,
    // já que existe a mutação da entidade de negócio, e a entidade
    // Midia não tem um atributo público que recebe a referência
    // para o User.

    @PostMapping("/me/avatar")
    public ResponseEntity<Usuario> uploadAvatar(
            @RequestParam("file") MultipartFile file,
            Authentication authentication) {
        String username = authentication.getName();
        String caminhoAvatar = uploadService.uploadImage(file);

        Usuario usuario = usuarioService.buscarPorUsername(username);
        usuario.setAvatar(caminhoAvatar);
        Usuario usuarioAtualizado = service.salvar(usuario);

        return ResponseEntity.ok(usuarioAtualizado);
    }
}
