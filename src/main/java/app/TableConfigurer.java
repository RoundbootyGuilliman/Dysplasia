package app;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class TableConfigurer {
	
	private static ObservableList<Patient> data;
	
	public static VBox configureTable() {
		
		TableView<Patient> table = new TableView<>();
		
		constructPatients();
		
		table.setEditable(true);
		
		TableColumn<Patient, String> firstNameCol = new TableColumn<>("Имя");
		firstNameCol.setMinWidth(100);
		firstNameCol.setCellValueFactory(new PropertyValueFactory<>("firstName"));
		
		
		TableColumn<Patient, String> lastNameCol = new TableColumn<>("Фамилия");
		lastNameCol.setMinWidth(100);
		lastNameCol.setCellValueFactory(new PropertyValueFactory<>("lastName"));
		
		TableColumn<Patient, String> ageCol = new TableColumn<>("Возраст");
		ageCol.setMinWidth(40);
		ageCol.setCellValueFactory(new PropertyValueFactory<>("age"));
		
		TableColumn<Patient, String> genderCol = new TableColumn<>("Пол");
		genderCol.setMinWidth(40);
		genderCol.setCellValueFactory(new PropertyValueFactory<>("gender"));
		
		
		List<TableColumn<Patient, String>> columns = Stream.of("АИ", "ОАШВ", "ЦУ", "АУ", "ИМР", "ИК", "ШДУ")
				.map(TableConfigurer::columnFactory)
				.collect(Collectors.toList());
		
		TableColumn<Patient, Patient> deleteCol = new TableColumn<>();
		deleteCol.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		deleteCol.setCellFactory(param -> new TableCell<Patient, Patient>() {
			private final Button deleteButton = new Button("Удалить");
			
			@Override
			protected void updateItem(Patient patient, boolean empty) {
				super.updateItem(patient, empty);
				
				if (patient == null) {
					setGraphic(null);
					return;
				}
				setGraphic(deleteButton);
				deleteButton.setOnAction(event -> {
					data.remove(patient);
					Props.delete(patient.getFirstName(), patient.getLastName());
				});
			}
		});
		
		table.setItems(data);
		table.getColumns().addAll(Arrays.asList(firstNameCol, lastNameCol, ageCol, genderCol));
		columns.forEach(tableColumn -> table.getColumns().add(tableColumn));
		table.getColumns().add(deleteCol);
		
		final VBox vbox = new VBox();
		vbox.getChildren().addAll(table);
		
		table.prefHeightProperty().bind(vbox.heightProperty());
		
		return vbox;
	}
	
	private static void constructPatients() {
		
		String degress = "\u00B0";
		String percentage = "%";
		
		Set<Map.Entry<Object, Object>> all = Props.getAll();
		Map<String, Patient> patients = new HashMap<>();
		
		all.forEach(objectObjectEntry -> {
			
			Patient patient;
			
			String[] key = objectObjectEntry.getKey().toString().split(";");
			String value = objectObjectEntry.getValue().toString();
			
			String firstName = key[0].split("SPC")[0];
			String lastName = key[0].split("SPC")[1];
			
			if (!patients.containsKey(firstName + lastName)) {
				Patient newPatient = new Patient();
				newPatient.setFirstName(firstName);
				newPatient.setLastName(lastName);
				newPatient.setAge(key[1]);
				newPatient.setGender(key[2].replace("M", "Муж.").replace("F", "Жен."));
				patients.put(firstName + lastName, newPatient);
				patient = newPatient;
			} else {
				patient = patients.get(firstName + lastName);
			}
			
			String mode = key[3];
			
			switch (mode) {
				case "AIL":
					patient.setAiL(value + degress);
					break;
				case "AIR":
					patient.setAiR(value + degress);
					break;
				case "ADRL":
					patient.setAdrL(value + percentage);
					break;
				case "ADRR":
					patient.setAdrR(value + percentage);
					break;
				case "CEAL":
					patient.setCeaL(value + degress);
					break;
				case "CEAR":
					patient.setCeaR(value + degress);
					break;
				case "AAL":
					patient.setAaL(value + degress);
					break;
				case "AAR":
					patient.setAaR(value + degress);
					break;
				case "RIL":
					patient.setRiL(value + percentage);
					break;
				case "RIR":
					patient.setRiR(value + percentage);
					break;
				case "CIL":
					patient.setCiL(value + percentage);
					break;
				case "CIR":
					patient.setCiR(value + percentage);
					break;
				case "SDUL":
					patient.setSduL(value + percentage);
					break;
				case "SDUR":
					patient.setSduR(value + percentage);
					break;
			}
		});
		data = FXCollections.observableArrayList(patients.values());
	}
	
	private static TableColumn<Patient, String> columnFactory(String index) {
		TableColumn<Patient, String> centerCol = new TableColumn<>(index);
		
		index = index
				.replace("АИ", "AI")
				.replace("ОАШВ", "ADR")
				.replace("ЦУ", "CEA")
				.replace("АУ", "AA")
				.replace("ИМР", "RI")
				.replace("ИК", "CI")
				.replace("ШДУ", "SDU");
		
		TableColumn<Patient, String> lCol = new TableColumn<>("L");
		lCol.setCellValueFactory(new PropertyValueFactory<>(index.toLowerCase() + "L"));
		lCol.setMinWidth(35);
		
		TableColumn<Patient, String> rCol = new TableColumn<>("R");
		rCol.setCellValueFactory(new PropertyValueFactory<>(index.toLowerCase() + "R"));
		rCol.setMinWidth(35);
		
		
		centerCol.getColumns().addAll(lCol, rCol);
		centerCol.setMinWidth(70);
		
		return centerCol;
	}
	
	public static class Patient {
		
		private SimpleStringProperty firstName = new SimpleStringProperty();
		private SimpleStringProperty lastName = new SimpleStringProperty();
		private SimpleStringProperty age = new SimpleStringProperty();
		private SimpleStringProperty gender = new SimpleStringProperty();
		private SimpleStringProperty aiL = new SimpleStringProperty();
		private SimpleStringProperty aiR = new SimpleStringProperty();
		private SimpleStringProperty adrL = new SimpleStringProperty();
		private SimpleStringProperty adrR = new SimpleStringProperty();
		private SimpleStringProperty ceaL = new SimpleStringProperty();
		private SimpleStringProperty ceaR = new SimpleStringProperty();
		private SimpleStringProperty aaL = new SimpleStringProperty();
		private SimpleStringProperty aaR = new SimpleStringProperty();
		private SimpleStringProperty riL = new SimpleStringProperty();
		private SimpleStringProperty riR = new SimpleStringProperty();
		private SimpleStringProperty ciL = new SimpleStringProperty();
		private SimpleStringProperty ciR = new SimpleStringProperty();
		private SimpleStringProperty sduL = new SimpleStringProperty();
		private SimpleStringProperty sduR = new SimpleStringProperty();
		
		public Patient() {
		}
		
		public String getFirstName() {
			return firstName.get();
		}
		
		public void setFirstName(String firstName) {
			this.firstName.set(firstName);
		}
		
		public String getAge() {
			return age.get();
		}
		
		public void setAge(String age) {
			this.age.set(age);
		}
		
		public SimpleStringProperty ageProperty() {
			return age;
		}
		
		public String getGender() {
			return gender.get();
		}
		
		public void setGender(String gender) {
			this.gender.set(gender);
		}
		
		public SimpleStringProperty genderProperty() {
			return gender;
		}
		
		public SimpleStringProperty firstNameProperty() {
			return firstName;
		}
		
		public String getLastName() {
			return lastName.get();
		}
		
		public void setLastName(String lastName) {
			this.lastName.set(lastName);
		}
		
		public SimpleStringProperty lastNameProperty() {
			return lastName;
		}
		
		public String getAiL() {
			return aiL.get();
		}
		
		public void setAiL(String aiL) {
			this.aiL.set(aiL);
		}
		
		public SimpleStringProperty aiLProperty() {
			return aiL;
		}
		
		public String getAiR() {
			return aiR.get();
		}
		
		public void setAiR(String aiR) {
			this.aiR.set(aiR);
		}
		
		public SimpleStringProperty aiRProperty() {
			return aiR;
		}
		
		public String getAdrL() {
			return adrL.get();
		}
		
		public void setAdrL(String adrL) {
			this.adrL.set(adrL);
		}
		
		public SimpleStringProperty adrLProperty() {
			return adrL;
		}
		
		public String getAdrR() {
			return adrR.get();
		}
		
		public void setAdrR(String adrR) {
			this.adrR.set(adrR);
		}
		
		public SimpleStringProperty adrRProperty() {
			return adrR;
		}
		
		public String getCeaL() {
			return ceaL.get();
		}
		
		public void setCeaL(String ceaL) {
			this.ceaL.set(ceaL);
		}
		
		public SimpleStringProperty ceaLProperty() {
			return ceaL;
		}
		
		public String getCeaR() {
			return ceaR.get();
		}
		
		public void setCeaR(String ceaR) {
			this.ceaR.set(ceaR);
		}
		
		public SimpleStringProperty ceaRProperty() {
			return ceaR;
		}
		
		public String getAaL() {
			return aaL.get();
		}
		
		public void setAaL(String aaL) {
			this.aaL.set(aaL);
		}
		
		public SimpleStringProperty aaLProperty() {
			return aaL;
		}
		
		public String getAaR() {
			return aaR.get();
		}
		
		public void setAaR(String aaR) {
			this.aaR.set(aaR);
		}
		
		public SimpleStringProperty aaRProperty() {
			return aaR;
		}
		
		public String getRiL() {
			return riL.get();
		}
		
		public void setRiL(String riL) {
			this.riL.set(riL);
		}
		
		public SimpleStringProperty riLProperty() {
			return riL;
		}
		
		public String getRiR() {
			return riR.get();
		}
		
		public void setRiR(String riR) {
			this.riR.set(riR);
		}
		
		public SimpleStringProperty riRProperty() {
			return riR;
		}
		
		public String getCiL() {
			return ciL.get();
		}
		
		public void setCiL(String ciL) {
			this.ciL.set(ciL);
		}
		
		public SimpleStringProperty ciLProperty() {
			return ciL;
		}
		
		public String getCiR() {
			return ciR.get();
		}
		
		public void setCiR(String ciR) {
			this.ciR.set(ciR);
		}
		
		public SimpleStringProperty ciRProperty() {
			return ciR;
		}
		
		public String getSduL() {
			return sduL.get();
		}
		
		public void setSduL(String sduL) {
			this.sduL.set(sduL);
		}
		
		public SimpleStringProperty sduLProperty() {
			return sduL;
		}
		
		public String getSduR() {
			return sduR.get();
		}
		
		public void setSduR(String sduR) {
			this.sduR.set(sduR);
		}
		
		public SimpleStringProperty sduRProperty() {
			return sduR;
		}
	}
}