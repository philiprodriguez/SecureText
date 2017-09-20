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
import securetext.backend.SecureTextManager;

/**
 *
 * Philip Rodriguez
 */
public class Gui extends JFrame implements WindowListener {
    private SecureTextManager manager;
    private final JScrollPane scrollPane;
    private final JTextArea textArea;
    
    private long historySize = 0;
    private LinkedList<String> history = new LinkedList<String>();
    
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
                else if ((ke.getModifiers() & KeyEvent.CTRL_MASK) == 0)
                {
                    self.setTitle("SecureText - " + manager.getFile().getName() + "*");
                    updateHistory();
                }
            }

            @Override
            public void keyReleased(KeyEvent ke) {
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
        
        JPanel panel = new JPanel();
        JLabel label = new JLabel("Enter the password to use with the file:");
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
            
            try
            {
                this.manager = new SecureTextManager(new File(filename), new String(password));
            }
            catch (Exception exc)
            {
                JOptionPane.showMessageDialog(null, "Error:\n\n" + exc);
                System.exit(0);
            }
        }
        else
        {
            
            //Cancel
            System.exit(0);
        }
    }
    
    private static final long MAX_HISTORY_CHARS = 1500000000; // about 1.5GB
    private void updateHistory()
    {
        historySize += textArea.getText().length();
        history.addLast(textArea.getText());
        while(historySize > MAX_HISTORY_CHARS)
        {
            historySize -=  history.removeFirst().length();
        }
    }
    
    private void undo()
    {
        this.textArea.setText(history.getLast());
        if (history.size() > 1)
        {
            historySize -= history.removeLast().length();
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
            this.textArea.setText(this.manager.loadContent());
            history.clear();
            historySize = 0;
            updateHistory();
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