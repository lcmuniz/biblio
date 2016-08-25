package com.eficaztech.biblio.model;

import java.util.List;

import com.avaje.ebean.Query;
import com.eficaztech.biblio.util.BiblioServer;
import com.eficaztech.biblio.util.ValidationException;
import com.eficaztech.biblio.util.Validations;

public class LivroDao {

	public static void save(Livro livro) throws ValidationException {
		Validations.validate(livro);
		if (livro.getId() == null) {
			BiblioServer.ebean.save(livro);
		} else {
			BiblioServer.ebean.update(livro);
		}
	}

	public static Livro find(Long id) {
		return BiblioServer.ebean.find(Livro.class, id);
	}

	public static void delete(Livro livro) {
		BiblioServer.ebean.delete(livro);
	}

	public static List<Livro> all() {
		Query<Livro> query = BiblioServer.ebean.createQuery(Livro.class)
				.orderBy("titulo");
		return query.findList();
	}

	public static List<Livro> find(String filtro) {

		String sql = "find livro where (livroid = :livroid) or (titulo like :titulo) or (subtitulo like :subtitulo) order by titulo";
		Query<Livro> query = BiblioServer.ebean.createQuery(Livro.class, sql)
				.setParameter("livroid", filtro.trim())
				.setParameter("titulo", "%" + filtro.trim() + "%")
				.setParameter("subtitulo", "%" + filtro.trim() + "%");
		return query.findList();

	}

	public static List<Livro> findParaConsultaPublica(String filtro) {

		// pesquisa por:
		String f0 = "(ativo = 'S')";
		String f1 = "(titulo like :filtro)"; // titulo do livro
		String f2 = "(subtitulo like :filtro)"; // subtitulo
		String f3 = "(autores like :filtro)"; // assuntos
		String f4 = "(assuntos like :filtro)"; // assuntos
		String where = f0 + " and " + f1 + " or " + f2 + " or " + f3 + " or "
				+ f4;

		String sql = "find livro where " + where + " order by titulo";
		Query<Livro> query = BiblioServer.ebean.createQuery(Livro.class, sql)
				.setParameter("filtro", "%" + filtro.trim() + "%");
		return query.findList();

	}

	public static List<Livro> ultimos100() {
		String sql = "find livro order by id desc limit 100";
		Query<Livro> query = BiblioServer.ebean.createQuery(Livro.class, sql);
		return query.findList();
	}

	public static List<Livro> ultimos100ParaConsultaPublica() {
		String sql = "find livro where ativo = 'S' order by id desc limit 100";
		Query<Livro> query = BiblioServer.ebean.createQuery(Livro.class, sql);
		return query.findList();
	}

}
