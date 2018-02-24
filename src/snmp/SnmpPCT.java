package snmp;

import java.io.IOException;
import java.util.List;

import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.Integer32;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.Variable;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.util.DefaultPDUFactory;
import org.snmp4j.util.TreeEvent;
import org.snmp4j.util.TreeUtils;

public class SnmpPCT {
	private TransportMapping transport = null;
	private Snmp snmp = null;

	public SnmpPCT() throws IOException {
		transport = new DefaultUdpTransportMapping();
		transport.listen();
		snmp = new Snmp(transport);
	}

	public String doSnmpGet(String ip, String community, String oidValue) throws IOException {
		String errorStatusText = null;
		String result = null;
		Address address = GenericAddress.parse("udp:" + ip + "/" + "161");
		CommunityTarget comtarget = new CommunityTarget();
		comtarget.setAddress(address);
		comtarget.setVersion(SnmpConstants.version2c);
		comtarget.setRetries(2);
		comtarget.setTimeout(1000);
		comtarget.setCommunity(new OctetString(community));

		PDU pdu = new PDU();
		pdu.add(new VariableBinding(new OID(oidValue)));
		pdu.setType(PDU.GET);
		pdu.setRequestID(new Integer32(1));

		ResponseEvent response = snmp.get(pdu, comtarget);

		if (response != null) {
			PDU responsePDU = response.getResponse();
			if (responsePDU != null) {
				int errorStatus = responsePDU.getErrorStatus();
				int errorIndex = responsePDU.getErrorIndex();
				errorStatusText = responsePDU.getErrorStatusText();
				if (errorStatus == PDU.noError) {
					result = responsePDU.getVariableBindings().firstElement().getVariable().toString();
					if (result.equals("noSuchInstance")) {
						result = "";
					}
				
				}
			}
		} 
		
		else 
		{
			result = errorStatusText;
		}

		return result;
	}

	public List<TreeEvent> doSnmpWalk(String ip, String community, String oidValue) throws IOException {
		
		Address address = GenericAddress.parse("udp:" + ip + "/" + "161");
		CommunityTarget comtarget = new CommunityTarget();
	//	comtarget = new CommunityTarget();
		comtarget.setAddress(address);
		comtarget.setVersion(SnmpConstants.version2c);
		comtarget.setRetries(1);
		comtarget.setTimeout(1000);
		comtarget.setCommunity(new OctetString(community));

		TreeUtils treeUtils = new TreeUtils(snmp, new DefaultPDUFactory());
		List<TreeEvent> events = treeUtils.getSubtree(comtarget, new OID(oidValue));

		return events;
	}
}