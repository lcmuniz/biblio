package com.eficaztech.biblio.model;

import java.util.List;

import com.avaje.ebean.Query;
import com.eficaztech.biblio.util.BiblioServer;
import com.eficaztech.biblio.util.ValidationException;
import com.eficaztech.biblio.util.Validations;

public class EdicaoDao {

	public static void save(Edicao edicao) throws ValidationException {
		Validations.validate(edicao);
		if (edicao.getId() == null) {
			BiblioServer.ebean.save(edicao);
		} else {
			BiblioServer.ebean.update(edicao);
		}
	}

	public static Edicao find(Long id) {
		return BiblioServer.ebean.find(Edicao.class, id);
	}

	public static void delete(Edicao edicao) {
		BiblioServer.ebean.delete(edicao);
	}

	public static List<Edicao> all() {
		Query<Edicao> query = BiblioServer.ebean.createQuery(Edicao.class)
				.orderBy("periodico.titulo, titulo");
		return query.findList();
	}

	public static List<Edicao> find(Periodico periodico, String titulo) {

		String while1 = "(1=1)";
		if (periodico != null) {
			while1 = "(periodico.id = " + periodico.getId() + ")";
		}

		String sql = "find edicao where " + while1
				+ " and (titulo like :filtro) order by id";
		Query<Edicao> query = BiblioServer.ebean.createQuery(Edicao.class, sql)
				.setParameter("filtro", "%" + titulo.trim() + "%");
		return query.findList();

	}

}
