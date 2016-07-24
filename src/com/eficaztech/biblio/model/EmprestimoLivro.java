package com.eficaztech.biblio.model;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.joda.time.DateTime;
import org.joda.time.Days;

@Entity
@Table(name = "EmprestimoLivro")
public class EmprestimoLivro implements Emprestimo {

	@Id
	@GeneratedValue
	@Column(name = "emprestimoLivroId")
	private Long id;

	@NotNull(message = "O usuário da biblioteca deve ser informado.")
	@ManyToOne
	@JoinColumn(name = "clienteId")
	private Cliente cliente;

	@NotNull(message = "O exemplar deve ser informado.")
	@ManyToOne
	@JoinColumn(name = "exemplarLivroId")
	private ExemplarLivro exemplar;

	@Column(name = "dataEmprestimo")
	private Date dataEmprestimo;

	@Column(name = "dataPrevisaoDevolucao")
	private Date dataPrevisaoDevolucao;

	@Column(name = "dataDevolucao")
	private Date dataDevolucao;
	
	@Column(name = "multa", precision=10, scale=2)
	private BigDecimal multa;

	public String getDataEmprestimoBR() {
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		return format.format(dataEmprestimo);
	}

	public String getDataPrevisaoDevolucaoBR() {
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		return format.format(dataPrevisaoDevolucao);
	}

	public String getDataDevolucaoBR() {
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		if (dataDevolucao == null) {
			return "";
		}
		return format.format(dataDevolucao);
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
		EmprestimoLivro other = (EmprestimoLivro) obj;
		if (getId() == null) {
			if (other.getId() != null)
				return false;
		} else if (!getId().equals(other.getId()))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return cliente.getNome() + " - " + exemplar.getLivro().getTitulo();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	public ExemplarLivro getExemplar() {
		return exemplar;
	}

	public void setExemplar(ExemplarLivro exemplar) {
		this.exemplar = exemplar;
	}

	public Date getDataEmprestimo() {
		return dataEmprestimo;
	}

	public void setDataEmprestimo(Date dataEmprestimo) {
		this.dataEmprestimo = dataEmprestimo;
	}

	public Date getDataPrevisaoDevolucao() {
		return dataPrevisaoDevolucao;
	}

	public void setDataPrevisaoDevolucao(Date dataPrevisaoDevolucao) {
		this.dataPrevisaoDevolucao = dataPrevisaoDevolucao;
	}

	public Date getDataDevolucao() {
		return dataDevolucao;
	}

	public void setDataDevolucao(Date dataDevolucao) {
		this.dataDevolucao = dataDevolucao;
	}

	public BigDecimal getMulta() {
		
		// a multa eh calculada se a data de devolucao ainda nao estiver preenchida
		if (dataDevolucao == null) {
			
			Empresa empresa = EmpresaDao.findFirst();
			BigDecimal valor = empresa.getValorMulta();
			
			DateTime hoje = new DateTime();
			DateTime previsao = new DateTime(dataPrevisaoDevolucao);
			int dias = Days.daysBetween(previsao, hoje).getDays();
			
			if (dias < 1) {
				return new BigDecimal(0);
			}
			
			return valor.multiply(new BigDecimal(dias));
			
		}
		return multa;
	}
	
	public void setMulta(BigDecimal multa) {
		this.multa = multa;
	}
	
	public String getMultaBR() {
		if (getMulta() == null) {
			return "";
		}
		DecimalFormat decFormat = new DecimalFormat("#,###,##0.00");
		return decFormat.format(getMulta());
	}
	
	public String getTipo() {
		return "Livro";
	}

}
