package com.eficaztech.biblio.model;

import java.util.List;

import com.avaje.ebean.Query;
import com.eficaztech.biblio.util.BiblioServer;
import com.eficaztech.biblio.util.ValidationException;
import com.eficaztech.biblio.util.Validations;

public class IdiomaDao {

	public static void save(Idioma idioma) throws ValidationException {
		Validations.validate(idioma);
		if (idioma.getId() == null) {
			BiblioServer.ebean.save(idioma);
		} else {
			BiblioServer.ebean.update(idioma);
		}
	}

	public static void delete(Idioma idioma) {
		BiblioServer.ebean.delete(idioma);
	}

	public static Idioma find(Long id) {
		return BiblioServer.ebean.find(Idioma.class, id);
	}

	public static List<Idioma> all() {
		Query<Idioma> query = BiblioServer.ebean.createQuery(Idioma.class).orderBy("nome");
		return query.findList();
	}

	public static List<Idioma> find(String filtro) {
		String sql = "find idioma where nome like :filtro order by nome";
		Query<Idioma> query = BiblioServer.ebean.createQuery(Idioma.class, sql).setParameter("filtro", "%" + filtro + "%");
		return query.findList();
	}

}
