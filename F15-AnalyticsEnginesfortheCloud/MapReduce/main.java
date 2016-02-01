import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

import org.apache.hadoop.io.Text;


public class main {

	public static void main(String[] args) {
		String line = "gooasdfasd	100";

		// parse string
		String[] v = line.split("\t");
		String word = v[0];
		String vCount = line.replace("\t", ":");
		
		for (int i=1; i<word.length(); i++) {
			String pre = word.substring(0, i);
			Text p = new Text(pre);
			Text w = new Text(vCount);
			System.out.print("p:  "+ p.toString());
			System.out.println(" w: " + w.toString());
		}
	}
}
