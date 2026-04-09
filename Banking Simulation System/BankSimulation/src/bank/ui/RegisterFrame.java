package bank.ui;

import bank.service.BankService;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import static bank.ui.UIHelper.*;

public class RegisterFrame extends JFrame {
    private BankService bankService;
    private JTextField txtUser;
    private JPasswordField txtPass;
    private JComboBox<String> cbAccType;

    public RegisterFrame(BankService bankService) {
        this.bankService = bankService;
        setUndecorated(true);
        setSize(420, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setShape(new RoundRectangle2D.Double(0, 0, 420, 550, 20, 20));
        initUI();
    }

    private void initUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG);

        JPanel header = new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(0, 0, GREEN, 0, getHeight(), new Color(21, 128, 61)));
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        header.setPreferredSize(new Dimension(420, 140));

        JLabel back = makeLabel("<  BACK", new Color(255, 255, 255, 180), 11, Font.BOLD);
        back.setBounds(15, 15, 60, 20);
        back.setCursor(new Cursor(Cursor.HAND_CURSOR));
        back.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { new LoginFrame(bankService, false).setVisible(true); dispose(); }
        });
        header.add(back);

        JPanel icon = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                drawIcon((Graphics2D)g, "USER", 0, 0, 50, Color.WHITE);
            }
        };
        icon.setOpaque(false);
        icon.setBounds(185, 25, 50, 50);
        header.add(icon);

        JLabel title = makeLabel("Open New Account", Color.WHITE, 19, Font.BOLD);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setBounds(50, 85, 320, 30);
        header.add(title);

        root.add(header, BorderLayout.NORTH);

        JPanel form = new JPanel(null);
        form.setOpaque(false);

        JLabel lblU = makeLabel("CHOOSE USERNAME", TEXT_MID, 11, Font.BOLD);
        lblU.setBounds(40, 30, 200, 20);
        form.add(lblU);
        txtUser = makeField("Desired username");
        txtUser.setBounds(40, 55, 340, 42);
        form.add(txtUser);

        JLabel lblP = makeLabel("CHOOSE PASSWORD", TEXT_MID, 11, Font.BOLD);
        lblP.setBounds(40, 115, 200, 20);
        form.add(lblP);
        txtPass = makePasswordField("********");
        txtPass.setBounds(40, 140, 340, 42);
        form.add(txtPass);

        JLabel lblA = makeLabel("ACCOUNT TYPE", TEXT_MID, 11, Font.BOLD);
        lblA.setBounds(40, 200, 200, 20);
        form.add(lblA);
        cbAccType = new JComboBox<>(new String[]{"Savings Account", "Current Account"});
        cbAccType.setBounds(40, 225, 340, 42);
        cbAccType.setBackground(WHITE);
        cbAccType.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        form.add(cbAccType);

        JButton btn = makePrimaryButton("REGISTER NOW", GREEN);
        btn.setBounds(40, 300, 340, 50);
        btn.addActionListener(e -> handle());
        form.add(btn);

        root.add(form, BorderLayout.CENTER);
        add(root);
    }

    private void handle() {
        String u = txtUser.getText().trim();
        String p = new String(txtPass.getPassword());
        if(u.isEmpty() || p.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Empty fields.", "Error", 2);
            return;
        }
        try {
            boolean isSavings = cbAccType.getSelectedIndex() == 0;
            bankService.registerUser(u, p, isSavings);
            JOptionPane.showMessageDialog(this, "Success! Please log in.", "Registration", 1);
            new LoginFrame(bankService, false).setVisible(true);
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", 0);
        }
    }
}
