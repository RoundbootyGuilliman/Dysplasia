package deprecated;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

public final class DraggablePanelsExample extends Application {
	
	private final BooleanProperty dragModeActiveProperty = new SimpleBooleanProperty(this, "dragModeActive", true);
	
	public static void main(final String[] args) {
		launch(args);
	}
	
	private static Node createProgressPanel() {
		final Slider slider = new Slider();
		
		final ProgressIndicator progressIndicator = new ProgressIndicator(0);
		progressIndicator.progressProperty().bind(Bindings.divide(slider.valueProperty(), slider.maxProperty()));
		
		final HBox panel = createHBox(6, new Label("Progress:"), slider, progressIndicator);
		configureBorder(panel);
		
		return panel;
	}
	
	private static void configureBorder(final Region region) {
		region.setStyle("-fx-background-color: white;"
				+ "-fx-border-color: black;"
				+ "-fx-border-width: 1;"
				+ "-fx-border-radius: 6;"
				+ "-fx-padding: 6;");
	}
	
	private static HBox createHBox(final double spacing, final Node... children) {
		final HBox hbox = new HBox(spacing);
		hbox.getChildren().addAll(children);
		
		return hbox;
	}
	
	@Override
	public void start(final Stage stage) {

//		final Node progressPanel = createProgressPanel();
//		enableDrag(progressPanel);
		
		final Node progressPanel = makeDraggable(createProgressPanel());
		
		progressPanel.relocate(0, 0);
		
		final Pane panelsPane = new Pane();
		panelsPane.getChildren().addAll(progressPanel);
		
		final BorderPane sceneRoot = new BorderPane();
		
		BorderPane.setAlignment(panelsPane, Pos.TOP_LEFT);
		sceneRoot.setCenter(panelsPane);
		
		final CheckBox dragModeCheckbox = new CheckBox("Drag mode");
		BorderPane.setMargin(dragModeCheckbox, new Insets(6));
		sceneRoot.setBottom(dragModeCheckbox);
		
		dragModeActiveProperty.bind(dragModeCheckbox.selectedProperty());
		
		final Scene scene = new Scene(sceneRoot, 400, 300);
		stage.setScene(scene);
		stage.setTitle("Draggable Panels Example");
		stage.show();
	}
	
	private Node makeDraggable(final Node node) {
		
		final DragContext dragContext = new DragContext();
		
		final Group wrapGroup = new Group(node);
		
		wrapGroup.addEventFilter(
				MouseEvent.ANY,
				mouseEvent -> {
					if (dragModeActiveProperty.get()) {
						mouseEvent.consume();
					}
				});
		
		wrapGroup.addEventFilter(
				MouseEvent.MOUSE_PRESSED,
				mouseEvent -> {
					if (dragModeActiveProperty.get()) {
						dragContext.mouseAnchorX = mouseEvent.getX();
						dragContext.mouseAnchorY = mouseEvent.getY();
						dragContext.initialTranslateX = node.getTranslateX();
						dragContext.initialTranslateY = node.getTranslateY();
					}
				});
		
		
		wrapGroup.addEventFilter(
				MouseEvent.MOUSE_DRAGGED,
				mouseEvent -> {
					if (dragModeActiveProperty.get()) {
						node.setTranslateX(
								dragContext.initialTranslateX
										+ mouseEvent.getX()
										- dragContext.mouseAnchorX);
						node.setTranslateY(
								dragContext.initialTranslateY
										+ mouseEvent.getY()
										- dragContext.mouseAnchorY);
					}
				});
		
		return wrapGroup;
	}
	
	private static final class DragContext {
		double mouseAnchorX;
		double mouseAnchorY;
		double initialTranslateX;
		double initialTranslateY;
	}
}

