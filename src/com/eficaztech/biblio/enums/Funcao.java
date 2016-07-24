package com.eficaztech.biblio.enums;

public enum Funcao {

	ADM("Administrador"), BIB("Bibliotecária"), SEC("Secretária");

	private String nome; 

	Funcao(String nome) {
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
