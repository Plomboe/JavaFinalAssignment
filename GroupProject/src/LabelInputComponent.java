import javafx.scene.layout.GridPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class LabelInputComponent {
	
	LabelInputComponent(String LabelName)
	{
		mLabel = new Label(LabelName + ":");
		mTextField = new TextField();
	}
	
	public void CreateComponent(GridPane Pane, int Order)
	{
		Pane.add(mLabel, 0, Order);
		Pane.add(mTextField, 1, Order);
	}
	
	public void DisableEditing()
	{
		mTextField.setDisable(true);
	}
	
	public boolean HasValue()
	{
		boolean Empty = mTextField.getText().isEmpty();
		return !mTextField.getText().isEmpty();
	}
	
	public String GetValue()
	{		
		return mTextField.getText();
	}
	
	public TextField getTextField() {
		return mTextField;
	}
	
    private	Label 		mLabel;
	private TextField	mTextField;
}
