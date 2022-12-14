package com.eficaztech.biblio.view;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.eficaztech.biblio.enums.TipoAquisicao;
import com.eficaztech.biblio.exceptions.EmprestimoEdicaoException;
import com.eficaztech.biblio.exceptions.EmprestimoLivroException;
import com.eficaztech.biblio.exceptions.EmprestimoMidiaException;
import com.eficaztech.biblio.exceptions.EmprestimoMonografiaException;
import com.eficaztech.biblio.model.Cliente;
import com.eficaztech.biblio.model.ClienteDao;
import com.eficaztech.biblio.model.Emprestimo;
import com.eficaztech.biblio.model.EmprestimoEdicao;
import com.eficaztech.biblio.model.EmprestimoEdicaoDao;
import com.eficaztech.biblio.model.EmprestimoLivro;
import com.eficaztech.biblio.model.EmprestimoLivroDao;
import com.eficaztech.biblio.model.EmprestimoMidia;
import com.eficaztech.biblio.model.EmprestimoMidiaDao;
import com.eficaztech.biblio.model.EmprestimoMonografia;
import com.eficaztech.biblio.model.EmprestimoMonografiaDao;
import com.eficaztech.biblio.relatorio.ComprovanteEmprestimoRel;
import com.eficaztech.biblio.util.Notification;

@Init(superclass = true)
@AfterCompose(superclass = true)
public class EmprestimoView extends View {

	private List<EmprestimoLivro> emprestimosLivros;
	private List<EmprestimoMidia> emprestimosMidias;
	private List<EmprestimoMonografia> emprestimosMonografias;
	private List<EmprestimoEdicao> emprestimosEdicoes;
	private List<String> tipos;

	private List<Cliente> clientes;
	private Cliente clienteSelecionado;

	private List<Emprestimo> emprestimos;

	private List<Emprestimo> emprestimosSelecionados;
	private String tipoSelecionado;

	@Wire
	Window livroWindow;

	@Wire
	Window clientesWindow;

	@Wire
	Listbox livroListbox;

	@Wire
	Intbox filtroClienteIntbox;

	@Wire("#clientesWindow #filtroClienteTextbox")
	Textbox filtroClienteTextbox;

	@Wire
	Checkbox filtroMostrarDevolvidosCheckbox;

	@Wire
	Checkbox consultaLocalCheckbox;

	@Wire
	Intbox exemplarIntbox;

	@Wire
	Label tituloListboxLabel;

	private boolean primeiroAcesso;

	@Override
	public void afterCompose() {

		primeiroAcesso = true;
		iniciarComboboxes();

		pesquisar();
	}

	private void iniciarComboboxes() {

		tipos = new ArrayList<String>();
		tipos.add("Livro");
		tipos.add("M??dia");
		tipos.add("Monografia");
		tipos.add("Peri??dico");

		setTipoSelecionado("Livro");

		BindUtils.postNotifyChange(null, null, EmprestimoView.this, "tipos");

	}

	@Command
	@NotifyChange("clientes")
	public void mostrarClientes() {
		filtroClienteTextbox.setValue("");
		clientes = ClienteDao.all();
		clienteSelecionado = null;
		clientesWindow.doModal();
	}

	@Command
	@NotifyChange("clientes")
	public void pesquisarClientes() {
		String filtro = filtroClienteTextbox.getValue();
		clientes = ClienteDao.find(filtro);
	}

	@Command
	@NotifyChange("clientes")
	public void limparClientes() {
		clientes = ClienteDao.all();
		clienteSelecionado = null;
		filtroClienteTextbox.setValue("");
	}

	@Command
	@NotifyChange("emprestimos")
	public void escolherCliente(@BindingParam("codigo") int codigo) {
		filtroClienteIntbox.setValue(codigo);
		clientesWindow.setVisible(false);
		pesquisar();
	}

	@Command
	@NotifyChange("emprestimos")
	public void pesquisar() {

		emprestimosSelecionados = null;

		long codigo = filtroClienteIntbox.getValue() == null ? 0
				: filtroClienteIntbox.getValue();

		if (codigo == 0) {
			if (!primeiroAcesso) {
				Notification.show("warning", "Escolha um usu??rio");
			}
			tituloListboxLabel.setValue("");
			filtroClienteIntbox.setFocus(true);
			primeiroAcesso = false;
			return;
		}

		Cliente cliente = ClienteDao.findByCodigo(codigo);
		if (cliente == null) {
			if (!primeiroAcesso) {
				Notification.show("warning", "Usu??rio '" + codigo
						+ "' n??o existe");
			}
			tituloListboxLabel.setValue("");
			filtroClienteIntbox.setFocus(true);
			primeiroAcesso = false;
			emprestimos = new ArrayList<>();
			return;
		}

		emprestimosLivros = EmprestimoLivroDao.findByCliente(cliente.getId(),
				filtroMostrarDevolvidosCheckbox.isChecked());

		emprestimosMidias = EmprestimoMidiaDao.findByCliente(cliente.getId(),
				filtroMostrarDevolvidosCheckbox.isChecked());

		emprestimosMonografias = EmprestimoMonografiaDao.findByCliente(
				cliente.getId(), filtroMostrarDevolvidosCheckbox.isChecked());

		emprestimosEdicoes = EmprestimoEdicaoDao.findByCliente(cliente.getId(),
				filtroMostrarDevolvidosCheckbox.isChecked());

		emprestimos = new ArrayList<Emprestimo>();
		emprestimos.addAll(emprestimosLivros);
		emprestimos.addAll(emprestimosMidias);
		emprestimos.addAll(emprestimosMonografias);
		emprestimos.addAll(emprestimosEdicoes);

		if (emprestimos.size() == 0) {
			Notification.show("warning",
					"N??o foi encontrado nenhum empr??stimo para '" + codigo
							+ " - " + cliente.getNome() + "'");
			tituloListboxLabel.setValue("");
			filtroClienteIntbox.setFocus(true);
			primeiroAcesso = false;
			return;
		}

		Comparator<? super Emprestimo> comparator = new Comparator<Emprestimo>() {

			@Override
			public int compare(Emprestimo o1, Emprestimo o2) {
				DateTime d1 = new DateTime(o1.getDataEmprestimo());
				DateTime d2 = new DateTime(o2.getDataEmprestimo());
				return Days.daysBetween(d1, d2).getDays();
			}
		};

		Collections.sort(emprestimos, comparator);

		tituloListboxLabel.setValue("Empr??stimos de " + cliente.getNome());
		filtroClienteIntbox.setFocus(true);
		primeiroAcesso = false;
	}

	@Command
	@NotifyChange({ "emprestimos" })
	public void emprestar() {

		long codigo_cliente = filtroClienteIntbox.getValue() == null ? 0
				: filtroClienteIntbox.getValue();
		long exemplar = exemplarIntbox.getValue() == null ? 0 : exemplarIntbox
				.getValue();

		if (codigo_cliente == 0 || exemplar == 0) {
			Notification.show("warning", "Escolha um cliente e um exemplar");
			return;
		}

		Cliente cliente = ClienteDao.findByCodigo(codigo_cliente);
		
		if (tipoSelecionado == null) {
			Notification.show("warning", "Tipo n??o informado corretamente");
			return;
		} else if (tipoSelecionado.equals("Livro")) {

			try {
				EmprestimoLivroDao.emprestar(cliente.getId(), exemplar,
						consultaLocalCheckbox.isChecked());
			} catch (EmprestimoLivroException e) {
				Notification.show("error", e.getMessage());
				e.printStackTrace();
			}

		} else if (tipoSelecionado.equals("M??dia")) {

			try {
				EmprestimoMidiaDao.emprestar(cliente.getId(), exemplar,
						consultaLocalCheckbox.isChecked());
			} catch (EmprestimoMidiaException e) {
				Notification.show("error", e.getMessage());
				e.printStackTrace();
			}

		} else if (tipoSelecionado.equals("Monografia")) {

			try {
				EmprestimoMonografiaDao.emprestar(cliente.getId(), exemplar,
						consultaLocalCheckbox.isChecked());
			} catch (EmprestimoMonografiaException e) {
				Notification.show("error", e.getMessage());
				e.printStackTrace();
			}

		} else if (tipoSelecionado.equals("Peri??dico")) {

			try {
				EmprestimoEdicaoDao.emprestar(cliente.getId(), exemplar,
						consultaLocalCheckbox.isChecked());
			} catch (EmprestimoEdicaoException e) {
				Notification.show("error", e.getMessage());
				e.printStackTrace();
			}

		} else {
			Notification.show("warning", "Tipo n??o informado corretamente");
			return;
		}

		emprestimosSelecionados = null;

		pesquisar();

	}

	@Command
	@NotifyChange({ "emprestimos" })
	public void renovar() {

		if (emprestimosSelecionados == null
				|| emprestimosSelecionados.size() == 0) {
			Notification.show("warning",
					"Selecione ao menos um empr??stimo para renovar");
			return;
		}

		for (Emprestimo emprestimo : emprestimosSelecionados) {

			if (emprestimo instanceof EmprestimoLivro) {
				try {
					EmprestimoLivroDao.renovar(emprestimo.getId());
				} catch (EmprestimoLivroException e) {
					Notification.show("error", e.getMessage());
					e.printStackTrace();
				}
			} else if (emprestimo instanceof EmprestimoMidia) {
				try {
					EmprestimoMidiaDao.renovar(emprestimo.getId());
				} catch (EmprestimoMidiaException e) {
					Notification.show("error", e.getMessage());
					e.printStackTrace();
				}
			} else if (emprestimo instanceof EmprestimoMonografia) {
				try {
					EmprestimoMonografiaDao.renovar(emprestimo.getId());
				} catch (EmprestimoMonografiaException e) {
					Notification.show("error", e.getMessage());
					e.printStackTrace();
				}
			} else if (emprestimo instanceof EmprestimoEdicao) {
				try {
					EmprestimoEdicaoDao.renovar(emprestimo.getId());
				} catch (EmprestimoEdicaoException e) {
					Notification.show("error", e.getMessage());
					e.printStackTrace();
				}
			}

		}

		emprestimosSelecionados = null;

		pesquisar();

	}

	@Command
	@NotifyChange({ "emprestimos" })
	public void devolver() {
		if (emprestimosSelecionados == null
				|| emprestimosSelecionados.size() == 0) {
			Notification.show("warning",
					"Selecione ao menos um empr??stimo para devolver");
			return;
		}

		for (Emprestimo emprestimo : emprestimosSelecionados) {

			if (emprestimo instanceof EmprestimoLivro) {
				try {
					EmprestimoLivroDao.devolver(emprestimo.getId());
				} catch (EmprestimoLivroException e) {
					Notification.show("error", e.getMessage());
					e.printStackTrace();
				}
			} else if (emprestimo instanceof EmprestimoMidia) {
				try {
					EmprestimoMidiaDao.devolver(emprestimo.getId());
				} catch (EmprestimoMidiaException e) {
					Notification.show("error", e.getMessage());
					e.printStackTrace();
				}
			} else if (emprestimo instanceof EmprestimoMonografia) {
				try {
					EmprestimoMonografiaDao.devolver(emprestimo.getId());
				} catch (EmprestimoMonografiaException e) {
					Notification.show("error", e.getMessage());
					e.printStackTrace();
				}
			} else if (emprestimo instanceof EmprestimoEdicao) {
				try {
					EmprestimoEdicaoDao.devolver(emprestimo.getId());
				} catch (EmprestimoEdicaoException e) {
					Notification.show("error", e.getMessage());
					e.printStackTrace();
				}
			}

		}

		pesquisar();
	}

	@Command
	public void imprimir() {
		ComprovanteEmprestimoRel.rel(livroWindow, emprestimos);
	}

	public List<Emprestimo> getEmprestimos() {
		return emprestimos;
	}

	public void setEmprestimos(List<Emprestimo> emprestimos) {
		this.emprestimos = emprestimos;
	}

	public List<Emprestimo> getEmprestimosSelecionados() {
		return emprestimosSelecionados;
	}

	public void setEmprestimosSelecionados(
			List<Emprestimo> emprestimosSelecionados) {
		this.emprestimosSelecionados = emprestimosSelecionados;
	}

	public TipoAquisicao[] getTiposAquisicoes() {
		return TipoAquisicao.values();
	}

	public List<String> getTipos() {
		return tipos;
	}

	public void setTipos(List<String> tipos) {
		this.tipos = tipos;
	}

	public String getTipoSelecionado() {
		return tipoSelecionado;
	}

	public void setTipoSelecionado(String tipoSelecionado) {
		this.tipoSelecionado = tipoSelecionado;
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

}
