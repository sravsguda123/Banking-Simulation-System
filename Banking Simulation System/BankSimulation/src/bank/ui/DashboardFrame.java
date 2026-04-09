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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import java.util.Map;

import static bank.ui.UIHelper.*;

public class DashboardFrame extends JFrame {
    private BankService bankService;
    private User currentUser;

    private CardLayout cardLayout;
    private JPanel cardPanel;
    private JComboBox<String> cbAccounts;
    private DefaultTableModel txModel;
    private JPanel statsRow;
    private JPanel chartArea;
    private JPanel goalsArea;
    private JButton activeNavBtn = null;

    public DashboardFrame(BankService bankService, User user) {
        this.bankService = bankService;
        this.currentUser = user;
        setUndecorated(true);
        setSize(1100, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setShape(new RoundRectangle2D.Double(0, 0, 1100, 750, 20, 20));
        initUI();
        refreshData();
    }

    private void initUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG);

        // Sidebar
        JPanel sidebar = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(SIDEBAR_BG);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(240, 750));

        JPanel logoPanel = new JPanel(null);
        logoPanel.setOpaque(false);
        logoPanel.setMaximumSize(new Dimension(240, 100));
        JPanel bankIcon = new JPanel() { @Override protected void paintComponent(Graphics g) { drawIcon((Graphics2D)g, "BANK", 0, 0, 24, Color.WHITE); } };
        bankIcon.setOpaque(false); bankIcon.setBounds(20, 30, 24, 24); logoPanel.add(bankIcon);
        JLabel bankTitle = makeLabel(bankService.getBankName(), Color.WHITE, 18, Font.BOLD);
        bankTitle.setBounds(52, 30, 170, 24); logoPanel.add(bankTitle);
        sidebar.add(logoPanel);
        sidebar.add(Box.createVerticalStrut(10));

        sidebar.add(navBtn("Summary", "overview", "HISTORY"));
        sidebar.add(navBtn("Transactions", "ops", "RS"));
        sidebar.add(navBtn("Prosperity Goals", "goals", "STAR"));
        sidebar.add(navBtn("Investments", "invest", "GRAPH"));
        sidebar.add(navBtn("Benefits", "benefits", "SHIELD"));
        sidebar.add(navBtn("Bills & Lifestyle", "lifestyle", "BILL"));
        sidebar.add(navBtn("Cards", "cards", "CARD"));

        sidebar.add(Box.createVerticalGlue());
        JButton logout = navBtn("Sign Out", null, "LOGOUT");
        logout.setForeground(new Color(248, 113, 113));
        logout.addActionListener(e -> { new WelcomeFrame(bankService).setVisible(true); dispose(); });
        sidebar.add(logout);
        sidebar.add(Box.createVerticalStrut(20));
        root.add(sidebar, BorderLayout.WEST);

        // Main Area
        JPanel mainArea = new JPanel(new BorderLayout());
        mainArea.setOpaque(false);

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(WHITE);
        topBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER));
        topBar.setPreferredSize(new Dimension(860, 65));

        JPanel leftTop = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 20));
        leftTop.setOpaque(false);
        leftTop.add(makeLabel("Welcome back, " + currentUser.getUsername(), TEXT_DARK, 16, Font.BOLD));
        topBar.add(leftTop, BorderLayout.WEST);

        JPanel rightTop = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 20));
        rightTop.setOpaque(false);
        
        JPanel bell = new JPanel() { @Override protected void paintComponent(Graphics g) { drawIcon((Graphics2D)g, "BELL", 0, 0, 20, TEXT_MID); } };
        bell.setPreferredSize(new Dimension(20, 20)); bell.setCursor(new Cursor(Cursor.HAND_CURSOR));
        rightTop.add(bell);

        JLabel closeBtn = makeLabel("X", TEXT_LIGHT, 16, Font.BOLD);
        closeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeBtn.addMouseListener(new MouseAdapter() { public void mouseClicked(MouseEvent e) { System.exit(0); } });
        rightTop.add(closeBtn);
        topBar.add(rightTop, BorderLayout.EAST);
        mainArea.add(topBar, BorderLayout.NORTH);

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setOpaque(false);
        cardPanel.add(buildOverviewPage(), "overview");
        cardPanel.add(buildOpsPage(), "ops");
        cardPanel.add(buildGoalsPage(), "goals");
        cardPanel.add(buildInvestPage(), "invest");
        cardPanel.add(buildBenefitsPage(), "benefits");
        cardPanel.add(buildLifestylePage(), "lifestyle");
        cardPanel.add(buildCardsPage(), "cards");
        mainArea.add(cardPanel, BorderLayout.CENTER);

        root.add(mainArea, BorderLayout.CENTER);
        add(root);
    }

    private JButton navBtn(String text, String page, String iconType) {
        JButton b = new JButton(text);
        b.setIcon(getIcon(iconType, 18, SIDEBAR_TEXT));
        b.setIconTextGap(15);
        b.setFont(new Font("Segoe UI", Font.BOLD, 14));
        b.setForeground(SIDEBAR_TEXT);
        b.setBackground(SIDEBAR_BG);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setHorizontalAlignment(SwingConstants.LEFT);
        b.setMaximumSize(new Dimension(240, 50));
        b.setPreferredSize(new Dimension(240, 50));
        b.setBorder(new EmptyBorder(0, 25, 0, 0));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        if (page != null) {
            b.addActionListener(e -> {
                refreshData();
                cardLayout.show(cardPanel, page);
                if (activeNavBtn != null) { activeNavBtn.setBackground(SIDEBAR_BG); activeNavBtn.setForeground(SIDEBAR_TEXT); }
                b.setBackground(new Color(30, 41, 59)); b.setForeground(WHITE);
                activeNavBtn = b;
            });
        }
        return b;
    }

    private JPanel buildOverviewPage() {
        JPanel p = new JPanel(new BorderLayout(0, 20));
        p.setOpaque(false); p.setBorder(new EmptyBorder(30, 30, 30, 30));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(makeLabel("Financial Insights", NAVY, 24, Font.BOLD), BorderLayout.WEST);

        JPanel accSel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        accSel.setOpaque(false);
        cbAccounts = new JComboBox<>();
        cbAccounts.setPreferredSize(new Dimension(280, 35));
        cbAccounts.addActionListener(e -> updateStats());
        accSel.add(cbAccounts);
        top.add(accSel, BorderLayout.EAST);
        p.add(top, BorderLayout.NORTH);

        JPanel mid = new JPanel(new BorderLayout(0, 20));
        mid.setOpaque(false);
        statsRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        statsRow.setOpaque(false);
        mid.add(statsRow, BorderLayout.NORTH);

        JPanel content = new JPanel(new GridLayout(1, 2, 20, 0));
        content.setOpaque(false);

        JPanel chartCard = createWhiteCard(15);
        chartCard.setLayout(new BorderLayout());
        chartCard.setBorder(new EmptyBorder(20, 20, 20, 20));
        chartCard.add(makeLabel("MONTHLY ACTIVITY", TEXT_MID, 11, Font.BOLD), BorderLayout.NORTH);
        chartArea = new JPanel(new BorderLayout());
        chartArea.setOpaque(false);
        chartCard.add(chartArea, BorderLayout.CENTER);
        content.add(chartCard);

        JPanel tableCard = createWhiteCard(15);
        tableCard.setLayout(new BorderLayout());
        tableCard.setBorder(new EmptyBorder(20, 20, 20, 20));
        tableCard.add(makeLabel("RECENT ACTIVITY", TEXT_MID, 11, Font.BOLD), BorderLayout.NORTH);
        txModel = new DefaultTableModel(new String[]{"DATE", "TYPE", "AMOUNT"}, 0);
        JTable table = makeTable(txModel);
        JScrollPane sp = new JScrollPane(table); sp.setBorder(null); sp.getViewport().setBackground(WHITE);
        tableCard.add(sp, BorderLayout.CENTER);
        content.add(tableCard);

        mid.add(content, BorderLayout.CENTER);
        p.add(mid, BorderLayout.CENTER);
        return p;
    }

    private JPanel buildGoalsPage() {
        JPanel p = new JPanel(null); p.setOpaque(false);
        p.add(makeLabel("Prosperity Goals", NAVY, 24, Font.BOLD)).setBounds(30, 30, 400, 35);
        
        JPanel createCard = createWhiteCard(15); createCard.setLayout(null);
        createCard.setBounds(30, 85, 400, 280);
        createCard.add(makeLabel("CREATE NEW GOAL", NAVY, 14, Font.BOLD)).setBounds(25, 20, 200, 25);
        
        createCard.add(makeLabel("GOAL NAME", TEXT_MID, 10, Font.BOLD)).setBounds(25, 60, 200, 15);
        JTextField tN = makeField("e.g. Dream Car"); tN.setBounds(25, 80, 350, 40); createCard.add(tN);

        createCard.add(makeLabel("TARGET AMOUNT (₹)", TEXT_MID, 10, Font.BOLD)).setBounds(25, 135, 200, 15);
        JTextField tA = makeField("50000"); tA.setBounds(25, 155, 350, 40); createCard.add(tA);

        JButton btn = makePrimaryButton("START TRACKING", ORANGE);
        btn.setBounds(25, 215, 350, 45);
        btn.addActionListener(e -> {
            try {
                bankService.addGoal(currentUser.getUsername(), tN.getText(), Double.parseDouble(tA.getText()));
                tN.setText(""); tA.setText(""); refreshData();
            } catch (Exception ex) { showErr(ex.getMessage()); }
        });
        createCard.add(btn);
        p.add(createCard);

        p.add(makeLabel("My Aspirations", NAVY, 18, Font.BOLD)).setBounds(460, 30, 300, 30);
        goalsArea = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 20));
        goalsArea.setOpaque(false);
        JScrollPane sp = new JScrollPane(goalsArea);
        sp.setBounds(460, 85, 580, 580);
        sp.setBorder(null); sp.setOpaque(false); sp.getViewport().setOpaque(false);
        p.add(sp);

        return p;
    }

    private JPanel buildInvestPage() {
        JPanel p = new JPanel(null); p.setOpaque(false);
        p.add(makeLabel("Fixed Deposits", NAVY, 24, Font.BOLD)).setBounds(30, 30, 400, 35);
        
        JPanel card = createWhiteCard(15);
        card.setLayout(null); card.setBounds(30, 85, 450, 320);
        card.add(makeLabel("Create New Fixed Deposit", NAVY, 16, Font.BOLD)).setBounds(25, 20, 300, 25);
        
        card.add(makeLabel("AMOUNT (₹)", TEXT_MID, 10, Font.BOLD)).setBounds(25, 60, 200, 15);
        JTextField tA = makeField("5000"); tA.setBounds(25, 80, 400, 40); card.add(tA);

        card.add(makeLabel("TENURE (MONTHS)", TEXT_MID, 10, Font.BOLD)).setBounds(25, 135, 200, 15);
        String[] terms = {"6 Months (5.5%)", "12 Months (7.5%)", "24 Months (7.5%)"};
        JComboBox<String> cbT = new JComboBox<>(terms); cbT.setBounds(25, 155, 400, 40); card.add(cbT);

        JButton btn = makePrimaryButton("OPEN FD", GREEN);
        btn.setBounds(25, 230, 400, 50);
        btn.addActionListener(e -> {
            try {
                int m = (cbT.getSelectedIndex() == 0) ? 6 : (cbT.getSelectedIndex() == 1 ? 12 : 24);
                bankService.createFixedDeposit(currentUser.getUsername(), getSelAcc(), Double.parseDouble(tA.getText()), m);
                tA.setText(""); refreshData();
                JOptionPane.showMessageDialog(this, "Fixed Deposit Created successfully!");
            } catch (Exception ex) { showErr(ex.getMessage()); }
        });
        card.add(btn);
        p.add(card);
        return p;
    }

    private JPanel buildBenefitsPage() {
        JPanel p = new JPanel(new BorderLayout(0, 30));
        p.setOpaque(false); p.setBorder(new EmptyBorder(30,30,30,30));
        
        p.add(makeLabel("Membership Benefits", NAVY, 26, Font.BOLD), BorderLayout.NORTH);
        
        JPanel grid = new JPanel(new GridLayout(1, 2, 30, 0));
        grid.setOpaque(false);
        
        // Savings Card
        JPanel sav = createWhiteCard(20); sav.setLayout(new BorderLayout());
        JPanel savTop = new JPanel(new FlowLayout(FlowLayout.CENTER)); savTop.setOpaque(false); savTop.setBorder(new EmptyBorder(30,0,10,0));
        JPanel savIco = new JPanel() { @Override protected void paintComponent(Graphics g) { drawIcon((Graphics2D)g, "GRAPH", 0, 0, 60, GREEN); } };
        savIco.setPreferredSize(new Dimension(60, 60)); savTop.add(savIco);
        sav.add(savTop, BorderLayout.NORTH);
        
        JPanel savContent = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 18));
                g2.setColor(NAVY);
                g2.drawString("Savings Account", 85, 30);
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                g2.setColor(TEXT_MID);
                g2.drawString("\u2713  4.0% Annual Interest Rate", 60, 80);
                g2.drawString("\u2713  Prosperity Goal Tracking", 60, 110);
                g2.drawString("\u2713  Zero Hidden Transaction Fees", 60, 140);
                g2.setColor(ORANGE);
                g2.drawString("!  ₹500 Minimum Balance", 60, 180);
            }
        };
        savContent.setOpaque(false);
        sav.add(savContent, BorderLayout.CENTER);
        grid.add(sav);
        
        // Current Card
        JPanel cur = createWhiteCard(20); cur.setLayout(new BorderLayout());
        JPanel curTop = new JPanel(new FlowLayout(FlowLayout.CENTER)); curTop.setOpaque(false); curTop.setBorder(new EmptyBorder(30,0,10,0));
        JPanel curIco = new JPanel() { @Override protected void paintComponent(Graphics g) { drawIcon((Graphics2D)g, "TRANSFER", 0, 0, 60, ACCENT); } };
        curIco.setPreferredSize(new Dimension(60, 60)); curTop.add(curIco);
        cur.add(curTop, BorderLayout.NORTH);
        
        JPanel curContent = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 18));
                g2.setColor(NAVY);
                g2.drawString("Current Account", 85, 30);
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                g2.setColor(TEXT_MID);
                g2.drawString("\u2713  ₹10,000 Instant Overdraft", 60, 80);
                g2.drawString("\u2713  Unlimited Bill Payments", 60, 110);
                g2.drawString("\u2713  No Minimum Balance", 60, 140);
                g2.setColor(ORANGE);
                g2.drawString("!  ₹15 Service Fee per Withdrawal", 60, 180);
            }
        };
        curContent.setOpaque(false);
        cur.add(curContent, BorderLayout.CENTER);
        grid.add(cur);
        
        p.add(grid, BorderLayout.CENTER);
        return p;
    }

    private JPanel buildLifestylePage() {
        JPanel p = new JPanel(null); p.setOpaque(false);
        p.add(makeLabel("Bills & Lifestyle", NAVY, 24, Font.BOLD)).setBounds(30, 30, 400, 35);
        p.add(billCard("Electricity", 1250, "BILL")).setBounds(30, 85, 250, 180);
        p.add(billCard("Internet", 899, "BILL")).setBounds(310, 85, 250, 180);
        p.add(billCard("Mobile", 499, "BILL")).setBounds(590, 85, 250, 180);
        return p;
    }

    private JPanel billCard(String name, double amt, String icon) {
        JPanel c = createWhiteCard(15); c.setLayout(null);
        JPanel ico = new JPanel() { @Override protected void paintComponent(Graphics g) { drawIcon((Graphics2D)g, icon, 0, 0, 32, ACCENT); } };
        ico.setOpaque(false); ico.setBounds(109, 20, 32, 32); c.add(ico);
        JLabel t = makeLabel(name, NAVY, 15, Font.BOLD); t.setHorizontalAlignment(0); t.setBounds(10, 65, 230, 25); c.add(t);
        JLabel aLabel = makeLabel("₹ " + amt, TEXT_MID, 13, Font.PLAIN); aLabel.setHorizontalAlignment(0); aLabel.setBounds(10, 85, 230, 20); c.add(aLabel);
        JButton b = makePrimaryButton("Pay Now", ACCENT); b.setBounds(40, 120, 170, 35);
        b.addActionListener(e -> {
            try {
                bankService.payBill(currentUser.getUsername(), getSelAcc(), name, amt);
                refreshData(); 
                b.setText("PAID"); b.setEnabled(false); b.setBackground(GREEN);
                aLabel.setText("₹ 0.0"); aLabel.setForeground(GREEN);
                JOptionPane.showMessageDialog(this, name + " Bill Paid Successfully!", "Payment Success", 1);
            } catch (Exception ex) { showErr(ex.getMessage()); }
        });
        c.add(b);
        return c;
    }

    private JPanel buildOpsPage() {
        JPanel p = new JPanel(null); p.setOpaque(false);
        p.add(makeLabel("Account Actions", NAVY, 24, Font.BOLD)).setBounds(30, 30, 400, 35);
        JPanel card = createWhiteCard(15); card.setLayout(null); card.setBounds(30, 85, 600, 280);
        card.add(makeLabel("QUICK TRANSFER (₹)", TEXT_MID, 11, Font.BOLD)).setBounds(25, 60, 200, 20);
        JTextField txtAmt = makeField("0.00"); txtAmt.setBounds(25, 85, 550, 45); card.add(txtAmt);
        JButton btnD = makePrimaryButton("DEPOSIT", GREEN); btnD.setBounds(25, 160, 260, 50);
        btnD.addActionListener(e -> { try { bankService.deposit(getSelAcc(), Double.parseDouble(txtAmt.getText())); txtAmt.setText(""); refreshData(); } catch (Exception ex) { showErr(ex.getMessage()); } });
        card.add(btnD);
        JButton btnW = makePrimaryButton("WITHDRAW", RED); btnW.setBounds(315, 160, 260, 50);
        btnW.addActionListener(e -> { try { bankService.withdraw(getSelAcc(), Double.parseDouble(txtAmt.getText())); txtAmt.setText(""); refreshData(); } catch (Exception ex) { showErr(ex.getMessage()); } });
        card.add(btnW);
        p.add(card);
        return p;
    }

    private JPanel buildCardsPage() {
        JPanel p = new JPanel(null); p.setOpaque(false);
        p.add(makeLabel("Card Management", NAVY, 24, Font.BOLD)).setBounds(30, 30, 400, 35);
        p.add(buildMiniCard("CREDIT CARD", "₹2L Limit + 2% Cashback", ACCENT, true)).setBounds(30, 85, 300, 180);
        p.add(buildMiniCard("DEBIT CARD", "Direct link to account", GREEN, false)).setBounds(350, 85, 300, 180);
        return p;
    }

    private JPanel buildMiniCard(String title, String desc, Color color, boolean isCC) {
        JPanel p = createWhiteCard(15); p.setLayout(null);
        JPanel ico = new JPanel() { @Override protected void paintComponent(Graphics g) { drawIcon((Graphics2D)g, "CARD", 0, 0, 45, color); } };
        ico.setOpaque(false); ico.setBounds(127, 20, 45, 45); p.add(ico);
        JLabel t = makeLabel(title, NAVY, 15, Font.BOLD); t.setHorizontalAlignment(0); t.setBounds(10, 75, 280, 25); p.add(t);
        JLabel d = makeLabel(desc, TEXT_MID, 11, Font.PLAIN); d.setHorizontalAlignment(0); d.setBounds(10, 100, 280, 18); p.add(d);
        JButton btn = makePrimaryButton("Apply", color); btn.setBounds(50, 130, 200, 36);
        btn.addActionListener(e -> { try { if(isCC) bankService.applyForCreditCard(currentUser.getUsername()); else bankService.applyForDebitCard(currentUser.getUsername(), getSelAcc()); refreshData(); JOptionPane.showMessageDialog(this, "Success!"); } catch (Exception ex) { showErr(ex.getMessage()); } });
        p.add(btn); return p;
    }

    private void showErr(String m) { JOptionPane.showMessageDialog(this, m, "Error", 0); }
    private String getSelAcc() { return (cbAccounts.getSelectedItem()==null) ? "" : ((String)cbAccounts.getSelectedItem()).split(" ")[0]; }

    private void refreshData() {
        if(cbAccounts==null) return;
        int idx = cbAccounts.getSelectedIndex();
        cbAccounts.removeAllItems();
        currentUser = bankService.getUser(currentUser.getUsername());
        List<Account> accs = bankService.getUserAccounts(currentUser.getUsername());
        for(Account a : accs) cbAccounts.addItem(a.getAccountNumber() + " (" + a.getAccountType() + ")");
        if(idx>=0 && idx<cbAccounts.getItemCount()) cbAccounts.setSelectedIndex(idx);
        updateStats();

        txModel.setRowCount(0);
        for(Account a : accs) {
            for(Transaction t : a.getTransactions()) {
                Vector<String> r = new Vector<>();
                r.add(new SimpleDateFormat("dd/MM").format(new Date(t.getTimestamp())));
                r.add(t.getType());
                boolean deb = t.getType().equals("WITHDRAW") || t.getType().equals("PURCHASE");
                r.add((deb ? "- " : "+ ") + "₹ " + String.format("%.2f", t.getAmount()));
                txModel.addRow(r);
            }
        }

        chartArea.removeAll();
        chartArea.add(createBarChart(bankService.getMonthlyAnalytics(currentUser.getUsername())));
        chartArea.revalidate();

        updateGoalsArea();
    }

    private void updateGoalsArea() {
        if(goalsArea == null) return;
        goalsArea.removeAll();
        goalsArea.setPreferredSize(new Dimension(560, bankService.getGoals(currentUser.getUsername()).size() * 120));
        for(SavingsGoal g : bankService.getGoals(currentUser.getUsername())) {
            JPanel card = createWhiteCard(12);
            card.setLayout(new BorderLayout(15, 0));
            card.setPreferredSize(new Dimension(540, 100));
            card.setBorder(new EmptyBorder(15, 20, 15, 20));
            
            JPanel left = new JPanel(new GridLayout(2, 1)); left.setOpaque(false);
            left.add(makeLabel(g.getName(), NAVY, 15, Font.BOLD));
            left.add(makeLabel("Target: ₹" + g.getTargetAmount(), TEXT_MID, 12, Font.PLAIN));
            
            JPanel mid = new JPanel(new GridBagLayout()); mid.setOpaque(false);
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
            gbc.insets = new Insets(0, 20, 0, 15);
            mid.add(createProgressBar(g.getProgress(), ORANGE), gbc);
            
            gbc.weightx = 0; gbc.gridx = 1; gbc.insets = new Insets(0, 0, 0, 10);
            int percent = (int)(g.getProgress() * 100);
            JLabel pctLabel = makeLabel(percent + "%", percent < 50 ? TEXT_MID : (percent < 100 ? ORANGE : GREEN), 13, Font.BOLD);
            pctLabel.setPreferredSize(new Dimension(45, 20));
            mid.add(pctLabel, gbc);
            
            JButton add = makePrimaryButton("+ CONTRIBUTE", GREEN);
            add.setPreferredSize(new Dimension(140, 35));
            add.addActionListener(e -> {
                String val = JOptionPane.showInputDialog(this, "Contribute to " + g.getName());
                if(val != null) {
                    try {
                        bankService.contributeToGoal(currentUser.getUsername(), g.getName(), Double.parseDouble(val));
                        refreshData();
                    } catch(Exception ex) { showErr(ex.getMessage()); }
                }
            });

            card.add(left, BorderLayout.WEST);
            card.add(mid, BorderLayout.CENTER);
            card.add(add, BorderLayout.EAST);
            goalsArea.add(card);
        }
        goalsArea.revalidate(); goalsArea.repaint();
    }

    private void updateStats() {
        if(statsRow==null) return;
        statsRow.removeAll();
        currentUser = bankService.getUser(currentUser.getUsername());
        String cur = getSelAcc();
        for(Account a : bankService.getUserAccounts(currentUser.getUsername())) {
            if(a.getAccountNumber().equals(cur)) {
                boolean cc = a instanceof CreditCardAccount;
                double bal = a.getBalance();
                String valStr, label; Color col;
                if (cc) { label = (bal > 0) ? "Debt" : "Credit"; col = (bal > 0) ? RED : GREEN; valStr = (bal == 0) ? "₹0.00" : (bal > 0 ? "- ₹" : "+ ₹") + String.format("%.2f", Math.abs(bal));
                } else { label = (bal >= 0) ? "Balance" : "Overdraft"; col = (bal < 0) ? RED : GREEN; valStr = (bal >= 0 ? "" : "- ") + "₹" + String.format("%.2f", Math.abs(bal)); }
                statsRow.add(makeStat(label, valStr, col, cc?"CARD":"RS"));

                if (a instanceof SavingsAccount) {
                    statsRow.add(makeStat("Interest Rate", "4.0% APY", GREEN, "GRAPH"));
                } else if (a instanceof CurrentAccount) {
                    statsRow.add(makeStat("Service Fee", "₹15 / Tx", ORANGE, "BILL"));
                }
                break;
            }
        }
        statsRow.add(makeStat("Reward Pts", String.valueOf((int)currentUser.getRewardPoints()), ORANGE, "STAR"));
        statsRow.revalidate(); statsRow.repaint();
    }
}
