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
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.eficaztech.biblio.enums.SimNao;
import com.eficaztech.biblio.enums.TipoCliente;
import com.eficaztech.biblio.model.Cliente;
import com.eficaztech.biblio.model.ClienteDao;
import com.eficaztech.biblio.util.Notification;
import com.eficaztech.biblio.util.ValidationException;
import com.eficaztech.biblio.util.Validations;

@Init(superclass = true)
@AfterCompose(superclass = true)
public class ClienteView extends View {

	private List<Cliente> clientes;

	private Cliente clienteSelecionado;

	@Wire
	Window clienteWindow;

	@Wire
	Window editWindow;

	@Wire("#include #editConsultaWindow")
	Window editConsultaWindow;

	@Wire
	Listbox clienteListbox;

	@Wire
	Textbox filtroTextbox;

	@Wire("#editWindow #excluirButton")
	Button excluirButton;

	@Wire("#editWindow #codigoIntbox")
	Intbox codigoIntbox;

	@Override
	public void afterCompose() {
		limpar();
	}

	@Command
	@NotifyChange("clientes")
	public void pesquisar() {
		filtroTextbox.setFocus(true);
		clientes = ClienteDao.find(filtroTextbox.getValue());
	}

	@Command
	@NotifyChange("clientes")
	public void limpar() {
		filtroTextbox.setValue("");
		filtroTextbox.setFocus(true);
		clientes = ClienteDao.all();
		
	}

	@Command
	@NotifyChange("clienteSelecionado")
	public void novo() {
		clienteSelecionado = new Cliente();
		editWindow.setTitle("Novo Usuário da Biblioteca");
		editWindow.doModal();
		excluirButton.setDisabled(true);
		codigoIntbox.setFocus(true);
	}

	@Command
	@NotifyChange("clienteSelecionado")
	public void editar(@BindingParam("id") Long id) {
		clienteSelecionado = ClienteDao.find(id);
		editWindow.setTitle("Editar Usuário da Biblioteca");
		editWindow.doModal();
		excluirButton.setDisabled(false);
		codigoIntbox.setFocus(true);
	}

	@Command
	@NotifyChange("clientes")
	public void salvar() {
		try {
			ClienteDao.save(clienteSelecionado);
			Notification.show("success",
					"USUÁRIO DA BIBLIOTECA salvo com sucesso.");
			clienteSelecionado = null;
		} catch (ValidationException e) {
			Validations.show(editWindow, "e_errors", e.getErros());
		}

		editWindow.setVisible(false);
		pesquisar();
	}

	@Command
	public void excluir() {

		Messagebox.show("Excluir este USUÁRIO DA BIBLIOTECA?", "Biblio",
				Messagebox.YES | Messagebox.NO, Messagebox.QUESTION,
				new org.zkoss.zk.ui.event.EventListener<Event>() {
					@Override
					public void onEvent(Event e) {
						if (Messagebox.ON_YES.equals(e.getName())) {
							ClienteDao.delete(clienteSelecionado);
							Notification
									.show("success",
											"USUÁRIO DA BIBLIOTECA excluído com sucesso.");
							editWindow.setVisible(false);
							BindUtils.postNotifyChange(null, null,
									ClienteView.this, "clientes");
							pesquisar();
						}
					}
				});

	}

	@Command
	@NotifyChange("clientes")
	public void cancelar() {
		editWindow.setVisible(false);
		pesquisar();
	}

	public List<Cliente> getClientes() {
		return clientes;
	}

	public void setClientes(List<Cliente> clientes) {
		this.clientes = clientes;
	}

	public Cliente getClienteSelecionado() {
		return clienteSelecionado;
	}

	public void setClienteSelecionado(Cliente clienteSelecionado) {
		this.clienteSelecionado = clienteSelecionado;
	}

	public TipoCliente[] getTiposClientes() {
		return TipoCliente.values();
	}

	public SimNao[] getAtivos() {
		return SimNao.values();
	}

}
