import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.hbase.mapreduce.TableReducer;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

public class LanguageModel {


	public static class TokenizerMapper extends
			Mapper<Object, Text, Text, Text> {

		@Override
		public void map(Object key, Text value, Context context)
				throws IOException, InterruptedException {
			String line = value.toString().trim();

			int endIndex = line.lastIndexOf(" ");

			// we don't care single word
			if (endIndex == -1) {
				return;
			}

			// parse string phrase\tcount => phrase:count
			String phrase = line.substring(0, endIndex);
			String words = line.replace("\t", ":");
			Text p = new Text(phrase.trim());
			Text w = new Text(words.trim());

			// write to output
			context.write(p, w);

		}
	}

	public static class Value implements Comparable<Value> {
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

	public static class StringCastReducer extends
			TableReducer<Text, Text, ImmutableBytesWritable> {
		private Configuration conf;
		private int n;

		@Override
		public void setup(Context context) throws IOException,
				InterruptedException {
			
			// set top n parameter
			conf = context.getConfiguration();
			n = conf.getInt("n", 5);
		}

		@Override
		public void reduce(Text key, Iterable<Text> values, Context context)
				throws IOException, InterruptedException {
			PriorityQueue<Value> valueQueue = new PriorityQueue<Value>();
			// read value from values and put into priority queue
			for (Text val : values) {
				String[] v = val.toString().split(":");
				
				// get the last word of this phrase
				String[] words = v[0].split(" ");
				String word = words[words.length-1];
				int counter = Integer.parseInt(v[1]);
				valueQueue.add(new Value(word, counter));

			}

			// read from priority queue and make value
			Put put = new Put(Bytes.toBytes(key.toString()));
			int count = 0;
			while (valueQueue.peek() != null && count < n) {
				Value v = valueQueue.poll();
				put.add(Bytes.toBytes("data"), Bytes.toBytes(v.word), Bytes.toBytes(String.format("%d", v.count)));
				count += 1;
			}
			
			context.write(null, put);

			// build result and write into hbase

			//context.write(key, result);

		}
	}

	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		GenericOptionsParser optionParser = new GenericOptionsParser(conf, args);
		String[] remainingArgs = optionParser.getRemainingArgs();
		
		
		Job job = Job.getInstance(conf, "word count");
		job.setJarByClass(LanguageModel.class);
		job.setMapperClass(TokenizerMapper.class);
		//job.setCombinerClass(StringCastReducer.class);
		job.setReducerClass(StringCastReducer.class);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(Text.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		
		// set table
		TableMapReduceUtil.initTableReducerJob("language", StringCastReducer.class, job);

		List<String> otherArgs = new ArrayList<String>();
		for (int i = 0; i < remainingArgs.length; ++i) {
			otherArgs.add(remainingArgs[i]);
		}
		FileInputFormat.addInputPath(job, new Path(otherArgs.get(0)));
		System.out.println("input: " + otherArgs.get(0));
		FileOutputFormat.setOutputPath(job, new Path(otherArgs.get(1)));
		System.out.println("output: " + otherArgs.get(1));

		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}
}