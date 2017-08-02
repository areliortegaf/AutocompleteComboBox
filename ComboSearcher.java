
package singletonfactory.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ComboBoxEditor;
import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import javax.swing.text.PlainDocument;
import singletonfactory.webservices.Autocomplete;

/**
 *
 * @author asortega
 */
public class ComboSearcher extends PlainDocument {

    //  PARAMETROS
    public static ParametrosCombo parametros = new ParametrosCombo();

    //AQUI SE CARGA TODO
    public JComboBox comboBox = new JComboBox();

    static String RESULTADO;
    ComboBoxModel model;
    JTextComponent editor;
    static int parametro;
    boolean selecting = false;
    boolean hidePopupOnFocusLoss;
    boolean hitBackspace = false;
    boolean hitBackspaceOnSelection;

    KeyListener editorKeyListener;
    FocusListener editorFocusListener;

    public ComboSearcher() {

        model = comboBox.getModel();
        comboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!selecting) {
                    highlightCompletedText(0);
                }
            }
        });
        comboBox.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent e) {
                if (e.getPropertyName().equals("editor")) {
                    configureEditor((ComboBoxEditor) e.getNewValue());
                }
                if (e.getPropertyName().equals("model")) {
                    model = (ComboBoxModel) e.getNewValue();
                }
            }
        });

        editorKeyListener = new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (comboBox.isDisplayable()) {
                    comboBox.setPopupVisible(true);
                }
                hitBackspace = false;
                switch (e.getKeyCode()) {

                    case KeyEvent.VK_BACK_SPACE:
                        hitBackspace = true;
                        hitBackspaceOnSelection = editor.getSelectionStart() != editor.getSelectionEnd();
                        break;

                    case KeyEvent.VK_DELETE:
                        e.consume();
                        comboBox.getToolkit().beep();
                        break;
                }
            }
        };

        hidePopupOnFocusLoss = System.getProperty("java.version").startsWith("1.5");
        editorFocusListener = new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                highlightCompletedText(0);
            }

            public void focusLost(FocusEvent e) {
                if (hidePopupOnFocusLoss) {
                    comboBox.setPopupVisible(true);
                }
            }
        };
        configureEditor(comboBox.getEditor());
        Object selected = comboBox.getSelectedItem();
        if (selected != null) {
            setText(selected.toString());
        }
        highlightCompletedText(0);

        createAndShowGUI();

    }//TERMINA CONSTRUCTOR

    public static void enable(JComboBox comboBox) {
        // has to be editable
        comboBox.setEditable(true);
        // change the editor's document
        new AutoCompletion(comboBox);
    }

    void configureEditor(ComboBoxEditor newEditor) {
        if (editor != null) {
            editor.removeKeyListener(editorKeyListener);
            editor.removeFocusListener(editorFocusListener);
        }

        if (newEditor != null) {
            editor = (JTextComponent) newEditor.getEditorComponent();
            editor.addKeyListener(editorKeyListener);
            editor.addFocusListener(editorFocusListener);
            editor.setDocument(this);
        }
    }

    public void remove(int offs, int len) throws BadLocationException {

        if (selecting) {
            return;
        }
        if (hitBackspace) {

            if (offs > 0) {
                if (hitBackspaceOnSelection) {
                    offs--;
                }
            } else {
                comboBox.getToolkit().beep();
            }
            highlightCompletedText(offs);
        } else {
            super.remove(offs, len);
        }
    }

    public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
        if (selecting) {
            return;
        }
        super.insertString(offs, str, a);
        Object item = lookupItem(getText(0, getLength()));
        if (item != null) {
            setSelectedItem(item);
        } else {
            item = comboBox.getSelectedItem();//COMENTE ESTO PARA VER LO QUE ESTABA INGRESANDO
            //CARACTEER X CARACTER
            offs = offs - str.length();
        }
        setText(item.toString());
        highlightCompletedText(offs + str.length());
    }

    private void setText(String text) {//aqui se meten las predicciones
        try {
            super.remove(0, getLength());
            super.insertString(0, text, null); //la prediccion se inserta antes del texto
            //super.replace(0, text.length(), text, null);

        } catch (BadLocationException e) {
            throw new RuntimeException(e.toString());
        }
    }

    private void highlightCompletedText(int start) {
        editor.setCaretPosition(getLength());
        //editor.moveCaretPosition(start);
    }

    private void setSelectedItem(Object item) {
        selecting = true;
        model.setSelectedItem(item);
        selecting = false;
    }

    private Object lookupItem(String pattern) {
        Object selectedItem = model.getSelectedItem();
        if (selectedItem != null && startsWithIgnoreCase(selectedItem.toString(), pattern)) {
            return selectedItem;
        } else {
            for (int i = 0, n = model.getSize(); i < n; i++) {
                Object currentItem = model.getElementAt(i);
                if (currentItem != null && startsWithIgnoreCase(currentItem.toString(), pattern)) {
                    return currentItem;
                }
            }
        }
        return null;
    }

    private boolean startsWithIgnoreCase(String str1, String str2) {
        return str1.toUpperCase().startsWith(str2.toUpperCase());
    }

    public void createAndShowGUI() {

        // TRAE YA LOS RESULTADOS
        //Autocomplete resultados = new Autocomplete();
        //List lista = resultados.buscar("");//arreglo con valores
        List<String> arregloVacio = new ArrayList<>(); //arreglo vacio

        String[] array = (String[]) arregloVacio.toArray(new String[arregloVacio.size()]);
        //DefaultComboBoxModel model = new DefaultComboBoxModel(array);
        //combo.setModel(model);

        final JComboBox comboBox = new JComboBox(array);
        enable(comboBox);
        comboBox.getEditor().getEditorComponent().addKeyListener(new KeyAdapter() {

            @Override
            public void keyReleased(KeyEvent event) {
                if (event.getKeyChar() == KeyEvent.VK_ENTER) {
                    if (((JTextComponent) ((JComboBox) ((Component) event
                            .getSource()).getParent()).getEditor()
                            .getEditorComponent()).getText().isEmpty()) {
                        System.out.println("please dont make me blank");
                    }
                }
            }
            //concatena los caracteres escits para buscar una ubicacion
            String concat = "";

            //agregar los demas metodos
            @Override
            public void keyTyped(KeyEvent e) {

                concat = concat + e.getKeyChar();
                System.out.println(concat);

                //castea para obtener valor del unicode
                int unicodeKey = (int) e.getKeyChar();
                if (unicodeKey == 8) {
                    for (int i = 0; i <= concat.length(); i++) {
                        concat = concat.substring(0, concat.length() - i);
                        System.out.println(concat);
                    }
                }
                if (unicodeKey == 27) {
                    //CODIGO DE LA TECLA ESCAPE
                    concat = "a";
                }
                //System.out.println(unicodeKey);
                if (unicodeKey == 10) {
                    //clave letra enter
                    concat = comboBox.getSelectedItem().toString();
                    RESULTADO = comboBox.getSelectedItem().toString();
                    System.out.println("RESULTADO desde el combo: " + RESULTADO);
                    //mandar resultado
                    parametros.setResultadoBox(RESULTADO);
                    parametros.setParametro(1);

                }

                //VALIDAR AQUI ANTES DE QUE CONCAT TENGA VALOR
                Autocomplete resultados = new Autocomplete();
                List listaValores = resultados.buscar(concat);//arreglo con valores

                //BORRA TODOS LOS VALORES DEL COMBOBOX
                comboBox.removeAllItems();

                for (int i = 0; i < listaValores.size(); i++) {//ACTUALIZA LA DROPLIST
                    //comboBox.removeAllItems();//solo muestra el primer resultado

                    String prediccion = listaValores.get(i).toString();

                    comboBox.addItem(prediccion);

                }
                if (unicodeKey == 13 && concat != null) {
                    System.out.println(listaValores.get(0).toString());
                }

            }//aqui termina el key typed

        });

        Dimension preferredSize = comboBox.getPreferredSize();
        preferredSize.height = 33;//para que muestre el los 3 resultados
        preferredSize.width = 800;
        comboBox.setPreferredSize(preferredSize);
        comboBox.setBackground(Color.LIGHT_GRAY);

        comboBox.setVisible(true);
        //ACTUALIZA EL ESTADO DEL COMBOBOX
        actualizaBox(comboBox);
    }

    public void actualizaBox(JComboBox combo) {
        this.comboBox = combo;
    }

    public JComboBox traerBox() {
        return this.comboBox;
    }

    /*public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFrame frame = new JFrame();
               new ComboSearcher();
                
            }
        });
    }*/
}
