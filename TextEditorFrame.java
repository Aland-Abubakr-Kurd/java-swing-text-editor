import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.event.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

// Based on a tutorial by Bro Code
// Original video: https://youtu.be/NKjqAQAtq-g
// I do not claim ownership of the original code.
// You may notice some slight modifications, but the core logic remains largely the same as in the original tutorial.

// TextEditorFrame handles the GUI and all the underlying logic for 
// typing, formatting, opening, and saving text files.

public class TextEditorFrame extends JFrame implements ActionListener {

    // --- GUI Components ---
    JTextArea textArea;         // The main typing area
    JScrollPane scrollPane;     // Adds scrollbars to the text area
    JLabel fontLabel;           // Simple text label
    JSpinner fontSizeSpinner;   // The up/down number box for font size
    JButton fontColorButton;    // Button to open the color picker
    JComboBox fontBox;          // Dropdown menu for font styles
    
    // --- Top Menu Components ---
    JMenuBar menuBar;
    JMenu fileMenu;
    JMenuItem openItem;
    JMenuItem saveItem;
    JMenuItem exitItem;

    TextEditorFrame() {
        // 1. Basic Window Setup
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setTitle("Text Editor");
        this.setLayout(new FlowLayout()); // FlowLayout places components in a row, wrapping to the next line if they don't fit
        this.setSize(500, 500);
        this.setLocationRelativeTo(null);

        // 2. Setup the Text Area
        textArea = new JTextArea();
        textArea.setLineWrap(true);       // Forces text to the next line when hitting the edge
        textArea.setWrapStyleWord(true);  // Ensures it wraps whole words, not cutting them in half
        textArea.setFont(new Font("Academy Engraved LET", Font.PLAIN, 20));

        // 3. Setup the Scroll Pane
        // We put the textArea INSIDE the scrollPane so it can actually scroll
        scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(502, 450));
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS); // Always show the vertical scrollbar

        // 4. Setup Font Controls
        fontLabel = new JLabel("Font:");

        fontSizeSpinner = new JSpinner();
        fontSizeSpinner.setPreferredSize(new Dimension(50, 25));
        fontSizeSpinner.setValue(20); // Default font size
        
        // Spinners use ChangeListeners instead of ActionListeners
        fontSizeSpinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                // When the spinner changes, grab the new value and apply it to the text area's font
                textArea.setFont(new Font(textArea.getFont().getFamily(), Font.PLAIN, (int) fontSizeSpinner.getValue()));
            }
        });

        fontColorButton = new JButton("Color");
        fontColorButton.addActionListener(this);

        // This awesome line talks to the Operating System to get an array of EVERY font installed on the computer!
        String[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();

        fontBox = new JComboBox(fonts); // Populate the dropdown with the OS fonts
        fontBox.addActionListener(this);
        fontBox.setSelectedItem("Academy Engraved LET"); // Set default selection

        // 5. Setup the File Menu Bar
        menuBar = new JMenuBar();
        fileMenu = new JMenu("File");
        openItem = new JMenuItem("Open");
        saveItem = new JMenuItem("Save");
        exitItem = new JMenuItem("Exit");

        openItem.addActionListener(this);
        saveItem.addActionListener(this);
        exitItem.addActionListener(this);

        fileMenu.add(openItem);
        fileMenu.add(saveItem);
        fileMenu.add(exitItem);
        menuBar.add(fileMenu);

        // 6. Add everything to the frame
        this.setJMenuBar(menuBar); // Menus get added via a special method, not standard .add()
        this.add(fontLabel);
        this.add(fontSizeSpinner);
        this.add(fontColorButton);
        this.add(fontBox);
        this.add(scrollPane);
        
        this.setVisible(true);
    }

    // --- Event Handling ---
    @Override
    public void actionPerformed(ActionEvent e) {
        
        // --- Change Font Color ---
        if (e.getSource() == fontColorButton) {
            JColorChooser colorChooser = new JColorChooser();
            // Opens a pop-up window for the user to pick a color. Defaults to black.
            Color color = colorChooser.showDialog(null, "Choose a Color", Color.BLACK);
            textArea.setForeground(color);
        }
        
        // --- Change Font Style ---
        if (e.getSource() == fontBox) {
            // Get the selected font name from the dropdown and apply it, keeping the current size
            textArea.setFont(new Font((String) fontBox.getSelectedItem(), Font.PLAIN, textArea.getFont().getSize()));
        }
        
        // --- Open a File ---
        if (e.getSource() == openItem) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(new File(".")); // "." means start in the current project folder
            
            // Forces the file chooser to only look for .txt files
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Text Files", "txt");
            fileChooser.setFileFilter(filter);

            // showOpenDialog returns an integer representing what the user clicked (Save, Cancel, etc.)
            int response = fileChooser.showOpenDialog(null);

            // If the user actually clicked "Open"
            if (response == JFileChooser.APPROVE_OPTION) {
                File file = new File(fileChooser.getSelectedFile().getAbsolutePath());
                Scanner fileIn = null;

                try {
                    fileIn = new Scanner(file);
                    
                    if (file.isFile()) {
                        // Read the file line by line and slap it into the text area
                        while (fileIn.hasNextLine()) {
                            String line = fileIn.nextLine() + "\n";
                            textArea.append(line);
                        }
                    }
                } catch (FileNotFoundException e1) {
                    e1.printStackTrace();
                } finally {
                    // Always close the scanner to prevent memory leaks!
                    fileIn.close();
                }
            }
        }
        
        // --- Save a File ---
        if (e.getSource() == saveItem) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(new File("."));

            int response = fileChooser.showSaveDialog(null);

            if (response == JFileChooser.APPROVE_OPTION) {
                File file;
                PrintWriter fileOut = null;

                file = new File(fileChooser.getSelectedFile().getAbsolutePath());
                
                try {
                    fileOut = new PrintWriter(file);
                    // Grab everything from the text area and write it to the file
                    fileOut.println(textArea.getText());
                } catch (FileNotFoundException e1) {
                    e1.printStackTrace();
                } finally {
                    // Always close the writer to ensure the file saves properly and isn't locked
                    fileOut.close();
                }
            }
        }
        
        // --- Exit Program ---
        if (e.getSource() == exitItem) {
            System.exit(0);
        }
    }
}