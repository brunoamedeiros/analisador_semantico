package semantico;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Label;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Stack;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

public class Pane {

	private JFrame frame;
	public static Stack<Integer> stack = new Stack<Integer>();
	public Lexico lexico = new Lexico();

	String column_names1[] = { "#", "Instruction", "Op1", "Op2" };
	DefaultTableModel tableModel1 = new DefaultTableModel(column_names1, 0);

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Pane window = new Pane();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 * 
	 * @throws IOException
	 */
	public Pane() throws IOException {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 * 
	 * @throws IOException
	 */
	private void initialize() throws IOException {
		frame = new JFrame();
		frame.setResizable(false);
		frame.setTitle("TRADUTOR PARA A LINGUAGEM LMS");
		frame.setBounds(100, 100, 1068, 784);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		Label fileLabel = new Label("File: ");
		fileLabel.setBounds(10, 10, 650, 21);
		frame.getContentPane().add(fileLabel);

		JTextPane textPane = new JTextPane();
		textPane.setFont(new Font("Courier New", Font.PLAIN, 14));
		LineNumbersView lineNumbers = new LineNumbersView(textPane);

		JScrollPane scrollPane = new JScrollPane(textPane);
		scrollPane.setBounds(10, 37, 650, 530);
		scrollPane.setRowHeaderView(lineNumbers);

		frame.getContentPane().add(scrollPane);

		JTextArea textArea = new JTextArea();
		textArea.setWrapStyleWord(true);
		textArea.setEditable(false);
		textArea.setFont(new Font("Courier New", Font.PLAIN, 14));
		textArea.setForeground(Color.RED);
		textArea.setBounds(10, 597, 1025, 110);
		frame.getContentPane().add(textArea);

		Label consoleLabel = new Label("Console");
		consoleLabel.setBounds(10, 573, 59, 21);
		frame.getContentPane().add(consoleLabel);

		String column_names[] = { "ID", "Lexeme", "Position" };
		DefaultTableModel tableModel = new DefaultTableModel(column_names, 0);

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(670, 37, 373, 530);
		frame.getContentPane().add(tabbedPane);
		JTable table = new JTable(tableModel);

		table.getColumnModel().getColumn(0).setPreferredWidth(10);
		table.getColumnModel().getColumn(1).setPreferredWidth(120);
		table.getColumnModel().getColumn(2).setPreferredWidth(10);

		JScrollPane scrollPane_1 = new JScrollPane();
		tabbedPane.addTab("Tokens", null, scrollPane_1, null);
		scrollPane_1.setViewportView(table);
		JTable table1 = new JTable(tableModel1);

		table1.getColumnModel().getColumn(0).setPreferredWidth(10);

		JScrollPane scrollPane_2 = new JScrollPane();
		tabbedPane.addTab("Intermediate Code", null, scrollPane_2, null);
		scrollPane_2.setViewportView(table1);

		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);

		JFileChooser chooser = new JFileChooser();

		FileNameExtensionFilter filter = new FileNameExtensionFilter("Text Files", "txt");
		chooser.setFileFilter(filter);

		JButton btnExec = new JButton("Exec");

		JButton btnOpen = new JButton("Open");
		menuBar.add(btnOpen);

		btnOpen.addActionListener(ev -> {
			int returnVal = chooser.showOpenDialog(frame);
			btnExec.setEnabled(false);

			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = chooser.getSelectedFile();
				fileLabel.setText("File: " + file.getName());

				try {
					BufferedReader input = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
					textPane.read(input, "Lendo arquivo :)");
					textPane.setText(textPane.getText().toUpperCase());
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				textArea.setText("Opera��o cancelada :(");
			}
		});

		JButton btnCompile = new JButton("Compile");
		menuBar.add(btnCompile);

		btnCompile.addActionListener(ev -> {
			stack.clear();

			textPane.setText(textPane.getText().toUpperCase());
			textArea.setForeground(Color.RED);

			String code = textPane.getText();

			if (code.trim().length() != 0) {
				lexico.setInput(code);
				tableModel.setRowCount(0);
				tableModel1.setRowCount(0);
				textArea.setText("");

				Token t = null;
				try {
					while ((t = lexico.nextToken()) != null) {
						tableModel.addRow(new Object[] { t.getId(), t.getLexeme(), t.getPosition() });
					}

					lexico.setInput(code);

					stack.push(Constants.DOLLAR);
					stack.push(Constants.START_SYMBOL);

					Token previousToken = null;
					Token currentToken = lexico.nextToken();

					while (!stack.empty()) {

						if (currentToken == null) {
							int pos = 0;
							if (previousToken != null)
								pos = previousToken.getPosition() + previousToken.getLexeme().length();

							currentToken = new Token(Constants.DOLLAR, "$", pos);
						}

						int X = stack.pop(); // topo da pilha
						int a = currentToken.getId(); // pr�ximo s�mbolo da entrada

						if (X != Constants.EPSILON) {

							if (isTerminal(X)) {

								if (X == a) {
									previousToken = currentToken;
									currentToken = lexico.nextToken();
								} else {
									throw new Error("Erro sint�tico encontrado em um s�mbolo terminal: "
											+ ParserConstants.PARSER_ERROR[X] + " no token: " + currentToken.getId()
											+ " no lexema: " + currentToken.getLexeme() + " na posi��o: "
											+ currentToken.getPosition());
								}
							} else if (isNaoTerminal(X)) {

								// Ap�s a instru��o 76 <RPINTEIRO> armazena na pilha o integer de cada ramo do
								// case, para posterior marca��o.
								if (X == 76) {
									int number = Integer.parseInt(previousToken.getLexeme());
									AnalisadorSemantico.addIntegerCase(number);
								}

								if (!producao(X, a)) {
									throw new Error("Erro sint�tico encontrado em um s�mbolo n�o terminal: "
											+ ParserConstants.PARSER_ERROR[X] + " no token: " + currentToken.getId()
											+ " no lexema: " + currentToken.getLexeme() + " na posi��o: "
											+ currentToken.getPosition());
								}
							} else if (isSemantico(X)) {
								AnalisadorSemantico.geraInstrucoes(X, previousToken);
							}

						}
					}

					showCodigoIntermediario();

					textArea.setForeground(new Color(0, 128, 0));
					textArea.setText("Nenhum erro encontrado, boa!");
					btnExec.setEnabled(true);

				} catch (LexicalError e) {
					textArea.setText("Erro l�xico: " + e.getMessage() + ", na posi��o " + e.getPosition());
				} catch (Error e) {
					textArea.setText(e.getMessage());
				}
			} else {
				textArea.setText("Nenhum c�digo foi inserido, tente novamente!");
			}
		});

		btnExec.setEnabled(false);
		menuBar.add(btnExec);

		btnExec.addActionListener(ev -> {
			AnalisadorSemantico.run();
		});
	}

	private void showCodigoIntermediario() {
		ArrayList<Object[]> instrucoes = AnalisadorSemantico.show();

		for (Object[] row : instrucoes) {
			tableModel1.addRow(row);
		}
	}

	public static boolean isTerminal(int X) {
		return X < Constants.FIRST_NON_TERMINAL;
	}

	public static boolean isNaoTerminal(int X) {
		return X >= Constants.FIRST_NON_TERMINAL && X < Constants.FIRST_SEMANTIC_ACTION;
	}

	public static boolean isSemantico(int X) {
		return X >= Constants.FIRST_SEMANTIC_ACTION;
	}

	public static boolean producao(int X, int a) {
		int parseTableIndex = ParserConstants.PARSER_TABLE[X - ParserConstants.FIRST_NON_TERMINAL][a - 1];

		if (parseTableIndex >= 0) {
			int[] production = ParserConstants.PRODUCTIONS[parseTableIndex];
			reverseStack(production);

			return true;
		}

		return false;

	}

	public static void reverseStack(int[] array) {

		for (int i = array.length - 1; i >= 0; i--) {
			stack.push(array[i]);
		}
	}

	public static Stack<Token> reverseStack(Stack<Token> tokens) {

		Stack<Token> reversedStack = new Stack<Token>();

		for (int i = tokens.size() - 1; i >= 0; i--) {
			reversedStack.push(tokens.get(i));
		}

		return reversedStack;
	}
}
