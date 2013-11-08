/*
** Gravity simulator
** Copyright (C) 2013 Alex Reidy
** (11/03/2013)
*/

import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

public class GravitySim implements MouseListener, KeyListener {
    
    static final int W = 800;
    static final int H = 750;
    
    boolean canDeleteFixedOrb;
    boolean toDeleteOrbiters;
    boolean started;
    
    JFrame frame = new JFrame("Alex Reidy's gravity simulator");
    Painter painter;
    
    ArrayList<Orbiter> orbiters = new ArrayList<Orbiter>();
    ArrayList<Orbiter> orbiterAdditionQueue = new ArrayList<Orbiter>();
    
    public void mouseClicked(MouseEvent  e) {}
    public void mouseExited(MouseEvent   e) {}
    public void mouseEntered(MouseEvent  e) {}
    public void keyReleased(KeyEvent e) {}
    public void keyTyped(KeyEvent e) {}
    
    public static double rin(double range) {
        return Math.round(Math.random() * range);
    }
    
    public static double rsign(double n) {
        return rin(100) > 50 ? -n : n;
    }
    
    private static double circleArea(double radius) {
        return Math.PI * Math.pow(radius, 2);
    }
    
    private void merge(Orbiter bigOrb, Orbiter smallOrb) {
        double area = circleArea(bigOrb.radius) + circleArea(smallOrb.radius);
        bigOrb.radius = Math.sqrt(area / Math.PI);
        smallOrb.radius = 0;
    }
    
    private void randomlyPopulate(int count) {
        for (int i = 0; i < count; i++) {
            orbiterAdditionQueue.add(new Orbiter());
        }
    }
    
    public void keyPressed(KeyEvent e) {
        started = true;
        if (e.getKeyChar() == 'x')
            toDeleteOrbiters = true;
        if (e.getKeyChar() == 'r')
            randomlyPopulate(150);
    }
    
    public void mousePressed(MouseEvent e) {
        started = true;
        Orbiter orb = new Orbiter(e.getX(), e.getY(), 3);
        
        if (e.getButton() == e.BUTTON3) {
            orb.fixed = true;
            canDeleteFixedOrb = false;
            orb.radius = 15;
        }
        
        orbiterAdditionQueue.add(orb);
    }
    
    public void mouseReleased(MouseEvent e) {
        canDeleteFixedOrb = true;
    }
    
    public static void main(String[] args) {
        GravitySim app = new GravitySim();
        app.go();
    }
    
    private void go() {

        frame.setSize(W, H);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setBackground(Color.BLACK);

        frame.add(painter = new Painter());
        painter.addMouseListener(this);

        frame.addKeyListener(this);
        frame.setVisible(true);
        
        while (true) {
            try {
                for (Orbiter orb : orbiterAdditionQueue)
                    orbiters.add(orb);
                orbiterAdditionQueue.clear();
            } catch(ConcurrentModificationException e) {}
            
            for (Orbiter orb : orbiters) {
                double xVelocityMod = 0;
                double yVelocityMod = 0;
                
                if (orb.fixed) {
                    if (!canDeleteFixedOrb) {
                        orb.x = painter.getMousePosition().getX();
                        orb.y = painter.getMousePosition().getY();
                    }
                    continue;
                }
                
                for (Orbiter o : orbiters) {
                    if (o.x == orb.x && o.y == orb.y)
                        continue;
                    
                    double distance = Math.sqrt(
                        Math.pow(orb.x - o.x, 2) + Math.pow(orb.y - o.y, 2)
                    );
                    
                    if (distance < orb.radius + o.radius) {
                        if (orb.radius > o.radius) merge(orb, o);
                        else merge(o, orb);
                        
                        continue;
                    }
                    
                    double forceOfGravity = 7 * o.radius / distance;
                    
                    orb.theta = Math.atan2(o.y - orb.y, o.x - orb.x);
                    
                    xVelocityMod += Math.cos(orb.theta) * forceOfGravity;
                    yVelocityMod += Math.sin(orb.theta) * forceOfGravity;
                    
                }
                
                orb.x += orb.xVelocity += xVelocityMod;
                orb.y += orb.yVelocity += yVelocityMod;
            }
            
            for (int i = 0; i < orbiters.size(); i++) {
                Orbiter orb = orbiters.get(i);
                
                if (toDeleteOrbiters || orb.radius == 0 || (orb.fixed && canDeleteFixedOrb))
                    orbiters.remove(i);
            }
            
            if (orbiters.isEmpty()) toDeleteOrbiters = false;
            
            painter.repaint();
            
            try { Thread.sleep(60); }
            catch (Exception e) {}
        }
        
    }
    
    class Painter extends Component {
        Ellipse2D.Double shape = new Ellipse2D.Double();

        public void paint(Graphics g) {
            Graphics2D g2D = (Graphics2D) g;
            
            g2D.setColor(Color.BLACK);
            g2D.fillRect(0, 0, W, H);
            
            g2D.setColor(Color.GREEN);
            
            if (!started) {
                g2D.drawString("Left-click to spawn an orbiter", 10, 15);
                g2D.drawString("Right-click (and hold) to control a black hole", 10, 35);
                g2D.drawString("Press the R key to randomly spawn orbiters",10, 55);
                g2D.drawString("Press the X key to delete all orbiters", 10, 75);
                g2D.drawString("Click or type to start", 10, 95);
            }
            
            for (Orbiter orb : orbiters) {
                if (orb.fixed) g2D.setColor(Color.DARK_GRAY);
                else g2D.setColor(Color.GREEN);
                
                shape.setFrame(
                    orb.x - orb.radius,
                    orb.y - orb.radius,
                    orb.radius * 2,
                    orb.radius * 2
                );
                
                g2D.fill(shape);
            }
            
        }
    }
    
}
