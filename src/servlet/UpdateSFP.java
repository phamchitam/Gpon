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
import telnet.TelnetPCT;

/**
 * Servlet implementation class MyServlet
 */
public class UpdateSFP extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Connection conn = null;
	private static Statement stmt1 = null, stmt2 = null;
	private static ResultSet rs = null;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public UpdateSFP() {
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
	}
*/
	/**
	 * @see Servlet#destroy()
	 */
	/*public void destroy() {
		try {
			rs.close();
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
		int countHW, countZTE, count = 0;
		String oidValue;
		
		String prompt = "#";
		String resultStr;

		
		String strSQL1 = "select * from manager";
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

				String community = rs.getString("Community");
				String manufacture = rs.getString("Manufacture");

				if (manufacture.equals("ZTE")) {
					countZTE = 0;
					TelnetPCT telnetobj = new TelnetPCT(ip, 23, prompt);
					telnetobj.readUntil("Username:");
					telnetobj.write("ftthcamau");
					telnetobj.readUntil("Password:");
					telnetobj.write("ftthcamau");
					telnetobj.readUntil(prompt);
					for (int j = 1; j < 3; j++) {
						for (int i = 1; i < 17; i++) {
							resultStr = telnetobj
									.sendCommand("show interface optical-module-info gpon-olt_1/" + j + "/" + i);

							if (resultStr.contains("Wavelength     : 1490")) {
								countZTE = countZTE + 1;
							}
							Thread.sleep(50);
						}
					}
					count = countZTE;
					telnetobj.disconnect();
				}

				if (manufacture.equals("Huawei")) {
					oidValue = ".1.3.6.1.2.1.47.1.1.1.1.7";
					SnmpPCT snmpobj = new SnmpPCT();
					List<TreeEvent> events = snmpobj.doSnmpWalk(ip, community, oidValue);
					countHW = 0;
					for (TreeEvent event : events) {
						VariableBinding[] varBindings = event.getVariableBindings();
						if (varBindings != null) {
							for (VariableBinding varBinding : varBindings) {
								String tmpStr = varBinding.getVariable().toString();
								if (tmpStr.matches("GPON_UNI")) {
									countHW = countHW + 1;
								}
							}
						}
					}
					count = countHW;

				}

				if (count != 0){
				String strSQL2 = "update manager set `SfpUsed`=" + "'" + count + "'" + " where `Address`=" + "'" + ip
						+ "'";
				stmt2.executeUpdate(strSQL2);
				}
				
			}
				rs.close();
				stmt1.close();
				stmt2.close();
				conn.close();
		} catch (SQLException | InterruptedException e) {

			e.printStackTrace();
		}

		out.println("Update SFP successful");
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