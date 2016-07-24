package com.eficaztech.biblio.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.eficaztech.biblio.enums.SimNao;
import com.eficaztech.biblio.enums.TipoMidia;

@Entity
@Table(name = "Midia")
public class Midia {

	@Id
	@GeneratedValue
	@Column(name = "midiaId")
	private Long id;

	@NotEmpty(message = "O título deve ser informado.")
	@Column(length = 200, nullable = false)
	private String titulo;

	@Column(length = 200)
	private String subtitulo;

	@Column(length = 500)
	private String descricao;

	@NotNull(message = "O tipo de mídia deve ser informado.")
	@Enumerated(EnumType.STRING)
	private TipoMidia tipoMidia;

	@NotNull(message = "Deve ser informado se a mídia está ativa ou não.")
	@Enumerated(EnumType.STRING)
	private SimNao ativo;

	@OneToMany(mappedBy = "midia", cascade = CascadeType.ALL)
	private List<ExemplarMidia> exemplares;

	@AssertTrue(message = "A mídia deve ter pelo menos um exemplar.")
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
		Midia other = (Midia) obj;
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

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public TipoMidia getTipoMidia() {
		return tipoMidia;
	}

	public void setTipoMidia(TipoMidia tipoMidia) {
		this.tipoMidia = tipoMidia;
	}

	public List<ExemplarMidia> getExemplares() {
		return exemplares;
	}

	public void setExemplares(List<ExemplarMidia> exemplares) {
		this.exemplares = exemplares;
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
		for (ExemplarMidia exemplar : this.exemplares) {
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
