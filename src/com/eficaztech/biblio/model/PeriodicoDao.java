package com.eficaztech.biblio.model;

import java.util.List;

import com.avaje.ebean.Query;
import com.eficaztech.biblio.util.BiblioServer;
import com.eficaztech.biblio.util.ValidationException;
import com.eficaztech.biblio.util.Validations;

public class PeriodicoDao {

	public static void save(Periodico periodico) throws ValidationException {
		Validations.validate(periodico);
		if (periodico.getId() == null) {
			BiblioServer.ebean.save(periodico);
		} else {
			BiblioServer.ebean.update(periodico);
		}
	}

	public static Periodico find(Long id) {
		return BiblioServer.ebean.find(Periodico.class, id);
	}

	public static void delete(Periodico periodico) {
		BiblioServer.ebean.delete(periodico);
	}

	public static List<Periodico> all() {
		Query<Periodico> query = BiblioServer.ebean.createQuery(Periodico.class).orderBy("titulo");
		return query.findList();
	}

	public static List<Periodico> find(String filtro) {
		String sql = "find periodico where titulo like :filtro order by titulo";
		Query<Periodico> query = BiblioServer.ebean.createQuery(Periodico.class, sql).setParameter("filtro", "%" + filtro + "%");
		return query.findList();
	}

}
