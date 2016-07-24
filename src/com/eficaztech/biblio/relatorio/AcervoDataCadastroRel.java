package com.eficaztech.biblio.relatorio;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Window;

import com.eficaztech.biblio.model.Empresa;
import com.eficaztech.biblio.model.EmpresaDao;
import com.eficaztech.biblio.model.ExemplarEdicao;
import com.eficaztech.biblio.model.ExemplarEdicaoDao;
import com.eficaztech.biblio.model.ExemplarLivro;
import com.eficaztech.biblio.model.ExemplarLivroDao;
import com.eficaztech.biblio.model.ExemplarMidia;
import com.eficaztech.biblio.model.ExemplarMidiaDao;
import com.eficaztech.biblio.model.ExemplarMonografia;
import com.eficaztech.biblio.model.ExemplarMonografiaDao;
import com.eficaztech.biblio.util.Notification;
import com.eficaztech.biblio.view.View;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

@Init(superclass = true)
@AfterCompose(superclass = true)
public class AcervoDataCadastroRel extends View {

	private List<String> tipos;
	private String tipoSelecionado;

	Window editWindow;

	@Wire("#editWindow #tiposCombobox")
	Combobox tiposCombobox;

	@Wire("#editWindow #inicioDatebox")
	Datebox inicioDatebox;

	@Wire("#editWindow #fimDatebox")
	Datebox fimDatebox;

	@Override
	public void afterCompose() {

		tipos = new ArrayList<String>();
		tipos.add("Livro");
		tipos.add("Mídia");
		tipos.add("Monografia");
		tipos.add("Periódico");

		setTipoSelecionado("Livro");
		tiposCombobox.setFocus(true);

		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());

		cal.set(Calendar.DAY_OF_MONTH, 1);
		inicioDatebox.setValue(cal.getTime());

		cal.set(Calendar.DAY_OF_MONTH,
				cal.getActualMaximum(Calendar.DAY_OF_MONTH));
		fimDatebox.setValue(cal.getTime());

		BindUtils.postNotifyChange(null, null, AcervoDataCadastroRel.this,
				"tipos");

	}

	@Command
	public void imprimir() {
		if (tipoSelecionado == null) {
			Notification.show("warning", "Tipo inválido");
			return;
		} else if (tipoSelecionado.equals("Livro")) {
			imprimirLivro();
		} else if (tipoSelecionado.equals("Monografia")) {
			imprimirMonografia();
		} else if (tipoSelecionado.equals("Periódico")) {
			imprimirPeriodico();
		} else if (tipoSelecionado.equals("Mídia")) {
			imprimirMidia();
		} else {
			Notification.show("warning", "Tipo inválido");
			return;
		}

	}

	public void imprimirLivro() {

		try {

			Font fontcab = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL);
			Font fontcabb = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);

			File temp = File.createTempFile("acervo", ".pdf");

			Document document = new Document();

			PdfWriter.getInstance(document, new FileOutputStream(temp));
			document.setPageSize(PageSize.A4);
			document.open();

			Empresa empresa = EmpresaDao.findFirst();

			// cabecalho
			PdfPTable table = new PdfPTable(2);
			table.setWidthPercentage(100);
			float[] widths = { 1, 6 };
			table.setWidths(widths);
			PdfPCell empresaCell = new PdfPCell(new Paragraph(
					empresa.getNome(), fontcab));
			PdfPCell bibliotecaCell = new PdfPCell(new Paragraph(
					empresa.getBiblioteca(), fontcab));
			PdfPCell telefoneCell = new PdfPCell(new Paragraph("Telefone: "
					+ empresa.getTelefones(), fontcab));
			PdfPCell titCell = new PdfPCell(new Paragraph(
					"LIVROS POR DATA DE CADASTRO", fontcabb));

			SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
			PdfPCell dataCell = new PdfPCell(new Paragraph("Entre "
					+ format.format(inicioDatebox.getValue()) + " e "
					+ format.format(fimDatebox.getValue()), fontcab));

			empresaCell.setColspan(2);
			empresaCell.setBorder(PdfPCell.TOP);
			empresaCell.setPaddingTop(10);
			empresaCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			bibliotecaCell.setBorder(PdfPCell.NO_BORDER);
			bibliotecaCell.setColspan(2);
			bibliotecaCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			telefoneCell.setColspan(2);
			telefoneCell.setBorder(PdfPCell.NO_BORDER);
			telefoneCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			titCell.setColspan(2);
			titCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			titCell.setBorder(PdfPCell.NO_BORDER);
			dataCell.setColspan(2);
			dataCell.setBorder(PdfPCell.NO_BORDER);
			dataCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			dataCell.setPaddingBottom(2);
			dataCell.setPaddingBottom(10);
			dataCell.setBorder(PdfPCell.BOTTOM);

			table.addCell(empresaCell);
			table.addCell(bibliotecaCell);
			table.addCell(telefoneCell);
			table.addCell(titCell);
			table.addCell(dataCell);

			document.add(table);

			PdfPTable table2;

			table2 = new PdfPTable(4);
			float[] widths1 = { 1, 4, 2, 1 };
			table2.setWidths(widths1);

			table2.setWidthPercentage(100);
			table2.setPaddingTop(10);

			Font fonth = new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD);
			Font font = new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL);

			PdfPCell registroTit = new PdfPCell(
					new Paragraph("Registro", fonth));
			registroTit.setBackgroundColor(BaseColor.LIGHT_GRAY);

			PdfPCell tituloTit = new PdfPCell(new Paragraph("Título", fonth));
			tituloTit.setBackgroundColor(BaseColor.LIGHT_GRAY);

			PdfPCell classificacaoTit = new PdfPCell(new Paragraph(
					"Classificação/Cutter/Código", fonth));
			classificacaoTit.setBackgroundColor(BaseColor.LIGHT_GRAY);
			classificacaoTit.setHorizontalAlignment(Element.ALIGN_LEFT);

			PdfPCell cadastroTit = new PdfPCell(new Paragraph(
					"Data de Cadastro", fonth));
			cadastroTit.setBackgroundColor(BaseColor.LIGHT_GRAY);
			cadastroTit.setHorizontalAlignment(Element.ALIGN_CENTER);

			table2.addCell(registroTit);
			table2.addCell(tituloTit);
			table2.addCell(classificacaoTit);
			table2.addCell(cadastroTit);

			Date inicio = inicioDatebox.getValue() == null ? new Date()
					: inicioDatebox.getValue();
			Date fim = fimDatebox.getValue() == null ? new Date() : fimDatebox
					.getValue();

			List<ExemplarLivro> exemplares = ExemplarLivroDao.findBetween(
					inicio, fim);

			for (ExemplarLivro exemplar : exemplares) {

				Paragraph registro = new Paragraph(exemplar.getId() + "", font);
				Paragraph titulo = new Paragraph(exemplar.getLivro()
						.getTituloCompleto()
						+ "\nAutores: "
						+ exemplar.getLivro().getAutores()
						+ "\nLocal: "
						+ exemplar.getLivro().getLocal().getNome()
						+ " - Editora: "
						+ exemplar.getLivro().getEditora().getNome()
						+ " - Ano: " + exemplar.getLivro().getAno(), font);
				Paragraph classificacao = new Paragraph(exemplar.getLivro()
						.getClassificacao()
						+ " / "
						+ exemplar.getLivro().getCutter()
						+ " / "
						+ exemplar.getLivro().getId(), font);
				Paragraph cadastro = new Paragraph(exemplar.getDataCadastroBR()
						+ "", font);

				PdfPCell registroCell = new PdfPCell(registro);
				registroCell.setHorizontalAlignment(Element.ALIGN_LEFT);
				PdfPCell tituloCell = new PdfPCell(titulo);
				tituloCell.setHorizontalAlignment(Element.ALIGN_LEFT);
				PdfPCell classificacaoCell = new PdfPCell(classificacao);
				classificacaoCell.setHorizontalAlignment(Element.ALIGN_LEFT);
				PdfPCell cadastroCell = new PdfPCell(cadastro);
				cadastroCell.setHorizontalAlignment(Element.ALIGN_CENTER);

				table2.addCell(registroCell);
				table2.addCell(tituloCell);
				table2.addCell(classificacaoCell);
				table2.addCell(cadastroCell);

			}
			document.add(table2);

			document.add(new Paragraph("Total de exemplares: "
					+ exemplares.size(), font));

			document.close();

			Hashtable<String, String> h = new Hashtable<String, String>();
			h.put("File", temp.getAbsolutePath());

			Executions.getCurrent().createComponents("/pdf/pdf.zul",
					editWindow, h);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void imprimirMonografia() {

		try {

			Font fontcab = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL);
			Font fontcabb = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);

			File temp = File.createTempFile("acervo", ".pdf");

			Document document = new Document();

			PdfWriter.getInstance(document, new FileOutputStream(temp));
			document.setPageSize(PageSize.A4);
			document.open();

			Empresa empresa = EmpresaDao.findFirst();

			// cabecalho
			PdfPTable table = new PdfPTable(2);
			table.setWidthPercentage(100);
			float[] widths = { 1, 6 };
			table.setWidths(widths);
			PdfPCell empresaCell = new PdfPCell(new Paragraph(
					empresa.getNome(), fontcab));
			PdfPCell bibliotecaCell = new PdfPCell(new Paragraph(
					empresa.getBiblioteca(), fontcab));
			PdfPCell telefoneCell = new PdfPCell(new Paragraph("Telefone: "
					+ empresa.getTelefones(), fontcab));
			PdfPCell titCell = new PdfPCell(new Paragraph(
					"MONOGRAFIAS POR DATA DE CADASTRO", fontcabb));

			SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
			PdfPCell dataCell = new PdfPCell(new Paragraph("Entre "
					+ format.format(inicioDatebox.getValue()) + " e "
					+ format.format(fimDatebox.getValue()), fontcab));

			empresaCell.setColspan(2);
			empresaCell.setBorder(PdfPCell.TOP);
			empresaCell.setPaddingTop(10);
			empresaCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			bibliotecaCell.setBorder(PdfPCell.NO_BORDER);
			bibliotecaCell.setColspan(2);
			bibliotecaCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			telefoneCell.setColspan(2);
			telefoneCell.setBorder(PdfPCell.NO_BORDER);
			telefoneCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			titCell.setColspan(2);
			titCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			titCell.setBorder(PdfPCell.NO_BORDER);
			dataCell.setColspan(2);
			dataCell.setBorder(PdfPCell.NO_BORDER);
			dataCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			dataCell.setPaddingBottom(2);
			dataCell.setPaddingBottom(10);
			dataCell.setBorder(PdfPCell.BOTTOM);

			table.addCell(empresaCell);
			table.addCell(bibliotecaCell);
			table.addCell(telefoneCell);
			table.addCell(titCell);
			table.addCell(dataCell);

			document.add(table);

			PdfPTable table2;

			table2 = new PdfPTable(4);
			float[] widths1 = { 1, 4, 2, 1 };
			table2.setWidths(widths1);

			table2.setWidthPercentage(100);
			table2.setPaddingTop(10);

			Font fonth = new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD);
			Font font = new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL);

			PdfPCell registroTit = new PdfPCell(
					new Paragraph("Registro", fonth));
			registroTit.setBackgroundColor(BaseColor.LIGHT_GRAY);

			PdfPCell tituloTit = new PdfPCell(new Paragraph("Título", fonth));
			tituloTit.setBackgroundColor(BaseColor.LIGHT_GRAY);

			PdfPCell classificacaoTit = new PdfPCell(new Paragraph(
					"Classificação/Cutter/Código", fonth));
			classificacaoTit.setBackgroundColor(BaseColor.LIGHT_GRAY);
			classificacaoTit.setHorizontalAlignment(Element.ALIGN_CENTER);

			PdfPCell cadastroTit = new PdfPCell(new Paragraph(
					"Data de Cadastro", fonth));
			cadastroTit.setBackgroundColor(BaseColor.LIGHT_GRAY);
			cadastroTit.setHorizontalAlignment(Element.ALIGN_CENTER);

			table2.addCell(registroTit);
			table2.addCell(tituloTit);
			table2.addCell(classificacaoTit);
			table2.addCell(cadastroTit);

			Date inicio = inicioDatebox.getValue() == null ? new Date()
					: inicioDatebox.getValue();
			Date fim = fimDatebox.getValue() == null ? new Date() : fimDatebox
					.getValue();

			List<ExemplarMonografia> exemplares = ExemplarMonografiaDao
					.findBetween(inicio, fim);

			for (ExemplarMonografia exemplar : exemplares) {

				Paragraph registro = new Paragraph(exemplar.getId() + "", font);
				Paragraph titulo = new Paragraph(exemplar.getMonografia()
						.getTituloCompleto()
						+ "\nAutores: "
						+ exemplar.getMonografia().getAutores()
						+ "\nLocal: "
						+ exemplar.getMonografia().getLocal().getNome()
						+ " - Editora: "
						+ exemplar.getMonografia().getEditora().getNome()
						+ " - Ano: " + exemplar.getMonografia().getAno(), font);
				Paragraph classificacao = new Paragraph(exemplar
						.getMonografia().getClassificacao()
						+ " / "
						+ exemplar.getMonografia().getCutter()
						+ " / "
						+ exemplar.getMonografia().getId(), font);
				Paragraph cadastro = new Paragraph(exemplar.getDataCadastroBR()
						+ "", font);

				PdfPCell registroCell = new PdfPCell(registro);
				registroCell.setHorizontalAlignment(Element.ALIGN_LEFT);
				PdfPCell tituloCell = new PdfPCell(titulo);
				tituloCell.setHorizontalAlignment(Element.ALIGN_LEFT);
				PdfPCell classificacaoCell = new PdfPCell(classificacao);
				classificacaoCell.setHorizontalAlignment(Element.ALIGN_LEFT);
				PdfPCell cadastroCell = new PdfPCell(cadastro);
				cadastroCell.setHorizontalAlignment(Element.ALIGN_CENTER);

				table2.addCell(registroCell);
				table2.addCell(tituloCell);
				table2.addCell(classificacaoCell);
				table2.addCell(cadastroCell);

			}
			document.add(table2);

			document.add(new Paragraph("Total de exemplares: "
					+ exemplares.size(), font));

			document.close();

			Hashtable<String, String> h = new Hashtable<String, String>();
			h.put("File", temp.getAbsolutePath());

			Executions.getCurrent().createComponents("/pdf/pdf.zul",
					editWindow, h);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void imprimirPeriodico() {

		try {

			Font fontcab = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL);
			Font fontcabb = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);

			File temp = File.createTempFile("acervo", ".pdf");

			Document document = new Document();

			PdfWriter.getInstance(document, new FileOutputStream(temp));
			document.setPageSize(PageSize.A4);
			document.open();

			Empresa empresa = EmpresaDao.findFirst();

			// cabecalho
			PdfPTable table = new PdfPTable(2);
			table.setWidthPercentage(100);
			float[] widths = { 1, 6 };
			table.setWidths(widths);
			PdfPCell empresaCell = new PdfPCell(new Paragraph(
					empresa.getNome(), fontcab));
			PdfPCell bibliotecaCell = new PdfPCell(new Paragraph(
					empresa.getBiblioteca(), fontcab));
			PdfPCell telefoneCell = new PdfPCell(new Paragraph("Telefone: "
					+ empresa.getTelefones(), fontcab));
			PdfPCell titCell = new PdfPCell(new Paragraph(
					"PERIÓDICOS POR DATA DE CADASTRO", fontcabb));

			SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
			PdfPCell dataCell = new PdfPCell(new Paragraph("Entre "
					+ format.format(inicioDatebox.getValue()) + " e "
					+ format.format(fimDatebox.getValue()), fontcab));

			empresaCell.setColspan(2);
			empresaCell.setBorder(PdfPCell.TOP);
			empresaCell.setPaddingTop(10);
			empresaCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			bibliotecaCell.setBorder(PdfPCell.NO_BORDER);
			bibliotecaCell.setColspan(2);
			bibliotecaCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			telefoneCell.setColspan(2);
			telefoneCell.setBorder(PdfPCell.NO_BORDER);
			telefoneCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			titCell.setColspan(2);
			titCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			titCell.setBorder(PdfPCell.NO_BORDER);
			dataCell.setColspan(2);
			dataCell.setBorder(PdfPCell.NO_BORDER);
			dataCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			dataCell.setPaddingBottom(2);
			dataCell.setPaddingBottom(10);
			dataCell.setBorder(PdfPCell.BOTTOM);

			table.addCell(empresaCell);
			table.addCell(bibliotecaCell);
			table.addCell(telefoneCell);
			table.addCell(titCell);
			table.addCell(dataCell);

			document.add(table);

			PdfPTable table2;

			table2 = new PdfPTable(4);
			float[] widths1 = { 1, 4, 2, 1 };
			table2.setWidths(widths1);

			table2.setWidthPercentage(100);
			table2.setPaddingTop(10);

			Font fonth = new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD);
			Font font = new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL);

			PdfPCell registroTit = new PdfPCell(
					new Paragraph("Registro", fonth));
			registroTit.setBackgroundColor(BaseColor.LIGHT_GRAY);

			PdfPCell tituloTit = new PdfPCell(new Paragraph("Título", fonth));
			tituloTit.setBackgroundColor(BaseColor.LIGHT_GRAY);

			PdfPCell classificacaoTit = new PdfPCell(new Paragraph(
					"Volume/Número/Período/Código", fonth));
			classificacaoTit.setBackgroundColor(BaseColor.LIGHT_GRAY);
			classificacaoTit.setHorizontalAlignment(Element.ALIGN_CENTER);

			PdfPCell cadastroTit = new PdfPCell(new Paragraph(
					"Data de Cadastro", fonth));
			cadastroTit.setBackgroundColor(BaseColor.LIGHT_GRAY);
			cadastroTit.setHorizontalAlignment(Element.ALIGN_CENTER);

			table2.addCell(registroTit);
			table2.addCell(tituloTit);
			table2.addCell(classificacaoTit);
			table2.addCell(cadastroTit);

			Date inicio = inicioDatebox.getValue() == null ? new Date()
					: inicioDatebox.getValue();
			Date fim = fimDatebox.getValue() == null ? new Date() : fimDatebox
					.getValue();

			List<ExemplarEdicao> exemplares = ExemplarEdicaoDao.findBetween(
					inicio, fim);

			for (ExemplarEdicao exemplar : exemplares) {

				Paragraph registro = new Paragraph(exemplar.getId() + "", font);
				Paragraph titulo = new Paragraph(exemplar.getEdicao()
						.getTituloCompleto(), font);
				Paragraph classificacao = new Paragraph(exemplar.getEdicao()
						.getVolume()
						+ " / "
						+ exemplar.getEdicao().getNumero()
						+ " / "
						+ exemplar.getEdicao().getPeriodo()
						+ " / "
						+ exemplar.getEdicao().getId(), font);
				Paragraph cadastro = new Paragraph(exemplar.getDataCadastroBR()
						+ "", font);

				PdfPCell registroCell = new PdfPCell(registro);
				registroCell.setHorizontalAlignment(Element.ALIGN_LEFT);
				PdfPCell tituloCell = new PdfPCell(titulo);
				tituloCell.setHorizontalAlignment(Element.ALIGN_LEFT);
				PdfPCell classificacaoCell = new PdfPCell(classificacao);
				classificacaoCell.setHorizontalAlignment(Element.ALIGN_LEFT);
				PdfPCell cadastroCell = new PdfPCell(cadastro);
				cadastroCell.setHorizontalAlignment(Element.ALIGN_CENTER);

				table2.addCell(registroCell);
				table2.addCell(tituloCell);
				table2.addCell(classificacaoCell);
				table2.addCell(cadastroCell);

			}
			document.add(table2);

			document.add(new Paragraph("Total de exemplares: "
					+ exemplares.size(), font));

			document.close();

			Hashtable<String, String> h = new Hashtable<String, String>();
			h.put("File", temp.getAbsolutePath());

			Executions.getCurrent().createComponents("/pdf/pdf.zul",
					editWindow, h);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void imprimirMidia() {

		try {

			Font fontcab = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL);
			Font fontcabb = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);

			File temp = File.createTempFile("acervo", ".pdf");

			Document document = new Document();

			PdfWriter.getInstance(document, new FileOutputStream(temp));
			document.setPageSize(PageSize.A4);
			document.open();

			Empresa empresa = EmpresaDao.findFirst();

			// cabecalho
			PdfPTable table = new PdfPTable(2);
			table.setWidthPercentage(100);
			float[] widths = { 1, 6 };
			table.setWidths(widths);
			PdfPCell empresaCell = new PdfPCell(new Paragraph(
					empresa.getNome(), fontcab));
			PdfPCell bibliotecaCell = new PdfPCell(new Paragraph(
					empresa.getBiblioteca(), fontcab));
			PdfPCell telefoneCell = new PdfPCell(new Paragraph("Telefone: "
					+ empresa.getTelefones(), fontcab));
			PdfPCell titCell = new PdfPCell(new Paragraph(
					"MÍDIAS POR DATA DE CADASTRO", fontcabb));

			SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
			PdfPCell dataCell = new PdfPCell(new Paragraph("Entre "
					+ format.format(inicioDatebox.getValue()) + " e "
					+ format.format(fimDatebox.getValue()), fontcab));

			empresaCell.setColspan(2);
			empresaCell.setBorder(PdfPCell.TOP);
			empresaCell.setPaddingTop(10);
			empresaCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			bibliotecaCell.setBorder(PdfPCell.NO_BORDER);
			bibliotecaCell.setColspan(2);
			bibliotecaCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			telefoneCell.setColspan(2);
			telefoneCell.setBorder(PdfPCell.NO_BORDER);
			telefoneCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			titCell.setColspan(2);
			titCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			titCell.setBorder(PdfPCell.NO_BORDER);
			dataCell.setColspan(2);
			dataCell.setBorder(PdfPCell.NO_BORDER);
			dataCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			dataCell.setPaddingBottom(2);
			dataCell.setPaddingBottom(10);
			dataCell.setBorder(PdfPCell.BOTTOM);

			table.addCell(empresaCell);
			table.addCell(bibliotecaCell);
			table.addCell(telefoneCell);
			table.addCell(titCell);
			table.addCell(dataCell);

			document.add(table);

			PdfPTable table2;

			table2 = new PdfPTable(5);
			float[] widths1 = { 1, 4, 1, 1, 1 };
			table2.setWidths(widths1);

			table2.setWidthPercentage(100);
			table2.setPaddingTop(10);

			Font fonth = new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD);
			Font font = new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL);

			PdfPCell registroTit = new PdfPCell(
					new Paragraph("Registro", fonth));
			registroTit.setBackgroundColor(BaseColor.LIGHT_GRAY);

			PdfPCell tituloTit = new PdfPCell(new Paragraph("Título", fonth));
			tituloTit.setBackgroundColor(BaseColor.LIGHT_GRAY);

			PdfPCell classificacaoTit = new PdfPCell(new Paragraph("Tipo",
					fonth));
			classificacaoTit.setBackgroundColor(BaseColor.LIGHT_GRAY);
			classificacaoTit.setHorizontalAlignment(Element.ALIGN_LEFT);

			PdfPCell codigoTit = new PdfPCell(new Paragraph("Código", fonth));
			codigoTit.setBackgroundColor(BaseColor.LIGHT_GRAY);
			codigoTit.setHorizontalAlignment(Element.ALIGN_LEFT);

			PdfPCell cadastroTit = new PdfPCell(new Paragraph(
					"Data de Cadastro", fonth));
			cadastroTit.setBackgroundColor(BaseColor.LIGHT_GRAY);
			cadastroTit.setHorizontalAlignment(Element.ALIGN_CENTER);

			table2.addCell(registroTit);
			table2.addCell(tituloTit);
			table2.addCell(classificacaoTit);
			table2.addCell(codigoTit);
			table2.addCell(cadastroTit);

			Date inicio = inicioDatebox.getValue() == null ? new Date()
					: inicioDatebox.getValue();
			Date fim = fimDatebox.getValue() == null ? new Date() : fimDatebox
					.getValue();

			List<ExemplarMidia> exemplares = ExemplarMidiaDao.findBetween(
					inicio, fim);

			for (ExemplarMidia exemplar : exemplares) {

				Paragraph registro = new Paragraph(exemplar.getId() + "", font);
				Paragraph titulo = new Paragraph(exemplar.getMidia()
						.getTituloCompleto(), font);
				Paragraph classificacao = new Paragraph(exemplar.getMidia()
						.getTipoMidia().getNome(), font);
				Paragraph codigo = new Paragraph(exemplar.getMidia().getId()
						+ "", font);
				Paragraph cadastro = new Paragraph(exemplar.getDataCadastroBR()
						+ "", font);

				PdfPCell registroCell = new PdfPCell(registro);
				registroCell.setHorizontalAlignment(Element.ALIGN_LEFT);
				PdfPCell tituloCell = new PdfPCell(titulo);
				tituloCell.setHorizontalAlignment(Element.ALIGN_LEFT);
				PdfPCell classificacaoCell = new PdfPCell(classificacao);
				classificacaoCell.setHorizontalAlignment(Element.ALIGN_LEFT);
				PdfPCell codigoCell = new PdfPCell(codigo);
				codigoCell.setHorizontalAlignment(Element.ALIGN_LEFT);
				PdfPCell cadastroCell = new PdfPCell(cadastro);
				cadastroCell.setHorizontalAlignment(Element.ALIGN_CENTER);

				table2.addCell(registroCell);
				table2.addCell(tituloCell);
				table2.addCell(classificacaoCell);
				table2.addCell(codigoCell);
				table2.addCell(cadastroCell);

			}
			document.add(table2);

			document.add(new Paragraph("Total de exemplares: "
					+ exemplares.size(), font));

			document.close();

			Hashtable<String, String> h = new Hashtable<String, String>();
			h.put("File", temp.getAbsolutePath());

			Executions.getCurrent().createComponents("/pdf/pdf.zul",
					editWindow, h);

		} catch (Exception e) {
			e.printStackTrace();
		}

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

}
