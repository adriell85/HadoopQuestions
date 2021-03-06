package br.ufc.great.hadoop.tweets.eleicoes;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import br.ufc.great.hadoop.commons.model.Tweet;
import br.ufc.great.hadoop.commons.utils.MyEnv;
import br.ufc.great.hadoop.commons.utils.MyFileUtils;
import br.ufc.great.hadoop.commons.utils.ReadTSV;

public class HashTagCountByDay {
	
	public static class MapperClass extends Mapper<Object, Text, Text, Text> {
		public void map(Object key, Text data, Context context) throws IOException, InterruptedException {
			Tweet tweet = ReadTSV.parse(data.toString());
			ArrayList<String> tags = tweet.getHashTags();
			for (String tag : tags) {
				context.write(new Text(tag), new Text(tweet.getFormattedDate()));
			}
		}
	}
	
	public static class ReducerClass extends Reducer<Text, Text, Text, NullWritable> {
		public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
			HashMap<String, Integer> map = new HashMap<>();
			for (Text val : values) {
				String mkey = val.toString();
				if(map.containsKey(mkey)) {
					map.put(mkey, map.get(mkey) + 1);
				}else {
					map.put(mkey, 1);
				}
			}
			for(String mkey : map.keySet()) {
				context.write(new Text(key + "\t" + mkey + "\t" + map.get(mkey)), null);
			}
		}
	}

	public static void main(String[] args) throws Exception {
		String input_dir = null, output_dir = null;
		if(MyEnv.isDevelopment) {
			// Only in development environment...
			input_dir = "D:/dev/github/pedroalmir/cloud_study/hadoop/tests/tweets";
			output_dir = "D:/dev/github/pedroalmir/cloud_study/hadoop/tests/output";
			
			File output_dirFile = new File(output_dir);
			if(output_dirFile.exists()) {
				MyFileUtils.delete(output_dirFile);
			}
		}else {
			input_dir = args[0];
			output_dir = args[1];
		}
		
		Configuration conf = new Configuration();
		Job job = new Job(conf, "q2b-hashtag-count-by-day");
		job.setJarByClass(HashTagCountByDay.class);
		job.setMapperClass(MapperClass.class);
		job.setReducerClass(ReducerClass.class);
		
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(Text.class);
		
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(NullWritable.class);
		
		FileInputFormat.addInputPath(job, new Path(input_dir));
		FileOutputFormat.setOutputPath(job, new Path(output_dir));
		
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}
}