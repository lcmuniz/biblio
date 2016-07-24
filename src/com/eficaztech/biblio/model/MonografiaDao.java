package com.eficaztech.biblio.model;

import java.util.List;

import com.avaje.ebean.Query;
import com.eficaztech.biblio.util.BiblioServer;
import com.eficaztech.biblio.util.ValidationException;
import com.eficaztech.biblio.util.Validations;

public class MonografiaDao {

	public static void save(Monografia monografia) throws ValidationException {
		Validations.validate(monografia);
		if (monografia.getId() == null) {
			BiblioServer.ebean.save(monografia);
		} else {
			BiblioServer.ebean.update(monografia);
		}
	}

	public static Monografia find(Long id) {
		return BiblioServer.ebean.find(Monografia.class, id);
	}

	public static void delete(Monografia monografia) {
		BiblioServer.ebean.delete(monografia);
	}

	public static List<Monografia> all() {
		Query<Monografia> query = BiblioServer.ebean.createQuery(
				Monografia.class).orderBy("titulo");
		return query.findList();
	}

	public static List<Monografia> find(String filtro) {

		String sql = "find monografia where (titulo like :titulo) or (subtitulo like :subtitulo) order by titulo";
		Query<Monografia> query = BiblioServer.ebean
				.createQuery(Monografia.class, sql)
				.setParameter("titulo", "%" + filtro.trim() + "%")
				.setParameter("subtitulo", "%" + filtro.trim() + "%");
		return query.findList();

	}

	public static List<Monografia> findParaConsultaPublica(String filtro) {

		// pesquisa por:
		String f0 = "(ativo = 'S')";
		String f1 = "(titulo like :filtro)"; // titulo do livro
		String f2 = "(subtitulo like :filtro)"; // subtitulo
		String f3 = "(autores like :filtro)"; // assuntos
		String f4 = "(assuntos like :filtro)"; // assuntos
		String where = f0 + " and " + f1 + " or " + f2 + " or " + f3 + " or "
				+ f4;

		String sql = "find monografia where " + where + " order by titulo";
		Query<Monografia> query = BiblioServer.ebean.createQuery(
				Monografia.class, sql).setParameter("filtro",
				"%" + filtro.trim() + "%");
		return query.findList();

	}

	public static List<Monografia> ultimos100() {
		String sql = "find monografia order by id desc limit 100";
		Query<Monografia> query = BiblioServer.ebean.createQuery(
				Monografia.class, sql);
		return query.findList();
	}

	public static List<Monografia> ultimos100ParaConsultaPublica() {
		String sql = "find monografia where ativo = 'S' order by id desc limit 100";
		Query<Monografia> query = BiblioServer.ebean.createQuery(
				Monografia.class, sql);
		return query.findList();
	}

}
