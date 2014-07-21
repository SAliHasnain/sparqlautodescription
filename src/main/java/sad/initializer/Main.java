package sad.initializer;


import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sad.conceiver.Conceiver;
import sad.endpoints.provider.impl.DataHUB;
import sad.paramhandler.impl.Manipulator;

/**
 * 
 * @author qaiser.mehmood@insight-centre.org
 */

public class Main {
	static final Logger logger = LoggerFactory.getLogger("Main");

	final static Set<Conceiver> conc = new HashSet<Conceiver>();
	static { // open to add other configurations and classes like DataHub

		conc.add(new Conceiver(new Manipulator(new QryConfigurations(),
				new DataHUB())));

	}

	
	/*
	 * main entry point for the sad application
	 */
	public static void main(String[] args) {
		new Main().progStartPoint();

	}

	private void progStartPoint() {

		Scanner scanner = new Scanner(System.in);
		System.out
				.println("Enter 1 to start queries test \nEnter 2 to start file writing \nEnter any other integer value to exit the system");

		int i = scanner.nextInt();

		switch (i) {
		case 1:
			for (Conceiver con : conc) {
				con.execQryOverEndpointsStream();
			}

		default:
			System.exit(0);
			scanner.close();

		}

	}
}
