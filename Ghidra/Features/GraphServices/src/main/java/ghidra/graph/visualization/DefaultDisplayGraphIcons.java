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
package ghidra.graph.visualization;

import javax.swing.Icon;

import generic.theme.GIcon;
import resources.Icons;

/**
 * Holds the icons used by the DefaultProgramGraph toolbars and menus.
 */
final class DefaultDisplayGraphIcons {

	private DefaultDisplayGraphIcons() {
	}

	//@formatter:off
	public static final Icon SATELLITE_VIEW_ICON =  new GIcon("icon.graph.default.display.satellite.view");
	public static final Icon VIEW_MAGNIFIER_ICON =  new GIcon("icon.graph.default.display.view.magnifier");
	public static final Icon PROGRAM_GRAPH_ICON =  new GIcon("icon.graph.default.display.program.graph");
	public static final Icon LAYOUT_ALGORITHM_ICON =  new GIcon("icon.graph.default.display.layout.algorithm");
	public static final Icon LASSO_ICON =  new GIcon("icon.graph.default.display.lasso");
	public static final Icon FILTER_ICON = Icons.CONFIGURE_FILTER_ICON;
	public static final Icon FIT_TO_WINDOW = new GIcon("icon.graph.default.display.fit.to.window");
	//@formatter:on

}
