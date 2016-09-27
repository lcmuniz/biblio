package com.eficaztech.biblio.view;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.zhtml.Messagebox;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.avaje.ebean.EbeanServerFactory;
import com.eficaztech.biblio.enums.Funcao;
import com.eficaztech.biblio.model.Usuario;
import com.eficaztech.biblio.model.UsuarioDao;
import com.eficaztech.biblio.util.BiblioServer;
import com.eficaztech.biblio.util.Security;
import com.eficaztech.biblio.util.Sessao;

public class LoginView {

	@Wire
	Window loginWindow;

	@Wire("#loginWindow #organizacaoTextbox")
	Textbox organizacaoTextbox;

	@Wire("#loginWindow #usuarioTextbox")
	Textbox usuarioTextbox;

	@Wire("#loginWindow #senhaTextbox")
	Textbox senhaTextbox;

	@AfterCompose
	public void afterCompose(@ContextParam(ContextType.VIEW) Component view) {
		Selectors.wireComponents(view, this, false);
		Sessao.set(Sessao.USUARIO, "");
		Sessao.set(Sessao.FUNCAO, "");
		organizacaoTextbox.setFocus(true);
	}

	@Command
	public void login() {

		if (organizacaoTextbox.getValue().isEmpty()
				|| usuarioTextbox.getValue().isEmpty()
				|| senhaTextbox.getValue().isEmpty()) {
			Messagebox.show("Login inválido.", "Biblio", Messagebox.OK,
					Messagebox.EXCLAMATION);
			return;
		}

		try {
			BiblioServer.ebean = EbeanServerFactory.create(organizacaoTextbox
					.getValue());
		} catch (Exception e) {
			Messagebox
					.show("Não foi possível entrar no sistema. Verifique suas credenciais e tente novamente.",
							"Biblio", Messagebox.OK, Messagebox.EXCLAMATION);
			return;
		}

		Usuario usuario = UsuarioDao.login(usuarioTextbox.getValue(),
				senhaTextbox.getValue());

		if (usuario != null) {
			Sessao.set(Sessao.USUARIO, usuario);
			Sessao.set(Sessao.FUNCAO, usuario.getFuncao());

			if (Security.isAdmOrBib()) {
				Executions.sendRedirect("/livro");
			} else {
				return;
			}
			
		} else {
			Messagebox.show("Login inválido.", "Biblio", Messagebox.OK,
					Messagebox.EXCLAMATION);
		}

	}
}
