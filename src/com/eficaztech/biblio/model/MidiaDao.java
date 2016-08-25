package com.eficaztech.biblio.model;

import java.util.List;

import com.avaje.ebean.Query;
import com.eficaztech.biblio.util.BiblioServer;
import com.eficaztech.biblio.util.ValidationException;
import com.eficaztech.biblio.util.Validations;

public class MidiaDao {

	public static void save(Midia midia) throws ValidationException {
		Validations.validate(midia);
		if (midia.getId() == null) {
			BiblioServer.ebean.save(midia);
		} else {
			BiblioServer.ebean.update(midia);
		}
	}

	public static Midia find(Long id) {
		return BiblioServer.ebean.find(Midia.class, id);
	}

	public static void delete(Midia midia) {
		BiblioServer.ebean.delete(midia);
	}

	public static List<Midia> all() {
		Query<Midia> query = BiblioServer.ebean.createQuery(Midia.class)
				.orderBy("titulo");
		return query.findList();
	}

	public static List<Midia> find(String titulo) {

		String sql = "find midia where midiaId = :midiaId or titulo like :filtro order by titulo";
		Query<Midia> query = BiblioServer.ebean.createQuery(Midia.class, sql)
				.setParameter("midiaId", titulo.trim())
				.setParameter("filtro", "%" + titulo.trim() + "%");
		return query.findList();

	}

	public static List<Midia> findParaConsultaPublica(String titulo) {

		// pesquisa por:
		String f0 = "(ativo = 'S')";
		String f1 = "(titulo like :filtro)"; // titulo
		String f2 = "(subtitulo like :filtro)"; // subtitulo
		String f3 = "(descricao like :filtro)"; // descricao
		String where = f0 + " and " + f1 + " or " + f2 + " or " + f3;

		String sql = "find midia where " + where + " order by titulo";
		Query<Midia> query = BiblioServer.ebean.createQuery(Midia.class, sql)
				.setParameter("filtro", "%" + titulo.trim() + "%");
		return query.findList();

	}

	public static List<Midia> ultimos100() {
		String sql = "find midia order by id desc limit 100";
		Query<Midia> query = BiblioServer.ebean.createQuery(Midia.class, sql);
		return query.findList();
	}

	public static List<Midia> ultimos100ParaConsultaPublica() {
		String sql = "find midia where ativo = 'S' order by id desc limit 100";
		Query<Midia> query = BiblioServer.ebean.createQuery(Midia.class, sql);
		return query.findList();
	}

}
