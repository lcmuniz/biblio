package com.eficaztech.biblio.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.eficaztech.biblio.enums.Periodicidade;
import com.eficaztech.biblio.enums.SimNao;

@Entity
@Table(name = "Periodico")
public class Periodico {

	@Id
	@GeneratedValue
	@Column(name = "periodicoId")
	private Long id;

	@NotEmpty(message = "O título deve ser informado.")
	@Column(length = 200, nullable = false)
	private String titulo;

	@Column(length = 200)
	private String subtitulo;

	@ManyToMany
	private List<Curso> cursos;

	@NotNull(message = "O local deve ser informado.")
	@ManyToOne
	@JoinColumn(name="localId")
	private Local local;

	@NotNull(message = "O idioma deve ser informado.")
	@ManyToOne
	@JoinColumn(name="idiomaId")
	private Idioma idioma;

	@NotNull(message = "A editora deve ser informada.")
	@ManyToOne
	@JoinColumn(name="editoraId")
	private Editora editora;

	@NotNull(message = "A periodicidade deve ser informada.")
	@Enumerated(EnumType.STRING)
	private Periodicidade periodicidade;

	@NotNull(message = "Deve ser informado se é consulta local ou não.")
	@Enumerated(EnumType.STRING)
	private SimNao consultaLocal;

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
		Periodico other = (Periodico) obj;
		if (getId() == null) {
			if (other.getId() != null)
				return false;
		} else if (!getId().equals(other.getId()))
			return false;
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

	public String getSubtitulo() {
		return subtitulo;
	}

	public void setSubtitulo(String subtitulo) {
		this.subtitulo = subtitulo;
	}

	public List<Curso> getCursos() {
		return cursos;
	}

	public void setCursos(List<Curso> cursos) {
		this.cursos = cursos;
	}

	public Local getLocal() {
		return local;
	}

	public void setLocal(Local local) {
		this.local = local;
	}

	public Idioma getIdioma() {
		return idioma;
	}

	public void setIdioma(Idioma idioma) {
		this.idioma = idioma;
	}

	public Editora getEditora() {
		return editora;
	}

	public void setEditora(Editora editora) {
		this.editora = editora;
	}

	public Periodicidade getPeriodicidade() {
		return periodicidade;
	}

	public void setPeriodicidade(Periodicidade periodicidade) {
		this.periodicidade = periodicidade;
	}

	public SimNao getConsultaLocal() {
		return consultaLocal;
	}

	public void setConsultaLocal(SimNao consultaLocal) {
		this.consultaLocal = consultaLocal;
	}

}
