package ua.dp.skillsup;


import org.openjdk.jmh.annotations.*;
import ua.dp.skillsup.counter.Counter;
import ua.dp.skillsup.counter.CounterFactory;

@State(Scope.Group)
@Fork(1)
public class CountersBenchmark {
	private Counter counter;

	@Param
	CounterFactory.CounterType counterType;

	@Setup
	public void buildMeCounterHearty() {
		counter = CounterFactory.build(counterType);
	}

	@Benchmark
	@Group("rw")
	@GroupThreads(8)
	public void inc() {
		counter.inc();
	}

	@Benchmark
	@Group("rw")
	@GroupThreads(1)
	public long get() {
		return counter.get();
	}
}