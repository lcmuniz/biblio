package com.eficaztech.biblio.model;

import java.util.List;

import com.avaje.ebean.Query;
import com.eficaztech.biblio.util.BiblioServer;

public class ArtigoEdicaoDao {

	public static ArtigoEdicao find(Long id) {
		return BiblioServer.ebean.find(ArtigoEdicao.class, id);
	}
	
	public static void delete(ArtigoEdicao artigoEdicao) {
		BiblioServer.ebean.delete(artigoEdicao);
	}
	
	public static List<ArtigoEdicao> findParaConsultaPublica(String titulo) {

		// pesquisa por:
		String f0 = "(edicao.ativo = 'S')";
		String f1 = "(edicao.periodico.titulo like :filtro)"; // titulo do periodico
		String f2 = "(edicao.titulo like :filtro)"; // titulo da edicao
		String f3 = "(titulo like :filtro)"; // titulo do artigo
		String f4 = "(autores like :filtro)"; // autores do artigo
		String f5 = "(assuntos like :filtro)"; // assuntos do artigo
		String where = f0 + " and " + f1 + " or " + f2 + " or " + f3 + " or " + f4 + " or " + f5;
		
		String sql = "find artigoEdicao where " + where + " order by edicao.periodico.titulo, edicao.titulo, titulo";
		Query<ArtigoEdicao> query = BiblioServer.ebean.createQuery(
				ArtigoEdicao.class, sql).setParameter("filtro",
				"%" + titulo.trim() + "%");

		return query.findList();

	}

	public static List<ArtigoEdicao> ultimos100() {
		String sql = "find artigoEdicao order by id desc limit 100";
		Query<ArtigoEdicao> query = BiblioServer.ebean.createQuery(
				ArtigoEdicao.class, sql);
		return query.findList();
	}
	
	public static List<ArtigoEdicao> ultimos100ParaConsultaPublica() {
		String sql = "find artigoEdicao where edicao.ativo = 'S' order by id desc limit 100";
		Query<ArtigoEdicao> query = BiblioServer.ebean.createQuery(
				ArtigoEdicao.class, sql);
		return query.findList();
	}

}
