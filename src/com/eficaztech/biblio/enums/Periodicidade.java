package com.eficaztech.biblio.enums;

public enum Periodicidade {

	DIARIO("Diária"), SEMANAL("Semanal"), MENSAL("Mensal"), BIMESTRAL("Bimestral"), TRIMESTRAL("Trimestral"), SEMESTRAL("Semestral"), ANUAL("Anual"), BIENAL("Bienal"), TRIENAL("Trienal"), SEM_PERIODICIDADE("Sem periodicidade");

	private String nome;

	Periodicidade(String nome) {
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
