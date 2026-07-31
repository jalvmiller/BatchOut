package br.com.joaomu.config;

import br.com.joaomu.entity.Despesa;
import br.com.joaomu.entity.Usuario;
import br.com.joaomu.entity.enums.CategoriaDespesa;
import br.com.joaomu.entity.enums.StatusDespesa;
import br.com.joaomu.repository.DespesaRepository;
import br.com.joaomu.repository.UsuarioRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Component
public class DatabaseSeeder implements CommandLineRunner {

	private final UsuarioRepository usuarioRepository;
	private final DespesaRepository despesaRepository;
	private final PasswordEncoder passwordEncoder;

	public DatabaseSeeder(UsuarioRepository usuarioRepository,
			DespesaRepository despesaRepository,
			PasswordEncoder passwordEncoder) {
		this.usuarioRepository = usuarioRepository;
		this.despesaRepository = despesaRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public void run(String... args) throws Exception {
		if (usuarioRepository.count() == 0) {
			System.out.println("=== Banco vazio. Iniciando seed... ===");

			// ===============================================
			// Usuários de teste
			// ===============================================

			Usuario admin = new Usuario();
			admin.setUsername("admin");
			admin.setPassword(passwordEncoder.encode("admin123"));
			admin.setNome("Admin BatchOut");
			admin.setEmail("admin@batchout.com");
			admin.setAvatar("https://i.pravatar.cc/150?u=admin");
			admin.setPontos(100);
			admin.setEspecialista(true);
			admin.setAdministrador(true);

			// Gestor = especialista, pode aprovar/rejeitar despesas
			Usuario gestor = new Usuario();
			gestor.setUsername("gestor");
			gestor.setPassword(passwordEncoder.encode("gestor123"));
			gestor.setNome("Ana Gestora");
			gestor.setEmail("gestor@batchout.com");
			gestor.setAvatar("https://i.pravatar.cc/150?u=gestor");
			gestor.setPontos(50);
			gestor.setEspecialista(true);
			gestor.setAdministrador(false);

			// Colaborador = usuário comum, registra despesas
			Usuario colaborador = new Usuario();
			colaborador.setUsername("colaborador");
			colaborador.setPassword(passwordEncoder.encode("colab123"));
			colaborador.setNome("João Colaborador");
			colaborador.setEmail("colaborador@batchout.com");
			colaborador.setPontos(0);
			colaborador.setEspecialista(false);
			colaborador.setAdministrador(false);

			List<Usuario> usuarios = usuarioRepository.saveAll(Arrays.asList(admin, gestor, colaborador));
			System.out.println("Usuários criados: admin, gestor, colaborador");

			// ===============================================
			// Despesas de exemplo para visualização e testes
			// ===============================================

			// Despesa PENDENTE — aguardando aprovação do gestor
			Despesa d1 = new Despesa();
			d1.setTitulo("Jantar com cliente");
			d1.setDescricao("Reunião de negócios com cliente da XYZ Ltda.");
			d1.setValor(new BigDecimal("187.50"));
			d1.setCategoria(CategoriaDespesa.ALIMENTACAO);
			d1.setStatus(StatusDespesa.PENDENTE);
			d1.setDataOcorrencia(LocalDate.now().minusDays(3));
			d1.setDataCriacao(LocalDateTime.now().minusDays(2));
			d1.setUsuario(colaborador);

			// Despesa APROVADA — já tem aprovador e data de aprovação
			Despesa d2 = new Despesa();
			d2.setTitulo("Passagem aérea, São Paulo");
			d2.setDescricao("Viagem para reunião de planejamento Q3.");
			d2.setValor(new BigDecimal("890.00"));
			d2.setCategoria(CategoriaDespesa.TRANSPORTE);
			d2.setStatus(StatusDespesa.APROVADA);
			d2.setDataOcorrencia(LocalDate.now().minusDays(10));
			d2.setDataCriacao(LocalDateTime.now().minusDays(9));
			d2.setAprovador(gestor);
			d2.setDataAprovacao(LocalDateTime.now().minusDays(8));
			d2.setUsuario(colaborador);

			// Despesa REJEITADA — tem motivo de rejeição
			Despesa d3 = new Despesa();
			d3.setTitulo("Notebook pessoal");
			d3.setDescricao("Compra de notebook para uso no trabalho.");
			d3.setValor(new BigDecimal("4500.00"));
			d3.setCategoria(CategoriaDespesa.EQUIPAMENTO);
			d3.setStatus(StatusDespesa.REJEITADA);
			d3.setDataOcorrencia(LocalDate.now().minusDays(15));
			d3.setDataCriacao(LocalDateTime.now().minusDays(14));
			d3.setAprovador(gestor);
			d3.setDataAprovacao(LocalDateTime.now().minusDays(13));
			d3.setMotivoRejeicao("Equipamentos pessoais não são cobertos pela política de reembolso.");
			d3.setUsuario(colaborador);

			// Despesa PENDENTE do admin
			Despesa d4 = new Despesa();
			d4.setTitulo("Licença IntelliJ IDEA");
			d4.setDescricao("Renovação anual da licença da IDE.");
			d4.setValor(new BigDecimal("299.00"));
			d4.setCategoria(CategoriaDespesa.SOFTWARE);
			d4.setStatus(StatusDespesa.PENDENTE);
			d4.setDataOcorrencia(LocalDate.now().minusDays(1));
			d4.setDataCriacao(LocalDateTime.now().minusHours(5));
			d4.setUsuario(admin);

			despesaRepository.saveAll(Arrays.asList(d1, d2, d3, d4));
			System.out.println("Despesas de exemplo criadas: 2 PENDENTE, 1 APROVADA, 1 REJEITADA");
			System.out.println("=== Seed realizado ===");
		} else {
			System.out.println("Banco de dados já contém usuários, pular seed");
		}
	}
}
