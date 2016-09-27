package com.eficaztech.biblio.util;

import org.zkoss.zk.ui.Executions;

import com.eficaztech.biblio.enums.Funcao;

public class Security {

	public static boolean isAdmOrBib() {
		boolean adm = Sessao.get(Sessao.FUNCAO) == Funcao.ADM;
		boolean bib = Sessao.get(Sessao.FUNCAO) == Funcao.BIB;
		boolean sec = Sessao.get(Sessao.FUNCAO) == Funcao.SEC;

		if (adm || bib) {
			return true;
		} else {
			Executions.sendRedirect("/emprestimo");
			return false;
		}

	}

}
