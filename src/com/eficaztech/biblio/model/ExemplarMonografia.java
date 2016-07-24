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
@Table(name = "ExemplarMonografia")
public class ExemplarMonografia implements Exemplar {

	@Transient
	private long uid;

	@Id
	@GeneratedValue
	@Column(name = "exemplarMonografiaId")
	private Long id;

	@NotNull(message = "A monografia deve ser informada.")
	@ManyToOne
	@JoinColumn(name = "monografiaId")
	private Monografia monografia;

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
		ExemplarMonografia other = (ExemplarMonografia) obj;
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
		return monografia.getTitulo();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Monografia getMonografia() {
		return monografia;
	}

	public void setMonografia(Monografia monografia) {
		this.monografia = monografia;
	}

	public Monografia getObra() {
		return monografia;
	}

	public void setObra(Monografia monografia) {
		this.monografia = monografia;
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
