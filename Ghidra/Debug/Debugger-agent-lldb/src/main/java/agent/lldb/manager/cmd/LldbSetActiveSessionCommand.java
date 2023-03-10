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
package agent.lldb.manager.cmd;

import SWIG.SBDebugger;
import SWIG.SBTarget;
import agent.lldb.lldb.DebugClientImpl;
import agent.lldb.manager.impl.LldbManagerImpl;

public class LldbSetActiveSessionCommand extends AbstractLldbCommand<Void> {
	private SBTarget session;

	/**
	 * Set the active session
	 * 
	 * @param manager the manager to execute the command
	 * @param process the desired process
	 */
	public LldbSetActiveSessionCommand(LldbManagerImpl manager, SBTarget session) {
		super(manager);
		this.session = session;
	}

	@Override
	public void invoke() {
		DebugClientImpl client = (DebugClientImpl) manager.getClient();
		SBDebugger debugger = client.getDebugger();
		if (!client.isSessionImmutable()) {
			debugger.SetSelectedTarget(session);
		}
	}
}
