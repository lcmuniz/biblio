package com.eficaztech.biblio.view;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.eficaztech.biblio.enums.Estado;
import com.eficaztech.biblio.model.Empresa;
import com.eficaztech.biblio.model.EmpresaDao;
import com.eficaztech.biblio.util.Notification;
import com.eficaztech.biblio.util.ValidationException;
import com.eficaztech.biblio.util.Validations;

@Init(superclass = true)
@AfterCompose(superclass = true)
public class EmpresaView extends View {

	private Empresa empresaSelecionada;

	@Wire
	Window editWindow;

	@Wire
	Textbox nomeTextbox;

	@Override
	public void afterCompose() {

		empresaSelecionada = EmpresaDao.findFirst();

		editWindow.doOverlapped();

	}

	@Command
	public void salvar() {
		try {
			EmpresaDao.save(empresaSelecionada);
			Notification.show("success", "Item salvo com sucesso.");
		} catch (ValidationException e) {
			Validations.show(editWindow, "e_errors", e.getErros());
		}
	}

	@Command
	public void cancelar() {
		Executions.sendRedirect("/");
	}

	public Empresa getEmpresaSelecionada() {
		return empresaSelecionada;
	}

	public void setEmpresaSelecionada(Empresa empresaSelecionada) {
		this.empresaSelecionada = empresaSelecionada;
	}

	public Estado[] getEstados() {
		return Estado.values();
	}

}
