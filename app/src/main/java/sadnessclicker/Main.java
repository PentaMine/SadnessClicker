/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package sadnessclicker;

import java.awt.BorderLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.event.ComponentListener;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class Main implements WindowListener, MouseListener, ComponentListener {

    public static final String VERSION = "1.3.1";
    public static final String[] CHANGELOG = {
        "Fixed save button cost comparison error",
        "timeElapsed is now volatile"
    };


    private File file;

    private String workingDirectory;

    private volatile long fileClicks;

    private volatile long clicks;
    private volatile long timeElapsed;

    private volatile long fails;


    private JFrame frame = new JFrame("Sadness Clicker");
    private JLabel dud =  new JLabel();
    
    public static void main(String[] args) {
        new Main().start();
    }

    public void start() {
        //here, we assign the name of the OS, according to Java, to a variable...
        String OS = (System.getProperty("os.name")).toUpperCase();
        if (OS.contains("WIN")) {
            workingDirectory = System.getenv("AppData");
        }
        else {
            //in either case, we would start in the user's home directory
            workingDirectory = System.getProperty("user.home");
            //if we are on a Mac, we are not done, we look for "Application Support"
            workingDirectory += "/Library/Application Support";
        }
        //we are now free to set the workingDirectory to the subdirectory that is our 
        //folder.
        
        file = new File(workingDirectory + "/SadnessClicker");
        read();
        make();
    }

    private void read() {
        try {
            if (!file.exists()) {
                file.createNewFile();
                clicks = 0;
                timeElapsed = 0;
                fails = 0;
            } else {
                FileReader fr = new FileReader(file);
                char[] rawdata = new char[40];
                fr.read(rawdata);
                fr.close();
                String[] data = new String(rawdata).split(",");
                try {
                    clicks = Long.parseLong(data[0]);
                } catch (IndexOutOfBoundsException e) {
                    clicks = 0;
                } catch (NumberFormatException e) {
                    clicks = 0;
                }
                fileClicks = clicks;
                try {
                    timeElapsed = Long.parseLong(data[1]);
                } catch (IndexOutOfBoundsException e) {
                    timeElapsed = 0;
                } catch (NumberFormatException e) {
                    timeElapsed = 0;
                }
                try {
                    fails = Long.parseLong(data[1]);
                } catch (IndexOutOfBoundsException e) {
                    fails = 0;
                } catch (NumberFormatException e) {
                    fails = 0;
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Cannot Access Save Data", "sadness", JOptionPane.ERROR_MESSAGE);
            System.exit(-1);
        }
    }

    private void save() {
        save(true);
    }

    private void save(boolean includeClicks) {
        try (FileWriter fw = new FileWriter(file)) {
            if (includeClicks) {
                fw.write(clicks + "," + timeElapsed + "," + fails + ",");
                fw.close();
                fileClicks = clicks;
            } else {
                fw.write(fileClicks + "," + timeElapsed + "," + fails + ",");
                fw.close();
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Cannot Access Save Data", "sadness", JOptionPane.ERROR_MESSAGE);
            System.exit(-1);
        }
    }

    private JLabel clicksLabel;
    private JLabel timeLabel;
    private JLabel failsLabel;

    private JButton clickButton = new JButton("Click");
    private JButton wrongButton = new JButton();
    private JLabel wrongLabel = new JLabel("Hello :D");
    private JButton saveButton = new JButton("Save your precious progress");

    private JButton aboutButton = new JButton("about");

    private void make() {
        clicksLabel = new JLabel("Number of clicks: " + clicks);
        timeLabel = new JLabel("Hold on...");
        failsLabel = new JLabel("Number of slip-ups: " + fails);

        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(this);
        frame.setResizable(false);
        frame.addComponentListener(this);

        clicksLabel.setBounds(30, 100, 1000, 30);
        timeLabel.setBounds(30, 130, 1000, 30);
        failsLabel.setBounds(30, 160, 1000, 30);

        clickButton.addMouseListener(this);
        wrongButton.addMouseListener(this);
        aboutButton.addMouseListener(this);
        saveButton.addMouseListener(this);

        wrongButton.setOpaque(false);
        wrongButton.setContentAreaFilled(false);
        wrongButton.setBorderPainted(false);
        wrongButton.setFocusPainted(false);

        clickButton.setBounds(225, 225, 50, 50);
        wrongButton.setBounds(-10, -10, 520, 520);
        wrongLabel.setBounds(50, 275, 1000, 100);

        saveButton.setBounds(280, 30, 220, 30);
        
        aboutButton.setBounds(380, 400, 100, 30);

        aboutLabel.setBounds(30, 30, 400, 200);

        changelogButton.addMouseListener(this);
        backButton.addMouseListener(this);

        changelogButton.setBounds(20, 230, 100, 30);
        versionLabel.setBounds(120, 230, 100, 30);

        String temp = "<html><h1>Changelog</h1>" + Main.VERSION;
        for (int i = 0; i < Main.CHANGELOG.length; i++) {
            temp += "<br />•" + Main.CHANGELOG[i];
        }
        temp += "</html>";

        changelogLabel.setText(temp);
        changelogLabel.setVerticalAlignment(SwingConstants.TOP);
        changelogLabel.setBounds(0, 0, 400, Main.CHANGELOG.length * 40 + 50);


        Runnable timerClock = new Runnable() {
            public void run() {
                timeElapsed += 1;
                timeLabel.setText(
                    "Time you've wasted: " + 
                    String.valueOf(TimeUnit.SECONDS.toDays(timeElapsed)) + ":" +
                    String.valueOf(TimeUnit.SECONDS.toHours(timeElapsed) - ((TimeUnit.SECONDS.toDays(timeElapsed) *24))) + ":" +
                    String.valueOf(TimeUnit.SECONDS.toMinutes(timeElapsed) - (TimeUnit.SECONDS.toHours(timeElapsed)* 60)) + ":" +
                    String.valueOf(TimeUnit.SECONDS.toSeconds(timeElapsed) - (TimeUnit.SECONDS.toMinutes(timeElapsed) *60))
                );
                save(false);
            }
        };

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(timerClock, 0, 1, TimeUnit.SECONDS);

        showMain();
    }

    private void showMain() {
        frame.getContentPane().removeAll();
        frame.setSize(500, 500);

        frame.getContentPane().add(aboutButton);

        frame.getContentPane().add(saveButton);
        
        frame.getContentPane().add(clicksLabel);
        frame.getContentPane().add(timeLabel);
        frame.getContentPane().add(failsLabel);

        frame.getContentPane().add(wrongLabel);
        frame.getContentPane().add(clickButton);
        frame.getContentPane().add(wrongButton);
        frame.getContentPane().add(dud);

        frame.setVisible(true);
    }

    private JLabel aboutLabel = new JLabel("<html><h1>About</h1><br />Hi! i'm Canary<br />This is a pointless little app that I made to cause suffering :D<br />If you're looking for a useful thing, this ain't it mate. In fact, most of my things are pretty useless...<br />However, this is the first thing i made with save data functionality! <b>YAY!<br /></html>");

    private JButton changelogButton = new JButton("Changelog");
    private JButton backButton = new JButton("Back");
    private JLabel versionLabel = new JLabel(Main.VERSION);

    private void showAboutMenu() {
        frame.getContentPane().removeAll();
        frame.getContentPane().add(aboutLabel);

        backButton.setBounds(300, 230, 100, 30);


        frame.getContentPane().add(changelogButton);
        frame.getContentPane().add(versionLabel);
        frame.getContentPane().add(backButton);

        frame.getContentPane().add(dud);
        frame.setSize(500, 300);
        frame.repaint();
    }

    private JLabel changelogLabel = new JLabel();

    private void showChangelog() {
        frame.getContentPane().removeAll();

        frame.getContentPane().add(backButton, BorderLayout.PAGE_END);

        frame.getContentPane().add(changelogLabel);
        frame.getContentPane().add(dud);

        frame.setResizable(true);
        frame.setSize(420, Main.CHANGELOG.length * 30 + 160);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (!SwingUtilities.isLeftMouseButton(e) && (e.getSource() == clickButton || e.getSource() == aboutButton)) {
            clicks = 0;
            fails++;
            failsLabel.setText("Number of slip-ups: " + fails);
            clicksLabel.setText("Number of clicks: 0 :O");
            wrongLabel.setText("<html><h2>Welp. your clicks are now reset</h2><br />at least you learned something<br />next time remember not to right click ;D</html>");
            save();
            return;
        }
        if (e.getSource() == clickButton) {
            clicks++;        
            clicksLabel.setText("Number of clicks: " + clicks);
            wrongLabel.setText("good job");

            switch ((int)(Math.random() * 10)) {
                case 0 -> 
                    clickButton.setLocation(clickButton.getX() + (int)(Math.random() * 15), clickButton.getY());
                case 1 -> 
                    clickButton.setLocation(clickButton.getX() - (int)(Math.random() * 15), clickButton.getY());
                case 2 -> 
                    clickButton.setLocation(clickButton.getX(), clickButton.getY() + (int)(Math.random() * 15));
                case 3 -> 
                    clickButton.setLocation(clickButton.getX(), clickButton.getY() - (int)(Math.random() * 15));
            }
            if (clickButton.getX() + 50 < 0 || clickButton.getX() > 500 || clickButton.getY() + 50 < 0 || clickButton.getY() > 500)
                clickButton.setLocation(225, 225);
        }
        if (e.getSource() == wrongButton) {
            clicks = 0;
            fails++;
            failsLabel.setText("Number of slip-ups: " + fails);
            clicksLabel.setText("Number of clicks: 0 :O");
            wrongLabel.setText("<html><h2>Welp. your clicks are now reset</h2><br />next time remember to not click here</html>");
            save();
        }
        if (e.getSource() == aboutButton) 
            showAboutMenu();
        if (e.getSource() == backButton) {
            frame.setResizable(false);
            showMain();
        }
        if (e.getSource() == changelogButton) {
            frame.setResizable(true);
            showChangelog();
        }
        if (e.getSource() == saveButton)  {
            if (Math.random() > 0.3)
                saveButton.setBounds(280, 30, 220, 30);
            else
                saveButton.setBounds(20, 30, 220, 30);
            if (Math.random() > 0.001) {
                wrongLabel.setText("<html><h1>Saving...</h1> or not...<br />it cost 1 click point!</html>");
                clicks = (clicks >= 1)? clicks - 1 : clicks;
            } else {
                wrongLabel.setText("<html><h1>Saving...</h1> or not...<br />it cost <b>100</b> click points!<br />Lucky!!</html>");
                clicks = (clicks >= 100)? clicks - 100 : 0;
            }
            clicksLabel.setText("Number of clicks: " + clicks);
            if (Math.random() > 0.3)
                save();
        }
    }

    @Override
    public void windowClosing(WindowEvent e) {
        frame.dispose();
        save(false);
        JOptionPane.showMessageDialog(null, "Until next time ;D");
        System.exit(0);
    }

    @Override
    public void windowOpened(WindowEvent e) {        
    }

    @Override
    public void windowClosed(WindowEvent e) {        
    }

    @Override
    public void windowIconified(WindowEvent e) {        
    }

    @Override
    public void windowDeiconified(WindowEvent e) {        
    }

    @Override
    public void windowActivated(WindowEvent e) {        
    }

    @Override
    public void windowDeactivated(WindowEvent e) {        
    }

    @Override
    public void mouseClicked(MouseEvent e) {        
    }

    @Override
    public void mouseReleased(MouseEvent e) {        
    }

    @Override
    public void mouseEntered(MouseEvent e) {        
    }

    @Override
    public void mouseExited(MouseEvent e) {        
    }

    @Override
    public void componentResized(ComponentEvent e) {
        changelogLabel.setBounds(0, 0, e.getComponent().getWidth() - 20, e.getComponent().getHeight() - 20); 
    }

    @Override
    public void componentMoved(ComponentEvent e) {
    }

    @Override
    public void componentShown(ComponentEvent e) {
    }

    @Override
    public void componentHidden(ComponentEvent e) {
    }
}
