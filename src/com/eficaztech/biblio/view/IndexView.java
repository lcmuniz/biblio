package com.eficaztech.biblio.view;

import org.zkoss.bind.annotation.Init;
import org.zkoss.zk.ui.Executions;

import com.eficaztech.biblio.util.Sessao;

public class IndexView {

	@Init
	public void init() {

		boolean nao_logado = Sessao.get(Sessao.USUARIO) == null || Sessao.get(Sessao.USUARIO).toString().isEmpty();

		if (nao_logado) {
			Executions.sendRedirect("/login");
		} else {
			Executions.sendRedirect("/emprestimo");
		}

	}

}
