package com.eficaztech.biblio.model;

import java.util.List;

import com.avaje.ebean.Query;
import com.eficaztech.biblio.util.BiblioServer;
import com.eficaztech.biblio.util.ValidationException;
import com.eficaztech.biblio.util.Validations;

public class CursoDao {

	public static void save(Curso curso) throws ValidationException {
		Validations.validate(curso);
		if (curso.getId() == null) {
			BiblioServer.ebean.save(curso);
		} else {
			BiblioServer.ebean.update(curso);
		}
	}

	public static void delete(Curso curso) {
		BiblioServer.ebean.delete(curso);
	}

	public static Curso find(Long id) {
		return BiblioServer.ebean.find(Curso.class, id);
	}

	public static List<Curso> all() {
		Query<Curso> query = BiblioServer.ebean.createQuery(Curso.class).orderBy("nome");
		return query.findList();
	}

	public static List<Curso> find(String filtro) {
		String sql = "find curso where nome like :filtro order by nome";
		Query<Curso> query = BiblioServer.ebean.createQuery(Curso.class, sql).setParameter("filtro", "%" + filtro + "%");
		return query.findList();
	}

}
