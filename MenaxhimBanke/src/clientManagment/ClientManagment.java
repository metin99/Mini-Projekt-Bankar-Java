package clientManagment;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.MaskFormatter;

public class ClientManagment extends JFrame{
	
	private static final long serialVersionUID = 1L;
	
	private JTextField searchByIdField;
	private JTable infoTable;
	
	// Create various masks for various restriction purposes
	private MaskFormatter idMask = new MaskFormatter("L########L");
	private MaskFormatter numberMask = new MaskFormatter("0049 ###########");
	private MaskFormatter interestMask = new MaskFormatter("##.##");
	private MaskFormatter yearMask = new MaskFormatter("##");
	
	private JFormattedTextField idTextField;
	private JTextField lastNameTextField;
	private JTextField firstNameTextField;
	private JFormattedTextField phoneTextField;
	private JTextField balanceTextField;
	private JFormattedTextField idTextField2;
	private JTextField sumField;
	private JTextField newBalanceTextField;
	private JFormattedTextField idTextField3;
	private JFormattedTextField idTextField4;
	private JTextField amountTextField;
	private JFormattedTextField interestTextField;
	private JFormattedTextField yearTextField;
	private JFormattedTextField no_paymentsTextField;
	private JTextArea creditInfoArea;
	private JTextField loanPaymentTextField;
	private JTable loanInfoTable;
	private JFormattedTextField idTextField5;
	private JFormattedTextField otherLoanPaymentTextField;
 
	
	// Create the table model as an instance variable since we are going to use it accross various methods
	private DefaultTableModel loan_dtm = new DefaultTableModel(new Object[][]{}, new String[]{"ID", "Kapitali", "Pagesa Vjetore", 
			"Shuma e pageses", "Shuma e ngelur", "Pagesa te vonuara"}){
		private static final long serialVersionUID = 2L;
		@Override
		public boolean isCellEditable (int row, int column){
			// all cells false
			return false;
		}
	};
	
	private // create the history table model
	DefaultTableModel historyTableModel = new DefaultTableModel(new Object[][]{}, new String[]{"ID", "Emri", "Mbiemri", "Veprimi", "Shuma", "Data"}){
		private static final long serialVersionUID = 3L;
		@Override
		public boolean isCellEditable (int row, int column){
			// all cells false
			return false;
		}
	};
	
	private JFormattedTextField idTextField6;
	private JFormattedTextField idTextField7;
	private JTable historyTable;

	private JTextField searchByIdField2;
	
	
	
	public ClientManagment() throws Exception{
		
		getContentPane().setLayout(null);
		JTabbedPane window = new JTabbedPane(JTabbedPane.TOP);
		window.setBackground(new Color(255,129,50));
		window.setBounds(0, 32, 650, 365);
		getContentPane().add(window);
		
		window.addTab("Informacione mbi klientet", null, createCustomerInfoPanel(), null);
		window.addTab("Regjistrim", null, createRegisterPanel(), null);
		window.addTab("Veprime", null, createActionPanel(), null);
		window.addTab("Marrje kredish", null, createLoanRegisterPanel(), null);
		window.addTab("Informacione mbi kredine", null, createLoanInfoPanel(), null);
		window.addTab("Veprime te meparshme", null, createHistoryPanel(), null);
		
		JMenuBar menuBar = new JMenuBar();
		menuBar.setBounds(0, 0, 650, 21);
		getContentPane().add(menuBar);
		menuBar.add(createFileMenu());
		menuBar.add(createHelpMenu());
	}
	
	
		private Component createHistoryPanel() {
			JPanel historyPanel = new JPanel();
			historyPanel.setLayout(null);
			historyPanel.setBackground(Color.LIGHT_GRAY);
			
			// create the JTable that will contain the history information
			historyTable = new JTable();
			historyTable.setModel(historyTableModel);
			
			JScrollPane tableScrollPane = new JScrollPane(historyTable);
			tableScrollPane.setBounds(10, 42, 604, 195);
			historyPanel.add(tableScrollPane);
			
			// Populate the table
			try{
				ArrayList<ActionHistory> history = ActionHistory.getAllHistory();
				for (ActionHistory temp : history)
				{
					historyTableModel.addRow(new Object[]{
							temp.getClientId(), temp.getClientName(), temp.getClientSurname(), temp.getActionPerformed(), temp.getSum(), temp.getDate()
					});
				}
			}catch(SQLException ex){
				JOptionPane.showMessageDialog(null, "Error executing query: " + ex);
			}
			
			// Create the search Field
			searchByIdField2 = new JFormattedTextField(idMask);
			searchByIdField2.setBounds(485, 11, 129, 20);
			historyPanel.add(searchByIdField2);
			searchByIdField2.setColumns(20);
			
			// Create the search label
			JLabel searchLabel = new JLabel("Kerko: ");
			searchLabel.setBounds(440, 11, 80, 20);
			historyPanel.add(searchLabel);
			
			searchByIdField2.addActionListener(new ActionListener(){
				public void actionPerformed (ActionEvent event){
					try{
						String cId = searchByIdField2.getText();
						if (!Client.clientExists(cId))
						{
							// Check if the client has ever existed and print an appropriate message.
							// If the client has ever existed we can find him in the ActionHistory 
							// table of the database
							ArrayList<ActionHistory> clientHistory = ActionHistory.getClientHistory(searchByIdField2.getText());
							
							// Flag that will check if the client has existed before but was deleted
							boolean found = false;	
							for (ActionHistory temp : clientHistory){
								if (temp.getActionPerformed().equals("Konto löschen")){
									JOptionPane.showMessageDialog(null, "Ky klient nuk ekziston me. Ai u fshi ne "+temp.getDate());
									found = true;
								
									ArrayList<ActionHistory> clientHistory2 = ActionHistory.getClientHistory(cId);
									historyTableModel.setRowCount(0);
									for (ActionHistory temp2 : clientHistory2){
										historyTableModel.addRow(new Object[]{
											temp2.getClientId(), temp2.getClientName(), temp2.getClientSurname(), temp2.getActionPerformed(), temp2.getSum(), 
											temp2.getDate()
										});
									}
									
								}
								
							}
							if (!found)
								JOptionPane.showMessageDialog(null, "Ky klient nuk ekziston.");					
						}
						else if (!ActionHistory.existsId(cId)){
							JOptionPane.showMessageDialog(null, "Nuk ka asnje histori veprimesh per kete klient.");
						}
						else{
						
							ArrayList<ActionHistory> clientHistory = ActionHistory.getClientHistory(cId);
							historyTableModel.setRowCount(0);
							for (ActionHistory temp : clientHistory){
								historyTableModel.addRow(new Object[]{
									temp.getClientId(), temp.getClientName(), temp.getClientSurname(), temp.getActionPerformed(), temp.getSum(), temp.getDate()
								});
							}
						}
					}catch(SQLException ex){
						JOptionPane.showMessageDialog(null, "Error executing query: " + ex);
					}
				}
			});
			
			
			// Get all the action History of the bank. In this way after a search the employee
			// can get all the history again
			JButton allHistoryButton = new JButton("Historiku veprimeve");
			allHistoryButton.setForeground(Color.blue);
			allHistoryButton.setBounds(195, 250, 260, 23);
			historyPanel.add(allHistoryButton);
			
			allHistoryButton.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent event){	
					try{
						historyTableModel.setRowCount(0);
						ArrayList<ActionHistory> history = ActionHistory.getAllHistory();
						for (ActionHistory temp : history)
						{
							historyTableModel.addRow(new Object[]{
									temp.getClientId(), temp.getClientName(), temp.getClientSurname(), temp.getActionPerformed(), temp.getSum(), temp.getDate()
							});
						}
					}catch(SQLException ex){
						JOptionPane.showMessageDialog(null, "Error executing query: " + ex);
					}
					
				}
			});
			
			return historyPanel;
		}

		
		private Component createLoanInfoPanel() {
		JPanel loanInfoPanel = new JPanel();
		loanInfoPanel.setLayout(null);
		loanInfoPanel.setBackground(Color.LIGHT_GRAY);
		
		// create the JTable that will contain the loan information
		
		loanInfoTable = new JTable();
		loanInfoTable.setModel(loan_dtm);
	
		getLoanInfo();
		
		JScrollPane tableScrollPane = new JScrollPane(loanInfoTable);
		tableScrollPane.setBounds(0, 142, 640, 145);
		loanInfoPanel.add(tableScrollPane);
		
		// Create search Label
		JLabel searchLoanLabel = new JLabel("Kerko(id): ");
		searchLoanLabel.setBounds(3, 15, 80, 20);
		loanInfoPanel.add(searchLoanLabel);
		
		// Create text searchField
		idTextField5 = new JFormattedTextField(idMask);
		idTextField5.setColumns(20);
		idTextField5.setBounds(60, 16, 90, 20);
		loanInfoPanel.add(idTextField5);
			
		// Create text searchField yielding fuller information on loans
		idTextField6 = new JFormattedTextField(idMask);
		idTextField6.setColumns(20);
		idTextField6.setBounds(520, 16, 90, 20);
		loanInfoPanel.add(idTextField6);
		
		// Create search label
		JLabel fullerSearchLabel = new JLabel("Detaje mbi kredi: ");
		fullerSearchLabel.setBounds(375, 15, 150, 20);
		loanInfoPanel.add(fullerSearchLabel);
		
		// Create text search field to check for former 
		// loans that the client might have
		idTextField7 = new JFormattedTextField(idMask);
		idTextField7.setColumns(20);
		idTextField7.setBounds(120, 60, 90, 20);
		loanInfoPanel.add(idTextField7);
		
		// Create search label
		JLabel formerLoansSearchLabel = new JLabel("Kredi te kaluara: ");
		formerLoansSearchLabel.setBounds(3, 60, 120, 20);
		loanInfoPanel.add(formerLoansSearchLabel);
		
		// get Information for all the former loans that the client might have had
		idTextField7.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent event){
				String cId = idTextField7.getText();
				// Check if the client exists
				try{
					if (!Client.clientExists(cId))
					{
						// Check if the client has ever existed and print an appropriate message.
						// If the client has ever existed we can find him in the ActionHistory 
						// table of the database
						ArrayList<ActionHistory> clientHistory = ActionHistory.getClientHistory(idTextField5.getText());
						
						// Flag that will check if the client has indeed existed before
						boolean found = false;
						for (ActionHistory temp : clientHistory){
							if (temp.getActionPerformed().equals("Konto löschen")){
								JOptionPane.showMessageDialog(null, "Ky klient nuk ekziston me. Ai u fshi me "+temp.getDate());
								found = true;
								
								// Check if the client has any old loan (former, paid loans)
								ArrayList<OldLoan> oldLoans = OldLoan.getFormerLoans(cId);
								if (oldLoans == null)
									JOptionPane.showMessageDialog(null, "Ky klient nuk ka kredi te meparshme.");
								else{
									// 	get the client from the ActionHistory
									ActionHistory client = ActionHistory.getActionHistory(cId);
									JOptionPane.showMessageDialog(null, "Informacionet e plota mbi te gjitha kredite e meparshme:\n"
											+ "Klienti " + client.getClientName() + " " + client.getClientSurname() + " me ID-ne \n" + client.getClientId()
											+ " ka marre kredite e meposhtme: \n" );
									
									for (OldLoan l : oldLoans){

										JOptionPane.showMessageDialog(null, "Kapitali: " + l.getPrincipalAmount() 
												+ "\nPagesa te vonuara: " + l.getIrregularPayments()
												+ "\nData e fillimit "+l.getStartDate()+"\nData e mbarimit: "+l.getEndDate());
									}
								}
							}
						}
						if (!found)
							JOptionPane.showMessageDialog(null, "Ky klient nuk ekziston.");
	
					}
					else{
						
						// Check if the client has any old loan (former, paid loans)
						ArrayList<OldLoan> oldLoans = OldLoan.getFormerLoans(cId);
						if (oldLoans == null)
							JOptionPane.showMessageDialog(null, "Ky klient nuk ka asnje kredi te meparshme");
						else{
							// 	get the client
							Client client = Client.getClient(cId);
							JOptionPane.showMessageDialog(null, "Informacione te plota mbi te gjitha kredite:\n"
									+ "Klienti " + client.getName() + " " + client.getSurname() + " me ID \n" + client.getId()
									+ " ka marre kredite e meposhtme: \n" );
							
							for (OldLoan l : oldLoans){

								JOptionPane.showMessageDialog(null, "Kapitali: " + l.getPrincipalAmount() 
										+ "\nPagesa te vonuara: " + l.getIrregularPayments()
										+ "\nData e fillimit: "+l.getStartDate()+"\nData e mbarimit: "+l.getEndDate());
							}
						}
					}
					
				}catch(SQLException ex){
					JOptionPane.showMessageDialog(null, "Error executing query." + ex);
				}
			}
		});
		
		// get the full information for all the loans that the client currently has
		idTextField6.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent event){
				String cId = idTextField6.getText();
				// Check if the client exists
				try{
					if (!Client.clientExists(cId))
					{
						// Check if the client has ever existed and print an appropriate message.
						// If the client has ever existed we can find him in the ActionHistory 
						// table of the database
						ArrayList<ActionHistory> clientHistory = ActionHistory.getClientHistory(idTextField5.getText());
						
						// Flag that will check if the client has indeed existed before
						boolean found = false;
						for (ActionHistory temp : clientHistory){
							if (temp.getActionPerformed().equals("Konto löschen")){
								JOptionPane.showMessageDialog(null, "Ky klient nuk ekziston me. Ai u fshi me "+temp.getDate());
								found = true;
							}
						}
						if (!found)
							JOptionPane.showMessageDialog(null, "Ky klient nuk ekziston me");
	
					}
					else{
						// get the client
						Client client = Client.getClient(cId);
						
						// check if the client has loans
						if (!Loan.hasLoan(cId))
							JOptionPane.showMessageDialog(null, "Ky klient nuk ka asnje kredi.");
						else{
							ArrayList<Loan> loans = Loan.getLoans(idTextField6.getText());

							JOptionPane.showMessageDialog(null, "Pasqyra e plote e te gjitha kredive:\n"
									+ "Klienti " + client.getName() + " " + client.getSurname() + " me ID \n" + client.getId()
									+ " ka marre kredite e meposhtme\n" );
							
							for (Loan l:loans){
								JOptionPane.showMessageDialog(null, "Kapitali: " + l.getPrincipalAmount() + "€\n"
										+ "Pagesa vjetore: "+l.getNoPaymentsYearly()+ "\nKohezgjatja: "+l.getNoYears() + " vite"
										+ "\nShuma e cdo pagese: "+l.getAmountOfFixedPayments() + "\nShuma e mbetur: "+l.getRemainingPayment(false)
										+ "\nPagesa te vonuara(total): "+l.getNumberOfIrregularPayments() + "\nInteresi: "+l.getInterest() + "%"
										+ "\nData e fillimit: "+l.getDate()+"\nPagesa te bera: "+l.getCurrentNumberOfPaymentsOtherLoans());
							}
						}
					}
				}catch(SQLException exe){
					JOptionPane.showMessageDialog(null, "Error executing query: " + exe);
				}
			}
		});
		
		// get all the current active loans that the client has
		idTextField5.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent event){
				String c_id = idTextField5.getText();
				// Check if the client exists
				try{
					if (!Client.clientExists(c_id))
						{
							// Check if the client has ever existed and print an appropriate message.
							// If the client has ever existed we can find him in the ActionHistory 
							// table of the database
							ArrayList<ActionHistory> clientHistory = ActionHistory.getClientHistory(idTextField5.getText());
							
							// Flag that will check if the client has indeed existed before
							boolean found = false;
							for (ActionHistory temp : clientHistory){
								if (temp.getActionPerformed().equals("Konto löschen")){
									JOptionPane.showMessageDialog(null, "Dieser Kunde existiert nicht mehr. Er war nämlich am "+temp.getDate()+" gelöscht.");
									found = true;
								}
							}
							if (!found)
								JOptionPane.showMessageDialog(null, "Dieser Kunde existiert nicht.");
		
						}
					else{
						// check if the client has loans
						if (!Loan.hasLoan(c_id))
							JOptionPane.showMessageDialog(null, "Dieser Kunde hat keine Darlehn.");
						else{
							loan_dtm.setRowCount(0);
							ArrayList<Loan> loans = Loan.getLoans(idTextField5.getText());
							for (Loan l:loans){
								loan_dtm.addRow(new Object[]{
										l.getClientId(), l.getPrincipalAmount(), l.getNoPaymentsYearly(), l.getAmountOfFixedPayments(), 
										l.getRemainingPayment(false), l.getNumberOfIrregularPayments()
								});
							}
						}
						
					}
				}
				catch(SQLException ex){
					JOptionPane.showMessageDialog(null, "Error executing query: " + ex);
				}
			}
		});
		
		// Get all the loans
		JButton getLoanInfoButton = new JButton("Te gjitha kredite");
		getLoanInfoButton.setForeground(Color.blue);
		getLoanInfoButton.setBounds(150, 115, 140, 23);
		loanInfoPanel.add(getLoanInfoButton);
		
		getLoanInfoButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent event){
				getLoanInfo();
			}
		});
		
		// Reset all field
		JButton resetButton = new JButton ("Fshi");
		resetButton.setForeground(Color.red);
		resetButton.setBounds(350, 115, 140, 23);
		loanInfoPanel.add(resetButton);
		
		resetButton.addActionListener(new ActionListener(){
			public void actionPerformed (ActionEvent event){
				idTextField5.setValue(null);
				idTextField6.setValue(null);
				idTextField7.setValue(null);
			}
		});
		
		return loanInfoPanel;
	}

		
	private  void getLoanInfo(){
		try{
			ArrayList<Loan> loans = Loan.getAllLoans();
			loan_dtm.setRowCount(0);
			for (Loan l : loans)
				loan_dtm.addRow(new Object[]{
					l.getClientId(), l.getPrincipalAmount(), l.getNoPaymentsYearly(), l.getAmountOfFixedPayments(), 
					l.getRemainingPayment(false), l.getNumberOfIrregularPayments()
				});
				
		}catch(SQLException exe){
			JOptionPane.showMessageDialog(null, "Error executing query: " + exe);
		}
	}

		
	/**
	 * Save the loan in the db and thereafter save it in the loan table
	 * @param c_id the loan id
	 * @param amount the principal amount
	 * @param interest the loan interest
	 * @param years the number of years in which the loan will be paid
	 * @param no_payments the no. payments that will be made yearly
	 */
	private void createNewLoan(String c_id, double amount, double interest, int years, int no_payments) throws SQLException{
		Loan loan = new Loan (c_id, amount, interest, years, no_payments);
		loan.saveLoan(loan);
		creditInfoArea.setText(loan.getLoanInfo());
	
		// Update the value of the client's account in the client information tab
		for (int i = 0; i < infoTable.getRowCount(); i ++){
			if (idTextField4.getText().equals(infoTable.getValueAt(i, 0))){
				double deposit_value = Double.parseDouble((String)infoTable.getValueAt(i, 4));
				deposit_value += amount;
				infoTable.setValueAt(deposit_value+"", i, 4);
			}
		}
	

		// Add the loan to the loan table
		loan_dtm.addRow(new Object[]{
			loan.getClientId(), loan.getPrincipalAmount(), loan.getNoPaymentsYearly(), loan.getAmountOfFixedPayments(),
			loan.getRemainingPayment(false), "0"
		});

	}
	
	
	private boolean checkIrregularPayments (String c_id) throws SQLException{

		// Check for the 3rd condition
		// If the client has paid more than 10 times irregularly in his loans he cannot 
		// take another loan as he is considered unreliable
		
		ArrayList<OldLoan> oldLoans = OldLoan.getFormerLoans(c_id);
		ArrayList<Loan> loans = Loan.getLoans(c_id);
		
		int delayedPaments = 0;
		if (oldLoans != null)
			for (OldLoan l : oldLoans){
				delayedPaments += l.getIrregularPayments();
			}
		
		for (Loan l : loans){
			delayedPaments += l.getNumberOfIrregularPayments();
		}
		
		if (delayedPaments > 10)
			return false;
		return true;
	}
	
	private Component createLoanRegisterPanel() {
		JPanel loanRegisterPanel = new JPanel();
		loanRegisterPanel.setLayout(null);
		loanRegisterPanel.setBackground(Color.LIGHT_GRAY);
		
		// Create the id label
		JLabel idLabel = new JLabel ("ID: ");
		idLabel.setForeground(new Color(51, 163, 78));
		idLabel.setBounds(15, 20, 90, 14);
		loanRegisterPanel.add(idLabel);
		
		// Create the id textField with a maskformatter
		idTextField4 = new JFormattedTextField(idMask);
		idTextField4.setBounds(125, 17, 90, 24);
		idTextField4.setColumns(15);
		loanRegisterPanel.add(idTextField4);
		
		// Create the amount label
		JLabel amountLabel = new JLabel ("Shuma: ");
		amountLabel.setBounds(350 , 20, 90, 14);
		amountLabel.setForeground(new Color(51, 163, 78));
		loanRegisterPanel.add(amountLabel);
		
		// Create amount textField
		amountTextField = new JTextField(15);
		amountTextField.setBounds(420, 17, 90, 24);
		loanRegisterPanel.add(amountTextField);
		
		// Create interest Label
		JLabel interestLabel = new JLabel("Interesi: ");
		interestLabel.setForeground(new Color (51, 163, 78));
		interestLabel.setBounds(15, 65, 90, 14);
		loanRegisterPanel.add(interestLabel);
		
		// Create interest TextField
		interestTextField = new JFormattedTextField(interestMask);
		interestTextField.setBounds(125, 62, 90, 24);
		interestTextField.setColumns(15);
		loanRegisterPanel.add(interestTextField);
		
		// Create year Label
		JLabel yearLabel = new JLabel("Koha: ");
		yearLabel.setForeground(new Color(51, 163, 78));
		yearLabel.setBounds(350, 65, 90, 14);
		loanRegisterPanel.add(yearLabel);
		
		// Create year TextField
		yearTextField = new JFormattedTextField(yearMask);
		yearTextField.setColumns(15);
		yearTextField.setBounds(420, 62, 90, 24);
		loanRegisterPanel.add(yearTextField);
		
		// Create number of payments yearly label
		JLabel no_paymentsLabel1 = new JLabel("Numri i pagesave");
		no_paymentsLabel1.setForeground(new Color(51, 163, 78));
		no_paymentsLabel1.setBounds(15, 110, 120, 14);
		loanRegisterPanel.add(no_paymentsLabel1);
		
		JLabel no_paymentsLabel2 = new JLabel ("vjetore:");
		no_paymentsLabel2.setForeground(new Color(51, 163, 78));
		no_paymentsLabel2.setBounds(15, 125, 120, 14);
		loanRegisterPanel.add(no_paymentsLabel2);
		
		// Create number of payments yearly textField
		no_paymentsTextField = new JFormattedTextField(yearMask);
		no_paymentsTextField.setColumns(15);
		no_paymentsTextField.setBounds(138, 115, 90, 24);
		loanRegisterPanel.add(no_paymentsTextField);
		
		// Create register Button
		JButton registerButton = new JButton ("Merr kredi");
		registerButton.setForeground(Color.blue);
		registerButton.setBounds(290, 115, 140, 23);
		loanRegisterPanel.add(registerButton);
		
		final int ROWS = 20;
		final int COLUMNS = 35;
		creditInfoArea = new JTextArea(ROWS, COLUMNS);
		creditInfoArea.setEditable(false);
		creditInfoArea.setBounds(105, 175, 430, 120);
		creditInfoArea.setBackground(new Color(149, 149, 149));
		loanRegisterPanel.add(creditInfoArea);
		
		registerButton.addActionListener(new ActionListener(){
			public void actionPerformed (ActionEvent event){
				
				// Check that the user has filled in all the required values
				if (idTextField4.getText().equals("") || amountTextField.getText().equals("") || interestTextField.getText().equals("") 
						|| yearTextField.getText().equals("") || no_paymentsTextField.getText().equals("")){
					JOptionPane.showMessageDialog(null, "Ju lutem plotesoni informacionet qe mungojne!");
				}else{
					try{
						// Check if the client exists
						if (Client.clientExists(idTextField4.getText())){
							/*
							 * For the loan to be approved, certain conditions must be meet (thereby an attempt to emulate 
							 * real life scenarios has been made): 
							 * 1) Maximum years to pay off the loan has been set to 10 years, whilst the minimum to 1 year
							 * 2) The maximum amount of the loan has been set to 100k, whilst the minimum to 3k
							 * 3) The interest varies between 2-20%
							 * 4) The number of payments yearly should be 1, 4, 2 or 12
							 *  
							 * Provided the above-mentioned conditions are met a client would have to meet the following as well:
							 * 
							 * 1) He cannot take a loan if the last taken loan was taken less than 6 months ago
							 * 2) If he already has take one or more loans they should not be more than 100k when summed together
							 * 3) If the client has paid more than 10 times irregularly in previous loans that he might have taken
							 * 	  or that he already has
							 */
							
							String c_id = idTextField4.getText();
							double amount = Double.parseDouble(amountTextField.getText());
							double interest = Double.parseDouble(interestTextField.getText());
							int years = Integer.parseInt(yearTextField.getText());
							int no_payments = Integer.parseInt(no_paymentsTextField.getText());
							
							// Check the basic requirements
							if (years < 1 || years > 10){
								JOptionPane.showMessageDialog(null, "Kredia duhet te paguhet ne nje afat kohor nga 1-10 vite.");
							}else if ( amount < 3000 || amount > 100000)
							{
								JOptionPane.showMessageDialog(null, "Shuma e kredise se marre duhet te jete midis 3000 dhe 100 000.");
							}else if (interest < 2 || interest > 20){
								JOptionPane.showMessageDialog(null, "Interesi duhet te jete midis 2 dhe 20%.");
							}else if (no_payments != 1 && no_payments != 4 && no_payments != 2 && no_payments != 12){
								JOptionPane.showMessageDialog(null, "Pagesat mund te jene vetem mujore, 4-mujore, 6-mujore apo nje ne vit.");
							}else{
								if (JOptionPane.showConfirmDialog(null, "Jeni te sigurt se doni ta kryeni kete veprim?", "Bestätigung", JOptionPane.YES_NO_OPTION, 
										JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION){
									
									// Check the 4 remaining requirements
									
									// 1st: Client cannot take a loan if the last taken loan was taken less than
									// 6 months ago. The 1st and 2nd condition are only checked if the client has other loans
									if (Loan.hasLoan(c_id)){
										ArrayList<Loan>loans = Loan.getLoans(c_id);
										
										// get the most recent loan
										int month = 0;
										int day = 0;
										int index = -1;
										for (Loan l :  loans){
											String date = l.getDate();
											String[] dates = date.split("-");

											if (Integer.parseInt(dates[1]) > month){
												month = Integer.parseInt(dates[1]);
												index++;
											}
											else if (Integer.parseInt(dates[1]) == month)
											{
												if (Integer.parseInt(dates[2]) > day){
													day = Integer.parseInt(dates[2]);
													index++;
												}
											}
										}
										
										if (loans.get(index).getMonthsPast() <= 6)
											JOptionPane.showMessageDialog(null, "Klienti nuk mund te marre nje kredi te re.\nKredia e fundit u morr "
													+ "para 6 muajsh.");
										else{
											// 2nd: If the client has other loans, the sum (principal amount and not remaining)
											// of all loans shouldn't be more than 100k
											
											double sum = 0;
											for (Loan l : loans)
												sum += l.getPrincipalAmount();
											
											sum += amount;
											if (sum > 100000)
												JOptionPane.showMessageDialog(null, "Klienti nuk mund te marre nje kredi te re.\nShuma e kredive te tjera "
														+ "(perfshire kete) tejkalon 100,000.");
											else{
												if (!checkIrregularPayments (c_id))
													JOptionPane.showMessageDialog(null, "Ky klient ka me shume se 10 pagesa te vonuara. \n"
															+ "Ai nuk mund te marre nje kredi te re.");
												else
													// create the new loan
													createNewLoan(c_id, amount, interest, years, no_payments);
												
													// Save the actionPerformed in the database
													Client client = Client.getClient(c_id);
													String actionPerformed = "Kredi e re";
													ActionHistory action = new ActionHistory(c_id, client.getName(), client.getSurname(), actionPerformed,
															amount);
													ActionHistory.saveAction(action);
													// add to the history table
													
													historyTableModel.addRow(new Object[]{
															client.getId(), client.getName(), client.getSurname(), actionPerformed, amount, action.getDate()
													});
												
											}
										}
									}else{
										
										if (!checkIrregularPayments (c_id))
											JOptionPane.showMessageDialog(null, "Ky klient ka me shume se 10 pagesa te vonuara. \n"
													+ "Ai nuk mund te marre nje kredi te re.");
										else{
											createNewLoan(c_id, amount, interest, years, no_payments);
										
											// Save the actionPerformed in the database
											Client client = Client.getClient(c_id);
											String actionPerformed = "Kredi e re";
											ActionHistory action = new ActionHistory(c_id, client.getName(), client.getSurname(), actionPerformed,
													amount);
											ActionHistory.saveAction(action);
											// add to the history table
											historyTableModel.addRow(new Object[]{
													client.getId(), client.getName(), client.getSurname(), actionPerformed, amount, action.getDate()
											});
										
										}
									}
								}
							}
							
						}else{
							JOptionPane.showMessageDialog(null, "Ky klient nuk ekziston");
						}
					}catch(SQLException exception){
						JOptionPane.showMessageDialog(null, "Could not execute query: " + exception);
					}
					catch(NumberFormatException ex){
						JOptionPane.showMessageDialog(null, "Te dhenat nuk jane te sakta");
					}
				}
			}
		});
		
		JButton resetButton = new JButton ("Fshi");
		resetButton.setForeground(Color.red);
		resetButton.setBounds(470, 115, 140, 23);
		loanRegisterPanel.add(resetButton);
		
		resetButton.addActionListener(new ActionListener(){
			public void actionPerformed (ActionEvent event){
				if (JOptionPane.showConfirmDialog(null, "Jeni te sigurt se doni t'i fshini te gjitha fushat?", "Bestätigung", JOptionPane.YES_NO_OPTION, 
						JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION){
					idTextField4.setValue(null);
					amountTextField.setText("");
					interestTextField.setValue(null);
					yearTextField.setValue(null);
					no_paymentsTextField.setValue(null);
					creditInfoArea.setText("");
				}
			}
		});
		
		
		return loanRegisterPanel;
	}


	/**
	 * Creates the ActionPanel where actions such as withdraw, deposit or transfer can be taken. 
	 * @return the actionPanel
	 */
	private Component createActionPanel() {
		JPanel actionPanel = new JPanel();
		actionPanel.setLayout(null);
		actionPanel.setBackground(Color.LIGHT_GRAY);
		
		// Create loan payment button
		JButton loanPaymentButton = new JButton("Paguaj kredine");
		loanPaymentButton.setBounds(480, 192, 140, 23);
		actionPanel.add(loanPaymentButton);
				
		// Create the id label
		JLabel idLabel = new JLabel("ID: ");
		idLabel.setForeground(new Color (51, 163, 78));
		idLabel.setBounds(15, 20, 90, 14);
		actionPanel.add(idLabel);
		
		// Create the id textField with a maskFormatter
		idTextField2 = new JFormattedTextField(idMask);
		idTextField2.setBounds(150, 17, 90, 24);
		idTextField2.setColumns(15);
		actionPanel.add(idTextField2);
		
		// Listen for changes in the account id JTextField and reset the availability of both 
		// the loan payment button and the other loan payment JTextField if any change is detected.
		// In other words, if a second loan payment has been enabled for a client and the user makes any
		// change in his credentials that have been already typed by that point, the loan payment button (which serves 
		// to pay the oldest loan) will be enabled and the 'Andere Darlehen zahlen' field will be disabled
		idTextField2.getDocument().addDocumentListener(new DocumentListener(){
			public void changedUpdate (DocumentEvent e){
				changeAvailability();
			}
			
			public void removeUpdate (DocumentEvent e){
				changeAvailability();
			}
			
			public void insertUpdate (DocumentEvent e){
				changeAvailability();
			}
			
			// Enable the button, therefore enabling the payment of the oldest loan
			// disable the payment of other possible loans
			private void changeAvailability(){
				loanPaymentButton.setEnabled(true);
				otherLoanPaymentTextField.setEditable(false);
			}
		});
		
		// Create the sumLabel
		JLabel sumLabel = new JLabel("Shuma: ");
		sumLabel.setForeground(new Color(51, 163, 78));
		sumLabel.setBounds(15, 65, 90, 14);
		actionPanel.add(sumLabel);
		
		// Create the sum Field
		sumField = new JTextField(15);
		sumField.setBounds(150, 62, 90, 24);
		actionPanel.add(sumField);
		
		// Create the withdraw Button
		JButton withdrawButton = new JButton ("Terhiq para");
		withdrawButton.setForeground(Color.red);
		withdrawButton.setBounds(290, 20, 140, 23);
		actionPanel.add(withdrawButton);
		
		// Create the deposit Button
		JButton depositButton = new JButton ("Shto para");
		depositButton.setForeground(Color.blue);
		depositButton.setBounds(290, 65, 140, 23);
		actionPanel.add(depositButton);
		
		// Create the transfer Button
		JButton transferButton = new JButton ("Transfero");
		transferButton.setForeground(Color.ORANGE);
		transferButton.setBounds(290, 110, 140, 23);
		actionPanel.add(transferButton);
		
		// Create the new balance label
		JLabel newBalanceLabel = new JLabel("Gjendja e re:");
		newBalanceLabel.setForeground(new Color (51, 163, 78));
		newBalanceLabel.setBounds(500, 77, 110, 14);
		actionPanel.add(newBalanceLabel);
		
		// Create the new balance textField
		newBalanceTextField = new JTextField(15);
		newBalanceTextField.setBounds(480, 105, 140, 23);
		newBalanceTextField.setEditable(false);
		actionPanel.add(newBalanceTextField);
		
		// Create the transfer ID textField
		idTextField3 = new JFormattedTextField(idMask);
		idTextField3.setColumns(15);
		idTextField3.setBounds(150, 110, 90, 24);
		actionPanel.add(idTextField3);
		
		// Create the transfer ID label
		JLabel transferIdLabel = new JLabel("ID");
		transferIdLabel.setForeground(new Color(51, 163, 78));
		transferIdLabel.setBounds(15, 110, 250, 14);
		actionPanel.add(transferIdLabel);
		
		JLabel transferIdLabel2 = new JLabel ("e transfertes: ");
		transferIdLabel2.setForeground(new Color(51, 163, 78));
		transferIdLabel2.setBounds(15, 125, 130, 14);
		actionPanel.add(transferIdLabel2);
				
		withdrawButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent event){
				// This flag will check if the provided ID actually exists 
				boolean updateCondition = false;
				
				// Check if the required information has been provided
				if (idTextField2.getText().equals("") || sumField.getText().equals("")){
					JOptionPane.showMessageDialog(null, "Ju lutem plotesoni informacionet qe mungojne!");
				}
				else{
					try{
						// Check whether the client exists
						if (Client.clientExists(idTextField2.getText())){
							updateCondition = true;
							
							// confirm message
							if (JOptionPane.showConfirmDialog(null, "A jeni te sigurt se doni ta terhiqni shumen?", "Bestätigung", JOptionPane.YES_NO_OPTION, 
									JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION){
								
								if (Client.isFrozen(idTextField2.getText()))
									JOptionPane.showMessageDialog(null, "Kjo llogari eshte e ngrire. Nuk mund te behet asnje veprim me te.");
								else{
									double sum = Double.parseDouble(sumField.getText()); 
							
									Client client = Client.getClient(idTextField2.getText());
									double initialBalance = client.getBalance();
								
									if (sum > initialBalance){
										JOptionPane.showMessageDialog(null, "Balanca nuk eshte e mjaftueshme per te mbyllur kete veprim");
									}else{
										double result = initialBalance - Double.parseDouble(sumField.getText());
									
										// Update the value of the table
										for (int i = 0; i < infoTable.getRowCount(); i ++){
											if (idTextField2.getText().equals(infoTable.getValueAt(i, 0)))
												infoTable.setValueAt(result+"", i, 4);
										}
										newBalanceTextField.setText(result+"");
										Client.withdraw(client, Double.parseDouble(sumField.getText()));
										
										// Save the actionPerformed in the database
										String actionPerformed = "Terheqje parash";
										ActionHistory action = new ActionHistory(idTextField2.getText(), client.getName(), client.getSurname(), actionPerformed,
												Double.parseDouble(sumField.getText()));
										ActionHistory.saveAction(action);
										// add to the history table
										historyTableModel.insertRow(0, new Object[]{
												client.getId(), client.getName(), client.getSurname(), actionPerformed, sumField.getText(), action.getDate()
										});
										JOptionPane.showMessageDialog(null, "Veprimi u krye me sukses");
									}
								}
							}
						}
					}
					catch (SQLException exception){
						JOptionPane.showMessageDialog(null, "Error executing query: " + exception);
					}
					catch(NumberFormatException exception){
						JOptionPane.showMessageDialog(null, "Ju lutem jepni nje numer");
					}
				
					if (!updateCondition){
						
						// Check if the client ever existed. If he did we can find him in the action_history 
						// table of the database since an appropriate message would have been stored.
						// get all the history of this client
						try{
							ArrayList<ActionHistory> clientHistory = ActionHistory.getClientHistory(idTextField2.getText());
							
							// A flag which will check whether the client has indeed existed before
							boolean found = false;
							for (ActionHistory temp : clientHistory){
								if (temp.getActionPerformed().equals("Konto löschen")){
									JOptionPane.showMessageDialog(null, "Ky klient nuk ekziston me. Ai u fshi me "+temp.getDate());
									found = true;
								}
							}
							if (!found)
								JOptionPane.showMessageDialog(null, "Ky klient nuk ekziston");
						}catch(SQLException ex){
							JOptionPane.showMessageDialog(null, "Error executing query " + ex); 
						}
					}
				}
			}
		});
		
		
		depositButton.addActionListener(new ActionListener(){
			public void actionPerformed (ActionEvent event){
				// This flag will check if the provided ID actually exists
				boolean updateCondition = false;
				
				if (idTextField2.getText().equals("") || sumField.getText().equals("")){
					JOptionPane.showMessageDialog(null, "Ju lutem plotesoni informacionet qe mungojne!");
				}
				else{
					try{
					// Check if the client exists
					if (Client.clientExists(idTextField2.getText())){
							updateCondition = true;
							if (JOptionPane.showConfirmDialog(null, "Jeni te sigurt se doni ta kryeni kete veprim?", "Bestätigung", JOptionPane.YES_NO_OPTION, 
									JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION)
							{
								if (Client.isFrozen(idTextField2.getText()))
									JOptionPane.showMessageDialog(null, "Kjo llogari eshte e ngrire. Asnje veprim nuk u krye.");
								else{
									double sum = Double.parseDouble(sumField.getText());
								
									Client client = Client.getClient(idTextField2.getText());
									double initialBalance = client.getBalance();
									double result = initialBalance + sum;
								
									// Update the value of the table
									for (int i = 0; i < infoTable.getRowCount(); i ++){
										if (idTextField2.getText().equals(infoTable.getValueAt(i, 0)))
											infoTable.setValueAt(result+"", i, 4);
									}
									newBalanceTextField.setText(result+"");
									Client.deposit(client, Double.parseDouble(sumField.getText()));
									
									// Save the actionPerformed in the database
									String actionPerformed = "Depozitim parash";
									ActionHistory action = new ActionHistory(idTextField2.getText(), client.getName(), client.getSurname(), actionPerformed,
											Double.parseDouble(sumField.getText()));
									ActionHistory.saveAction(action);
									// add to the history table
									historyTableModel.insertRow(0, new Object[]{
											client.getId(), client.getName(), client.getSurname(), actionPerformed, sumField.getText(), action.getDate()
									});
									JOptionPane.showMessageDialog(null, "Ky veprim u realizua me sukses!");
								}
							}
						}
					
					if (!updateCondition){
						
					// Check if the client ever existed. If he did we can find him in the action_history 
					// table of the database since an appropriate message would have been stored.
					// get all the history of this client
					
						ArrayList<ActionHistory> clientHistory = ActionHistory.getClientHistory(idTextField2.getText());
							
						// A flag which will check whether the client has indeed existed before
						boolean found = false;
						for (ActionHistory temp : clientHistory){
							if (temp.getActionPerformed().equals("Konto löschen")){
								JOptionPane.showMessageDialog(null, "Ky klient nuk ekziston me. Ai u fshi me "+temp.getDate());
								found = true;
							}
						}
						if (!found)
							JOptionPane.showMessageDialog(null, "Ky klient nuk ekziston"); 
					
					}
				}catch (SQLException ex){
					JOptionPane.showMessageDialog(null, "Error while executing query: " + ex);
				}
				catch(NumberFormatException exception){
					JOptionPane.showMessageDialog(null, "Ju lutem jepni nje numer.");
				}
					
				}
			}
		});
		
		
		transferButton.addActionListener(new ActionListener(){
			
			public void actionPerformed (ActionEvent event){
				// This conditions will become true if the ids of the transfering account as well as
				// the receiving accounts exist, they remain false otherwise 
				boolean updateCondition1 = false;
				boolean updateCondition2 = false;
				
				// Variables to store the balances
				double balance1 = 0;
				double balance2 = 0;
				
				// Variables to store the rows
				int r1 = 0, r2 = 0;
				
				if (idTextField2.getText().equals("") || idTextField3.getText().equals("") || sumField.getText().equals("")){
					JOptionPane.showMessageDialog(null, "Ju lutem plotesoni informacionet qe mungojne!");
				}
				else{
				for (int i = 0; i < infoTable.getRowCount(); i ++){
					if (idTextField2.getText().equals(infoTable.getValueAt(i, 0)))
					{
						updateCondition1 = true;
						balance1 = Double.parseDouble(""+infoTable.getValueAt(i, 4));
						r1 = i;
					}
					if (idTextField3.getText().equals(infoTable.getValueAt(i, 0))){
						updateCondition2 = true;
						balance2 = Double.parseDouble(""+infoTable.getValueAt(i, 4));
						r2 = i;
					}
				}
				
				if (!updateCondition1 || !updateCondition2){
					if (!updateCondition1){
						
						// Check if the client ever existed. If he did we can find him in the action_history 
						// table of the database since an appropriate message would have been stored.
						// get all the history of this client
						try{
							ArrayList<ActionHistory> clientHistory = ActionHistory.getClientHistory(idTextField2.getText());
							
							// A flag which will check whether the client has indeed existed before
							boolean found = false;
							for (ActionHistory temp : clientHistory){
								if (temp.getActionPerformed().equals("Konto löschen")){
									JOptionPane.showMessageDialog(null, "Klienti i pare nuk ekziston me. Ai u fshi me "+temp.getDate());
									found = true;
								}
							}
							if (!found)
								JOptionPane.showMessageDialog(null, "Klienti i pare nuk ekziston.");
						}catch(SQLException ex){
							JOptionPane.showMessageDialog(null, "Error executing query " + ex); 
						}
					}
					if (!updateCondition2){
						
						// Check if the client ever existed. If he did we can find him in the action_history 
						// table of the database since an appropriate message would have been stored.
						// get all the history of this client
						try{
							ArrayList<ActionHistory> clientHistory = ActionHistory.getClientHistory(idTextField3.getText());
							
							// A flag which will check whether the client has indeed existed before
							boolean found = false;
							for (ActionHistory temp : clientHistory){
								if (temp.getActionPerformed().equals("Konto löschen")){
									JOptionPane.showMessageDialog(null, "Klienti i dyte nuk ekziston me. Ai u fshi me "+temp.getDate());
									found = true;
								}
							}
							if (!found)
								JOptionPane.showMessageDialog(null, "Klienti i dyte nuk ekziston");
						}catch(SQLException ex){
							JOptionPane.showMessageDialog(null, "Error executing query " + ex); 
						}
					}
				}
				else{
					if (JOptionPane.showConfirmDialog(null, "Jeni te sigurt se doni ta realizoni kete transferte?", "Bestätigung", JOptionPane.YES_NO_OPTION, 
							JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION){
						try{
							if (Client.isFrozen(idTextField2.getText()))
								JOptionPane.showMessageDialog(null, "Kjo llogari eshte e ngrire. Asnje veprim nuk u krye.");
							else if (Client.isFrozen(idTextField3.getText()))
								JOptionPane.showMessageDialog(null, "Llogaria e dyte eshte e ngrire. Asnje veprim nuk u krye.");
							else{
								double result1 = balance1 - Double.parseDouble(sumField.getText());
								double result2 = balance2 + Double.parseDouble(sumField.getText());
								
								// Special Case: Try to transfer within the same account
								if (idTextField2.getText().equalsIgnoreCase(idTextField3.getText()))
									JOptionPane.showMessageDialog(null, "Nuk mund te transferohen para midis se njejtes llogari.");
								else{
									Client client1 = Client.getClient(idTextField2.getText());
									Client client2 = Client.getClient(idTextField3.getText());
									Client.withdraw(client1, Double.parseDouble(sumField.getText()));
									Client.deposit(client2, Double.parseDouble(sumField.getText()));
								
									
									// Save the actionPerformed in the database
									String actionPerformed1 = "Transferta nga";
									String actionPerformed2 = "Transferta tek";
									ActionHistory action1 = new ActionHistory(idTextField2.getText(), client1.getName(), client1.getSurname(), actionPerformed1,
									Double.parseDouble(sumField.getText()));
									ActionHistory.saveAction(action1);
									// add to the history table
									historyTableModel.insertRow(0, new Object[]{
											client1.getId(), client1.getName(), client1.getSurname(), actionPerformed1, sumField.getText(), action1.getDate()
									});
								
									ActionHistory action2 = new ActionHistory(idTextField3.getText(), client2.getName(), client2.getSurname(), actionPerformed2,
									Double.parseDouble(sumField.getText()));
									ActionHistory.saveAction(action2);
								
									historyTableModel.insertRow(0, new Object[]{
											client2.getId(), client2.getName(), client2.getSurname(), actionPerformed2, sumField.getText(), action2.getDate()
									});
								
									infoTable.setValueAt(result1+"", r1, 4);
									infoTable.setValueAt(result2+"", r2, 4);
									newBalanceTextField.setText(""+result1);
								}
								JOptionPane.showMessageDialog(null, "Veprimi u krye me sukses");
							}
						}
						catch(NumberFormatException ex){
							JOptionPane.showMessageDialog(null, "Te dhenat nuk jane te sakta");
						}
					catch(SQLException exception){
						JOptionPane.showMessageDialog(null, "Error executing query: " + exception); 
						}
					
					}
				}
			}
		}
		});
		
		// Create the reset button which will set every field to an empty string
		JButton resetButton = new JButton ("Fshi");
		resetButton.setForeground(Color.red);
		resetButton.setBounds(240, 250, 140, 23);
		actionPanel.add(resetButton);
		
		resetButton.addActionListener(new ActionListener(){
			public void actionPerformed (ActionEvent event){
				if (JOptionPane.showConfirmDialog(null, "Jeni te sigurt se doni te fshini cdo fushe?", "Bestätigung", JOptionPane.YES_NO_OPTION, 
						JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION){
					idTextField2.setValue(null);
					idTextField3.setValue(null);
					sumField.setText("");
					newBalanceTextField.setText("");
					loanPaymentTextField.setText("");
					otherLoanPaymentTextField.setText("");
				}
			}
		});
		
		// Create the loan Payment Label
		JLabel loanLabel = new JLabel("Paguaj kredine: ");
		loanLabel.setForeground(new Color(51, 163, 78));
		loanLabel.setBounds(15, 195, 130, 14);
		actionPanel.add(loanLabel);
		
		// Create loan payment textField
		loanPaymentTextField = new JTextField(15);
		loanPaymentTextField.setBounds(150, 192, 90, 24);
		actionPanel.add(loanPaymentTextField);
		
		// Create a loan payment textField for the 2nd or 3rd loan
		// after at least one month in advance payment have been made 
		// for the oldest loan
		DateFormat loanDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		otherLoanPaymentTextField = new JFormattedTextField(loanDateFormat);
		otherLoanPaymentTextField.setColumns(15);
		otherLoanPaymentTextField.setBounds(360, 192, 90, 24);
		otherLoanPaymentTextField.setEditable(false);
		actionPanel.add(otherLoanPaymentTextField);
		
		// Enable other loan payment. This payment can only be made if the oldest loan has been 
		// paid one months in advance. Otherwise the client can only pay for the oldest loan.
		otherLoanPaymentTextField.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent event){
				
				// Check first if the bank employee has forgotten to input the id and give a seperate message
				// actually this check is redundant since it is already included in Kennnummer field action listener
				if (idTextField2.getText().trim().length() == 0){
					JOptionPane.showMessageDialog(null, "Duhet te plotesoni ID (siper).");
				}// Check if the amount has been given
				else if (loanPaymentTextField.getText().trim().length() == 0)
					JOptionPane.showMessageDialog(null, "Duhet te plotesoni shumen.");
				else{
					// Check if the client has inputed a date
					if(otherLoanPaymentTextField.getText().trim().length() == 0)
						JOptionPane.showMessageDialog(null, "Duhet te jepni nje date per kredine");
					else{
						if (JOptionPane.showConfirmDialog(null, "A jeni te sigurt se doni ta kryeni kete veprim?", "Bestätigung", JOptionPane.YES_NO_CANCEL_OPTION,
								JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION){
							try{
								String c_id = idTextField2.getText();
								String start_date = otherLoanPaymentTextField.getText();
								double currPayment = Double.parseDouble(loanPaymentTextField.getText()); 
						
								// Check if the loan exists
								Loan otherLoan = new Loan(c_id, start_date);
								if (!otherLoan.existsLoan())
									JOptionPane.showMessageDialog(null, "Klienti nuk ka asnje kredi tjeter me kete date");
								else{
									Loan loan = Loan.getLoan(c_id, start_date);
								
									if (currPayment < loan.getAmountOfFixedPayments()){
										JOptionPane.showMessageDialog(null, "Nuk eshte e mundur te beni kete pagese. Sasia e kerkuar eshte: \n"
												+ loan.getAmountOfFixedPayments());
									}else{
										// The client cannot pay more than 1 month in advance
										int paymentCheck = loan.getCurrentNumberOfPayments() - loan.getNoPaymentsUpToDate();
										
										// the control is made for >=2 because the paymentCheck variable is
										// calculated before the payment is made
										if (paymentCheck >= 2)
											JOptionPane.showMessageDialog(null, "Klienti ka paguar nje pagese shtese. Nje pagese tjeter \n"
													+ "nuk eshte e mundur.");
										else{
											
											Client client = Client.getClient(c_id);
											if (currPayment > client.getBalance()){
												JOptionPane.showMessageDialog(null, "Klienti nuk ka para per ta bere pagesen.");
											}else{
											
												int loanInfoTableIndex = -1;
												// get the index of the loan in the infoTable
												for (int i = 0; i < loanInfoTable.getRowCount(); i++){
													if (loanInfoTable.getValueAt(i, 0).equals(c_id)){
														
														String value = loanInfoTable.getValueAt(i, 4)+"";
														if (Double.parseDouble(value) == loan.getRemainingPayment(false)){
															loanInfoTableIndex = i;
														}
													}
												}
												
												// withdraw the money from the client
												Client.withdraw(client, loan.getAmountOfFixedPayments());
												
												// Save the actionPerformed in the database
												String actionPerformed = "Darlehenzahlung";
												ActionHistory action = new ActionHistory(idTextField2.getText(), client.getName(), client.getSurname(), actionPerformed,
														loan.getAmountOfFixedPayments());
												ActionHistory.saveAction(action);
												// add to the history table
												historyTableModel.insertRow(0, new Object[]{
														client.getId(), client.getName(), client.getSurname(), actionPerformed, loan.getAmountOfFixedPayments(), 
														action.getDate()
												});
												
												// update the current_payment_counter in the database
												loan.increasePaymentCounter();
												
												JOptionPane.showMessageDialog(null, "Pagesa ishte e suksesshme.");
												
												boolean isPaid = false;
												
												// 	Check if the loan has been paid
												if (loan.isPaid())
												{
													JOptionPane.showMessageDialog(null, "Kredia u mbyll.");
			
													// 	Delete the loan and add it to loan History
													loan.deleteLoan();
				
													// Save the actionPerformed in the database
													String actionPerformed2 = "Darlehen völlig bezahlt";
													ActionHistory action2 = new ActionHistory(idTextField2.getText(), client.getName(), client.getSurname(), actionPerformed2,
															-1);
													ActionHistory.saveAction(action2);
													
													// add to the history table
													historyTableModel.addRow(new Object[]{
															client.getId(), client.getName(), client.getSurname(), actionPerformed, "-1", action2.getDate()
													});
													
													
													// Delete the loan from the loanInfoTable
													((DefaultTableModel)loanInfoTable.getModel()).removeRow(loanInfoTableIndex);
													isPaid = true;
												}
											
												// If there are unpaid months, give a message and update 
												// the no_irregular_payments field in the database
												if (paymentCheck < 0){
									
													int tempPayCheck = paymentCheck * -1;
													if (tempPayCheck != 0){
														JOptionPane.showMessageDialog(null, "Klienti ka  "+tempPayCheck+" pagesa te prapambetura.");
														loan.updateIrregularPayments(tempPayCheck);
													}
												}
												
												// Reflect the changes in the info table
												for (int i = 0; i < infoTable.getRowCount(); i ++){
												
													if (infoTable.getValueAt(i, 0).equals(c_id)){
														String currentAmount = (String) infoTable.getValueAt(i, 4);
														double result = Double.parseDouble(currentAmount) - loan.getAmountOfFixedPayments();
														double result2 = Math.round(result * 100.0) / 100.0;
														infoTable.setValueAt(""+result2, i, 4);
														newBalanceTextField.setText(""+result2);
														break;
													}
												}
												
												
												// Reflect the changes in the loan info table only if the loan hasn't been 
												// completely paid
												if (!isPaid){
													if (loanInfoTableIndex != -1){
														loanInfoTable.setValueAt(loan.getRemainingPayment(false), loanInfoTableIndex, 4);
														loanInfoTable.setValueAt(loan.getNumberOfIrregularPayments(), loanInfoTableIndex, 5);
													}
												}
			
											}
										}
									}
								}
							}
							catch(NumberFormatException ex1){
								JOptionPane.showMessageDialog(null, "Duhet te jepni nje numer");
							}
							catch(SQLException ex2){
								JOptionPane.showMessageDialog(null, "Error executing query: " + ex2);
							}
						}
					}
				}
			}
		});
		
		// Create a label for this purpose
		JLabel otherLoanLabel = new JLabel("Paguaj kredi");
		otherLoanLabel.setForeground(new Color (51, 163, 78));
		otherLoanLabel.setBounds(260, 189, 130, 14);
		actionPanel.add(otherLoanLabel);
		
		JLabel otherLoanLabel2 = new JLabel("tjeter: ");
		otherLoanLabel2.setForeground(new Color (51, 163, 78));
		otherLoanLabel2.setBounds(286, 205, 130, 14);
		actionPanel.add(otherLoanLabel2);
		
		loanPaymentButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent event){
				
				// Check first if the bank employee has forgotten to input the id and give a seperate message
				if (idTextField2.getText().trim().length() == 0)
					JOptionPane.showMessageDialog(null, "Duhet te plotesoni ID (larte).");
				else{
					
					// Check if the amount has been given
					if (loanPaymentTextField.getText().equals(""))
						JOptionPane.showMessageDialog(null, "Duhet te plotesoni shumen");
					else{
						
						if (JOptionPane.showConfirmDialog(null, "A jeni te sigurt qe doni ta kryeni kete veprim?", "Bestätigung", JOptionPane.YES_NO_OPTION,
								JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION){
							try{
								
								// If the client has more than one loan the oldest one(meaning the one taken the earliest)
								// has precedence and therefore will be the one to be paid
								double amount = Double.parseDouble(loanPaymentTextField.getText());
								String c_id = idTextField2.getText();
								
								if (!Client.clientExists(idTextField2.getText()))
								{
									// Check if the client has ever existed and print an appropriate message.
									// If the client has ever existed we can find him in the ActionHistory 
									// table of the database
									ArrayList<ActionHistory> clientHistory = ActionHistory.getClientHistory(idTextField2.getText());
										
									// Flag that will check if the client has indeed existed before
									boolean found = false;
									for (ActionHistory temp : clientHistory){
										if (temp.getActionPerformed().equals("Konto löschen")){
											JOptionPane.showMessageDialog(null, "Ky klient nuk ekziston. Ai u fshi me "+temp.getDate());
											found = true;
										}
									}
									if (!found)
										JOptionPane.showMessageDialog(null, "Ky klient nuk ekziston me.");
								}
								else{
								if (Client.isFrozen(idTextField2.getText()))
									JOptionPane.showMessageDialog(null, "Kjo llogari eshte e ngrire. Nuk mund te kryhet asnje veprim");
								else{
									// First check if this client has any existing loan
									Client client = Client.getClient(c_id);
									if (!Loan.hasLoan(c_id)){
										JOptionPane.showMessageDialog(null, "Ky klient nuk ka asnje kredi.");
									}else{
									
										// Get all the loans that this client might have
										ArrayList<Loan> loans = Loan.getLoans(c_id);
									
										// 	Get the oldest loan
										Loan oldestLoan = loans.get(0).getOldestLoan();
									
										// check that the provided amount is at least as much as the 
										// fixed amount to be payed
										if (amount < oldestLoan.getAmountOfFixedPayments())
											JOptionPane.showMessageDialog(null, "Nuk eshte e mundur qe pagesa te realizohet. Sasia e kerkuar eshte: \n"
													+ oldestLoan.getAmountOfFixedPayments());
										else {
										
											// The client cannot pay more than 1 month in advance
											int paymentCheck = oldestLoan.getCurrentNumberOfPayments() - oldestLoan.getNoPaymentsUpToDate();
										
											// 	the control is made for >=2 because the paymentCheck variable is
											// calculated before the payment is made
											if (paymentCheck >= 2){
												JOptionPane.showMessageDialog(null, "Klienti ka paguar nje muaj shtese. Nje pagese tjeter \n"
														+ "nuk eshte e mundur ne kete moment.");
												loanPaymentButton.setEnabled(false);
												otherLoanPaymentTextField.setEditable(true);
												JOptionPane.showMessageDialog(null, "Klienti mund te ripaguaj kredi te tjera tani");
											}
											else{
												if (amount > client.getBalance()){
													JOptionPane.showMessageDialog(null, "Klienti nuk ka para per te bere pagesen");
												}
												else{
												
													int loanInfoTableIndex = -1;
													// get the index of the loan in the infoTable
													for (int i = 0; i < loanInfoTable.getRowCount(); i++){
													
														if (loanInfoTable.getValueAt(i, 0).equals(c_id)){
														
															// Since a client can have more than one loan we need to make another check
															// 	in order to know that we have the right loan. We will do so by checking 
															// the total remaining payment field. Because of the way the bank works this field
															// 	will be unique among the loans that the client might have.
														
															// As a matter of fact this is not completely the case. The client might have 3 loans
															// 	and the 2nd and 3rd loan could have the same remaining payment at some point
															// It would be better to implement this with a date since we are sure that it is unique
															// among loans of the same client
															String value = loanInfoTable.getValueAt(i, 4)+"";
														
															if (Double.parseDouble(value) == oldestLoan.getRemainingPayment(true)){
																loanInfoTableIndex = i;
															}
														}
													}
												
													// 	withdraw the money from the clients account
													Client.withdraw(client, oldestLoan.getAmountOfFixedPayments());
													
													// Save the actionPerformed in the database
													String actionPerformed = "Pagese kredie";
													ActionHistory action = new ActionHistory(idTextField2.getText(), client.getName(), client.getSurname(), actionPerformed,
															oldestLoan.getAmountOfFixedPayments());
													ActionHistory.saveAction(action);
													// add to the history table
													historyTableModel.insertRow(0, new Object[]{
															client.getId(), client.getName(), client.getSurname(), actionPerformed, oldestLoan.getAmountOfFixedPayments(),
															action.getDate()
													});
													
													
													// 	update the current_payment_counter in the database
													oldestLoan.increasePaymentCounter();
												
													JOptionPane.showMessageDialog(null, "Pagesa u krye me sukses");
												
													boolean isPaid = false;
													// 	Check if the loan has been paid
													if (oldestLoan.isPaid())
													{	
														JOptionPane.showMessageDialog(null, "Kredia u pagua me sukses");
			
														// Save the actionPerformed in the database
														String actionPerformed2 = "Darlehen völlig bezahlt";
														ActionHistory action2 = new ActionHistory(idTextField2.getText(), client.getName(), client.getSurname(), actionPerformed2,
																-1);
														ActionHistory.saveAction(action2);
														
														// add to the history table
														historyTableModel.addRow(new Object[]{
																client.getId(), client.getName(), client.getSurname(), actionPerformed, "-1", action2.getDate()
														});
														
														
														// 	Delete the loan and add it to loan History
														oldestLoan.deleteLoan();
													
														// Delete the loan from the loanInfoTable
														((DefaultTableModel)loanInfoTable.getModel()).removeRow(loanInfoTableIndex);
														isPaid = true;
													
													}
											
													// 	If there are unpaid months, give a message and update 
													// the no_irregular_payments field in the database
													if (paymentCheck < 0){
									
														int tempPayCheck = paymentCheck * -1;
														if (tempPayCheck != 0){
															JOptionPane.showMessageDialog(null, "Klienti ka "+tempPayCheck+" pagesa te prapambetura");
															oldestLoan.updateIrregularPayments(tempPayCheck);
														}
													}
												
													// 	Reflect the changes in the info table
													for (int i = 0; i < infoTable.getRowCount(); i ++){
												
														if (infoTable.getValueAt(i, 0).equals(c_id)){
															String currentAmount = (String) infoTable.getValueAt(i, 4);
															double result = Double.parseDouble(currentAmount) - oldestLoan.getAmountOfFixedPayments();
															double result2 = Math.round(result * 100.0) / 100.0;
															infoTable.setValueAt(""+result2, i, 4);
															newBalanceTextField.setText(""+result2);
															break;
														}
													}
												
												
													// Reflect the changes in the loan info table only if the loan hasn't been 
													// completely paid
													if (!isPaid){
														if (loanInfoTableIndex != -1){
															loanInfoTable.setValueAt(oldestLoan.getRemainingPayment(true), loanInfoTableIndex, 4);
															loanInfoTable.setValueAt(oldestLoan.getNumberOfIrregularPayments(), loanInfoTableIndex, 5);
														}
													}
												}
											}
										}
									}
								}
							}
							}catch(NumberFormatException ex1){
								JOptionPane.showMessageDialog(null, "Duhet te jepni nje numer");
							}
							catch(SQLException ex2){
								JOptionPane.showMessageDialog(null, "Error executing query: " + ex2);
							}
						}
					}
				}
			}
		});
		
		return actionPanel;
	}

	


	/**
	 * Create the Help menu
	 * @return the help menu
	 */
	private JMenu createHelpMenu() {
		JMenu helpMenu = new JMenu("Help");
		JMenuItem bankInfItem = new JMenuItem("On the Bank");
		helpMenu.add(bankInfItem);
		helpMenu.add(createLoanInformationMenuItem());
		bankInfItem.addActionListener(new ActionListener(){
			public void actionPerformed (ActionEvent event){
				JOptionPane.showMessageDialog(null, "ID-ja e perdoruesve eshte e formes: A12345678A\nBanka u themelua me gusht 2018.");
			}
		});
		return helpMenu;
	}

	/**
	 * Create a JMenuItem containing informations on the loan
	 * @return the JMenuItem
	 */
	private JMenuItem createLoanInformationMenuItem() {
		JMenuItem loanInformation = new JMenuItem("Information on loans");
		loanInformation.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent event){
				JOptionPane.showMessageDialog(null, "Kredia mund te paguhet midis 1 dhe 10 viteve.\n"
						+ "Shuma qe mund te merret si kredi duhet te jete midis 3.000 dhe 100.00€.\n"
						+ "Interesi duhet te jete midis 2 dhe 20%.\n"
						+ "Pagesat per kredine mund te behen mujore, 4-mujore, 6-mujore ose 1-vjecare.\n"
						+ "Ne qofte se klienti ka kredi te tjera:\n" 
						+ "1) Kredia me e vjeter ka prioritet per sa i perket shlyerjes.\n"
						+ "2) Nqs shume e kredive i kalon 100.000€ klienti nuk mund te marre kredi tjeter.\n"
						+ "3) Distanca midis dy kredive te marra nga i njejti person duhet te jete te pakten 6 muaj.\n"
						+ "4) Nqs klienti ka me shume se 10 pagesa te vonuara nga kredite e tjera \n     ai nuk mund te marre me kredi.\n"
						+ "Kjo kredi paguhet me ane te dates. Dmth nqs klienti ka 3 kredi, ai duhet ne fillim te beje nje\n"
						+ "pagese shtese ne kredine me te vjeter dhe mandej mund te paguaj nje nga 2 kredite e tjera \n"
						+ "ne baze te dates ne te cilen ato jane marre (kjo date eshte unike).\n"
						+ "\n"
						+ "Ne kete program, nje muaj eshte ekuivalent me nje dite.");
				
				
			}
		});
		return loanInformation;
	}


	/**
	 * Create the file menu	
	 * @return the file menu
	 */
	private JMenu createFileMenu() {
		JMenu fileMenu = new JMenu("File");
		JMenu accountsMenu = new JMenu ("Account");
		fileMenu.add(accountsMenu);
		accountsMenu.add(createDeleteAccountOption());
		accountsMenu.add(createFrezeAccountOption());
		accountsMenu.add(createUnFreezeAccountOption());
		fileMenu.add(createExitMenu());
		return fileMenu;
	}

	/**
	 * Unfreeze the account, enabling any and all possible transactions
	 * @return the JMenuItem enabling the operation
	 */
	private JMenuItem createUnFreezeAccountOption() {
		JMenuItem unFreezeAccount = new JMenuItem("Unfreeze account");
		unFreezeAccount.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent event){
				String id = JOptionPane.showInputDialog("Jepni ID-ne e llogarise qe do te riaktivizohet");
				if ((id != null) && id.length() > 0 ){
					
					try{
						
						if (!Client.clientExists(id))
							JOptionPane.showMessageDialog(null, "Kjo ID nuk u gjet");
						else{
							
							if (JOptionPane.showConfirmDialog(null, "Jeni te sigurt se doni ta kryeni kete veprim?", "Bestätigung", JOptionPane.YES_NO_OPTION,
									JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION){
								Client.unFreeze(id);
								
								// Save the actionPerformed in the database
								Client client = Client.getClient(id);
								String actionPerformed = "Riaktivizim llogarie";
								ActionHistory action = new ActionHistory(id, client.getName(), client.getSurname(), actionPerformed,
										-1);
								ActionHistory.saveAction(action);
								// add to the history table
								historyTableModel.addRow(new Object[]{
										client.getId(), client.getName(), client.getSurname(), actionPerformed, "-1", action.getDate()
								});
							
								
								JOptionPane.showMessageDialog(null, "Llogaria u riaktivizua me sukses.");
							}
						}
					}catch(SQLException ex){
						JOptionPane.showMessageDialog(null, "Error executing query.");
					}
				}else{
					JOptionPane.showMessageDialog(null, "Nuk u krye asnje ndryshim");
				}

			}
		});
		return unFreezeAccount;
	}


	/**
	 * Freeze the account, disabling any and all possible transactions
	 * @return the JMenuItem enabling the operation
	 */
	private JMenuItem createFrezeAccountOption() {
		JMenuItem freezeAccount = new JMenuItem("Freeze account");
		freezeAccount.addActionListener(new ActionListener(){
			public void actionPerformed (ActionEvent event){
				String id = JOptionPane.showInputDialog("Jepni ID-ne e llogarise qe do te ngrihet");
				if ((id != null) && id.length() > 0 ){
					
					try{
						
						if (!Client.clientExists(id))
							JOptionPane.showMessageDialog(null, "Kjo ID nuk u gjet");
						else{
							
							if (JOptionPane.showConfirmDialog(null, "Jeni te sigurt se doni ta ngrini kete llogari?", "Bestätigung", JOptionPane.YES_NO_OPTION,
									JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION){
								Client.freeze(id);
							
								// Save the actionPerformed in the database
								Client client = Client.getClient(id);
								String actionPerformed = "Ngrirje llogarie";
								ActionHistory action = new ActionHistory(id, client.getName(), client.getSurname(), actionPerformed,
										-1);
								ActionHistory.saveAction(action);
								// add to the history table
								historyTableModel.addRow(new Object[]{
										client.getId(), client.getName(), client.getSurname(), actionPerformed, "-1", action.getDate()
								});
							
								JOptionPane.showMessageDialog(null, "Llogaria u ngri me sukses");
							}
						}
					}catch(SQLException ex){
						JOptionPane.showMessageDialog(null, "Error executing query.");
					}
				}else{
					JOptionPane.showMessageDialog(null, "Nuk u be asnje ndryshim");
				}
			}
		});
		
		return freezeAccount;
	}


	/**
	 * Creates the exit menu
	 * @return the exit menu
	 */
	private JMenuItem createExitMenu() {
		JMenuItem exitItem = new JMenuItem("Exit");
		exitItem.addActionListener(new ActionListener(){
			public void actionPerformed (ActionEvent event){
				System.exit(0);
			}
		});
		return exitItem;
	}

	/**
	 * Creates the deleteAccount JMenuItem
	 * @return the deleteAccount JMenuItem
	 */
	private JMenuItem createDeleteAccountOption() {
		JMenuItem deleteAccountItem = new JMenuItem("Delete account");
		
		deleteAccountItem.addActionListener(new ActionListener(){
			public void actionPerformed (ActionEvent event){
				String id = JOptionPane.showInputDialog("Jepni ID-ne e llogarise qe do fshihet.");
				if ((id != null) && id.length() > 0 ){
					
					// This flag will check if the id give actually exists
					boolean deleteCondition = false;
					
					for (int i = 0; i < infoTable.getRowCount(); i ++)
					{
						if (id.equals(infoTable.getValueAt(i, 0))){
							deleteCondition = true;
							if (JOptionPane.showConfirmDialog(null, "Jeni te sigurt se doni ta kryeni kete veprim?", "Bestätigung", JOptionPane.YES_NO_OPTION, 
									JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION){
								
								try{
									// Check if the client has any loans. If he does, the account cannot be deleted.
									ArrayList<Loan> loans = Loan.getLoans(id);
									boolean hasLoans = true;
									for (Loan l : loans){
										if (l.existsLoan())
											hasLoans = false;
									}
									if (!hasLoans)
										JOptionPane.showMessageDialog(null, "Klienti nuk mund te fshihet sepse ka kredi te papaguara");
									else{
									
										// Save the actionPerformed in the database
										Client client = Client.getClient(id);
										String actionPerformed = "Konto löschen";
										ActionHistory action = new ActionHistory(id, client.getName(), client.getSurname(), actionPerformed,
												-1);
										ActionHistory.saveAction(action);
										// add to the history table
										
										historyTableModel.addRow(new Object[]{
												client.getId(), client.getName(), client.getSurname(), actionPerformed, "-1", action.getDate()
										});
										
										Client.deleteAccount(id);
										((DefaultTableModel) infoTable.getModel()).removeRow(i);
									}
								}catch(SQLException ex){
									JOptionPane.showMessageDialog(null, "Error executing query: " + ex);
								}
							}
						}
					}
					if (!deleteCondition)
					{
						JOptionPane.showMessageDialog(null, "Kjo ID nuk u gjet.");
					}
				}else{
					JOptionPane.showMessageDialog(null, "Nuk u be asnje ndryshim.");
				}
				
			}
		});
		
		return deleteAccountItem;
	}
	

	/**
	 * Creates the register panel which will allow the registration of users.
	 * @return the register panel panel
	 */
	private Component createRegisterPanel() {
		JPanel registerPanel = new JPanel();
		registerPanel.setBackground(Color.LIGHT_GRAY);
		registerPanel.setLayout(null);
		
		// Create the id Label
		JLabel iDLabel = new JLabel("ID:");
		iDLabel.setForeground(Color.BLUE);
		iDLabel.setBounds(56, 51, 99, 14);
		registerPanel.add(iDLabel);

		// Create the id TextField
		idTextField = new JFormattedTextField(idMask);
		idTextField.setColumns(10);
		idTextField.setBounds(175, 40, 308, 34);
		registerPanel.add(idTextField);

		// Create the important Label
		JLabel importantLabel1 = new JLabel("Duhet");
		importantLabel1.setForeground(Color.RED);
		importantLabel1.setBounds(493, 50, 100, 14);
		registerPanel.add(importantLabel1);

		//Create the lastName Label
		JLabel lastNameLabel = new JLabel("Mbiemri:");
		lastNameLabel.setForeground(Color.BLUE);
		lastNameLabel.setBounds(56, 89, 110, 14);
		registerPanel.add(lastNameLabel);

		//Create the lastName textField
		lastNameTextField = new JTextField();
		lastNameTextField.setColumns(10);
		lastNameTextField.setBounds(175, 80, 308, 34);
		registerPanel.add(lastNameTextField);

		JLabel importantLabel2 = new JLabel("Duhet");
		importantLabel2.setForeground(Color.RED);
		importantLabel2.setBounds(493, 90, 100, 14);
		registerPanel.add(importantLabel2);

		// Create the firstName label
		JLabel firstNameLabel = new JLabel("Emri:");
		firstNameLabel.setForeground(Color.BLUE);
		firstNameLabel.setBounds(56, 128, 93, 14);
		registerPanel.add(firstNameLabel);

		//Create the firstName textField
		firstNameTextField = new JTextField();
		firstNameTextField.setColumns(10);
		firstNameTextField.setBounds(175, 120, 308, 34);
		registerPanel.add(firstNameTextField);

		JLabel importantLabel3 = new JLabel("Duhet");
		importantLabel3.setForeground(Color.RED);
		importantLabel3.setBounds(493, 130, 100, 14);
		registerPanel.add(importantLabel3);

		// Create the phone Label
		JLabel phoneLabel = new JLabel("Telefon:");
		phoneLabel.setForeground(Color.BLUE);
		phoneLabel.setBounds(55, 169, 120, 14);
		registerPanel.add(phoneLabel);

		// Create the phone TextField
		phoneTextField = new JFormattedTextField(numberMask);
		phoneTextField.setColumns(10);
		phoneTextField.setBounds(175, 160, 308, 34);
		registerPanel.add(phoneTextField);

		JLabel importantLabel4 = new JLabel("Duhet");
		importantLabel4.setForeground(Color.RED);
		importantLabel4.setBounds(493, 170, 64, 14);
		registerPanel.add(importantLabel4);

		// Create the balance label
		JLabel balanceLabel = new JLabel("Balanca:");
		balanceLabel.setForeground(Color.BLUE);
		balanceLabel.setBounds(55, 202, 123, 30);
		registerPanel.add(balanceLabel);

		// Create the balance TextField
		balanceTextField = new JTextField();
		balanceTextField.setColumns(10);
		balanceTextField.setBounds(175, 200, 308, 34);
		registerPanel.add(balanceTextField);

		JLabel importantLabel5 = new JLabel("Duhet");
		importantLabel5.setForeground(Color.RED);
		importantLabel5.setBounds(493, 210, 100, 14);
		registerPanel.add(importantLabel5);
	
		// Create the add Button
		JButton addButton = new JButton ("Shto");
		addButton.setForeground(Color.blue);
		addButton.setBounds(175, 253, 99, 23);
		registerPanel.add(addButton);
		
		addButton.addActionListener(new ActionListener(){
			public void actionPerformed (ActionEvent event){
				// Check if all relevant fields have been filled up
				
				if (idTextField.getText().equalsIgnoreCase("") || lastNameTextField.getText().equalsIgnoreCase("")
						|| firstNameTextField.getText().equalsIgnoreCase("") ||
						phoneTextField.getText().equalsIgnoreCase("") || balanceTextField.getText().equalsIgnoreCase("")
						)
				{
					JOptionPane.showMessageDialog(null, "Ju lutem plotesoni te gjitha fushat e domosdoshme");
					
				}else if (JOptionPane.showConfirmDialog(null, "Deshironi ta shtoni kete klient?",
						"Hinzufügen Nachricht", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE)
						== JOptionPane.YES_OPTION){
					
					DefaultTableModel dtm = (DefaultTableModel) infoTable.getModel();
					
					String id = idTextField.getText();
					String name = lastNameTextField.getText();
					String firstName = firstNameTextField.getText();
					String phone = phoneTextField.getText();
					
					double balance = 0;
					
					try{
						balance = Double.parseDouble(balanceTextField.getText());
						if (balance < 0){
							JOptionPane.showMessageDialog(null, "Sasi lipset te jete nje numer positiv");
						}else{
							Client.saveClient(new Client(id, firstName, name, phone, balance));
							String actionName = "Llogari e re";
							
							// save the action in the Action History database
							ActionHistory newAction = new ActionHistory(id, firstName, name, actionName, balance);
							ActionHistory.saveAction(newAction);
							// add to the history table
							historyTableModel.insertRow(0, new Object[]{
									id, firstName, name, actionName, balance, newAction.getDate() 
							});
							
							dtm.addRow(new Object[]{id, name, firstName, phone, Double.toString(balance)});
							JOptionPane.showMessageDialog(null, "Klienti u shtua me sukses");
						}
					}
					catch (SQLException exception){
						JOptionPane.showMessageDialog(null, "Ky klient ekziston.");
					}
					catch (NumberFormatException ex){
						JOptionPane.showMessageDialog(null, "Sasia duhet te jete nje numer");
					}
				
				}
			}
		});
	
		// Create the delete field button
		JButton clearFieldButton = new JButton ("Fshi");
		clearFieldButton.setBounds(344, 253, 159, 23);
		clearFieldButton.setForeground(Color.red);
		registerPanel.add(clearFieldButton);
		
		clearFieldButton.addActionListener(new ActionListener(){
			public void actionPerformed (ActionEvent event){
				if (JOptionPane.showConfirmDialog(null, "A jeni i sigurt se doni t'i fshini te gjitha fushat?", 
						"Bestätigung", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == 
						JOptionPane.YES_OPTION){
					idTextField.setValue(null);
					lastNameTextField.setText("");
					firstNameTextField.setText("");
					phoneTextField.setValue(null);
					balanceTextField.setText("");
				}else{
					JOptionPane.showMessageDialog(null, "Nuk u be asnje ndryshim.");
				}
			}
		});

		
		return registerPanel;
	}


	/**
	 * Create the customer information panel which will contain relevant 
	 * information such as ID, name, surname about each and every customer 
	 * in a JTable.
	 * @return the panel that will display informations on the customers
	 */
	private Component createCustomerInfoPanel() { 
		
		JPanel customerInfoPanel = new JPanel();
		customerInfoPanel.setBackground(Color.LIGHT_GRAY);
		customerInfoPanel.setLayout(null);		
		
		// Create the search Field
		searchByIdField = new JTextField();
		searchByIdField.setBounds(485, 11, 129, 20);
		customerInfoPanel.add(searchByIdField);
		searchByIdField.setColumns(20);
		
		// Create the search label
		JLabel searchLabel = new JLabel("Kerko: ");
		searchLabel.setBounds(440, 11, 80, 20);
		customerInfoPanel.add(searchLabel);
		
		// Create a search by first & last name
		// The first and last name should be provided separated by a space
		searchByIdField.addActionListener(new ActionListener(){
			public void actionPerformed (ActionEvent event){
			
					try{
						// split the string into a first name and a last name
						String firstName = searchByIdField.getText().split(" ")[0];
						String lastName = searchByIdField.getText().split(" ")[1];
						if (Client.clientExists(firstName, lastName)){
							
							// get all the possible clients with this name and surname
							ArrayList<Client> clients = Client.getClient(firstName, lastName);
							for (Client client : clients)
								JOptionPane.showMessageDialog(null, "ID: " +client.getId() +
										"\nMbiemri: "+client.getSurname() + "\nEmri: "
										+ client.getName()+ "\nBalanca ne €: " + client.getBalance());
						}
						else
						{
							// Check if the client has ever existed and print an appropriate message.
							// If the client has ever existed we can find him in the ActionHistory 
							// table of the database
							ArrayList<ActionHistory> clientHistory = ActionHistory.getClientHistory(firstName, lastName);
							
							// Flag that will check if the client has indeed existed before
							boolean found = false;
							
							if (clientHistory != null){
								for (ActionHistory temp : clientHistory){
									if (temp.getActionPerformed().equals("Konto löschen")){
										JOptionPane.showMessageDialog(null, "Ky klient nuk ekziston me. Ai u fshi me: "+temp.getDate());
										found = true;
									}
								}
							}
							if (!found)
								JOptionPane.showMessageDialog(null, "Ky klient nuk ekziston");
						}
					}
					catch (SQLException exception){
						JOptionPane.showMessageDialog(null, "Error executing query: "+exception);
					}
					catch (ArrayIndexOutOfBoundsException ex){
						JOptionPane.showMessageDialog(null, "Ju lutem jepni emrin dhe mbiemrin e klientit.");
					}
				
			}	
		});
		
		// Create the table that will display the user data
		infoTable = new JTable();
		
		DefaultTableModel dtm = new DefaultTableModel(new Object[][]{}, new String[]{"ID", "Emri", "Mbiemri", "Telefoni", "Balanca(EUR)"}) {
			private static final long serialVersionUID = 4L;
		    @Override
		    public boolean isCellEditable(int row, int column) {
		       //all cells false
		       return false;
		    }
		};
		
		infoTable.setModel(dtm);
		
		ArrayList<Client> clients = null;
		try{
			clients = Client.getClients();
		}catch(SQLException ex){
			JOptionPane.showMessageDialog(null, "Error executing query: " + ex);
		}
		
		if (clients != null){
			for (Client client : clients){
				dtm.addRow(new Object []{
						client.getId(), client.getName(), client.getSurname(),
						client.getPhone(), Double.toString(client.getBalance())
				});
			}
		}
		
		JScrollPane tableScrollPane = new JScrollPane(infoTable);
		tableScrollPane.setBounds(10, 42, 604, 195);
		customerInfoPanel.add(tableScrollPane);
		
				
		return customerInfoPanel;
	}



	public static void main (String[] args) throws Exception{
		JFrame frame = new ClientManagment();
		frame.setTitle("Menaxhim Klientesh");
		frame.setSize(650, 400);
		frame.setLocationRelativeTo(null); // position to the center of the screen
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		frame.setResizable(false);
	}
}