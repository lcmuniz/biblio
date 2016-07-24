package com.eficaztech.biblio.view;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zhtml.Messagebox;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.eficaztech.biblio.enums.Periodicidade;
import com.eficaztech.biblio.enums.SimNao;
import com.eficaztech.biblio.model.Curso;
import com.eficaztech.biblio.model.Editora;
import com.eficaztech.biblio.model.EditoraDao;
import com.eficaztech.biblio.model.Idioma;
import com.eficaztech.biblio.model.IdiomaDao;
import com.eficaztech.biblio.model.Local;
import com.eficaztech.biblio.model.LocalDao;
import com.eficaztech.biblio.model.Periodico;
import com.eficaztech.biblio.model.PeriodicoDao;
import com.eficaztech.biblio.util.Notification;
import com.eficaztech.biblio.util.ValidationException;
import com.eficaztech.biblio.util.Validations;

@Init(superclass = true)
@AfterCompose(superclass = true)
public class PeriodicoView extends View {

	private List<Periodico> periodicos;
	private List<Curso> cursos;
	private List<Local> locais;
	private List<Idioma> idiomas;
	private List<Editora> editoras;

	private Periodico periodicoSelecionado;

	@Wire
	Window periodicoWindow;

	@Wire
	Window editWindow;

	@Wire("#include #editConsultaWindow")
	Window editConsultaWindow;

	@Wire
	Listbox periodicoListbox;

	@Wire
	Textbox filtroTextbox;

	@Wire("#editWindow #excluirButton")
	Button excluirButton;

	@Wire("#editWindow #tituloTextbox")
	Textbox tituloTextbox;

	@Override
	public void afterCompose() {

		iniciarComboboxes();

		limpar();
	}

	private void iniciarComboboxes() {

		locais = LocalDao.all();
		BindUtils.postNotifyChange(null, null, PeriodicoView.this, "locais");

		idiomas = IdiomaDao.all();
		BindUtils.postNotifyChange(null, null, PeriodicoView.this, "idiomas");

		editoras = EditoraDao.all();
		BindUtils.postNotifyChange(null, null, PeriodicoView.this, "editoras");

	}

	@Command
	@NotifyChange("periodicos")
	public void pesquisar() {
		filtroTextbox.setFocus(true);
		periodicos = PeriodicoDao.find(filtroTextbox.getValue());
	}

	@Command
	@NotifyChange("periodicos")
	public void limpar() {
		filtroTextbox.setValue("");
		filtroTextbox.setFocus(true);
		periodicos = PeriodicoDao.all();
	}

	@Command
	@NotifyChange("periodicoSelecionado")
	public void novo() {
		periodicoSelecionado = new Periodico();
		editWindow.setTitle("Novo Periódico");
		editWindow.doModal();
		excluirButton.setDisabled(true);
		tituloTextbox.setFocus(true);
	}
	
	@Command
	public void verEdicoes(@BindingParam("id") Long id) {
		Executions.sendRedirect("/edicao?pid="+id);
	}

	@Command
	@NotifyChange("periodicoSelecionado")
	public void editar(@BindingParam("id") Long id) {
		periodicoSelecionado = PeriodicoDao.find(id);
		editWindow.setTitle("Editar Periódico");
		editWindow.doModal();
		excluirButton.setDisabled(false);
		tituloTextbox.setFocus(true);
	}

	@Command
	@NotifyChange("periodicos")
	public void salvar() {

		try {
			PeriodicoDao.save(periodicoSelecionado);
			Notification.show("success", "PERIÓDICO salvo com sucesso.");
			periodicoSelecionado = null;
		} catch (ValidationException e) {
			Validations.show(editWindow, "e_errors", e.getErros());
		}

		editWindow.setVisible(false);
		pesquisar();
	}

	@Command
	public void excluir() {

		Messagebox.show("Excluir este PERIÓDICO?", "Biblio", Messagebox.YES
				| Messagebox.NO, Messagebox.QUESTION,
				new org.zkoss.zk.ui.event.EventListener<Event>() {
					@Override
					public void onEvent(Event e) {
						if (Messagebox.ON_YES.equals(e.getName())) {
							PeriodicoDao.delete(periodicoSelecionado);
							Notification.show("success",
									"PERIÓDICO excluído com sucesso.");
							editWindow.setVisible(false);
							BindUtils.postNotifyChange(null, null,
									PeriodicoView.this, "periodicos");
							pesquisar();
						}
					}
				});

	}

	@Command
	@NotifyChange("periodicos")
	public void cancelar() {
		editWindow.setVisible(false);
		pesquisar();
	}

	@Command
	public void novaConsulta(@BindingParam("id") Long id) {

		Map<String, Long> args = new HashMap<String, Long>();
		args.put("consulta_id", new Long(0));
		args.put("cliente_id", id);
		Window win = (Window) Executions.getCurrent().createComponents(
				"/consulta/edit_consulta.zul", periodicoWindow, args);
		win.doModal();

	}

	public List<Periodico> getPeriodicos() {
		return periodicos;
	}

	public void setPeriodicos(List<Periodico> periodicos) {
		this.periodicos = periodicos;
	}

	public List<Curso> getCursos() {
		return cursos;
	}

	public void setCursos(List<Curso> cursos) {
		this.cursos = cursos;
	}

	public List<Local> getLocais() {
		return locais;
	}

	public void setLocais(List<Local> locais) {
		this.locais = locais;
	}

	public List<Idioma> getIdiomas() {
		return idiomas;
	}

	public void setIdiomas(List<Idioma> idiomas) {
		this.idiomas = idiomas;
	}

	public List<Editora> getEditoras() {
		return editoras;
	}

	public void setEditoras(List<Editora> editoras) {
		this.editoras = editoras;
	}

	public Periodico getPeriodicoSelecionado() {
		return periodicoSelecionado;
	}

	public void setPeriodicoSelecionado(Periodico periodicoSelecionado) {
		this.periodicoSelecionado = periodicoSelecionado;
	}

	public Periodicidade[] getPeriodicidades() {
		return Periodicidade.values();
	}

	public SimNao[] getConsultasLocais() {
		return SimNao.values();
	}

}
