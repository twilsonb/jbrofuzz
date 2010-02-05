/**
 * JBroFuzz 1.9
 *
 * JBroFuzz - A stateless network protocol fuzzer for web applications.
 * 
 * Copyright (C) 2007 - 2010 subere@uncon.org
 *
 * This file is part of JBroFuzz.
 * 
 * JBroFuzz is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * JBroFuzz is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with JBroFuzz.  If not, see <http://www.gnu.org/licenses/>.
 * Alternatively, write to the Free Software Foundation, Inc., 51 
 * Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 * 
 * Verbatim copying and distribution of this entire program file is 
 * permitted in any medium without royalty provided this notice 
 * is preserved. 
 * 
 */
package org.owasp.jbrofuzz.ui.prefs;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JCheckBox;

import org.owasp.jbrofuzz.JBroFuzz;
import org.owasp.jbrofuzz.version.JBroFuzzFormat;

public class FuzzPPanel extends AbstractPrefsPanel {

	private static final long serialVersionUID = -6307578507750425289L;

	public FuzzPPanel(final PrefDialog dialog) {
		super("Fuzzing");
		
		
		// Fuzzing... -> Socket Timeout

		final boolean socketbox = JBroFuzz.PREFS.getBoolean(JBroFuzzFormat.PR_FUZZ_1,
				false);
		final JCheckBox socketCheckBox = new JCheckBox(
				" Extend the socket timeout from 5 seconds to 30 seconds ",
				socketbox);

		socketCheckBox.setBorderPaintedFlat(true);
		socketCheckBox
		.setToolTipText("Tick this box, if you are getting timeout responses");

		socketCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				if (socketCheckBox.isSelected()) {
					JBroFuzz.PREFS.putBoolean(JBroFuzzFormat.PR_FUZZ_1, true);
				} else {
					JBroFuzz.PREFS.putBoolean(JBroFuzzFormat.PR_FUZZ_1, false);
				}
			}
		});
		add(socketCheckBox);
		add(Box.createRigidArea(new Dimension(0, 20)));

		// Fuzzing... -> End of Line Character

		final boolean endlinebox = JBroFuzz.PREFS.getBoolean(JBroFuzzFormat.PR_FUZZ_2,
				false);
		final JCheckBox endlineCheckBox = new JCheckBox(
				" Use \"\\n\" instead of \"\\r\\n\" as an end of line character ",
				endlinebox);

		endlineCheckBox.setBorderPaintedFlat(true);
		endlineCheckBox
		.setToolTipText("Tick this box, if you want to use \"\\n\" for each line put on the wire");

		endlineCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				if (endlineCheckBox.isSelected()) {
					JBroFuzz.PREFS.putBoolean(JBroFuzzFormat.PR_FUZZ_2, true);
				} else {
					JBroFuzz.PREFS.putBoolean(JBroFuzzFormat.PR_FUZZ_2, false);
				}
			}
		});
		add(endlineCheckBox);
		add(Box.createRigidArea(new Dimension(0, 20)));


		// Fuzzing... -> Word wrap request text panel

		final boolean wrap_req_box = JBroFuzz.PREFS.getBoolean(
				JBroFuzzFormat.WRAP_REQUEST, false);
		final JCheckBox wrap_req_check_box = new JCheckBox(
				" Word wrap text in the \"Request\" area (requires restart) ",
				wrap_req_box);
		wrap_req_check_box.setBorderPaintedFlat(true);
		wrap_req_check_box
		.setToolTipText("If ticked, the request text area will wrap the text to fit the size of the area");

		wrap_req_check_box.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				if (wrap_req_check_box.isSelected()) {
					JBroFuzz.PREFS.putBoolean(JBroFuzzFormat.WRAP_REQUEST, true);
				} else {
					JBroFuzz.PREFS.putBoolean(JBroFuzzFormat.WRAP_REQUEST, false);
				}
			}
		});

		add(wrap_req_check_box);
		add(Box.createRigidArea(new Dimension(0, 20)));

		// Fuzzing... -> Word wrap response text panel

		final boolean wrap_res_bool = JBroFuzz.PREFS.getBoolean(
				JBroFuzzFormat.WRAP_RESPONSE, false);
		final JCheckBox wrap_res_check_box = new JCheckBox(
				" Word wrap text in the \"Response\" window (requires restart) ",
				wrap_res_bool);
		wrap_res_check_box.setBorderPaintedFlat(true);
		wrap_res_check_box
		.setToolTipText("Tick this box, to see all output text wrapped to the size of the response window");

		wrap_res_check_box.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				if (wrap_res_check_box.isSelected()) {
					JBroFuzz.PREFS.putBoolean(JBroFuzzFormat.WRAP_RESPONSE, true);
				} else {
					JBroFuzz.PREFS.putBoolean(JBroFuzzFormat.WRAP_RESPONSE, false);
				}
			}
		});

		add(wrap_res_check_box);
		add(Box.createRigidArea(new Dimension(0, 20)));

		// Fuzzing ...-> "Re-send POST Data if 100 Continue is received"
		final boolean cont_bool = JBroFuzz.PREFS.getBoolean(JBroFuzzFormat.PR_FUZZ_4,
				true);
		final JCheckBox cont_check_box = new JCheckBox(
				"Re-send POST Data if 100 Continue is received", cont_bool);
		cont_check_box.setBorderPaintedFlat(true);
		cont_check_box
		.setToolTipText("Tick this box, to re-send the POST Data in a HTTP/1.1 message, if a 100 continue is received");

		cont_check_box.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent event5) {
				if (cont_check_box.isSelected()) {
					JBroFuzz.PREFS.putBoolean(JBroFuzzFormat.PR_FUZZ_4, true);
				} else {
					JBroFuzz.PREFS.putBoolean(JBroFuzzFormat.PR_FUZZ_4, false);
				}
			}
		});

		add(cont_check_box);
		add(Box.createRigidArea(new Dimension(0, 20)));

		
		
	}
	
	public void apply() { }

}
