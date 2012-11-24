package dialogs;

import java.awt.Frame;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.AbstractDocument;

import drawable.Marker;

public class MarkerDialog extends JDialog implements PropertyChangeListener {

	private static final long serialVersionUID = 1L;

	// nomi dei pulsanti di conferma o concellazione
	private static final String yes = "Ok";
	private static final String no = "Cancel";
	private static final String try_again = "Correggi";

	// titolo della finestra
	private static final String title = "Inserisci il tipo di marker";

	// costanti di lunghezza dei textField
	private static final int RFID_LENGHT = 15;
	private static final int GENERIC_INPUT_LENGHT = 150;
	private static final int GENERIC_INPUT_LINES = 3;

	// testo degli errori
	private static final String ERROR_EMPTY_TEXT = "Errore! Immettere dei valori nei campi selezionati!";
	private static final String ERROR_PREFIX = "Errore! Il valore inserito nel campo ";
	private static final String ERROR_SUFFIX_RFID = " è già stato utilizzato in un altro punto!";
	private static final String ERROR_SUFFIX_OTHER = " è già stato usato per questo piano!";
	private static final String ERROR_NO_INPUT = "Devi introdurre almeno un dato per salvare!";

	// testo dei checkbox
	private static final String RFID_CHECKBOX = "Abilita il codice RFID";
	private static final String ACCESS_CHECKBOX = "Imposta come ingresso";
	private static final String ROOM_CHECKBOX = "Imposta come stanza";
	private static final String STAIRS_CHECKBOX = "Imposta come scale";
	private static final String ELEVATOR_CHECKBOX = "Imposta come ascensore";

	// testo dei jTextField
	private static final String GENERIC_TEXT_ROOM = "Inserisci il nome della stanza o del personale presente, separati da una virgola.";
	private static final String GENERIC_TEXT_ST_EL = "Inserisci un nome identificativo, dovrà essere coerente con gli altri piani.";

	// testo BASE del drop-down menu
	private static final String ADD_NEW_EL_ST = "Crea nuovo identificativo";

	// aree di input testuale
	private JTextField RFID_textField = new JTextField(RFID_LENGHT);
	private JTextArea generic_input = new JTextArea();

	// checkBox
	private JCheckBox RFID_checkBox = new JCheckBox(RFID_CHECKBOX, false);
	private JCheckBox access_checkBox = new JCheckBox(ACCESS_CHECKBOX, false);
	private JCheckBox room_checkBox = new JCheckBox(ROOM_CHECKBOX, false);
	private JCheckBox stairs_checkBox = new JCheckBox(STAIRS_CHECKBOX, false);
	private JCheckBox elevator_checkBox = new JCheckBox(ELEVATOR_CHECKBOX,
			false);

	// contenitore di tutto il dialogo
	private JOptionPane optionPane;

	// variabili interne, mi servono per sapere quali chechbox sono abilitati
	private boolean access = false;
	private boolean room = false;
	private boolean stairs = false;
	private boolean elevator = false;

	// array salvati da piani precedenti, da proporre per scelta rapida
	private String[] elevators_others_floors;
	private String[] stairs_others_floors;
	private String[] RFID_others;

	// array di QUESTO piano, per evitare ripetizioni
	private String[] elevators_this_floor;
	private String[] stairs_this_floor;

	// Drop-Down Menu per visualizzare le scale e gli ascensori già inseriti su
	// altri piani
	private JComboBox choose_el_st_comboBox = new JComboBox();

	// array degli oggetti di input da mostrare
	private Object[] params = { RFID_checkBox, RFID_textField, access_checkBox,
			room_checkBox, stairs_checkBox, elevator_checkBox,
			choose_el_st_comboBox, new JScrollPane(generic_input) };

	// array dei bottoni di salvataggio e cancellazione
	private Object[] options = { yes, no };

	// array di oggetti di output
	private Object[] output = { null, null, null, null, null };

	// Nel caso venga editato un marker già esistente
	private Marker m = null;

	// //////////////////////////////////////////////////////////////////////////////////////////////////////
	// //////////////////////////////////////////////////////////////////////////////////////////////////////
	// //////////////////////////////////////////////////////////////////////////////////////////////////////

	// costruttore
	public MarkerDialog(Frame aFrame, Object[] this_floor,
			Object[] other_floors, Marker m) {

		super(aFrame, true);

		buildReferenceArrays(other_floors, this_floor);

		setDialogParameters();

		if (m != null)
			setExistingMarkerData(m);

		// Creo il JOptionPane.
		optionPane = new JOptionPane(params, JOptionPane.PLAIN_MESSAGE,
				JOptionPane.YES_NO_OPTION, null, options, options[1]);

		// Mostro nel dialogo quello che ho creato sopra
		setContentPane(optionPane);

		// imposto i listener dei checkBox
		setCheckBoxListeners();

		// imposto i listeners dei textField
		setTextFieldListeners();

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

			if (value == yes) {
				boolean RFID_test = testRFID();
				boolean stairs_test = testStairs();
				boolean elevator_test = testElevators();
				boolean room_test = testRoom();
				boolean is_input_test = isInput();

				String message = "";
				JComponent textField = null;

				if (RFID_test && stairs_test && elevator_test && is_input_test
						&& room_test) {
					// we're done; clear and dismiss the dialog
					createOutputArray();
					clearAndHide();
					return;
				} else if (!is_input_test) {
					message = ERROR_NO_INPUT;
					textField = RFID_textField;
				} else if (!RFID_test) {
					String s = RFID_textField.getText();
					textField = RFID_textField;
					if (s == null)
						message = ERROR_EMPTY_TEXT;
					else if (s.isEmpty())
						message = ERROR_EMPTY_TEXT;
					else
						message = ERROR_PREFIX + "RFID" + ERROR_SUFFIX_RFID;
				} else if (!stairs_test) {
					String s = generic_input.getText();
					textField = generic_input;
					if (s == null)
						message = ERROR_EMPTY_TEXT;
					else if (s.isEmpty())
						message = ERROR_EMPTY_TEXT;
					else
						message = ERROR_PREFIX + "SCALE" + ERROR_SUFFIX_OTHER;
				} else if (!elevator_test) {
					String s = generic_input.getText();
					textField = generic_input;
					if (s == null)
						message = ERROR_EMPTY_TEXT;
					else if (s.isEmpty())
						message = ERROR_EMPTY_TEXT;
					else
						message = ERROR_PREFIX + "ASCENSORE"
								+ ERROR_SUFFIX_OTHER;
				} else if (!room_test) {
					textField = generic_input;
					message = ERROR_EMPTY_TEXT;
					generic_input.setText(null);
				}

				JOptionPane.showMessageDialog(MarkerDialog.this, message,
						try_again, JOptionPane.ERROR_MESSAGE);

				textField.requestFocusInWindow();
			} else
				clearAndHide();
		}
	}

	/** This method clears the dialog and hides it. */
	public void clearAndHide() {
		RFID_textField.setText(null);
		generic_input.setText(null);

		stairs_checkBox.setSelected(false);
		RFID_checkBox.setSelected(false);
		elevator_checkBox.setSelected(false);
		room_checkBox.setSelected(false);
		access_checkBox.setSelected(false);

		choose_el_st_comboBox.removeAllItems();

		setVisible(false);
	}

	// verifico se è stato selezionato qualcosa
	private boolean isInput() {
		return stairs_checkBox.isSelected() || elevator_checkBox.isSelected()
				|| RFID_checkBox.isSelected() || access_checkBox.isSelected()
				|| room_checkBox.isSelected();
	}

	// verifico se il campo room è selezionato, e se i dati sono validi
	private boolean testRoom() {

		if (!room_checkBox.isSelected())
			return true;

		String room = generic_input.getText();

		if (room.equals(GENERIC_TEXT_ROOM))
			return false;

		return true;
	}

	// verifico se il campo Stair è selezionato, in quel caso verifico che NON
	// sia vuoto
	// e che non sia un duplicato di uno già presente sul piano
	private boolean testStairs() {

		if (!stairs_checkBox.isSelected())
			return true;

		if (choose_el_st_comboBox.getSelectedIndex() > 0)
			return true;

		String stairs = generic_input.getText();
		if (stairs == null)
			return false;
		if (stairs.isEmpty())
			return false;
		if (stairs.equals(GENERIC_TEXT_ST_EL))
			return false;

		try {
			for (String i : stairs_this_floor) {
				if (m != null) {
					if (i.equals(stairs)
							&& !(i.equals(m.generic_data) && m.stair))
						return false;
				} else if (i.equals(stairs))
					return false;
			}
		} catch (NullPointerException npe) {
		}

		return true;
	}

	// verifico se il campo Elevator è selezionato, in quel caso verifico che
	// NON sia vuoto
	// e che non sia un duplicato di uno già presente sul piano
	private boolean testElevators() {

		if (!elevator_checkBox.isSelected())
			return true;

		if (choose_el_st_comboBox.getSelectedIndex() > 0)
			return true;

		String elevator = generic_input.getText();
		if (elevator == null)
			return false;
		if (elevator.isEmpty())
			return false;
		if (elevator.equals(GENERIC_TEXT_ST_EL))
			return false;

		try {
			for (String i : elevators_this_floor) {
				if (m != null) {
					if (i.equals(elevator)
							&& !(i.equals(m.generic_data) && m.elevator))
						return false;
				} else if (i.equals(elevator))
					return false;
			}
		} catch (NullPointerException npe) {
		}

		return true;
	}

	// verifico se il campo RFID è selezionato, in quel caso verifico che NON
	// sia vuoto
	// e che non sia un duplicato
	private boolean testRFID() {

		if (!RFID_checkBox.isSelected())
			return true;

		String RFID = RFID_textField.getText();
		if (RFID == null)
			return false;
		if (RFID.isEmpty())
			return false;

		try {
			for (String i : RFID_others) {
				if (m != null) {
					if (i.equals(RFID) && (!i.equals(m.RFID_data)))
						return false;
				} else if (i.equals(RFID))
					return false;
			}

		} catch (NullPointerException npe) {
		}
		/*
		 * for(String i: RFID_this_floor) if(i.equals(RFID)) return false;
		 */
		return true;
	}

	private void setCheckBoxListeners() {

		RFID_checkBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent arg0) {
				RFID_textField.setEnabled(RFID_checkBox.isSelected());
			}
		});

		access_checkBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent arg0) {
				access = access_checkBox.isSelected();
				room_checkBox.setEnabled(!access);
				stairs_checkBox.setEnabled(!access);
				elevator_checkBox.setEnabled(!access);
				generic_input.setEnabled(!access);
				if (access)
					generic_input.setText(null);
			}
		});

		room_checkBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent arg0) {
				room = room_checkBox.isSelected();
				access_checkBox.setEnabled(!room);
				stairs_checkBox.setEnabled(!room);
				elevator_checkBox.setEnabled(!room);
				generic_input.setEnabled(room);
				if (room)
					generic_input.setText(GENERIC_TEXT_ROOM);
				else
					generic_input.setText(null);
			}
		});

		stairs_checkBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent arg0) {
				stairs = stairs_checkBox.isSelected();
				access_checkBox.setEnabled(!stairs);
				room_checkBox.setEnabled(!stairs);
				elevator_checkBox.setEnabled(!stairs);
				generic_input.setEnabled(stairs);
				choose_el_st_comboBox.setEnabled(stairs);
				if (stairs) {
					generic_input.setText(GENERIC_TEXT_ST_EL);

					// aggiungo al Drop-Down i valori e lo abilito
					// il PRIMO elemento della lista DEVE essere
					// "Aggiungi nuova scala/ascensore"
					choose_el_st_comboBox.addItem(ADD_NEW_EL_ST);

					try {
						for (String i : stairs_others_floors)
							choose_el_st_comboBox.addItem(i);
					} catch (NullPointerException npe) {
					}
				} else {
					generic_input.setText(null);
					// tolgo tutti gli elementi al dropdown
					choose_el_st_comboBox.removeAllItems();
				}
			}
		});

		elevator_checkBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent arg0) {
				elevator = elevator_checkBox.isSelected();
				access_checkBox.setEnabled(!elevator);
				room_checkBox.setEnabled(!elevator);
				stairs_checkBox.setEnabled(!elevator);
				generic_input.setEnabled(elevator);
				choose_el_st_comboBox.setEnabled(elevator);
				if (elevator) {

					// stesse cose di quello sopra
					choose_el_st_comboBox.addItem(ADD_NEW_EL_ST);

					generic_input.setText(GENERIC_TEXT_ST_EL);

					try {
						for (String i : elevators_others_floors)
							choose_el_st_comboBox.addItem(i);
					} catch (NullPointerException npe) {
					}
				} else {
					generic_input.setText(null);
					choose_el_st_comboBox.removeAllItems();
				}
			}
		});

	}

	// imposto i listener dei textField
	private void setTextFieldListeners() {

		// semplice listener per "eliminare" il testo di default del
		// "generic input"
		// al click dell'utente
		generic_input.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {

				String text = generic_input.getText();
				if (text.matches(GENERIC_TEXT_ST_EL)
						|| text.matches(GENERIC_TEXT_ROOM))
					generic_input.setText(null);
			}
		});

		// listener sugli elementi del drop-down menu
		choose_el_st_comboBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent arg0) {

				// SE l'elemento selezionato è lo 0 (aggiungi un nuovo
				// ascensore/scala)
				// abilito il generic input
				if ((elevator || stairs)
						&& (choose_el_st_comboBox.getSelectedIndex() == 0)) {
					generic_input.setText(GENERIC_TEXT_ST_EL);
					generic_input.setEnabled(true);
				} else {
					// altrimenti blocco il generic input
					generic_input.setText(null);
					generic_input.setEnabled(false);
				}
			}
		});
	}

	// imposto alcuni parameti del dialog
	private void setDialogParameters() {

		// imposto il titolo
		setTitle(title);

		// disabilito i textfield e il drop-down
		RFID_textField.setEnabled(false);
		choose_el_st_comboBox.setEnabled(false);
		generic_input.setEnabled(false);

		// imposto parametri per il JTextArea
		generic_input.setAutoscrolls(true);
		generic_input.setRows(GENERIC_INPUT_LINES);
		generic_input.setLineWrap(true);
		generic_input.setWrapStyleWord(true);
		// imposto il limite di caratteri per il JTextArea
		((AbstractDocument) generic_input.getDocument())
				.setDocumentFilter(new MyDocumentFilter(generic_input,
						GENERIC_INPUT_LENGHT));
	}

	// importo le liste di riferimento degli altri piani
	private void buildReferenceArrays(Object[] other_floors, Object[] this_floor) {

		RFID_others = validateInputArrays(other_floors, 0);
		elevators_others_floors = validateInputArrays(other_floors, 1);
		stairs_others_floors = validateInputArrays(other_floors, 2);

		elevators_this_floor = validateInputArrays(this_floor, 0);
		stairs_this_floor = validateInputArrays(this_floor, 1);
	}

	// funzione per evitare la nullPointerException nel caso non ci siano
	// scale, rfid o ascensori
	private String[] validateInputArrays(Object[] external, int position) {

		try {
			return (String[]) external[position];
		} catch (Exception e) {
			return null;
		}
	}

	private void createOutputArray() {

		if (RFID_checkBox.isSelected())
			output[0] = RFID_textField.getText();

		if (access_checkBox.isSelected())
			output[1] = "true";

		else if (room_checkBox.isSelected())
			output[2] = generic_input.getText();

		else if (stairs_checkBox.isSelected()) {
			if (generic_input.isEnabled())
				output[3] = generic_input.getText();
			else
				output[3] = (String) choose_el_st_comboBox.getSelectedItem();
		} else if (elevator_checkBox.isSelected()) {
			if (generic_input.isEnabled())
				output[4] = generic_input.getText();
			else
				output[4] = (String) choose_el_st_comboBox.getSelectedItem();
		}
	}

	// restituisce i valori immessi dall'utente, oppure null
	public Object[] getValidatedData() {

		for (Object i : output)
			if (i != null)
				return output;

		return null;
	}

	// nel caso stia editando un marker già esistente, con questa funzione
	// carico i dati
	// del marker nel dialog
	private void setExistingMarkerData(Marker m) {

		this.m = m;

		if (m.RFID) {
			RFID_checkBox.setSelected(true);
			RFID_textField.setText(m.RFID_data);
			RFID_textField.setEnabled(true);
		}

		if (m.access)
			access_checkBox.setSelected(true);

		else if (m.room) {
			room_checkBox.setSelected(true);
			generic_input.setText(m.generic_data);
			generic_input.setEnabled(true);
		}

		else if (m.stair) {
			stairs_checkBox.setSelected(true);
			generic_input.setText(m.generic_data);
			generic_input.setEnabled(true);
		}

		else if (m.elevator) {
			elevator_checkBox.setSelected(true);
			generic_input.setText(m.generic_data);
			generic_input.setEnabled(true);
		}
	}
}
