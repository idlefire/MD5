import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;

public class md5encode extends JFrame implements ActionListener {

	private static int[] message = new int[16];
	private static int[] key = new int[16];
	private static int[] keyTemp = new int[16];

	// 显示文本区
	JTextArea jta = new JTextArea(24, 34);
	// 设置文本区的滚动条
	int vertical = ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED;
	int horizontal = ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED;
	JScrollPane jsp = new JScrollPane(jta, vertical, horizontal);
	// 按钮
	JButton encryptButton = new JButton("生成摘要"),
			emptyButton = new JButton("清空");
	// 标签
	JLabel jlable1 = new JLabel("明文消息：", SwingConstants.LEFT),
		   jlable3 = new JLabel("摘要消息：",SwingConstants.LEFT),
		   jlable4 = new JLabel("摘要生成过程：",SwingConstants.LEFT),
	       msgTitle = new JLabel("MD5哈希算法", SwingConstants.CENTER);
	// 文本域
	JTextField inputMsg = new JTextField(25),
			   outputMsg = new JTextField(25);

	public md5encode(String title) {
		super(title);
		// 添加面板内容
		Container cp = this.getContentPane();
		cp.setLayout(new FlowLayout());

		JPanel jPanel_1 = new JPanel(new GridLayout(7, 1));
		JPanel jPanel_2 = new JPanel(new GridLayout(1, 3));
		JPanel jPanel_3 = new JPanel(new BorderLayout());
		JPanel jPanel_4 = new JPanel(new GridLayout(1, 1));

		jPanel_1.add(msgTitle);
		jPanel_1.add(jlable1);
		jPanel_1.add(inputMsg);
		jPanel_1.add(jlable3);
		jPanel_1.add(outputMsg);

		jPanel_2.add(encryptButton);
		jPanel_2.add(emptyButton);

		jPanel_3.add(jlable4, BorderLayout.NORTH);
		jPanel_3.add(jsp, BorderLayout.CENTER);




		cp.add(jPanel_1);
		cp.add(jPanel_2);
		cp.add(jPanel_3);
		cp.add(jPanel_4);

		// 添加事件监听器
		encryptButton.addActionListener(this);
		emptyButton.addActionListener(this);

		this.setSize(400, 710);
		//this.setLocation(200, 200);
		this.setVisible(true);
		this.setResizable(false);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public static void main(String[] args) {
		new md5encode("md5哈希摘要生成程序");
	}
	//点击事件的处理
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		JButton eventSource = (JButton) e.getSource();

		if(eventSource==encryptButton){
			String inputString = inputMsg.getText().trim();
			if(inputString.isEmpty())
			{
				JOptionPane.showMessageDialog(null, "明文消息不能为空", "ERROR", JOptionPane.ERROR_MESSAGE);
			}
			else {
				Javamd5 output = new Javamd5();
				String md5 = output.md5encode(inputString);
				outputMsg.setText(md5.split("~")[0]);
				jta.setText(md5.split("~")[1]);
			}
		}

		if(eventSource==emptyButton){
			inputMsg.setText("");
			outputMsg.setText("");
			jta.setText("");
		}

	}
}
