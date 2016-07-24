package com.eficaztech.biblio.model;

import java.util.List;

import com.avaje.ebean.Query;
import com.eficaztech.biblio.util.BiblioServer;
import com.eficaztech.biblio.util.ValidationException;
import com.eficaztech.biblio.util.Validations;

public class EditoraDao {

	public static void save(Editora editora) throws ValidationException {
		Validations.validate(editora);
		if (editora.getId() == null) {
			BiblioServer.ebean.save(editora);
		} else {
			BiblioServer.ebean.update(editora);
		}
	}

	public static void delete(Editora editora) {
		BiblioServer.ebean.delete(editora);
	}

	public static Editora find(Long id) {
		return BiblioServer.ebean.find(Editora.class, id);
	}

	public static List<Editora> all() {
		Query<Editora> query = BiblioServer.ebean.createQuery(Editora.class).orderBy("nome");
		return query.findList();
	}

	public static List<Editora> find(String filtro) {
		String sql = "find editora where nome like :filtro order by nome";
		Query<Editora> query = BiblioServer.ebean.createQuery(Editora.class, sql).setParameter("filtro", "%" + filtro + "%");
		return query.findList();
	}

}
