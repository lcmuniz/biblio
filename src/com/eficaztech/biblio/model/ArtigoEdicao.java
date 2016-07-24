package com.eficaztech.biblio.model;

import java.util.Date;
import java.util.Random;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

@Entity
@Table(name = "ArtigoEdicao")
public class ArtigoEdicao {

	@Transient
	private long uid;

	@Id
	@GeneratedValue
	@Column(name = "artigoEdicaoId")
	private Long id;

	@NotNull(message = "A edição deve ser informada.")
	@ManyToOne
	@JoinColumn(name = "edicaoId")
	private Edicao edicao;

	@NotEmpty(message = "e1_titulo=O título deve ser informado.")
	@Column(length = 200, nullable = false)
	private String titulo;

	@NotEmpty(message = "e1_subtitulo=O subtítulo deve ser informado.")
	@Column(length = 200, nullable = false)
	private String subtitulo;

	@NotEmpty(message = "e1_pagina=A página deve ser informada.")
	@Column(length = 10)
	private String pagina;

	@NotEmpty(message = "e1_autores=Os autores devem ser informados.")
	@Column
	private String autores;

	@NotEmpty(message = "e1_assuntos=Os assuntos devem ser informados.")
	@Column
	private String assuntos;

	@NotEmpty(message = "e1_resumo =O resumo devem ser informado.")
	@Column
	private String resumo;

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
		ArtigoEdicao other = (ArtigoEdicao) obj;
		if (getId() == null) {
			if (other.getId() != null)
				return false;
		} else if (!getId().equals(other.getId()))
			return false;
		if (getId().equals(0L) && other.getId().equals(0L))
			return getUid() == other.getUid();
		return true;
	}

	@Override
	public String toString() {
		return titulo;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getTituloUpperCase() {
		return titulo.toUpperCase();
	}

	public String getSubtitulo() {
		return subtitulo;
	}

	public void setSubtitulo(String subtitulo) {
		this.subtitulo = subtitulo;
	}

	public String getPagina() {
		return pagina;
	}

	public void setPagina(String pagina) {
		this.pagina = pagina;
	}

	public String getAutores() {
		return autores;
	}

	public void setAutores(String autores) {
		this.autores = autores;
	}

	public String getAssuntos() {
		return assuntos;
	}

	public void setAssuntos(String assuntos) {
		this.assuntos = assuntos;
	}

	public String getResumo() {
		return resumo;
	}

	public void setResumo(String resumo) {
		this.resumo = resumo;
	}

	public Edicao getEdicao() {
		return edicao;
	}

	public void setEdicao(Edicao edicao) {
		this.edicao = edicao;
	}

	public long getUid() {
		if (uid == 0) {
			uid = new Random().nextInt(10000) + new Date().getTime();
		}
		return uid;
	}

	public void setUid(long uid) {
		this.uid = uid;
	}

}
