package com.example.pescevia.controller;

import com.example.pescevia.database.PesceDAO;
import com.example.pescevia.model.Pesce;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class GestoreController {

    @FXML
    private TableColumn<Pesce, String> colNome;

    @FXML
    private TableColumn<Pesce, Double> colPrezzo;

    @FXML
    private TableColumn<Pesce, Double> colQuantita;

    @FXML
    private TableView<Pesce> tablePesci;

    @FXML
    private TextField txtNome;

    @FXML
    private TextField txtPrezzo;

    @FXML
    private TextField txtQuantita;

    private final PesceDAO pesceDAO = new PesceDAO();
    private final ObservableList<Pesce> pesci = FXCollections.observableArrayList();

    @FXML
    public void initialize(){
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colPrezzo.setCellValueFactory(new PropertyValueFactory<>("prezzo"));
        colQuantita.setCellValueFactory(new PropertyValueFactory<>("quantita"));

        tablePesci.setItems(pesci);

        //Carico pesci dal database
        caricaPesci();
    }

    public void caricaPesci(){
        pesci.clear();
        pesci.setAll(pesceDAO.getAll());
        tablePesci.refresh();
    }

    @FXML
    private void aggiungiPesce(){
        String nome = txtNome.getText().trim();
        double prezzo;
        double quantita;

        try{
            prezzo = Double.parseDouble(txtPrezzo.getText());
            quantita = Double.parseDouble(txtQuantita.getText());
        }catch (NumberFormatException e){
            showAlert(Alert.AlertType.ERROR, "Errore",  "Prezzo o quantità non validi");
            return;
        }

        if (nome.isEmpty() || prezzo <= 0 || quantita < 0){
            showAlert(Alert.AlertType.ERROR, "Errore",  "Inserisci tutti i dati corretti");
            return;
        }

        boolean successo = pesceDAO.addPesce(new Pesce(0, nome, prezzo, quantita));
        if (successo){
            showAlert(Alert.AlertType.INFORMATION, "Successo",  "Pesce aggiunto correttamente!");
            caricaPesci();
            pulisciCampi();
        }else {
            showAlert(Alert.AlertType.ERROR, "Errore",  "Impossibile aggiungere il pesce");
        }
    }

    @FXML
    private void modificaPesce(){
        Pesce selezionato = tablePesci.getSelectionModel().getSelectedItem();
        if (selezionato == null){
            showAlert(Alert.AlertType.WARNING, "Selezione mancante",  "Seleziona un pesce da modificare");
            return;
        }

        String prezzoTxt = txtPrezzo.getText().trim();
        String quantitaTxt = txtQuantita.getText().trim();

        if (prezzoTxt.isEmpty() && quantitaTxt.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Dati mancanti", "Inserisci il nuovo prezzo o la nuova quantità (o entrambi)");
            return;
        }

        boolean datiValidi = true;

        if (!prezzoTxt.isEmpty()) {
            try{
                double nuovoPrezzo = Double.parseDouble(prezzoTxt);
                if (nuovoPrezzo > 0) {
                    selezionato.setPrezzo(nuovoPrezzo);
                } else {
                    showAlert(Alert.AlertType.ERROR, "Errore", "Il prezzo deve essere maggiore di zero.");
                    datiValidi = false;
                }
            } catch (NumberFormatException e){
                showAlert(Alert.AlertType.ERROR, "Errore", "Prezzo non valido (inserisci un numero)");
                datiValidi = false;
            }
        }

        if (!datiValidi) return;

        if (!quantitaTxt.isEmpty()) {
            try{
                double nuovaQuantita = Double.parseDouble(quantitaTxt);
                if (nuovaQuantita >= 0) {
                    selezionato.setQuantita(nuovaQuantita);
                } else {
                    showAlert(Alert.AlertType.ERROR, "Errore", "La quantità non può essere negativa.");
                    datiValidi = false;
                }
            } catch (NumberFormatException e){
                showAlert(Alert.AlertType.ERROR, "Errore", "Quantità non valida (inserisci un numero)");
                datiValidi = false;
            }
        }

        if (!datiValidi) return;

        boolean successo = pesceDAO.update(selezionato);
        if (successo){
            showAlert(Alert.AlertType.INFORMATION, "Successo",  "Pesce aggiornato correttamente");
            caricaPesci();
            pulisciCampi();
        } else {
            showAlert(Alert.AlertType.ERROR, "Errore", "Impossibile aggiornare il pesce");
        }
    }

    @FXML
    private void eliminaPesce(){
        Pesce selezionato = tablePesci.getSelectionModel().getSelectedItem();
        if (selezionato == null){
            showAlert(Alert.AlertType.WARNING, "Selezione mancante",  "Seleziona un pesce da eliminare");
            return;
        }

        boolean successo = pesceDAO.delete(selezionato.getId());
        if (successo){
            showAlert(Alert.AlertType.INFORMATION, "Successo", "Pesce eliminato correttamente");
            caricaPesci();
        } else {
            showAlert(Alert.AlertType.ERROR, "Errore", "Impossibile eliminare il pesce");
        }
    }

    private void pulisciCampi() {
        txtNome.clear();
        txtPrezzo.clear();
        txtQuantita.clear();
    }

    private void showAlert(Alert.AlertType type, String title, String content){
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
