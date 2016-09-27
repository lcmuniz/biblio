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
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.eficaztech.biblio.enums.SimNao;
import com.eficaztech.biblio.enums.TipoAquisicao;
import com.eficaztech.biblio.model.ArtigoEdicao;
import com.eficaztech.biblio.model.Edicao;
import com.eficaztech.biblio.model.EdicaoDao;
import com.eficaztech.biblio.model.ExemplarEdicao;
import com.eficaztech.biblio.model.Periodico;
import com.eficaztech.biblio.model.PeriodicoDao;
import com.eficaztech.biblio.util.Notification;
import com.eficaztech.biblio.util.Security;
import com.eficaztech.biblio.util.ValidationException;
import com.eficaztech.biblio.util.Validations;

@Init(superclass = true)
@AfterCompose(superclass = true)
public class EdicaoView extends View {

	private List<Edicao> edicoes;
	private List<Periodico> periodicos;

	private Edicao edicaoSelecionada;
	private ArtigoEdicao artigoSelecionado;

	@Wire
	Window edicaoWindow;

	@Wire
	Window editWindow;

	@Wire
	Window editArtigoWindow;

	@Wire
	Listbox edicaoListbox;

	@Wire
	Combobox filtroPeriodicoCombobox;

	Periodico filtroPeriodico;

	@Wire
	Textbox filtroTituloTextbox;

	@Wire("#editWindow #excluirButton")
	Button excluirButton;

	@Wire("#editWindow #periodicoCombobox")
	Combobox periodicoCombobox;

	@Wire("#editWindow #numeroNovosExemplaresIntbox")
	Intbox numeroNovosExemplaresIntbox;

	@Wire("#editWindow #tabbox")
	Tabbox tabbox;

	@Wire("#editArtigoWindow #excluirArtigoButton")
	Button excluirArtigoButton;

	@Wire("#editArtigoWindow #tituloArtigoTextbox")
	Textbox tituloArtigoTextbox;

	@Override
	public void afterCompose() {
		
		if (!Security.isAdmOrBib()) return;

		iniciarComboboxes();

		String param = Executions.getCurrent().getParameter("pid");
		if (param != null && !param.equals("")) {
			filtroPeriodico = PeriodicoDao.find(new Long(param));
			pesquisar();
		} else {
			limpar();
		}

	}

	private void iniciarComboboxes() {

		periodicos = PeriodicoDao.all();
		BindUtils.postNotifyChange(null, null, EdicaoView.this, "periodicos");

	}

	@Command
	@NotifyChange("edicoes")
	public void pesquisar() {
		filtroPeriodicoCombobox.setFocus(true);
		filtroTituloTextbox.setFocus(true);
		edicoes = EdicaoDao.find(filtroPeriodico,
				filtroTituloTextbox.getValue());
	}

	@Command
	@NotifyChange("edicoes")
	public void limpar() {
		filtroPeriodico = null;
		filtroPeriodicoCombobox.setSelectedItem(null);
		filtroPeriodicoCombobox.setFocus(true);
		filtroTituloTextbox.setValue("");
		filtroTituloTextbox.setFocus(true);
		edicoes = EdicaoDao.all();
	}

	@Command
	@NotifyChange("edicaoSelecionada")
	public void novo() {
		edicaoSelecionada = new Edicao();
		editWindow.setTitle("Nova Edição");
		editWindow.doModal();
		excluirButton.setDisabled(true);
		tabbox.setSelectedIndex(0);
		periodicoCombobox.setFocus(true);
	}

	@Command
	@NotifyChange("edicaoSelecionada")
	public void editar(@BindingParam("id") Long id) {
		edicaoSelecionada = EdicaoDao.find(id);
		editWindow.setTitle("Editar Edição");
		editWindow.doModal();
		excluirButton.setDisabled(false);
		tabbox.setSelectedIndex(0);
		periodicoCombobox.setFocus(true);
	}

	@Command
	@NotifyChange("artigoSelecionado")
	public void novoArtigo() {
		artigoSelecionado = new ArtigoEdicao();
		artigoSelecionado.setId(0L);
		artigoSelecionado.setEdicao(edicaoSelecionada);
		editArtigoWindow.setTitle("Novo Artigo");
		editArtigoWindow.doModal();
		excluirArtigoButton.setDisabled(true);
		tituloArtigoTextbox.setFocus(true);

	}

	@Command
	@NotifyChange("artigoSelecionado")
	public void editarArtigo(@BindingParam("id") Long id,
			@BindingParam("obj") ArtigoEdicao ae) {
		artigoSelecionado = ae;
		editArtigoWindow.setTitle("Editar Artigo");
		editArtigoWindow.doModal();
		excluirArtigoButton.setDisabled(false);
		tituloArtigoTextbox.setFocus(true);
	}

	@Command
	@NotifyChange("edicoes")
	public void salvar() {

		try {
			EdicaoDao.save(edicaoSelecionada);
			Notification.show("success", "EDIÇÃO salvo com sucesso.");
		} catch (ValidationException e) {
			Validations.show(editWindow, "e_errors", e.getErros());
		}

	}

	@Command
	public void excluir() {

		Messagebox.show("Excluir esta EDIÇÃO?", "Biblio", Messagebox.YES
				| Messagebox.NO, Messagebox.QUESTION,
				new org.zkoss.zk.ui.event.EventListener<Event>() {
					@Override
					public void onEvent(Event e) {
						if (Messagebox.ON_YES.equals(e.getName())) {
							EdicaoDao.delete(edicaoSelecionada);
							Notification.show("success",
									"EDIÇÃO excluída com sucesso.");
							editWindow.setVisible(false);
							BindUtils.postNotifyChange(null, null,
									EdicaoView.this, "edicoes");
							pesquisar();
						}
					}
				});

	}

	@Command
	@NotifyChange("edicaoSelecionada")
	public void salvarArtigo() {

		try {
			Validations.validate(artigoSelecionado);
			edicaoSelecionada.getArtigos().remove(artigoSelecionado);
			edicaoSelecionada.getArtigos().add(artigoSelecionado);
			Notification.show("success", "ARTIGO salvo com sucesso.");
		} catch (ValidationException e) {
			Validations.show(editArtigoWindow, "e1_errors", e.getErros());
		}

	}

	@Command
	public void excluirArtigo() {

		Messagebox.show("Excluir este ARTIGO?", "Biblio", Messagebox.YES
				| Messagebox.NO, Messagebox.QUESTION,
				new org.zkoss.zk.ui.event.EventListener<Event>() {
					@Override
					public void onEvent(Event e) {
						if (Messagebox.ON_YES.equals(e.getName())) {
							edicaoSelecionada.getArtigos().remove(
									artigoSelecionado);
							Notification.show("success",
									"ARTIGO excluído com sucesso.");
							editArtigoWindow.setVisible(false);
							BindUtils.postNotifyChange(null, null,
									EdicaoView.this, "edicoes");
						}
					}
				});

	}

	@Command
	@NotifyChange("edicoes")
	public void fechar() {
		editWindow.setVisible(false);
		pesquisar();
	}

	@Command
	public void fecharArtigo() {
		editArtigoWindow.setVisible(false);
	}

	@Command
	public void novaConsulta(@BindingParam("id") Long id) {

		Map<String, Long> args = new HashMap<String, Long>();
		args.put("consulta_id", new Long(0));
		args.put("cliente_id", id);
		Window win = (Window) Executions.getCurrent().createComponents(
				"/consulta/edit_consulta.zul", edicaoWindow, args);
		win.doModal();

	}

	@Command
	@NotifyChange("edicaoSelecionada")
	public void adicionarExemplares() {

		for (int i = 0; i < numeroNovosExemplaresIntbox.getValue(); i++) {
			ExemplarEdicao ee = new ExemplarEdicao();
			ee.setId(0L);
			ee.setEdicao(edicaoSelecionada);
			edicaoSelecionada.getExemplares().add(ee);
		}

	}

	@Command
	@NotifyChange("edicaoSelecionada")
	public void excluirExemplar(@BindingParam("ee") ExemplarEdicao ee) {
		edicaoSelecionada.getExemplares().remove(ee);
	}

	public List<Edicao> getEdicoes() {
		return edicoes;
	}

	public void setEdicoes(List<Edicao> edicoes) {
		this.edicoes = edicoes;
	}

	public List<Periodico> getPeriodicos() {
		return periodicos;
	}

	public void setPeriodicos(List<Periodico> periodicos) {
		this.periodicos = periodicos;
	}

	public Edicao getEdicaoSelecionada() {
		return edicaoSelecionada;
	}

	public void setEdicaoSelecionada(Edicao edicaoSelecionada) {
		this.edicaoSelecionada = edicaoSelecionada;
	}

	public TipoAquisicao[] getTiposAquisicoes() {
		return TipoAquisicao.values();
	}

	public Periodico getFiltroPeriodico() {
		return filtroPeriodico;
	}

	public void setFiltroPeriodico(Periodico filtroPeriodico) {
		this.filtroPeriodico = filtroPeriodico;
	}

	public ArtigoEdicao getArtigoSelecionado() {
		return artigoSelecionado;
	}

	public void setArtigoSelecionado(ArtigoEdicao artigoSelecionado) {
		this.artigoSelecionado = artigoSelecionado;
	}
	
	public SimNao[] getSimNao() {
		return SimNao.values();
	}


}
