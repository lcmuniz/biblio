package com.eficaztech.biblio.model;

import java.util.List;

import com.avaje.ebean.Query;
import com.eficaztech.biblio.util.BiblioServer;
import com.eficaztech.biblio.util.ValidationException;
import com.eficaztech.biblio.util.Validations;

public class LocalDao {

	public static void save(Local local) throws ValidationException {
		Validations.validate(local);
		if (local.getId() == null) {
			BiblioServer.ebean.save(local);
		} else {
			BiblioServer.ebean.update(local);
		}
	}

	public static void delete(Local local) {
		BiblioServer.ebean.delete(local);
	}

	public static Local find(Long id) {
		return BiblioServer.ebean.find(Local.class, id);
	}

	public static List<Local> all() {
		Query<Local> query = BiblioServer.ebean.createQuery(Local.class).orderBy("nome");
		return query.findList();
	}

	public static List<Local> find(String filtro) {
		String sql = "find local where nome like :filtro order by nome";
		Query<Local> query = BiblioServer.ebean.createQuery(Local.class, sql).setParameter("filtro", "%" + filtro + "%");
		return query.findList();
	}

}
