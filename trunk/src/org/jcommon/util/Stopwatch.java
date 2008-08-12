package org.jcommon.util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Stopwatch {
	private long start;
	private long prior;
	private List<Long> laps;
	
	public Stopwatch() {
		this.reset();
	}
	
	public void reset() {
		this.start = System.nanoTime();
		this.prior = this.start;
		this.laps = new ArrayList<Long>();
	}
	
	public long mark() {
		long mark = System.nanoTime();
		long elapsed = mark - prior;
		this.laps.add(elapsed);
		prior = mark;
		return elapsed;
	}
	
	public long mark(TimeUnit t) {
		return t.convert(this.mark(), TimeUnit.NANOSECONDS); 
	}
	
	public Long getLap(TimeUnit unit) {
		Long time = null;
		if (this.laps!=null) {
			time = this.getLap(this.laps.size()-1,unit);
		}
		return time;
	}
	
	public long getLap(int lap,TimeUnit unit) {
		Long time = null;
		if ((laps != null) && (laps.size()>lap)) {
			time = unit.convert(laps.get(lap),TimeUnit.NANOSECONDS);
		}
		return time;
	}
	
	public List<Long> getLaps(TimeUnit unit) {
		List<Long> laps = new ArrayList<Long>(); 
		if (this.laps != null){
			for (Long lap : this.laps) {
				laps.add(unit.convert(lap, TimeUnit.NANOSECONDS));
			}
		}
		return laps;
	}
	
	public Long getAverage(TimeUnit unit) {
		Long avg = null;
		if (this.laps != null) {
			long elapsed = unit.convert(this.getElapsed(), TimeUnit.NANOSECONDS);
			avg = Math.round(Double.valueOf(elapsed)/this.laps.size());
		}
		return avg;
	}
	
	public Long getElapsed() {
		return this.getElapsed(TimeUnit.NANOSECONDS);
	}
	
	public Long getElapsed(TimeUnit unit) {
		long total = 0;
		for (Long lap : this.laps) {
			total += lap;
		}
		return unit.convert(total,TimeUnit.NANOSECONDS);
	}
}
