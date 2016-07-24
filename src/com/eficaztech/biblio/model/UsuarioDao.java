package com.eficaztech.biblio.model;

import java.util.List;

import com.avaje.ebean.Query;
import com.eficaztech.biblio.util.BiblioServer;
import com.eficaztech.biblio.util.ValidationException;
import com.eficaztech.biblio.util.Validations;

public class UsuarioDao {

	public static void save(Usuario usuario) throws ValidationException {
		Validations.validate(usuario);

		if (usuario.getId() == null) {
			BiblioServer.ebean.save(usuario);
		} else {
			BiblioServer.ebean.update(usuario);
		}
	}

	public static Usuario find(Long id) {
		return BiblioServer.ebean.find(Usuario.class, id);
	}

	public static void delete(Usuario usuario) {
		BiblioServer.ebean.delete(usuario);
	}

	public static List<Usuario> all() {
		Query<Usuario> query = BiblioServer.ebean.createQuery(Usuario.class)
				.orderBy("nome");
		return query.findList();
	}

	public static List<Usuario> find(String filtro) {
		String sql = "find usuario where nome like :filtro or usuario like :filtro order by nome";
		Query<Usuario> query = BiblioServer.ebean.createQuery(Usuario.class,
				sql).setParameter("filtro", "%" + filtro + "%");
		return query.findList();
	}

	public static Usuario login(String usuario, String senha) {
		// String senhaCriptografada = DigestUtils.md5Hex(senha);
		String senhaCriptografada = senha;
		String sql = "find usuario where usuario = :usuario and senha = :senha";
		Query<Usuario> query = BiblioServer.ebean
				.createQuery(Usuario.class, sql)
				.setParameter("usuario", usuario)
				.setParameter("senha", senhaCriptografada);
		return query.findUnique();
	}

}
