package bank.ui;

import bank.service.BankService;
import bank.model.User;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import static bank.ui.UIHelper.*;

public class LoginFrame extends JFrame {
    private BankService bankService;
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private boolean adminLogin;

    public LoginFrame(BankService bankService, boolean adminLogin) {
        this.bankService = bankService;
        this.adminLogin = adminLogin;
        setUndecorated(true);
        setSize(420, 520);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setShape(new RoundRectangle2D.Double(0, 0, 420, 520, 20, 20));
        initUI();
    }

    private void initUI() {
        Color main = adminLogin ? new Color(139, 92, 246) : ACCENT;
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG);

        // Header
        JPanel header = new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(0, 0, main, 0, getHeight(), main.darker()));
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        header.setPreferredSize(new Dimension(420, 140));

        JLabel back = makeLabel("<  BACK", new Color(255, 255, 255, 180), 11, Font.BOLD);
        back.setBounds(15, 15, 60, 20);
        back.setCursor(new Cursor(Cursor.HAND_CURSOR));
        back.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { new WelcomeFrame(bankService).setVisible(true); dispose(); }
        });
        header.add(back);

        JPanel icon = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                drawIcon((Graphics2D)g, adminLogin ? "ADMIN" : "USER", 0, 0, 50, Color.WHITE);
            }
        };
        icon.setOpaque(false);
        icon.setBounds(185, 25, 50, 50);
        header.add(icon);

        JLabel title = makeLabel(adminLogin ? "Administration" : "Personal Banking", Color.WHITE, 19, Font.BOLD);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setBounds(50, 85, 320, 30);
        header.add(title);

        root.add(header, BorderLayout.NORTH);

        // Form
        JPanel form = new JPanel(null);
        form.setOpaque(false);

        JLabel lblU = makeLabel("USERNAME", TEXT_MID, 11, Font.BOLD);
        lblU.setBounds(40, 30, 200, 20);
        form.add(lblU);
        txtUsername = makeField("Enter your ID");
        txtUsername.setBounds(40, 55, 340, 42);
        form.add(txtUsername);

        JLabel lblP = makeLabel("PASSWORD", TEXT_MID, 11, Font.BOLD);
        lblP.setBounds(40, 115, 200, 20);
        form.add(lblP);
        txtPassword = makePasswordField("********");
        txtPassword.setBounds(40, 140, 340, 42);
        form.add(txtPassword);

        JButton btn = makePrimaryButton("LOG IN", main);
        btn.setBounds(40, 205, 340, 48);
        btn.addActionListener(e -> handleLogin());
        form.add(btn);

        if(!adminLogin) {
            JLabel regHint = makeLabel("New to " + bankService.getBankName() + "?", TEXT_MID, 12, Font.PLAIN);
            regHint.setHorizontalAlignment(SwingConstants.CENTER);
            regHint.setBounds(40, 280, 340, 20);
            form.add(regHint);

            JButton regBtn = makeOutlineButton("Create Account", ACCENT);
            regBtn.setBounds(100, 305, 220, 40);
            regBtn.addActionListener(e -> { new RegisterFrame(bankService).setVisible(true); dispose(); });
            form.add(regBtn);
        }

        root.add(form, BorderLayout.CENTER);
        add(root);
    }

    private void handleLogin() {
        String user = txtUsername.getText().trim();
        String pass = new String(txtPassword.getPassword());
        
        // --- Premium Loading Overlay ---
        JPanel glass = new JPanel(new GridBagLayout()) {
            @Override protected void paintComponent(Graphics g) {
                g.setColor(new Color(255, 255, 255, 200));
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        glass.setOpaque(false);
        JLabel lbl = makeLabel("Connecting to Secure Server...", TEXT_DARK, 14, Font.BOLD);
        JProgressBar bar = new JProgressBar();
        bar.setIndeterminate(true);
        bar.setPreferredSize(new Dimension(200, 5));
        
        JPanel box = createWhiteCard(15);
        box.setLayout(new BorderLayout(0, 15));
        box.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        box.add(lbl, BorderLayout.NORTH);
        box.add(bar, BorderLayout.CENTER);
        glass.add(box);
        
        setGlassPane(glass);
        glass.setVisible(true);

        SwingWorker<User, Void> worker = new SwingWorker<>() {
            @Override protected User doInBackground() throws Exception {
                Thread.sleep(1500); // Simulate network latency
                return bankService.login(user, pass);
            }

            @Override protected void done() {
                try {
                    User u = get();
                    if(adminLogin && !u.isAdmin()) throw new Exception("Admin access required.");
                    if(!adminLogin && u.isAdmin()) throw new Exception("Please use the admin portal.");
                    
                    if(u.isAdmin()) new AdminFrame(bankService, u).setVisible(true);
                    else new DashboardFrame(bankService, u).setVisible(true);
                    dispose();
                } catch (Exception ex) {
                    glass.setVisible(false);
                    String msg = ex.getMessage();
                    if (ex.getCause() != null) msg = ex.getCause().getMessage();
                    JOptionPane.showMessageDialog(LoginFrame.this, msg, "Connection Error", 0);
                }
            }
        };
        worker.execute();
    }
}
