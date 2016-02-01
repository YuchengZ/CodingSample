import java.io.IOException;
import java.util.ArrayList;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class WordCount2 {

	/**
	 * mapper
	 * @author ZhangYC
	 *
	 */
	public static class TokenizerMapper extends
			Mapper<Object, Text, Text, IntWritable> {
		private final static IntWritable one = new IntWritable(1);
		private Text word = new Text();

		@Override
		public void map(Object key, Text value, Context context)
				throws IOException, InterruptedException {
			String line = value.toString().trim();
			// if the line is empty string, return
			if (line.length() == 0) {
				return;
			}
			// replace non-english words and split to words array
			String[] wordsArray = line.replaceAll("[^a-zA-Z]", " ").trim()
					.split("\\s+");
			// remove empty string， then write
			ArrayList<String> words = new ArrayList<String>();
			for (String word : wordsArray) {
				if (word.length() != 0) {
					context.write(new Text(word.toLowerCase()), one);
				}
			}
		}
	}

	/**
	 * reducer
	 * @author ZhangYC
	 *
	 */
	public static class IntSumReducer extends
			Reducer<Text, IntWritable, Text, IntWritable> {
		private IntWritable result = new IntWritable();

		public void reduce(Text key, Iterable<IntWritable> values,
				Context context) throws IOException, InterruptedException {
			int sum = 0;
			for (IntWritable val : values) {
				sum += val.get();
			}
			// remove sum smaller or equal to 2, since we don't need it later
			if (sum > 2) {
				result.set(sum);
				context.write(key, result);
			}
		}
	}

	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();

		// it should contains two arguments
		if (args.length != 2) {
			System.err.println("Usage: wordcount <in> <out>");
			System.exit(2);
		}
		Job job = Job.getInstance(conf, "word count");
		job.setJarByClass(WordCount2.class);
		job.setMapperClass(TokenizerMapper.class);
		job.setReducerClass(IntSumReducer.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);

		// set file path
		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));

		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}
}