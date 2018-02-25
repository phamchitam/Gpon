package servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.snmp4j.smi.VariableBinding;
import org.snmp4j.util.TreeEvent;

import snmp.SnmpPCT;

/**
 * Servlet implementation class Update
 */

public class Update extends HttpServlet {
	
	private static Connection conn = null;
	private static Statement stmt1 = null, stmt2 = null;
	private static ResultSet rs = null;
	int countHW, countZTE, countZTE_uplink, countHW_uplink, count = 0, count_uplink = 0;
	String oidValue = ".1.3.6.1.4.1.3902.1082.30.40.2.4.1.13";
	int ctrl = 0, pon8 = 0, pon16 = 0, ctrl_zte = 0, ctrl_hw = 0;
	int pon8_zte = 0, pon8_hw = 0, pon16_zte = 0, pon16_hw = 0;
	Pattern pattern_sfp = null;
	Matcher matcher_sfp = null;
	String regex_sfp = "285278[4|7]\\d+\\s+=\\s+([\\d|\\w])+";
	String strSQL1 = "select * from manager";
	String oid_sfp[] = { ".1.3.6.1.2.1.47.1.1.1.1.11.1764397", ".1.3.6.1.2.1.47.1.1.1.1.11.1764398",
			".1.3.6.1.2.1.47.1.1.1.1.11.1764399", ".1.3.6.1.2.1.47.1.1.1.1.11.1764400",
			".1.3.6.1.2.1.47.1.1.1.1.11.1764401", ".1.3.6.1.2.1.47.1.1.1.1.11.1764402",
			".1.3.6.1.2.1.47.1.1.1.1.11.1764403", ".1.3.6.1.2.1.47.1.1.1.1.11.1764404",
			".1.3.6.1.2.1.47.1.1.1.1.11.1764405", ".1.3.6.1.2.1.47.1.1.1.1.11.1764406",
			".1.3.6.1.2.1.47.1.1.1.1.11.1764407", ".1.3.6.1.2.1.47.1.1.1.1.11.1764408",
			".1.3.6.1.2.1.47.1.1.1.1.11.1764409", ".1.3.6.1.2.1.47.1.1.1.1.11.1764410",
			".1.3.6.1.2.1.47.1.1.1.1.11.1764411", ".1.3.6.1.2.1.47.1.1.1.1.11.1764412",
			".1.3.6.1.2.1.47.1.1.1.1.11.1764805", ".1.3.6.1.2.1.47.1.1.1.1.11.1764806",
			".1.3.6.1.2.1.47.1.1.1.1.11.1764807", ".1.3.6.1.2.1.47.1.1.1.1.11.1764808",
			".1.3.6.1.2.1.47.1.1.1.1.11.1764809", ".1.3.6.1.2.1.47.1.1.1.1.11.1764810",
			".1.3.6.1.2.1.47.1.1.1.1.11.1764811", ".1.3.6.1.2.1.47.1.1.1.1.11.1764812",
			".1.3.6.1.2.1.47.1.1.1.1.11.1764813", ".1.3.6.1.2.1.47.1.1.1.1.11.1764814",
			".1.3.6.1.2.1.47.1.1.1.1.11.1764815", ".1.3.6.1.2.1.47.1.1.1.1.11.1764816",
			".1.3.6.1.2.1.47.1.1.1.1.11.1764817", ".1.3.6.1.2.1.47.1.1.1.1.11.1764818",
			".1.3.6.1.2.1.47.1.1.1.1.11.1764819", ".1.3.6.1.2.1.47.1.1.1.1.11.1764820" };
	String card_hw[] = {".1.3.6.1.2.1.47.1.1.1.1.13.1764396",".1.3.6.1.2.1.47.1.1.1.1.13.1764804"};
	String ctrlboard_hw[] = {".1.3.6.1.2.1.47.1.1.1.1.2.1765212",".1.3.6.1.2.1.47.1.1.1.1.2.1765620"};
	String card_zte[] = {".1.3.6.1.4.1.3902.1082.10.1.2.4.1.4.1.1.1",".1.3.6.1.4.1.3902.1082.10.1.2.4.1.4.1.1.2"};
	String ctrlboard_zte[] = {".1.3.6.1.4.1.3902.1082.10.1.2.4.1.4.1.1.3",".1.3.6.1.4.1.3902.1082.10.1.2.4.1.4.1.1.4"};

       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Update() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		pattern_sfp = Pattern.compile(regex_sfp);
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
				String community = rs.getString("Community");
				String manufacture = rs.getString("Manufacture");
			
				if (manufacture.equals("ZTE")) {
//					out.println("IP: " + ip + "</br>");
					countZTE = 0;
					ctrl_zte = 0;
					pon8_zte = 0;
					pon16_zte = 0;
					
					List<TreeEvent> events = snmpobj.doSnmpWalk(ip, community, oidValue);
					for (TreeEvent event : events) {
						VariableBinding[] varBindings = event.getVariableBindings();
						if (varBindings != null) {
							for (VariableBinding varBinding : varBindings) {
								
								String tmpStr = varBinding.toString();
								matcher_sfp = pattern_sfp.matcher(tmpStr);
								if (matcher_sfp.find()) {
//									out.println(varBinding.toString() + "</br>");
									countZTE = countZTE + 1;
								}
								
							}
						}
					}
					
					
					for (int i = 0; i < ctrlboard_zte.length; i++) {
						String tmp = snmpobj.doSnmpGet(ip, community, ctrlboard_zte[i]);
//						out.println("Ctrl: " + tmp + "</br>");
						if (tmp.contains("SMXA")) {
							ctrl_zte = ctrl_zte + 1;
						};
					};
					
//					out.println("CtrlBoard: " + ctrl_zte + "</br>");
					
					for (int i = 0; i < card_zte.length; i++) {
						String tmp = snmpobj.doSnmpGet(ip, community, card_zte[i]);
						if (tmp.contains("GTGH")) {
							pon16_zte = pon16_zte + 1;
						};
						if (tmp.contains("GTGO")) {
							pon8_zte = pon8_zte + 1;
						};
						
					};
					
//					out.println("Pon8: " + pon8_zte + " . Pon16: " + pon16_zte + "</br>");
					
					count = countZTE;
					pon8 = pon8_zte;
					pon16 = pon16_zte;
					ctrl = ctrl_zte;
					
//					out.println("Count :  " + count + "</br>" );
					
				}

				if (manufacture.equals("Huawei")) {
					countHW = 0;
					pon8_hw = 0;
					pon16_hw = 0;
					ctrl_hw = 0;

//					out.println("</br> IP: " + ip + "</br>");
					for (int i = 0; i < oid_sfp.length; i++) {
						String tmp = snmpobj.doSnmpGet(ip, community, oid_sfp[i]);
						if (tmp != "") {
							countHW = countHW + 1;
//							out.println(tmp + "</br>");
						};

					};
		
					for (int i = 0; i < ctrlboard_hw.length; i++) {
						String tmp = snmpobj.doSnmpGet(ip, community, ctrlboard_hw[i]);
//						out.println("CtrlBoard: " + tmp + "</br>");
						if (tmp.contains("MCUD")) {
							ctrl_hw = ctrl_hw + 1;
						};
					};
					
//					out.println("CtrlBoard: " + ctrl_hw +"</br>");
					
					for (int i = 0; i < card_hw.length; i++) {
						String tmp = snmpobj.doSnmpGet(ip, community, card_hw[i]);
						if (tmp.contains("GPFD")) {
							pon16_hw = pon16_hw + 1;
						};
						if (tmp.contains("GPBH")) {
							pon8_hw = pon8_hw + 1;
						};
						
					};
					
//					out.println("Pon8: " + pon8_hw + " . Pon16: " + pon16_hw + "</br>");
					count = countHW;
					pon8 = pon8_hw;
					pon16 = pon16_hw;
					ctrl = ctrl_hw;
				};
				
				if (count != 0) {
					String strSQL2 = "update manager set `SfpUsed`=" + "'" + count + "', `CtrlBoard`= '" + ctrl + "',`PON8`= '" + pon8 + "', `PON16`= '" + pon16 + "'" + " where `Address`=" + "'" + ip + "'";
					stmt2.executeUpdate(strSQL2);
//					out.println(strSQL2 + "</br>");
				};

			};
			rs.close();
			stmt1.close();
			stmt2.close();
		} catch (SQLException e) {

			e.printStackTrace();
		}

		out.println("Update successful");
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
