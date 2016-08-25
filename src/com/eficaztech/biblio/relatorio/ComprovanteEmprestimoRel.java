package com.eficaztech.biblio.relatorio;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.Window;

import com.eficaztech.biblio.model.Empresa;
import com.eficaztech.biblio.model.EmpresaDao;
import com.eficaztech.biblio.model.Emprestimo;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

public class ComprovanteEmprestimoRel {

	public static void rel(Window window, List<Emprestimo> emprestimos) {

		try {

			Font font = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL);

			Empresa empresa = EmpresaDao.findFirst();

			File temp = File.createTempFile("comprovantes", ".pdf");

			Document document = new Document();

			PdfWriter.getInstance(document, new FileOutputStream(temp));
			// document.setPageSize(new Rectangle(new Integer(w1.getValue()),
			// new Integer(h1.getValue())));
			document.setPageSize(PageSize.A4);
			document.open();

			// cabecalho
			PdfPTable table = new PdfPTable(2);
			table.setWidthPercentage(100);
			float[] widths = { 1, 6 };
			table.setWidths(widths);
			PdfPCell empresaCell = new PdfPCell(new Paragraph(
					empresa.getNome(), font));
			PdfPCell bibliotecaCell = new PdfPCell(new Paragraph(
					empresa.getBiblioteca(), font));
			PdfPCell telefoneCell = new PdfPCell(new Paragraph("Telefone: "
					+ empresa.getTelefones(), font));
			PdfPCell tituloCell = new PdfPCell(new Paragraph(
					"C O M P R O V A N T E", font));

			SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
			Emprestimo emp = emprestimos.get(0);
			PdfPCell clienteCell = new PdfPCell(new Paragraph(emp.getCliente()
					.getNome(), font));
			PdfPCell dataCell = new PdfPCell(new Paragraph("Data: "
					+ format.format(new Date()), font));

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
			tituloCell.setColspan(2);
			tituloCell.setBorder(PdfPCell.BOTTOM);
			tituloCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			tituloCell.setPaddingBottom(10);
			clienteCell.setColspan(2);
			clienteCell.setBorder(PdfPCell.NO_BORDER);
			clienteCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			dataCell.setColspan(2);
			dataCell.setBorder(PdfPCell.NO_BORDER);
			dataCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			dataCell.setPaddingBottom(2);
			dataCell.setPaddingBottom(10);
			table.addCell(empresaCell);
			table.addCell(bibliotecaCell);
			table.addCell(telefoneCell);
			table.addCell(tituloCell);
			table.addCell(clienteCell);
			table.addCell(dataCell);

			document.add(table);

			PdfPTable table2;

			table2 = new PdfPTable(6);
			float[] widths1 = { 1, 1, 3, 1, 1, 1 };
			table2.setWidths(widths1);

			table2.setWidthPercentage(100);
			table2.setPaddingTop(10);

			Font font1 = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);

			PdfPCell registroTit = new PdfPCell(
					new Paragraph("Registro", font1));
			registroTit.setBackgroundColor(BaseColor.LIGHT_GRAY);

			PdfPCell tipoTit = new PdfPCell(new Paragraph("Tipo", font1));
			tipoTit.setBackgroundColor(BaseColor.LIGHT_GRAY);

			PdfPCell tituloTit = new PdfPCell(new Paragraph("Título", font1));
			tituloTit.setBackgroundColor(BaseColor.LIGHT_GRAY);

			PdfPCell emprestadoTit = new PdfPCell(new Paragraph(
					"Emprestado em", font1));
			emprestadoTit.setBackgroundColor(BaseColor.LIGHT_GRAY);
			emprestadoTit.setHorizontalAlignment(Element.ALIGN_CENTER);

			PdfPCell devolverTit = new PdfPCell(new Paragraph("Devolver em",
					font1));
			devolverTit.setBackgroundColor(BaseColor.LIGHT_GRAY);
			devolverTit.setHorizontalAlignment(Element.ALIGN_CENTER);

			PdfPCell multaTit = new PdfPCell(new Paragraph("Multa (R$)", font1));
			multaTit.setBackgroundColor(BaseColor.LIGHT_GRAY);
			multaTit.setHorizontalAlignment(Element.ALIGN_RIGHT);

			table2.addCell(registroTit);
			table2.addCell(tipoTit);
			table2.addCell(tituloTit);
			table2.addCell(emprestadoTit);
			table2.addCell(devolverTit);
			table2.addCell(multaTit);

			BigDecimal total = new BigDecimal(0);

			for (Emprestimo emprestimo : emprestimos) {

				Paragraph registro = new Paragraph(emprestimo.getExemplar()
						.getId() + "", font);
				Paragraph tipo = new Paragraph(emprestimo.getTipo() + "", font);
				Paragraph titulo = new Paragraph(emprestimo.getExemplar() + "",
						font);
				Paragraph emprestado = new Paragraph(
						emprestimo.getDataEmprestimoBR() + "", font);
				Paragraph devolver = new Paragraph(
						emprestimo.getDataPrevisaoDevolucaoBR() + "", font);
				Paragraph multa = new Paragraph(emprestimo.getMultaBR() + "",
						font);

				PdfPCell emprestadoCell = new PdfPCell(emprestado);
				emprestadoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
				PdfPCell devolverCell = new PdfPCell(devolver);
				devolverCell.setHorizontalAlignment(Element.ALIGN_CENTER);
				PdfPCell multaCell = new PdfPCell(multa);
				multaCell.setHorizontalAlignment(Element.ALIGN_RIGHT);

				table2.addCell(new PdfPCell(registro));
				table2.addCell(new PdfPCell(tipo));
				table2.addCell(new PdfPCell(titulo));
				table2.addCell(emprestadoCell);
				table2.addCell(devolverCell);
				table2.addCell(multaCell);

				total = total.add(emprestimo.getMulta());

			}
			document.add(table2);

			document.add(new Paragraph("Total de obras: " + emprestimos.size(),
					font));

			document.close();

			Hashtable<String, String> h = new Hashtable<String, String>();
			h.put("File", temp.getAbsolutePath());

			Executions.getCurrent().createComponents("/pdf/pdf.zul", window, h);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
