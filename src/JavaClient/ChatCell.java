package JavaClient;

import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

public class ChatCell extends ListCell<Message> {

    private HBox message;
    private Pane pane;
    private Label text;
    Button accept;
    Button decline;
    private Message m;

    public ChatCell(Message m) {
        this.m = m;
        if(this.isEmpty()) System.out.println("empty");
        message = new HBox();
        pane = new Pane();
        text = new Label("<empty>");
        accept = new Button("Accept");
        decline = new Button("Decline");

        accept.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                m.setConfirmed(true);
                System.out.println("accepted");
            }
        });

        decline.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                m.setConfirmed(false);
                System.out.println("declined");
            }
        });
    }

    public void updateItem(Message m, boolean empty) {
        super.updateItem(m, empty);
        setText(null);
        setGraphic(null);
        message.getChildren().remove(0, message.getChildren().size());

        if(m == null || empty) {
            setGraphic(null);
            return;
        }
        if (m.mustConfirm()) {
            text.setText(m.getMessage());
            message.getChildren().addAll(text, pane, accept, decline);
            setGraphic(message);
        }else {
            String messageString = m.getTimestamp() + "\t" + m.getUsername() + "" + "\n"
                    + m.getMessage();
            setText(messageString);
        }
    }
}
