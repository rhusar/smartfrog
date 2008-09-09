package org.smartfrog.vast.architecture.CommandDispatcher;

import org.smartfrog.vast.architecture.VirtualMachineConfig;

public class TimerThread implements Runnable {
	private VirtualMachineConfig refCfg;
	private long Time;
	private Command command;
	private boolean stopTimer = false;

	public TimerThread(VirtualMachineConfig refCfg) {
		this.refCfg = refCfg;
	}

	public void run() {
		long lastTime = System.currentTimeMillis();

		long tmpTime;
		while (true) {
			if (stopTimer) {
				stopTimer = false;
				return;
			}

			// use one timevalue to calculate with
			tmpTime = System.currentTimeMillis();

			Time -= (tmpTime - lastTime);

			if (Time <= 0)
				break;

			lastTime = tmpTime;

			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
			}
		}

		command.timeOut(refCfg);
	}

	/**
	 * Stops the timer.
	 */
	public void stopTimer() {
		stopTimer = true;
	}

	public Command getCommand() {
		return command;
	}

	public void setCommand(Command command) {
		this.command = command;
	}

	public long getTime() {
		return Time;
	}

	/**
	 * Set the timer.
	 * @param time Time in miliseconds.
	 */
	public void setTime(long time) {
		Time = time;
	}
}
