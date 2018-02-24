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
public class CountGpon extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Connection conn = null;
	private static Statement stmt1 = null, stmt2 = null;
	private static ResultSet rs = null;
	private static String oid_zte = ".1.3.6.1.4.1.3902.1082.500.10.2.3.3.1.6";
	private static String oid_hw = ".1.3.6.1.4.1.2011.6.128.1.1.2.43.1.3";
	private String oid = null;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public CountGpon() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see Servlet#init(ServletConfig)
	 */
	/*
	 * public void init(ServletConfig config) throws ServletException { try {
	 * Class.forName("com.mysql.jdbc.Driver"); conn =
	 * DriverManager.getConnection("jdbc:mysql://localhost:3306/gpon", "gpon",
	 * "gpon_vnpt"); stmt1 = conn.createStatement(); stmt2 = conn.createStatement();
	 * } catch (SQLException e) { e.printStackTrace(); } catch
	 * (ClassNotFoundException e) { // TODO Auto-generated catch block
	 * e.printStackTrace(); } }
	 */
	/**
	 * @see Servlet#destroy()
	 */

	/*
	 * public void destroy() { try { rs.close(); stmt1.close(); stmt2.close(); }
	 * catch (SQLException e) { // TODO Auto-generated catch block
	 * e.printStackTrace(); } }
	 */
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		int count;
		String group = request.getParameter("group");
		String strSQL1 = "select Address,Community,Manufacture from manager where `Group`=" + "'" + group + "'";
		SnmpPCT snmpobj = new SnmpPCT();

		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://10.96.36.10:3306/gpon", "gpon", "gpon_vnpt");
			stmt1 = conn.createStatement();
			stmt2 = conn.createStatement();
			rs = stmt1.executeQuery(strSQL1);
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		try {
			while (rs.next()) {
				String ip = rs.getString("Address");
//				String oidValue = rs.getString("Oid");
				String community = rs.getString("Community");
				String manufacture = rs.getString("Manufacture");
				if (manufacture.equals("ZTE")) {
					oid = oid_zte;
				}
				else {
					oid = oid_hw;
				}
				
				List<TreeEvent> events = snmpobj.doSnmpWalk(ip, community, oid);
				count = 0;

				for (TreeEvent event : events) {

					VariableBinding[] varBindings = event.getVariableBindings();
					if (varBindings != null) {
						int tmp = varBindings.length;
						count = count + tmp;
					}
				}

				if (count != 0) {
					String strSQL2 = "update manager set `Used`=" + "'" + count + "'" + " where `Address`=" + "'" + ip + "'";
					stmt2.executeUpdate(strSQL2);
				}

			}
			rs.close();
			stmt1.close();
			stmt2.close();
			conn.close();
		} catch (SQLException e) {

			e.printStackTrace();
		}

		out.println("Successful");

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