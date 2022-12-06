package ie.atu.sw.os.data;

import java.util.LinkedList;
import java.util.List;

interface Doable{
	public abstract void doStuff();
}
class A implements Doable{
	@Override
	public void doStuff() {
	}
}
class B implements Doable{
	@Override
	public void doStuff() {
	}
}


public class TestClass {
	
	private List<A> listA = new LinkedList<>();
	private List<B> listB = new LinkedList<>();
	
	public void populateList(List<? extends Doable> list) {
	//	list.add(new A());
	}
	
	public static void main() {
		TestClass tc = new TestClass();
	//	tc.populateList(listA);
	}
}


