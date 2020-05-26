package bfroehlich.set;

import java.awt.Color;
import java.awt.Font;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

public class CardPanel extends JPanel {

	private JLabel label;
	private Card card;
	private boolean selected;
	
	public CardPanel() {
		BoxLayout box = new BoxLayout(this, BoxLayout.Y_AXIS);
		setLayout(box);
		label = new JLabel();
		add(label);
		setSelected(false);
	}

	public Card getCard() {
		return card;
	}

	public void setCard(Card card) {
		this.card = card;
		//label.setIcon(Main.loadImage("Images\\cookie.jpg", 100, 100));
		if(card == null) {
			label.setText(null);
			label.setIcon(null);
		}
		else {
			label.setIcon(card.getImage());
//			label.setText(card.toString());
//			label.setForeground(card.getColor());
//			Font f = new Font("Times New Roman", Font.PLAIN, 60);
//			if(card.getShade() == Card.Shade.Solid) {
//				f = new Font("Times New Roman", Font.BOLD, 60);
//			}
//			else if (card.getShade() == Card.Shade.Striped) {
//				f = new Font("Times New Roman", Font.ITALIC, 60);
//			}
//			label.setFont(f);
		}
	}
	
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		label.setEnabled(enabled);
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
		if(selected) {
			label.setBorder(new LineBorder(Color.RED, 3, true));
		}
		else {
			label.setBorder(new LineBorder(Color.WHITE, 3, true));
		}
	}
}