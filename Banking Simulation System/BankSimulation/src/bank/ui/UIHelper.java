package bank.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.geom.*;

public class UIHelper {

    // --- Premium Light Banking Palette ---
    public static final Color BG           = new Color(248, 250, 253); 
    public static final Color WHITE        = Color.WHITE;
    public static final Color NAVY         = new Color(10, 31, 68);     
    public static final Color NAVY_LIGHT   = new Color(40, 70, 130);
    public static final Color ACCENT       = new Color(0, 102, 255);    
    public static final Color GREEN        = new Color(34, 197, 94);   
    public static final Color RED          = new Color(239, 68, 68);    
    public static final Color ORANGE       = new Color(249, 115, 22);   
    public static final Color TEXT_DARK    = new Color(15, 23, 42);    
    public static final Color TEXT_MID     = new Color(71, 85, 105);   
    public static final Color TEXT_LIGHT   = new Color(148, 163, 184); 
    public static final Color BORDER       = new Color(226, 232, 240);  
    public static final Color SIDEBAR_BG   = new Color(10, 31, 68);     
    public static final Color SIDEBAR_TEXT = new Color(203, 213, 225);  
    public static final Color ROW_ALT      = new Color(241, 245, 249);  

    public static Icon getIcon(String type, int size, Color color) {
        return new Icon() {
            @Override public void paintIcon(Component c, Graphics g, int x, int y) {
                drawIcon((Graphics2D)g, type, x, y, size, color);
            }
            @Override public int getIconWidth() { return size; }
            @Override public int getIconHeight() { return size; }
        };
    }
    public static JPanel createWhiteCard(int radius) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Enhanced multi-layer shadow for premium depth
                g2.setColor(new Color(0, 0, 0, 10));
                g2.fill(new RoundRectangle2D.Double(3, 3, getWidth()-6, getHeight()-6, radius, radius));
                g2.setColor(new Color(0, 0, 0, 15));
                g2.fill(new RoundRectangle2D.Double(1, 1, getWidth()-3, getHeight()-3, radius, radius));
                
                g2.setColor(WHITE);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth()-2, getHeight()-2, radius, radius));
                
                g2.setColor(BORDER);
                g2.setStroke(new BasicStroke(1));
                g2.draw(new RoundRectangle2D.Double(0, 0, getWidth()-2, getHeight()-2, radius, radius));
            }
        };
        card.setOpaque(false);
        return card;
    }

    public static JLabel makeLabel(String text, Color color, int size, int style) {
        JLabel l = new JLabel(text);
        l.setForeground(color);
        l.setFont(new Font("Inter", style == Font.BOLD ? Font.BOLD : Font.PLAIN, size));
        if (l.getFont().getName().equals("Dialog")) { // Fallback if Inter not found
            l.setFont(new Font("Segoe UI", style, size));
        }
        return l;
    }

    public static JTextField makeField(String placeholder) {
        JTextField tf = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getText().isEmpty() && !isFocusOwner()) {
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setColor(TEXT_LIGHT);
                    g2.setFont(new Font("Segoe UI", Font.ITALIC, 13));
                    g2.drawString(placeholder, 12, getHeight() / 2 + 5);
                }
            }
        };
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tf.setForeground(TEXT_DARK);
        tf.setBackground(WHITE);
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER, 1),
            BorderFactory.createEmptyBorder(8, 14, 8, 14)
        ));
        tf.setCaretColor(ACCENT);
        return tf;
    }

    public static JPasswordField makePasswordField(String placeholder) {
        JPasswordField pf = new JPasswordField();
        pf.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        pf.setForeground(TEXT_DARK);
        pf.setBackground(WHITE);
        pf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER, 1),
            BorderFactory.createEmptyBorder(8, 14, 8, 14)
        ));
        pf.setCaretColor(ACCENT);
        return pf;
    }

    public static JButton makePrimaryButton(String text, Color bg) {
        JButton b = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (getModel().isPressed()) g2.setColor(bg.darker());
                else if (getModel().isRollover()) g2.setColor(bg.brighter());
                else g2.setColor(bg);
                
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 10, 10));
                
                g2.setColor(WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(getText(), x, y);
            }
        };
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }

    public static JButton makeOutlineButton(String text, Color color) {
        JButton b = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (getModel().isRollover()) {
                    g2.setColor(color);
                    g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 10, 10));
                    g2.setColor(WHITE);
                } else {
                    g2.setColor(WHITE);
                    g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 10, 10));
                    g2.setColor(color);
                    g2.setStroke(new BasicStroke(1.5f));
                    g2.draw(new RoundRectangle2D.Double(1, 1, getWidth()-2, getHeight()-2, 10, 10));
                }
                
                g2.setFont(new Font("Segoe UI", Font.BOLD, 13));
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(getText(), x, y);
            }
        };
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }

    public static JTable makeTable(DefaultTableModel model) {
        JTable t = new JTable(model) {
            @Override
            public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int col) {
                Component c = super.prepareRenderer(renderer, row, col);
                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? WHITE : ROW_ALT);
                    c.setForeground(TEXT_DARK);
                }
                return c;
            }
        };
        t.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        t.setRowHeight(38);
        t.setShowGrid(false);
        t.setIntercellSpacing(new Dimension(0, 0));
        t.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        t.getTableHeader().setBackground(new Color(241, 245, 249));
        t.getTableHeader().setForeground(TEXT_MID);
        t.getTableHeader().setPreferredSize(new Dimension(100, 40));
        t.setSelectionBackground(new Color(0, 102, 255, 40));
        t.setSelectionForeground(TEXT_DARK);
        return t;
    }

    public static JPanel makeStat(String label, String value, Color valueColor, String iconType) {
        JPanel card = createWhiteCard(12);
        card.setLayout(new BorderLayout(15, 0));
        card.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        card.setPreferredSize(new Dimension(240, 95));

        JPanel iconBox = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                drawIcon((Graphics2D)g, iconType, 0, 0, 40, valueColor);
            }
        };
        iconBox.setOpaque(false);
        iconBox.setPreferredSize(new Dimension(40, 40));

        JPanel right = new JPanel(new GridLayout(2, 1, 0, 2));
        right.setOpaque(false);
        JLabel lblL = makeLabel(label.toUpperCase(), TEXT_LIGHT, 11, Font.BOLD);
        JLabel lblV = makeLabel(value, valueColor, 20, Font.BOLD);
        right.add(lblL);
        right.add(lblV);

        card.add(iconBox, BorderLayout.WEST);
        card.add(right, BorderLayout.CENTER);
        return card;
    }

    public static JPanel createProgressBar(double progress, Color color) {
        JPanel bar = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int h = getHeight();
                int w_tot = getWidth();
                int r = h; // Fully rounded ends
                
                // Premium Track - Glass effect / Soft Gradient
                g2.setPaint(new GradientPaint(0, 0, new Color(241, 245, 249), 0, h, new Color(226, 232, 240)));
                g2.fill(new RoundRectangle2D.Double(0, 0, w_tot, h, r, r));
                
                // Track Inner Shadow/Border for visibility
                g2.setColor(new Color(0, 0, 0, 20));
                g2.setStroke(new BasicStroke(1.0f));
                g2.draw(new RoundRectangle2D.Double(0, 0, w_tot - 1, h - 1, r, r));
                
                // Progress Fill - Glowing Gradient
                if (progress > 0) {
                    int w = (int)(w_tot * progress);
                    if (w < r) w = r; // Ensure min width for roundedness
                    
                    // Main Gradient
                    GradientPaint gp = new GradientPaint(0, 0, color, w, 0, color.darker());
                    g2.setPaint(gp);
                    g2.fill(new RoundRectangle2D.Double(0, 0, w, h, r, r));
                    
                    // Top highlight/shine for "glass" feel
                    g2.setPaint(new GradientPaint(0, 0, new Color(255, 255, 255, 80), 0, h/2, new Color(255, 255, 255, 0)));
                    g2.fill(new RoundRectangle2D.Double(2, 2, w - 4, h/2, r, r));
                    
                    // Subtle glow/border around progress
                    g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 100));
                    g2.setStroke(new BasicStroke(1.5f));
                    g2.draw(new RoundRectangle2D.Double(0, 0, w - 1, h - 1, r, r));
                }
            }
        };
        bar.setOpaque(false);
        bar.setPreferredSize(new Dimension(300, 14));
        return bar;
    }
    public static JPanel createBarChart(java.util.Map<String, Double> data) {
        JPanel chart = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int padding = 25;
                int count = data.size();
                int barWidth = (getWidth() - 2*padding) / count - 40;
                int maxHeight = getHeight() - 2*padding - 20;
                
                double maxVal = 0;
                for (double d : data.values()) if(d > maxVal) maxVal = d;
                if(maxVal == 0) maxVal = 1;

                int i = 0;
                for (String key : data.keySet()) {
                    double val = data.get(key);
                    int h = (int)((val / maxVal) * maxHeight);
                    int x = padding + i * (barWidth + 40) + 20;
                    int y = getHeight() - padding - h;
                    
                    // Bar
                    g2.setPaint(new GradientPaint(x, y, ACCENT, x, getHeight()-padding, NAVY_LIGHT));
                    if(key.equals("Spending")) g2.setPaint(new GradientPaint(x, y, RED, x, getHeight()-padding, new Color(153, 27, 27)));
                    if(key.equals("Savings")) g2.setPaint(new GradientPaint(x, y, GREEN, x, getHeight()-padding, new Color(21, 128, 61)));
                    
                    g2.fill(new RoundRectangle2D.Double(x, y, barWidth, h, 6, 6));
                    
                    // Label
                    g2.setColor(TEXT_MID);
                    g2.setFont(new Font("Segoe UI", Font.BOLD, 10));
                    g2.drawString(key.toUpperCase(), x + (barWidth - g2.getFontMetrics().stringWidth(key.toUpperCase()))/2, getHeight() - 10);
                    
                    // Value
                    g2.setColor(TEXT_DARK);
                    String valStr = "₹ " + (int)val;
                    g2.drawString(valStr, x + (barWidth - g2.getFontMetrics().stringWidth(valStr))/2, y - 5);
                    i++;
                }
            }
        };
        chart.setOpaque(false);
        chart.setPreferredSize(new Dimension(400, 200));
        return chart;
    }

    // --- Custom Vector Icon Drawing ---
    public static void drawIcon(Graphics2D g2, String type, int x, int y, int size, Color color) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(color);
        float s = size / 100f; // Scale factor based on 100x100 design
        g2.translate(x, y);
        g2.scale(s, s);

        switch (type.toUpperCase()) {
            case "BANK":
                // Building
                g2.fillRect(10, 80, 80, 10); // Base
                g2.fillRect(20, 30, 60, 10); // Roof top
                int[] px = {15, 50, 85};
                int[] py = {30, 5, 30};
                g2.fillPolygon(px, py, 3); // Triangle roof
                for(int i=0; i<4; i++) g2.fillRect(25 + i*13, 40, 8, 40); // Pillars
                break;
            case "USER":
                // Head and Shoulders
                g2.fillOval(30, 10, 40, 40); // Head
                g2.fillArc(10, 55, 80, 70, 0, 180); // Shoulders
                break;
            case "ADMIN":
                // Lock
                g2.setStroke(new BasicStroke(8, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.draw(new Arc2D.Double(25, 10, 50, 50, 0, 180, Arc2D.OPEN)); // Shackle
                g2.fillRoundRect(20, 45, 60, 45, 10, 10); // Body
                g2.setColor(WHITE);
                g2.fillOval(45, 62, 10, 10); // Keyhole
                break;
            case "CARD":
                // Credit Card
                g2.fillRoundRect(10, 20, 80, 60, 10, 10);
                g2.setColor(WHITE);
                g2.fillRect(10, 35, 80, 15); // Magnetic stripe
                g2.fillRect(20, 65, 20, 8); // Chip placeholder
                break;
            case "RS":
                // Rupee Symbol Text-based but drawn for safety
                g2.setStroke(new BasicStroke(8, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawLine(20, 20, 80, 20); // Top bar
                g2.drawLine(20, 40, 80, 40); // Mid bar
                g2.drawArc(20, 20, 60, 40, 90, -180); // Curve
                g2.drawLine(40, 60, 80, 90); // Slash
                break;
            case "TRANSFER":
                // Two arrows
                g2.setStroke(new BasicStroke(6, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawLine(10, 30, 70, 30); g2.drawLine(70, 30, 55, 15); g2.drawLine(70, 30, 55, 45); // Left to Right
                g2.drawLine(90, 70, 30, 70); g2.drawLine(30, 70, 45, 55); g2.drawLine(30, 70, 45, 85); // Right to Left
                break;
            case "LOGOUT":
                // Door and arrow
                g2.setStroke(new BasicStroke(6));
                g2.drawRect(10, 10, 50, 80);
                g2.drawLine(40, 50, 90, 50); g2.drawLine(90, 50, 75, 35); g2.drawLine(90, 50, 75, 65);
                break;
            case "STAR":
                int[] sx = {50, 61, 98, 68, 79, 50, 21, 32, 2, 39};
                int[] sy = {2, 35, 35, 58, 93, 72, 93, 58, 35, 35};
                g2.fillPolygon(sx, sy, 10);
                break;
            case "ALERT":
                // Triangle with !
                int[] ax = {50, 5, 95};
                int[] ay = {5, 90, 90};
                g2.fillPolygon(ax, ay, 3);
                g2.setColor(WHITE);
                g2.fillRect(46, 30, 8, 35);
                g2.fillOval(46, 72, 8, 8);
                break;
            case "HISTORY":
                g2.setStroke(new BasicStroke(8));
                g2.drawOval(15, 15, 70, 70);
                g2.drawLine(50, 50, 50, 25);
                g2.drawLine(50, 50, 70, 50);
                break;
            case "BILL":
                g2.drawRect(20, 10, 60, 80);
                g2.drawLine(30, 30, 70, 30);
                g2.drawLine(30, 50, 70, 50);
                g2.drawLine(30, 70, 50, 70);
                break;
            case "GRAPH":
                g2.setStroke(new BasicStroke(6));
                g2.drawLine(10, 90, 90, 90); // X axis
                g2.drawLine(10, 10, 10, 90); // Y axis
                g2.drawLine(15, 80, 40, 50);
                g2.drawLine(40, 50, 65, 70);
                g2.drawLine(65, 70, 90, 20);
                break;
            case "BELL":
                int[] bx = {20, 80, 70, 30};
                int[] by = {75, 75, 20, 20};
                g2.fillPolygon(bx, by, 4);
                g2.fillOval(40, 75, 20, 15);
                g2.drawArc(30, 5, 40, 30, 0, 180);
                break;
            case "SHIELD":
                int[] sx2 = {50, 10, 10, 50, 90, 90};
                int[] sy2 = {10, 30, 60, 90, 60, 30};
                g2.fillPolygon(sx2, sy2, 6);
                g2.setColor(WHITE);
                g2.setStroke(new BasicStroke(6));
                g2.drawLine(50, 30, 50, 70);
                g2.drawLine(35, 50, 65, 50);
                break;
        }

        g2.scale(1/s, 1/s);
        g2.translate(-x, -y);
    }
}
