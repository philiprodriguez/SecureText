package securetext.frontend;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import securetext.backend.SecureTextManager;

/**
 *
 * Philip Rodriguez
 */
public class Gui extends JFrame implements WindowListener {
    private SecureTextManager manager;
    private final JScrollPane scrollPane;
    private final JTextArea textArea;
    
    
    private static final long MAX_HISTORY_CHARS = 1500000000; // about 3GB
    private static final boolean printHistoryInfo = true;
    private static final boolean printAllHistoryInfo = false;
    
    private long historySize = 0;
    private LinkedList<String> history = new LinkedList<String>();
    private LinkedList<Integer> historyPos = new LinkedList<Integer>();
    
    
    public Gui()
    {
        initializeManager();
        
        this.setTitle("SecureText - " + manager.getFile().getName());
        
        this.setPreferredSize(new Dimension(640, 480));
        this.setSize(new Dimension(640, 480));
        this.addWindowListener(this);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        
        this.textArea = new JTextArea();
        this.textArea.setEditable(true);
        this.textArea.setEnabled(true);
        this.textArea.setFont(new Font("Serif", Font.PLAIN, 16));
        this.textArea.setLineWrap(true);
        this.textArea.setWrapStyleWord(true);
        
        JFrame self = this;
        this.textArea.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent ke) {
            }

            @Override
            public void keyPressed(KeyEvent ke) {
                if ((ke.getKeyCode() == KeyEvent.VK_S) && ((ke.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
                    save();
                }
                else if ((ke.getKeyCode() == KeyEvent.VK_Z) && ((ke.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
                    undo();
                }
            }

            @Override
            public void keyReleased(KeyEvent ke) {
            }
        });
        this.textArea.getDocument().addDocumentListener(new DocumentListener(){
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateHistory();
                last = textArea.getText();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateHistory();
                last = textArea.getText();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
            }
        });
        this.scrollPane = new JScrollPane(this.textArea);
        
        this.getContentPane().add(scrollPane, BorderLayout.CENTER);
        
        this.setVisible(true);
        
        load();
    }
    
    //Does what it says... closes upon any failure...
    private void initializeManager()
    {
        String filename = JOptionPane.showInputDialog("Enter the file name (with extention):");
        if (filename == null || filename.length() <= 0)
        {
            JOptionPane.showMessageDialog(null, "Invalid input! Closing...");
            System.exit(0);
        }
        
        String password = promptPassword("Enter the password to use with the file:");
        
        //If we're making a new file, confirm the password...
        if (!new File(filename).exists())
        {
            String confirm = promptPassword("Confirm the password to create the new file with:");
            if (!password.equals(confirm))
            {
                JOptionPane.showMessageDialog(null, "Passwords didn't match! Closing...");
                System.exit(0);
            }
        }
        
        try
            {
                this.manager = new SecureTextManager(new File(filename), password);
            }
            catch (Exception exc)
            {
                JOptionPane.showMessageDialog(null, "Error:\n\n" + exc);
                System.exit(0);
            }
    }
    
    private String promptPassword(String message)
    {
        JPanel panel = new JPanel();
        JLabel label = new JLabel(message);
        JPasswordField pass = new JPasswordField();
        pass.setPreferredSize(new Dimension(240, 20));
        panel.add(label);
        panel.add(pass);
        panel.setPreferredSize(new Dimension(320, 60));
        panel.setSize(new Dimension(320, 60));
        
        pass.addAncestorListener(new AncestorListener() {
            @Override
            public void ancestorAdded(AncestorEvent ae) {
                pass.requestFocusInWindow();
            }

            @Override
            public void ancestorRemoved(AncestorEvent ae) {
            }

            @Override
            public void ancestorMoved(AncestorEvent ae) {
            }
        });
        
        int option = JOptionPane.showOptionDialog(null, panel, "Input",
                                 JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE,
                                 null, null, null);
        
        if(option == 0) // pressing OK button
        {
            char[] password = pass.getPassword();
            if (password.length <= 0)
            {
                JOptionPane.showMessageDialog(null, "Invalid input! Closing...");
                System.exit(0);
            }
            return new String(password);
            
        }
        else
        {
            
            //Cancel
            System.exit(0);
            return null;
        }
    }
    
    String last = null;
    private void updateHistory()
    {
        if (undoing)
            return;
        
        setTitle("SecureText - " + manager.getFile().getName() + "*");
        historySize += last.length();
        history.addLast(last);
        historyPos.addLast(textArea.getCaretPosition());
        while(historySize > MAX_HISTORY_CHARS)
        {
            historySize -=  history.removeFirst().length();
            historyPos.removeFirst();
        }
        
        if (printHistoryInfo)
        {
            if (printAllHistoryInfo)
            {
                System.out.println(history);
                System.out.println(historyPos);
            }
            System.out.println("(" + history.size() + " history entries of " + historySize + " characters)");
        }
    }
    
    boolean undoing = false;
    private void undo()
    {
        undoing = true;
        
        this.textArea.setText(history.getLast());
        this.textArea.setCaretPosition(historyPos.getLast());
        
        if (history.size() > 1)
        {
            historySize -= history.removeLast().length();
            historyPos.removeLast();
        }
        
        undoing = false;
        
        if (printHistoryInfo)
        {
            if (printAllHistoryInfo)
            {
                System.out.println(history);
                System.out.println(historyPos);
            }
            System.out.println("(" + history.size() + " history entries of " + historySize + " characters)");
        }
    }
    
    private void save()
    {
        try {
            System.out.println("saving...");
            this.manager.saveContent(this.textArea.getText());
            this.setTitle("SecureText - " + manager.getFile().getName());
        } catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(null, "Error saving content:\n\n" + ex);
        }
    }
    
    private void load()
    {
        try {
            System.out.println("loading...");
            history.clear();
            historyPos.clear();
            historySize = 0;
            last = this.manager.loadContent();
            this.textArea.setText(last);
            setTitle("SecureText - " + manager.getFile().getName());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error loading content:\n\n" + ex);
        }
    }

    @Override
    public void windowOpened(WindowEvent we) {
    }

    @Override
    public void windowClosing(WindowEvent we) {
        save();
    }

    @Override
    public void windowClosed(WindowEvent we) {
    }

    @Override
    public void windowIconified(WindowEvent we) {
    }

    @Override
    public void windowDeiconified(WindowEvent we) {
    }

    @Override
    public void windowActivated(WindowEvent we) {
    }

    @Override
    public void windowDeactivated(WindowEvent we) {
    }
}
