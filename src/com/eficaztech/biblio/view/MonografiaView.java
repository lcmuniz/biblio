package com.eficaztech.biblio.view;

import java.util.ArrayList;
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
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.eficaztech.biblio.enums.SimNao;
import com.eficaztech.biblio.enums.TipoAquisicao;
import com.eficaztech.biblio.model.Curso;
import com.eficaztech.biblio.model.CursoDao;
import com.eficaztech.biblio.model.Editora;
import com.eficaztech.biblio.model.EditoraDao;
import com.eficaztech.biblio.model.ExemplarMonografia;
import com.eficaztech.biblio.model.Idioma;
import com.eficaztech.biblio.model.IdiomaDao;
import com.eficaztech.biblio.model.Local;
import com.eficaztech.biblio.model.LocalDao;
import com.eficaztech.biblio.model.Monografia;
import com.eficaztech.biblio.model.MonografiaDao;
import com.eficaztech.biblio.util.Notification;
import com.eficaztech.biblio.util.Security;
import com.eficaztech.biblio.util.ValidationException;
import com.eficaztech.biblio.util.Validations;

@Init(superclass = true)
@AfterCompose(superclass = true)
public class MonografiaView extends View {

	private List<Monografia> monografias;
	private List<Local> locais;
	private List<Idioma> idiomas;
	private List<Editora> editoras;
	private List<Curso> cursos;

	private Monografia monografiaSelecionada;
	private List<Curso> cursosDaMonografiaSelecionada;

	@Wire
	Window monografiaWindow;

	@Wire
	Window editWindow;

	@Wire("#include #editConsultaWindow")
	Window editConsultaWindow;

	@Wire
	Listbox monografiaListbox;

	@Wire
	Textbox filtroTextbox;

	@Wire("#editWindow #excluirButton")
	Button excluirButton;

	@Wire("#editWindow #tituloTextbox")
	Textbox tituloTextbox;

	// @Wire("#cursoWindow #cursoListbox")
	@Wire("#editWindow #cursoListbox")
	Listbox cursoListbox;

	@Wire("#editWindow #numeroNovosExemplaresIntbox")
	Intbox numeroNovosExemplaresIntbox;

	@Wire("#editWindow #tabbox")
	Tabbox tabbox;

	@Override
	public void afterCompose() {
		
		if (!Security.isAdmOrBib()) return;

		iniciarComboboxes();

		limpar();
	}

	private void iniciarComboboxes() {

		locais = LocalDao.all();
		BindUtils.postNotifyChange(null, null, MonografiaView.this, "locais");

		idiomas = IdiomaDao.all();
		BindUtils.postNotifyChange(null, null, MonografiaView.this, "idiomas");

		editoras = EditoraDao.all();
		BindUtils.postNotifyChange(null, null, MonografiaView.this, "editoras");

		cursos = CursoDao.all();
		BindUtils.postNotifyChange(null, null, MonografiaView.this, "cursos");

	}

	@Command
	@NotifyChange("monografias")
	public void pesquisar() {
		filtroTextbox.setFocus(true);
		monografias = MonografiaDao.find(filtroTextbox.getValue());
	}

	@Command
	@NotifyChange("monografias")
	public void limpar() {
		filtroTextbox.setValue("");
		filtroTextbox.setFocus(true);
		monografias = MonografiaDao.all();
	}

	@Command
	@NotifyChange({ "monografiaSelecionada", "cursosDaMonografiaSelecionada" })
	public void novo() {
		monografiaSelecionada = new Monografia();

		cursosDaMonografiaSelecionada = new ArrayList<Curso>();
		cursosDaMonografiaSelecionada.add(new Curso());

		editWindow.setTitle("Novo Monografia");
		editWindow.doModal();
		excluirButton.setDisabled(true);
		tabbox.setSelectedIndex(0);
		tituloTextbox.setFocus(true);
	}

	@Command
	@NotifyChange({ "monografiaSelecionada", "cursosDaMonografiaSelecionada" })
	public void editar(@BindingParam("id") Long id) {
		monografiaSelecionada = MonografiaDao.find(id);

		// preencher listbox de cursos
		cursosDaMonografiaSelecionada = new ArrayList<Curso>(
				monografiaSelecionada.getCursos());
		cursosDaMonografiaSelecionada.add(new Curso());

		editWindow.setTitle("Editar Monografia");
		editWindow.doModal();
		excluirButton.setDisabled(false);
		tabbox.setSelectedIndex(0);
		tituloTextbox.setFocus(true);
	}

	@Command
	@NotifyChange("monografias")
	public void salvar() {

		try {
			adicionarCursosAoMonografia();
			MonografiaDao.save(monografiaSelecionada);
			Notification.show("success", "LIVRO salvo com sucesso.");
		} catch (ValidationException e) {
			Validations.show(editWindow, "e_errors", e.getErros());
		}

	}

	@Command
	public void excluir() {

		Messagebox.show("Excluir este LIVRO?", "Biblio", Messagebox.YES
				| Messagebox.NO, Messagebox.QUESTION,
				new org.zkoss.zk.ui.event.EventListener<Event>() {
					@Override
					public void onEvent(Event e) {
						if (Messagebox.ON_YES.equals(e.getName())) {
							MonografiaDao.delete(monografiaSelecionada);
							Notification.show("success",
									"LIVRO excluído com sucesso.");
							editWindow.setVisible(false);
							BindUtils.postNotifyChange(null, null,
									MonografiaView.this, "monografias");
							pesquisar();
						}
					}
				});

	}

	@Command
	@NotifyChange("monografias")
	public void fechar() {
		editWindow.setVisible(false);
		pesquisar();
	}

	@Command
	public void novaConsulta(@BindingParam("id") Long id) {

		Map<String, Long> args = new HashMap<String, Long>();
		args.put("consulta_id", new Long(0));
		args.put("cliente_id", id);
		Window win = (Window) Executions.getCurrent().createComponents(
				"/consulta/edit_consulta.zul", monografiaWindow, args);
		win.doModal();

	}

	private void adicionarCursosAoMonografia() {

		List<Listitem> items = cursoListbox.getItems();
		List<Curso> cursosTemp = new ArrayList<Curso>();

		for (Listitem listitem : items) {
			Curso value = listitem.getValue();
			if (value.getId() != null) {
				cursosTemp.add(value);
			}
		}

		monografiaSelecionada.getCursos().clear();
		monografiaSelecionada.setCursos(cursosTemp);

	}

	@NotifyChange("cursosDaMonografiaSelecionada")
	@Command
	public void excluirCurso(@BindingParam("id") Long id) {
		if (id == null)
			return;
		Curso curso = CursoDao.find(id);
		cursosDaMonografiaSelecionada.remove(curso);
	}

	@Command
	public void aoAlterarCursos() {

		// ao alterar um curso na listbox,
		// alterar o model da listbox

		cursosDaMonografiaSelecionada.clear();
		int size = cursoListbox.getModel().getSize();
		for (int i = 0; i < size; i++) {
			Curso curso = (Curso) cursoListbox.getModel().getElementAt(i);
			cursosDaMonografiaSelecionada.add(curso);
		}

		Curso ultimo = cursosDaMonografiaSelecionada.get(size - 1);
		if (ultimo.getId() != null) {
			cursosDaMonografiaSelecionada.add(new Curso());
			BindUtils.postNotifyChange(null, null, MonografiaView.this,
					"cursosDaMonografiaSelecionada");
		}

	}

	@Command
	@NotifyChange("monografiaSelecionada")
	public void adicionarExemplares() {

		for (int i = 0; i < numeroNovosExemplaresIntbox.getValue(); i++) {
			ExemplarMonografia el = new ExemplarMonografia();
			el.setId(0L);
			el.setMonografia(monografiaSelecionada);
			monografiaSelecionada.getExemplares().add(el);
		}

	}

	@Command
	@NotifyChange("monografiaSelecionada")
	public void excluirExemplar(@BindingParam("el") ExemplarMonografia el) {
		monografiaSelecionada.getExemplares().remove(el);
	}

	public List<Monografia> getMonografias() {
		return monografias;
	}

	public void setMonografias(List<Monografia> monografias) {
		this.monografias = monografias;
	}

	public Monografia getMonografiaSelecionada() {
		return monografiaSelecionada;
	}

	public void setMonografiaSelecionada(Monografia monografiaSelecionada) {
		this.monografiaSelecionada = monografiaSelecionada;
	}

	public TipoAquisicao[] getTiposAquisicoes() {
		return TipoAquisicao.values();
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

	public List<Curso> getCursos() {
		return cursos;
	}

	public void setCursos(List<Curso> cursos) {
		this.cursos = cursos;
	}

	public SimNao[] getConsultasLocais() {
		return SimNao.values();
	}

	public List<Curso> getCursosDaMonografiaSelecionada() {
		return cursosDaMonografiaSelecionada;
	}

	public void setCursosDaMonografiaSelecionada(
			List<Curso> cursosDaMonografiaSelecionada) {
		this.cursosDaMonografiaSelecionada = cursosDaMonografiaSelecionada;
	}
	
	public SimNao[] getSimNao() {
		return SimNao.values();
	}

}
