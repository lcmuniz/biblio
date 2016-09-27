package com.eficaztech.biblio.model;

import java.util.List;

import com.avaje.ebean.Query;
import com.eficaztech.biblio.util.BiblioServer;
import com.eficaztech.biblio.util.ValidationException;
import com.eficaztech.biblio.util.Validations;

public class ClienteDao {

	public static void save(Cliente cliente) throws ValidationException {
		Validations.validate(cliente);
		if (cliente.getId() == null) {
			BiblioServer.ebean.save(cliente);
		} else {
			BiblioServer.ebean.update(cliente);
		}
	}

	public static Cliente find(Long id) {
		return BiblioServer.ebean.find(Cliente.class, id);
	}

	public static void delete(Cliente cliente) {
		BiblioServer.ebean.delete(cliente);
	}

	public static List<Cliente> all() {
		Query<Cliente> query = BiblioServer.ebean.createQuery(Cliente.class)
				.where().eq("ativo", "S").orderBy("nome");
		return query.findList();
	}

	public static List<Cliente> find(String filtro) {
		String sql = "find cliente where (ativo = 'S') and (codigo like :filtro or nome like :filtro) order by nome";
		Query<Cliente> query = BiblioServer.ebean.createQuery(Cliente.class,
				sql).setParameter("filtro", "%" + filtro + "%");
		return query.findList();
	}
	
	public static Cliente findByCodigo(Long codigo) {
		String sql = "find cliente where (ativo = 'S') and (codigo = :codigo)";
		Query<Cliente> query = BiblioServer.ebean.createQuery(Cliente.class,
				sql).setParameter("codigo", codigo);
		return query.findUnique();
	}

}
