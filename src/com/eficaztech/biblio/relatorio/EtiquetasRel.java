package com.eficaztech.biblio.relatorio;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Window;

import com.eficaztech.biblio.model.ExemplarEdicao;
import com.eficaztech.biblio.model.ExemplarEdicaoDao;
import com.eficaztech.biblio.model.ExemplarLivro;
import com.eficaztech.biblio.model.ExemplarLivroDao;
import com.eficaztech.biblio.model.ExemplarMidia;
import com.eficaztech.biblio.model.ExemplarMidiaDao;
import com.eficaztech.biblio.model.ExemplarMonografia;
import com.eficaztech.biblio.model.ExemplarMonografiaDao;
import com.eficaztech.biblio.util.Notification;
import com.eficaztech.biblio.util.Security;
import com.eficaztech.biblio.view.View;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.Barcode;
import com.itextpdf.text.pdf.BarcodeInter25;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

@Init(superclass = true)
@AfterCompose(superclass = true)
public class EtiquetasRel extends View {

	private List<String> tipos;
	private String tipoSelecionado;

	Window editWindow;

	@Wire("#editWindow #tiposCombobox")
	Combobox tiposCombobox;

	@Wire("#editWindow #inicioIntbox")
	Intbox inicioIntbox;

	@Wire("#editWindow #fimIntbox")
	Intbox fimIntbox;

	@Wire("#editWindow #linhaIntbox")
	Intbox linhaIntbox;

	@Wire("#editWindow #colunaIntbox")
	Intbox colunaIntbox;

	@Override
	public void afterCompose() {
		
		if (!Security.isAdmOrBib()) return;

		tipos = new ArrayList<String>();
		tipos.add("Livro");
		tipos.add("Mídia");
		tipos.add("Monografia");
		tipos.add("Periódico");

		setTipoSelecionado("Livro");
		tiposCombobox.setFocus(true);

		BindUtils.postNotifyChange(null, null, EtiquetasRel.this, "tipos");

	}

	@Command
	public void imprimir() {
		if (tipoSelecionado == null) {
			Notification.show("warning", "Tipo inválido");
			return;
		} else if (tipoSelecionado.equals("Livro")) {
			imprimirEtiquetaLivro();
		} else if (tipoSelecionado.equals("Monografia")) {
			imprimirEtiquetaMonografia();
		} else if (tipoSelecionado.equals("Periódico")) {
			imprimirEtiquetaPeriodico();
		} else if (tipoSelecionado.equals("Mídia")) {
			imprimirEtiquetaMidia();
		} else {
			Notification.show("warning", "Tipo inválido");
			return;
		}

	}

	public void imprimirEtiquetaLivro() {

		try {

			Font font = new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL);
			Font fontb = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);

			File temp = File.createTempFile("etiquetas", ".pdf");

			Document document = new Document();

			PdfWriter writer = PdfWriter.getInstance(document,
					new FileOutputStream(temp));

			document.setPageSize(PageSize.LETTER);
			document.setMargins(55, 10, 40, 40);
			document.open();

			PdfContentByte cb = writer.getDirectContent();

			PdfPTable table2;

			table2 = new PdfPTable(2);
			float[] widths1 = { 1, 1 };
			table2.setWidths(widths1);

			table2.setWidthPercentage(100);
			table2.setPaddingTop(10);

			int inicio = inicioIntbox.getValue() == null ? 0 : inicioIntbox
					.getValue();
			int fim = fimIntbox.getValue() == null ? 0 : fimIntbox.getValue();

			List<ExemplarLivro> exemplares = ExemplarLivroDao.findBetween(
					inicio, fim);

			if (exemplares.size() == 0) {
				Notification.show("warning",
						"Não foi encontrado nenhum exemplar");
				return;
			}
			int linha = linhaIntbox.getValue() == null ? 1 : linhaIntbox
					.getValue();
			int coluna = colunaIntbox.getValue() == null ? 1 : colunaIntbox
					.getValue();

			if (linha < 1)
				linhaIntbox.setValue(1);
			if (coluna < 1 || coluna > 2)
				colunaIntbox.setValue(1);

			int brancos = ((2 * linha) - 2) + coluna;

			for (int i = 1; i < brancos; i++) {
				PdfPCell branco = new PdfPCell(new Paragraph(""));
				branco.setMinimumHeight(71f);
				branco.setBorder(PdfPCell.NO_BORDER);
				//branco.setBorder(1);
				table2.addCell(branco);
			}

			for (ExemplarLivro exemplar : exemplares) {

				PdfPTable table3 = new PdfPTable(2);
				float[] widths2 = { 1, 1 };
				table3.setWidths(widths2);

				table3.setWidthPercentage(100);
				table3.setPaddingTop(10);

				BarcodeInter25 barcode = new BarcodeInter25();
				barcode.setCodeType(Barcode.SUPP2);
				barcode.setBarHeight(40f);
				barcode.setX(1.5f);

				String text = exemplar.getId() + "";
				text = (text.length() % 2 == 0) ? text : "0" + text;

				barcode.setCode(text);

				PdfPCell barraCell = new PdfPCell(
						barcode.createImageWithBarcode(cb, BaseColor.BLACK,
								BaseColor.GRAY), false);
				barraCell.setPadding(10);
				barraCell.setMinimumHeight(71f);
				barraCell.setBorder(PdfPCell.NO_BORDER);
				//barraCell.setBorder(1);
				barraCell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);

				Paragraph p = new Paragraph();
				Chunk c1 = new Chunk("I E S M A\n", font);
				Chunk c2 = new Chunk(exemplar.getLivro().getClassificacao()
						+ "\n", fontb);
				//Chunk c3 = new Chunk("Cutter: ", font);
				Chunk c4 = new Chunk(exemplar.getLivro().getCutter() + "\n",
						fontb);
				Chunk c5 = new Chunk("Código: " + exemplar.getLivro().getId()
						+ "\n", font);
				Chunk c6 = new Chunk("Registro: " + exemplar.getId(), font);

				p.add(c1);
				p.add(c2);
				//p.add(c3);
				p.add(c4);
				p.add(c5);
				p.add(c6);

				PdfPCell classificacaoCell = new PdfPCell(p);
				classificacaoCell.setBorder(PdfPCell.NO_BORDER);
				//classificacaoCell.setBorder(1);
				
				classificacaoCell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);

				table3.addCell(barraCell);
				table3.addCell(classificacaoCell);

				PdfPCell cell = new PdfPCell(table3);
				cell.setBorder(PdfPCell.NO_BORDER);
				//cell.setBorder(1);
				table2.addCell(cell);

			}
			
			// essa celula é necessária para que a última linha seja impressa
			PdfPCell cell = new PdfPCell();
			cell.setBorder(PdfPCell.NO_BORDER);
			table2.addCell(cell);
			
			document.add(table2);

			document.close();

			Hashtable<String, String> h = new Hashtable<String, String>();
			h.put("File", temp.getAbsolutePath());

			Executions.getCurrent().createComponents("/pdf/pdf.zul",
					editWindow, h);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void imprimirEtiquetaMonografia() {

		try {

			Font font = new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL);
			Font fontb = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);

			File temp = File.createTempFile("etiquetas", ".pdf");

			Document document = new Document();

			PdfWriter writer = PdfWriter.getInstance(document,
					new FileOutputStream(temp));

			document.setPageSize(PageSize.A4);
			document.open();

			PdfContentByte cb = writer.getDirectContent();

			PdfPTable table2;

			table2 = new PdfPTable(2);
			float[] widths1 = { 1, 1 };
			table2.setWidths(widths1);

			table2.setWidthPercentage(100);
			table2.setPaddingTop(10);

			int inicio = inicioIntbox.getValue() == null ? 0 : inicioIntbox
					.getValue();
			int fim = fimIntbox.getValue() == null ? 0 : fimIntbox.getValue();

			List<ExemplarMonografia> exemplares = ExemplarMonografiaDao
					.findBetween(inicio, fim);

			if (exemplares.size() == 0) {
				Notification.show("warning",
						"Não foi encontrado nenhum exemplar");
				return;
			}
			int linha = linhaIntbox.getValue() == null ? 1 : linhaIntbox
					.getValue();
			int coluna = colunaIntbox.getValue() == null ? 1 : colunaIntbox
					.getValue();

			if (linha < 1)
				linhaIntbox.setValue(1);
			if (coluna < 1 || coluna > 2)
				colunaIntbox.setValue(1);

			int brancos = ((2 * linha) - 2) + coluna;

			for (int i = 1; i < brancos; i++) {
				PdfPCell branco = new PdfPCell(new Paragraph(""));
				branco.setMinimumHeight(71f);
				branco.setBorder(PdfPCell.NO_BORDER);
				table2.addCell(branco);
			}

			for (ExemplarMonografia exemplar : exemplares) {

				PdfPTable table3 = new PdfPTable(2);
				float[] widths2 = { 1, 1 };
				table3.setWidths(widths2);

				table3.setWidthPercentage(100);
				table3.setPaddingTop(10);

				BarcodeInter25 barcode = new BarcodeInter25();
				barcode.setCodeType(Barcode.SUPP2);
				barcode.setBarHeight(40f);
				barcode.setX(1.5f);

				String text = exemplar.getId() + "";
				text = (text.length() % 2 == 0) ? text : "0" + text;

				barcode.setCode(text);

				PdfPCell barraCell = new PdfPCell(
						barcode.createImageWithBarcode(cb, BaseColor.BLACK,
								BaseColor.GRAY), false);
				barraCell.setPadding(10);
				barraCell.setMinimumHeight(71f);
				barraCell.setBorder(PdfPCell.NO_BORDER);
				barraCell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);

				Paragraph p = new Paragraph();
				Chunk c1 = new Chunk("I E S M A\n", font);
				Chunk c2 = new Chunk(exemplar.getMonografia()
						.getClassificacao() + "\n", fontb);
				//Chunk c3 = new Chunk("Cutter: ", font);
				Chunk c4 = new Chunk(exemplar.getMonografia().getCutter()
						+ "\n", fontb);
				Chunk c5 = new Chunk("Código: "
						+ exemplar.getMonografia().getId() + "\n", font);
				Chunk c6 = new Chunk("Registro: " + exemplar.getId(), font);

				p.add(c1);
				p.add(c2);
				//p.add(c3);
				p.add(c4);
				p.add(c5);
				p.add(c6);

				PdfPCell classificacaoCell = new PdfPCell(p);
				classificacaoCell.setBorder(PdfPCell.NO_BORDER);
				classificacaoCell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);

				table3.addCell(barraCell);
				table3.addCell(classificacaoCell);

				PdfPCell cell = new PdfPCell(table3);
				cell.setBorder(PdfPCell.NO_BORDER);
				table2.addCell(cell);

			}
			document.add(table2);

			document.close();

			Hashtable<String, String> h = new Hashtable<String, String>();
			h.put("File", temp.getAbsolutePath());

			Executions.getCurrent().createComponents("/pdf/pdf.zul",
					editWindow, h);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void imprimirEtiquetaPeriodico() {

		try {

			Font font = new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL);
			Font fontb = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);

			File temp = File.createTempFile("etiquetas", ".pdf");

			Document document = new Document();

			PdfWriter writer = PdfWriter.getInstance(document,
					new FileOutputStream(temp));

			document.setPageSize(PageSize.A4);
			document.open();

			PdfContentByte cb = writer.getDirectContent();

			PdfPTable table2;

			table2 = new PdfPTable(2);
			float[] widths1 = { 1, 1 };
			table2.setWidths(widths1);

			table2.setWidthPercentage(100);
			table2.setPaddingTop(10);

			int inicio = inicioIntbox.getValue() == null ? 0 : inicioIntbox
					.getValue();
			int fim = fimIntbox.getValue() == null ? 0 : fimIntbox.getValue();

			List<ExemplarEdicao> exemplares = ExemplarEdicaoDao.findBetween(
					inicio, fim);

			if (exemplares.size() == 0) {
				Notification.show("warning",
						"Não foi encontrado nenhum exemplar");
				return;
			}
			int linha = linhaIntbox.getValue() == null ? 1 : linhaIntbox
					.getValue();
			int coluna = colunaIntbox.getValue() == null ? 1 : colunaIntbox
					.getValue();

			if (linha < 1)
				linhaIntbox.setValue(1);
			if (coluna < 1 || coluna > 2)
				colunaIntbox.setValue(1);

			int brancos = ((2 * linha) - 2) + coluna;

			for (int i = 1; i < brancos; i++) {
				PdfPCell branco = new PdfPCell(new Paragraph(""));
				branco.setMinimumHeight(71f);
				branco.setBorder(PdfPCell.NO_BORDER);
				table2.addCell(branco);
			}

			for (ExemplarEdicao exemplar : exemplares) {

				PdfPTable table3 = new PdfPTable(2);
				float[] widths2 = { 1, 1 };
				table3.setWidths(widths2);

				table3.setWidthPercentage(100);
				table3.setPaddingTop(10);

				BarcodeInter25 barcode = new BarcodeInter25();
				barcode.setCodeType(Barcode.SUPP2);
				barcode.setBarHeight(40f);
				barcode.setX(1.5f);

				String text = exemplar.getId() + "";
				text = (text.length() % 2 == 0) ? text : "0" + text;

				barcode.setCode(text);

				PdfPCell barraCell = new PdfPCell(
						barcode.createImageWithBarcode(cb, BaseColor.BLACK,
								BaseColor.GRAY), false);
				barraCell.setPadding(10);
				barraCell.setMinimumHeight(71f);
				barraCell.setBorder(PdfPCell.NO_BORDER);
				barraCell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);

				Paragraph p = new Paragraph();
				Chunk c2 = new Chunk(exemplar.getEdicao().getPeriodico().getTitulo()+ "\n", fontb);
				Chunk c5 = new Chunk("Código: " + exemplar.getEdicao().getId()
						+ "\n", font);
				Chunk c6 = new Chunk("Registro: " + exemplar.getId(), font);

				p.add(c2);
				p.add(c5);
				p.add(c6);

				PdfPCell classificacaoCell = new PdfPCell(p);
				classificacaoCell.setBorder(PdfPCell.NO_BORDER);
				classificacaoCell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);

				table3.addCell(barraCell);
				table3.addCell(classificacaoCell);

				PdfPCell cell = new PdfPCell(table3);
				cell.setBorder(PdfPCell.NO_BORDER);
				table2.addCell(cell);

			}
			document.add(table2);

			document.close();

			Hashtable<String, String> h = new Hashtable<String, String>();
			h.put("File", temp.getAbsolutePath());

			Executions.getCurrent().createComponents("/pdf/pdf.zul",
					editWindow, h);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void imprimirEtiquetaMidia() {

		try {

			Font font = new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL);
			Font fontb = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);

			File temp = File.createTempFile("etiquetas", ".pdf");

			Document document = new Document();

			PdfWriter writer = PdfWriter.getInstance(document,
					new FileOutputStream(temp));

			document.setPageSize(PageSize.A4);
			document.open();

			PdfContentByte cb = writer.getDirectContent();

			PdfPTable table2;

			table2 = new PdfPTable(2);
			float[] widths1 = { 1, 1 };
			table2.setWidths(widths1);

			table2.setWidthPercentage(100);
			table2.setPaddingTop(10);

			int inicio = inicioIntbox.getValue() == null ? 0 : inicioIntbox
					.getValue();
			int fim = fimIntbox.getValue() == null ? 0 : fimIntbox.getValue();

			List<ExemplarMidia> exemplares = ExemplarMidiaDao.findBetween(
					inicio, fim);

			if (exemplares.size() == 0) {
				Notification.show("warning",
						"Não foi encontrado nenhum exemplar");
				return;
			}
			int linha = linhaIntbox.getValue() == null ? 1 : linhaIntbox
					.getValue();
			int coluna = colunaIntbox.getValue() == null ? 1 : colunaIntbox
					.getValue();

			if (linha < 1)
				linhaIntbox.setValue(1);
			if (coluna < 1 || coluna > 2)
				colunaIntbox.setValue(1);

			int brancos = ((2 * linha) - 2) + coluna;

			for (int i = 1; i < brancos; i++) {
				PdfPCell branco = new PdfPCell(new Paragraph(""));
				branco.setMinimumHeight(71f);
				branco.setBorder(PdfPCell.NO_BORDER);
				table2.addCell(branco);
			}

			for (ExemplarMidia exemplar : exemplares) {

				PdfPTable table3 = new PdfPTable(2);
				float[] widths2 = { 1, 1 };
				table3.setWidths(widths2);

				table3.setWidthPercentage(100);
				table3.setPaddingTop(10);

				BarcodeInter25 barcode = new BarcodeInter25();
				barcode.setCodeType(Barcode.SUPP2);
				barcode.setBarHeight(40f);
				barcode.setX(1.5f);

				String text = exemplar.getId() + "";
				text = (text.length() % 2 == 0) ? text : "0" + text;

				barcode.setCode(text);

				PdfPCell barraCell = new PdfPCell(
						barcode.createImageWithBarcode(cb, BaseColor.BLACK,
								BaseColor.GRAY), false);
				barraCell.setPadding(10);
				barraCell.setMinimumHeight(71f);
				barraCell.setBorder(PdfPCell.NO_BORDER);
				barraCell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);

				Paragraph p = new Paragraph();
				Chunk c2 = new Chunk(exemplar.getMidia().getTipoMidia()
						.getNome()
						+ "\n", fontb);
				Chunk c5 = new Chunk("Código: " + exemplar.getMidia().getId()
						+ "\n", font);
				Chunk c6 = new Chunk("Registro: " + exemplar.getId(), font);

				p.add(c2);
				p.add(c5);
				p.add(c6);

				PdfPCell classificacaoCell = new PdfPCell(p);
				classificacaoCell.setBorder(PdfPCell.NO_BORDER);
				classificacaoCell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);

				table3.addCell(barraCell);
				table3.addCell(classificacaoCell);

				PdfPCell cell = new PdfPCell(table3);
				cell.setBorder(PdfPCell.NO_BORDER);
				table2.addCell(cell);

			}
			document.add(table2);

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
