package com.eficaztech.biblio.enums;

public enum TipoCliente {

	ALUNO("Aluno"), PROFESSOR("Professor"), FUNCIONARIO("Funcionário");

	private String nome;

	TipoCliente(String nome) {
		this.nome = nome;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	@Override
	public String toString() {
		return nome;
	}

}
