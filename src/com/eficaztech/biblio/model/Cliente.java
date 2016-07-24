package com.eficaztech.biblio.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import com.eficaztech.biblio.enums.SimNao;
import com.eficaztech.biblio.enums.TipoCliente;

@Entity
@Table(name = "Cliente")
public class Cliente {

	@Id
	@GeneratedValue
	@Column(name = "clienteId")
	private Long id;

	@NotNull(message = "O código deve ser informado.")
	@Column(nullable = false)
	private Long codigo;

	@NotEmpty(message = "O nome deve ser informado.")
	@Column(length = 100, nullable = false)
	private String nome;

	@Column(length = 300)
	private String endereco;

	@Column(length = 100)
	private String telefones;

	@Email(message = "Não é um email válido.")
	@Column(length = 100)
	private String email;

	@NotNull(message = "O tipo de usuário deve ser informado.")
	@Enumerated(EnumType.STRING)
	private TipoCliente tipoCliente;

	@NotNull(message = "Deve ser informado se o usuário está ativo ou não.")
	@Enumerated(EnumType.STRING)
	private SimNao ativo;

	@ManyToMany
	private List<Curso> cursos;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Cliente other = (Cliente) obj;
		if (getId() == null) {
			if (other.getId() != null)
				return false;
		} else if (!getId().equals(other.getId()))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return nome;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getCodigo() {
		return codigo;
	}

	public void setCodigo(Long codigo) {
		this.codigo = codigo;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getEndereco() {
		return endereco;
	}

	public void setEndereco(String endereco) {
		this.endereco = endereco;
	}

	public String getTelefones() {
		return telefones;
	}

	public void setTelefones(String telefones) {
		this.telefones = telefones;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public TipoCliente getTipoCliente() {
		return tipoCliente;
	}

	public void setTipoCliente(TipoCliente tipoCliente) {
		this.tipoCliente = tipoCliente;
	}

	public SimNao getAtivo() {
		return ativo;
	}

	public void setAtivo(SimNao ativo) {
		this.ativo = ativo;
	}

	public boolean atingiuLimiteLivros() {

		// pega livros emprestados
		List<EmprestimoLivro> livros = EmprestimoLivroDao.findByCliente(
				this.getId(), false);

		// pega quantos pode emprestar
		int quantosPodeEmprestar = Empresa.QUANTIDADE_EMPRESTIMO;
		if (this.getTipoCliente() == TipoCliente.ALUNO) {
			Empresa empresa = EmpresaDao.findFirst();
			quantosPodeEmprestar = empresa.getQuantidadeLivrosEmprestimo();
		}

		// testa se ja passou do limite
		if (livros.size() < quantosPodeEmprestar) {
			return false;
		} else {
			return true;
		}

	}

	public boolean atingiuLimiteMidias() {
		// pega midias emprestadas
		List<EmprestimoMidia> midias = EmprestimoMidiaDao.findByCliente(
				this.getId(), false);

		// pega quantos pode emprestar
		int quantosPodeEmprestar = Empresa.QUANTIDADE_EMPRESTIMO;
		if (this.getTipoCliente() == TipoCliente.ALUNO) {
			Empresa empresa = EmpresaDao.findFirst();
			quantosPodeEmprestar = empresa.getQuantidadeMidiasEmprestimo();
		}

		// testa se ja passou do limite
		if (midias.size() < quantosPodeEmprestar) {
			return false;
		} else {
			return true;
		}
	}

	public boolean atingiuLimiteMonografias() {
		// pega monografias emprestadas
		List<EmprestimoMonografia> monografias = EmprestimoMonografiaDao
				.findByCliente(this.getId(), false);

		// pega quantos pode emprestar
		int quantosPodeEmprestar = Empresa.QUANTIDADE_EMPRESTIMO;
		if (this.getTipoCliente() == TipoCliente.ALUNO) {
			Empresa empresa = EmpresaDao.findFirst();
			quantosPodeEmprestar = empresa.getQuantidadeMonografiasEmprestimo();
		}

		// testa se ja passou do limite
		if (monografias.size() < quantosPodeEmprestar) {
			return false;
		} else {
			return true;
		}
	}

	public boolean atingiuLimiteEdicoes() {
		// pega edicoes monografias emprestadas
		List<EmprestimoEdicao> edicoes = EmprestimoEdicaoDao.findByCliente(
				this.getId(), false);

		// pega quantos pode emprestar
		int quantosPodeEmprestar = Empresa.QUANTIDADE_EMPRESTIMO;
		if (this.getTipoCliente() == TipoCliente.ALUNO) {
			Empresa empresa = EmpresaDao.findFirst();
			quantosPodeEmprestar = empresa.getQuantidadePeriodicosEmprestimo();
		}

		// testa se ja passou do limite
		if (edicoes.size() < quantosPodeEmprestar) {
			return false;
		} else {
			return true;
		}
	}

}