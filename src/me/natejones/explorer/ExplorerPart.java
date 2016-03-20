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

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.e4.ui.di.PersistState;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swt.FXCanvas;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

public class ExplorerPart {
	private static final String PERSIST_KEY =
			ExplorerPart.class.getName() + ".path";
	private final ObservableList<Item> items =
			FXCollections.observableArrayList();
	@Inject
	private ESelectionService selectionService;
	private TextField pathCtl;

	@SuppressWarnings("unchecked")
	@PostConstruct
	public void postConstruct(Composite parent, MPart part) throws IOException {
		parent.setLayout(new FillLayout());
		FXCanvas canvas = new FXCanvas(parent, SWT.BORDER);
		GridPane grid = new GridPane();
		Button upBtn = new Button("Up");
		upBtn.setOnAction(e -> {
			Path p = Paths.get(pathCtl.getText());
			p = p.getParent();
			pathCtl.setText(p.toString());
			go();
		});
		pathCtl = new TextField();
		GridPane.setHgrow(pathCtl, Priority.ALWAYS);
		pathCtl.setOnAction(e -> go());
		Button goBtn = new Button("Go");
		goBtn.setOnMouseClicked(e -> go());

		grid.addRow(1, upBtn, pathCtl, goBtn);

		TableView<Item> table = new TableView<>(items);
		table.setOnMouseClicked(e -> {
			if (table.getSelectionModel().isEmpty())
				return;
			selectionService
					.setSelection(table.getSelectionModel().getSelectedItem());
			if (e.getClickCount() != 2)
				return;
			Item i = table.getSelectionModel().getSelectedItem();
			pathCtl.setText(i.getPath().toString());
			go();
		});
		TableColumn<Item, String> nameColumn = new TableColumn<>("Name");
		nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
		nameColumn.setPrefWidth(200);
		TableColumn<Item, Image> iconColumn = new TableColumn<>();
		iconColumn.setCellValueFactory(new PropertyValueFactory<>("icon"));
		iconColumn.setCellFactory(c -> new TableCell<Item, Image>() {
			@Override
			protected void updateItem(Image item, boolean empty) {
				ImageView img = new ImageView(item);
				img.setImage(item);
				setGraphic(img);
			}
		});
		iconColumn.setPrefWidth(25);
		TableColumn<Item, String> typeColumn = new TableColumn<>("Type");
		typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
		TableColumn<Item, Long> sizeColumn = new TableColumn<>("Size");
		sizeColumn.setCellValueFactory(new PropertyValueFactory<>("size"));
		table.getColumns().addAll(iconColumn, nameColumn, sizeColumn, typeColumn);
		grid.add(table, 0, 2, 3, 1);
		GridPane.setVgrow(table, Priority.ALWAYS);
		Scene scene = new Scene(grid);
		canvas.setScene(scene);

		String persisted = part.getPersistedState().get(PERSIST_KEY);
		if (persisted != null)
			pathCtl.setText(persisted);

		go();
	}

	@PersistState
	public void persistState(MPart part) {
		part.getPersistedState().put(PERSIST_KEY, pathCtl.getText());
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
				if (!Files.isDirectory(p)) {
					IFileStore fileStore =
							EFS.getLocalFileSystem().getStore(p.toUri());
					IWorkbenchPage page = PlatformUI.getWorkbench()
							.getActiveWorkbenchWindow().getActivePage();
					IDE.openEditorOnFileStore(page, fileStore);
					pathCtl.setText(p.getParent().toString());
					go();
					return;
				}
				pathCtl.setText(p.toString());
				newItems = Files.list(p);
			}
			items.clear();
			newItems.map(Item::new).forEach(items::add);
		}
		catch (IOException | PartInitException e) {
			throw new RuntimeException(e);
		}
	}
}