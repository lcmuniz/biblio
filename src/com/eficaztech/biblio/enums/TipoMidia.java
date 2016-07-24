package com.eficaztech.biblio.enums;

public enum TipoMidia {

	CD("CD"), DVD("DVD"), VHS("VHS");

	private String nome;

	TipoMidia(String nome) {
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
