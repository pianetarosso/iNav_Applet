package dialogs;

import java.awt.Frame;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

public class ErrorDialog extends JDialog implements PropertyChangeListener {

	private static final long serialVersionUID = 1L;

	// nomi dei pulsanti di conferma o concellazione
	private static final String yes = "Ok";

	// contenitore di tutto il dialogo
	private JOptionPane optionPane;

	// array dei bottoni di salvataggio e cancellazione
	private Object[] options = { yes };

	private static final String TITLE = "Errore!!!";

	// //////////////////////////////////////////////////////////////////////////////////////////////////////
	// //////////////////////////////////////////////////////////////////////////////////////////////////////
	// //////////////////////////////////////////////////////////////////////////////////////////////////////

	// costruttore
	public ErrorDialog(Frame aFrame, String message) {

		super(aFrame, true);

		// imposto il titolo
		setTitle(TITLE);

		// oggetti da mostrare
		Object[] params = { message };

		// Creo il JOptionPane.
		optionPane = new JOptionPane(params, JOptionPane.ERROR_MESSAGE,
				JOptionPane.OK_OPTION, null, options, options[0]);

		// Mostro nel dialogo quello che ho creato sopra
		setContentPane(optionPane);

		// Register an event handler that reacts to option pane state changes.
		optionPane.addPropertyChangeListener(this);
	}

	/** This method reacts to state changes in the option pane. */
	public void propertyChange(PropertyChangeEvent e) {
		String prop = e.getPropertyName();

		if (isVisible()
				&& (e.getSource() == optionPane)
				&& (JOptionPane.VALUE_PROPERTY.equals(prop) || JOptionPane.INPUT_VALUE_PROPERTY
						.equals(prop))) {
			Object value = optionPane.getValue();

			if (value == JOptionPane.UNINITIALIZED_VALUE) {
				// ignore reset
				return;
			}

			// Reset the JOptionPane's value.
			// If you don't do this, then if the user
			// presses the same button next time, no
			// property change event will be fired.
			optionPane.setValue(JOptionPane.UNINITIALIZED_VALUE);

			clearAndHide();
		}
	}

	/** This method clears the dialog and hides it. */
	public void clearAndHide() {
		setVisible(false);
	}

	// restituisce la scelta dell'utente
	public boolean getValidatedData() {
		return false;
	}
}
