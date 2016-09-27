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
import com.eficaztech.biblio.enums.TipoMidia;
import com.eficaztech.biblio.model.ExemplarMidia;
import com.eficaztech.biblio.model.Midia;
import com.eficaztech.biblio.model.MidiaDao;
import com.eficaztech.biblio.util.Notification;
import com.eficaztech.biblio.util.Security;
import com.eficaztech.biblio.util.ValidationException;
import com.eficaztech.biblio.util.Validations;

@Init(superclass = true)
@AfterCompose(superclass = true)
public class MidiaView extends View {

	private List<Midia> midias;

	private Midia midiaSelecionada;

	@Wire
	Window edicaoWindow;

	@Wire
	Window editWindow;

	@Wire("#include #editConsultaWindow")
	Window editConsultaWindow;

	@Wire
	Listbox midiaListbox;

	@Wire
	Textbox filtroTituloTextbox;

	@Wire("#editWindow #excluirButton")
	Button excluirButton;

	@Wire("#editWindow #tipoMidiaCombobox")
	Combobox tipoMidiaCombobox;

	@Wire("#editWindow #numeroNovosExemplaresIntbox")
	Intbox numeroNovosExemplaresIntbox;

	@Wire("#editWindow #tabbox")
	Tabbox tabbox;

	@Override
	public void afterCompose() {
		
		if (!Security.isAdmOrBib()) return;
		
		limpar();
		
	}

	@Command
	@NotifyChange("midias")
	public void pesquisar() {
		filtroTituloTextbox.setFocus(true);
		midias = MidiaDao.find(filtroTituloTextbox.getValue());
	}

	@Command
	@NotifyChange("midias")
	public void limpar() {
		filtroTituloTextbox.setValue("");
		filtroTituloTextbox.setFocus(true);
		midias = MidiaDao.all();
	}

	@Command
	@NotifyChange("midiaSelecionada")
	public void novo() {
		midiaSelecionada = new Midia();
		editWindow.setTitle("Nova Mídia");
		editWindow.doModal();
		excluirButton.setDisabled(true);
		tabbox.setSelectedIndex(0);
		tipoMidiaCombobox.setFocus(true);
	}

	@Command
	@NotifyChange("midiaSelecionada")
	public void editar(@BindingParam("id") Long id) {
		midiaSelecionada = MidiaDao.find(id);
		editWindow.setTitle("Editar Mídia");
		editWindow.doModal();
		excluirButton.setDisabled(false);
		tabbox.setSelectedIndex(0);
		tipoMidiaCombobox.setFocus(true);
	}

	@Command
	@NotifyChange("midias")
	public void salvar() {

		try {
			MidiaDao.save(midiaSelecionada);
			Notification.show("success", "MÍDIA salva com sucesso.");
		} catch (ValidationException e) {
			Validations.show(editWindow, "e_errors", e.getErros());
		}

	}

	@Command
	public void excluir() {

		Messagebox.show("Excluir esta MÍDIA?", "Biblio", Messagebox.YES
				| Messagebox.NO, Messagebox.QUESTION,
				new org.zkoss.zk.ui.event.EventListener<Event>() {
					@Override
					public void onEvent(Event e) {
						if (Messagebox.ON_YES.equals(e.getName())) {
							MidiaDao.delete(midiaSelecionada);
							Notification.show("success",
									"MÍDIA excluída com sucesso.");
							editWindow.setVisible(false);
							BindUtils.postNotifyChange(null, null,
									MidiaView.this, "midias");
							pesquisar();
						}
					}
				});

	}

	@Command
	@NotifyChange("midias")
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
				"/consulta/edit_consulta.zul", edicaoWindow, args);
		win.doModal();

	}

	@Command
	@NotifyChange("midiaSelecionada")
	public void adicionarExemplares() {

		for (int i = 0; i < numeroNovosExemplaresIntbox.getValue(); i++) {
			ExemplarMidia em = new ExemplarMidia();
			em.setId(0L);
			em.setMidia(midiaSelecionada);
			midiaSelecionada.getExemplares().add(em);
		}

	}

	@Command
	@NotifyChange("midiaSelecionada")
	public void excluirExemplar(@BindingParam("em") ExemplarMidia em) {
		midiaSelecionada.getExemplares().remove(em);
	}

	public List<Midia> getMidias() {
		return midias;
	}

	public void setMidias(List<Midia> midias) {
		this.midias = midias;
	}

	public Midia getMidiaSelecionada() {
		return midiaSelecionada;
	}

	public void setMidiaSelecionada(Midia midiaSelecionada) {
		this.midiaSelecionada = midiaSelecionada;
	}

	public TipoMidia[] getTiposMidias() {
		return TipoMidia.values();
	}
	
	public SimNao[] getSimNao() {
		return SimNao.values();
	}

}
