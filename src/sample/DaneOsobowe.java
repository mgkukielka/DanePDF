package sample;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javafx.event.ActionEvent;
import javafx.scene.control.cell.PropertyValueFactory;

import javax.swing.*;

/**
 * Created by pwilkin on 30-Nov-17.
 */
public class DaneOsobowe implements HierarchicalController<MainController> {

    public TextField imie;
    public TextField nazwisko;
    public TextField pesel;
    public TextField indeks;
    public TableView<Student> tabelka;
    private MainController parentController;

    public void dodaj(ActionEvent actionEvent) {
        Student st = new Student();
        st.setName(imie.getText());
        st.setSurname(nazwisko.getText());
        st.setPesel(pesel.getText());
        st.setIdx(indeks.getText());
        tabelka.getItems().add(st);
    }

    public void setParentController(MainController parentController) {
        this.parentController = parentController;
        //tabelka.getItems().addAll(parentController.getDataContainer().getStudents());
        tabelka.setItems(parentController.getDataContainer().getStudents());
    }

    public void usunZmiany() {
        tabelka.getItems().clear();
        tabelka.getItems().addAll(parentController.getDataContainer().getStudents());
    }

    public MainController getParentController() {
        return parentController;
    }

    public void initialize() {
        for (TableColumn<Student, ?> studentTableColumn : tabelka.getColumns()) {
            if ("imie".equals(studentTableColumn.getId())) {
                studentTableColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
            } else if ("nazwisko".equals(studentTableColumn.getId())) {
                studentTableColumn.setCellValueFactory(new PropertyValueFactory<>("surname"));
            } else if ("pesel".equals(studentTableColumn.getId())) {
                studentTableColumn.setCellValueFactory(new PropertyValueFactory<>("pesel"));
            } else if ("indeks".equals(studentTableColumn.getId())) {
                studentTableColumn.setCellValueFactory(new PropertyValueFactory<>("idx"));
            }
        }

    }

    public void zapisz(ActionEvent event) throws IOException, DocumentException {
        Document document = new Document();
        Alert alert = new Alert(Alert.AlertType.NONE);
        alert.setTitle("Wybierz miejsce zapisu");
        ButtonType nowy = new ButtonType("Nowy plik");
        ButtonType exist = new ButtonType("Wybierz plik");
        alert.getButtonTypes().setAll(nowy, exist);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == nowy) {
            try {
                FileOutputStream fos = new FileOutputStream("Dane osobowe.pdf");
                PdfWriter.getInstance(document, fos);
            } catch (DocumentException | FileNotFoundException e1) {
                e1.printStackTrace();
            }
        } else {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Zapisz jako: ");
            File file = fileChooser.showSaveDialog(new Stage());
            if (file != null) {
                try {
                    FileOutputStream fos = new FileOutputStream(file);
                    PdfWriter.getInstance(document, fos);
                } catch (FileNotFoundException | DocumentException e1) {
                    e1.printStackTrace();
                }
            } else {

            }
            document.open();
            PdfPTable table = new PdfPTable(6);
            table.setHeaderRows(1);
            table.setSplitRows(false);
            table.setComplete(false);
            document.addCreationDate();
            document.addTitle("Dane osobowe");
            table.addCell("Imię");
            table.addCell("Nazwisko");
            table.addCell("Ocena");
            table.addCell("Uzasadnienie");
            table.addCell("Index");
            table.addCell("Pesel");

            for (Student student : tabelka.getItems()) {
                document.add(table);
                table.addCell(student.getName());
                table.addCell(student.getSurname());
                if (student.getGrade() != null) {
                    table.addCell(student.getGrade().toString());
                } else {
                    table.addCell(" ");
                }
                table.addCell(student.getGradeDetailed());
                table.addCell(student.getIdx());
                table.addCell(student.getPesel());
            }


            table.setComplete(true);
            document.add(table);
            document.close();
        }
    }
}
