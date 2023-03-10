/* ###
 * IP: GHIDRA
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ghidra.app.plugin.core.functiongraph.graph.layout;

import javax.swing.Icon;

import generic.theme.GIcon;

public abstract class ExperimentalLayoutProvider extends FGLayoutProviderExtensionPoint {

	private static final Icon ICON = new GIcon("icon.plugin.functiongraph.layout.experimental");

	@Override
	public Icon getActionIcon() {
		return ICON;
	}

	@Override
	public int getPriorityLevel() {
		return -100; // below the others
	}
}
