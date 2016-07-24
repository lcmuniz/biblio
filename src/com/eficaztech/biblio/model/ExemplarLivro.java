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
@Table(name = "ExemplarLivro")
public class ExemplarLivro implements Exemplar {

	@Transient
	private long uid;

	@Id
	@GeneratedValue
	@Column(name = "exemplarLivroId")
	private Long id;

	@NotNull(message = "O livro deve ser informado.")
	@ManyToOne
	@JoinColumn(name = "livroId")
	private Livro livro;

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
		ExemplarLivro other = (ExemplarLivro) obj;
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
		return livro.getTitulo();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Livro getLivro() {
		return livro;
	}

	public void setLivro(Livro livro) {
		this.livro = livro;
	}

	public Livro getObra() {
		return livro;
	}

	public void setObra(Livro livro) {
		this.livro = livro;
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
