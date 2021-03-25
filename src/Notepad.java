import javax.swing.*;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.UndoManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

public class Notepad extends JFrame {

    private JPanel panel1;
    private JButton buttonSave;
    private JButton buttonLoad;
    private JTextArea textArea;

    UndoManager undoManager;

    public Notepad(String title) {
        super(title);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(panel1);
        pack(); // Adjusts the Frame size to the main panel content
        setJMenuBar(createMenuBar());
        myInit();
    }

    private void myInit() {
        undoManager = new UndoManager();
        textArea.getDocument().addUndoableEditListener(new UndoableEditListener() {
            @Override
            public void undoableEditHappened(UndoableEditEvent e) {
                undoManager.addEdit(e.getEdit());
            }
        });
        buttonSave.setIcon(new ImageIcon(getClass().getResource("/resources/Actions-document-save-icon.png")));
        buttonLoad.setIcon(new ImageIcon(getClass().getResource("/resources/Actions-document-open-icon.png")));
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(createFileMenu());
        menuBar.add(createEditMenu());
        menuBar.add(new JPopupMenu.Separator());
        menuBar.add(creatwHelpMenu());
        return menuBar;
    }
    private JMenu createEditMenu() {
        JMenu editMenu = new JMenu("Edit");
        JMenuItem undoItem = new JMenuItem("Undo");
        editMenu.add(undoItem);
        JMenuItem redoItem = new JMenuItem("Redo");
        editMenu.add(redoItem);
        JMenuItem copyItem = new JMenuItem("Copy");
        editMenu.add(copyItem);
        JMenuItem cutItem = new JMenuItem("Cut");
        editMenu.add(cutItem);
        JMenuItem pasteItem = new JMenuItem("Paste");
        editMenu.add(pasteItem);

        undoItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (undoManager.canUndo()) {
                    undoManager.undo();
                }
            }
        });
        redoItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (undoManager.canRedo()) {
                    undoManager.redo();
                }
            }
        });
        copyItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textArea.copy();
            }
        });
        cutItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textArea.cut();
            }
        });
        pasteItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textArea.paste();
            }
        });
        return editMenu;
    }
    private JMenu createFileMenu() {
        JMenu fileMenu = new JMenu("File");
        JMenuItem newItem = new JMenuItem("New");
        newItem.setIcon(new ImageIcon(getClass().getResource("/com/sun/java/swing/plaf/windows/icons/File.gif")));
        fileMenu.add(newItem);
        JMenuItem openItem = new JMenuItem("Open ...");
        openItem.setIcon(new ImageIcon(getClass().getResource("/com/sun/java/swing/plaf/windows/icons/Directory.gif")));
        fileMenu.add(openItem);
        JMenuItem saveItem = new JMenuItem("Save ");
        saveItem.setIcon(new ImageIcon(getClass().getResource("/com/sun/java/swing/plaf/windows/icons/FloppyDrive.gif")));
        fileMenu.add(saveItem);
        fileMenu.add(new JSeparator());
        JMenuItem exitItem = new JMenuItem("Exit");
        fileMenu.add(exitItem);

        newItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                newFile();
            }
        });
        openItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                load();
            }
        });
        saveItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                save();
            }
        });
        exitItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        return fileMenu;
    }

    private JMenu creatwHelpMenu() {
        JMenu helpMenu = new JMenu("Help");
        JMenuItem aboutItem = new JMenuItem("About ...");
        helpMenu.add(aboutItem);
        aboutItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AboutDialog dialog = new AboutDialog();
                dialog.pack();
                dialog.setVisible(true);
            }
        });
        return helpMenu;
    }

    private void newFile() {
        int n = JOptionPane.showConfirmDialog(
                this,
                "Do you want to delete the current text?",
                "Remove test?",
                JOptionPane.YES_NO_OPTION);
        if (n == 0) {
            textArea.setText("");
        }
    }

    private void save() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.showSaveDialog(this);
        File file = fileChooser.getSelectedFile();

        if (file != null) {
            String fileName = file.getAbsolutePath();

            if (file.exists()) {
                int response = JOptionPane.showConfirmDialog(null, //
                        "Do you want to replace the existing file?", //
                        "Confirm", JOptionPane.YES_NO_OPTION, //
                        JOptionPane.QUESTION_MESSAGE);
                if (response != JOptionPane.YES_OPTION) {
                    return;
                }
            }

            try {
                saveFile(fileName);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, "Error writing file "
                        + fileName, "ERROR", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    private void saveFile(String fileName) throws IOException {
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(fileName);
            writer.print(textArea.getText());
        } finally {
            if (writer != null) {
                writer.close();
            }

        }
    }

    private void load() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.showOpenDialog(this);
        File file = fileChooser.getSelectedFile();
        String s = null;
        if (file != null) {
            String fileName = file.getAbsolutePath();
            System.out.println(fileName);
            try {
                s = loadFile(fileName);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, "Error reading file " + fileName, "ERROR", JOptionPane.WARNING_MESSAGE);
            }
            textArea.setText(s);
        }

    }

    private String loadFile(String fileName) throws IOException {
        StringBuilder str = new StringBuilder();
        BufferedReader reader = null;
        try {
            File file = new File(fileName);
            FileReader fileReader = new FileReader(file);
            reader = new BufferedReader(fileReader);

            String s;
            while ((s = reader.readLine()) != null) {
                str.append(s + "\n");
            }
        } finally {
            reader.close();
        }
        return str.toString();
    }

    public static void main (String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Notepad notepad = new Notepad("My notepad");
                notepad.setVisible(true);
            }
        });
    }
}
