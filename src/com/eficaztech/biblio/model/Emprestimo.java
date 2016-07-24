package com.eficaztech.biblio.model;

import java.math.BigDecimal;
import java.util.Date;

public interface Emprestimo {

	public Long getId();

	public Cliente getCliente();
	
	public Exemplar getExemplar();

	public String getTipo();

	public Date getDataEmprestimo();

	public String getDataEmprestimoBR();

	public Date getDataPrevisaoDevolucao();

	public String getDataPrevisaoDevolucaoBR();

	public BigDecimal getMulta();
	
	public String getMultaBR();

}