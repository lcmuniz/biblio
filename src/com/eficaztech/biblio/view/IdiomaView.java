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

import com.eficaztech.biblio.model.Idioma;
import com.eficaztech.biblio.model.IdiomaDao;
import com.eficaztech.biblio.util.Notification;
import com.eficaztech.biblio.util.ValidationException;
import com.eficaztech.biblio.util.Validations;

@Init(superclass = true)
@AfterCompose(superclass = true)
public class IdiomaView extends View {

	private List<Idioma> idiomas;

	private Idioma idiomaSelecionado;

	@Wire
	Window editWindow;

	@Wire
	Listbox idiomaListbox;

	@Wire
	Textbox filtroTextbox;

	@Wire("#editWindow #excluirButton")
	Button excluirButton;

	@Wire("#editWindow #nomeTextbox")
	Textbox nomeTextbox;

	@Override
	public void afterCompose() {
		limpar();
	}

	@Command
	@NotifyChange("idiomas")
	public void pesquisar() {
		filtroTextbox.setFocus(true);
		idiomas = IdiomaDao.find(filtroTextbox.getValue());
	}

	@Command
	@NotifyChange("idiomas")
	public void limpar() {
		filtroTextbox.setValue("");
		filtroTextbox.setFocus(true);
		idiomas = IdiomaDao.all();
	}

	@Command
	@NotifyChange("idiomaSelecionado")
	public void novo() {
		idiomaSelecionado = new Idioma();
		editWindow.setTitle("Novo Idioma");
		editWindow.doModal();
		excluirButton.setDisabled(true);
		nomeTextbox.setFocus(true);
	}

	@Command
	@NotifyChange("idiomaSelecionado")
	public void editar(@BindingParam("id") Long id) {
		idiomaSelecionado = IdiomaDao.find(id);
		editWindow.setTitle("Editar Idioma");
		editWindow.doModal();
		excluirButton.setDisabled(false);
		nomeTextbox.setFocus(true);
	}

	@Command
	@NotifyChange("idiomas")
	public void salvar() {

		try {
			IdiomaDao.save(idiomaSelecionado);
			Notification.show("success", "IDIOMA salvo com sucesso.");
			idiomaSelecionado = null;
		} catch (ValidationException e) {
			Validations.show(editWindow, "e_errors", e.getErros());
		}

		editWindow.setVisible(false);
		pesquisar();
	}

	@Command
	public void excluir() {

		Messagebox.show("Excluir este IDIOMA?", "Biblio", Messagebox.YES
				| Messagebox.NO, Messagebox.QUESTION,
				new org.zkoss.zk.ui.event.EventListener<Event>() {
					@Override
					public void onEvent(Event e) {
						if (Messagebox.ON_YES.equals(e.getName())) {
							IdiomaDao.delete(idiomaSelecionado);
							Notification.show("success",
									"IDIOMA excluído com sucesso.");
							editWindow.setVisible(false);
							BindUtils.postNotifyChange(null, null,
									IdiomaView.this, "idiomas");
							pesquisar();
						}
					}
				});

	}

	@Command
	@NotifyChange("idiomas")
	public void cancelar() {
		editWindow.setVisible(false);
		pesquisar();
	}

	public List<Idioma> getIdiomas() {
		return idiomas;
	}

	public void setIdiomas(List<Idioma> idiomas) {
		this.idiomas = idiomas;
	}

	public Idioma getIdiomaSelecionado() {
		return idiomaSelecionado;
	}

	public void setIdiomaSelecionado(Idioma idiomaSelecionado) {
		this.idiomaSelecionado = idiomaSelecionado;
	}

}
