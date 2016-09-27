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

import com.eficaztech.biblio.model.Curso;
import com.eficaztech.biblio.model.CursoDao;
import com.eficaztech.biblio.util.Notification;
import com.eficaztech.biblio.util.Security;
import com.eficaztech.biblio.util.ValidationException;
import com.eficaztech.biblio.util.Validations;

@Init(superclass = true)
@AfterCompose(superclass = true)
public class CursoView extends View {

	private List<Curso> cursos;

	private Curso cursoSelecionado;

	@Wire
	Window editWindow;

	@Wire
	Listbox cursoListbox;

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
	@NotifyChange("cursos")
	public void pesquisar() {
		filtroTextbox.setFocus(true);
		cursos = CursoDao.find(filtroTextbox.getValue());
	}

	@Command
	@NotifyChange("cursos")
	public void limpar() {
		filtroTextbox.setValue("");
		filtroTextbox.setFocus(true);
		cursos = CursoDao.all();
	}

	@Command
	@NotifyChange("cursoSelecionado")
	public void novo() {
		cursoSelecionado = new Curso();
		editWindow.setTitle("Novo Curso");
		editWindow.doModal();
		excluirButton.setDisabled(true);
		nomeTextbox.setFocus(true);
	}

	@Command
	@NotifyChange("cursoSelecionado")
	public void editar(@BindingParam("id") Long id) {
		cursoSelecionado = CursoDao.find(id);
		editWindow.setTitle("Editar Curso");
		editWindow.doModal();
		excluirButton.setDisabled(false);
		nomeTextbox.setFocus(true);
	}

	@Command
	@NotifyChange("cursos")
	public void salvar() {
		try {
			CursoDao.save(cursoSelecionado);
			Notification.show("success", "Item salvo com sucesso.");
			cursoSelecionado = null;
		} catch (ValidationException e) {
			Validations.show(editWindow, "e_errors", e.getErros());
		}

		editWindow.setVisible(false);
		pesquisar();
	}

	@Command
	public void excluir() {

		Messagebox.show("Excluir este CURSO?", "Biblio", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION, new org.zkoss.zk.ui.event.EventListener<Event>() {
			@Override
			public void onEvent(Event e) {
				if (Messagebox.ON_YES.equals(e.getName())) {
					CursoDao.delete(cursoSelecionado);
					Notification.show("success", "CURSO excluído com sucesso.");
					editWindow.setVisible(false);
					BindUtils.postNotifyChange(null, null, CursoView.this, "cursos");
					pesquisar();
				}
			}
		});

	}

	@Command
	@NotifyChange("cursos")
	public void cancelar() {
		editWindow.setVisible(false);
		pesquisar();
	}

	public List<Curso> getCursos() {
		return cursos;
	}

	public void setCursos(List<Curso> cursos) {
		this.cursos = cursos;
	}

	public Curso getCursoSelecionado() {
		return cursoSelecionado;
	}

	public void setCursoSelecionado(Curso cursoSelecionado) {
		this.cursoSelecionado = cursoSelecionado;
	}

}
