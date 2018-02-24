package servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.snmp4j.smi.VariableBinding;
import org.snmp4j.util.TreeEvent;

import snmp.SnmpPCT;

/**
 * Servlet implementation class MyServlet
 */
public class Thongke extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Connection conn = null;
	private static Statement stmt1 = null, stmt2 = null;
	private static ResultSet rs1 = null, rs2 = null;
	private static String oid_zte = ".1.3.6.1.4.1.3902.1082.500.10.2.3.3.1.6";
	private static String oid_hw = ".1.3.6.1.4.1.2011.6.128.1.1.2.43.1.3";
	private String oid = null;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Thongke() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see Servlet#init(ServletConfig)
	 */
	/*public void init(ServletConfig config) throws ServletException {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/gpon", "gpon", "gpon_vnpt");
			stmt1 = conn.createStatement();
			stmt2 = conn.createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}*/

	/**
	 * @see Servlet#destroy()
	 */
	/*public void destroy() {
		try {
			rs1.close();
			rs2.close();
			stmt1.close();
			stmt2.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}*/

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		PrintWriter out = response.getWriter();

		String strSQL1 = "select Address,Community from manager where `Manufacture`=" + "'ZTE'";
		int count1 = 0;
		int count2 = 0;
		SnmpPCT snmpobj = new SnmpPCT();

		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://10.96.36.10:3306/gpon", "gpon", "gpon_vnpt");
			stmt1 = conn.createStatement();
			stmt2 = conn.createStatement();
			rs1 = stmt1.executeQuery(strSQL1);
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		try {
			while (rs1.next()) {
				String ip = rs1.getString("Address");
//				String oidValue = rs1.getString("Oid");
				String community = rs1.getString("Community");
				
				List<TreeEvent> events = snmpobj.doSnmpWalk(ip, community, oid_zte);

				for (TreeEvent event : events) {
					VariableBinding[] varBindings = event.getVariableBindings();
					if (varBindings != null) {
						int tmp = varBindings.length;
						count1 = count1 + tmp;
					}
				}

			}
			rs1.close();
			stmt1.close();
		} catch (SQLException e) {

			e.printStackTrace();
		}

		out.println("ONT ZTE: " + count1 + "</br>");

		String strSQL2 = "select Address,Community from manager where `Manufacture`= 'Huawei'";
		try {
			rs2 = stmt2.executeQuery(strSQL2);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			while (rs2.next()) {
				String ip = rs2.getString("Address");
				String community = rs2.getString("Community");
//				String oidValue = rs2.getString("Oid");
				List<TreeEvent> events = snmpobj.doSnmpWalk(ip, community, oid_hw);
				for (TreeEvent event : events) {
					VariableBinding[] varBindings = event.getVariableBindings();
					if (varBindings != null) {
						int tmp = varBindings.length;
						count2 = count2 + tmp;
					}
				}
			}
			rs2.close();
			stmt2.close();
			conn.close();
		} catch (SQLException e) {

			e.printStackTrace();
		}

		out.println("ONT Huawei: " + count2);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}