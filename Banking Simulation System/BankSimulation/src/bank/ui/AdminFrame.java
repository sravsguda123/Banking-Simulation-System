package bank.ui;

import bank.service.BankService;
import bank.model.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.List;
import java.util.Vector;
import static bank.ui.UIHelper.*;

public class AdminFrame extends JFrame {
    private BankService bankService;
    private CardLayout cardLayout;
    private JPanel cardPanel;
    private DefaultTableModel accountModel;
    private DefaultTableModel fraudModel;
    private JPanel statsRow;
    private JButton activeBtn = null;

    public AdminFrame(BankService bankService, User adminUser) {
        this.bankService = bankService;
        setUndecorated(true);
        setSize(1100, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setShape(new RoundRectangle2D.Double(0, 0, 1100, 720, 20, 20));
        initUI();
        refreshAll();
    }

    private void initUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG);

        Color main = new Color(55, 20, 80);
        JPanel sidebar = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(main);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(240, 720));

        JPanel logo = new JPanel(null);
        logo.setOpaque(false);
        logo.setMaximumSize(new Dimension(240, 100));
        
        JPanel ico = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                drawIcon((Graphics2D)g, "ADMIN", 0, 0, 24, Color.WHITE);
            }
        };
        ico.setOpaque(false); ico.setBounds(20, 30, 24, 24); logo.add(ico);
        
        JLabel t = makeLabel("ADMIN PORTAL", Color.WHITE, 16, Font.BOLD);
        t.setBounds(55, 30, 170, 24); logo.add(t);
        sidebar.add(logo);

        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(adminNavBtn("All Accounts", "accounts", main, "HISTORY"));
        sidebar.add(adminNavBtn("Fraud Alerts", "fraud", main, "ALERT"));

        sidebar.add(Box.createVerticalGlue());
        JButton logout = adminNavBtn("Logout", null, main, "LOGOUT");
        logout.setForeground(new Color(255, 150, 150));
        logout.addActionListener(e -> { new WelcomeFrame(bankService).setVisible(true); dispose(); });
        sidebar.add(logout);
        sidebar.add(Box.createVerticalStrut(20));
        
        root.add(sidebar, BorderLayout.WEST);

        JPanel mainArea = new JPanel(new BorderLayout());
        mainArea.setOpaque(false);

        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(WHITE);
        top.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER));
        top.setPreferredSize(new Dimension(860, 65));
        top.add(makeLabel("MANAGEMENT CONSOLE", TEXT_MID, 12, Font.BOLD), BorderLayout.WEST);
        
        JLabel close = makeLabel("X", TEXT_LIGHT, 16, Font.BOLD);
        close.setCursor(new Cursor(12));
        close.addMouseListener(new MouseAdapter() { public void mouseClicked(MouseEvent e) { System.exit(0); } });
        top.add(close, BorderLayout.EAST);
        mainArea.add(top, BorderLayout.NORTH);

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setOpaque(false);
        cardPanel.add(buildAccountsPage(), "accounts");
        cardPanel.add(buildFraudPage(), "fraud");
        mainArea.add(cardPanel, BorderLayout.CENTER);

        root.add(mainArea, BorderLayout.CENTER);
        add(root);
    }

    private JButton adminNavBtn(String text, String page, Color base, String iconType) {
        JButton b = new JButton(text);
        b.setIcon(getIcon(iconType, 18, new Color(210, 180, 230)));
        b.setIconTextGap(15);
        b.setFont(new Font("Segoe UI", Font.BOLD, 14));
        b.setForeground(new Color(210, 180, 230));
        b.setBackground(base);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setHorizontalAlignment(0);
        b.setMaximumSize(new Dimension(240, 50));
        b.setPreferredSize(new Dimension(240, 50));
        b.setCursor(new Cursor(12));
        if (page != null) {
            b.addActionListener(e -> {
                refreshAll();
                cardLayout.show(cardPanel, page);
                if (activeBtn != null) b.setBackground(base);
                b.setBackground(new Color(90, 40, 120));
                activeBtn = b;
            });
        }
        return b;
    }

    private JPanel buildAccountsPage() {
        JPanel p = new JPanel(new BorderLayout(0, 25));
        p.setOpaque(false); p.setBorder(new EmptyBorder(30,30,30,30));
        p.add(makeLabel("Global Account Registry", mainColor(), 24, Font.BOLD), BorderLayout.NORTH);
        
        statsRow = new JPanel(new FlowLayout(0,20,0));
        statsRow.setOpaque(false);
        
        JPanel mid = new JPanel(new BorderLayout(0,25));
        mid.setOpaque(false); mid.add(statsRow, BorderLayout.NORTH);

        JPanel tableC = createWhiteCard(15);
        tableC.setLayout(new BorderLayout());
        tableC.setBorder(new EmptyBorder(20,20,20,20));
        String[] cols = {"User", "Account No.", "Type", "Balance"};
        accountModel = new DefaultTableModel(cols, 0);
        JTable t = makeTable(accountModel);
        JScrollPane sp = new JScrollPane(t); sp.setBorder(null); sp.getViewport().setBackground(WHITE);
        tableC.add(sp, BorderLayout.CENTER);
        mid.add(tableC, BorderLayout.CENTER);
        
        p.add(mid, BorderLayout.CENTER);
        return p;
    }

    private JPanel buildFraudPage() {
        JPanel p = new JPanel(new BorderLayout(0, 25));
        p.setOpaque(false); p.setBorder(new EmptyBorder(30,30,30,30));
        p.add(makeLabel("Fraud Detection System", RED, 24, Font.BOLD), BorderLayout.NORTH);
        
        JPanel tableC = createWhiteCard(15);
        tableC.setLayout(new BorderLayout());
        tableC.setBorder(new EmptyBorder(20,20,20,20));
        String[] cols = {"ID", "Suspicious Activity Description"};
        fraudModel = new DefaultTableModel(cols,0);
        JTable t = makeTable(fraudModel);
        JScrollPane sp = new JScrollPane(t); sp.setBorder(null); sp.getViewport().setBackground(WHITE);
        tableC.add(sp, BorderLayout.CENTER);
        p.add(tableC, BorderLayout.CENTER);
        return p;
    }

    private Color mainColor() { return new Color(89, 30, 120); }

    private void refreshAll() {
        statsRow.removeAll();
        List<Account> all = bankService.getAllAccounts();
        double liq = 0; for(Account a : all) if(!(a instanceof CreditCardAccount)) liq += a.getBalance();
        statsRow.add(makeStat("Liquidity", "₹"+String.format("%.2f", liq), GREEN, "BANK"));
        statsRow.add(makeStat("Accounts", String.valueOf(all.size()), mainColor(), "HISTORY"));
        statsRow.add(makeStat("Fraud", String.valueOf(bankService.getFraudSystem().getAlertCount()), RED, "ALERT"));
        statsRow.revalidate(); statsRow.repaint();

        accountModel.setRowCount(0);
        for(Account a : all) {
            Vector<String> r = new Vector<>();
            r.add(a.getUsername()); r.add(a.getAccountNumber()); r.add(a.getAccountType());
            
            double bal = a.getBalance();
            boolean isCC = a instanceof CreditCardAccount;
            String valStr;
            if (isCC) {
                if (bal == 0) valStr = "₹ 0.00";
                else if (bal > 0) valStr = "- ₹ " + String.format("%.2f", bal);
                else valStr = "+ ₹ " + String.format("%.2f", Math.abs(bal));
            } else {
                if (bal >= 0) valStr = "₹ " + String.format("%.2f", bal);
                else valStr = "- ₹ " + String.format("%.2f", Math.abs(bal));
            }
            r.add(valStr);
            accountModel.addRow(r);
        }

        fraudModel.setRowCount(0);
        List<String> alerts = bankService.getFraudSystem().getFraudAlerts();
        for(int i=0; i<alerts.size(); i++) {
            Vector<String> r = new Vector<>();
            r.add(String.valueOf(i+1)); r.add(alerts.get(i));
            fraudModel.addRow(r);
        }
    }
}
