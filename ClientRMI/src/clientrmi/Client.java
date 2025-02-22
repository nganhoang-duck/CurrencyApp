package clientrmi;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import interfacermi.IChuyenDoiTienTe;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class Client extends JFrame {
    private JComboBox<Country> fromComboBox;
    private JComboBox<Country> toComboBox;
    private JTextField amountTextField = new JTextField();
    private JTextField resultTextField = new JTextField();
    private JLabel descriptionLabel = new JLabel();
    private String description = "";
    private List<Country> countryList;

    public Client() {
    	String url = "jdbc:mysql://localhost:3306/currencydb";
        String user = "root";
        String pass = "";
        countryList = new ArrayList<>();

        try {
	        setTitle("Ứng dụng chuyển đổi tiền tệ");
	        JPanel mainPanel = new JPanel(new BorderLayout());
	        JLabel title = new JLabel("CHUYỂN ĐỔI TIỀN TỆ");
	        title.setFont(new Font("Arial", Font.BOLD, 30));
	        title.setHorizontalAlignment(SwingConstants.CENTER);
	        title.setBorder(new EmptyBorder(30, 0, 10, 0));
	        mainPanel.add(title, BorderLayout.NORTH);
	
	        JPanel containerPanel = new JPanel(new GridBagLayout());
	
	        //From
	        GridBagConstraints gbc = new GridBagConstraints();
	        gbc.insets = new Insets(5, 10, 5, 10);
	        gbc.fill = GridBagConstraints.HORIZONTAL;
	        gbc.gridx = 0;
	        gbc.gridy = 0;
	        JLabel fromLabel = new JLabel("Chuyển đổi từ:");
	        fromLabel.setFont(new Font("Arial", Font.BOLD, 13));
	        containerPanel.add(fromLabel, gbc);
	        
	        //To
	        gbc.gridx = 2;
	        gbc.gridy = 0;
	        JLabel toLabel = new JLabel("Chuyển đổi thành:");
	        toLabel.setFont(new Font("Arial", Font.BOLD, 13));
	        containerPanel.add(toLabel, gbc);
	        
	        //Amount of money
	        gbc.gridx = 0;
	        gbc.gridy = 2;
	        amountTextField.getDocument().addDocumentListener(new DocumentListener() {
				@Override
				public void removeUpdate(DocumentEvent e) {
					updateResult();
				}
				@Override
				public void insertUpdate(DocumentEvent e) {
					updateResult();
				}
				@Override
				public void changedUpdate(DocumentEvent e) {
					updateResult();
				}
				private void updateResult() {
			        String amountText = amountTextField.getText();
			        try {
			            double amount = Double.parseDouble(amountText);
			            if (amount >= 0) {
			                updateResultField(amount);
			            }
			        } catch (NumberFormatException ex) {
			            resultTextField.setText("");
			        }
			    }
			});
	        amountTextField.setPreferredSize(new Dimension(100, 25));
	        Insets margin = new Insets(5, 5, 5, 5);
	        amountTextField.setBorder(BorderFactory.createCompoundBorder(
	            BorderFactory.createLineBorder(new Color(0x58769D)),
	            BorderFactory.createEmptyBorder(margin.top, margin.left, margin.bottom, margin.right)
	        ));
	        containerPanel.add(amountTextField, gbc);
	        
	        //Result
	        gbc.gridx = 2;
	        gbc.gridy = 2;
	        resultTextField.setPreferredSize(new Dimension(100, 25));
	        resultTextField.setBorder(BorderFactory.createCompoundBorder(
		            BorderFactory.createLineBorder(new Color(0x58769D)),
		            BorderFactory.createEmptyBorder(margin.top, margin.left, margin.bottom, margin.right)
		    ));
	        resultTextField.setEditable(false);
	        containerPanel.add(resultTextField, gbc);
	        
	        JButton reverseButton = new JButton("Reverse");
	        reverseButton.addActionListener(new ActionListener() {
	            @Override
	            public void actionPerformed(ActionEvent e) {
	                reverseValues();
	            }
	        });
	        gbc.gridx = 1;
	        gbc.gridy = 1;
	        containerPanel.add(reverseButton, gbc);

        	//Kết nối với CSDL
            Connection conn = DriverManager.getConnection(url, user, pass);
            System.out.println("Kết nối thành công!");

            String query = "SELECT Territory, ExchangeRate, Flag, Code  FROM exchangerate";
            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

	        // Dropdown
	        while (rs.next()) {
	            String territory = rs.getString("Territory");
	            double exchangeRate = rs.getDouble("ExchangeRate");
	            String flag = rs.getString("Flag");
	            String code = rs.getString("Code");
	            
	            Country country = new Country(territory, exchangeRate, flag, code);
	            countryList.add(country);
	        }
	        fromComboBox = new JComboBox<>(countryList.toArray(new Country[0]));
	        fromComboBox.setRenderer(new IconComboBoxRenderer());
	        fromComboBox.setSelectedItem(countryList.get(177));
	        // Thêm ActionListener cho JComboBox
	        fromComboBox.addActionListener(new ActionListener() {
	            @Override
	            public void actionPerformed(ActionEvent e) {
	                descriptionLabel.setText(getDescription());
	                amountTextField.setText("");
	                resultTextField.setText("");
	            }
	        });
	        gbc.gridx = 0;
	        gbc.gridy = 1;
	        containerPanel.add(fromComboBox, gbc);

	        toComboBox = new JComboBox<>(countryList.toArray(new Country[0]));
	        toComboBox.setRenderer(new IconComboBoxRenderer());
	        toComboBox.setSelectedItem(countryList.get(181));
	        toComboBox.addActionListener(new ActionListener() {
	            @Override
	            public void actionPerformed(ActionEvent e) {
	                descriptionLabel.setText(getDescription());
	                amountTextField.setText("");
	                resultTextField.setText("");
	            }
	        });
	        gbc.gridx = 2;
	        gbc.gridy = 1;
	        containerPanel.add(toComboBox, gbc);
	        
	        containerPanel.setBorder(new EmptyBorder(0, 0, 10, 0));
	        mainPanel.add(containerPanel, BorderLayout.CENTER);
	        
	        descriptionLabel.setText(getDescription());
	        descriptionLabel.setBorder(new EmptyBorder(0, 25, 30, 0));
	        mainPanel.add(descriptionLabel, BorderLayout.SOUTH);
	        add(mainPanel);
	
	        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	        setSize(800, 300);
	        setLocationRelativeTo(null);
	        setVisible(true);
	        
	        conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } 
    }

    private void reverseValues() {
        Country selectedFromValue = (Country) fromComboBox.getSelectedItem();
        Country selectedToValue = (Country) toComboBox.getSelectedItem();
        fromComboBox.setSelectedItem(selectedToValue);
        amountTextField.setText("");
        resultTextField.setText("");
        toComboBox.setSelectedItem(selectedFromValue);
    }
    
    private String getDescription() {
    	try {
    	    // Gọi phương thức chuyenDoiTienTe
    	    IChuyenDoiTienTe cdtt = (IChuyenDoiTienTe) Naming.lookup("rmi://localhost/chuyenDoiTienTe");
    	    
    	    Country fromCountry = (Country) fromComboBox.getSelectedItem();
    	    Country toCountry = (Country) toComboBox.getSelectedItem();
    	    double exchangeRate1 = fromCountry.getExchangeRate();
    	    double exchangeRate2 = toCountry.getExchangeRate();
    	    
    	    DecimalFormat df = new DecimalFormat("#.##");
    	    description = "1 " + fromCountry.getCode() + " = " + String.valueOf(df.format(cdtt.chuyenDoiTienTe(exchangeRate1, exchangeRate2, 1))) + " " + toCountry.getCode();
    	    
    	} catch (RemoteException e) {
    	    resultTextField.setText("");
        } catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return description;
    }
    
    private void updateResultField(double amount) {
    	try {
    	    // Gọi phương thức chuyenDoiTienTe
    	    IChuyenDoiTienTe cdtt = (IChuyenDoiTienTe) Naming.lookup("rmi://localhost/chuyenDoiTienTe");
    	    
    	    Country fromCountry = (Country) fromComboBox.getSelectedItem();
    	    Country toCountry = (Country) toComboBox.getSelectedItem();
    	    double exchangeRate1 = fromCountry.getExchangeRate();
    	    double exchangeRate2 = toCountry.getExchangeRate();
    	    
    	    double result = cdtt.chuyenDoiTienTe(exchangeRate1, exchangeRate2, amount);
    	    DecimalFormat df = new DecimalFormat("#.##");
    	    resultTextField.setText(String.valueOf(df.format(result)));
    	    
    	} catch (RemoteException e) {
    	    resultTextField.setText("");
        } catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Client();
            }
        });
    }
}

