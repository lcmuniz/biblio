package com.eficaztech.biblio.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.eficaztech.biblio.enums.SimNao;
import com.eficaztech.biblio.enums.TipoAquisicao;

@Entity
@Table(name = "Livro")
public class Livro {

	@Id
	@GeneratedValue
	@Column(name = "livroId")
	private Long id;

	@NotEmpty(message = "O título deve ser informado.")
	@Column(length = 200, nullable = false)
	private String titulo;

	@Column(length = 200)
	private String subtitulo;

	@Column
	private String autores;

	@Column
	private String assuntos;

	@Column(length = 50)
	private String classificacao;

	@Column(length = 50)
	private String cutter;

	@Column(length = 50)
	private String isbn;

	@NotNull(message = "O local deve ser informado.")
	@ManyToOne
	@JoinColumn(name = "localId")
	private Local local;

	@NotNull(message = "O idioma deve ser informado.")
	@ManyToOne
	@JoinColumn(name = "idiomaId")
	private Idioma idioma;

	@NotNull(message = "A editora deve ser informada.")
	@ManyToOne
	@JoinColumn(name = "editoraId")
	private Editora editora;

	@NotNull(message = "O tipo de aquisição deve ser informado.")
	@Enumerated(EnumType.STRING)
	private TipoAquisicao tipoAquisicao;

	@Column(length = 10)
	private String ano;

	@Column(length = 10)
	private String edicao;

	@Column(length = 20)
	private String volume;

	@Column(length = 50)
	private String serie;

	@Column(length = 10)
	private String numeroPaginas;

	@NotNull(message = "Deve ser informado se é consulta local ou não.")
	@Enumerated(EnumType.STRING)
	private SimNao consultaLocal;

	@NotNull(message = "Deve ser informado se o livro está ativo ou não.")
	@Enumerated(EnumType.STRING)
	private SimNao ativo;

	@ManyToMany
	private List<Curso> cursos;

	@OneToMany(mappedBy = "livro", cascade = CascadeType.ALL)
	private List<ExemplarLivro> exemplares;

	public List<ExemplarLivro> getExemplares() {
		return exemplares;
	}

	public void setExemplares(List<ExemplarLivro> exemplares) {
		this.exemplares = exemplares;
	}

	@AssertTrue(message = "O livro deve ter pelo menos um exemplar.")
	public boolean isDeveTerExemplares() {
		if (exemplares.size() == 0) {
			return false;
		}
		return true;
	}

	@AssertTrue(message = "O livro deve ter pelo menos um curso.")
	public boolean isDeveTerCursos() {
		if (cursos == null || cursos.size() == 0) {
			return false;
		}
		return true;
	}

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
		Livro other = (Livro) obj;
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

	public String getTituloCompleto() {
		if (subtitulo == null || subtitulo.equals("")) {
			return titulo;
		} else {
			return titulo + ": " + subtitulo;
		}
	}
	
	public String getTituloCompletoUpperCase() {
		if (subtitulo == null || subtitulo.equals("")) {
			return titulo.toUpperCase();
		} else {
			return titulo.toUpperCase() + ": " + subtitulo.toUpperCase();
		}
	}

	public String getSubtitulo() {
		return subtitulo;
	}

	public void setSubtitulo(String subtitulo) {
		this.subtitulo = subtitulo;
	}

	public String getSubtituloUpperCase() {
		return subtitulo.toUpperCase();
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

	public String getClassificacao() {
		return classificacao;
	}

	public void setClassificacao(String classificacao) {
		this.classificacao = classificacao;
	}

	public String getCutter() {
		return cutter;
	}

	public void setCutter(String cutter) {
		this.cutter = cutter;
	}

	public String getIsbn() {
		return isbn;
	}

	public void setIsbn(String isbn) {
		this.isbn = isbn;
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

	public TipoAquisicao getTipoAquisicao() {
		return tipoAquisicao;
	}

	public void setTipoAquisicao(TipoAquisicao tipoAquisicao) {
		this.tipoAquisicao = tipoAquisicao;
	}

	public String getAno() {
		return ano;
	}

	public void setAno(String ano) {
		this.ano = ano;
	}

	public String getEdicao() {
		return edicao;
	}

	public void setEdicao(String edicao) {
		this.edicao = edicao;
	}

	public String getVolume() {
		return volume;
	}

	public void setVolume(String volume) {
		this.volume = volume;
	}

	public String getSerie() {
		return serie;
	}

	public void setSerie(String serie) {
		this.serie = serie;
	}

	public String getNumeroPaginas() {
		return numeroPaginas;
	}

	public void setNumeroPaginas(String numeroPaginas) {
		this.numeroPaginas = numeroPaginas;
	}

	public SimNao getConsultaLocal() {
		return consultaLocal;
	}

	public void setConsultaLocal(SimNao consultaLocal) {
		this.consultaLocal = consultaLocal;
	}

	public List<Curso> getCursos() {
		return cursos;
	}

	public void setCursos(List<Curso> cursos) {
		this.cursos = cursos;
	}

	public String getTextoExemplaresDisponíveis() {

		String ret1 = "";
		String ret2 = "";

		int exemplares = this.exemplares.size();

		if (exemplares == 0) {
			ret1 = "Nenhum exemplar";
		} else if (exemplares == 1) {
			ret1 = "1 exemplar";
		} else {
			ret1 = exemplares + " exemplares";
		}

		
		int quant = 0;
		for (ExemplarLivro exemplar : this.exemplares) {
			if (exemplar.getCliente() == null) {
				quant++;
			}
		}

		if (quant == 0) {
			ret2 = "Nenhum disponível";
		} else if (quant == 1) {
			ret2 = "1 disponível";
		} else {
			ret2 = quant + " disponíveis";
		}

		return ret1 + " (" + ret2 + ")";
	}

	public SimNao getAtivo() {
		return ativo;
	}

	public void setAtivo(SimNao ativo) {
		this.ativo = ativo;
	}
}
