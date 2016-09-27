package com.eficaztech.biblio.view;

import java.util.List;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zhtml.Messagebox;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.eficaztech.biblio.model.Local;
import com.eficaztech.biblio.model.LocalDao;
import com.eficaztech.biblio.util.Notification;
import com.eficaztech.biblio.util.Security;
import com.eficaztech.biblio.util.ValidationException;
import com.eficaztech.biblio.util.Validations;

@Init(superclass = true)
@AfterCompose(superclass = true)
public class LocalView extends View {

	private List<Local> locais;

	private Local localSelecionado;

	@Wire
	Window editWindow;

	@Wire
	Listbox localListbox;

	@Wire
	Textbox filtroTextbox;

	@Wire("#editWindow #excluirButton")
	Button excluirButton;

	@Wire("#editWindow #nomeTextbox")
	Textbox nomeTextbox;

	@Override
	public void afterCompose() {
		if (!Security.isAdmOrBib()) return;
		limpar();
	}

	@Command
	@NotifyChange("locais")
	public void pesquisar() {
		filtroTextbox.setFocus(true);
		locais = LocalDao.find(filtroTextbox.getValue());
	}

	@Command
	@NotifyChange("locais")
	public void limpar() {
		filtroTextbox.setValue("");
		filtroTextbox.setFocus(true);
		locais = LocalDao.all();
	}

	@Command
	@NotifyChange("localSelecionado")
	public void novo() {
		localSelecionado = new Local();
		editWindow.setTitle("Novo Local");
		editWindow.doModal();
		excluirButton.setDisabled(true);
		nomeTextbox.setFocus(true);
	}

	@Command
	@NotifyChange("localSelecionado")
	public void editar(@BindingParam("id") Long id) {
		localSelecionado = LocalDao.find(id);
		editWindow.setTitle("Editar Curso");
		editWindow.doModal();
		excluirButton.setDisabled(false);
		nomeTextbox.setFocus(true);
	}

	@Command
	@NotifyChange("locais")
	public void salvar() {

		try {
			LocalDao.save(localSelecionado);
			Notification.show("success", "LOCAL salvo com sucesso.");
			localSelecionado = null;
		} catch (ValidationException e) {
			Validations.show(editWindow, "e_errors", e.getErros());
		}

		editWindow.setVisible(false);
		pesquisar();
	}

	@Command
	public void excluir() {

		Messagebox.show("Excluir este LOCAL?", "Biblio", Messagebox.YES
				| Messagebox.NO, Messagebox.QUESTION,
				new org.zkoss.zk.ui.event.EventListener<Event>() {
					@Override
					public void onEvent(Event e) {
						if (Messagebox.ON_YES.equals(e.getName())) {
							LocalDao.delete(localSelecionado);
							Notification.show("success",
									"LOCAL excluído com sucesso.");
							editWindow.setVisible(false);
							BindUtils.postNotifyChange(null, null,
									LocalView.this, "locais");
							pesquisar();
						}
					}
				});

	}

	@Command
	@NotifyChange("locais")
	public void cancelar() {
		editWindow.setVisible(false);
		pesquisar();
	}

	public List<Local> getLocais() {
		return locais;
	}

	public void setLocais(List<Local> locais) {
		this.locais = locais;
	}

	public Local getLocalSelecionado() {
		return localSelecionado;
	}

	public void setLocalSelecionado(Local localSelecionado) {
		this.localSelecionado = localSelecionado;
	}

}
