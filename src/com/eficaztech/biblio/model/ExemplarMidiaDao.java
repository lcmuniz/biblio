package com.eficaztech.biblio.model;

import java.util.Date;
import java.util.List;

import com.avaje.ebean.Query;
import com.eficaztech.biblio.util.BiblioServer;
import com.eficaztech.biblio.util.ValidationException;
import com.eficaztech.biblio.util.Validations;

public class ExemplarMidiaDao {

	public static void save(ExemplarMidia obj) throws ValidationException {
		Validations.validate(obj);
		if (obj.getId() == null) {
			BiblioServer.ebean.save(obj);
		} else {
			BiblioServer.ebean.update(obj);
		}
	}

	public static ExemplarMidia find(Long id) {
		return BiblioServer.ebean.find(ExemplarMidia.class, id);
	}

	public static List<ExemplarMidia> findBetween(Integer inicio, Integer fim) {
		String sql = "find ExemplarMidia where id between :inicio and :fim order by id";
		Query<ExemplarMidia> query = BiblioServer.ebean
				.createQuery(ExemplarMidia.class, sql)
				.setParameter("inicio", inicio).setParameter("fim", fim);
		return query.findList();

	}

	public static List<ExemplarMidia> findBetween(Date inicio, Date fim) {
		String sql = "find ExemplarMidia where dataCadastro between :inicio and :fim order by dataCadastro";
		Query<ExemplarMidia> query = BiblioServer.ebean
				.createQuery(ExemplarMidia.class, sql)
				.setParameter("inicio", inicio).setParameter("fim", fim);
		return query.findList();
	}
}
