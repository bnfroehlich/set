package bfroehlich.set;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.Timer;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Main {
	
	private ArrayList<Card> deck;
	private ArrayList<Card> inPlay;
	private ArrayList<Card> discard;

	private JLabel forTaunting;
	private JLabel clock;
	private Timer timer;
	private ArrayList<CardPanel> board;
	private JButton noSet;
	private JButton newGame;

	public static void main(String[] args) {
		new Main();
	}
	
	public Main() {
		createWindow();
	}
	
	public static Image loadImage(String path, int width, int height) {
		if(path == null) {
			return null;
		}
		URL url = Main.class.getResource("/" + path);
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Image image = toolkit.getImage(url);
        if(width > 0 && height > 0) {
        	image = image.getScaledInstance(width, height, Image.SCALE_DEFAULT);
        }
        return image;
	}
	
	public static BufferedImage loadBufferedImage(String path) {
		BufferedImage image = null;
		try {
			image = ImageIO.read(Main.class.getResource("/" + path));
		}
		catch(IOException e) {
			System.err.println(e.getClass());
			System.err.println(e.getMessage());
		}
		return image;
	}
	
	private void createWindow() {
		JFrame frame = new JFrame("Set");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		frame.setLayout(new BorderLayout());
		
		JPanel center = new JPanel();
		center.setBackground(Color.white);
		frame.add(center, BorderLayout.CENTER);
		//center.setPreferredSize(new Dimension(500, 500));
		center.setLayout(new GridLayout(4, 4));
		board = new ArrayList<CardPanel>();
		for(int i = 0; i < 15; i++) {
			CardPanel cardPanel = new CardPanel();
			//cardPanel.setPreferredSize(new Dimension(150, 150));
			cardPanel.addMouseListener(new MouseListener() {
				public void mouseReleased(MouseEvent e) {
					if(cardPanel.getCard() != null && cardPanel.isEnabled()) {
						cardPanel.setSelected(!cardPanel.isSelected());
						cardSelected();
					}
				}
				public void mousePressed(MouseEvent e) {}
				public void mouseExited(MouseEvent e) {}
				public void mouseEntered(MouseEvent e) {}
				public void mouseClicked(MouseEvent e) {}
			});
			board.add(cardPanel);
			center.add(cardPanel);
		}
		
		noSet = new JButton("No set");
		noSet.setEnabled(false);
		center.add(noSet);
		noSet.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(Card.existsSet(inPlay)) {
					forTaunting.setText("Guess again");
				}
				else {
					forTaunting.setText("Good eye");
					overfillBoard();
				}
			}
		});

		JPanel south = new JPanel();
		frame.add(south, BorderLayout.SOUTH);
		newGame = new JButton("New Game");
		south.add(newGame);
		newGame.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				deck.addAll(inPlay);
				inPlay = new ArrayList<Card>();
				deck.addAll(discard);
				discard = new ArrayList<Card>();
				Collections.shuffle(deck);
				
				for(CardPanel cp : board) {
					cp.setCard(null);
					cp.setEnabled(true);
				}
				populateBoard();
				
				newGame.setEnabled(false);
				noSet.setEnabled(true);
				long start = System.currentTimeMillis();
				int delay = 1000; //milliseconds
				ActionListener taskPerformer = new ActionListener() {
					public void actionPerformed(ActionEvent evt) {
						clock.setText("" + (int) ((System.currentTimeMillis()-start)/1000));
					}
				};
				timer = new Timer(delay, taskPerformer);
				timer.start();
				
				frame.pack();
			}
		});
		
		forTaunting = new JLabel("prepare for disappointment");
		south.add(forTaunting);
		clock = new JLabel("0");
		south.add(clock);
		
		frame.pack();
		
		deck = Card.makeDeck();
		inPlay = new ArrayList<Card>();
		discard = new ArrayList<Card>();
	}
	
	private void cardSelected() {
		ArrayList<CardPanel> selected = new ArrayList<CardPanel>();
		for(CardPanel cp : board) {
			if(cp.isSelected()) {
				selected.add(cp);
			}
		}
		if(selected.size() == 3) {
			if(Card.isSet(selected.get(0).getCard(), selected.get(1).getCard(), selected.get(2).getCard())) {
				forTaunting.setText("Correct");
				for(CardPanel cp : selected) {
					inPlay.remove(cp.getCard());
					discard.add(cp.getCard());
					cp.setCard(null);;
				}
				populateBoard();
			}
			else {
				forTaunting.setText("Wrong again");
			}
			for(CardPanel cp : selected) {
				cp.setSelected(false);
			}
		}
	}
	
	private void populateBoard() {
		for(int i = 0; i < 12; i++) {
			CardPanel current = board.get(i);
			if(current.getCard() == null) {
				for(int j = 12; j < 15; j++) {
					//check if there are cards in overflow
					CardPanel overflowPanel = board.get(j);
					if(overflowPanel.getCard() != null) {
						current.setCard(overflowPanel.getCard());
						overflowPanel.setCard(null);
						break;
					}
				}
				if(current.getCard() == null) {
					if(deck.size() == 0) {
						if(!Card.existsSet(inPlay)) {
							gameOver();
						}
					}
					else {
						Card next = deck.remove(0);
						inPlay.add(next);
						current.setCard(next);
					}
				}
			}
		}
		//System.out.println("deck: " + deck.size() + " inplay: " + inPlay.size() + " discard: " + discard.size());
	}
	
	private void overfillBoard() {
		if(deck.isEmpty()) {
			gameOver();
			return;
		}
		for(int j = 12; j < 15; j++) {
			CardPanel overflowPanel = board.get(j);
			if(overflowPanel.getCard() != null) {
				//if already overfilled throw them away
				Card c = overflowPanel.getCard();
				inPlay.remove(c);
				discard.add(c);
				overflowPanel.setCard(null);
			}
			Card next = deck.remove(0);
			inPlay.add(next);
			overflowPanel.setCard(next);
		}
		//System.out.println("deck: " + deck.size() + " inplay: " + inPlay.size() + " discard: " + discard.size());
	}
	
	private void gameOver() {
		forTaunting.setText("game over");
		timer.stop();
		noSet.setEnabled(false);
		newGame.setEnabled(true);
		for(CardPanel cp : board) {
			cp.setEnabled(false);
		}
	}
}