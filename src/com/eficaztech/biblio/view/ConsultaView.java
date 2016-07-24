package com.eficaztech.biblio.view;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zhtml.Messagebox;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.avaje.ebean.EbeanServerFactory;
import com.eficaztech.biblio.model.ArtigoEdicao;
import com.eficaztech.biblio.model.ArtigoEdicaoDao;
import com.eficaztech.biblio.model.Livro;
import com.eficaztech.biblio.model.LivroDao;
import com.eficaztech.biblio.model.Midia;
import com.eficaztech.biblio.model.MidiaDao;
import com.eficaztech.biblio.model.Monografia;
import com.eficaztech.biblio.model.MonografiaDao;
import com.eficaztech.biblio.util.BiblioServer;

public class ConsultaView {

	private List<Livro> livros;
	private List<Monografia> monografias;
	private List<Midia> midias;
	private List<ArtigoEdicao> artigos;

	@Wire
	Window livroWindow;

	@Wire
	Listbox livroListbox;

	@Wire
	Textbox filtroTextbox;

	@Wire
	Tab livrosTab;

	@Wire
	Tab monografiasTab;

	@Wire
	Tab periodicosTab;

	@Wire
	Tab midiasTab;

	@AfterCompose
	public void afterCompose(@ContextParam(ContextType.VIEW) Component view) {

		try {
			// pega o nome na url depois da ultima / para ter o nome da empresa
			String url = Executions.getCurrent().getDesktop().getRequestPath();
			String[] tokens = url.split("/");
			String empresa = tokens[tokens.length - 2];
			BiblioServer.ebean = EbeanServerFactory.create(empresa);
		} catch (Exception e) {
			Messagebox.show("Não foi possível entrar no sistema", "Biblio",
					Messagebox.OK, Messagebox.EXCLAMATION);
			return;
		}

		Selectors.wireComponents(view, this, false);

		ultimos100();

	}

	private void ultimos100() {
		filtroTextbox.setFocus(true);
		filtroTextbox.setPlaceholder("Obras adiquiridas mais recentemente");
		livros = LivroDao.ultimos100ParaConsultaPublica();
		setMonografias(MonografiaDao.ultimos100ParaConsultaPublica());
		setMidias(MidiaDao.ultimos100ParaConsultaPublica());
		setArtigos(ArtigoEdicaoDao.ultimos100ParaConsultaPublica());
		livrosTab.setLabel("Livros: " + livros.size());
		monografiasTab.setLabel("Monografias: " + getMonografias().size());
		midiasTab.setLabel("Mídias: " + getMidias().size());
		periodicosTab.setLabel("Artigos de Periódicos: " + getArtigos().size());
	}

	@Command
	@NotifyChange({ "livros", "monografias", "midias", "artigos" })
	public void pesquisar() {
		filtroTextbox.setFocus(true);
		if (filtroTextbox.getValue().equals("")) {
			livros = LivroDao.ultimos100ParaConsultaPublica();
			setMonografias(MonografiaDao.ultimos100ParaConsultaPublica());
			setMidias(MidiaDao.ultimos100ParaConsultaPublica());
			setArtigos(ArtigoEdicaoDao.ultimos100ParaConsultaPublica());
		} else {
			livros = LivroDao.findParaConsultaPublica(filtroTextbox.getValue());
			setMonografias(MonografiaDao.findParaConsultaPublica(filtroTextbox
					.getValue()));
			setMidias(MidiaDao
					.findParaConsultaPublica(filtroTextbox.getValue()));
			setArtigos(ArtigoEdicaoDao.findParaConsultaPublica(filtroTextbox
					.getValue()));
		}
		livrosTab.setLabel("Livros: " + livros.size());
		monografiasTab.setLabel("Monografias: " + getMonografias().size());
		midiasTab.setLabel("Mídias: " + getMidias().size());
		periodicosTab.setLabel("Artigos de Periódicos: " + getArtigos().size());
	}

	@Command
	@NotifyChange({ "livros", "monografias", "midias", "artigos" })
	public void limpar() {
		filtroTextbox.setValue("");
		filtroTextbox.setFocus(true);
		livros = LivroDao.ultimos100ParaConsultaPublica();
		setMonografias(MonografiaDao.ultimos100ParaConsultaPublica());
		setMidias(MidiaDao.ultimos100ParaConsultaPublica());
		setArtigos(ArtigoEdicaoDao.ultimos100ParaConsultaPublica());
		livrosTab.setLabel("Livros: " + livros.size());
		monografiasTab.setLabel("Monografias: " + getMonografias().size());
		midiasTab.setLabel("Mídias: " + getMidias().size());
		periodicosTab.setLabel("Artigos de Periódicos: " + getArtigos().size());
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

	public List<Livro> getLivros() {
		return livros;
	}

	public void setLivros(List<Livro> livros) {
		this.livros = livros;
	}

	public List<Monografia> getMonografias() {
		return monografias;
	}

	public void setMonografias(List<Monografia> monografias) {
		this.monografias = monografias;
	}

	public List<Midia> getMidias() {
		return midias;
	}

	public void setMidias(List<Midia> midias) {
		this.midias = midias;
	}

	public List<ArtigoEdicao> getArtigos() {
		return artigos;
	}

	public void setArtigos(List<ArtigoEdicao> artigos) {
		this.artigos = artigos;
	}

}
