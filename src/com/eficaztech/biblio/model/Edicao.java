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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.eficaztech.biblio.enums.SimNao;
import com.eficaztech.biblio.enums.TipoAquisicao;

@Entity
@Table(name = "Edicao")
public class Edicao {

	@Id
	@GeneratedValue
	@Column(name = "edicaoId")
	private Long id;

	@NotNull(message = "O periódico deve ser informado.")
	@ManyToOne
	@JoinColumn(name = "periodicoId")
	private Periodico periodico;

	@NotEmpty(message = "O título deve ser informado.")
	@Column(length = 200, nullable = false)
	private String titulo;

	@Column(length = 20)
	private String volume;

	@Column(length = 20)
	private String numero;

	@Column(length = 50)
	private String periodo;

	@NotNull(message = "O tipo de aquisição deve ser informado.")
	@Enumerated(EnumType.STRING)
	private TipoAquisicao tipoAquisicao;

	@NotNull(message = "Deve ser informado se a edição está ativa ou não.")
	@Enumerated(EnumType.STRING)
	private SimNao ativo;

	@OneToMany(mappedBy = "edicao", cascade = CascadeType.ALL)
	private List<ExemplarEdicao> exemplares;

	@OneToMany(mappedBy = "edicao", cascade = CascadeType.ALL)
	@OrderBy("titulo")
	private List<ArtigoEdicao> artigos;

	public List<ExemplarEdicao> getExemplares() {
		return exemplares;
	}

	public void setExemplares(List<ExemplarEdicao> exemplares) {
		this.exemplares = exemplares;
	}

	@AssertTrue(message = "A edição deve ter pelo menos um exemplar.")
	public boolean isDeveTerExemplares() {
		if (exemplares.size() == 0) {
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
		Edicao other = (Edicao) obj;
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

	public Periodico getPeriodico() {
		return periodico;
	}

	public void setPeriodico(Periodico periodico) {
		this.periodico = periodico;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getTituloCompleto() {
		String titPeriodico = "";
		if (periodico.getSubtitulo() == null
				|| periodico.getSubtitulo().equals("")) {
			titPeriodico = periodico.getTitulo();
		} else {
			titPeriodico = periodico.getTitulo() + " ("
					+ periodico.getSubtitulo() + ")";
		}
		return titPeriodico + ": " + titulo;
	}

	public String getTituloCompletoUpperCase() {
		String titPeriodico = "";
		if (periodico.getSubtitulo() == null
				|| periodico.getSubtitulo().equals("")) {
			titPeriodico = periodico.getTitulo().toUpperCase();
		} else {
			titPeriodico = periodico.getTitulo().toUpperCase() + " ("
					+ periodico.getSubtitulo().toUpperCase() + ")";
		}
		return titPeriodico + ": " + titulo.toUpperCase();
	}

	public String getVolume() {
		return volume;
	}

	public void setVolume(String volume) {
		this.volume = volume;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public String getPeriodo() {
		return periodo;
	}

	public void setPeriodo(String periodo) {
		this.periodo = periodo;
	}

	public TipoAquisicao getTipoAquisicao() {
		return tipoAquisicao;
	}

	public void setTipoAquisicao(TipoAquisicao tipoAquisicao) {
		this.tipoAquisicao = tipoAquisicao;
	}

	public List<ArtigoEdicao> getArtigos() {
		return artigos;
	}

	public void setArtigos(List<ArtigoEdicao> artigos) {
		this.artigos = artigos;
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

		String disp = "";
		int quant = 0;
		for (ExemplarEdicao exemplar : this.exemplares) {
			if (exemplar.getCliente() == null) {
				disp = disp + exemplar.getId() + ", ";
				quant++;
			}
		}
		disp = disp + "#";
		disp = disp.replace(", #", "");

		if (quant == 0) {
			ret2 = "Nenhum disponível";
		} else if (quant == 1) {
			ret2 = "1 disponível : " + disp;
		} else {
			ret2 = quant + " disponíveis: " + disp;
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
