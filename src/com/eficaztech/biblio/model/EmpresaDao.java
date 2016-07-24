package com.eficaztech.biblio.model;

import com.avaje.ebean.Query;
import com.eficaztech.biblio.util.BiblioServer;
import com.eficaztech.biblio.util.ValidationException;
import com.eficaztech.biblio.util.Validations;

public class EmpresaDao {

	public static void save(Empresa empresa) throws ValidationException {
		Validations.validate(empresa);
		if (empresa.getId() == null) {
			BiblioServer.ebean.save(empresa);
		} else {
			BiblioServer.ebean.update(empresa);
		}
	}

	public static Empresa find(Long id) {
		return BiblioServer.ebean.find(Empresa.class, id);
	}

	public static Empresa findFirst() {
		String sql = "find empresa order by id";
		Query<Empresa> query = BiblioServer.ebean.createQuery(Empresa.class, sql);
		return query.findList().get(0);
	}

}
