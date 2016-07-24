package com.eficaztech.biblio.model;

import java.math.BigDecimal;
import java.util.List;

import org.joda.time.DateTime;

import com.avaje.ebean.Query;
import com.eficaztech.biblio.enums.SimNao;
import com.eficaztech.biblio.enums.TipoCliente;
import com.eficaztech.biblio.exceptions.EmprestimoMonografiaException;
import com.eficaztech.biblio.util.BiblioServer;
import com.eficaztech.biblio.util.ValidationException;
import com.eficaztech.biblio.util.Validations;

public class EmprestimoMonografiaDao {

	public static void save(EmprestimoMonografia obj)
			throws ValidationException {
		Validations.validate(obj);
		if (obj.getId() == null) {
			BiblioServer.ebean.save(obj);
		} else {
			BiblioServer.ebean.update(obj);
		}
	}

	public static EmprestimoMonografia find(Long id) {
		return BiblioServer.ebean.find(EmprestimoMonografia.class, id);
	}

	public static void delete(EmprestimoMonografia obj) {
		BiblioServer.ebean.delete(obj);
	}

	public static List<EmprestimoMonografia> all() {
		Query<EmprestimoMonografia> query = BiblioServer.ebean
				.createQuery(EmprestimoMonografia.class).where().le("id", 1000)
				.orderBy("dataEmprestimo");
		return query.findList();
	}

	public static List<EmprestimoMonografia> findByCliente(long clienteId,
			boolean mostrarDevolvidos) {
		if (mostrarDevolvidos) {
			Query<EmprestimoMonografia> query = BiblioServer.ebean
					.createQuery(EmprestimoMonografia.class).where()
					.eq("cliente.id", clienteId).orderBy("dataEmprestimo");
			return query.findList();
		} else {
			Query<EmprestimoMonografia> query = BiblioServer.ebean
					.createQuery(EmprestimoMonografia.class).where()
					.eq("cliente.id", clienteId).isNull("dataDevolucao")
					.orderBy("dataEmprestimo");
			return query.findList();
		}

	}

	public static void emprestar(long clienteId, long exemplarId,
			boolean consultaLocal) throws EmprestimoMonografiaException {

		// pega o cliente
		Cliente cliente = ClienteDao.find(clienteId);

		// verifica se esta habilitado
		if (cliente.getAtivo() == SimNao.N) {
			throw new EmprestimoMonografiaException("Cliente não está ativo.");
		}

		// verifica se cliente ja tem o numero limite de monografias
		if (cliente.atingiuLimiteMonografias()) {
			throw new EmprestimoMonografiaException(
					"Cliente já atingiu o limite de monografias que pode emprestar.");
		}

		// pega o exemplar
		ExemplarMonografia exemplar = ExemplarMonografiaDao.find(exemplarId);

		if (exemplar == null) {
			throw new EmprestimoMonografiaException("Este exemplar não existe.");
		}

		if (exemplar.getMonografia().getAtivo() == SimNao.N) {
			throw new EmprestimoMonografiaException(
					"Este exemplar não está ativo.");
		}

		// verifica se o exemplar já está emprestado
		if (!(exemplar.getCliente() == null || exemplar.getCliente().getId()
				.equals(0L))) {
			throw new EmprestimoMonografiaException(
					"O exemplar já está emprestado para '"
							+ exemplar.getCliente().getNome() + "'.");
		}

		int numeroDias = Empresa.DIAS_EMPRESTIMO;
		if (consultaLocal) {
			numeroDias = 0;
		} else if (cliente.getTipoCliente() == TipoCliente.ALUNO) {
			// pega o numero de dias para emprestar
			Empresa empresa = EmpresaDao.findFirst();
			numeroDias = empresa.getDiasMonografiasEmprestimo();
		}

		DateTime dataEmprestimo = new DateTime();

		EmprestimoMonografia el = new EmprestimoMonografia();
		el.setCliente(cliente);
		el.setExemplar(exemplar);
		el.setDataEmprestimo(dataEmprestimo.toDate());

		DateTime dataDevolucao = dataEmprestimo.plusDays(numeroDias);
		el.setDataPrevisaoDevolucao(dataDevolucao.toDate());

		// empresta o monografia
		try {
			EmprestimoMonografiaDao.save(el);
		} catch (ValidationException e) {
			throw new EmprestimoMonografiaException("Erro ao salvar empréstimo");
		}

		// seta o cliente no exemplar e salva
		exemplar.setCliente(cliente);
		try {
			ExemplarMonografiaDao.save(exemplar);
		} catch (ValidationException e) {
			throw new EmprestimoMonografiaException(
					"Erro ao atualizar exemplar");
		}

	}

	public static void devolver(long id) throws EmprestimoMonografiaException {

		DateTime hoje = new DateTime();

		EmprestimoMonografia emp = find(id);
		long exemplarId = emp.getExemplar().getId();
		BigDecimal multa = emp.getMulta();

		// devolve a monografia
		emp.setDataDevolucao(hoje.toDate());
		emp.setMulta(multa);
		try {
			EmprestimoMonografiaDao.save(emp);
		} catch (ValidationException e) {
			throw new EmprestimoMonografiaException("Erro ao salvar empréstimo");
		}

		// seta o cliente no exemplar para nulo
		ExemplarMonografia exemplar = ExemplarMonografiaDao.find(exemplarId);
		exemplar.setCliente(null);

		try {
			ExemplarMonografiaDao.save(exemplar);
		} catch (ValidationException e) {
			throw new EmprestimoMonografiaException(
					"Erro ao atualizar exemplar");
		}

	}

	public static void renovar(long id) throws EmprestimoMonografiaException {

		EmprestimoMonografia el = find(id);

		long clienteId = el.getCliente().getId();
		long exemplarId = el.getExemplar().getId();
		boolean consultaLocal = el.getExemplar().getMonografia()
				.getConsultaLocal() == SimNao.S ? true : false;

		devolver(id);
		emprestar(clienteId, exemplarId, consultaLocal);

	}

}
