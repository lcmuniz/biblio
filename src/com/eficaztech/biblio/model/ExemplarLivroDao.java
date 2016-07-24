package com.eficaztech.biblio.model;

import java.util.Date;
import java.util.List;

import com.avaje.ebean.Query;
import com.eficaztech.biblio.util.BiblioServer;
import com.eficaztech.biblio.util.ValidationException;
import com.eficaztech.biblio.util.Validations;

public class ExemplarLivroDao {

	public static void save(ExemplarLivro obj) throws ValidationException {
		Validations.validate(obj);
		if (obj.getId() == null) {
			BiblioServer.ebean.save(obj);
		} else {
			BiblioServer.ebean.update(obj);
		}
	}

	public static ExemplarLivro find(Long id) {
		return BiblioServer.ebean.find(ExemplarLivro.class, id);
	}

	public static List<ExemplarLivro> findBetween(Integer inicio, Integer fim) {
		String sql = "find ExemplarLivro where id between :inicio and :fim order by id";
		Query<ExemplarLivro> query = BiblioServer.ebean
				.createQuery(ExemplarLivro.class, sql)
				.setParameter("inicio", inicio).setParameter("fim", fim);
		return query.findList();

	}

	public static List<ExemplarLivro> findBetween(Date inicio, Date fim) {
		String sql = "find ExemplarLivro where dataCadastro between :inicio and :fim order by dataCadastro";
		Query<ExemplarLivro> query = BiblioServer.ebean
				.createQuery(ExemplarLivro.class, sql)
				.setParameter("inicio", inicio).setParameter("fim", fim);
		return query.findList();
	}

}
