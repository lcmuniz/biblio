package com.eficaztech.biblio.model;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

@Entity
@Table(name = "Config")
public class Empresa {
	
	public static final int QUANTIDADE_EMPRESTIMO = 100; // quantidade de emprestimos nao alunos
	public static final int DIAS_EMPRESTIMO = 100;  // dias de emprestimo nao alunos

	@Id
	@GeneratedValue
	@Column(name = "configId")
	private Long id;

	@NotEmpty(message = "O nome da empresa deve ser informado.")
	@Column(name = "nomeEmpresa", length = 100, nullable = false)
	private String nome;

	@NotEmpty(message = "O nome da biblioteca deve ser informado.")
	@Column(name = "nomeBiblioteca", length = 100, nullable = false)
	private String biblioteca;

	@NotEmpty(message = "O endereço deve ser informado.")
	@Column(length = 300, nullable = false)
	private String endereco;

	@NotEmpty(message = "Os telefones devem ser informados.")
	@Column(length = 50, nullable = false)
	private String telefones;

	@NotNull(message = "O valor da multa deve ser informado.")
	@Column(name = "valorMultaAlunos")
	private BigDecimal valorMulta;

	@NotNull(message = "A quantidade de livros para empréstimo por aluno deve ser informada.")
	@Column(name = "numeroEmprestimosLivrosAlunos")
	private int quantidadeLivrosEmprestimo;

	@NotNull(message = "O número de dias para empréstimo de monografias deve ser informado.")
	@Column(name = "diasEmprestimoMonografiasAlunos")
	private int diasMonografiasEmprestimo;

	@NotNull(message = "A quantidade de monografias para empréstimo por aluno deve ser informada.")
	@Column(name = "numeroEmprestimosMonografiasAlunos")
	private int quantidadeMonografiasEmprestimo;

	@NotNull(message = "O número de dias para empréstimo de livros deve ser informado.")
	@Column(name = "diasEmprestimoLivrosAlunos")
	private int diasLivrosEmprestimo;

	@NotNull(message = "A quantidade de periódicos para empréstimo por pessoa deve ser informada.")
	@Column(name = "numeroEmprestimosPeriodicosAlunos	")
	private int quantidadePeriodicosEmprestimo;

	@NotNull(message = "O número de dias para empréstimo de periódicos deve ser informado.")
	@Column(name = "diasEmprestimoPeriodicosAlunos")
	private int diasPeriodicosEmprestimo;

	@NotNull(message = "A quantidade de mídias para empréstimo por pessoa deve ser informada.")
	@Column(name = "numeroEmprestimosMidiasAlunos")
	private int quantidadeMidiasEmprestimo;

	@NotNull(message = "O número de dias para empréstimo de mídias deve ser informado.")
	@Column(name = "diasEmprestimoMidiasAlunos")
	private int diasMidiasEmprestimo;

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
		Empresa other = (Empresa) obj;
		if (getId() == null) {
			if (other.getId() != null)
				return false;
		} else if (!getId().equals(other.getId()))
			return false;
		return true;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getBiblioteca() {
		return biblioteca;
	}

	public void setBiblioteca(String biblioteca) {
		this.biblioteca = biblioteca;
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

	public BigDecimal getValorMulta() {
		return valorMulta;
	}

	public void setValorMulta(BigDecimal valorMulta) {
		this.valorMulta = valorMulta;
	}

	public int getQuantidadeLivrosEmprestimo() {
		return quantidadeLivrosEmprestimo;
	}

	public void setQuantidadeLivrosEmprestimo(int quantidadeLivrosEmprestimo) {
		this.quantidadeLivrosEmprestimo = quantidadeLivrosEmprestimo;
	}

	public int getDiasLivrosEmprestimo() {
		return diasLivrosEmprestimo;
	}

	public void setDiasLivrosEmprestimo(int diasLivrosEmprestimo) {
		this.diasLivrosEmprestimo = diasLivrosEmprestimo;
	}

	public int getQuantidadePeriodicosEmprestimo() {
		return quantidadePeriodicosEmprestimo;
	}

	public void setQuantidadePeriodicosEmprestimo(
			int quantidadePeriodicosEmprestimo) {
		this.quantidadePeriodicosEmprestimo = quantidadePeriodicosEmprestimo;
	}

	public int getDiasPeriodicosEmprestimo() {
		return diasPeriodicosEmprestimo;
	}

	public void setDiasPeriodicosEmprestimo(int diasPeriodicosEmprestimo) {
		this.diasPeriodicosEmprestimo = diasPeriodicosEmprestimo;
	}

	public int getQuantidadeMidiasEmprestimo() {
		return quantidadeMidiasEmprestimo;
	}

	public void setQuantidadeMidiasEmprestimo(int quantidadeMidiasEmprestimo) {
		this.quantidadeMidiasEmprestimo = quantidadeMidiasEmprestimo;
	}

	public int getDiasMidiasEmprestimo() {
		return diasMidiasEmprestimo;
	}

	public void setDiasMidiasEmprestimo(int diasMidiasEmprestimo) {
		this.diasMidiasEmprestimo = diasMidiasEmprestimo;
	}

	public int getDiasMonografiasEmprestimo() {
		return diasMonografiasEmprestimo;
	}

	public void setDiasMonografiasEmprestimo(int diasMonografiasEmprestimo) {
		this.diasMonografiasEmprestimo = diasMonografiasEmprestimo;
	}

	public int getQuantidadeMonografiasEmprestimo() {
		return quantidadeMonografiasEmprestimo;
	}

	public void setQuantidadeMonografiasEmprestimo(
			int quantidadeMonografiasEmprestimo) {
		this.quantidadeMonografiasEmprestimo = quantidadeMonografiasEmprestimo;
	}

}
