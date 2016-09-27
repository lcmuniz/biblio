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
import com.eficaztech.biblio.model.ExemplarLivro;
import com.eficaztech.biblio.model.Idioma;
import com.eficaztech.biblio.model.IdiomaDao;
import com.eficaztech.biblio.model.Livro;
import com.eficaztech.biblio.model.LivroDao;
import com.eficaztech.biblio.model.Local;
import com.eficaztech.biblio.model.LocalDao;
import com.eficaztech.biblio.util.Notification;
import com.eficaztech.biblio.util.Security;
import com.eficaztech.biblio.util.ValidationException;
import com.eficaztech.biblio.util.Validations;

@Init(superclass = true)
@AfterCompose(superclass = true)
public class LivroView extends View {

	private List<Livro> livros;
	private List<Local> locais;
	private List<Idioma> idiomas;
	private List<Editora> editoras;
	private List<Curso> cursos;

	private Livro livroSelecionado;
	private List<Curso> cursosDoLivroSelecionado;

	@Wire
	Window livroWindow;

	@Wire
	Window editWindow;

	@Wire("#include #editConsultaWindow")
	Window editConsultaWindow;

	@Wire
	Listbox livroListbox;

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

		ultimos100();

	}

	private void ultimos100() {
		filtroTextbox.setFocus(true);
		filtroTextbox.setPlaceholder("Últimos 100 livros cadastrados");
		livros = LivroDao.ultimos100();

	}

	private void iniciarComboboxes() {

		locais = LocalDao.all();
		BindUtils.postNotifyChange(null, null, LivroView.this, "locais");

		idiomas = IdiomaDao.all();
		BindUtils.postNotifyChange(null, null, LivroView.this, "idiomas");

		editoras = EditoraDao.all();
		BindUtils.postNotifyChange(null, null, LivroView.this, "editoras");

		cursos = CursoDao.all();
		BindUtils.postNotifyChange(null, null, LivroView.this, "cursos");

	}

	@Command
	@NotifyChange("livros")
	public void pesquisar() {
		filtroTextbox.setFocus(true);
		if (filtroTextbox.getValue().equals("")) {
			livros = LivroDao.ultimos100();
		} else {
			livros = LivroDao.find(filtroTextbox.getValue());
		}
	}

	@Command
	@NotifyChange("livros")
	public void limpar() {
		filtroTextbox.setValue("");
		filtroTextbox.setFocus(true);
		livros = LivroDao.ultimos100();
	}

	@Command
	@NotifyChange({ "livroSelecionado", "cursosDoLivroSelecionado" })
	public void novo() {
		livroSelecionado = new Livro();
		livroSelecionado.setConsultaLocal(SimNao.N);
		livroSelecionado.setAtivo(SimNao.S);

		cursosDoLivroSelecionado = new ArrayList<Curso>();
		cursosDoLivroSelecionado.add(new Curso());

		editWindow.setTitle("Novo Livro");
		editWindow.doModal();
		excluirButton.setDisabled(true);
		tabbox.setSelectedIndex(0);
		tituloTextbox.setFocus(true);
	}

	@Command
	@NotifyChange({ "livroSelecionado", "cursosDoLivroSelecionado" })
	public void editar(@BindingParam("id") Long id) {
		livroSelecionado = LivroDao.find(id);

		// preencher listbox de cursos
		cursosDoLivroSelecionado = new ArrayList<Curso>(
				livroSelecionado.getCursos());
		cursosDoLivroSelecionado.add(new Curso());

		editWindow.setTitle("Editar Livro");
		editWindow.doModal();
		excluirButton.setDisabled(false);
		tabbox.setSelectedIndex(0);
		tituloTextbox.setFocus(true);
	}

	@Command
	@NotifyChange({ "livros", "livroSelecionado" })
	public void salvar() {

		try {
			adicionarCursosAoLivro();
			LivroDao.save(livroSelecionado);
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
							LivroDao.delete(livroSelecionado);
							Notification.show("success",
									"LIVRO excluído com sucesso.");
							editWindow.setVisible(false);
							BindUtils.postNotifyChange(null, null,
									LivroView.this, "livros");
							pesquisar();
						}
					}
				});

	}

	@Command
	@NotifyChange("livros")
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
				"/consulta/edit_consulta.zul", livroWindow, args);
		win.doModal();

	}

	private void adicionarCursosAoLivro() {

		List<Listitem> items = cursoListbox.getItems();
		List<Curso> cursosTemp = new ArrayList<Curso>();

		for (Listitem listitem : items) {
			Curso value = listitem.getValue();
			if (value.getId() != null) {
				cursosTemp.add(value);
			}
		}

		livroSelecionado.getCursos().clear();
		livroSelecionado.setCursos(cursosTemp);

	}

	@NotifyChange("cursosDoLivroSelecionado")
	@Command
	public void excluirCurso(@BindingParam("id") Long id) {
		if (id == null)
			return;
		Curso curso = CursoDao.find(id);
		cursosDoLivroSelecionado.remove(curso);
	}

	@Command
	public void aoAlterarCursos() {

		// ao alterar um curso na listbox,
		// alterar o model da listbox

		cursosDoLivroSelecionado.clear();
		int size = cursoListbox.getModel().getSize();
		for (int i = 0; i < size; i++) {
			Curso curso = (Curso) cursoListbox.getModel().getElementAt(i);
			cursosDoLivroSelecionado.add(curso);
		}

		Curso ultimo = cursosDoLivroSelecionado.get(size - 1);
		if (ultimo.getId() != null) {
			cursosDoLivroSelecionado.add(new Curso());
			BindUtils.postNotifyChange(null, null, LivroView.this,
					"cursosDoLivroSelecionado");
		}

	}

	@Command
	@NotifyChange("livroSelecionado")
	public void adicionarExemplares() {

		for (int i = 0; i < numeroNovosExemplaresIntbox.getValue(); i++) {
			ExemplarLivro el = new ExemplarLivro();
			el.setId(0L);
			el.setLivro(livroSelecionado);
			livroSelecionado.getExemplares().add(el);
		}

	}

	@Command
	@NotifyChange("livroSelecionado")
	public void excluirExemplar(@BindingParam("el") ExemplarLivro el) {
		livroSelecionado.getExemplares().remove(el);
	}

	public List<Livro> getLivros() {
		return livros;
	}

	public void setLivros(List<Livro> livros) {
		this.livros = livros;
	}

	public Livro getLivroSelecionado() {
		return livroSelecionado;
	}

	public void setLivroSelecionado(Livro livroSelecionado) {
		this.livroSelecionado = livroSelecionado;
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

	public List<Curso> getCursosDoLivroSelecionado() {
		return cursosDoLivroSelecionado;
	}

	public void setCursosDoLivroSelecionado(List<Curso> cursosDoLivroSelecionado) {
		this.cursosDoLivroSelecionado = cursosDoLivroSelecionado;
	}

	public SimNao[] getSimNao() {
		return SimNao.values();
	}

}
