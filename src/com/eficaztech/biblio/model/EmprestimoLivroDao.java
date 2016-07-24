package com.eficaztech.biblio.model;

import java.math.BigDecimal;
import java.util.List;

import org.joda.time.DateTime;

import com.avaje.ebean.Query;
import com.eficaztech.biblio.enums.SimNao;
import com.eficaztech.biblio.enums.TipoCliente;
import com.eficaztech.biblio.exceptions.EmprestimoLivroException;
import com.eficaztech.biblio.util.BiblioServer;
import com.eficaztech.biblio.util.ValidationException;
import com.eficaztech.biblio.util.Validations;

public class EmprestimoLivroDao {

	public static void save(EmprestimoLivro emprestimoLivro)
			throws ValidationException {
		Validations.validate(emprestimoLivro);
		if (emprestimoLivro.getId() == null) {
			BiblioServer.ebean.save(emprestimoLivro);
		} else {
			BiblioServer.ebean.update(emprestimoLivro);
		}
	}

	public static EmprestimoLivro find(Long id) {
		return BiblioServer.ebean.find(EmprestimoLivro.class, id);
	}

	public static void delete(EmprestimoLivro emprestimoLivro) {
		BiblioServer.ebean.delete(emprestimoLivro);
	}

	public static List<EmprestimoLivro> all() {
		Query<EmprestimoLivro> query = BiblioServer.ebean
				.createQuery(EmprestimoLivro.class).where().le("id", 1000)
				.orderBy("dataEmprestimo");
		return query.findList();
	}

	public static List<EmprestimoLivro> findByCliente(long clienteId,
			boolean mostrarDevolvidos) {
		if (mostrarDevolvidos) {
			Query<EmprestimoLivro> query = BiblioServer.ebean
					.createQuery(EmprestimoLivro.class).where()
					.eq("cliente.id", clienteId).orderBy("dataEmprestimo");
			return query.findList();
		} else {
			Query<EmprestimoLivro> query = BiblioServer.ebean
					.createQuery(EmprestimoLivro.class).where()
					.eq("cliente.id", clienteId).isNull("dataDevolucao")
					.orderBy("dataEmprestimo");
			return query.findList();
		}

	}

	public static void emprestar(long clienteId, long exemplarId,
			boolean consultaLocal) throws EmprestimoLivroException {

		// pega o cliente
		Cliente cliente = ClienteDao.find(clienteId);

		// verifica se esta habilitado
		if (cliente.getAtivo() == SimNao.N) {
			throw new EmprestimoLivroException("Cliente não está ativo.");
		}

		// verifica se cliente ja tem o numero limite de livros
		if (cliente.atingiuLimiteLivros()) {
			throw new EmprestimoLivroException(
					"Cliente já atingiu o limite de livros que pode emprestar.");
		}

		// pega o exemplar
		ExemplarLivro exemplar = ExemplarLivroDao.find(exemplarId);

		if (exemplar == null) {
			throw new EmprestimoLivroException("Este exemplar não existe.");
		}

		if (exemplar.getLivro().getAtivo() == SimNao.N) {
			throw new EmprestimoLivroException("Este exemplar não está ativo.");
		}

		// verifica se o exemplar já está emprestado
		if (!(exemplar.getCliente() == null || exemplar.getCliente().getId()
				.equals(0L))) {
			throw new EmprestimoLivroException(
					"O exemplar já está emprestado para '"
							+ exemplar.getCliente().getNome() + "'.");
		}

		int numeroDias = Empresa.DIAS_EMPRESTIMO;
		if (consultaLocal) {
			numeroDias = 0;
		} else if (cliente.getTipoCliente() == TipoCliente.ALUNO) {
			// pega o numero de dias para emprestar
			Empresa empresa = EmpresaDao.findFirst();
			numeroDias = empresa.getDiasLivrosEmprestimo();
		}

		DateTime dataEmprestimo = new DateTime();

		EmprestimoLivro el = new EmprestimoLivro();
		el.setCliente(cliente);
		el.setExemplar(exemplar);
		el.setDataEmprestimo(dataEmprestimo.toDate());

		DateTime dataDevolucao = dataEmprestimo.plusDays(numeroDias);
		el.setDataPrevisaoDevolucao(dataDevolucao.toDate());

		// empresta o livro
		try {
			EmprestimoLivroDao.save(el);
		} catch (ValidationException e) {
			throw new EmprestimoLivroException("Erro ao salvar empréstimo");
		}

		// seta o cliente no exemplar e salva
		exemplar.setCliente(cliente);
		try {
			ExemplarLivroDao.save(exemplar);
		} catch (ValidationException e) {
			throw new EmprestimoLivroException("Erro ao atualizar exemplar");
		}

	}

	public static void devolver(long id) throws EmprestimoLivroException {

		DateTime hoje = new DateTime();

		EmprestimoLivro emp = find(id);
		long exemplarId = emp.getExemplar().getId();
		BigDecimal multa = emp.getMulta();

		// devolve o livro
		emp.setDataDevolucao(hoje.toDate());
		emp.setMulta(multa);
		try {
			EmprestimoLivroDao.save(emp);
		} catch (ValidationException e) {
			throw new EmprestimoLivroException("Erro ao salvar empréstimo");
		}

		// seta o cliente no exemplar para nulo
		ExemplarLivro exemplar = ExemplarLivroDao.find(exemplarId);
		exemplar.setCliente(null);

		try {
			ExemplarLivroDao.save(exemplar);
		} catch (ValidationException e) {
			throw new EmprestimoLivroException("Erro ao atualizar exemplar");
		}

	}

	public static void renovar(long id) throws EmprestimoLivroException {

		EmprestimoLivro el = find(id);

		long clienteId = el.getCliente().getId();
		long exemplarId = el.getExemplar().getId();
		boolean consultaLocal = el.getExemplar().getLivro().getConsultaLocal() == SimNao.S ? true
				: false;

		devolver(id);
		emprestar(clienteId, exemplarId, consultaLocal);

	}

}
