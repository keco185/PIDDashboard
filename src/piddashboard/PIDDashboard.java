package piddashboard;

import edu.wpi.first.wpilibj.networktables.NetworkTable;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
class LoadingWindow extends JComponent {
    private static final long serialVersionUID = 1L;
    public LoadingWindow() {
        
    }
    @Override
    public void paint (Graphics g3) {
        Graphics2D g4 = (Graphics2D) g3;
        g4.setColor(Color.getHSBColor((float)3.51, (float) 0.61, 1));
        g4.fillRect(0, 0, 350, 200);
        g4.scale(3,3);
        g4.setColor(Color.WHITE);
        g4.drawString("Connecting...", 20, 30);
    }
}
class DSWindow extends JComponent {
    private static final long serialVersionUID = 1L;
    public static int windowWidth = 1366;
    public static int windowHeight = 725;
    ArrayList<Double> current = new ArrayList<>();
    double p = 0.0;
    double i = 0.0;
    double d = 0.0;
    double f = 0.0;
    double setpoint = 0.0;
    double maxCurrent = 0.0;
    boolean log = false;
    boolean requireRobot = false;
    boolean hasRobot = false;
    public DSWindow(double p, double i, double d, double f, double setpoint, ArrayList<Double> current, double maxCurrent, boolean log, boolean requireRobot, boolean hasRobot) {
        this.current = current;
        this.p = p;
        this.i = i;
        this.d = d;
        this.setpoint = setpoint;
        this.maxCurrent = maxCurrent;
        this.f = f;
        this.log = log;
        this.requireRobot = requireRobot;
        this.hasRobot = hasRobot;
    }
    @Override
    public void paint (Graphics g) {
        InputMap im = getInputMap(WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = getActionMap();
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0), "space");
        am.put("space", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                PIDDashboard.pause = !PIDDashboard.pause;
            }
        });
        double scaleY;
        double scaleX;
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(Color.getHSBColor((float)3.51, (float) 0.61, 1));
        g2.fillRect(0,0,windowWidth,windowHeight);
        g2.setColor(Color.WHITE);
        g2.fillRoundRect(60, 30, 900, 500, 20, 20);
        g2.setColor(Color.BLACK);
        g2.drawRoundRect(60, 30, 900, 500, 20, 20);
        g2.setColor(Color.WHITE);
        g2.fillRoundRect(990, 30, 346, 500, 20, 20);
        g2.setColor(Color.BLACK);
        g2.drawRoundRect(990, 30, 346, 500, 20, 20);
        g2.scale(2, 2);
        g2.setColor(Color.red);
        g2.drawString("Error", 290, 280);
        g2.setColor(Color.green);
        g2.drawString("Setpoint", 335, 280);
        g2.setColor(Color.blue);
        g2.drawString("Current Value", 400, 280);
        g2.scale(0.5, 0.5);
        if (log) {
        g2.setColor(Color.RED);
        g2.fillOval(60, 560, 30, 30);
        g2.scale(2,2);
        g2.drawString("RECORDING", 60, 290);
        } else {
            g2.setColor(Color.LIGHT_GRAY);
            g2.fillOval(60, 560, 30, 30);
            g2.scale(2,2);
            g2.drawString("NOT RECORDING", 60, 290);
        }
        g2.scale(0.5, 0.5);
        if (requireRobot) {
            if (hasRobot) {
                g2.setColor(Color.GREEN);
            } else {
                g2.setColor(Color.RED);
            }
        } else {
            g2.setColor(Color.LIGHT_GRAY);
        }
        g2.fillOval(60, 620, 30, 30);
        g2.scale(2, 2);
        g2.setColor(Color.DARK_GRAY);
        g2.drawString("Robot Status", 60, 320);
        g2.scale(0.5, 0.5);
        g2.setColor(Color.BLACK);
        if (setpoint * 1.5 > maxCurrent) {
            scaleY = 0.0000001;
            while (scaleY * 2 < setpoint) {
                scaleY = scaleY * 10;
            }
            scaleY = scaleY / 10;
            int j = 0;
            while (((scaleY * j) / (setpoint * 1.5) < 0.95)) {
                g2.drawString("" + Math.round(scaleY * j * 100000000) / 100000000.0, 5, (int) (530 - (scaleY * j / (setpoint * 1.5))*500));
                g2.drawRect(50, (int) (530 - (scaleY * j / (setpoint * 1.5))*500), 20, 2);
                j++;
            }
            g2.setColor(Color.GREEN);
            g2.drawLine(60, 530 - (int) (1/1.5*500), 960, 530 - (int) (1/1.5*500)); //coord Y = 530 - value / setpoint / 1.5 *500
            j = 0;
            g2.setColor(Color.BLUE);
            int currentSize = current.size();
            while (j + 1 < currentSize) {
                g2.drawLine((int) (1.0 * j /currentSize * 900 + 60), 530 - (int) (current.get(j)/setpoint/1.5*500), (int)((j+1.0)/currentSize * 900 + 60), 530 - (int) (current.get(j+1)/setpoint/1.5*500));
                j++;
            }
            j = 0;
            g2.setColor(Color.RED);
            while (j + 1 < currentSize) {
                g2.drawLine((int) (1.0 * j /currentSize * 900 + 60), 530 - (int) (Math.abs(current.get(j) - setpoint)/setpoint/1.5*500), (int)((j+1.0)/currentSize * 900 + 60), 530 - (int) (Math.abs(current.get(j+1) - setpoint)/setpoint/1.5*500));
                j++;
            }
        } else {
            scaleY = 0.0000001;
            while (scaleY * 2 < maxCurrent) {
                scaleY = scaleY * 10;
            }
            scaleY = scaleY / 10;
            int j = 0;
            while (((scaleY * j) / maxCurrent < 0.95)) {
                g2.drawString("" + Math.round(scaleY * j * 100000000) / 100000000.0, 5, (int) (530 - (scaleY * j / maxCurrent)*500));
                g2.drawRect(50, (int) (530 - (scaleY * j / maxCurrent)*500), 20, 2);
                j++;
            }
            g2.setColor(Color.GREEN);
            g2.drawLine(60, 530 - (int) (setpoint/maxCurrent*500), 960, 530 - (int) (setpoint/maxCurrent*500));
            j = 0;
            g2.setColor(Color.BLUE);
            int currentSize = current.size();
            while (j + 1 < currentSize) {
                g2.drawLine((int) (1.0 * j /currentSize * 900 + 60), 530 - (int) (current.get(j)/maxCurrent*500), (int)((j+1.0)/currentSize * 900 + 60), 530 - (int) (current.get(j+1)/maxCurrent*500));
                j++;
            }
            j = 0;
            g2.setColor(Color.RED);
            while (j + 1 < currentSize) {
                g2.drawLine((int) (1.0 * j /currentSize * 900 + 60), 530 - (int) (Math.abs(current.get(j) - setpoint)/maxCurrent*500), (int)((j+1.0)/currentSize * 900 + 60), 530 - (int) (Math.abs(current.get(j+1) - setpoint)/maxCurrent*500));
                j++;
            }
        }
        scaleX = 0.01;
        while (scaleX * 2 < (current.size() / 50.0)) {
            scaleX = scaleX * 10;
        }
        scaleX = scaleX / 10;
        int j = 1;
        while (((scaleX * j) / (current.size() / 50.0) < 0.95)) {
            g2.setColor(Color.BLACK);
            g2.drawString("" + Math.round(scaleX * j * 10.0) / 10.0, (int) (scaleX * j / (current.size() / 50.0) * 900) + 65, 520);
            g2.setColor(Color.LIGHT_GRAY);
            g2.drawRect((int) (scaleX * j / (current.size() / 50.0) * 900) + 60, 30, 1, 500);
            j++;
        }
        g2.scale(2.8, 2.8);
        g2.setColor(Color.DARK_GRAY);
        g2.drawString("Proportional: ", 357, 25);
        g2.drawString(p + "", 357, 37);
        g2.drawString("Integral: ", 357, 50);
        g2.drawString(i + "", 357, 62);
        g2.drawString("Derivitive: ", 357, 75);
        g2.drawString(d + "", 357, 87);
        g2.drawString("Feed Forward", 357, 100);
        g2.drawString(f + "", 357, 112);
        g2.drawString("Setpoint: ", 357, 125);
        g2.drawString(setpoint + "", 357, 137);
        g2.drawString("Current Point: ", 357, 150);
        g2.drawString(Math.round(current.get(current.size() - 1)*100000)/100000.0 + "", 357, 162);
        g2.drawString("Time: ", 357, 175);
        g2.drawString(Math.round(current.size() / 5) / 10.0 + "s", 357, 187);
        
    }
}

public class PIDDashboard {
    static GraphicsDevice device = GraphicsEnvironment
	.getLocalGraphicsEnvironment().getScreenDevices()[0];
    ArrayList<Double> current = new ArrayList<>();
    double setP = 0.0;
    double setI = 0.0;
    double setD = 0.0;
    double setF = 0.0;
    double setSetpoint = 0.0;
    public static boolean pause = false;
    boolean log = false;
    boolean flushLog = false;
    boolean requireRobot = false;
    public static void main(String[] args) throws IOException {
        System.out.println("Launching...");
        new PIDDashboard().run();
    }
    @SuppressWarnings("SleepWhileInLoop")
    public void run() throws IOException {
        NetworkTable.setClientMode();
        NetworkTable.setIPAddress("10.4.84.2");
        System.out.println("Set IP address\nConnecting...");
        JFrame window2 = new JFrame();
        window2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window2.setTitle("PID - Connecting");
        window2.setResizable(false);
        window2.setName("PID - Connecting");
        window2.setBounds(20,20,320,190);
        window2.getContentPane().removeAll();
        window2.getContentPane().add(new LoadingWindow());
        window2.setVisible(true);
        NetworkTable dashTable = NetworkTable.getTable("SmartDashboard");
        System.out.println("Set up network tables");
        JFrame window = new JFrame();
        window.revalidate ();
	window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	window.setTitle("PID");
	window.setResizable(false);
	window.setName("PID");
	window.setBounds(0, 0, 1366, 725);
        MenuBar menuBar = new MenuBar();
        Menu menuFile = new Menu("File");
        Menu menuSet = new Menu("Set");
        menuBar.add(menuFile);
        menuBar.add(menuSet);
        MenuItem menuFileClear = new MenuItem("Clear");
        menuFile.add(menuFileClear);
        MenuItem menuFilePause = new MenuItem("Toggle Pause");
        menuFile.add(menuFilePause);
        MenuItem menuToggleLogging = new MenuItem("Toggle Logging");
        menuFile.add(menuToggleLogging);
        MenuItem menuRequireRobot = new MenuItem("Require Robot");
        menuFile.add(menuRequireRobot);
        MenuItem menuVariables = new MenuItem("Dash Vars");
        menuFile.add(menuVariables);
        MenuItem menuSetP = new MenuItem("Proportional");
        menuSet.add(menuSetP);
        MenuItem menuSetI = new MenuItem("Integral");
        menuSet.add(menuSetI);
        MenuItem menuSetD = new MenuItem("Derivitive");
        menuSet.add(menuSetD);
        MenuItem menuSetF = new MenuItem("Feed Forward");
        menuSet.add(menuSetF);
        MenuItem menuSetSetpoint = new MenuItem("Setpoint");
        menuSet.add(menuSetSetpoint);
        window.setMenuBar(menuBar);
        menuFileClear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                current.clear();
            }
        });
        menuFilePause.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                pause = !pause;
            }
        });
        menuToggleLogging.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                log = !log;
                if (!log) {
                    flushLog = true;
                }
            }
        });
        menuRequireRobot.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                requireRobot = !requireRobot;
            }
        });
        menuVariables.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                pause = true;
                JOptionPane.showMessageDialog(null,"Sensor Value: current\nSetpoint: setpoint\nProportional: kP\nIntegral: kI\nDerivitive: kD\nFeedForward: kF\nUsingPID: ReadPID (boolean)", "Smart Dash Var Names", JOptionPane.INFORMATION_MESSAGE);
                pause = false;
            }
        });
        menuSetP.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                pause = true;
                try {
                    setP = Double.valueOf(JOptionPane.showInputDialog("Set Proportional Value. Currently: " + setP));
                } catch (HeadlessException | NumberFormatException e) {
                }
                pause = false;
            }
        });
        menuSetI.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                pause = true;
                try {
                    setI = Double.valueOf(JOptionPane.showInputDialog("Set Integral Value. Currently: " + setI));
                } catch (HeadlessException | NumberFormatException e) {
                }
                pause = false;
            }
        });
        menuSetD.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                pause = true;
                try {
                    setD = Double.valueOf(JOptionPane.showInputDialog("Set Derivitive Value. Currently: " + setD));
                } catch (HeadlessException | NumberFormatException e) {
                }
                pause = false;
            }
        });
        menuSetF.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                pause = true;
                try {
                    setF = Double.valueOf(JOptionPane.showInputDialog("Set Feed Forward Value. Currently: " + setF));
                } catch (HeadlessException | NumberFormatException e) {
                }
                pause = false;
            }
        });
        menuSetSetpoint.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                pause = true;
                try {
                    setSetpoint = Double.valueOf(JOptionPane.showInputDialog("Set Setpoint Value. Currently: " + setSetpoint));
                } catch (HeadlessException | NumberFormatException e) {
                }
                pause = false;
            }
        });
        System.out.println("Set up window...");
        double kP = 0.0;
        double kI = 0.0;
        double kD = 0.0;
        double kF = 0.0;
        double setpoint = 0.0;
        double maxCurrent = 0.0;
        double workingCurrent;
        long oldTime = System.currentTimeMillis();
        Writer output;
        File logFile = new File("PIDLog.csv");
        boolean fileExists = logFile.exists();
            output = new BufferedWriter(new FileWriter("PIDLog.csv", true));
        if (!fileExists) {
            output.append("Sample,Setpoint,kP,kI,kD,kF,Value,Error");
        }
        int sleepCounter;
        window2.setVisible(false);
        window2.dispose();
        while (true) {
            sleepCounter = 0;
            while (System.currentTimeMillis() < oldTime + 20 || pause || (requireRobot && !dashTable.getBoolean("ReadPID", false))) {
                sleepCounter++;
                if (sleepCounter > 500) {
                    window.getContentPane().removeAll();
                    window.getContentPane().add(new DSWindow(kP, kI, kD, kF, setpoint, current, maxCurrent, log, requireRobot, dashTable.getBoolean("ReadPID", false)));
                    window.setVisible(true);
                    sleepCounter = 0;
                }
                try {
                    Thread.sleep(0,1);
                } catch (InterruptedException e) {
                    System.out.println(e.getMessage());
                }
            }
            oldTime = System.currentTimeMillis();
            kP = dashTable.getNumber("kP", 0.0);
            kI = dashTable.getNumber("kI", 0.0);
            kD = dashTable.getNumber("kD", 0.0);
            kF = dashTable.getNumber("kF", 0.0);
            setpoint = dashTable.getNumber("setpoint", 0.0);
            if (kP != setP) {
                dashTable.putNumber("kP", setP);
            }
            if (kI != setI) {
                dashTable.putNumber("kI", setI);
            }
            if (kD != setD) {
                dashTable.putNumber("kD", setD);
            }
            if (kF != setF) {
                dashTable.putNumber("kF", setF);
            }
            if (setpoint != setSetpoint) {
                dashTable.putNumber("setpoint", setSetpoint);
            }
            workingCurrent = dashTable.getNumber("current", 0.0);
            //workingCurrent = 20 * (Math.sin(current.size()/50.0 * Math.PI) / Math.exp(current.size()/50.0)) + 30; //For testing program without robot
            current.add(workingCurrent);
            if (workingCurrent > maxCurrent) {
                maxCurrent = workingCurrent;
            }
            window.getContentPane().removeAll();
            window.getContentPane().add(new DSWindow(kP, kI, kD, kF, setpoint, current, maxCurrent, log, requireRobot, dashTable.getBoolean("ReadPID", false)));
            window.setVisible(true);
            if (log) {
                output.append("\n"+current.size()+","+setpoint+","+kP+","+kI+","+kD+","+kF+","+workingCurrent+","+Math.abs(workingCurrent - setpoint));
            }
            if (flushLog) {
                output.flush();
                flushLog = !flushLog;
            }
        }
    }    
}
