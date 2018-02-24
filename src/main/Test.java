package main;

import java.io.IOException;
import java.util.List;

import org.snmp4j.smi.VariableBinding;
import org.snmp4j.util.TreeEvent;

import snmp.SnmpPCT;

public class Test {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		String ip = "10.96.53.43";
		String oidValue = ".1.3.6.1.4.1.3902.1082.30.40.2.4.1.13";
		String community = "Gpon#2015";
		SnmpPCT snmpobj = new SnmpPCT();
		List<TreeEvent> events = snmpobj.doSnmpWalk(ip, community, oidValue);
		if (events == null) {
			System.out.println("Null !");
		} else {
			for (TreeEvent event : events) {
				VariableBinding[] varBindings = event.getVariableBindings();
				if (varBindings != null) {
					int tmp = varBindings.length;

					for (VariableBinding varBinding : varBindings) {
						System.out.println(varBinding.toString());
					}
				}
			}

		}
	}

}
