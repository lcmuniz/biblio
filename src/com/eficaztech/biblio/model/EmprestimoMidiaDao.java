package com.eficaztech.biblio.model;

import java.math.BigDecimal;
import java.util.List;

import org.joda.time.DateTime;

import com.avaje.ebean.Query;
import com.eficaztech.biblio.enums.SimNao;
import com.eficaztech.biblio.enums.TipoCliente;
import com.eficaztech.biblio.exceptions.EmprestimoMidiaException;
import com.eficaztech.biblio.util.BiblioServer;
import com.eficaztech.biblio.util.ValidationException;
import com.eficaztech.biblio.util.Validations;

public class EmprestimoMidiaDao {

	public static void save(EmprestimoMidia obj) throws ValidationException {
		Validations.validate(obj);
		if (obj.getId() == null) {
			BiblioServer.ebean.save(obj);
		} else {
			BiblioServer.ebean.update(obj);
		}
	}

	public static EmprestimoMidia find(Long id) {
		return BiblioServer.ebean.find(EmprestimoMidia.class, id);
	}

	public static void delete(EmprestimoMidia obj) {
		BiblioServer.ebean.delete(obj);
	}

	public static List<EmprestimoMidia> all() {
		Query<EmprestimoMidia> query = BiblioServer.ebean
				.createQuery(EmprestimoMidia.class).where().le("id", 1000)
				.orderBy("dataEmprestimo");
		return query.findList();
	}

	public static List<EmprestimoMidia> findByCliente(long clienteId,
			boolean mostrarDevolvidos) {
		if (mostrarDevolvidos) {
			Query<EmprestimoMidia> query = BiblioServer.ebean
					.createQuery(EmprestimoMidia.class).where()
					.eq("cliente.id", clienteId).orderBy("dataEmprestimo");
			return query.findList();
		} else {
			Query<EmprestimoMidia> query = BiblioServer.ebean
					.createQuery(EmprestimoMidia.class).where()
					.eq("cliente.id", clienteId).isNull("dataDevolucao")
					.orderBy("dataEmprestimo");
			return query.findList();
		}

	}

	public static void emprestar(long clienteId, long exemplarId,
			boolean consultaLocal) throws EmprestimoMidiaException {

		// pega o cliente
		Cliente cliente = ClienteDao.find(clienteId);

		// verifica se esta habilitado
		if (cliente.getAtivo() == SimNao.N) {
			throw new EmprestimoMidiaException("Cliente não está ativo.");
		}

		// verifica se cliente ja tem o numero limite de mídias
		if (cliente.atingiuLimiteMidias()) {
			throw new EmprestimoMidiaException(
					"Cliente já atingiu o limite de mídias que pode emprestar.");
		}

		// pega o exemplar
		ExemplarMidia exemplar = ExemplarMidiaDao.find(exemplarId);

		if (exemplar == null) {
			throw new EmprestimoMidiaException("Este exemplar não existe.");
		}
		
		if (exemplar.getMidia().getAtivo() == SimNao.N) {
			throw new EmprestimoMidiaException("Este exemplar não está ativo.");
		}

		// verifica se o exemplar já está emprestado
		if (!(exemplar.getCliente() == null || exemplar.getCliente().getId()
				.equals(0L))) {
			throw new EmprestimoMidiaException(
					"O exemplar já está emprestado para '"
							+ exemplar.getCliente().getNome() + "'.");
		}

		int numeroDias = Empresa.DIAS_EMPRESTIMO;
		if (consultaLocal) {
			numeroDias = 0;
		} else if (cliente.getTipoCliente() == TipoCliente.ALUNO) {
			// pega o numero de dias para emprestar
			Empresa empresa = EmpresaDao.findFirst();
			numeroDias = empresa.getDiasMidiasEmprestimo();
		}

		DateTime dataEmprestimo = new DateTime();

		EmprestimoMidia em = new EmprestimoMidia();
		em.setCliente(cliente);
		em.setExemplar(exemplar);
		em.setDataEmprestimo(dataEmprestimo.toDate());

		DateTime dataDevolucao = dataEmprestimo.plusDays(numeroDias);
		em.setDataPrevisaoDevolucao(dataDevolucao.toDate());

		// empresta a midia
		try {
			EmprestimoMidiaDao.save(em);
		} catch (ValidationException e) {
			throw new EmprestimoMidiaException("Erro ao salvar empréstimo");
		}

		// seta o cliente no exemplar e salva
		exemplar.setCliente(cliente);
		try {
			ExemplarMidiaDao.save(exemplar);
		} catch (ValidationException e) {
			throw new EmprestimoMidiaException("Erro ao atualizar exemplar");
		}

	}

	public static void devolver(long id) throws EmprestimoMidiaException {

		DateTime hoje = new DateTime();

		EmprestimoMidia emp = find(id);
		long exemplarId = emp.getExemplar().getId();
		BigDecimal multa = emp.getMulta();

		// devolve a midia
		emp.setDataDevolucao(hoje.toDate());
		emp.setMulta(multa);
		try {
			EmprestimoMidiaDao.save(emp);
		} catch (ValidationException e) {
			throw new EmprestimoMidiaException("Erro ao salvar empréstimo");
		}

		// seta o cliente no exemplar para nulo
		ExemplarMidia exemplar = ExemplarMidiaDao.find(exemplarId);
		exemplar.setCliente(null);

		try {
			ExemplarMidiaDao.save(exemplar);
		} catch (ValidationException e) {
			throw new EmprestimoMidiaException("Erro ao atualizar exemplar");
		}

	}

	public static void renovar(long id) throws EmprestimoMidiaException {

		EmprestimoMidia el = find(id);

		long clienteId = el.getCliente().getId();
		long exemplarId = el.getExemplar().getId();

		devolver(id);
		emprestar(clienteId, exemplarId, false);

	}
}
