package br.com.joaomu.config;

import br.com.joaomu.entity.Usuario;
import br.com.joaomu.repository.UsuarioRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class DatabaseSeeder implements CommandLineRunner {

	private final UsuarioRepository usuarioRepository;
	private final PasswordEncoder passwordEncoder;

	public DatabaseSeeder(UsuarioRepository usuarioRepository,
			PasswordEncoder passwordEncoder) {
		this.usuarioRepository = usuarioRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public void run(String... args) throws Exception {
		if (usuarioRepository.count() == 0) {
			System.out.println("=== Banco de dados vazio. Iniciando seed de usuários... ===");

			Usuario admin = new Usuario();
			admin.setUsername("admin");
			admin.setPassword(passwordEncoder.encode("admin123"));
			admin.setNome("Admin BatchOut");
			admin.setEmail("admin@batchout.com");
			admin.setAvatar("https://i.pravatar.cc/150?u=admin");
			admin.setPontos(100);
			admin.setEspecialista(true);
			admin.setAdministrador(true);

			Usuario gestor = new Usuario();
			gestor.setUsername("gestor");
			gestor.setPassword(passwordEncoder.encode("gestor123"));
			gestor.setNome("Ana Gestora");
			gestor.setEmail("gestor@batchout.com");
			gestor.setAvatar("https://i.pravatar.cc/150?u=gestor");
			gestor.setPontos(50);
			gestor.setEspecialista(true);
			gestor.setAdministrador(false);

			Usuario colaborador = new Usuario();
			colaborador.setUsername("colaborador");
			colaborador.setPassword(passwordEncoder.encode("colab123"));
			colaborador.setNome("João Colaborador");
			colaborador.setEmail("colaborador@batchout.com");
			colaborador.setPontos(0);
			colaborador.setEspecialista(false);
			colaborador.setAdministrador(false);

			usuarioRepository.saveAll(Arrays.asList(admin, gestor, colaborador));
			System.out.println("Usuários de teste cadastrados com sucesso!");
			System.out.println("=== Seed concluído! ===");
		} else {
			System.out.println("Banco de dados já contém usuários. Pulando seed.");
		}
	}
}
