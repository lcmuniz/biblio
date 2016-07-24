package com.eficaztech.biblio.model;

import java.util.Date;
import java.util.List;

import com.avaje.ebean.Query;
import com.eficaztech.biblio.util.BiblioServer;
import com.eficaztech.biblio.util.ValidationException;
import com.eficaztech.biblio.util.Validations;

public class ExemplarEdicaoDao {

	public static void save(ExemplarEdicao obj) throws ValidationException {
		Validations.validate(obj);
		if (obj.getId() == null) {
			BiblioServer.ebean.save(obj);
		} else {
			BiblioServer.ebean.update(obj);
		}
	}

	public static ExemplarEdicao find(Long id) {
		return BiblioServer.ebean.find(ExemplarEdicao.class, id);
	}

	public static List<ExemplarEdicao> findBetween(Integer inicio, Integer fim) {
		String sql = "find ExemplarEdicao where id between :inicio and :fim order by id";
		Query<ExemplarEdicao> query = BiblioServer.ebean
				.createQuery(ExemplarEdicao.class, sql)
				.setParameter("inicio", inicio).setParameter("fim", fim);
		return query.findList();

	}

	public static List<ExemplarEdicao> findBetween(Date inicio, Date fim) {
		String sql = "find ExemplarEdicao where dataCadastro between :inicio and :fim order by dataCadastro";
		Query<ExemplarEdicao> query = BiblioServer.ebean
				.createQuery(ExemplarEdicao.class, sql)
				.setParameter("inicio", inicio).setParameter("fim", fim);
		return query.findList();
	}

}
