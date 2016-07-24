package com.eficaztech.biblio.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "ExemplarMidia")
public class ExemplarMidia implements Exemplar {

	@Transient
	private long uid;

	@Id
	@GeneratedValue
	@Column(name = "exemplarMidiaId")
	private Long id;

	@NotNull(message = "A mídia deve ser informada.")
	@ManyToOne
	@JoinColumn(name = "midiaId")
	private Midia midia;

	@ManyToOne
	@JoinColumn(name = "clienteId")
	private Cliente cliente;

	@Column(name = "dataDeCadastro")
	private Date dataCadastro;

	@PrePersist
	public void prePersist() {
		if (dataCadastro == null)
			dataCadastro = new Date();
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
		ExemplarMidia other = (ExemplarMidia) obj;
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
		return midia.getTitulo();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Midia getMidia() {
		return midia;
	}

	public void setMidia(Midia midia) {
		this.midia = midia;
	}

	public Midia getObra() {
		return midia;
	}

	public void setObra(Midia midia) {
		this.midia = midia;
	}

	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
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

	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public String getDataCadastroBR() {
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		return format.format(dataCadastro);
	}
}
