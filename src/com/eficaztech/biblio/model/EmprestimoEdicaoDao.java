package com.eficaztech.biblio.model;

import java.math.BigDecimal;
import java.util.List;

import org.joda.time.DateTime;

import com.avaje.ebean.Query;
import com.eficaztech.biblio.enums.SimNao;
import com.eficaztech.biblio.enums.TipoCliente;
import com.eficaztech.biblio.exceptions.EmprestimoEdicaoException;
import com.eficaztech.biblio.util.BiblioServer;
import com.eficaztech.biblio.util.ValidationException;
import com.eficaztech.biblio.util.Validations;

public class EmprestimoEdicaoDao {

	public static void save(EmprestimoEdicao obj) throws ValidationException {
		Validations.validate(obj);
		if (obj.getId() == null) {
			BiblioServer.ebean.save(obj);
		} else {
			BiblioServer.ebean.update(obj);
		}
	}

	public static EmprestimoEdicao find(Long id) {
		return BiblioServer.ebean.find(EmprestimoEdicao.class, id);
	}

	public static void delete(EmprestimoEdicao obj) {
		BiblioServer.ebean.delete(obj);
	}

	public static List<EmprestimoEdicao> all() {
		Query<EmprestimoEdicao> query = BiblioServer.ebean
				.createQuery(EmprestimoEdicao.class).where().le("id", 1000)
				.orderBy("dataEmprestimo");
		return query.findList();
	}

	public static List<EmprestimoEdicao> findByCliente(long clienteId,
			boolean mostrarDevolvidos) {
		if (mostrarDevolvidos) {
			Query<EmprestimoEdicao> query = BiblioServer.ebean
					.createQuery(EmprestimoEdicao.class).where()
					.eq("cliente.id", clienteId).orderBy("dataEmprestimo");
			return query.findList();
		} else {
			Query<EmprestimoEdicao> query = BiblioServer.ebean
					.createQuery(EmprestimoEdicao.class).where()
					.eq("cliente.id", clienteId).isNull("dataDevolucao")
					.orderBy("dataEmprestimo");
			return query.findList();
		}

	}

	public static void emprestar(long clienteId, long exemplarId,
			boolean consultaLocal) throws EmprestimoEdicaoException {

		// pega o cliente
		Cliente cliente = ClienteDao.find(clienteId);

		// verifica se esta habilitado
		if (cliente.getAtivo() == SimNao.N) {
			throw new EmprestimoEdicaoException("Cliente não está ativo.");
		}

		// verifica se cliente ja tem o numero limite de edições
		if (cliente.atingiuLimiteEdicoes()) {
			throw new EmprestimoEdicaoException(
					"Cliente já atingiu o limite de edições que pode emprestar.");
		}

		// pega o exemplar
		ExemplarEdicao exemplar = ExemplarEdicaoDao.find(exemplarId);

		if (exemplar == null) {
			throw new EmprestimoEdicaoException("Este exemplar não existe.");
		}

		if (exemplar.getEdicao().getAtivo() == SimNao.N) {
			throw new EmprestimoEdicaoException("Este exemplar não está ativo.");
		}

		// verifica se o exemplar já está emprestado
		if (!(exemplar.getCliente() == null || exemplar.getCliente().getId()
				.equals(0L))) {
			throw new EmprestimoEdicaoException(
					"O exemplar já está emprestado para '"
							+ exemplar.getCliente().getNome() + "'.");
		}

		int numeroDias = Empresa.DIAS_EMPRESTIMO;
		if (consultaLocal) {
			numeroDias = 0;
		} else if (cliente.getTipoCliente() == TipoCliente.ALUNO) {
			// pega o numero de dias para emprestar
			Empresa empresa = EmpresaDao.findFirst();
			numeroDias = empresa.getDiasPeriodicosEmprestimo();
		}

		DateTime dataEmprestimo = new DateTime();

		EmprestimoEdicao el = new EmprestimoEdicao();
		el.setCliente(cliente);
		el.setExemplar(exemplar);
		el.setDataEmprestimo(dataEmprestimo.toDate());

		DateTime dataDevolucao = dataEmprestimo.plusDays(numeroDias);
		el.setDataPrevisaoDevolucao(dataDevolucao.toDate());

		// empresta o edição
		try {
			EmprestimoEdicaoDao.save(el);
		} catch (ValidationException e) {
			throw new EmprestimoEdicaoException("Erro ao salvar empréstimo");
		}

		// seta o cliente no exemplar e salva
		exemplar.setCliente(cliente);
		try {
			ExemplarEdicaoDao.save(exemplar);
		} catch (ValidationException e) {
			throw new EmprestimoEdicaoException("Erro ao atualizar exemplar");
		}

	}

	public static void devolver(long id) throws EmprestimoEdicaoException {

		DateTime hoje = new DateTime();

		EmprestimoEdicao emp = find(id);
		long exemplarId = emp.getExemplar().getId();
		BigDecimal multa = emp.getMulta();

		// devolve a edicao
		emp.setDataDevolucao(hoje.toDate());
		emp.setMulta(multa);
		try {
			EmprestimoEdicaoDao.save(emp);
		} catch (ValidationException e) {
			throw new EmprestimoEdicaoException("Erro ao salvar empréstimo");
		}

		// seta o cliente no exemplar para nulo
		ExemplarEdicao exemplar = ExemplarEdicaoDao.find(exemplarId);
		exemplar.setCliente(null);

		try {
			ExemplarEdicaoDao.save(exemplar);
		} catch (ValidationException e) {
			throw new EmprestimoEdicaoException("Erro ao atualizar exemplar");
		}

	}

	public static void renovar(long id) throws EmprestimoEdicaoException {

		EmprestimoEdicao el = find(id);

		long clienteId = el.getCliente().getId();
		long exemplarId = el.getExemplar().getId();

		devolver(id);
		emprestar(clienteId, exemplarId, false);

	}
}
