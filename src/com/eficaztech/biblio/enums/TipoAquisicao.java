package com.eficaztech.biblio.enums;

public enum TipoAquisicao {

	COMPRA("Compra"), DOACAO("Doação"), PERMUTA("Permuta");

	private String nome;

	TipoAquisicao(String nome) {
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
