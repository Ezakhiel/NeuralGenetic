package cs.ubb.neural.gui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.SwingWorker;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.me.flappybird.Flappybird;

import cs.ubb.Genetic.GeneticDTO;
import cs.ubb.Genetic.GeneticLearn;
import cs.ubb.neural.NNetwork;

import javax.swing.JLabel;
import java.awt.Font;
import java.awt.Frame;

import javax.swing.JButton;
import javax.swing.JComboBox;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JCheckBox;

public class ConfigUI implements Runnable{

	private JFrame frame;
	private JTextField txtCrossing;
	private JTextField txtSelection;
	private JTextField txtPopCount;
	private JTextField txtMutation;
	private GeneticLearn geneticLearn;
	private GeneticDTO geneticDTO;
	private JLabel lblPleaseConfigure;
	private JButton btnStart;
	
	/**
	 * Create the application.
	 */
	public ConfigUI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 288, 393);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		txtMutation = new JTextField();
		txtMutation.setText("20");
		txtMutation.setBounds(22, 26, 86, 20);
		frame.getContentPane().add(txtMutation);
		txtMutation.setColumns(10);
		
		JLabel lblMutation = new JLabel("Mutation percentage");
		lblMutation.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblMutation.setBounds(128, 29, 135, 14);
		frame.getContentPane().add(lblMutation);
		
		txtCrossing = new JTextField();
		txtCrossing.setText("30");
		txtCrossing.setColumns(10);
		txtCrossing.setBounds(22, 57, 86, 20);
		frame.getContentPane().add(txtCrossing);
		
		JLabel lblCrossing = new JLabel("Crossing percentage");
		lblCrossing.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblCrossing.setBounds(128, 60, 135, 14);
		frame.getContentPane().add(lblCrossing);
		
		txtSelection = new JTextField();
		txtSelection.setText("20");
		txtSelection.setColumns(10);
		txtSelection.setBounds(22, 88, 86, 20);
		frame.getContentPane().add(txtSelection);
		
		JLabel lblSelection = new JLabel("Selection percentage");
		lblSelection.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblSelection.setBounds(128, 91, 135, 14);
		frame.getContentPane().add(lblSelection);
		
		txtPopCount = new JTextField();
		txtPopCount.setText("1");
		txtPopCount.setColumns(10);
		txtPopCount.setBounds(22, 119, 86, 20);
		frame.getContentPane().add(txtPopCount);
		
		JLabel lblPopCount = new JLabel("Population");
		lblPopCount.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblPopCount.setBounds(128, 122, 135, 14);
		frame.getContentPane().add(lblPopCount);
		
		JButton btnGenerate = new JButton("Generate");
		btnGenerate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (isSet()){
					geneticLearn.randomWeights();
					lblPleaseConfigure.setText("Networks set, please start!");
					btnStart.setEnabled(true);
				}
			}
		});
		btnGenerate.setBounds(22, 295, 89, 23);
		frame.getContentPane().add(btnGenerate);
		
		btnStart = new JButton("Start");
		btnStart.setEnabled(false);
		btnStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				geneticLearn.learn();
			}
		});
		btnStart.setBounds(131, 295, 89, 23);
		frame.getContentPane().add(btnStart);
		
		lblPleaseConfigure = new JLabel("Please Configure");
		lblPleaseConfigure.setBounds(22, 203, 241, 31);
		frame.getContentPane().add(lblPleaseConfigure);
		
		JButton btnLoad = new JButton("Load");
		btnLoad.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (isSet()){
					
					geneticLearn.randomWeights();
					geneticLearn.getWeights(0);
					//lblPleaseConfigure.setText("Networks set, please start!");
					//btnStart.setEnabled(true);
				}
			}
		});
		btnLoad.setBounds(22, 260, 89, 23);
		frame.getContentPane().add(btnLoad);
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			ConfigUI window = new ConfigUI();
			window.frame.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private boolean isSet(){
		try{
		double cross = Double.parseDouble(txtCrossing.getText());
		double selection = Double.parseDouble(txtSelection.getText());
		double mutate = Double.parseDouble(txtMutation.getText());
		double randPercentage = Math.abs((cross+selection+mutate)-100.0);
		int pop = Integer.parseInt(txtPopCount.getText());
		if (pop < 0){
			lblPleaseConfigure.setText("Wrong population set!");
			return false;
		}
		if ( randPercentage <= 0.000001){
			geneticDTO = new GeneticDTO(cross,selection, mutate, randPercentage, pop);
			geneticLearn = new GeneticLearn(geneticDTO);
			lblPleaseConfigure.setText("Random Genes very low");
			return true;
		}else if( randPercentage > 0.000001){
			geneticDTO = new GeneticDTO(cross,selection, mutate, randPercentage, pop);
			geneticLearn = new GeneticLearn(geneticDTO);
			return true;
		}
		lblPleaseConfigure.setText("Configuration wrong!");
		return false;
		}catch (NumberFormatException e){
			lblPleaseConfigure.setText("Configuration format wrong!");
			return false;
		}
	}

}
