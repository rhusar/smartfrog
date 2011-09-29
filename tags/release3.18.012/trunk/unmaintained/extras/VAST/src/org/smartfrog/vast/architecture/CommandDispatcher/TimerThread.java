package org.smartfrog.vast.architecture.CommandDispatcher;

import org.smartfrog.vast.architecture.VirtualMachineConfig;

public class TimerThread implements Runnable {
	private VirtualMachineConfig refCfg;
	private long Time;
	private Command command;
	private boolean stopTimer = false,
					running = false;

	public TimerThread(VirtualMachineConfig refCfg) {
		this.refCfg = refCfg;
	}

	public void run() {
		System.out.println(this + " started");
		running = true;
		long lastTime = System.currentTimeMillis();

		long tmpTime;
		while (true) {
			if (stopTimer) {
				stopTimer = false;
				running = false;
				System.out.println(this + " stopped");
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
				running = false;
				return;
			}
		}

		command.timeOut(refCfg);
	}

	/**
	 * Stops the timer.
	 */
	public void stopTimer() {
		if (running)
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
