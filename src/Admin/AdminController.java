package Admin;

import dbUtil.dbConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class AdminController implements Initializable {
    @FXML
    private TableView<StudentData> studentTable;
    @FXML
    private TableColumn<StudentData, String> idcolumn;

    @FXML
    private TableColumn<StudentData, String> firstnamecolumn;

    @FXML
    private TableColumn<StudentData, String> lastnamecolumn;

    @FXML
    private TableColumn<StudentData, String> emailcolumn;

    @FXML
    private TableColumn<StudentData, String> dobcolumn;

    @FXML
    private TextField searchTxt;

    private dbConnection db;
    private ObservableList<StudentData> data;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.db = new dbConnection();
    }//initialize

    @FXML
    private void loadStudentData(ActionEvent event){
        try {
            Connection conn = dbConnection.getConnection();
            this.data = FXCollections.observableArrayList();
            String sql = "select * from student";
            ResultSet rs = conn.createStatement().executeQuery(sql);

            while (rs.next()) {
                this.data.add(new StudentData(rs.getString(1),
                        rs.getString(2), rs.getString(3),
                        rs.getString(4), rs.getString(5)));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //load data into tableView
        this.idcolumn.setCellValueFactory(
                new PropertyValueFactory<StudentData,String>("ID"));
        this.firstnamecolumn.setCellValueFactory(
                new PropertyValueFactory<StudentData,String>("firstName"));
        this.lastnamecolumn.setCellValueFactory(
                new PropertyValueFactory<StudentData,String>("lastName"));
        this.emailcolumn.setCellValueFactory(
                new PropertyValueFactory<StudentData,String>("email"));
        this.dobcolumn.setCellValueFactory(
                new PropertyValueFactory<StudentData,String>("DOB"));

        this.studentTable.setItems(null);
        this.studentTable.setItems(data);

        //Filter Data in TableView
        FilteredList<StudentData> filteredData =
                new FilteredList<>(data, e -> true);
        searchTxt.setOnKeyReleased(e -> {
            searchTxt.textProperty().addListener(
                    (observable, oldValue, newValue) -> {
                        filteredData.setPredicate(StudentData -> {
                            if (newValue == null || newValue.isEmpty()) {
                                return true;
                            }
                            String lowerCaseFilter = newValue.toLowerCase();
                            if (StudentData.getID().contains(newValue)) {
                                return true;
                            } else if
                                    (StudentData.getFirstName().toLowerCase().contains(lowerCaseFilter)) {
                                return true;
                            } else if
                                    (StudentData.getLastName().toLowerCase().contains(lowerCaseFilter)) {
                                return true;
                            }
                            return false;
                        });
                    });
            SortedList<StudentData> sortedData =
                    new SortedList<>(filteredData);
            sortedData.comparatorProperty().bind(
                    studentTable.comparatorProperty());
            studentTable.setItems(sortedData);

        });

    }//loadStudentData

}//class