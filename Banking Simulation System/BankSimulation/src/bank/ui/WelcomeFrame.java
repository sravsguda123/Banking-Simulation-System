package bank.ui;

import bank.service.BankService;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import static bank.ui.UIHelper.*;

public class WelcomeFrame extends JFrame {
    private BankService bankService;

    public WelcomeFrame(BankService bankService) {
        this.bankService = bankService;
        setUndecorated(true);
        setSize(520, 560);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setShape(new RoundRectangle2D.Double(0, 0, 520, 560, 20, 20));
        initUI();
    }

    private void initUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG);

        // --- Premium Header ---
        JPanel header = new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Gradient depth
                g2.setPaint(new GradientPaint(0, 0, NAVY, 0, getHeight(), new Color(2, 6, 23)));
                g2.fillRect(0, 0, getWidth(), getHeight());
                
                // Decorative Mesh / Circles
                g2.setColor(new Color(255, 255, 255, 5));
                for(int i=0; i<5; i++) {
                    g2.drawOval(-50 + i*40, -50 + i*10, 300, 300);
                }
            }
        };
        header.setPreferredSize(new Dimension(520, 220));

        JLabel close = makeLabel("X", Color.WHITE, 16, Font.BOLD);
        close.setBounds(485, 15, 20, 20);
        close.setCursor(new Cursor(Cursor.HAND_CURSOR));
        close.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { System.exit(0); }
        });
        header.add(close);

        // Bank Icon (Drawn)
        JPanel logoArea = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                drawIcon((Graphics2D)g, "BANK", 0, 0, 70, Color.WHITE);
            }
        };
        logoArea.setOpaque(false);
        logoArea.setBounds(225, 35, 70, 70);
        header.add(logoArea);

        JLabel name = makeLabel(bankService.getBankName().toUpperCase(), Color.WHITE, 26, Font.BOLD);
        name.setHorizontalAlignment(SwingConstants.CENTER);
        name.setBounds(50, 115, 420, 35);
        header.add(name);

        JLabel sub = makeLabel("PREMIUM DIGITAL BANKING EXPERIENCE", new Color(148, 163, 184), 12, Font.BOLD);
        sub.setHorizontalAlignment(SwingConstants.CENTER);
        sub.setBounds(50, 150, 420, 20);
        header.add(sub);

        root.add(header, BorderLayout.NORTH);

        // --- Portal Selection ---
        JPanel center = new JPanel(null);
        center.setOpaque(false);

        JLabel prompt = makeLabel("Select your entry portal", TEXT_MID, 15, Font.PLAIN);
        prompt.setHorizontalAlignment(SwingConstants.CENTER);
        prompt.setBounds(50, 25, 420, 25);
        center.add(prompt);

        // Customer Card
        JPanel custCard = createWhiteCard(15);
        custCard.setLayout(null);
        custCard.setBounds(40, 65, 205, 190);
        
        JPanel cIcon = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                drawIcon((Graphics2D)g, "USER", 0, 0, 55, ACCENT);
            }
        };
        cIcon.setOpaque(false);
        cIcon.setBounds(75, 20, 55, 55);
        custCard.add(cIcon);

        JLabel cTitle = makeLabel("Personal Login", NAVY, 16, Font.BOLD);
        cTitle.setHorizontalAlignment(SwingConstants.CENTER);
        cTitle.setBounds(10, 85, 185, 25);
        custCard.add(cTitle);

        JLabel cDesc = makeLabel("Savings & Investments", TEXT_MID, 11, Font.PLAIN);
        cDesc.setHorizontalAlignment(SwingConstants.CENTER);
        cDesc.setBounds(10, 110, 185, 18);
        custCard.add(cDesc);

        JButton cBtn = makePrimaryButton("Log In", ACCENT);
        cBtn.setBounds(30, 140, 145, 38);
        cBtn.addActionListener(e -> { new LoginFrame(bankService, false).setVisible(true); dispose(); });
        custCard.add(cBtn);
        center.add(custCard);

        // Admin Card
        JPanel adminCard = createWhiteCard(15);
        adminCard.setLayout(null);
        adminCard.setBounds(275, 65, 205, 190);
        
        JPanel aIcon = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                drawIcon((Graphics2D)g, "ADMIN", 0, 0, 55, new Color(139, 92, 246));
            }
        };
        aIcon.setOpaque(false);
        aIcon.setBounds(75, 20, 55, 55);
        adminCard.add(aIcon);

        JLabel aTitle = makeLabel("Admin Portal", NAVY, 16, Font.BOLD);
        aTitle.setHorizontalAlignment(SwingConstants.CENTER);
        aTitle.setBounds(10, 85, 185, 25);
        adminCard.add(aTitle);

        JLabel aDesc = makeLabel("System Management", TEXT_MID, 11, Font.PLAIN);
        aDesc.setHorizontalAlignment(SwingConstants.CENTER);
        aDesc.setBounds(10, 110, 185, 18);
        adminCard.add(aDesc);

        JButton aBtn = makePrimaryButton("Log In", new Color(139, 92, 246));
        aBtn.setBounds(30, 140, 145, 38);
        aBtn.addActionListener(e -> { new LoginFrame(bankService, true).setVisible(true); dispose(); });
        adminCard.add(aBtn);
        center.add(adminCard);

        // Footer Info Bar
        JPanel bar = new JPanel(new GridLayout(1, 3, 20, 0));
        bar.setOpaque(false);
        bar.setBounds(40, 275, 440, 45);
        bar.add(miniInfo("NEFT/RTGS", "TRANSFER"));
        bar.add(miniInfo("CASHBACK", "STAR"));
        bar.add(miniInfo("24/7 SUPPORT", "HISTORY"));
        center.add(bar);

        root.add(center, BorderLayout.CENTER);
        add(root);
    }

    private JPanel miniInfo(String text, String icon) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        p.setOpaque(false);
        JPanel ico = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                drawIcon((Graphics2D)g, icon, 0, 0, 18, TEXT_LIGHT);
            }
        };
        ico.setPreferredSize(new Dimension(18, 18));
        ico.setOpaque(false);
        JLabel lbl = makeLabel(text, TEXT_MID, 10, Font.BOLD);
        p.add(ico);
        p.add(lbl);
        return p;
    }
}
