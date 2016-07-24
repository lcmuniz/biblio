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

import com.eficaztech.biblio.model.Editora;
import com.eficaztech.biblio.model.EditoraDao;
import com.eficaztech.biblio.util.Notification;
import com.eficaztech.biblio.util.ValidationException;
import com.eficaztech.biblio.util.Validations;

@Init(superclass = true)
@AfterCompose(superclass = true)
public class EditoraView extends View {

	private List<Editora> editoras;

	private Editora editoraSelecionada;

	@Wire
	Window editWindow;

	@Wire
	Listbox editoraListbox;

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
	@NotifyChange("editoras")
	public void pesquisar() {
		filtroTextbox.setFocus(true);
		editoras = EditoraDao.find(filtroTextbox.getValue());
	}

	@Command
	@NotifyChange("editoras")
	public void limpar() {
		filtroTextbox.setValue("");
		filtroTextbox.setFocus(true);
		editoras = EditoraDao.all();
	}

	@Command
	@NotifyChange("editoraSelecionada")
	public void novo() {
		editoraSelecionada = new Editora();
		editWindow.setTitle("Nova Editora");
		editWindow.doModal();
		excluirButton.setDisabled(true);
		nomeTextbox.setFocus(true);
	}

	@Command
	@NotifyChange("editoraSelecionada")
	public void editar(@BindingParam("id") Long id) {
		editoraSelecionada = EditoraDao.find(id);
		editWindow.setTitle("Editar Editora");
		editWindow.doModal();
		excluirButton.setDisabled(false);
		nomeTextbox.setFocus(true);
	}

	@Command
	@NotifyChange("editoras")
	public void salvar() {

		try {
			EditoraDao.save(editoraSelecionada);
			Notification.show("success", "EDITORA salva com sucesso.");
			editoraSelecionada = null;
		} catch (ValidationException e) {
			Validations.show(editWindow, "e_errors", e.getErros());
		}

		editWindow.setVisible(false);
		pesquisar();
	}

	@Command
	public void excluir() {

		Messagebox.show("Excluir esta EDITORA?", "Biblio", Messagebox.YES
				| Messagebox.NO, Messagebox.QUESTION,
				new org.zkoss.zk.ui.event.EventListener<Event>() {
					@Override
					public void onEvent(Event e) {
						if (Messagebox.ON_YES.equals(e.getName())) {
							EditoraDao.delete(editoraSelecionada);
							Notification.show("success",
									"EDITORA excluída com sucesso.");
							editWindow.setVisible(false);
							BindUtils.postNotifyChange(null, null,
									EditoraView.this, "editoras");
							pesquisar();
						}
					}
				});

	}

	@Command
	@NotifyChange("editoras")
	public void cancelar() {
		editWindow.setVisible(false);
		pesquisar();
	}

	public List<Editora> getEditoras() {
		return editoras;
	}

	public void setEditoras(List<Editora> editoras) {
		this.editoras = editoras;
	}

	public Editora getEditoraSelecionada() {
		return editoraSelecionada;
	}

	public void setEditoraSelecionada(Editora editoraSelecionada) {
		this.editoraSelecionada = editoraSelecionada;
	}

}
