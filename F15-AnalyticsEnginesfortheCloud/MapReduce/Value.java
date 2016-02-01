
public class Value implements Comparable<Value> {
	private String word;
	private int count;

	public Value(String word, int count) {
		this.word = word;
		this.count = count;
	}

	@Override
	public int compareTo(Value o) {
		if (o.count != count) {
			return o.count - count;
		}
		return word.compareTo(o.word);
	}

	public String toString() {
		return word;
	}
}
