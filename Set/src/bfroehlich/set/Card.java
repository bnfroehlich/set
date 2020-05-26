package bfroehlich.set;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;

public class Card {

	public enum Shape {
		V, O, S
	}
	
	public enum Shade {
		Empty, Striped, Solid
	}
	
	public enum MyColor {
		Red, Green, Purple
	}
	
	private int number;
	private MyColor myColor;
	private Shape shape;
	private Shade shade;
	private Icon image;
	
	public Card(int number, MyColor color, Shape shape, Shade shade, Icon image) {
		super();
		this.number = number;
		this.myColor = color;
		this.shape = shape;
		this.shade = shade;
		this.image = image;
	}
	
	public static ArrayList<Card> makeDeck() {
		BufferedImage masterImage = Main.loadBufferedImage("cards.png");
		
		ArrayList<Card> deck = new ArrayList<Card>();
		for(MyColor color : Card.MyColor.values()) {
			for(Shade shade : Card.Shade.values()) {
				for(Shape shape : Card.Shape.values()) {
					for(int i = 1; i < 4; i++) {
						int row = (shade.ordinal()+3*color.ordinal());
						int y = (int) (57.25*row);
						if(row == 7) {
							y += 1; //shameless hardcoding
						}
						Point p = new Point(27 + (int) (102.625*(i-1+3*shape.ordinal())), 12 + y); //location of this card image on the master image
						Dimension d = new Dimension(98, 56);
						BufferedImage img = masterImage.getSubimage(p.x, p.y, d.width, d.height); //fill in the corners of the desired crop location here
						BufferedImage subImage = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
						Graphics g = subImage.createGraphics();
						g.drawImage(img, 0, 0, null);
						Image scaled = subImage.getScaledInstance(196, 112, java.awt.Image.SCALE_SMOOTH);
						deck.add(new Card(i, color, shape, shade, new ImageIcon(scaled)));
					}
				}
			}
		}
		return deck;
	}
	
	public int getNumber() {
		return number;
	}
	
	public MyColor getMyColor() {
		return myColor;
	}
	
	public Color getColor() {
		if(myColor == MyColor.Green) {
			return Color.GREEN;
		}
		else if (myColor == MyColor.Purple) {
			return Color.BLUE;
		}
		else if(myColor == MyColor.Red) {
			return Color.RED;
		}
		return null;
	}
	
	public Shape getShape() {
		return shape;
	}
	
	public Shade getShade() {
		return shade;
	}
	
	public Icon getImage() {
		return image;
	}
	
	public String toString() {
		String s = "";
		for(int i = 0; i < number; i++) {
			s += shape;
		}
		return s;
	}
	
	public boolean equals(Card other) {
		return number == other.getNumber() && myColor == other.getMyColor() && shape == other.getShape() && shade == other.getShade();
	}
	
	public int hashCode() {
		return number + 10*myColor.ordinal() + 100*shape.ordinal() + 1000*shade.ordinal();
	}
	
	public static boolean isSet(Card card1, Card card2, Card card3) {
		return (card1.getNumber() + card2.getNumber() + card3.getNumber()) % 3 == 0
			&& (card1.getShape().ordinal() + card2.getShape().ordinal() + card3.getShape().ordinal()) % 3 == 0
			&& (card1.getShade().ordinal() + card2.getShade().ordinal() + card3.getShade().ordinal()) % 3 == 0
			&& (card1.getMyColor().ordinal() + card2.getMyColor().ordinal() + card3.getMyColor().ordinal()) % 3 == 0;
	}
	
	public static boolean existsSet(ArrayList<Card> field) {
		for(Card card1 : field) {
			for(Card card2 : field) {
				for(Card card3 : field) {
					if(!card1.equals(card2) && !card1.equals(card3) && !card2.equals(card3)) {
						if(isSet(card1, card2, card3)) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}
}