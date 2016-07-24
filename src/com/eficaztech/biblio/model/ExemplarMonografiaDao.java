package com.eficaztech.biblio.model;

import java.util.Date;
import java.util.List;

import com.avaje.ebean.Query;
import com.eficaztech.biblio.util.BiblioServer;
import com.eficaztech.biblio.util.ValidationException;
import com.eficaztech.biblio.util.Validations;

public class ExemplarMonografiaDao {

	public static void save(ExemplarMonografia obj) throws ValidationException {
		Validations.validate(obj);
		if (obj.getId() == null) {
			BiblioServer.ebean.save(obj);
		} else {
			BiblioServer.ebean.update(obj);
		}
	}

	public static ExemplarMonografia find(Long id) {
		return BiblioServer.ebean.find(ExemplarMonografia.class, id);
	}

	public static List<ExemplarMonografia> findBetween(Integer inicio,
			Integer fim) {
		String sql = "find ExemplarMonografia where id between :inicio and :fim order by id";
		Query<ExemplarMonografia> query = BiblioServer.ebean
				.createQuery(ExemplarMonografia.class, sql)
				.setParameter("inicio", inicio).setParameter("fim", fim);
		return query.findList();

	}

	public static List<ExemplarMonografia> findBetween(Date inicio, Date fim) {
		String sql = "find ExemplarMonografia where dataCadastro between :inicio and :fim order by dataCadastro";
		Query<ExemplarMonografia> query = BiblioServer.ebean
				.createQuery(ExemplarMonografia.class, sql)
				.setParameter("inicio", inicio).setParameter("fim", fim);
		return query.findList();
	}

}
