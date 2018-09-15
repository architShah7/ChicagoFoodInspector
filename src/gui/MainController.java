package gui;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableColumn.CellDataFeatures;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import modules.Data;
import modules.Facility;
import modules.Facility.Risk;
import modules.Facility.Type;
import modules.Inspection;
import modules.Inspection.Result;
import modules.Search;
import modules.Sort;
import structures.FacilityWeightedGraph;

public class MainController {

	@FXML
	private TreeTableColumn<Facility, Integer> zip;

	@FXML
	private TreeTableColumn<Facility, String> date;

	@FXML
	private Button addfilter;

	@FXML
	private Button deletefilter;

	@FXML
	private TreeTableColumn<Facility, String> address;

	@FXML
	private TreeTableColumn<Facility, Double> riskfactor;

	@FXML
	private TreeTableColumn<Facility, ArrayList<Integer>> violations;

	@FXML
	private TreeView<String> filters;

	@FXML
	private TreeTableColumn<Facility, Type> type;

	@FXML
	private Button help;

	@FXML
	private Button reset;

	@FXML
	private TreeTableColumn<Facility, Result> result;

	@FXML
	private TreeTableColumn<Facility, String> name;

	@FXML
	private TreeTableColumn<Facility, Risk> risk;

	@FXML
	private TreeTableView<Facility> table;

	private String helpdata = "Filtering this... \n blah blah";

	private HashMap<Integer, String> viols = new HashMap<>();

	@FXML
	void handleHelp(ActionEvent event) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Help");
		alert.setHeaderText("User Manual");
		alert.setContentText(helpdata);

		alert.showAndWait();
	}

	@FXML
	void handleReset(ActionEvent event) {
		filterdata.clear();
		filters.getRoot().getChildren().clear();
		
		try {
			Main.scene.setCursor(Cursor.WAIT);
			refresh();
		} finally {
			Main.scene.setCursor(Cursor.DEFAULT);
		}

		reset.setDisable(true);
		closest.setDisable(true);
	}

	@FXML
	void handleAddFilter(ActionEvent event) {
		Dialog<FilterData> dialog = new Dialog<>();
		dialog.setTitle("Add filter");
		dialog.setHeaderText("Fill in the information.");
		dialog.setResizable(false);

		CheckBox name = new CheckBox("Name:");
		CheckBox address = new CheckBox("Address:");
		CheckBox zip = new CheckBox("Zip:");
		CheckBox type = new CheckBox("Type:");
		CheckBox risk = new CheckBox("Risk:");
		CheckBox result = new CheckBox("Result:");

		TextField namedata = new TextField();
		TextField addressdata = new TextField();
		TextField zipdata = new TextField();
		ComboBox<Type> typedata = new ComboBox<Type>();
		ComboBox<Risk> riskdata = new ComboBox<Risk>();
		ComboBox<Result> resultdata = new ComboBox<Result>();

		typedata.getItems().setAll(Type.values());
		riskdata.getItems().setAll(Risk.values());
		resultdata.getItems().setAll(Result.values());

		typedata.getSelectionModel().select(0);
		riskdata.getSelectionModel().select(0);
		resultdata.getSelectionModel().select(0);

		zipdata.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> obs, String oldval, String newval) {
				if (!newval.matches("\\d*")) {
					zipdata.setText(newval.replaceAll("[^\\d]", ""));
				}
			}
		});

		GridPane grid = new GridPane();
		grid.add(name, 1, 1);
		grid.add(namedata, 2, 1);
		grid.add(address, 1, 2);
		grid.add(addressdata, 2, 2);
		grid.add(zip, 1, 3);
		grid.add(zipdata, 2, 3);
		grid.add(type, 1, 4);
		grid.add(typedata, 2, 4);
		grid.add(risk, 1, 5);
		grid.add(riskdata, 2, 5);
		grid.add(result, 1, 6);
		grid.add(resultdata, 2, 6);
		dialog.getDialogPane().setContent(grid);
		dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);

		dialog.setResultConverter((ButtonType b) -> {
			if (b == ButtonType.OK) {
				return new FilterData(name.isSelected(), address.isSelected(),
						zip.isSelected(), type.isSelected(), risk.isSelected(),
						result.isSelected(), namedata.getText(), addressdata
								.getText(), zipdata.getText().isEmpty() ? 0
								: Integer.parseInt(zipdata.getText()), typedata
								.getValue(), riskdata.getValue(), resultdata
								.getValue());
			}

			return null;
		});

		Optional<FilterData> resultd = dialog.showAndWait();

		if (resultd.isPresent()) {
			filterdata.add(resultd.get());
			TreeItem<String> f = new TreeItem<String>("Filter "
					+ filterdata.size());

			if (resultd.get().name)
				f.getChildren()
						.add(new TreeItem<String>("Name: "
								+ resultd.get().namedata));
			if (resultd.get().address)
				f.getChildren().add(
						new TreeItem<String>("Address: "
								+ resultd.get().addressdata));
			if (resultd.get().zip)
				f.getChildren().add(
						new TreeItem<String>("Zip: "
								+ String.valueOf(resultd.get().zipdata)));
			if (resultd.get().result)
				f.getChildren().add(
						new TreeItem<String>("Result: "
								+ resultd.get().resultdata.toString()));
			if (resultd.get().type)
				f.getChildren().add(
						new TreeItem<String>("Type: "
								+ resultd.get().typedata.toString()));
			if (resultd.get().risk)
				f.getChildren().add(
						new TreeItem<String>("Risk: "
								+ resultd.get().riskdata.toString()));

			drootf.getChildren().add(f);

			refresh();
		}
	}

	ArrayList<FilterData> filterdata = new ArrayList<FilterData>();

	private void refresh() {
		refresh(Data.facilities);
	}

	private void refresh(ArrayList<Facility> d) {
		ArrayList<Facility> data = d;
		HashSet<Facility> results = new HashSet<Facility>();

		if (filterdata.size() == 0) {
			results.addAll(data);
		}

		for (FilterData filter : filterdata) {
			results.addAll(Search
					.search(data,
							(a) -> {
								boolean matches = true;

								if (filter.name)
									matches = matches
											&& a.getName()
													.toLowerCase()
													.equals(filter.namedata
															.toLowerCase());
								if (filter.address)
									matches = matches
											&& a.getAddress()
													.toLowerCase()
													.equals(filter.addressdata
															.toLowerCase());
								if (filter.zip)
									matches = matches
											&& a.getZip() == filter.zipdata;
								if (filter.result)
									matches = matches
											&& a.getInspection(0).getResult() == filter.resultdata;
								if (filter.type)
									matches = matches
											&& a.getType() == filter.typedata;
								if (filter.risk)
									matches = matches
											&& a.getRisk() == filter.riskdata;

								return matches;
							}));
		}
		droot.getChildren().clear();

		for (Facility fac : results) {
			TreeItem<Facility> item = new TreeItem<Facility>(fac);

			for (Inspection i : fac.getInspections()) {
				TreeItem<Facility> inspection = new TreeItem<Facility>(
						new Facility(fac.getName(), fac.getLicense(),
								fac.getAddress(), fac.getZip(),
								fac.getLatitude(), fac.getLongitude(),
								fac.getType(), fac.getRisk(), i));
				item.getChildren().add(inspection);
			}

			droot.getChildren().add(item);
		}
	}

	@FXML
	void handleDeleteFilter(ActionEvent event) {
		int index = Integer.parseInt(filters
				.getSelectionModel()
				.getSelectedItem()
				.getValue()
				.substring(
						7,
						filters.getSelectionModel().getSelectedItem()
								.getValue().length())) - 1;
		try {
			filterdata.remove(index);
			filters.getRoot().getChildren().remove(index);
		} catch (Exception e) {
			return;
		}

		refresh();
	}

	TreeItem<Facility> droot;
	TreeItem<String> drootf;

	@FXML
	void handleFindClosest(ActionEvent event) {
		FacilityWeightedGraph graph = new FacilityWeightedGraph(Data.facilities);

		int l = table.getSelectionModel().getSelectedItem().getParent()
				.getValue().getLicense();
		int src = -1;
		for (int i = 0; i < Data.facilities.size(); i++) {
			if (Data.facilities.get(i).getLicense() == l) {
				src = i;
			}
		}

		if (src == -1)
			return;

		TextInputDialog dialog = new TextInputDialog("5");
		dialog.setTitle("Closest Restaurants");
		dialog.setHeaderText("Find Closest");
		dialog.setContentText("Please enter the amount:");
		dialog.getEditor().textProperty()
				.addListener(new ChangeListener<String>() {
					@Override
					public void changed(ObservableValue<? extends String> obs, String oldval, String newval) {
						if (!newval.matches("\\d*")) {
							dialog.getEditor().setText(
									newval.replaceAll("[^\\d]", ""));
						}
					}
				});

		Optional<String> result = dialog.showAndWait();
		if (result.isPresent()) {
			int n = Integer.parseInt(result.get());

			if (n == 0)
				return;

			ArrayList<Facility> facs = graph.getClosest(src, n);

			filterdata.clear();
			filters.getRoot().getChildren().clear();

			refresh(facs);

			reset.setDisable(false);
			closest.setDisable(true);
		}
	}

	@FXML
	private Button closest;

	private void readViols() {
		try {
			Scanner scnr = new Scanner(new File(System.getProperty("user.dir")
					+ "//assets//viols.txt"));

			while (scnr.hasNextLine()) {
				String[] data = scnr.nextLine().split("`");
				viols.put(Integer.parseInt(data[0]), data[1]);
			}

			scnr.close();
		} catch (Exception e) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Exception Dialog");
			alert.setHeaderText("Exception Dialog");
			alert.setContentText("Error reading viols.txt!");

			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			String ex = sw.toString();

			Label label = new Label("The exception stacktrace was:");

			TextArea textArea = new TextArea(ex);
			textArea.setEditable(false);
			textArea.setWrapText(true);

			textArea.setMaxWidth(Double.MAX_VALUE);
			textArea.setMaxHeight(Double.MAX_VALUE);
			GridPane.setVgrow(textArea, Priority.ALWAYS);
			GridPane.setHgrow(textArea, Priority.ALWAYS);

			GridPane expContent = new GridPane();
			expContent.setMaxWidth(Double.MAX_VALUE);
			expContent.add(label, 0, 0);
			expContent.add(textArea, 0, 1);

			alert.getDialogPane().setExpandableContent(expContent);

			alert.showAndWait();
		}
	}

	private void readHelp() {
		try {
			helpdata = new String(Files.readAllBytes(Paths.get(System
					.getProperty("user.dir") + "//assets//help.txt")));
		} catch (Exception e) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Exception Dialog");
			alert.setHeaderText("Exception Dialog");
			alert.setContentText("Error reading help.txt!");

			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			String ex = sw.toString();

			Label label = new Label("The exception stacktrace was:");

			TextArea textArea = new TextArea(ex);
			textArea.setEditable(false);
			textArea.setWrapText(true);

			textArea.setMaxWidth(Double.MAX_VALUE);
			textArea.setMaxHeight(Double.MAX_VALUE);
			GridPane.setVgrow(textArea, Priority.ALWAYS);
			GridPane.setHgrow(textArea, Priority.ALWAYS);

			GridPane expContent = new GridPane();
			expContent.setMaxWidth(Double.MAX_VALUE);
			expContent.add(label, 0, 0);
			expContent.add(textArea, 0, 1);

			alert.getDialogPane().setExpandableContent(expContent);

			alert.showAndWait();
		}
	}

	@FXML
	public void initialize() {
		readHelp();
		readViols();

		droot = new TreeItem<Facility>(Data.facilities.get(0));
		drootf = new TreeItem<String>("");
		drootf.setExpanded(true);

		filters.setRoot(drootf);

		filters.getSelectionModel().selectedItemProperty()
				.addListener(new ChangeListener<TreeItem<String>>() {
					@Override
					public void changed(ObservableValue<? extends TreeItem<String>> obs, TreeItem<String> oldval, TreeItem<String> newval) {

						TreeItem<String> selectedItem = newval;

						deletefilter.setDisable(selectedItem.getChildren()
								.size() == 0);
					}
				});

		for (Facility f : Data.facilities) {
			TreeItem<Facility> item = new TreeItem<Facility>(f);

			for (Inspection i : f.getInspections()) {
				TreeItem<Facility> inspection = new TreeItem<Facility>(
						new Facility(f.getName(), f.getLicense(),
								f.getAddress(), f.getZip(), f.getLatitude(),
								f.getLongitude(), f.getType(), f.getRisk(), i));
				item.getChildren().add(inspection);
			}

			droot.getChildren().add(item);
		}

		deletefilter.setDisable(true);

		name.setCellValueFactory((CellDataFeatures<Facility, String> param) -> new ReadOnlyObjectWrapper<String>(
				param.getValue().getValue().getName()));

		address.setCellValueFactory((CellDataFeatures<Facility, String> param) -> new ReadOnlyObjectWrapper<String>(
				param.getValue().getValue().getAddress()));

		zip.setCellValueFactory((CellDataFeatures<Facility, Integer> param) -> new ReadOnlyObjectWrapper<Integer>(
				param.getValue().getValue().getZip()));

		type.setCellValueFactory((CellDataFeatures<Facility, Type> param) -> new ReadOnlyObjectWrapper<Type>(
				param.getValue().getValue().getType()));

		risk.setCellValueFactory((CellDataFeatures<Facility, Risk> param) -> new ReadOnlyObjectWrapper<Risk>(
				param.getValue().getValue().getRisk()));

		riskfactor
				.setCellValueFactory((CellDataFeatures<Facility, Double> param) -> new ReadOnlyObjectWrapper<Double>(
						(int) (param.getValue().getValue()
								.getRecommndedFactor() * 100) / 100.0));

		result.setCellValueFactory((CellDataFeatures<Facility, Result> param) -> new ReadOnlyObjectWrapper<Result>(
				param.getValue().getValue().getInspection(0).getResult()));

		date.setCellValueFactory((CellDataFeatures<Facility, String> param) -> new ReadOnlyObjectWrapper<String>(
				new SimpleDateFormat("yyy-MM-dd").format(param.getValue()
						.getValue().getInspection(0).getDate())));

		violations
				.setCellValueFactory((CellDataFeatures<Facility, ArrayList<Integer>> param) -> new ReadOnlyObjectWrapper<ArrayList<Integer>>(
						(ArrayList<Integer>) Arrays
								.stream(param.getValue().getValue()
										.getInspection(0).getViolations())
								.boxed().collect(Collectors.toList())));

		table.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (event.getClickCount() == 2) {
					TreeItem<Facility> item = table.getSelectionModel()
							.getSelectedItem();
					try {
						if (item.isLeaf()) {
							String comp = "";// Show inspection violations
							for (int vin : item.getValue().getInspection(0)
									.getViolations()) {
								String vis = viols.get(vin);
								comp += "\n" + vis + "\n";
							}

							Alert alert = new Alert(AlertType.INFORMATION);
							alert.setTitle("Inspection Dialog");
							alert.setHeaderText("Inspection Dialog");
							alert.setContentText("Violations");

							Label label = new Label("The violations are:");

							TextArea textArea = new TextArea(comp);
							textArea.setEditable(false);
							textArea.setWrapText(true);

							textArea.setMaxWidth(Double.MAX_VALUE);
							textArea.setMaxHeight(Double.MAX_VALUE);
							GridPane.setVgrow(textArea, Priority.ALWAYS);
							GridPane.setHgrow(textArea, Priority.ALWAYS);

							GridPane c = new GridPane();
							c.setMaxWidth(Double.MAX_VALUE);
							c.add(label, 0, 0);
							c.add(textArea, 0, 1);

							// Set expandable into the dialog pane.
							alert.getDialogPane().setExpandableContent(c);
							alert.getDialogPane().setExpanded(true);

							alert.showAndWait();
						}
					} catch (Exception e) {
						return;
					}
				}
			}
		});

		table.getSelectionModel().selectedItemProperty()
				.addListener((obs, oldSelection, newSelection) -> {
					if (newSelection != null && newSelection.isLeaf()) {
						closest.setDisable(false);
					} else {
						closest.setDisable(true);
					}
				});

		droot.setExpanded(true);

		name.impl_setReorderable(false);
		address.impl_setReorderable(false);
		zip.impl_setReorderable(false);
		type.impl_setReorderable(false);
		risk.impl_setReorderable(false);
		riskfactor.impl_setReorderable(false);
		result.impl_setReorderable(false);
		date.impl_setReorderable(false);
		violations.impl_setReorderable(false);

		violations.setSortable(false);
		address.setSortable(false);

		table.setOnSort(sortEvent -> {
			while (table.getSortOrder().size() > 1) {
				table.getSortOrder().remove(1);
			}

			ArrayList<Facility> items = new ArrayList<Facility>();

			for (TreeItem<Facility> tif : table.getRoot().getChildren()) {
				items.add(tif.getValue());
			}

			Sort.Quick(items,
					(a, b) -> {
						if (table.getSortOrder().size() == 0)
							return 0;

						if (table.getSortOrder().get(0).getText()
								.equals("Name")) {
							return a.getName().compareTo(b.getName());
						} else if (table.getSortOrder().get(0).getText()
								.equals("Name")) {
							return a.getName().compareTo(b.getName());
						} else if (table.getSortOrder().get(0).getText()
								.equals("Name")) {
							return a.getName().compareTo(b.getName());
						} else if (table.getSortOrder().get(0).getText()
								.equals("Name")) {
							return a.getName().compareTo(b.getName());
						} else if (table.getSortOrder().get(0).getText()
								.equals("Name")) {
							return a.getName().compareTo(b.getName());
						} else if (table.getSortOrder().get(0).getText()
								.equals("Name")) {
							return a.getName().compareTo(b.getName());
						} else {
							return a.getName().compareTo(b.getName());
						}
					});
		});

		closest.setDisable(true);
		reset.setDisable(true);

		table.setRoot(droot);
		table.setShowRoot(false);
	}
}