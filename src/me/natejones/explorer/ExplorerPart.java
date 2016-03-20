
package me.natejones.explorer;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swt.FXCanvas;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

public class ExplorerPart {
	private final ObservableList<Item> items =
			FXCollections.observableArrayList();
	private TextField pathCtl;
	@Inject
	private ESelectionService selectionService;

	@PostConstruct
	public void postConstruct(Composite parent, MPart part) throws IOException {
		parent.setLayout(new FillLayout());
		FXCanvas canvas = new FXCanvas(parent, SWT.BORDER);
		GridPane grid = new GridPane();
		pathCtl = new TextField();
		grid.add(pathCtl, 1, 1);
		GridPane.setHgrow(pathCtl, Priority.ALWAYS);
		Button goBtn = new Button("Go");
		goBtn.setOnMouseClicked(e -> go());
		grid.add(goBtn, 2, 1);
		TableView<Item> table = new TableView<>(items);
		table.setOnMouseClicked(e -> {
			if (table.getSelectionModel().isEmpty())
				return;
			selectionService
					.setSelection(table.getSelectionModel().getSelectedItem());
			if (e.getClickCount() != 2)
				return;
			Item i = table.getSelectionModel().getSelectedItem();
			pathCtl.setText(i.getName());
			go();
		});
		TableColumn<Item, String> nameColumn = new TableColumn<>("Name");
		nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
		table.getColumns().add(nameColumn);
		grid.add(table, 1, 2, 2, 1);
		GridPane.setVgrow(table, Priority.ALWAYS);
		Scene scene = new Scene(grid);
		canvas.setScene(scene);
		go();
	}

	private void go() {
		Stream<Path> newItems;
		String pstr = pathCtl.getText().trim();
		try {
			if (pstr.isEmpty())
				newItems = StreamSupport.stream(
						FileSystems.getDefault().getRootDirectories().spliterator(),
						false);
			else {
				Path p = Paths.get(pstr).toRealPath().toAbsolutePath();
				if (!Files.isDirectory(p))
					return;
				pathCtl.setText(p.toString());
				newItems = Files.list(p);
			}
			items.clear();
			newItems.map(Item::new).forEach(items::add);
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}