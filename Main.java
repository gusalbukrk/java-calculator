import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.awt.GraphicsEnvironment;
import java.awt.FontFormatException;
import java.awt.font.TextAttribute;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.FlowLayout;
import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;

import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

interface getRandomValueFn {
  int run();
}

class Frame extends JFrame  implements ActionListener {
  JLabel label;

  Frame() {
    this.setTitle("Calculator");
    this.setResizable(true);
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.getContentPane().setBackground(this.getRandomColor());
    // this.setLayout(new BorderLayout()); // default

    JPanel outer = new JPanel();
    outer.setPreferredSize(new Dimension(325, 370));
    outer.setBorder(new EmptyBorder(10, 10, 10, 10));
    outer.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
    outer.setOpaque(false);
    this.add(outer);
    
    label = new JLabel();
    label.setText("0");
    label.setPreferredSize(new Dimension(305, 70));
    label.setOpaque(true);
    label.setBackground(Color.WHITE);
    label.setForeground(Color.BLACK);
    label.setFont(this.getSegoeFont(32, Font.BOLD));
    label.setHorizontalAlignment(JLabel.RIGHT);
    label.setBorder(new EmptyBorder(0,0,0,10)); //top,left,bottom,right
    outer.add(label);

    // space between result label and buttons' panel
    JPanel space = new JPanel();
    space.setOpaque(false);
    space.setPreferredSize(new Dimension(305, 30));
    outer.add(space);

    JPanel inner = new JPanel();
    inner.setPreferredSize(new Dimension(305, 250));
    inner.setLayout(new GridLayout(5, 4, 3, 3));
    inner.setOpaque(false);
    outer.add(inner);

    String[] keys = {
      "C", "B", "( )", "/", "7", "8", "9", "*", "4", "5", "6", "-", "1", "2", "3", "+", "c", "0", ".", "="
    };

    JButton[] buttons = new JButton[keys.length];

    for (int i = 0; i < keys.length; i++) {
      JButton button = new JButton();

      button.setActionCommand(keys[i]);

      // add icons to backspace, delete and colors buttons and text to everything else
      if (keys[i] == "c") {
        ImageIcon icon = new ImageIcon(getClass().getResource("colors.png"));
        button.setIcon(icon);
      } else if (keys[i] == "B") {
        ImageIcon icon = new ImageIcon(getClass().getResource("backspace.png"));
        button.setIcon(icon);
      } else if (keys[i] == "C") {
        ImageIcon icon = new ImageIcon(getClass().getResource("delete.png"));
        button.setIcon(icon);
      } else {
        button.setText(keys[i]);
      }

      button.setFocusable(false);
      button.setBackground(Color.WHITE);
      button.setForeground(Color.BLACK);
      button.setBorder(null);;
      button.setFont(this.getSegoeFont(18, Font.BOLD));
      button.addActionListener(this);

      buttons[i] = button;
      inner.add(button);
    }

    this.pack();
    this.setVisible(true);
    this.setLocationRelativeTo(null);
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    if (e.getActionCommand().matches("[0-9\\+\\-\\*\\/\\.]")) {

      String t = label.getText();
      label.setText(t == "0" ? e.getActionCommand() : t + e.getActionCommand());

    } else if (e.getActionCommand() == "( )") {

      String t = label.getText();
      int closing = t.lastIndexOf(")"), opening = t.lastIndexOf("(");
      if (opening == -1 || (opening != -1 && closing > opening)) label.setText(t + "(");
      else label.setText(t + ")");

    } else if (e.getActionCommand() == "=") {

      String result = calc(label.getText());
      label.setText(result);

    } else if (e.getActionCommand() == "B") {
      
      if (label.getText().length() == 0) return;

      label.setText(
        label.getText().length() == 1 ? "0" : label.getText().substring(0, label.getText().length() - 1)
      );

    } else if (e.getActionCommand() == "C") {
      label.setText("0");
    } else if (e.getActionCommand() == "c") {
      this.getContentPane().setBackground(this.getRandomColor());
    }
  }

  public Color getRandomColor() {
    getRandomValueFn r = () -> ThreadLocalRandom.current().nextInt(0, 255 + 1);

    return new Color(r.run(), r.run(), r.run());
  }

  public Font getSegoeFont(int size, int weight) {
    Font Segoe = null;

    try {
      Segoe = Font.createFont(Font.TRUETYPE_FONT, new File("segoe.ttf")).deriveFont(weight, (float) size);
    } catch (IOException|FontFormatException e) {
      e.printStackTrace();
    }
    
    return Segoe;
  }

  public String calc(String exp) {
    ScriptEngineManager manager = new ScriptEngineManager();
    ScriptEngine engine = manager.getEngineByName("js");
    
    String result = null;
    
    try {
      result = engine.eval(exp).toString();
    } catch (ScriptException e) {
      e.printStackTrace();
    }

    // needed because JavaScript has numeric precision issues
    if (result.toString().matches("\\d+\\.\\d+")) {
      Float f = Float.parseFloat(result);
      result = f.toString();
    }

    return result;
  }
}

class Main {
  public static void main(String[] args) {
    new  Frame();
  }
}